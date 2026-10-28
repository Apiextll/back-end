-- ==============================================================================
-- V1: Criação do Esquema Inicial do Competype
-- ==============================================================================

-- 1. Tabela de Questões
CREATE TABLE tb_questoes (
    id BIGSERIAL PRIMARY KEY,
    enunciado TEXT NOT NULL,
    materia VARCHAR(50) NOT NULL,
    dificuldade VARCHAR(30) NOT NULL,
    indice_correto INTEGER NOT NULL
);

-- 2. Tabela de Alternativas da Questão (1:N)
CREATE TABLE tb_questao_alternativas (
    questao_id BIGINT NOT NULL,
    alternativa TEXT NOT NULL,
    ordem INTEGER NOT NULL,
    CONSTRAINT fk_alternativas_questao FOREIGN KEY (questao_id) REFERENCES tb_questoes (id) ON DELETE CASCADE,
    PRIMARY KEY (questao_id, ordem)
);

-- 3. Tabela de Jogadores
CREATE TABLE tb_jogadores (
    id VARCHAR(36) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    pronto BOOLEAN NOT NULL DEFAULT FALSE,
    pontuacao_total INTEGER NOT NULL DEFAULT 0,
    questoes_acertadas INTEGER NOT NULL DEFAULT 0,
    questoes_erradas INTEGER NOT NULL DEFAULT 0,
    indice_questao_atual INTEGER NOT NULL DEFAULT 0
);

-- 4. Tabela de Partidas (Salas)
CREATE TABLE tb_partidas (
    codigo VARCHAR(20) PRIMARY KEY,
    senha VARCHAR(100),
    modo_jogo VARCHAR(30) NOT NULL DEFAULT 'DUPLO_PC_REDE',
    status VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO_JOGADORES',
    jogador1_id VARCHAR(36),
    jogador2_id VARCHAR(36),
    turno_atual_jogador_id VARCHAR(36),
    inicio_contagem TIMESTAMP WITH TIME ZONE,
    inicio_partida TIMESTAMP WITH TIME ZONE,
    inicio_turno_atual TIMESTAMP WITH TIME ZONE,
    fim_partida TIMESTAMP WITH TIME ZONE,
    duracao_total_segundos INTEGER NOT NULL DEFAULT 120,
    tempo_por_questao_segundos INTEGER NOT NULL DEFAULT 15,
    CONSTRAINT fk_partidas_jogador1 FOREIGN KEY (jogador1_id) REFERENCES tb_jogadores (id) ON DELETE SET NULL,
    CONSTRAINT fk_partidas_jogador2 FOREIGN KEY (jogador2_id) REFERENCES tb_jogadores (id) ON DELETE SET NULL
);

-- 5. Tabela de Matérias Selecionadas na Partida
CREATE TABLE tb_partida_materias (
    partida_codigo VARCHAR(20) NOT NULL,
    materia VARCHAR(50) NOT NULL,
    CONSTRAINT fk_partida_materias_partida FOREIGN KEY (partida_codigo) REFERENCES tb_partidas (codigo) ON DELETE CASCADE
);

-- 6. Tabela de Questões sorteadas para o Jogador 1 na Partida
CREATE TABLE tb_partida_questoes_j1 (
    partida_codigo VARCHAR(20) NOT NULL,
    questao_id BIGINT NOT NULL,
    ordem INTEGER NOT NULL,
    CONSTRAINT fk_partida_q_j1_partida FOREIGN KEY (partida_codigo) REFERENCES tb_partidas (codigo) ON DELETE CASCADE,
    CONSTRAINT fk_partida_q_j1_questao FOREIGN KEY (questao_id) REFERENCES tb_questoes (id) ON DELETE CASCADE,
    PRIMARY KEY (partida_codigo, ordem)
);

-- 7. Tabela de Questões sorteadas para o Jogador 2 na Partida
CREATE TABLE tb_partida_questoes_j2 (
    partida_codigo VARCHAR(20) NOT NULL,
    questao_id BIGINT NOT NULL,
    ordem INTEGER NOT NULL,
    CONSTRAINT fk_partida_q_j2_partida FOREIGN KEY (partida_codigo) REFERENCES tb_partidas (codigo) ON DELETE CASCADE,
    CONSTRAINT fk_partida_q_j2_questao FOREIGN KEY (questao_id) REFERENCES tb_questoes (id) ON DELETE CASCADE,
    PRIMARY KEY (partida_codigo, ordem)
);

-- 8. Tabela de Histórico de Respostas dos Jogadores
CREATE TABLE tb_respostas (
    id BIGSERIAL PRIMARY KEY,
    jogador_id VARCHAR(36) NOT NULL,
    questao_id BIGINT NOT NULL,
    indice_escolhido INTEGER NOT NULL,
    tempo_gasto_segundos DOUBLE PRECISION NOT NULL,
    correta BOOLEAN NOT NULL,
    pontos_obtidos INTEGER NOT NULL,
    instante_resposta TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_respostas_jogador FOREIGN KEY (jogador_id) REFERENCES tb_jogadores (id) ON DELETE CASCADE,
    CONSTRAINT fk_respostas_questao FOREIGN KEY (questao_id) REFERENCES tb_questoes (id) ON DELETE CASCADE
);

-- 9. Tabela de Resultados Finais das Partidas
CREATE TABLE tb_resultados_partida (
    id BIGSERIAL PRIMARY KEY,
    codigo_partida VARCHAR(20) NOT NULL UNIQUE,
    j1_jogador_id VARCHAR(36),
    j1_nome VARCHAR(100),
    j1_pontuacao_final INTEGER,
    j1_total_respondidas INTEGER,
    j1_total_acertos INTEGER,
    j1_total_erros INTEGER,
    j1_taxa_acerto DOUBLE PRECISION,
    j1_tempo_medio DOUBLE PRECISION,
    j2_jogador_id VARCHAR(36),
    j2_nome VARCHAR(100),
    j2_pontuacao_final INTEGER,
    j2_total_respondidas INTEGER,
    j2_total_acertos INTEGER,
    j2_total_erros INTEGER,
    j2_taxa_acerto DOUBLE PRECISION,
    j2_tempo_medio DOUBLE PRECISION,
    vencedor_id VARCHAR(36),
    nome_vencedor VARCHAR(100),
    motivo_vitoria TEXT,
    finalizada_em TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 10. Tabela de Histórico de Questões Consumidas no Torneio (Regra de Inéditas)
CREATE TABLE tb_questoes_consumidas_torneio (
    questao_id BIGINT PRIMARY KEY,
    consumida_em TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_questoes_consumidas_questao FOREIGN KEY (questao_id) REFERENCES tb_questoes (id) ON DELETE CASCADE
);

-- Índices para Otimização de Consultas
CREATE INDEX idx_tb_questoes_materia ON tb_questoes (materia);
CREATE INDEX idx_tb_respostas_jogador_id ON tb_respostas (jogador_id);
CREATE INDEX idx_tb_partidas_status ON tb_partidas (status);
