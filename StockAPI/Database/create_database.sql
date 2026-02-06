-- ========================================
-- Script de Criação da Base de Dados
-- Stock API - Gestão de Armazém
-- Projeto DAM 2025/26
-- ========================================

-- Criar base de dados
CREATE DATABASE IF NOT EXISTS stockdb
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE stockdb;

-- ========================================
-- Tabela: users
-- ========================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verification_code VARCHAR(6) NULL,
    verification_code_expiry DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Tabela: produtos
-- ========================================
CREATE TABLE IF NOT EXISTS produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_barras VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(200) NOT NULL,
    descricao VARCHAR(1000) NULL,
    imagem_url VARCHAR(500) NULL,
    stock INT DEFAULT 0,
    stock_minimo INT DEFAULT 0,
    preco_unitario DECIMAL(10, 2) DEFAULT 0.00,
    categoria VARCHAR(100) NULL,
    localizacao VARCHAR(100) NULL,
    ativo BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_codigo_barras (codigo_barras),
    INDEX idx_categoria (categoria),
    INDEX idx_ativo (ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Tabela: historico_stock
-- ========================================
CREATE TABLE IF NOT EXISTS historico_stock (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    user_id INT NOT NULL,
    quantidade INT NOT NULL,
    tipo_movimento TINYINT NOT NULL COMMENT '1=Entrada, 2=Saída',
    stock_anterior INT NOT NULL,
    stock_atual INT NOT NULL,
    observacoes VARCHAR(500) NULL,
    data_movimento DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_produto_id (produto_id),
    INDEX idx_user_id (user_id),
    INDEX idx_data_movimento (data_movimento),
    
    CONSTRAINT fk_historico_produto 
        FOREIGN KEY (produto_id) REFERENCES produtos(id) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_historico_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Dados de Teste
-- ========================================

-- Utilizador admin (password: admin123)
INSERT INTO users (email, password_hash, nome, is_verified) VALUES
('admin@stockapi.pt', '$2a$11$rBNwK5HHjCKMk5F0z5e5/.4KVPqRjS5YLqJqKHFQK7qKPZ5K5K5K5', 'Administrador', TRUE);

-- Produtos de exemplo
INSERT INTO produtos (codigo_barras, nome, descricao, stock, stock_minimo, preco_unitario, categoria, localizacao) VALUES
('5601234567890', 'Parafuso M8x50', 'Parafuso de aço zincado M8x50mm', 500, 100, 0.15, 'Fixação', 'Corredor A - Prateleira 1'),
('5609876543210', 'Porca M8', 'Porca sextavada M8 em aço zincado', 450, 100, 0.08, 'Fixação', 'Corredor A - Prateleira 1'),
('5605555555555', 'Chave de Fendas Phillips PH2', 'Chave de fendas Phillips tamanho PH2, cabo ergonómico', 25, 10, 4.99, 'Ferramentas', 'Corredor B - Prateleira 3'),
('5601111111111', 'Fita Isoladora Preta', 'Fita isoladora elétrica 19mm x 20m', 80, 20, 1.50, 'Elétrico', 'Corredor C - Prateleira 2'),
('5602222222222', 'Cabo Elétrico 2.5mm²', 'Cabo elétrico H07V-U 2.5mm² azul (por metro)', 5, 50, 0.85, 'Elétrico', 'Corredor C - Prateleira 1');

-- ========================================
-- Verificar criação
-- ========================================
SELECT 'Base de dados criada com sucesso!' AS Status;
SELECT COUNT(*) AS TotalUtilizadores FROM users;
SELECT COUNT(*) AS TotalProdutos FROM produtos;
