INSERT INTO tb_roles (id, name) VALUES (1, 'ROLE_USER')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO tb_roles (id, name) VALUES (2, 'ROLE_PROFESSIONAL')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO tb_roles (id, name) VALUES (3, 'ROLE_ADMIN')
    ON CONFLICT (id) DO NOTHING;
