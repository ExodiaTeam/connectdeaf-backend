CREATE TABLE IF NOT EXISTS TB_SERVICE (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    professional_id UUID NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    FOREIGN KEY (professional_id) REFERENCES TB_PROFESSIONAL(id) ON DELETE CASCADE
);