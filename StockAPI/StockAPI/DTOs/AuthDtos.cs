using System.ComponentModel.DataAnnotations;

namespace StockAPI.DTOs;

// ========================================
// DTOs de Autenticação
// ========================================

/// <summary>
/// DTO para registo de novo utilizador
/// </summary>
public class RegisterDto
{
    [Required(ErrorMessage = "O email é obrigatório")]
    [EmailAddress(ErrorMessage = "Email inválido")]
    [MaxLength(255)]
    public string Email { get; set; } = string.Empty;

    [Required(ErrorMessage = "A password é obrigatória")]
    [MinLength(6, ErrorMessage = "A password deve ter pelo menos 6 caracteres")]
    [MaxLength(100)]
    public string Password { get; set; } = string.Empty;

    [Required(ErrorMessage = "O nome é obrigatório")]
    [MaxLength(100)]
    public string Nome { get; set; } = string.Empty;
}

/// <summary>
/// DTO para login
/// </summary>
public class LoginDto
{
    [Required(ErrorMessage = "O email é obrigatório")]
    [EmailAddress(ErrorMessage = "Email inválido")]
    public string Email { get; set; } = string.Empty;

    [Required(ErrorMessage = "A password é obrigatória")]
    public string Password { get; set; } = string.Empty;
}

/// <summary>
/// DTO para verificação de código
/// </summary>
public class VerifyCodeDto
{
    [Required(ErrorMessage = "O email é obrigatório")]
    [EmailAddress]
    public string Email { get; set; } = string.Empty;

    [Required(ErrorMessage = "O código é obrigatório")]
    [StringLength(6, MinimumLength = 6, ErrorMessage = "O código deve ter 6 dígitos")]
    public string Code { get; set; } = string.Empty;
}

/// <summary>
/// DTO para reenvio de código de verificação
/// </summary>
public class ResendCodeDto
{
    [Required(ErrorMessage = "O email é obrigatório")]
    [EmailAddress]
    public string Email { get; set; } = string.Empty;
}

/// <summary>
/// Resposta de autenticação com token
/// </summary>
public class AuthResponseDto
{
    public bool Success { get; set; }
    public string Message { get; set; } = string.Empty;
    public string? Token { get; set; }
    public UserDto? User { get; set; }
}

/// <summary>
/// DTO de utilizador (sem dados sensíveis)
/// </summary>
public class UserDto
{
    public int Id { get; set; }
    public string Email { get; set; } = string.Empty;
    public string Nome { get; set; } = string.Empty;
    public bool IsVerified { get; set; }
    public DateTime CreatedAt { get; set; }
}
