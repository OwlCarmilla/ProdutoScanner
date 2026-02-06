using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace StockAPI.Models;

/// <summary>
/// Entidade que representa um utilizador do sistema
/// </summary>
[Table("users")]
public class User
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Required]
    [MaxLength(255)]
    [Column("email")]
    public string Email { get; set; } = string.Empty;

    [Required]
    [MaxLength(255)]
    [Column("password_hash")]
    public string PasswordHash { get; set; } = string.Empty;

    [Required]
    [MaxLength(100)]
    [Column("nome")]
    public string Nome { get; set; } = string.Empty;

    [Column("is_verified")]
    public bool IsVerified { get; set; } = false;

    [MaxLength(6)]
    [Column("verification_code")]
    public string? VerificationCode { get; set; }

    [Column("verification_code_expiry")]
    public DateTime? VerificationCodeExpiry { get; set; }

    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    [Column("updated_at")]
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    // Navegação
    public virtual ICollection<HistoricoStock> HistoricoMovimentos { get; set; } = new List<HistoricoStock>();
}
