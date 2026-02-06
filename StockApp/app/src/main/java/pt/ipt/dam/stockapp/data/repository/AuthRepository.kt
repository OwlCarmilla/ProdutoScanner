package pt.ipt.dam.stockapp.data.repository

import kotlinx.coroutines.flow.Flow
import pt.ipt.dam.stockapp.data.local.dao.UserSessionDao
import pt.ipt.dam.stockapp.data.local.entity.UserSessionEntity
import pt.ipt.dam.stockapp.data.model.*
import pt.ipt.dam.stockapp.data.remote.StockApiService
import pt.ipt.dam.stockapp.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositório para operações de autenticação
 */
@Singleton
class AuthRepository @Inject constructor(
    private val api: StockApiService,
    private val userSessionDao: UserSessionDao
) {
    
    /**
     * Observar estado da sessão
     */
    fun observeSession(): Flow<UserSessionEntity?> = userSessionDao.observeSession()
    
    /**
     * Verificar se utilizador está autenticado
     */
    suspend fun isLoggedIn(): Boolean = userSessionDao.getSession() != null
    
    /**
     * Obter sessão atual
     */
    suspend fun getSession(): UserSessionEntity? = userSessionDao.getSession()
    
    /**
     * Registar novo utilizador
     */
    suspend fun register(email: String, password: String, nome: String): Resource<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(email, password, nome))
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Resource.Success(body)
                } else {
                    Resource.Error(body.message)
                }
            } else {
                Resource.Error(response.message() ?: "Erro no registo")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de ligação")
        }
    }
    
    /**
     * Autenticar utilizador
     */
    suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.token != null && body.user != null) {
                    // Guardar sessão localmente
                    saveSession(body.user, body.token)
                    Resource.Success(body)
                } else {
                    Resource.Error(body.message)
                }
            } else {
                Resource.Error("Email ou password incorretos")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de ligação")
        }
    }
    
    /**
     * Verificar código de ativação
     */
    suspend fun verifyCode(email: String, code: String): Resource<AuthResponse> {
        return try {
            val response = api.verifyCode(VerifyCodeRequest(email, code))
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.token != null && body.user != null) {
                    saveSession(body.user, body.token)
                    Resource.Success(body)
                } else {
                    Resource.Error(body.message)
                }
            } else {
                Resource.Error("Código inválido")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de ligação")
        }
    }
    
    /**
     * Reenviar código de verificação
     */
    suspend fun resendCode(email: String): Resource<AuthResponse> {
        return try {
            val response = api.resendCode(ResendCodeRequest(email))
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Resource.Success(body)
                } else {
                    Resource.Error(body.message)
                }
            } else {
                Resource.Error("Erro ao reenviar código")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de ligação")
        }
    }
    
    /**
     * Terminar sessão
     */
    suspend fun logout() {
        userSessionDao.clearSession()
    }
    
    /**
     * Guardar sessão localmente
     */
    private suspend fun saveSession(user: UserDto, token: String) {
        userSessionDao.saveSession(
            UserSessionEntity(
                userId = user.id,
                email = user.email,
                nome = user.nome,
                token = token
            )
        )
    }
}
