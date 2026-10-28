package com.example.demo.dto.response;

public record FeedbackRespostaResponse(
        boolean correta,
        int indiceCorreto,
        int indiceEscolhido,
        int pontosGanhos,
        int pontuacaoTotal,
        QuestaoResponse proximaQuestao
) {
}
