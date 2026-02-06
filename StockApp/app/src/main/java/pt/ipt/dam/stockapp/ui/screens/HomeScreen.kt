package pt.ipt.dam.stockapp.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pt.ipt.dam.stockapp.data.local.entity.ProdutoEntity
import pt.ipt.dam.stockapp.ui.theme.StockColors
import pt.ipt.dam.stockapp.ui.viewmodel.AuthViewModel
import pt.ipt.dam.stockapp.ui.viewmodel.ModoOperacao
import pt.ipt.dam.stockapp.ui.viewmodel.StockViewModel

/**
 * Ecrã principal da aplicação
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    stockViewModel: StockViewModel,
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val stockState by stockViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    
    // Cor de fundo animada baseada no modo
    val backgroundColor by animateColorAsState(
        targetValue = when (stockState.modoOperacao) {
            ModoOperacao.ENTRADA -> StockColors.entradaColor.copy(alpha = 0.1f)
            ModoOperacao.SAIDA -> StockColors.saidaColor.copy(alpha = 0.1f)
        },
        label = "backgroundColor"
    )
    
    val modoColor = when (stockState.modoOperacao) {
        ModoOperacao.ENTRADA -> StockColors.entradaColor
        ModoOperacao.SAIDA -> StockColors.saidaColor
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Gestão de Stock")
                        Text(
                            text = if (stockState.modoOperacao == ModoOperacao.ENTRADA) 
                                "Modo: Entrada" else "Modo: Saída",
                            style = MaterialTheme.typography.bodySmall,
                            color = modoColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = modoColor.copy(alpha = 0.15f)
                ),
                actions = {
                    // Botão Sobre
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(Icons.Default.Info, contentDescription = "Sobre")
                    }
                    
                    // Botão Login/Logout
                    if (authState.isLoggedIn) {
                        IconButton(onClick = { authViewModel.logout() }) {
                            Icon(Icons.Default.Logout, contentDescription = "Sair")
                        }
                    } else {
                        IconButton(onClick = onNavigateToLogin) {
                            Icon(Icons.Default.Login, contentDescription = "Entrar")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botão de alternar modo (B)
                FloatingActionButton(
                    onClick = { stockViewModel.toggleModo() },
                    containerColor = modoColor,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Text(
                        text = "B",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Botão de scanner
                ExtendedFloatingActionButton(
                    onClick = onNavigateToScanner,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                    text = { Text("Scan") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            // Barra de pesquisa
            SearchBar(
                query = stockState.searchQuery,
                onQueryChange = { stockViewModel.search(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // Info do utilizador se autenticado
            if (authState.isLoggedIn && authState.user != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Olá, ${authState.user!!.nome}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Lista de produtos
            if (stockState.isLoading && stockState.produtos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (stockState.produtos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum produto encontrado",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(stockState.produtos) { produto ->
                        ProdutoCard(
                            produto = produto,
                            onClick = { onNavigateToProduct(produto.id) }
                        )
                    }
                }
            }
        }
    }
    
    // Snackbar para mensagens
    stockState.error?.let { error ->
        LaunchedEffect(error) {
            // Mostrar erro e limpar
            stockViewModel.clearMessages()
        }
    }
}

/**
 * Barra de pesquisa
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Pesquisar produto...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpar")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Card de produto
 */
@Composable
fun ProdutoCard(
    produto: ProdutoEntity,
    onClick: () -> Unit
) {
    val stockBaixo = produto.stock <= produto.stockMinimo
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de stock
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (stockBaixo) StockColors.warningColor.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (stockBaixo) Icons.Default.Warning else Icons.Default.Inventory,
                    contentDescription = null,
                    tint = if (stockBaixo) StockColors.warningColor 
                           else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Informações do produto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = produto.nome,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = produto.codigoBarras,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                produto.categoria?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Stock
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${produto.stock}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (stockBaixo) StockColors.warningColor 
                            else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "unidades",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
