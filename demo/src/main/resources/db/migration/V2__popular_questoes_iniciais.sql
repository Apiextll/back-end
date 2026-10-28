-- ==============================================================================
-- V2: Carga Inicial de Questões e Alternativas do Competype
-- ==============================================================================

-- 1. Matemática
INSERT INTO tb_questoes (id, enunciado, materia, dificuldade, indice_correto) VALUES
(1, 'Qual é o valor de x na equação 2x + 6 = 16?', 'MATEMATICA', 'FACIL', 1),
(2, 'Se a área de um quadrado é 64 cm², qual é o seu perímetro?', 'MATEMATICA', 'FACIL', 2),
(3, 'Qual é o vértice da parábola definida pela função f(x) = x² - 4x + 3?', 'MATEMATICA', 'MEDIO', 0),
(4, 'Qual é a probabilidade de tirar um número primo ao lançar um dado comum de 6 faces?', 'MATEMATICA', 'MEDIO', 2),
(5, 'Em uma progressão geométrica onde a₁ = 3 e a razão q = 2, qual é o 6º termo?', 'MATEMATICA', 'DIFICIL', 1);

INSERT INTO tb_questao_alternativas (questao_id, alternativa, ordem) VALUES
(1, '3', 0), (1, '5', 1), (1, '8', 2), (1, '10', 3),
(2, '16 cm', 0), (2, '24 cm', 1), (2, '32 cm', 2), (2, '64 cm', 3),
(3, '(2, -1)', 0), (3, '(-2, 1)', 1), (3, '(2, 3)', 2), (3, '(0, 3)', 3),
(4, '1/6', 0), (4, '1/3', 1), (4, '1/2', 2), (4, '2/3', 3),
(5, '48', 0), (5, '96', 1), (5, '192', 2), (5, '384', 3);

-- 2. Português
INSERT INTO tb_questoes (id, enunciado, materia, dificuldade, indice_correto) VALUES
(6, 'Qual figura de linguagem está presente em: ''O vento sussurrava nas folhas''?', 'PORTUGUES', 'FACIL', 1),
(7, 'Assinale a alternativa em que o uso da crase é OBRIGATÓRIO:', 'PORTUGUES', 'MEDIO', 1),
(8, 'Na oração ''Embora estivesse cansado, estudou até tarde'', a conjunção expressa ideia de:', 'PORTUGUES', 'MEDIO', 2);

INSERT INTO tb_questao_alternativas (questao_id, alternativa, ordem) VALUES
(6, 'Metáfora', 0), (6, 'Prosopopeia (Personificação)', 1), (6, 'Hipérbole', 2), (6, 'Eufemismo', 3),
(7, 'Fui a pé ao mercado', 0), (7, 'Entregou o livro à professora', 1), (7, 'Começou a chorar', 2), (7, 'Refiro-me a você', 3),
(8, 'Causa', 0), (8, 'Consequência', 1), (8, 'Concessão', 2), (8, 'Condição', 3);

-- 3. Física
INSERT INTO tb_questoes (id, enunciado, materia, dificuldade, indice_correto) VALUES
(9, 'Qual é a unidade de medida da força no Sistema Internacional (SI)?', 'FISICA', 'FACIL', 2),
(10, 'Um carro viaja a uma velocidade constante de 72 km/h. Qual é a sua velocidade em m/s?', 'FISICA', 'MEDIO', 1);

INSERT INTO tb_questao_alternativas (questao_id, alternativa, ordem) VALUES
(9, 'Joule', 0), (9, 'Pascal', 1), (9, 'Newton', 2), (9, 'Watt', 3),
(10, '15 m/s', 0), (10, '20 m/s', 1), (10, '25 m/s', 2), (10, '30 m/s', 3);

-- 4. Biologia
INSERT INTO tb_questoes (id, enunciado, materia, dificuldade, indice_correto) VALUES
(11, 'Qual organela celular é responsável pela produção de energia (respiração celular)?', 'BIOLOGIA', 'FACIL', 1),
(12, 'A fotossíntese nas plantas ocorre principalmente em qual estrutura celular?', 'BIOLOGIA', 'FACIL', 0);

INSERT INTO tb_questao_alternativas (questao_id, alternativa, ordem) VALUES
(11, 'Complexo de Golgi', 0), (11, 'Mitocôndria', 1), (11, 'Ribossomo', 2), (11, 'Lisossomo', 3),
(12, 'Cloroplasto', 0), (12, 'Vacúolo', 1), (12, 'Parede celular', 2), (12, 'Retículo Endoplasmático', 3);

-- 5. Química
INSERT INTO tb_questoes (id, enunciado, materia, dificuldade, indice_correto) VALUES
(13, 'Qual elemento químico possui o símbolo ''Na'' na tabela periódica?', 'QUIMICA', 'FACIL', 2),
(14, 'Qual é o pH aproximado de uma substância considerada neutra a 25°C?', 'QUIMICA', 'FACIL', 2);

INSERT INTO tb_questao_alternativas (questao_id, alternativa, ordem) VALUES
(13, 'Nitrogênio', 0), (13, 'Níquel', 1), (13, 'Sódio', 2), (13, 'Neônio', 3),
(14, '0', 0), (14, '5', 1), (14, '7', 2), (14, '14', 3);

-- 6. História
INSERT INTO tb_questoes (id, enunciado, materia, dificuldade, indice_correto) VALUES
(15, 'Em que ano ocorreu a Proclamação da República no Brasil?', 'HISTORIA', 'FACIL', 2),
(16, 'Qual evento histórico marcou o início da Idade Contemporânea em 1789?', 'HISTORIA', 'MEDIO', 0);

INSERT INTO tb_questao_alternativas (questao_id, alternativa, ordem) VALUES
(15, '1822', 0), (15, '1888', 1), (15, '1889', 2), (15, '1930', 3),
(16, 'Revolução Francesa', 0), (16, 'Revolução Industrial', 1), (16, 'Primeira Guerra Mundial', 2), (16, 'Queda de Constantinopla', 3);

-- 7. Geografia
INSERT INTO tb_questoes (id, enunciado, materia, dificuldade, indice_correto) VALUES
(17, 'Qual é o maior bioma brasileiro em extensão territorial?', 'GEOGRAFIA', 'FACIL', 2),
(18, 'Qual camada da atmosfera é responsável por filtrar grande parte dos raios ultravioleta (UV)?', 'GEOGRAFIA', 'MEDIO', 1);

INSERT INTO tb_questao_alternativas (questao_id, alternativa, ordem) VALUES
(17, 'Cerrado', 0), (17, 'Mata Atlântica', 1), (17, 'Amazônia', 2), (17, 'Caatinga', 3),
(18, 'Troposfera', 0), (18, 'Camada de Ozônio (Estratofosfera)', 1), (18, 'Mesosfera', 2), (18, 'Termosfera', 3);

-- 8. Filosofia & Sociologia
INSERT INTO tb_questoes (id, enunciado, materia, dificuldade, indice_correto) VALUES
(19, 'A famosa frase ''Só sei que nada sei'' é atribuída a qual filósofo da Grécia Antiga?', 'FILOSOFIA', 'FACIL', 2),
(20, 'O conceito de ''Fato Social'' é o pilar fundamental do método sociológico de qual autor?', 'SOCIOLOGIA', 'MEDIO', 1);

INSERT INTO tb_questao_alternativas (questao_id, alternativa, ordem) VALUES
(19, 'Platão', 0), (19, 'Aristóteles', 1), (19, 'Sócrates', 2), (19, 'Pitágoras', 3),
(20, 'Karl Marx', 0), (20, 'Émile Durkheim', 1), (20, 'Max Weber', 2), (20, 'Auguste Comte', 3);

-- Ajuste na sequence do PostgreSQL para que novos inserts comecem após o 20
SELECT setval('tb_questoes_id_seq', (SELECT MAX(id) FROM tb_questoes));
