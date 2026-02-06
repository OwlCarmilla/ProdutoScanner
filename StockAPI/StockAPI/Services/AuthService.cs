using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using StockAPI.Data;
using StockAPI.DTOs;
using StockAPI.Models;

namespace StockAPI.Services;

/// <summary>
/// Interface do serviço de autenticação
/// </summary>
public interface IAuthService
{
    Task<AuthResponseDto> RegisterAsync(RegisterDto dto);
    Task<AuthResponseDto> LoginAsync(LoginDto dto);
    Task<AuthResponseDto> VerifyCodeAsync(VerifyCodeDto dto);
    Task<AuthResponseDto> ResendCodeAsync(ResendCodeDto dto);
    Task<UserDto?> GetUserByIdAsync(int userId);
}

/// <summary>
/// Serviço de autenticação com JWT
/// </summary>
public class AuthService : IAuthService
{
    private readonly AppDbContext _context;
    private readonly IConfiguration _configuration;
    private readonly ILogger<AuthService> _logger;

    public AuthService(AppDbContext context, IConfiguration configuration, ILogger<AuthService> logger)
    {
        _context = context;
        _configuration = configuration;
        _logger = logger;
    }

    /// <summary>
    /// Regista um novo utilizador
    /// </summary>
    public async Task<AuthResponseDto> RegisterAsync(RegisterDto dto)
    {
        try
        {
            // Verificar se email já existe
            var existingUser = await _context.Users
                .FirstOrDefaultAsync(u => u.Email.ToLower() == dto.Email.ToLower());

            if (existingUser != null)
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Este email já está registado"
                };
            }

            // Gerar código de verificação
            var verificationCode = GenerateVerificationCode();

            // Criar utilizador
            var user = new User
            {
                Email = dto.Email.ToLower().Trim(),
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.Password),
                Nome = dto.Nome.Trim(),
                IsVerified = false,
                VerificationCode = verificationCode,
                VerificationCodeExpiry = DateTime.UtcNow.AddHours(24)
            };

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            _logger.LogInformation("Novo utilizador registado: {Email}, Código: {Code}", user.Email, verificationCode);

            // Nota: Aqui enviarias o email com o código
            // await _emailService.SendVerificationEmail(user.Email, verificationCode);

            return new AuthResponseDto
            {
                Success = true,
                Message = $"Registo efetuado com sucesso. Código de verificação: {verificationCode}",
                User = MapToUserDto(user)
            };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erro ao registar utilizador");
            return new AuthResponseDto
            {
                Success = false,
                Message = "Erro interno ao processar o registo"
            };
        }
    }

    /// <summary>
    /// Autenticar utilizador
    /// </summary>
    public async Task<AuthResponseDto> LoginAsync(LoginDto dto)
    {
        try
        {
            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.Email.ToLower() == dto.Email.ToLower());

            if (user == null)
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Email ou password incorretos"
                };
            }

            if (!BCrypt.Net.BCrypt.Verify(dto.Password, user.PasswordHash))
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Email ou password incorretos"
                };
            }

            if (!user.IsVerified)
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Conta não verificada. Por favor verifique o seu email."
                };
            }

            // Gerar token JWT
            var token = GenerateJwtToken(user);

            return new AuthResponseDto
            {
                Success = true,
                Message = "Login efetuado com sucesso",
                Token = token,
                User = MapToUserDto(user)
            };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erro ao fazer login");
            return new AuthResponseDto
            {
                Success = false,
                Message = "Erro interno ao processar o login"
            };
        }
    }

    /// <summary>
    /// Verificar código de ativação
    /// </summary>
    public async Task<AuthResponseDto> VerifyCodeAsync(VerifyCodeDto dto)
    {
        try
        {
            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.Email.ToLower() == dto.Email.ToLower());

            if (user == null)
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Utilizador não encontrado"
                };
            }

            if (user.IsVerified)
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Esta conta já está verificada"
                };
            }

            if (user.VerificationCode != dto.Code)
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Código de verificação inválido"
                };
            }

            if (user.VerificationCodeExpiry < DateTime.UtcNow)
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Código de verificação expirado. Solicite um novo código."
                };
            }

            // Ativar conta
            user.IsVerified = true;
            user.VerificationCode = null;
            user.VerificationCodeExpiry = null;
            user.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            // Gerar token
            var token = GenerateJwtToken(user);

            return new AuthResponseDto
            {
                Success = true,
                Message = "Conta verificada com sucesso",
                Token = token,
                User = MapToUserDto(user)
            };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erro ao verificar código");
            return new AuthResponseDto
            {
                Success = false,
                Message = "Erro interno ao verificar código"
            };
        }
    }

    /// <summary>
    /// Reenviar código de verificação
    /// </summary>
    public async Task<AuthResponseDto> ResendCodeAsync(ResendCodeDto dto)
    {
        try
        {
            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.Email.ToLower() == dto.Email.ToLower());

            if (user == null)
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Utilizador não encontrado"
                };
            }

            if (user.IsVerified)
            {
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Esta conta já está verificada"
                };
            }

            // Gerar novo código
            var newCode = GenerateVerificationCode();
            user.VerificationCode = newCode;
            user.VerificationCodeExpiry = DateTime.UtcNow.AddHours(24);
            user.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            _logger.LogInformation("Novo código enviado para: {Email}, Código: {Code}", user.Email, newCode);

            return new AuthResponseDto
            {
                Success = true,
                Message = $"Novo código enviado. Código: {newCode}"
            };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erro ao reenviar código");
            return new AuthResponseDto
            {
                Success = false,
                Message = "Erro interno ao reenviar código"
            };
        }
    }

    /// <summary>
    /// Obter utilizador por ID
    /// </summary>
    public async Task<UserDto?> GetUserByIdAsync(int userId)
    {
        var user = await _context.Users.FindAsync(userId);
        return user != null ? MapToUserDto(user) : null;
    }

    // ========================================
    // Métodos Privados
    // ========================================

    private string GenerateJwtToken(User user)
    {
        var key = new SymmetricSecurityKey(
            Encoding.UTF8.GetBytes(_configuration["Jwt:Secret"] ?? "ChaveSecretaMuitoSeguraParaJWT123!"));
        
        var credentials = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

        var claims = new[]
        {
            new Claim(ClaimTypes.NameIdentifier, user.Id.ToString()),
            new Claim(ClaimTypes.Email, user.Email),
            new Claim(ClaimTypes.Name, user.Nome),
            new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString())
        };

        var token = new JwtSecurityToken(
            issuer: _configuration["Jwt:Issuer"] ?? "StockAPI",
            audience: _configuration["Jwt:Audience"] ?? "StockApp",
            claims: claims,
            expires: DateTime.UtcNow.AddDays(7),
            signingCredentials: credentials
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    private static string GenerateVerificationCode()
    {
        var random = new Random();
        return random.Next(100000, 999999).ToString();
    }

    private static UserDto MapToUserDto(User user)
    {
        return new UserDto
        {
            Id = user.Id,
            Email = user.Email,
            Nome = user.Nome,
            IsVerified = user.IsVerified,
            CreatedAt = user.CreatedAt
        };
    }
}
