package pt.ipt.dam.stockapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade local para cache de produtos
 */
@Entity(tableName = "produtos")
data class ProdutoEntity(
    @PrimaryKey
    val id: Int,
    val codigoBarras: String,
    val nome: String,
    val descricao: String?,
    val imagemUrl: String?,
    val stock: Int,
    val stockMinimo: Int,
    val precoUnitario: Double,
    val categoria: String?,
    val localizacao: String?,
    val ativo: Boolean,
    val updatedAt: String,
    // Timestamp de sincronização local
    val syncedAt: Long = System.currentTimeMillis()
)

/**
 * Entidade local para cache de histórico
 */
@Entity(tableName = "historico")
data class HistoricoEntity(
    @PrimaryKey
    val id: Int,
    val produtoId: Int,
    val produtoNome: String,
    val codigoBarras: String,
    val userId: Int,
    val userNome: String,
    val quantidade: Int,
    val tipoMovimento: String,
    val stockAnterior: Int,
    val stockAtual: Int,
    val observacoes: String?,
    val dataMovimento: String
)

/**
 * Entidade para dados do utilizador autenticado
 */
@Entity(tableName = "user_session")
data class UserSessionEntity(
    @PrimaryKey
    val id: Int = 1, // Apenas um registo
    val userId: Int,
    val email: String,
    val nome: String,
    val token: String,
    val loginAt: Long = System.currentTimeMillis()
)
