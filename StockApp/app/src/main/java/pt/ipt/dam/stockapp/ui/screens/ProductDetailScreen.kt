package pt.ipt.dam.stockapp.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pt.ipt.dam.stockapp.data.local.entity.HistoricoEntity
import pt.ipt.dam.stockapp.ui.theme.StockColors
import pt.ipt.dam.stockapp.ui.viewmodel.AuthViewModel
import pt.ipt.dam.stockapp.ui.viewmodel.ModoOperacao
import pt.ipt.dam.stockapp.ui.viewmodel.StockViewModel

/**
 * Ecrã de detalhe do produto
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    stockViewModel: StockViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val stockState by stockViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    
    val produto = stockState.produtoSelecionado
    
    var quantidade by remember { mutableStateOf("1") }
    var observacoes by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    
    val modoColor = when (stockState.modoOperacao) {
        ModoOperacao.ENTRADA -> StockColors.entradaColor
        ModoOperacao.SAIDA -> StockColors.saidaColor
    }
    
    val backgroundColor by animateColorAsState(
        targetValue = modoColor.copy(alpha = 0.1f),
        label = "backgroundColor"
    )

    if (produto == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(produto.nome) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = modoColor.copy(alpha = 0.15f)
                ),
                actions = {
                    // Botão de alternar modo
                    IconButton(onClick = { stockViewModel.toggleModo() }) {
                        Icon(
                            imageVector = if (stockState.modoOperacao == ModoOperacao.ENTRADA)
                                Icons.Default.Add else Icons.Default.Remove,
                            contentDescription = "Alternar modo",
                            tint = modoColor
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de informação do produto
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Código de barras
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = produto.codigoBarras,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Nome
                        Text(
                            text = produto.nome,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Descrição
                        produto.descricao?.let { desc ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Categoria e localização
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            produto.categoria?.let {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(it) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Category,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                            
                            produto.localizacao?.let {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(it) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Place,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Card de Stock
            item {
                val stockBaixo = produto.stockBaixo
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (stockBaixo) 
                            StockColors.warningColor.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Stock Atual",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "${produto.stock}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (stockBaixo) StockColors.warningColor
                                    else MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = "unidades",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        
                        if (stockBaixo) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = StockColors.warningColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Stock baixo! (mín: ${produto.stockMinimo})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StockColors.warningColor
                                )
                            }
                        }
                    }
                }
            }
            
            // Área de movimento de stock
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = modoColor.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Indicador de modo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (stockState.modoOperacao == ModoOperacao.ENTRADA)
                                    Icons.Default.Add else Icons.Default.Remove,
                                contentDescription = null,
                                tint = modoColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (stockState.modoOperacao == ModoOperacao.ENTRADA)
                                    "ENTRADA DE STOCK" else "SAÍDA DE STOCK",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = modoColor
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (!authState.isLoggedIn) {
                            // Aviso de autenticação necessária
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "É necessário autenticar-se para alterar o stock",
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = onNavigateToLogin) {
                                        Text("Entrar")
                                    }
                                }
                            }
                        } else {
                            // Campo de quantidade
                            OutlinedTextField(
                                value = quantidade,
                                onValueChange = { 
                                    if (it.isEmpty() || it.all { c -> c.isDigit() }) {
                                        quantidade = it
                                    }
                                },
                                label = { Text("Quantidade") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Default.Numbers, contentDescription = null)
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Campo de observações
                            OutlinedTextField(
                                value = observacoes,
                                onValueChange = { observacoes = it },
                                label = { Text("Observações (opcional)") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2,
                                leadingIcon = {
                                    Icon(Icons.Default.Notes, contentDescription = null)
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Botão de confirmar
                            Button(
                                onClick = { showDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = quantidade.isNotEmpty() && 
                                          quantidade.toIntOrNull() != null &&
                                          quantidade.toInt() > 0 &&
                                          !stockState.isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = modoColor
                                )
                            ) {
                                if (stockState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (stockState.modoOperacao == ModoOperacao.ENTRADA)
                                            Icons.Default.Add else Icons.Default.Remove,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (stockState.modoOperacao == ModoOperacao.ENTRADA)
                                            "Registar Entrada" else "Registar Saída"
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Histórico
            item {
                Text(
                    text = "Histórico de Movimentos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (stockState.historico.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sem movimentos registados",
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(stockState.historico) { movimento ->
                    HistoricoItem(movimento = movimento)
                }
            }
        }
    }
    
    // Dialog de confirmação
    if (showDialog) {
        val qtd = quantidade.toIntOrNull() ?: 0
        
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    if (stockState.modoOperacao == ModoOperacao.ENTRADA)
                        "Confirmar Entrada" else "Confirmar Saída"
                )
            },
            text = {
                Column {
                    Text("Produto: ${produto.nome}")
                    Text("Quantidade: $qtd")
                    if (stockState.modoOperacao == ModoOperacao.ENTRADA) {
                        Text("Novo stock: ${produto.stock + qtd}")
                    } else {
                        Text("Novo stock: ${produto.stock - qtd}")
                        if (produto.stock - qtd < 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "⚠️ Atenção: Stock ficará negativo!",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        stockViewModel.registarMovimento(
                            qtd,
                            observacoes.takeIf { it.isNotBlank() }
                        )
                        quantidade = "1"
                        observacoes = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = modoColor)
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Mostrar mensagens
    LaunchedEffect(stockState.message) {
        stockState.message?.let {
            // Mensagem de sucesso
            stockViewModel.clearMessages()
        }
    }
}

/**
 * Item do histórico
 */
@Composable
fun HistoricoItem(movimento: HistoricoEntity) {
    val isEntrada = movimento.tipoMovimento == "Entrada"
    val color = if (isEntrada) StockColors.entradaColor else StockColors.saidaColor
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isEntrada) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = null,
                    tint = color
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movimento.tipoMovimento,
                    style = MaterialTheme.typography.titleSmall,
                    color = color
                )
                Text(
                    text = movimento.userNome,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                movimento.observacoes?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Quantidade e stock
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isEntrada) "+" else "-"}${movimento.quantidade}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "${movimento.stockAnterior} → ${movimento.stockAtual}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
