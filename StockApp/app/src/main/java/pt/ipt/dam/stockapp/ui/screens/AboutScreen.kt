package pt.ipt.dam.stockapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pt.ipt.dam.stockapp.BuildConfig

/**
 * Ecrã Sobre - Informação obrigatória do projeto
 * 
 * Conforme as regras de avaliação, deve incluir:
 * - Nome do curso, disciplina e ano letivo
 * - Nº, nome e fotografia dos autores
 * - Bibliotecas e código de terceiros utilizados
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sobre a Aplicação") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo/Ícone da App
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Gestão de Stock",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Versão ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // ============================================
            // Informação do Curso (OBRIGATÓRIO)
            // ============================================
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Informação Académica",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    InfoRow(label = "Instituição", value = "Politécnico de Tomar")
                    InfoRow(label = "Escola", value = "Escola Superior de Tecnologia de Tomar")
                    InfoRow(label = "Curso", value = "Licenciatura em Engenharia Informática")
                    InfoRow(label = "Disciplina", value = "Desenvolvimento de Aplicações Móveis")
                    InfoRow(label = "Ano Letivo", value = "2025/2026")
                    InfoRow(label = "Semestre", value = "1º Semestre - 3º Ano")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ============================================
            // Autores (OBRIGATÓRIO - Alterar com os vossos dados)
            // ============================================
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Autores",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // AUTOR 1 - Alterar com os vossos dados
                    AuthorCard(
                        numero = "XXXXX",  // TODO: Alterar
                        nome = "Nome do Aluno 1",  // TODO: Alterar
                        // TODO: Adicionar foto em res/drawable
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // AUTOR 2 - Alterar com os vossos dados
                    AuthorCard(
                        numero = "XXXXX",  // TODO: Alterar
                        nome = "Nome do Aluno 2",  // TODO: Alterar
                        // TODO: Adicionar foto em res/drawable
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ============================================
            // Bibliotecas e Código de Terceiros (OBRIGATÓRIO)
            // ============================================
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bibliotecas e Frameworks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LibraryItem(
                        name = "Jetpack Compose",
                        description = "UI toolkit declarativo para Android",
                        source = "Google/AndroidX"
                    )
                    
                    LibraryItem(
                        name = "Retrofit",
                        description = "Cliente HTTP type-safe para Android",
                        source = "Square - https://square.github.io/retrofit/"
                    )
                    
                    LibraryItem(
                        name = "Room",
                        description = "Camada de abstração sobre SQLite",
                        source = "Google/AndroidX"
                    )
                    
                    LibraryItem(
                        name = "Hilt",
                        description = "Injeção de dependências para Android",
                        source = "Google/Dagger - https://dagger.dev/hilt/"
                    )
                    
                    LibraryItem(
                        name = "CameraX",
                        description = "API de câmara para Android",
                        source = "Google/AndroidX"
                    )
                    
                    LibraryItem(
                        name = "ML Kit Barcode Scanning",
                        description = "Leitura de códigos de barras",
                        source = "Google - https://developers.google.com/ml-kit"
                    )
                    
                    LibraryItem(
                        name = "OkHttp",
                        description = "Cliente HTTP eficiente",
                        source = "Square - https://square.github.io/okhttp/"
                    )
                    
                    LibraryItem(
                        name = "Coil",
                        description = "Carregamento de imagens para Compose",
                        source = "https://coil-kt.github.io/coil/"
                    )
                    
                    LibraryItem(
                        name = "Accompanist Permissions",
                        description = "Gestão de permissões em Compose",
                        source = "Google - https://google.github.io/accompanist/"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Descrição da App
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sobre o Projeto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Aplicação de gestão de stocks para armazém desenvolvida " +
                               "no âmbito da unidade curricular de Desenvolvimento de " +
                               "Aplicações Móveis. A aplicação permite o registo de entradas " +
                               "e saídas de produtos através da leitura de códigos de barras, " +
                               "com autenticação de utilizadores e histórico de movimentos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Justify
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun AuthorCard(
    numero: String,
    nome: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder para foto (substituir por Image real)
        Surface(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = nome,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Nº $numero",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun LibraryItem(
    name: String,
    description: String,
    source: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = source,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        
        HorizontalDivider(
            modifier = Modifier.padding(top = 6.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
