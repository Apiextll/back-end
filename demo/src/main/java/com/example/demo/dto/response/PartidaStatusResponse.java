package com.example.demo.dto.response;

import com.example.demo.entity.ResultadoPartida;
import com.example.demo.enums.Materia;
import com.example.demo.enums.ModoJogo;
import com.example.demo.enums.StatusPartida;

public record PartidaStatusResponse(
        String codigo,
        ModoJogo modoJogo,
        StatusPartida status,
        Materia materia,
        String turnoAtualJogadorId,
        String nomeJogadorDaVez,
        int tempoRestanteTurnoSegundos,
        int tempoPorQuestaoSegundos,
        JogadorResumoResponse jogador1,
        JogadorResumoResponse jogador2,
        QuestaoResponse questaoAtual,
        ResultadoPartida resultado
) {
}
