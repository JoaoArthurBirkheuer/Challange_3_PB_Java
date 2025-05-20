-- Usuário Admin
INSERT INTO usuarios 
(id, nome, email, senha, deleted, created_at, updated_at, version) 
VALUES 
(1, 'Admin', 'admin@ecommerce.com', '$2a$10$CLxYXmjz6AvR5NYjVDWzK.B0mK9ydMyFVWqQgGfTqQlMB3PRhPCeS', false, NOW(), NOW(), 0);

-- Role do Admin
INSERT INTO usuario_roles (usuario_id, role) 
VALUES (1, 'ROLE_ADMIN');
