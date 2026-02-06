package pt.ipt.dam.stockapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * ========================================
 * Modelos de Autenticação
 * ========================================
 */

data class RegisterRequest(
    val email: String,
    val password: String,
    val nome: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class VerifyCodeRequest(
    val email: String,
    val code: String
)

data class ResendCodeRequest(
    val email: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserDto?
)

data class UserDto(
    val id: Int,
    val email: String,
    val nome: String,
    val isVerified: Boolean,
    val createdAt: String
)

/**
 * ========================================
 * Modelos de Produto
 * ========================================
 */

data class ProdutoDto(
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
    val stockBaixo: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class ProdutoCreateRequest(
    val codigoBarras: String,
    val nome: String,
    val descricao: String? = null,
    val imagemUrl: String? = null,
    val stock: Int = 0,
    val stockMinimo: Int = 0,
    val precoUnitario: Double = 0.0,
    val categoria: String? = null,
    val localizacao: String? = null
)

data class ProdutoUpdateRequest(
    val nome: String? = null,
    val descricao: String? = null,
    val imagemUrl: String? = null,
    val stockMinimo: Int? = null,
    val precoUnitario: Double? = null,
    val categoria: String? = null,
    val localizacao: String? = null,
    val ativo: Boolean? = null
)

/**
 * ========================================
 * Modelos de Stock
 * ========================================
 */

data class MovimentoStockRequest(
    val codigoBarras: String,
    val quantidade: Int,
    val observacoes: String? = null
)

data class HistoricoDto(
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
 * ========================================
 * Respostas Genéricas da API
 * ========================================
 */

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val errors: List<String>?
)

data class PaginatedResponse<T>(
    val items: List<T>,
    val totalItems: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
