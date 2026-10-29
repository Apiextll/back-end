-- ==============================================================================
-- V3: Adicionar coluna materia em tb_partidas e remover tb_partida_materias
-- ==============================================================================

ALTER TABLE tb_partidas ADD COLUMN IF NOT EXISTS materia VARCHAR(50);

DROP TABLE IF EXISTS tb_partida_materias;
