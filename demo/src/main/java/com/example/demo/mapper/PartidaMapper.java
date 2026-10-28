package com.example.demo.mapper;

import com.example.demo.config.RegrasJogoConfig;
import com.example.demo.dto.request.CriarSalaRequest;
import com.example.demo.dto.response.FeedbackRespostaResponse;
import com.example.demo.dto.response.PartidaStatusResponse;
import com.example.demo.dto.response.QuestaoResponse;
import com.example.demo.entity.Jogador;
import com.example.demo.entity.Partida;
import com.example.demo.entity.Questao;
import com.example.demo.entity.ResultadoPartida;
import com.example.demo.enums.Materia;
import com.example.demo.enums.ModoJogo;
import com.example.demo.enums.StatusPartida;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PartidaMapper {

    private final JogadorMapper jogadorMapper;
    private final QuestaoMapper questaoMapper;

    public Partida toEntity(String codigo, CriarSalaRequest request, List<Questao> questoesJ1, List<Questao> questoesJ2) {
        ModoJogo modo = request.modoJogo() != null ? request.modoJogo() : ModoJogo.DUPLO_PC_REDE;
        Partida partida = new Partida(codigo, request.senha(), modo, request.materia());
        partida.setDuracaoTotalSegundos(RegrasJogoConfig.DURACAO_TOTAL_PARTIDA_SEGUNDOS);
        partida.setTempoPorQuestaoSegundos(RegrasJogoConfig.TEMPO_LIMITE_QUESTAO_SEGUNDOS);
        partida.setQuestoesJogador1(questoesJ1);
        partida.setQuestoesJogador2(questoesJ2);

        // Se for Modo 1 PC e os nomes dos jogadores foram informados na criação
        if (modo == ModoJogo.TURNO_LOCAL_1PC && request.nomeJogador1() != null && request.nomeJogador2() != null) {
            Jogador j1 = jogadorMapper.toEntity(UUID.randomUUID().toString(), request.nomeJogador1());
            Jogador j2 = jogadorMapper.toEntity(UUID.randomUUID().toString(), request.nomeJogador2());
            j1.setPronto(true);
            j2.setPronto(true);
            partida.adicionarJogador(j1);
            partida.adicionarJogador(j2);
            partida.setTurnoAtualJogadorId(j1.getId());
            partida.setStatus(StatusPartida.CONTAGEM_REGRESSIVA);
            partida.setInicioContagem(Instant.now());
        }

        return partida;
    }

    public PartidaStatusResponse toStatusResponse(Partida partida, Questao questaoAtual, ResultadoPartida resultado) {
        QuestaoResponse questaoResponse = questaoMapper.toResponse(questaoAtual);

        String nomeDaVez = null;
        if (partida.getJogadorDaVez() != null) {
            nomeDaVez = partida.getJogadorDaVez().getNome();
        }

        return new PartidaStatusResponse(
                partida.getCodigo(),
                partida.getModoJogo(),
                partida.getStatus(),
                partida.getMateria() != null ? partida.getMateria() : Materia.GERAL,
                partida.getTurnoAtualJogadorId(),
                nomeDaVez,
                partida.getTempoRestanteTurnoSegundos(),
                partida.getTempoPorQuestaoSegundos(),
                jogadorMapper.toResumoResponse(partida.getJogador1()),
                jogadorMapper.toResumoResponse(partida.getJogador2()),
                questaoResponse,
                resultado
        );
    }

    public FeedbackRespostaResponse toFeedbackResponse(
            boolean correta,
            int indiceCorreto,
            int indiceEscolhido,
            int pontosGanhos,
            int pontuacaoTotal,
            Questao proximaQuestao
    ) {
        QuestaoResponse proximaResponse = questaoMapper.toResponse(proximaQuestao);
        return new FeedbackRespostaResponse(
                correta,
                indiceCorreto,
                indiceEscolhido,
                pontosGanhos,
                pontuacaoTotal,
                proximaResponse
        );
    }
}
