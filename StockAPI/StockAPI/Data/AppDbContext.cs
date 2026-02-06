using Microsoft.EntityFrameworkCore;
using StockAPI.Models;

namespace StockAPI.Data;

/// <summary>
/// Contexto de base de dados da aplicação
/// </summary>
public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
    }

    public DbSet<User> Users { get; set; }
    public DbSet<Produto> Produtos { get; set; }
    public DbSet<HistoricoStock> HistoricoStock { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // ========================================
        // Configuração User
        // ========================================
        modelBuilder.Entity<User>(entity =>
        {
            entity.HasIndex(e => e.Email).IsUnique();
        });

        // ========================================
        // Configuração Produto
        // ========================================
        modelBuilder.Entity<Produto>(entity =>
        {
            entity.HasIndex(e => e.CodigoBarras).IsUnique();
            
            entity.Property(e => e.PrecoUnitario)
                .HasPrecision(10, 2);
        });

        // ========================================
        // Configuração HistoricoStock
        // ========================================
        modelBuilder.Entity<HistoricoStock>(entity =>
        {
            entity.HasIndex(e => e.ProdutoId);
            entity.HasIndex(e => e.UserId);
            entity.HasIndex(e => e.DataMovimento);

            entity.HasOne(e => e.Produto)
                .WithMany(p => p.Historico)
                .HasForeignKey(e => e.ProdutoId)
                .OnDelete(DeleteBehavior.Restrict);

            entity.HasOne(e => e.User)
                .WithMany(u => u.HistoricoMovimentos)
                .HasForeignKey(e => e.UserId)
                .OnDelete(DeleteBehavior.Restrict);
        });

        // ========================================
        // Seed Data - Dados iniciais
        // ========================================
        
        // Utilizador admin (password: admin123)
        modelBuilder.Entity<User>().HasData(new User
        {
            Id = 1,
            Email = "admin@stockapi.pt",
            PasswordHash = "$2a$11$K5HHjCKMk5F0z5e5o4KVPq.RjS5YLqJqKHFQK7qKPZ5K5K5K5K5K5",
            Nome = "Administrador",
            IsVerified = true,
            CreatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc),
            UpdatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc)
        });

        // Produtos de exemplo
        modelBuilder.Entity<Produto>().HasData(
            new Produto
            {
                Id = 1,
                CodigoBarras = "5601234567890",
                Nome = "Parafuso M8x50",
                Descricao = "Parafuso de aço zincado M8x50mm",
                Stock = 500,
                StockMinimo = 100,
                PrecoUnitario = 0.15m,
                Categoria = "Fixação",
                Localizacao = "Corredor A - Prateleira 1",
                Ativo = true,
                CreatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc),
                UpdatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc)
            },
            new Produto
            {
                Id = 2,
                CodigoBarras = "5609876543210",
                Nome = "Porca M8",
                Descricao = "Porca sextavada M8 em aço zincado",
                Stock = 450,
                StockMinimo = 100,
                PrecoUnitario = 0.08m,
                Categoria = "Fixação",
                Localizacao = "Corredor A - Prateleira 1",
                Ativo = true,
                CreatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc),
                UpdatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc)
            },
            new Produto
            {
                Id = 3,
                CodigoBarras = "5605555555555",
                Nome = "Chave de Fendas Phillips PH2",
                Descricao = "Chave de fendas Phillips tamanho PH2, cabo ergonómico",
                Stock = 25,
                StockMinimo = 10,
                PrecoUnitario = 4.99m,
                Categoria = "Ferramentas",
                Localizacao = "Corredor B - Prateleira 3",
                Ativo = true,
                CreatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc),
                UpdatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc)
            },
            new Produto
            {
                Id = 4,
                CodigoBarras = "5601111111111",
                Nome = "Fita Isoladora Preta",
                Descricao = "Fita isoladora elétrica 19mm x 20m",
                Stock = 80,
                StockMinimo = 20,
                PrecoUnitario = 1.50m,
                Categoria = "Elétrico",
                Localizacao = "Corredor C - Prateleira 2",
                Ativo = true,
                CreatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc),
                UpdatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc)
            },
            new Produto
            {
                Id = 5,
                CodigoBarras = "5602222222222",
                Nome = "Cabo Elétrico 2.5mm²",
                Descricao = "Cabo elétrico H07V-U 2.5mm² azul (por metro)",
                Stock = 5,
                StockMinimo = 50,
                PrecoUnitario = 0.85m,
                Categoria = "Elétrico",
                Localizacao = "Corredor C - Prateleira 1",
                Ativo = true,
                CreatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc),
                UpdatedAt = new DateTime(2025, 1, 1, 0, 0, 0, DateTimeKind.Utc)
            }
        );
    }
}
