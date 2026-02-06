package pt.ipt.dam.stockapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pt.ipt.dam.stockapp.data.local.entity.HistoricoEntity
import pt.ipt.dam.stockapp.data.local.entity.ProdutoEntity
import pt.ipt.dam.stockapp.data.model.ProdutoDto
import pt.ipt.dam.stockapp.data.repository.ProdutoRepository
import pt.ipt.dam.stockapp.util.Resource
import javax.inject.Inject

/**
 * Modo de operação (Entrada ou Saída)
 */
enum class ModoOperacao {
    ENTRADA,  // Verde
    SAIDA     // Vermelho
}

/**
 * Estado da UI principal
 */
data class StockUiState(
    val isLoading: Boolean = false,
    val produtos: List<ProdutoEntity> = emptyList(),
    val produtoSelecionado: ProdutoDto? = null,
    val historico: List<HistoricoEntity> = emptyList(),
    val categorias: List<String> = emptyList(),
    val modoOperacao: ModoOperacao = ModoOperacao.ENTRADA,
    val error: String? = null,
    val message: String? = null,
    val searchQuery: String = "",
    val categoriaSelecionada: String? = null
)

/**
 * ViewModel principal para gestão de stock
 */
@HiltViewModel
class StockViewModel @Inject constructor(
    private val produtoRepository: ProdutoRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()
    
    init {
        // Observar produtos do cache
        viewModelScope.launch {
            produtoRepository.observeProdutos().collect { produtos ->
                _uiState.update { it.copy(produtos = produtos) }
            }
        }
        
        // Observar categorias
        viewModelScope.launch {
            produtoRepository.observeCategorias().collect { categorias ->
                _uiState.update { it.copy(categorias = categorias) }
            }
        }
        
        // Sincronizar com API
        syncProdutos()
    }
    
    /**
     * Sincronizar produtos com a API
     */
    fun syncProdutos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = produtoRepository.syncProdutos()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
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
     * Alternar modo de operação (Entrada/Saída)
     */
    fun toggleModo() {
        _uiState.update { 
            it.copy(
                modoOperacao = if (it.modoOperacao == ModoOperacao.ENTRADA) 
                    ModoOperacao.SAIDA else ModoOperacao.ENTRADA
            )
        }
    }
    
    /**
     * Definir modo de operação
     */
    fun setModo(modo: ModoOperacao) {
        _uiState.update { it.copy(modoOperacao = modo) }
    }
    
    /**
     * Pesquisar produto por código de barras (scanner)
     */
    fun scanBarcode(codigo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = produtoRepository.getProdutoByBarcode(codigo)) {
                is Resource.Success -> {
                    val produto = result.data!!
                    
                    // Carregar histórico
                    loadHistorico(produto.id)
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            produtoSelecionado = produto
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Produto não encontrado: $codigo"
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    /**
     * Selecionar produto por ID
     */
    fun selectProduto(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = produtoRepository.getProdutoById(id)) {
                is Resource.Success -> {
                    loadHistorico(id)
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            produtoSelecionado = result.data
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
     * Limpar produto selecionado (voltar)
     */
    fun clearSelection() {
        _uiState.update { 
            it.copy(
                produtoSelecionado = null,
                historico = emptyList()
            )
        }
    }
    
    /**
     * Registar movimento de stock
     */
    fun registarMovimento(quantidade: Int, observacoes: String? = null) {
        val produto = _uiState.value.produtoSelecionado ?: return
        val modo = _uiState.value.modoOperacao
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = if (modo == ModoOperacao.ENTRADA) {
                produtoRepository.entradaStock(produto.codigoBarras, quantidade, observacoes)
            } else {
                produtoRepository.saidaStock(produto.codigoBarras, quantidade, observacoes)
            }
            
            when (result) {
                is Resource.Success -> {
                    val tipoStr = if (modo == ModoOperacao.ENTRADA) "Entrada" else "Saída"
                    
                    // Recarregar histórico
                    loadHistorico(produto.id)
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            produtoSelecionado = result.data,
                            message = "$tipoStr de $quantidade unidades registada com sucesso"
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
     * Carregar histórico de um produto
     */
    private fun loadHistorico(produtoId: Int) {
        viewModelScope.launch {
            // Observar do cache
            produtoRepository.observeHistorico(produtoId).collect { historico ->
                _uiState.update { it.copy(historico = historico) }
            }
        }
        
        // Carregar da API
        viewModelScope.launch {
            produtoRepository.loadHistorico(produtoId)
        }
    }
    
    /**
     * Pesquisar produtos
     */
    fun search(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        if (query.isBlank()) {
            viewModelScope.launch {
                produtoRepository.observeProdutos().collect { produtos ->
                    _uiState.update { it.copy(produtos = produtos) }
                }
            }
        } else {
            viewModelScope.launch {
                produtoRepository.searchProdutos(query).collect { produtos ->
                    _uiState.update { it.copy(produtos = produtos) }
                }
            }
        }
    }
    
    /**
     * Filtrar por categoria
     */
    fun filterByCategoria(categoria: String?) {
        _uiState.update { it.copy(categoriaSelecionada = categoria) }
    }
    
    /**
     * Limpar mensagens
     */
    fun clearMessages() {
        _uiState.update { it.copy(error = null, message = null) }
    }
}
