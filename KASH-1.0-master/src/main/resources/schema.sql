-- Eliminar la foreign key si existe
SET @fk_exists = (
    SELECT COUNT(1) 
    FROM information_schema.TABLE_CONSTRAINTS 
    WHERE CONSTRAINT_SCHEMA = 'kash' 
    AND TABLE_NAME = 'componente' 
    AND CONSTRAINT_NAME = 'fk_programa'
);

SET @drop_fk_stmt = IF(@fk_exists > 0, 
    'ALTER TABLE componente DROP FOREIGN KEY fk_programa', 
    'SELECT "Foreign key does not exist"');

PREPARE stmt FROM @drop_fk_stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Eliminar la columna IDprograma si existe
SET @column_exists = (
    SELECT COUNT(1) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'kash' 
    AND TABLE_NAME = 'componente' 
    AND COLUMN_NAME = 'IDprograma'
);

SET @drop_column_stmt = IF(@column_exists > 0, 
    'ALTER TABLE componente DROP COLUMN IDprograma', 
    'SELECT "Column does not exist"');

PREPARE stmt FROM @drop_column_stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Crear tabla componente_programa si no existe
CREATE TABLE IF NOT EXISTS componente_programa (
    componente_id BIGINT NOT NULL,
    programa_id BIGINT NOT NULL,
    PRIMARY KEY (componente_id, programa_id),
    FOREIGN KEY (componente_id) REFERENCES componente(id),
    FOREIGN KEY (programa_id) REFERENCES programa(id)
); 