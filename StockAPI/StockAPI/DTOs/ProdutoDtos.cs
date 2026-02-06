using System.ComponentModel.DataAnnotations;
using StockAPI.Models;

namespace StockAPI.DTOs;

// ========================================
// DTOs de Produtos
// ========================================

/// <summary>
/// DTO para criar/atualizar produto
/// </summary>
public class ProdutoCreateDto
{
    [Required(ErrorMessage = "O código de barras é obrigatório")]
    [MaxLength(50)]
    public string CodigoBarras { get; set; } = string.Empty;

    [Required(ErrorMessage = "O nome é obrigatório")]
    [MaxLength(200)]
    public string Nome { get; set; } = string.Empty;

    [MaxLength(1000)]
    public string? Descricao { get; set; }

    [MaxLength(500)]
    public string? ImagemUrl { get; set; }

    [Range(0, int.MaxValue, ErrorMessage = "O stock não pode ser negativo")]
    public int Stock { get; set; } = 0;

    [Range(0, int.MaxValue)]
    public int StockMinimo { get; set; } = 0;

    [Range(0, double.MaxValue)]
    public decimal PrecoUnitario { get; set; } = 0;

    [MaxLength(100)]
    public string? Categoria { get; set; }

    [MaxLength(100)]
    public string? Localizacao { get; set; }
}

/// <summary>
/// DTO para atualizar produto
/// </summary>
public class ProdutoUpdateDto
{
    [MaxLength(200)]
    public string? Nome { get; set; }

    [MaxLength(1000)]
    public string? Descricao { get; set; }

    [MaxLength(500)]
    public string? ImagemUrl { get; set; }

    [Range(0, int.MaxValue)]
    public int? StockMinimo { get; set; }

    [Range(0, double.MaxValue)]
    public decimal? PrecoUnitario { get; set; }

    [MaxLength(100)]
    public string? Categoria { get; set; }

    [MaxLength(100)]
    public string? Localizacao { get; set; }

    public bool? Ativo { get; set; }
}

/// <summary>
/// DTO de resposta de produto
/// </summary>
public class ProdutoDto
{
    public int Id { get; set; }
    public string CodigoBarras { get; set; } = string.Empty;
    public string Nome { get; set; } = string.Empty;
    public string? Descricao { get; set; }
    public string? ImagemUrl { get; set; }
    public int Stock { get; set; }
    public int StockMinimo { get; set; }
    public decimal PrecoUnitario { get; set; }
    public string? Categoria { get; set; }
    public string? Localizacao { get; set; }
    public bool Ativo { get; set; }
    public bool StockBaixo => Stock <= StockMinimo;
    public DateTime CreatedAt { get; set; }
    public DateTime UpdatedAt { get; set; }
}

// ========================================
// DTOs de Movimentos de Stock
// ========================================

/// <summary>
/// DTO para movimento de stock (entrada ou saída)
/// </summary>
public class MovimentoStockDto
{
    [Required(ErrorMessage = "O código de barras é obrigatório")]
    public string CodigoBarras { get; set; } = string.Empty;

    [Required(ErrorMessage = "A quantidade é obrigatória")]
    [Range(1, int.MaxValue, ErrorMessage = "A quantidade deve ser maior que 0")]
    public int Quantidade { get; set; }

    [MaxLength(500)]
    public string? Observacoes { get; set; }
}

/// <summary>
/// DTO de resposta de histórico
/// </summary>
public class HistoricoDto
{
    public int Id { get; set; }
    public int ProdutoId { get; set; }
    public string ProdutoNome { get; set; } = string.Empty;
    public string CodigoBarras { get; set; } = string.Empty;
    public int UserId { get; set; }
    public string UserNome { get; set; } = string.Empty;
    public int Quantidade { get; set; }
    public string TipoMovimento { get; set; } = string.Empty;
    public int StockAnterior { get; set; }
    public int StockAtual { get; set; }
    public string? Observacoes { get; set; }
    public DateTime DataMovimento { get; set; }
}

// ========================================
// DTOs Genéricos
// ========================================

/// <summary>
/// Resposta genérica da API
/// </summary>
public class ApiResponse<T>
{
    public bool Success { get; set; }
    public string Message { get; set; } = string.Empty;
    public T? Data { get; set; }
    public List<string>? Errors { get; set; }

    public static ApiResponse<T> Ok(T data, string message = "Operação realizada com sucesso")
    {
        return new ApiResponse<T> { Success = true, Message = message, Data = data };
    }

    public static ApiResponse<T> Fail(string message, List<string>? errors = null)
    {
        return new ApiResponse<T> { Success = false, Message = message, Errors = errors };
    }
}

/// <summary>
/// Resposta paginada
/// </summary>
public class PaginatedResponse<T>
{
    public List<T> Items { get; set; } = new();
    public int TotalItems { get; set; }
    public int Page { get; set; }
    public int PageSize { get; set; }
    public int TotalPages => (int)Math.Ceiling((double)TotalItems / PageSize);
    public bool HasNext => Page < TotalPages;
    public bool HasPrevious => Page > 1;
}
