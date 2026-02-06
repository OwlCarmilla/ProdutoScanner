using Microsoft.EntityFrameworkCore;
using StockAPI.Data;
using StockAPI.DTOs;
using StockAPI.Models;

namespace StockAPI.Services;

/// <summary>
/// Interface do serviço de produtos
/// </summary>
public interface IProdutoService
{
    Task<PaginatedResponse<ProdutoDto>> GetAllAsync(int page = 1, int pageSize = 20, string? search = null, string? categoria = null);
    Task<ProdutoDto?> GetByIdAsync(int id);
    Task<ProdutoDto?> GetByCodigoBarrasAsync(string codigoBarras);
    Task<ApiResponse<ProdutoDto>> CreateAsync(ProdutoCreateDto dto);
    Task<ApiResponse<ProdutoDto>> UpdateAsync(int id, ProdutoUpdateDto dto);
    Task<ApiResponse<bool>> DeleteAsync(int id);
    Task<List<string>> GetCategoriasAsync();
}

/// <summary>
/// Interface do serviço de stock
/// </summary>
public interface IStockService
{
    Task<ApiResponse<ProdutoDto>> EntradaStockAsync(MovimentoStockDto dto, int userId);
    Task<ApiResponse<ProdutoDto>> SaidaStockAsync(MovimentoStockDto dto, int userId);
    Task<PaginatedResponse<HistoricoDto>> GetHistoricoAsync(int produtoId, int page = 1, int pageSize = 10);
    Task<PaginatedResponse<HistoricoDto>> GetHistoricoGeralAsync(int page = 1, int pageSize = 20);
}

/// <summary>
/// Serviço de gestão de produtos
/// </summary>
public class ProdutoService : IProdutoService
{
    private readonly AppDbContext _context;
    private readonly ILogger<ProdutoService> _logger;

    public ProdutoService(AppDbContext context, ILogger<ProdutoService> logger)
    {
        _context = context;
        _logger = logger;
    }

    /// <summary>
    /// Listar todos os produtos com paginação e filtros
    /// </summary>
    public async Task<PaginatedResponse<ProdutoDto>> GetAllAsync(
        int page = 1, 
        int pageSize = 20, 
        string? search = null, 
        string? categoria = null)
    {
        var query = _context.Produtos.AsQueryable();

        // Filtros
        if (!string.IsNullOrWhiteSpace(search))
        {
            search = search.ToLower();
            query = query.Where(p => 
                p.Nome.ToLower().Contains(search) || 
                p.CodigoBarras.Contains(search) ||
                (p.Descricao != null && p.Descricao.ToLower().Contains(search)));
        }

        if (!string.IsNullOrWhiteSpace(categoria))
        {
            query = query.Where(p => p.Categoria == categoria);
        }

        // Apenas produtos ativos
        query = query.Where(p => p.Ativo);

        var totalItems = await query.CountAsync();

        var items = await query
            .OrderBy(p => p.Nome)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .Select(p => MapToDto(p))
            .ToListAsync();

        return new PaginatedResponse<ProdutoDto>
        {
            Items = items,
            TotalItems = totalItems,
            Page = page,
            PageSize = pageSize
        };
    }

    /// <summary>
    /// Obter produto por ID
    /// </summary>
    public async Task<ProdutoDto?> GetByIdAsync(int id)
    {
        var produto = await _context.Produtos.FindAsync(id);
        return produto != null ? MapToDto(produto) : null;
    }

    /// <summary>
    /// Obter produto por código de barras
    /// </summary>
    public async Task<ProdutoDto?> GetByCodigoBarrasAsync(string codigoBarras)
    {
        var produto = await _context.Produtos
            .FirstOrDefaultAsync(p => p.CodigoBarras == codigoBarras);
        return produto != null ? MapToDto(produto) : null;
    }

    /// <summary>
    /// Criar novo produto
    /// </summary>
    public async Task<ApiResponse<ProdutoDto>> CreateAsync(ProdutoCreateDto dto)
    {
        try
        {
            // Verificar se código de barras já existe
            var existing = await _context.Produtos
                .FirstOrDefaultAsync(p => p.CodigoBarras == dto.CodigoBarras);

            if (existing != null)
            {
                return ApiResponse<ProdutoDto>.Fail("Já existe um produto com este código de barras");
            }

            var produto = new Produto
            {
                CodigoBarras = dto.CodigoBarras.Trim(),
                Nome = dto.Nome.Trim(),
                Descricao = dto.Descricao?.Trim(),
                ImagemUrl = dto.ImagemUrl?.Trim(),
                Stock = dto.Stock,
                StockMinimo = dto.StockMinimo,
                PrecoUnitario = dto.PrecoUnitario,
                Categoria = dto.Categoria?.Trim(),
                Localizacao = dto.Localizacao?.Trim(),
                Ativo = true
            };

            _context.Produtos.Add(produto);
            await _context.SaveChangesAsync();

            _logger.LogInformation("Produto criado: {Id} - {Nome}", produto.Id, produto.Nome);

            return ApiResponse<ProdutoDto>.Ok(MapToDto(produto), "Produto criado com sucesso");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erro ao criar produto");
            return ApiResponse<ProdutoDto>.Fail("Erro interno ao criar produto");
        }
    }

    /// <summary>
    /// Atualizar produto
    /// </summary>
    public async Task<ApiResponse<ProdutoDto>> UpdateAsync(int id, ProdutoUpdateDto dto)
    {
        try
        {
            var produto = await _context.Produtos.FindAsync(id);

            if (produto == null)
            {
                return ApiResponse<ProdutoDto>.Fail("Produto não encontrado");
            }

            // Atualizar apenas campos fornecidos
            if (dto.Nome != null) produto.Nome = dto.Nome.Trim();
            if (dto.Descricao != null) produto.Descricao = dto.Descricao.Trim();
            if (dto.ImagemUrl != null) produto.ImagemUrl = dto.ImagemUrl.Trim();
            if (dto.StockMinimo.HasValue) produto.StockMinimo = dto.StockMinimo.Value;
            if (dto.PrecoUnitario.HasValue) produto.PrecoUnitario = dto.PrecoUnitario.Value;
            if (dto.Categoria != null) produto.Categoria = dto.Categoria.Trim();
            if (dto.Localizacao != null) produto.Localizacao = dto.Localizacao.Trim();
            if (dto.Ativo.HasValue) produto.Ativo = dto.Ativo.Value;

            produto.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            _logger.LogInformation("Produto atualizado: {Id}", produto.Id);

            return ApiResponse<ProdutoDto>.Ok(MapToDto(produto), "Produto atualizado com sucesso");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erro ao atualizar produto {Id}", id);
            return ApiResponse<ProdutoDto>.Fail("Erro interno ao atualizar produto");
        }
    }

    /// <summary>
    /// Eliminar produto (soft delete)
    /// </summary>
    public async Task<ApiResponse<bool>> DeleteAsync(int id)
    {
        try
        {
            var produto = await _context.Produtos.FindAsync(id);

            if (produto == null)
            {
                return ApiResponse<bool>.Fail("Produto não encontrado");
            }

            // Soft delete
            produto.Ativo = false;
            produto.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            _logger.LogInformation("Produto desativado: {Id}", id);

            return ApiResponse<bool>.Ok(true, "Produto eliminado com sucesso");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erro ao eliminar produto {Id}", id);
            return ApiResponse<bool>.Fail("Erro interno ao eliminar produto");
        }
    }

    /// <summary>
    /// Listar categorias disponíveis
    /// </summary>
    public async Task<List<string>> GetCategoriasAsync()
    {
        return await _context.Produtos
            .Where(p => p.Ativo && p.Categoria != null)
            .Select(p => p.Categoria!)
            .Distinct()
            .OrderBy(c => c)
            .ToListAsync();
    }

    private static ProdutoDto MapToDto(Produto p)
    {
        return new ProdutoDto
        {
            Id = p.Id,
            CodigoBarras = p.CodigoBarras,
            Nome = p.Nome,
            Descricao = p.Descricao,
            ImagemUrl = p.ImagemUrl,
            Stock = p.Stock,
            StockMinimo = p.StockMinimo,
            PrecoUnitario = p.PrecoUnitario,
            Categoria = p.Categoria,
            Localizacao = p.Localizacao,
            Ativo = p.Ativo,
            CreatedAt = p.CreatedAt,
            UpdatedAt = p.UpdatedAt
        };
    }
}

/// <summary>
/// Serviço de gestão de stock
/// </summary>
public class StockService : IStockService
{
    private readonly AppDbContext _context;
    private readonly ILogger<StockService> _logger;

    public StockService(AppDbContext context, ILogger<StockService> logger)
    {
        _context = context;
        _logger = logger;
    }

    /// <summary>
    /// Registar entrada de stock
    /// </summary>
    public async Task<ApiResponse<ProdutoDto>> EntradaStockAsync(MovimentoStockDto dto, int userId)
    {
        return await ProcessarMovimentoAsync(dto, userId, TipoMovimento.Entrada);
    }

    /// <summary>
    /// Registar saída de stock
    /// </summary>
    public async Task<ApiResponse<ProdutoDto>> SaidaStockAsync(MovimentoStockDto dto, int userId)
    {
        return await ProcessarMovimentoAsync(dto, userId, TipoMovimento.Saida);
    }

    /// <summary>
    /// Obter histórico de um produto
    /// </summary>
    public async Task<PaginatedResponse<HistoricoDto>> GetHistoricoAsync(int produtoId, int page = 1, int pageSize = 10)
    {
        var query = _context.HistoricoStock
            .Include(h => h.Produto)
            .Include(h => h.User)
            .Where(h => h.ProdutoId == produtoId);

        var totalItems = await query.CountAsync();

        var items = await query
            .OrderByDescending(h => h.DataMovimento)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .Select(h => MapToHistoricoDto(h))
            .ToListAsync();

        return new PaginatedResponse<HistoricoDto>
        {
            Items = items,
            TotalItems = totalItems,
            Page = page,
            PageSize = pageSize
        };
    }

    /// <summary>
    /// Obter histórico geral
    /// </summary>
    public async Task<PaginatedResponse<HistoricoDto>> GetHistoricoGeralAsync(int page = 1, int pageSize = 20)
    {
        var query = _context.HistoricoStock
            .Include(h => h.Produto)
            .Include(h => h.User);

        var totalItems = await query.CountAsync();

        var items = await query
            .OrderByDescending(h => h.DataMovimento)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .Select(h => MapToHistoricoDto(h))
            .ToListAsync();

        return new PaginatedResponse<HistoricoDto>
        {
            Items = items,
            TotalItems = totalItems,
            Page = page,
            PageSize = pageSize
        };
    }

    // ========================================
    // Métodos Privados
    // ========================================

    private async Task<ApiResponse<ProdutoDto>> ProcessarMovimentoAsync(
        MovimentoStockDto dto, 
        int userId, 
        TipoMovimento tipo)
    {
        using var transaction = await _context.Database.BeginTransactionAsync();

        try
        {
            var produto = await _context.Produtos
                .FirstOrDefaultAsync(p => p.CodigoBarras == dto.CodigoBarras);

            if (produto == null)
            {
                return ApiResponse<ProdutoDto>.Fail("Produto não encontrado");
            }

            if (!produto.Ativo)
            {
                return ApiResponse<ProdutoDto>.Fail("Produto desativado");
            }

            var stockAnterior = produto.Stock;
            int novoStock;

            if (tipo == TipoMovimento.Entrada)
            {
                novoStock = stockAnterior + dto.Quantidade;
            }
            else
            {
                if (stockAnterior < dto.Quantidade)
                {
                    return ApiResponse<ProdutoDto>.Fail(
                        $"Stock insuficiente. Stock atual: {stockAnterior}, Quantidade pedida: {dto.Quantidade}");
                }
                novoStock = stockAnterior - dto.Quantidade;
            }

            // Atualizar stock
            produto.Stock = novoStock;
            produto.UpdatedAt = DateTime.UtcNow;

            // Registar no histórico
            var historico = new HistoricoStock
            {
                ProdutoId = produto.Id,
                UserId = userId,
                Quantidade = dto.Quantidade,
                TipoMovimento = tipo,
                StockAnterior = stockAnterior,
                StockAtual = novoStock,
                Observacoes = dto.Observacoes
            };

            _context.HistoricoStock.Add(historico);
            await _context.SaveChangesAsync();
            await transaction.CommitAsync();

            var tipoStr = tipo == TipoMovimento.Entrada ? "Entrada" : "Saída";
            _logger.LogInformation(
                "{Tipo} de stock: Produto {Id}, Quantidade {Qtd}, Stock {Anterior} -> {Atual}",
                tipoStr, produto.Id, dto.Quantidade, stockAnterior, novoStock);

            return ApiResponse<ProdutoDto>.Ok(
                new ProdutoDto
                {
                    Id = produto.Id,
                    CodigoBarras = produto.CodigoBarras,
                    Nome = produto.Nome,
                    Descricao = produto.Descricao,
                    ImagemUrl = produto.ImagemUrl,
                    Stock = produto.Stock,
                    StockMinimo = produto.StockMinimo,
                    PrecoUnitario = produto.PrecoUnitario,
                    Categoria = produto.Categoria,
                    Localizacao = produto.Localizacao,
                    Ativo = produto.Ativo,
                    CreatedAt = produto.CreatedAt,
                    UpdatedAt = produto.UpdatedAt
                },
                $"{tipoStr} registada com sucesso. Novo stock: {novoStock}");
        }
        catch (Exception ex)
        {
            await transaction.RollbackAsync();
            _logger.LogError(ex, "Erro ao processar movimento de stock");
            return ApiResponse<ProdutoDto>.Fail("Erro interno ao processar movimento");
        }
    }

    private static HistoricoDto MapToHistoricoDto(HistoricoStock h)
    {
        return new HistoricoDto
        {
            Id = h.Id,
            ProdutoId = h.ProdutoId,
            ProdutoNome = h.Produto?.Nome ?? "",
            CodigoBarras = h.Produto?.CodigoBarras ?? "",
            UserId = h.UserId,
            UserNome = h.User?.Nome ?? "",
            Quantidade = h.Quantidade,
            TipoMovimento = h.TipoMovimento == Models.TipoMovimento.Entrada ? "Entrada" : "Saída",
            StockAnterior = h.StockAnterior,
            StockAtual = h.StockAtual,
            Observacoes = h.Observacoes,
            DataMovimento = h.DataMovimento
        };
    }
}
