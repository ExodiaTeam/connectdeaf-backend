CREATE TABLE IF NOT EXISTS tb_address (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    cep VARCHAR(20) NOT NULL,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(10) NOT NULL,
    complement VARCHAR(255),
    neighborhood VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    client_id UUID,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
    );
