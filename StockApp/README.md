# Stock App - Gestão de Stocks para Android

Aplicação Android nativa para gestão de stocks de armazém, desenvolvida em Kotlin com Jetpack Compose.

## 📱 Funcionalidades

### Ecrã Principal
- Lista de produtos com pesquisa
- Indicador visual de stock baixo
- **Botão "B"** que alterna entre modo Entrada (verde) e Saída (vermelho)
- Cor de fundo muda conforme o modo selecionado

### Scanner de Códigos de Barras
- Utiliza CameraX + ML Kit
- Leitura automática de códigos de barras
- Suporta EAN-13, EAN-8, UPC-A, Code 128, QR Code, etc.

### Detalhe do Produto
- Informação completa do produto
- Stock atual com indicador visual
- **Botões +/-** para alterar quantidade (apenas com autenticação)
- Histórico de movimentos paginado

### Autenticação
- Registo com verificação por código
- Login com JWT
- Controlo de acesso: sem autenticação apenas visualiza

### Ecrã "Sobre"
- Informação académica obrigatória
- Lista de autores com foto
- Bibliotecas utilizadas

## 🛠️ Stack Tecnológica

| Componente | Tecnologia |
|------------|------------|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | MVVM + Clean Architecture |
| DI | Hilt |
| Networking | Retrofit + OkHttp |
| Cache Local | Room |
| Câmara | CameraX |
| Scanner | ML Kit Barcode Scanning |
| Navegação | Navigation Compose |

## 📁 Estrutura do Projeto

```
app/src/main/java/pt/ipt/dam/stockapp/
├── data/
│   ├── local/
│   │   ├── dao/           # DAOs Room
│   │   ├── entity/        # Entidades locais
│   │   └── AppDatabase.kt
│   ├── model/             # DTOs da API
│   ├── remote/
│   │   ├── StockApiService.kt  # Interface Retrofit
│   │   └── AuthInterceptor.kt  # JWT auto-inject
│   └── repository/        # Repositórios
├── di/
│   └── AppModule.kt       # Módulos Hilt
├── ui/
│   ├── navigation/        # Navegação
│   ├── screens/           # Ecrãs Compose
│   ├── theme/             # Tema Material 3
│   └── viewmodel/         # ViewModels
├── util/
│   └── Resource.kt        # Wrapper de estados
├── MainActivity.kt
└── StockApplication.kt
```

## ⚙️ Configuração

### 1. URL da API

Editar `app/build.gradle.kts`:

```kotlin
// Debug (emulador)
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:50826/api/\"")

// Release (produção)
buildConfigField("String", "API_BASE_URL", "\"https://10.0.2.2:50825/api/\"")
```

**Nota:** `10.0.2.2` é o IP do localhost visto pelo emulador Android.

### 2. Permissões Necessárias

Já configuradas no `AndroidManifest.xml`:
- `INTERNET` - Comunicação com API
- `ACCESS_NETWORK_STATE` - Verificar conectividade
- `CAMERA` - Scanner de código de barras

### 3. Requisitos

- Android Studio Hedgehog ou superior
- SDK mínimo: 26 (Android 8.0)
- SDK target: 34 (Android 14)
- JDK 17

## 🚀 Compilar e Executar

```bash
# Clonar repositório
git clone https://github.com/[username]/stock-app-android.git

# Abrir no Android Studio e sincronizar Gradle

# Executar no emulador ou dispositivo
```

## 🎨 Modos de Operação

A aplicação tem dois modos visuais:

| Modo | Cor | Ação |
|------|-----|------|
| **Entrada** | 🟢 Verde | Adiciona stock |
| **Saída** | 🔴 Vermelho | Remove stock |

O botão **"B"** no ecrã principal alterna entre os modos.

## 📊 Fluxo de Dados

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   UI/View   │ ←── │  ViewModel  │ ←── │ Repository  │
└─────────────┘     └─────────────┘     └─────────────┘
                           │                   │
                           │                   ├── API (Retrofit)
                           │                   └── Cache (Room)
                           │
                    StateFlow/Flow
```

## ✅ Checklist de Requisitos DAM

- [x] Desenvolvimento nativo em Kotlin
- [x] Componente hardware: Câmara (scanner)
- [x] API REST própria
- [x] Autenticação obrigatória
- [x] Validação de dados
- [x] Controlo de acesso
- [x] Interface em Português
- [x] Ecrã "Sobre" com créditos
- [ ] Publicação na Google Play
- [ ] Fotos dos autores

## 📝 TODO para Entrega

1. **Alterar dados dos autores** em `AboutScreen.kt`
2. **Adicionar fotos** dos autores em `res/drawable`
3. **Configurar URL** da API de produção
4. **Gerar APK assinado** para entrega
5. **Publicar na Google Play**
6. **Criar relatório PDF** com assinatura digital

## 🔧 Comandos Úteis

```bash
# Gerar APK debug
./gradlew assembleDebug

# Gerar APK release
./gradlew assembleRelease

# Gerar Bundle (para Play Store)
./gradlew bundleRelease

# Limpar e reconstruir
./gradlew clean build
```

## 📄 Licença

Projeto académico - IPT 2025/26

## 👥 Autores

- **[Nome Aluno 1]** - Nº XXXXX
- **[Nome Aluno 2]** - Nº XXXXX

---

**Eng. Informática - Politécnico de Tomar**  
**Desenvolvimento de Aplicações Móveis - 2025/26**
