using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace StockAPI.Models;

/// <summary>
/// Entidade que representa um produto no armazém
/// </summary>
[Table("produtos")]
public class Produto
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Required]
    [MaxLength(50)]
    [Column("codigo_barras")]
    public string CodigoBarras { get; set; } = string.Empty;

    [Required]
    [MaxLength(200)]
    [Column("nome")]
    public string Nome { get; set; } = string.Empty;

    [MaxLength(1000)]
    [Column("descricao")]
    public string? Descricao { get; set; }

    [MaxLength(500)]
    [Column("imagem_url")]
    public string? ImagemUrl { get; set; }

    [Column("stock")]
    public int Stock { get; set; } = 0;

    [Column("stock_minimo")]
    public int StockMinimo { get; set; } = 0;

    [Column("preco_unitario")]
    public decimal PrecoUnitario { get; set; } = 0;

    [MaxLength(100)]
    [Column("categoria")]
    public string? Categoria { get; set; }

    [MaxLength(100)]
    [Column("localizacao")]
    public string? Localizacao { get; set; }

    [Column("ativo")]
    public bool Ativo { get; set; } = true;

    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    [Column("updated_at")]
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    // Navegação
    public virtual ICollection<HistoricoStock> Historico { get; set; } = new List<HistoricoStock>();
}
