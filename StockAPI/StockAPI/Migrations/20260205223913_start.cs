using System;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace StockAPI.Migrations
{
    /// <inheritdoc />
    public partial class start : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AlterDatabase()
                .Annotation("MySql:CharSet", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "produtos",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySql:ValueGenerationStrategy", MySqlValueGenerationStrategy.IdentityColumn),
                    codigo_barras = table.Column<string>(type: "varchar(50)", maxLength: 50, nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    nome = table.Column<string>(type: "varchar(200)", maxLength: 200, nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    descricao = table.Column<string>(type: "varchar(1000)", maxLength: 1000, nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    imagem_url = table.Column<string>(type: "varchar(500)", maxLength: 500, nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    stock = table.Column<int>(type: "int", nullable: false),
                    stock_minimo = table.Column<int>(type: "int", nullable: false),
                    preco_unitario = table.Column<decimal>(type: "decimal(10,2)", precision: 10, scale: 2, nullable: false),
                    categoria = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    localizacao = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    ativo = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    created_at = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    updated_at = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_produtos", x => x.id);
                })
                .Annotation("MySql:CharSet", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "users",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySql:ValueGenerationStrategy", MySqlValueGenerationStrategy.IdentityColumn),
                    email = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    password_hash = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    nome = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    is_verified = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    verification_code = table.Column<string>(type: "varchar(6)", maxLength: 6, nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    verification_code_expiry = table.Column<DateTime>(type: "datetime(6)", nullable: true),
                    created_at = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    updated_at = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_users", x => x.id);
                })
                .Annotation("MySql:CharSet", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "historico_stock",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySql:ValueGenerationStrategy", MySqlValueGenerationStrategy.IdentityColumn),
                    produto_id = table.Column<int>(type: "int", nullable: false),
                    user_id = table.Column<int>(type: "int", nullable: false),
                    quantidade = table.Column<int>(type: "int", nullable: false),
                    tipo_movimento = table.Column<int>(type: "int", nullable: false),
                    stock_anterior = table.Column<int>(type: "int", nullable: false),
                    stock_atual = table.Column<int>(type: "int", nullable: false),
                    observacoes = table.Column<string>(type: "varchar(500)", maxLength: 500, nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    data_movimento = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_historico_stock", x => x.id);
                    table.ForeignKey(
                        name: "FK_historico_stock_produtos_produto_id",
                        column: x => x.produto_id,
                        principalTable: "produtos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_historico_stock_users_user_id",
                        column: x => x.user_id,
                        principalTable: "users",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySql:CharSet", "utf8mb4");

            migrationBuilder.InsertData(
                table: "produtos",
                columns: new[] { "id", "ativo", "categoria", "codigo_barras", "created_at", "descricao", "imagem_url", "localizacao", "nome", "preco_unitario", "stock", "stock_minimo", "updated_at" },
                values: new object[,]
                {
                    { 1, true, "Fixação", "5601234567890", new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc), "Parafuso de aço zincado M8x50mm", null, "Corredor A - Prateleira 1", "Parafuso M8x50", 0.15m, 500, 100, new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc) },
                    { 2, true, "Fixação", "5609876543210", new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc), "Porca sextavada M8 em aço zincado", null, "Corredor A - Prateleira 1", "Porca M8", 0.08m, 450, 100, new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc) },
                    { 3, true, "Ferramentas", "5605555555555", new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc), "Chave de fendas Phillips tamanho PH2, cabo ergonómico", null, "Corredor B - Prateleira 3", "Chave de Fendas Phillips PH2", 4.99m, 25, 10, new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc) },
                    { 4, true, "Elétrico", "5601111111111", new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc), "Fita isoladora elétrica 19mm x 20m", null, "Corredor C - Prateleira 2", "Fita Isoladora Preta", 1.50m, 80, 20, new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc) },
                    { 5, true, "Elétrico", "5602222222222", new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc), "Cabo elétrico H07V-U 2.5mm² azul (por metro)", null, "Corredor C - Prateleira 1", "Cabo Elétrico 2.5mm²", 0.85m, 5, 50, new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc) }
                });

            migrationBuilder.InsertData(
                table: "users",
                columns: new[] { "id", "created_at", "email", "is_verified", "nome", "password_hash", "updated_at", "verification_code", "verification_code_expiry" },
                values: new object[] { 1, new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc), "admin@stockapi.pt", true, "Administrador", "$2a$11$K5HHjCKMk5F0z5e5o4KVPq.RjS5YLqJqKHFQK7qKPZ5K5K5K5K5K5", new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc), null, null });

            migrationBuilder.CreateIndex(
                name: "IX_historico_stock_data_movimento",
                table: "historico_stock",
                column: "data_movimento");

            migrationBuilder.CreateIndex(
                name: "IX_historico_stock_produto_id",
                table: "historico_stock",
                column: "produto_id");

            migrationBuilder.CreateIndex(
                name: "IX_historico_stock_user_id",
                table: "historico_stock",
                column: "user_id");

            migrationBuilder.CreateIndex(
                name: "IX_produtos_codigo_barras",
                table: "produtos",
                column: "codigo_barras",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_users_email",
                table: "users",
                column: "email",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "historico_stock");

            migrationBuilder.DropTable(
                name: "produtos");

            migrationBuilder.DropTable(
                name: "users");
        }
    }
}
