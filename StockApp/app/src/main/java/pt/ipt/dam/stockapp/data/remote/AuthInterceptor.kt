package pt.ipt.dam.stockapp.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import pt.ipt.dam.stockapp.data.local.dao.UserSessionDao
import javax.inject.Inject

/**
 * Interceptor que adiciona o token JWT aos pedidos
 */
class AuthInterceptor @Inject constructor(
    private val userSessionDao: UserSessionDao
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Obter token de forma síncrona
        val token = runBlocking { userSessionDao.getToken() }
        
        // Se não há token, continuar sem header
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // Adicionar header de autorização
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        
        return chain.proceed(newRequest)
    }
}
