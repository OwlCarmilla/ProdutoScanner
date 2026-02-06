using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using StockAPI.DTOs;
using StockAPI.Services;

namespace StockAPI.Controllers;

/// <summary>
/// Controller de produtos
/// </summary>
[ApiController]
[Route("api/[controller]")]
public class ProdutosController : ControllerBase
{
    private readonly IProdutoService _produtoService;

    public ProdutosController(IProdutoService produtoService)
    {
        _produtoService = produtoService;
    }

    /// <summary>
    /// Listar todos os produtos (com paginação e filtros)
    /// </summary>
    /// <param name="page">Número da página (default: 1)</param>
    /// <param name="pageSize">Itens por página (default: 20)</param>
    /// <param name="search">Termo de pesquisa</param>
    /// <param name="categoria">Filtrar por categoria</param>
    /// <returns>Lista paginada de produtos</returns>
    [HttpGet]
    [ProducesResponseType(typeof(PaginatedResponse<ProdutoDto>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetAll(
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 20,
        [FromQuery] string? search = null,
        [FromQuery] string? categoria = null)
    {
        // Validar parâmetros
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 20;
        if (pageSize > 100) pageSize = 100;

        var result = await _produtoService.GetAllAsync(page, pageSize, search, categoria);
        return Ok(result);
    }

    /// <summary>
    /// Obter produto por ID
    /// </summary>
    /// <param name="id">ID do produto</param>
    /// <returns>Dados do produto</returns>
    [HttpGet("{id:int}")]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status404NotFound)]
    public async Task<IActionResult> GetById(int id)
    {
        var produto = await _produtoService.GetByIdAsync(id);

        if (produto == null)
        {
            return NotFound(ApiResponse<ProdutoDto>.Fail("Produto não encontrado"));
        }

        return Ok(ApiResponse<ProdutoDto>.Ok(produto));
    }

    /// <summary>
    /// Obter produto por código de barras
    /// </summary>
    /// <param name="codigo">Código de barras</param>
    /// <returns>Dados do produto</returns>
    [HttpGet("barcode/{codigo}")]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status404NotFound)]
    public async Task<IActionResult> GetByBarcode(string codigo)
    {
        var produto = await _produtoService.GetByCodigoBarrasAsync(codigo);

        if (produto == null)
        {
            return NotFound(ApiResponse<ProdutoDto>.Fail("Produto não encontrado"));
        }

        return Ok(ApiResponse<ProdutoDto>.Ok(produto));
    }

    /// <summary>
    /// Criar novo produto (requer autenticação)
    /// </summary>
    /// <param name="dto">Dados do produto</param>
    /// <returns>Produto criado</returns>
    [HttpPost]
    [Authorize]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status201Created)]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> Create([FromBody] ProdutoCreateDto dto)
    {
        if (!ModelState.IsValid)
        {
            var errors = ModelState.Values
                .SelectMany(v => v.Errors)
                .Select(e => e.ErrorMessage)
                .ToList();

            return BadRequest(ApiResponse<ProdutoDto>.Fail("Dados inválidos", errors));
        }

        var result = await _produtoService.CreateAsync(dto);

        if (!result.Success)
        {
            return BadRequest(result);
        }

        return CreatedAtAction(
            nameof(GetById), 
            new { id = result.Data!.Id }, 
            result);
    }

    /// <summary>
    /// Atualizar produto (requer autenticação)
    /// </summary>
    /// <param name="id">ID do produto</param>
    /// <param name="dto">Dados a atualizar</param>
    /// <returns>Produto atualizado</returns>
    [HttpPut("{id:int}")]
    [Authorize]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status400BadRequest)]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> Update(int id, [FromBody] ProdutoUpdateDto dto)
    {
        if (!ModelState.IsValid)
        {
            var errors = ModelState.Values
                .SelectMany(v => v.Errors)
                .Select(e => e.ErrorMessage)
                .ToList();

            return BadRequest(ApiResponse<ProdutoDto>.Fail("Dados inválidos", errors));
        }

        var result = await _produtoService.UpdateAsync(id, dto);

        if (!result.Success)
        {
            if (result.Message.Contains("não encontrado"))
            {
                return NotFound(result);
            }
            return BadRequest(result);
        }

        return Ok(result);
    }

    /// <summary>
    /// Eliminar produto (requer autenticação)
    /// </summary>
    /// <param name="id">ID do produto</param>
    /// <returns>Confirmação</returns>
    [HttpDelete("{id:int}")]
    [Authorize]
    [ProducesResponseType(typeof(ApiResponse<bool>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ApiResponse<bool>), StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> Delete(int id)
    {
        var result = await _produtoService.DeleteAsync(id);

        if (!result.Success)
        {
            return NotFound(result);
        }

        return Ok(result);
    }

    /// <summary>
    /// Listar categorias disponíveis
    /// </summary>
    /// <returns>Lista de categorias</returns>
    [HttpGet("categorias")]
    [ProducesResponseType(typeof(ApiResponse<List<string>>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetCategorias()
    {
        var categorias = await _produtoService.GetCategoriasAsync();
        return Ok(ApiResponse<List<string>>.Ok(categorias));
    }
}
