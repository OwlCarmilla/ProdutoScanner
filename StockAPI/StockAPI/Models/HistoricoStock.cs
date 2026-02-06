using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace StockAPI.Models;

/// <summary>
/// Tipos de movimento de stock
/// </summary>
public enum TipoMovimento
{
    Entrada = 1,
    Saida = 2
}

/// <summary>
/// Entidade que regista o histórico de movimentos de stock
/// </summary>
[Table("historico_stock")]
public class HistoricoStock
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Required]
    [Column("produto_id")]
    public int ProdutoId { get; set; }

    [Required]
    [Column("user_id")]
    public int UserId { get; set; }

    [Required]
    [Column("quantidade")]
    public int Quantidade { get; set; }

    [Required]
    [Column("tipo_movimento")]
    public TipoMovimento TipoMovimento { get; set; }

    [Column("stock_anterior")]
    public int StockAnterior { get; set; }

    [Column("stock_atual")]
    public int StockAtual { get; set; }

    [MaxLength(500)]
    [Column("observacoes")]
    public string? Observacoes { get; set; }

    [Column("data_movimento")]
    public DateTime DataMovimento { get; set; } = DateTime.UtcNow;

    // Navegação
    [ForeignKey("ProdutoId")]
    public virtual Produto? Produto { get; set; }

    [ForeignKey("UserId")]
    public virtual User? User { get; set; }
}
