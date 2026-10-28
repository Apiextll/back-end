package com.example.demo.service;

import com.example.demo.config.RegrasJogoConfig;
import com.example.demo.dto.request.CriarSalaRequest;
import com.example.demo.dto.request.EntrarSalaRequest;
import com.example.demo.dto.request.SubmeterRespostaRequest;
import com.example.demo.dto.response.FeedbackRespostaResponse;
import com.example.demo.dto.response.JogadorResumoResponse;
import com.example.demo.dto.response.PartidaStatusResponse;
import com.example.demo.entity.Jogador;
import com.example.demo.entity.Partida;
import com.example.demo.entity.Questao;
import com.example.demo.entity.Resposta;
import com.example.demo.entity.ResultadoPartida;
import com.example.demo.enums.ModoJogo;
import com.example.demo.enums.StatusPartida;
import com.example.demo.finder.PartidaFinder;
import com.example.demo.mapper.JogadorMapper;
import com.example.demo.mapper.PartidaMapper;
import com.example.demo.mapper.RespostaMapper;
import com.example.demo.repository.PartidaRepository;
import com.example.demo.repository.RespostaRepository;
import com.example.demo.support.HistoricoQuestoesTorneioSupport;
import com.example.demo.support.PartidaCicloVidaSupport;
import com.example.demo.validator.PartidaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final RespostaRepository respostaRepository;
    private final PartidaFinder partidaFinder;
    private final PartidaValidator partidaValidator;
    private final PartidaMapper partidaMapper;
    private final JogadorMapper jogadorMapper;
    private final RespostaMapper respostaMapper;
    private final QuestaoService questaoService;
    private final CalculadoraPontuacaoService calculadoraPontuacaoService;
    private final PartidaCicloVidaSupport partidaCicloVidaSupport;
    private final HistoricoQuestoesTorneioSupport historicoQuestoesTorneioSupport;

    @Transactional
    public PartidaStatusResponse criarSala(CriarSalaRequest request) {
        String codigo = (request.codigo() != null && !request.codigo().isBlank())
                ? request.codigo().toUpperCase().trim()
                : UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        List<Questao> poolJ1 = questaoService.obterQuestoesAleatorias(
                request.materia(),
                RegrasJogoConfig.POOL_QUESTOES_PARTIDA
        );

        List<Questao> poolJ2 = questaoService.obterQuestoesAleatorias(
                request.materia(),
                RegrasJogoConfig.POOL_QUESTOES_PARTIDA
        );

        Partida partida = partidaMapper.toEntity(codigo, request, poolJ1, poolJ2);
        partidaRepository.save(partida);

        log.info("Sala {} persistida no banco em modo {}. Questões J1: {}, Questões J2: {}",
                codigo, partida.getModoJogo(), poolJ1.size(), poolJ2.size());

        Questao questaoAtual = null;
        if (partida.getStatus() == StatusPartida.CONTAGEM_REGRESSIVA || partida.getStatus() == StatusPartida.EM_ANDAMENTO) {
            questaoAtual = !poolJ1.isEmpty() ? poolJ1.get(0) : null;
        }

        return partidaMapper.toStatusResponse(partida, questaoAtual, null);
    }

    @Transactional
    public JogadorResumoResponse entrarSala(EntrarSalaRequest request) {
        Partida partida = partidaFinder.buscarPorCodigoOuFalhar(request.codigo());

        partidaValidator.validarSenhaAcesso(partida, request.senha());
        partidaValidator.validarDisponibilidadeEntrada(partida);

        String jogadorId = UUID.randomUUID().toString();
        Jogador jogador = jogadorMapper.toEntity(jogadorId, request.nomeJogador());

        boolean adicionado = partida.adicionarJogador(jogador);
        partidaValidator.validarCapacidadeSala(adicionado);

        partidaRepository.save(partida);
        log.info("Jogador '{}' ({}) entrou na sala {}", jogador.getNome(), jogadorId, partida.getCodigo());

        return jogadorMapper.toResumoResponse(jogador);
    }

    @Transactional
    public boolean alternarPronto(String codigo, String jogadorId) {
        Partida partida = partidaFinder.buscarPorCodigoOuFalhar(codigo);
        Jogador jogador = partidaFinder.buscarJogadorNaSalaOuFalhar(partida, jogadorId);

        jogador.setPronto(!jogador.isPronto());
        log.info("Jogador {} na sala {} alterou status 'pronto' para: {}", jogador.getNome(), codigo, jogador.isPronto());

        if (partida.ambosProntos()) {
            partida.setStatus(StatusPartida.CONTAGEM_REGRESSIVA);
            partida.setInicioContagem(Instant.now());
            if (partida.getModoJogo() == ModoJogo.TURNO_LOCAL_1PC) {
                partida.setTurnoAtualJogadorId(partida.getJogador1().getId());
            }
            log.info("Ambos jogadores prontos na sala {}. Disparando contagem de 3 segundos!", codigo);
        }

        partidaRepository.save(partida);
        return jogador.isPronto();
    }

    @Transactional
    public PartidaStatusResponse iniciarSegundoTurno(String codigo) {
        Partida partida = partidaFinder.buscarPorCodigoOuFalhar(codigo);
        partidaValidator.validarPodeIniciarSegundoTurno(partida);

        partidaCicloVidaSupport.iniciarSegundoTurno(partida);

        Questao questaoAtual = null;
        if (!partida.getQuestoesJogador2().isEmpty()) {
            questaoAtual = partida.getQuestoesJogador2().get(0);
        }

        return partidaMapper.toStatusResponse(partida, questaoAtual, null);
    }

    @Transactional
    public PartidaStatusResponse obterStatus(String codigo, String jogadorId) {
        Partida partida = partidaFinder.buscarPorCodigoOuFalhar(codigo);

        partidaCicloVidaSupport.atualizarCicloDeVida(partida);

        String idAlvo = jogadorId;
        if ((idAlvo == null || idAlvo.isBlank()) && partida.getModoJogo() == ModoJogo.TURNO_LOCAL_1PC) {
            idAlvo = partida.getTurnoAtualJogadorId();
        }

        Questao questaoAtual = null;
        if (idAlvo != null && !idAlvo.isBlank() && partida.getStatus() == StatusPartida.EM_ANDAMENTO) {
            Jogador jogador = partida.getJogadorPorId(idAlvo);
            if (jogador != null) {
                List<Questao> pool = partida.getQuestoesDoJogador(idAlvo);
                int idx = jogador.getIndiceQuestaoAtual();
                if (idx < pool.size()) {
                    questaoAtual = pool.get(idx);
                }
            }
        }

        ResultadoPartida resultado = partidaFinder.buscarResultadoPorCodigo(partida.getCodigo()).orElse(null);
        return partidaMapper.toStatusResponse(partida, questaoAtual, resultado);
    }

    @Transactional
    public FeedbackRespostaResponse submeterResposta(SubmeterRespostaRequest request) {
        Partida partida = partidaFinder.buscarPorCodigoOuFalhar(request.codigoSala());

        partidaCicloVidaSupport.atualizarCicloDeVida(partida);
        partidaValidator.validarPartidaEmAndamento(partida);
        partidaValidator.validarTurnoJogador(partida, request.jogadorId());

        Jogador jogador = partidaFinder.buscarJogadorNaSalaOuFalhar(partida, request.jogadorId());
        List<Questao> questoes = partida.getQuestoesDoJogador(jogador.getId());

        int idx = jogador.getIndiceQuestaoAtual();
        partidaValidator.validarQuestaoDisponivel(idx, questoes.size());

        Questao questao = questoes.get(idx);

        // Registra no histórico do torneio (regra de inéditas)
        historicoQuestoesTorneioSupport.registrarResposta(questao.getId());

        boolean acertou = questao.isCorreta(request.indiceEscolhido());
        int qtdMateriasCalculo = partida.isMateriasGerais() ? 10 : 1;

        int pontosGanhos = calculadoraPontuacaoService.calcularPontos(
                acertou,
                questao.getDificuldade(),
                request.tempoGastoSegundos(),
                qtdMateriasCalculo
        );

        Resposta resposta = respostaMapper.toEntity(
                jogador,
                questao.getId(),
                request.indiceEscolhido(),
                request.tempoGastoSegundos(),
                acertou,
                pontosGanhos
        );

        jogador.registrarResposta(resposta);
        respostaRepository.save(resposta);
        partidaRepository.save(partida);

        log.info("Jogador {} respondeu questão {}: acertou={}, pontosGanhos={}, total={}",
                jogador.getNome(), questao.getId(), acertou, pontosGanhos, jogador.getPontuacaoTotal());

        Questao proximaQuestao = null;
        int proximoIdx = jogador.getIndiceQuestaoAtual();
        if (proximoIdx < questoes.size() && !partida.isTempoTurnoEsgotado()) {
            proximaQuestao = questoes.get(proximoIdx);
        }

        return partidaMapper.toFeedbackResponse(
                acertou,
                questao.getIndiceCorreto(),
                request.indiceEscolhido(),
                pontosGanhos,
                jogador.getPontuacaoTotal(),
                proximaQuestao
        );
    }

    @Transactional(readOnly = true)
    public ResultadoPartida obterResultado(String codigo) {
        Partida partida = partidaFinder.buscarPorCodigoOuFalhar(codigo);
        return partidaFinder.buscarResultadoPorCodigoOuFalhar(partida.getCodigo());
    }
}
