CREATE TABLE IF NOT EXISTS TB_PROFESSIONAL (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    qualification VARCHAR(255) NOT NULL,
    area_of_expertise VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES TB_USER(id) ON DELETE CASCADE
);