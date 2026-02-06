package pt.ipt.dam.stockapp.data.remote

import pt.ipt.dam.stockapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface Retrofit para comunicação com a API
 */
interface StockApiService {

    // ========================================
    // Autenticação
    // ========================================
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/verify")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<AuthResponse>
    
    @POST("auth/resend-code")
    suspend fun resendCode(@Body request: ResendCodeRequest): Response<AuthResponse>
    
    @GET("auth/profile")
    suspend fun getProfile(): Response<ApiResponse<UserDto>>

    // ========================================
    // Produtos
    // ========================================
    
    @GET("produtos")
    suspend fun getProdutos(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("categoria") categoria: String? = null
    ): Response<PaginatedResponse<ProdutoDto>>
    
    @GET("produtos/{id}")
    suspend fun getProdutoById(@Path("id") id: Int): Response<ApiResponse<ProdutoDto>>
    
    @GET("produtos/barcode/{codigo}")
    suspend fun getProdutoByBarcode(@Path("codigo") codigo: String): Response<ApiResponse<ProdutoDto>>
    
    @POST("produtos")
    suspend fun createProduto(@Body request: ProdutoCreateRequest): Response<ApiResponse<ProdutoDto>>
    
    @PUT("produtos/{id}")
    suspend fun updateProduto(
        @Path("id") id: Int,
        @Body request: ProdutoUpdateRequest
    ): Response<ApiResponse<ProdutoDto>>
    
    @DELETE("produtos/{id}")
    suspend fun deleteProduto(@Path("id") id: Int): Response<ApiResponse<Boolean>>
    
    @GET("produtos/categorias")
    suspend fun getCategorias(): Response<ApiResponse<List<String>>>

    // ========================================
    // Stock
    // ========================================
    
    @POST("stock/entrada")
    suspend fun entradaStock(@Body request: MovimentoStockRequest): Response<ApiResponse<ProdutoDto>>
    
    @POST("stock/saida")
    suspend fun saidaStock(@Body request: MovimentoStockRequest): Response<ApiResponse<ProdutoDto>>
    
    @GET("stock/historico/{produtoId}")
    suspend fun getHistoricoProduto(
        @Path("produtoId") produtoId: Int,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<PaginatedResponse<HistoricoDto>>
    
    @GET("stock/historico")
    suspend fun getHistoricoGeral(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<PaginatedResponse<HistoricoDto>>
}
