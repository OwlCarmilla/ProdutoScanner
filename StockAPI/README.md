# Stock API - Gestão de Armazém

API REST para gestão de stocks de armazém, desenvolvida para o projeto de DAM (Desenvolvimento de Aplicações Móveis) 2025/26.

## 🛠️ Tecnologias

- **Framework:** ASP.NET Core 8.0
- **Base de Dados:** MySQL 8.0
- **ORM:** Entity Framework Core 8.0
- **Autenticação:** JWT Bearer Tokens
- **Documentação:** Swagger/OpenAPI

## 📋 Pré-requisitos

- .NET 8.0 SDK
- MySQL Server 8.0+
- Visual Studio 2022

## 🚀 Instalação

### 1. Clonar o repositório

```bash
git clone https://github.com/[username]/stock-api.git
cd stock-api
```

### 2. Configurar a Connection String

Editar o ficheiro `appsettings.json`:

```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Server=localhost;Port=3306;Database=stockdb;User=root;Password=SUA_PASSWORD;"
  }
}
```

### 3. Criar a Base de Dados com Entity Framework

Abrir o **Package Manager Console** no Visual Studio (Tools → NuGet Package Manager → Package Manager Console) e executar:

```powershell
# Criar a primeira migration
Add-Migration InitialCreate

# Aplicar a migration e criar a BD
Update-Database
```

**Nota:** A base de dados `stockdb` será criada automaticamente no MySQL com todas as tabelas e dados de teste.

### 4. Executar a API

Pressionar **F5** no Visual Studio ou:

```bash
dotnet run
```

A API estará disponível em:
- **HTTP:** http://localhost:5000
- **Swagger UI:** http://localhost:5000

## 📖 Comandos Entity Framework (Package Manager Console)

| Comando | Descrição |
|---------|-----------|
| `Add-Migration NomeMigration` | Criar nova migration |
| `Update-Database` | Aplicar migrations pendentes |
| `Remove-Migration` | Remover última migration não aplicada |
| `Get-Migration` | Listar todas as migrations |
| `Script-Migration` | Gerar script SQL das migrations |

### Exemplos de uso:

```powershell
# Adicionar um novo campo ao modelo e criar migration
Add-Migration AddCampoProduto

# Reverter para uma migration específica
Update-Database -Migration NomeMigration

# Gerar script SQL para produção
Script-Migration -Output "script.sql"
```

## 📖 Documentação da API

### Autenticação

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/register` | Registar utilizador | ❌ |
| POST | `/api/auth/login` | Autenticar | ❌ |
| POST | `/api/auth/verify` | Verificar código | ❌ |
| POST | `/api/auth/resend-code` | Reenviar código | ❌ |
| GET | `/api/auth/profile` | Obter perfil | ✅ |

### Produtos

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/produtos` | Listar produtos | ❌ |
| GET | `/api/produtos/{id}` | Obter por ID | ❌ |
| GET | `/api/produtos/barcode/{codigo}` | Obter por código de barras | ❌ |
| POST | `/api/produtos` | Criar produto | ✅ |
| PUT | `/api/produtos/{id}` | Atualizar produto | ✅ |
| DELETE | `/api/produtos/{id}` | Eliminar produto | ✅ |
| GET | `/api/produtos/categorias` | Listar categorias | ❌ |

### Stock

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/stock/entrada` | Registar entrada | ✅ |
| POST | `/api/stock/saida` | Registar saída | ✅ |
| GET | `/api/stock/historico/{produtoId}` | Histórico do produto | ❌ |
| GET | `/api/stock/historico` | Histórico geral | ❌ |

## 🔑 Exemplos de Uso

### Registo

```bash
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@example.pt",
    "password": "123456",
    "nome": "Utilizador Teste"
  }'
```

### Login

```bash
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@stockapi.pt",
    "password": "admin123"
  }'
```

### Consultar Produto por Código de Barras

```bash
curl http://localhost:5000/api/produtos/barcode/5601234567890
```

### Entrada de Stock (requer token)

```bash
curl -X POST http://localhost:5000/api/stock/entrada \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -d '{
    "codigoBarras": "5601234567890",
    "quantidade": 100,
    "observacoes": "Reposição de stock"
  }'
```

### Saída de Stock (requer token)

```bash
curl -X POST http://localhost:5000/api/stock/saida \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -d '{
    "codigoBarras": "5601234567890",
    "quantidade": 10,
    "observacoes": "Venda ao cliente X"
  }'
```

## 👤 Credenciais de Teste

| Email | Password | Verificado |
|-------|----------|------------|
| admin@stockapi.pt | admin123 | ✅ |

## 📁 Estrutura do Projeto

```
StockAPI/
├── Controllers/          # Controladores da API
│   ├── AuthController.cs
│   ├── ProdutosController.cs
│   └── StockController.cs
├── Data/
│   └── AppDbContext.cs   # Contexto EF Core
├── DTOs/                 # Data Transfer Objects
│   ├── AuthDtos.cs
│   └── ProdutoDtos.cs
├── Models/               # Entidades
│   ├── User.cs
│   ├── Produto.cs
│   └── HistoricoStock.cs
├── Services/             # Lógica de negócio
│   ├── AuthService.cs
│   └── ProdutoService.cs
├── Program.cs            # Configuração da app
├── appsettings.json      # Configurações
└── StockAPI.csproj       # Dependências
```

## 🏭 Deploy no Servidor

### Docker (recomendado)

```dockerfile
# Dockerfile exemplo
FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app
COPY publish/ .
ENTRYPOINT ["dotnet", "StockAPI.dll"]
```

### Publicar

```bash
dotnet publish -c Release -o publish
```

## 📝 Licença

Projeto académico - IPT 2025/26

## 👥 Autores

- [Nome do Aluno 1]
- [Nome do Aluno 2]

---

**Eng. Informática - Politécnico de Tomar**  
**Desenvolvimento de Aplicações Móveis - 2025/26**
