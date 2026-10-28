package com.example.demo.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RegrasJogoConfig {

    // Regras de Tempo (segundos)
    public static final int DURACAO_TOTAL_PARTIDA_SEGUNDOS = 120; // 2 minutos
    public static final int TEMPO_LIMITE_QUESTAO_SEGUNDOS = 15;   // 15 segundos por questão
    public static final int CONTAGEM_REGRESSIVA_INICIAL_SEGUNDOS = 3; // 3 segundos após ambos "Pronto"
    public static final int DELAY_FEEDBACK_RESPOSTA_SEGUNDOS = 1; // 1 segundo exibindo verde/vermelho

    // Regras de Capacidade
    public static final int MAX_JOGADORES_POR_SALA = 2;
    public static final int POOL_QUESTOES_PARTIDA = 35; // Quantidade de questões preparadas por partida

    // Regras de Pontuação Base por Dificuldade
    public static final int PONTOS_BASE_FACIL = 100;
    public static final int PONTOS_BASE_MEDIO = 200;
    public static final int PONTOS_BASE_DIFICIL = 300;

    // Regras de Bônus por Velocidade (quanto mais rápido acerta, mais pontos ganha)
    public static final int MULTIPLICADOR_SEGUNDO_RESTANTE = 10; // 10 pontos por cada segundo que restava dos 15s

    // Bônus de Matérias: 1.nx (onde n é a quantidade de matérias)
    // Ex: 1 matéria = 1.1x, 5 matérias = 1.5x
    public static double calcularMultiplicadorMaterias(int quantidadeMaterias) {
        if (quantidadeMaterias <= 0) return 1.0;
        return 1.0 + (quantidadeMaterias * 0.1);
    }
}
