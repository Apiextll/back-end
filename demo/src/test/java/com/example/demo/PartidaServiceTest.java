package com.example.demo;

import com.example.demo.dto.request.CriarSalaRequest;
import com.example.demo.dto.request.EntrarSalaRequest;
import com.example.demo.dto.response.JogadorResumoResponse;
import com.example.demo.dto.response.PartidaStatusResponse;
import com.example.demo.entity.Questao;
import com.example.demo.enums.Dificuldade;
import com.example.demo.enums.Materia;
import com.example.demo.enums.ModoJogo;
import com.example.demo.exception.ConflictException;
import com.example.demo.repository.QuestaoRepository;
import com.example.demo.service.CalculadoraPontuacaoService;
import com.example.demo.service.PartidaService;
import com.example.demo.support.HistoricoQuestoesTorneioSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PartidaServiceTest {

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private CalculadoraPontuacaoService calculadoraPontuacaoService;

    @Autowired
    private HistoricoQuestoesTorneioSupport historicoQuestoesTorneioSupport;

    @Autowired
    private QuestaoRepository questaoRepository;

    @BeforeEach
    void setupQuestoes() {
        if (questaoRepository.count() == 0) {
            Questao q1 = Questao.builder()
                    .enunciado("Qual é o valor de x na equação 2x + 6 = 16?")
                    .materia(Materia.MATEMATICA)
                    .dificuldade(Dificuldade.FACIL)
                    .alternativas(List.of("3", "5", "8", "10"))
                    .indiceCorreto(1)
                    .build();
            questaoRepository.save(q1);
        }
    }

    @Test
    @DisplayName("Cálculo de pontuação: resposta errada não pontua")
    void testeRespostaErrada() {
        int pontos = calculadoraPontuacaoService.calcularPontos(false, Dificuldade.MEDIO, 3.0, 1);
        assertEquals(0, pontos, "Resposta errada deve render 0 pontos");
    }

    @Test
    @DisplayName("Cálculo de pontuação: resposta certa ganha base + bônus de velocidade")
    void testeRespostaCertaComVelocidade() {
        int pontos = calculadoraPontuacaoService.calcularPontos(true, Dificuldade.FACIL, 5.0, 1);
        assertEquals(220, pontos);
    }

    @Test
    @DisplayName("Modo 2 PCs: criar, entrar 2 jogadores e rejeitar o 3º com ConflictException")
    void testeLobbyDoisJogadores() {
        CriarSalaRequest criar = new CriarSalaRequest("TESTE1", "123", List.of(Materia.MATEMATICA));
        PartidaStatusResponse partida = partidaService.criarSala(criar);
        assertEquals("TESTE1", partida.codigo());
        assertEquals(ModoJogo.DUPLO_PC_REDE, partida.modoJogo());

        JogadorResumoResponse j1 = partidaService.entrarSala(new EntrarSalaRequest("TESTE1", "123", "Jogador 1"));
        assertNotNull(j1);

        JogadorResumoResponse j2 = partidaService.entrarSala(new EntrarSalaRequest("TESTE1", "123", "Jogador 2"));
        assertNotNull(j2);

        assertThrows(ConflictException.class, () -> {
            partidaService.entrarSala(new EntrarSalaRequest("TESTE1", "123", "Jogador 3"));
        });
    }

    @Test
    @DisplayName("Modo 1 PC (Plano B): criação direta com os 2 jogadores e controle de turnos")
    void testePlanoBModoUmPc() {
        CriarSalaRequest criar = new CriarSalaRequest(
                "HOTSEAT1",
                "",
                ModoJogo.TURNO_LOCAL_1PC,
                List.of(Materia.MATEMATICA),
                "Samuel",
                "Murilo"
        );

        PartidaStatusResponse partida = partidaService.criarSala(criar);
        assertEquals("HOTSEAT1", partida.codigo());
        assertEquals(ModoJogo.TURNO_LOCAL_1PC, partida.modoJogo());
        assertEquals("Samuel", partida.nomeJogadorDaVez());
        assertNotNull(partida.jogador1());
        assertNotNull(partida.jogador2());
        assertTrue(partida.jogador1().pronto());
        assertTrue(partida.jogador2().pronto());
    }

    @Test
    @DisplayName("Regra de inéditas no torneio: histórico pode ser resetado")
    void testeQuestaoConsumidaNoTorneio() {
        CriarSalaRequest criar = new CriarSalaRequest("TORNEIO1", "", List.of(Materia.MATEMATICA));
        partidaService.criarSala(criar);

        JogadorResumoResponse j1 = partidaService.entrarSala(new EntrarSalaRequest("TORNEIO1", "", "Player 1"));
        JogadorResumoResponse j2 = partidaService.entrarSala(new EntrarSalaRequest("TORNEIO1", "", "Player 2"));

        partidaService.alternarPronto("TORNEIO1", j1.id());
        partidaService.alternarPronto("TORNEIO1", j2.id());

        PartidaStatusResponse status = partidaService.obterStatus("TORNEIO1", j1.id());
        assertNotNull(status);

        historicoQuestoesTorneioSupport.reiniciarHistoricoTorneio();
        assertEquals(0, historicoQuestoesTorneioSupport.getTotalQuestoesConsumidas());
    }

    @Test
    @DisplayName("Criação de sala com Matérias Gerais: engloba todas as matérias aleatoriamente")
    void testeCriarSalaComMateriasGerais() {
        CriarSalaRequest criar = new CriarSalaRequest("GERAL1", "123", Materia.GERAL);
        PartidaStatusResponse partida = partidaService.criarSala(criar);

        assertEquals("GERAL1", partida.codigo());
        assertEquals(Materia.GERAL, partida.materia());
    }

    @Test
    @DisplayName("Criação de sala com Matéria Específica")
    void testeCriarSalaComMateriaEspecifica() {
        CriarSalaRequest criar = new CriarSalaRequest("SPEC1", "", Materia.MATEMATICA);
        PartidaStatusResponse partida = partidaService.criarSala(criar);

        assertEquals("SPEC1", partida.codigo());
        assertEquals(Materia.MATEMATICA, partida.materia());
    }
}
