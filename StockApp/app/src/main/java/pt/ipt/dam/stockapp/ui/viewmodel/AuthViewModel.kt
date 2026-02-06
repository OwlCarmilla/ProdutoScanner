package pt.ipt.dam.stockapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pt.ipt.dam.stockapp.data.local.entity.UserSessionEntity
import pt.ipt.dam.stockapp.data.repository.AuthRepository
import pt.ipt.dam.stockapp.util.Resource
import javax.inject.Inject

/**
 * Estados da UI de autenticação
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: UserSessionEntity? = null,
    val error: String? = null,
    val message: String? = null,
    val needsVerification: Boolean = false,
    val verificationEmail: String? = null
)

/**
 * ViewModel para autenticação
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        // Observar estado da sessão
        viewModelScope.launch {
            authRepository.observeSession().collect { session ->
                _uiState.update { 
                    it.copy(
                        isLoggedIn = session != null,
                        user = session
                    )
                }
            }
        }
    }
    
    /**
     * Registar novo utilizador
     */
    fun register(email: String, password: String, nome: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.register(email, password, nome)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            message = result.data?.message,
                            needsVerification = true,
                            verificationEmail = email
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    /**
     * Login
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            message = "Login efetuado com sucesso"
                        )
                    }
                }
                is Resource.Error -> {
                    // Verificar se precisa de verificação
                    if (result.message?.contains("não verificada", ignoreCase = true) == true) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                needsVerification = true,
                                verificationEmail = email,
                                error = result.message
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
    
    /**
     * Verificar código
     */
    fun verifyCode(code: String) {
        val email = _uiState.value.verificationEmail ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.verifyCode(email, code)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            needsVerification = false,
                            message = "Conta verificada com sucesso"
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    /**
     * Reenviar código
     */
    fun resendCode() {
        val email = _uiState.value.verificationEmail ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.resendCode(email)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            message = result.data?.message
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    /**
     * Logout
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { 
                AuthUiState(isLoggedIn = false)
            }
        }
    }
    
    /**
     * Limpar erros/mensagens
     */
    fun clearMessages() {
        _uiState.update { it.copy(error = null, message = null) }
    }
    
    /**
     * Voltar ao login
     */
    fun backToLogin() {
        _uiState.update { 
            it.copy(needsVerification = false, verificationEmail = null)
        }
    }
}
