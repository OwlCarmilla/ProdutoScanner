using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using StockAPI.DTOs;
using StockAPI.Services;

namespace StockAPI.Controllers;

/// <summary>
/// Controller de movimentos de stock
/// </summary>
[ApiController]
[Route("api/[controller]")]
public class StockController : ControllerBase
{
    private readonly IStockService _stockService;

    public StockController(IStockService stockService)
    {
        _stockService = stockService;
    }

    /// <summary>
    /// Registar entrada de stock (requer autenticação)
    /// </summary>
    /// <param name="dto">Dados do movimento</param>
    /// <returns>Produto com stock atualizado</returns>
    [HttpPost("entrada")]
    [Authorize]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> EntradaStock([FromBody] MovimentoStockDto dto)
    {
        if (!ModelState.IsValid)
        {
            var errors = ModelState.Values
                .SelectMany(v => v.Errors)
                .Select(e => e.ErrorMessage)
                .ToList();

            return BadRequest(ApiResponse<ProdutoDto>.Fail("Dados inválidos", errors));
        }

        var userId = GetUserIdFromToken();
        if (userId == null)
        {
            return Unauthorized();
        }

        var result = await _stockService.EntradaStockAsync(dto, userId.Value);

        if (!result.Success)
        {
            return BadRequest(result);
        }

        return Ok(result);
    }

    /// <summary>
    /// Registar saída de stock (requer autenticação)
    /// </summary>
    /// <param name="dto">Dados do movimento</param>
    /// <returns>Produto com stock atualizado</returns>
    [HttpPost("saida")]
    [Authorize]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ApiResponse<ProdutoDto>), StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> SaidaStock([FromBody] MovimentoStockDto dto)
    {
        if (!ModelState.IsValid)
        {
            var errors = ModelState.Values
                .SelectMany(v => v.Errors)
                .Select(e => e.ErrorMessage)
                .ToList();

            return BadRequest(ApiResponse<ProdutoDto>.Fail("Dados inválidos", errors));
        }

        var userId = GetUserIdFromToken();
        if (userId == null)
        {
            return Unauthorized();
        }

        var result = await _stockService.SaidaStockAsync(dto, userId.Value);

        if (!result.Success)
        {
            return BadRequest(result);
        }

        return Ok(result);
    }

    /// <summary>
    /// Obter histórico de um produto específico
    /// </summary>
    /// <param name="produtoId">ID do produto</param>
    /// <param name="page">Número da página</param>
    /// <param name="pageSize">Itens por página</param>
    /// <returns>Histórico paginado</returns>
    [HttpGet("historico/{produtoId:int}")]
    [ProducesResponseType(typeof(PaginatedResponse<HistoricoDto>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetHistorico(
        int produtoId,
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 10)
    {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 50) pageSize = 50;

        var result = await _stockService.GetHistoricoAsync(produtoId, page, pageSize);
        return Ok(result);
    }

    /// <summary>
    /// Obter histórico geral de movimentos
    /// </summary>
    /// <param name="page">Número da página</param>
    /// <param name="pageSize">Itens por página</param>
    /// <returns>Histórico paginado</returns>
    [HttpGet("historico")]
    [ProducesResponseType(typeof(PaginatedResponse<HistoricoDto>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetHistoricoGeral(
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 20)
    {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 20;
        if (pageSize > 100) pageSize = 100;

        var result = await _stockService.GetHistoricoGeralAsync(page, pageSize);
        return Ok(result);
    }

    // ========================================
    // Métodos Auxiliares
    // ========================================

    private int? GetUserIdFromToken()
    {
        var userIdClaim = User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier);
        if (userIdClaim != null && int.TryParse(userIdClaim.Value, out int userId))
        {
            return userId;
        }
        return null;
    }
}
