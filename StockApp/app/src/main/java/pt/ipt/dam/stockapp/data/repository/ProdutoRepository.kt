package pt.ipt.dam.stockapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.ipt.dam.stockapp.data.local.dao.HistoricoDao
import pt.ipt.dam.stockapp.data.local.dao.ProdutoDao
import pt.ipt.dam.stockapp.data.local.entity.HistoricoEntity
import pt.ipt.dam.stockapp.data.local.entity.ProdutoEntity
import pt.ipt.dam.stockapp.data.model.*
import pt.ipt.dam.stockapp.data.remote.StockApiService
import pt.ipt.dam.stockapp.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositório para operações com produtos e stock
 */
@Singleton
class ProdutoRepository @Inject constructor(
    private val api: StockApiService,
    private val produtoDao: ProdutoDao,
    private val historicoDao: HistoricoDao
) {
    
    // ========================================
    // Produtos - Leitura
    // ========================================
    
    /**
     * Observar produtos do cache local
     */
    fun observeProdutos(): Flow<List<ProdutoEntity>> = produtoDao.getAllProdutos()
    
    /**
     * Pesquisar produtos
     */
    fun searchProdutos(query: String): Flow<List<ProdutoEntity>> = produtoDao.searchProdutos(query)
    
    /**
     * Produtos com stock baixo
     */
    fun observeProdutosStockBaixo(): Flow<List<ProdutoEntity>> = produtoDao.getProdutosStockBaixo()
    
    /**
     * Categorias disponíveis
     */
    fun observeCategorias(): Flow<List<String>> = produtoDao.getCategorias()
    
    /**
     * Sincronizar produtos com a API
     */
    suspend fun syncProdutos(): Resource<List<ProdutoDto>> {
        return try {
            val response = api.getProdutos(page = 1, pageSize = 100)
            
            if (response.isSuccessful && response.body() != null) {
                val produtos = response.body()!!.items
                
                // Guardar no cache local
                val entities = produtos.map { it.toEntity() }
                produtoDao.insertProdutos(entities)
                
                Resource.Success(produtos)
            } else {
                Resource.Error("Erro ao carregar produtos")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de ligação")
        }
    }
    
    /**
     * Obter produto por código de barras
     */
    suspend fun getProdutoByBarcode(codigo: String): Resource<ProdutoDto> {
        return try {
            // Tentar primeiro do cache
            val cached = produtoDao.getProdutoByBarcode(codigo)
            
            // Buscar da API para ter dados atualizados
            val response = api.getProdutoByBarcode(codigo)
            
            if (response.isSuccessful && response.body()?.data != null) {
                val produto = response.body()!!.data!!
                
                // Atualizar cache
                produtoDao.insertProduto(produto.toEntity())
                
                Resource.Success(produto)
            } else if (cached != null) {
                // Usar cache se API falhar
                Resource.Success(cached.toDto())
            } else {
                Resource.Error("Produto não encontrado")
            }
        } catch (e: Exception) {
            // Tentar do cache em caso de erro de rede
            val cached = produtoDao.getProdutoByBarcode(codigo)
            if (cached != null) {
                Resource.Success(cached.toDto())
            } else {
                Resource.Error(e.message ?: "Erro de ligação")
            }
        }
    }
    
    /**
     * Obter produto por ID
     */
    suspend fun getProdutoById(id: Int): Resource<ProdutoDto> {
        return try {
            val response = api.getProdutoById(id)
            
            if (response.isSuccessful && response.body()?.data != null) {
                val produto = response.body()!!.data!!
                produtoDao.insertProduto(produto.toEntity())
                Resource.Success(produto)
            } else {
                val cached = produtoDao.getProdutoById(id)
                if (cached != null) {
                    Resource.Success(cached.toDto())
                } else {
                    Resource.Error("Produto não encontrado")
                }
            }
        } catch (e: Exception) {
            val cached = produtoDao.getProdutoById(id)
            if (cached != null) {
                Resource.Success(cached.toDto())
            } else {
                Resource.Error(e.message ?: "Erro de ligação")
            }
        }
    }
    
    // ========================================
    // Stock - Movimentos
    // ========================================
    
    /**
     * Registar entrada de stock
     */
    suspend fun entradaStock(
        codigoBarras: String,
        quantidade: Int,
        observacoes: String? = null
    ): Resource<ProdutoDto> {
        return try {
            val response = api.entradaStock(
                MovimentoStockRequest(codigoBarras, quantidade, observacoes)
            )
            
            if (response.isSuccessful && response.body()?.data != null) {
                val produto = response.body()!!.data!!
                
                // Atualizar cache local
                produtoDao.insertProduto(produto.toEntity())
                
                Resource.Success(produto)
            } else {
                val errorMsg = response.body()?.message ?: "Erro ao registar entrada"
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de ligação")
        }
    }
    
    /**
     * Registar saída de stock
     */
    suspend fun saidaStock(
        codigoBarras: String,
        quantidade: Int,
        observacoes: String? = null
    ): Resource<ProdutoDto> {
        return try {
            val response = api.saidaStock(
                MovimentoStockRequest(codigoBarras, quantidade, observacoes)
            )
            
            if (response.isSuccessful && response.body()?.data != null) {
                val produto = response.body()!!.data!!
                
                // Atualizar cache local
                produtoDao.insertProduto(produto.toEntity())
                
                Resource.Success(produto)
            } else {
                val errorMsg = response.body()?.message ?: "Erro ao registar saída"
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de ligação")
        }
    }
    
    // ========================================
    // Histórico
    // ========================================
    
    /**
     * Observar histórico de um produto
     */
    fun observeHistorico(produtoId: Int): Flow<List<HistoricoEntity>> = 
        historicoDao.getHistoricoByProduto(produtoId)
    
    /**
     * Carregar histórico de um produto da API
     */
    suspend fun loadHistorico(produtoId: Int, page: Int = 1): Resource<List<HistoricoDto>> {
        return try {
            val response = api.getHistoricoProduto(produtoId, page)
            
            if (response.isSuccessful && response.body() != null) {
                val historico = response.body()!!.items
                
                // Guardar no cache
                val entities = historico.map { it.toEntity() }
                historicoDao.insertHistoricos(entities)
                
                Resource.Success(historico)
            } else {
                Resource.Error("Erro ao carregar histórico")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de ligação")
        }
    }
    
    // ========================================
    // Mappers
    // ========================================
    
    private fun ProdutoDto.toEntity() = ProdutoEntity(
        id = id,
        codigoBarras = codigoBarras,
        nome = nome,
        descricao = descricao,
        imagemUrl = imagemUrl,
        stock = stock,
        stockMinimo = stockMinimo,
        precoUnitario = precoUnitario,
        categoria = categoria,
        localizacao = localizacao,
        ativo = ativo,
        updatedAt = updatedAt
    )
    
    private fun ProdutoEntity.toDto() = ProdutoDto(
        id = id,
        codigoBarras = codigoBarras,
        nome = nome,
        descricao = descricao,
        imagemUrl = imagemUrl,
        stock = stock,
        stockMinimo = stockMinimo,
        precoUnitario = precoUnitario,
        categoria = categoria,
        localizacao = localizacao,
        ativo = ativo,
        stockBaixo = stock <= stockMinimo,
        createdAt = updatedAt,
        updatedAt = updatedAt
    )
    
    private fun HistoricoDto.toEntity() = HistoricoEntity(
        id = id,
        produtoId = produtoId,
        produtoNome = produtoNome,
        codigoBarras = codigoBarras,
        userId = userId,
        userNome = userNome,
        quantidade = quantidade,
        tipoMovimento = tipoMovimento,
        stockAnterior = stockAnterior,
        stockAtual = stockAtual,
        observacoes = observacoes,
        dataMovimento = dataMovimento
    )
}
