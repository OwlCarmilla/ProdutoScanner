using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using StockAPI.Data;
using StockAPI.Services;

var builder = WebApplication.CreateBuilder(args);

// ========================================
// Configuração de Serviços
// ========================================

// Controllers
builder.Services.AddControllers();

// Swagger/OpenAPI
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Stock API - Gestão de Armazém",
        Version = "v1",
        Description = "API REST para gestão de stocks de armazém - Projeto DAM 2025/26",
        Contact = new OpenApiContact
        {
            Name = "Eng. Informática - IPT",
            Email = "dam@ipt.pt"
        }
    });

    // Configurar autenticação no Swagger
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.ApiKey,
        Scheme = "Bearer",
        BearerFormat = "JWT",
        In = ParameterLocation.Header,
        Description = "Insira o token JWT no formato: Bearer {token}"
    });

    c.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            Array.Empty<string>()
        }
    });
});


// ========================================
// Base de Dados - MySQL com Entity Framework
// ========================================
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection") 
    ?? "Server=localhost;Database=stockdb;User=root;Password=root;";

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString)));

// ========================================
// Autenticação JWT
// ========================================
var jwtSecret = builder.Configuration["Jwt:Secret"] ?? "ChaveSecretaMuitoSeguraParaJWT123!ParaProjetoDAM2025";
var jwtIssuer = builder.Configuration["Jwt:Issuer"] ?? "StockAPI";
var jwtAudience = builder.Configuration["Jwt:Audience"] ?? "StockApp";

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        ValidIssuer = jwtIssuer,
        ValidAudience = jwtAudience,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtSecret)),
        ClockSkew = TimeSpan.Zero
    };
});

// ========================================
// Injeção de Dependências - Serviços
// ========================================
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IProdutoService, ProdutoService>();
builder.Services.AddScoped<IStockService, StockService>();

// ========================================
// CORS - Permitir acesso da app Android
// ========================================
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

var app = builder.Build();

// ========================================
// Pipeline HTTP
// ========================================

// Swagger (disponível em todos os ambientes para facilitar testes)
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "Stock API v1");
    c.RoutePrefix = string.Empty; // Swagger na raiz
});

// CORS
app.UseCors("AllowAll");

// Autenticação e Autorização
app.UseAuthentication();
app.UseAuthorization();

// Controllers
app.MapControllers();

// ========================================
// Iniciar Aplicação
// ========================================
app.Run();
