package com.example.demo.service;

import com.example.demo.config.RegrasJogoConfig;
import com.example.demo.enums.Dificuldade;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraPontuacaoService {

    public int calcularPontos(boolean acertou, Dificuldade dificuldade, double tempoGastoSegundos, int quantidadeMaterias) {
        if (!acertou) {
            return 0;
        }

        int pontosBase = switch (dificuldade) {
            case FACIL -> RegrasJogoConfig.PONTOS_BASE_FACIL;
            case MEDIO -> RegrasJogoConfig.PONTOS_BASE_MEDIO;
            case DIFICIL -> RegrasJogoConfig.PONTOS_BASE_DIFICIL;
        };

        double tempoRestante = Math.max(0.0, RegrasJogoConfig.TEMPO_LIMITE_QUESTAO_SEGUNDOS - tempoGastoSegundos);
        int bonusVelocidade = (int) Math.round(tempoRestante * RegrasJogoConfig.MULTIPLICADOR_SEGUNDO_RESTANTE);

        int subtotal = pontosBase + bonusVelocidade;
        double multiplicador = RegrasJogoConfig.calcularMultiplicadorMaterias(quantidadeMaterias);

        return (int) Math.round(subtotal * multiplicador);
    }
}
