package pt.ipt.dam.stockapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam.stockapp.data.local.entity.HistoricoEntity
import pt.ipt.dam.stockapp.data.local.entity.ProdutoEntity
import pt.ipt.dam.stockapp.data.local.entity.UserSessionEntity

/**
 * DAO para operações com produtos
 */
@Dao
interface ProdutoDao {
    
    @Query("SELECT * FROM produtos WHERE ativo = 1 ORDER BY nome ASC")
    fun getAllProdutos(): Flow<List<ProdutoEntity>>
    
    @Query("SELECT * FROM produtos WHERE id = :id")
    suspend fun getProdutoById(id: Int): ProdutoEntity?
    
    @Query("SELECT * FROM produtos WHERE codigoBarras = :codigo")
    suspend fun getProdutoByBarcode(codigo: String): ProdutoEntity?
    
    @Query("SELECT * FROM produtos WHERE nome LIKE '%' || :search || '%' OR codigoBarras LIKE '%' || :search || '%'")
    fun searchProdutos(search: String): Flow<List<ProdutoEntity>>
    
    @Query("SELECT * FROM produtos WHERE categoria = :categoria AND ativo = 1")
    fun getProdutosByCategoria(categoria: String): Flow<List<ProdutoEntity>>
    
    @Query("SELECT * FROM produtos WHERE stock <= stockMinimo AND ativo = 1")
    fun getProdutosStockBaixo(): Flow<List<ProdutoEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduto(produto: ProdutoEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProdutos(produtos: List<ProdutoEntity>)
    
    @Update
    suspend fun updateProduto(produto: ProdutoEntity)
    
    @Query("UPDATE produtos SET stock = :novoStock, updatedAt = :updatedAt WHERE id = :produtoId")
    suspend fun updateStock(produtoId: Int, novoStock: Int, updatedAt: String)
    
    @Delete
    suspend fun deleteProduto(produto: ProdutoEntity)
    
    @Query("DELETE FROM produtos")
    suspend fun deleteAll()
    
    @Query("SELECT DISTINCT categoria FROM produtos WHERE categoria IS NOT NULL AND ativo = 1 ORDER BY categoria")
    fun getCategorias(): Flow<List<String>>
}

/**
 * DAO para operações com histórico
 */
@Dao
interface HistoricoDao {
    
    @Query("SELECT * FROM historico WHERE produtoId = :produtoId ORDER BY dataMovimento DESC")
    fun getHistoricoByProduto(produtoId: Int): Flow<List<HistoricoEntity>>
    
    @Query("SELECT * FROM historico ORDER BY dataMovimento DESC LIMIT :limit")
    fun getRecentHistorico(limit: Int = 50): Flow<List<HistoricoEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistorico(historico: HistoricoEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricos(historicos: List<HistoricoEntity>)
    
    @Query("DELETE FROM historico WHERE produtoId = :produtoId")
    suspend fun deleteByProduto(produtoId: Int)
    
    @Query("DELETE FROM historico")
    suspend fun deleteAll()
}

/**
 * DAO para sessão do utilizador
 */
@Dao
interface UserSessionDao {
    
    @Query("SELECT * FROM user_session WHERE id = 1")
    suspend fun getSession(): UserSessionEntity?
    
    @Query("SELECT * FROM user_session WHERE id = 1")
    fun observeSession(): Flow<UserSessionEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: UserSessionEntity)
    
    @Query("DELETE FROM user_session")
    suspend fun clearSession()
    
    @Query("SELECT token FROM user_session WHERE id = 1")
    suspend fun getToken(): String?
}
