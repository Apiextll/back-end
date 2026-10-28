package com.example.demo;

import com.example.demo.dto.request.CadastrarQuestaoRequest;
import com.example.demo.dto.request.CriarSalaRequest;
import com.example.demo.dto.request.EntrarSalaRequest;
import com.example.demo.dto.request.SubmeterRespostaRequest;
import com.example.demo.dto.response.FeedbackRespostaResponse;
import com.example.demo.dto.response.JogadorResumoResponse;
import com.example.demo.dto.response.PartidaStatusResponse;
import com.example.demo.entity.Partida;
import com.example.demo.entity.Questao;
import com.example.demo.entity.ResultadoPartida;
import com.example.demo.enums.Dificuldade;
import com.example.demo.enums.Materia;
import com.example.demo.enums.ModoJogo;
import com.example.demo.enums.StatusPartida;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.PartidaRepository;
import com.example.demo.repository.QuestaoRepository;
import com.example.demo.service.PartidaService;
import com.example.demo.service.QuestaoService;
import com.example.demo.support.PartidaCicloVidaSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PartidaFluxoCompletoIntegrationTest {

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private QuestaoService questaoService;

    @Autowired
    private QuestaoRepository questaoRepository;

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private PartidaCicloVidaSupport partidaCicloVidaSupport;

    @BeforeEach
    void setupBancoQuestoes() {
        if (questaoRepository.count() == 0) {
            questaoRepository.save(Questao.builder()
                    .enunciado("Quanto é 2 + 2?")
                    .materia(Materia.MATEMATICA)
                    .dificuldade(Dificuldade.FACIL)
                    .alternativas(List.of("1", "2", "3", "4"))
                    .indiceCorreto(3)
                    .build());

            questaoRepository.save(Questao.builder()
                    .enunciado("Qual é a capital do Brasil?")
                    .materia(Materia.GEOGRAFIA)
                    .dificuldade(Dificuldade.FACIL)
                    .alternativas(List.of("São Paulo", "Brasília", "Rio de Janeiro", "Salvador"))
                    .indiceCorreto(1)
                    .build());
        }
    }

    @Test
    @DisplayName("Fluxo completo modo DUPLO_PC_REDE: criação, senha, entrada, prontos, respostas e resultado")
    void testeFluxoCompletoDuploPcRede() {
        // 1. Criar sala com senha
        CriarSalaRequest criarRequest = new CriarSalaRequest("REDE99", "secreta123", Materia.MATEMATICA);
        PartidaStatusResponse salaCriada = partidaService.criarSala(criarRequest);

        assertEquals("REDE99", salaCriada.codigo());
        assertEquals(StatusPartida.AGUARDANDO_JOGADORES, salaCriada.status());
        assertNull(salaCriada.jogador1());
        assertNull(salaCriada.jogador2());

        // 2. Validação de senha incorreta
        assertThrows(UnauthorizedException.class, () ->
                partidaService.entrarSala(new EntrarSalaRequest("REDE99", "senha_errada", "Carlos"))
        );

        // 3. Jogador 1 entra na sala
        JogadorResumoResponse j1 = partidaService.entrarSala(new EntrarSalaRequest("REDE99", "secreta123", "Jogador Alfa"));
        assertNotNull(j1.id());
        assertEquals("Jogador Alfa", j1.nome());
        assertFalse(j1.pronto());

        // 4. Jogador 2 entra na sala
        JogadorResumoResponse j2 = partidaService.entrarSala(new EntrarSalaRequest("REDE99", "secreta123", "Jogador Beta"));
        assertNotNull(j2.id());
        assertEquals("Jogador Beta", j2.nome());
        assertFalse(j2.pronto());

        // 5. Tentativa do 3º jogador entrar na sala cheia
        assertThrows(ConflictException.class, () ->
                partidaService.entrarSala(new EntrarSalaRequest("REDE99", "secreta123", "Intruso"))
        );

        // 6. Jogador 1 marca pronto
        boolean prontoJ1 = partidaService.alternarPronto("REDE99", j1.id());
        assertTrue(prontoJ1);
        PartidaStatusResponse statusAposJ1Pronto = partidaService.obterStatus("REDE99", j1.id());
        assertEquals(StatusPartida.AGUARDANDO_JOGADORES, statusAposJ1Pronto.status());

        // 7. Jogador 2 marca pronto -> Dispara CONTAGEM_REGRESSIVA
        boolean prontoJ2 = partidaService.alternarPronto("REDE99", j2.id());
        assertTrue(prontoJ2);
        PartidaStatusResponse statusAposJ2Pronto = partidaService.obterStatus("REDE99", j2.id());
        // Como o obterStatus chama atualizarCicloDeVida, se não passaram 3s, permanece em CONTAGEM_REGRESSIVA
        assertEquals(StatusPartida.CONTAGEM_REGRESSIVA, statusAposJ2Pronto.status());

        // 8. Tentar responder durante a contagem regressiva deve falhar
        assertThrows(BadRequestException.class, () ->
                partidaService.submeterResposta(new SubmeterRespostaRequest("REDE99", j1.id(), 1L, 3, 2.0))
        );

        // 9. Simular passagem do tempo de contagem regressiva (3 segundos) para entrar em EM_ANDAMENTO
        Partida partida = partidaRepository.findByCodigoIgnoreCase("REDE99").orElseThrow();
        partida.setInicioContagem(Instant.now().minus(4, ChronoUnit.SECONDS));
        partidaRepository.save(partida);

        PartidaStatusResponse statusEmAndamento = partidaService.obterStatus("REDE99", j1.id());
        assertEquals(StatusPartida.EM_ANDAMENTO, statusEmAndamento.status());
        assertNotNull(statusEmAndamento.questaoAtual());

        // 10. Jogador 1 responde questão corretamente em 3.0 segundos
        Long questaoIdJ1 = statusEmAndamento.questaoAtual().id();
        FeedbackRespostaResponse feedbackJ1 = partidaService.submeterResposta(
                new SubmeterRespostaRequest("REDE99", j1.id(), questaoIdJ1, 3, 3.0)
        );
        assertTrue(feedbackJ1.correta());
        assertEquals(3, feedbackJ1.indiceCorreto());
        assertTrue(feedbackJ1.pontosGanhos() > 0, "Deve pontuar na resposta correta");
        assertEquals(feedbackJ1.pontosGanhos(), feedbackJ1.pontuacaoTotal());

        // 11. Jogador 2 responde questão incorretamente
        PartidaStatusResponse statusJ2 = partidaService.obterStatus("REDE99", j2.id());
        Long questaoIdJ2 = statusJ2.questaoAtual().id();
        FeedbackRespostaResponse feedbackJ2 = partidaService.submeterResposta(
                new SubmeterRespostaRequest("REDE99", j2.id(), questaoIdJ2, 0, 5.0)
        );
        assertFalse(feedbackJ2.correta());
        assertEquals(0, feedbackJ2.pontosGanhos(), "Resposta incorreta rende 0 pontos");
        assertEquals(0, feedbackJ2.pontuacaoTotal());

        // 12. Simular término do tempo total da partida (120 segundos)
        partida = partidaRepository.findByCodigoIgnoreCase("REDE99").orElseThrow();
        partida.setInicioPartida(Instant.now().minus(125, ChronoUnit.SECONDS));
        partidaRepository.save(partida);

        // Ao consultar o status, o ciclo de vida detecta o tempo esgotado e finaliza a partida
        PartidaStatusResponse statusFinal = partidaService.obterStatus("REDE99", j1.id());
        assertEquals(StatusPartida.FINALIZADA, statusFinal.status());
        assertNotNull(statusFinal.resultado());

        ResultadoPartida resultado = partidaService.obterResultado("REDE99");
        assertNotNull(resultado);
        assertEquals(j1.id(), resultado.getVencedorId());
        assertEquals("Jogador Alfa", resultado.getNomeVencedor());
        assertTrue(resultado.getMotivoVitoria().contains("Maior pontuação"));
    }

    @Test
    @DisplayName("Fluxo completo modo TURNO_LOCAL_1PC: Samuel vs Murilo com transição de turnos")
    void testeFluxoModoTurnoLocal1Pc() {
        CriarSalaRequest criar = new CriarSalaRequest(
                "HOTSEAT",
                "",
                ModoJogo.TURNO_LOCAL_1PC,
                Materia.GERAL,
                "Samuel",
                "Murilo"
        );

        PartidaStatusResponse sala = partidaService.criarSala(criar);
        assertEquals("HOTSEAT", sala.codigo());
        assertEquals(ModoJogo.TURNO_LOCAL_1PC, sala.modoJogo());
        assertEquals(StatusPartida.CONTAGEM_REGRESSIVA, sala.status());

        String idJ1 = sala.jogador1().id();
        String idJ2 = sala.jogador2().id();

        // Simula passagem da contagem de 3s
        Partida partida = partidaRepository.findByCodigoIgnoreCase("HOTSEAT").orElseThrow();
        partida.setInicioContagem(Instant.now().minus(4, ChronoUnit.SECONDS));
        partidaRepository.save(partida);

        PartidaStatusResponse statusJ1 = partidaService.obterStatus("HOTSEAT", idJ1);
        assertEquals(StatusPartida.EM_ANDAMENTO, statusJ1.status());
        assertEquals(idJ1, statusJ1.turnoAtualJogadorId());
        assertEquals("Samuel", statusJ1.nomeJogadorDaVez());

        // J2 tenta responder na vez de J1 -> deve falhar com BadRequestException
        assertThrows(BadRequestException.class, () ->
                partidaService.submeterResposta(new SubmeterRespostaRequest("HOTSEAT", idJ2, 1L, 3, 2.0))
        );

        // J1 responde
        partidaService.submeterResposta(new SubmeterRespostaRequest("HOTSEAT", idJ1, statusJ1.questaoAtual().id(), 3, 2.0));

        // Simular que o tempo de 120s do turno do Jogador 1 acabou
        partida = partidaRepository.findByCodigoIgnoreCase("HOTSEAT").orElseThrow();
        partida.setInicioTurnoAtual(Instant.now().minus(125, ChronoUnit.SECONDS));
        partidaRepository.save(partida);

        // Consulta status -> deve transicionar para INTERVALO_TRANSICAO
        PartidaStatusResponse statusTransicao = partidaService.obterStatus("HOTSEAT", idJ1);
        assertEquals(StatusPartida.INTERVALO_TRANSICAO, statusTransicao.status());

        // Inicia o 2º turno (vez do Jogador 2)
        PartidaStatusResponse statusSegundoTurno = partidaService.iniciarSegundoTurno("HOTSEAT");
        assertEquals(StatusPartida.CONTAGEM_REGRESSIVA, statusSegundoTurno.status());
        assertEquals(idJ2, statusSegundoTurno.turnoAtualJogadorId());

        // Simula passagem dos 3s para o Jogador 2
        partida = partidaRepository.findByCodigoIgnoreCase("HOTSEAT").orElseThrow();
        partida.setInicioContagem(Instant.now().minus(4, ChronoUnit.SECONDS));
        partidaRepository.save(partida);

        PartidaStatusResponse statusEmAndamentoJ2 = partidaService.obterStatus("HOTSEAT", idJ2);
        assertEquals(StatusPartida.EM_ANDAMENTO, statusEmAndamentoJ2.status());
        assertEquals("Murilo", statusEmAndamentoJ2.nomeJogadorDaVez());

        // Jogador 2 responde
        partidaService.submeterResposta(new SubmeterRespostaRequest("HOTSEAT", idJ2, statusEmAndamentoJ2.questaoAtual().id(), 3, 5.0));

        // Simula término do tempo do Jogador 2 -> partida deve finalizar
        partida = partidaRepository.findByCodigoIgnoreCase("HOTSEAT").orElseThrow();
        partida.setInicioTurnoAtual(Instant.now().minus(125, ChronoUnit.SECONDS));
        partidaRepository.save(partida);

        PartidaStatusResponse finalizada = partidaService.obterStatus("HOTSEAT", idJ2);
        assertEquals(StatusPartida.FINALIZADA, finalizada.status());
        assertNotNull(finalizada.resultado());
    }

    @Test
    @DisplayName("Gestão de questões e reinício do torneio")
    void testeGestaoQuestoesETorneio() {
        // Listar matérias
        var materias = questaoService.listarMaterias();
        assertFalse(materias.isEmpty());

        // Cadastrar nova questão
        CadastrarQuestaoRequest novaQuestao = new CadastrarQuestaoRequest(
                "Qual é a fórmula da água?",
                Materia.QUIMICA,
                Dificuldade.FACIL,
                List.of("H2O", "CO2", "NaCl", "CH4"),
                0
        );
        questaoService.cadastrarQuestao(novaQuestao);

        var todas = questaoService.listarTodas();
        assertTrue(todas.stream().anyMatch(q -> q.enunciado().equals("Qual é a fórmula da água?")));

        // Reiniciar torneio
        questaoService.reiniciarTorneio();
    }
}
