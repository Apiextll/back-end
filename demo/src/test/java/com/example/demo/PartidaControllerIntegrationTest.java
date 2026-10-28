package com.example.demo;

import com.example.demo.controller.PartidaController;
import com.example.demo.controller.QuestaoController;
import com.example.demo.dto.request.CadastrarQuestaoRequest;
import com.example.demo.dto.request.CriarSalaRequest;
import com.example.demo.dto.request.EntrarSalaRequest;
import com.example.demo.enums.Dificuldade;
import com.example.demo.enums.Materia;
import com.example.demo.enums.ModoJogo;
import com.example.demo.enums.StatusPartida;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PartidaControllerIntegrationTest {

    @Autowired
    private PartidaController partidaController;

    @Autowired
    private QuestaoController questaoController;

    @Test
    @DisplayName("API: Listar matérias retorna 200 OK com lista de matérias")
    void testeListarMaterias() {
        var response = questaoController.listarMaterias();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("API: Criar sala via POST retorna 201 CREATED")
    void testeCriarSalaEndpoint() {
        CriarSalaRequest request = new CriarSalaRequest("API001", "1234", ModoJogo.DUPLO_PC_REDE, Materia.MATEMATICA, null, null);
        var response = partidaController.criarSala(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("API001", response.getBody().codigo());
        assertEquals(StatusPartida.AGUARDANDO_JOGADORES, response.getBody().status());
        assertEquals(Materia.MATEMATICA, response.getBody().materia());
    }

    @Test
    @DisplayName("API: Entrar na sala via POST retorna 200 OK com resumo do jogador")
    void testeEntrarSalaEndpoint() {
        CriarSalaRequest criar = new CriarSalaRequest("API002", "1234", ModoJogo.DUPLO_PC_REDE, Materia.GERAL, null, null);
        partidaController.criarSala(criar);

        EntrarSalaRequest entrar = new EntrarSalaRequest("API002", "1234", "Tester");
        var response = partidaController.entrarSala(entrar);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tester", response.getBody().nome());
        assertNotNull(response.getBody().id());
        assertFalse(response.getBody().pronto());
    }

    @Test
    @DisplayName("API: Alternar status pronto do jogador retorna 200 OK com AlternarProntoResponse")
    void testeAlternarProntoEndpoint() {
        CriarSalaRequest criar = new CriarSalaRequest("API003", "", ModoJogo.DUPLO_PC_REDE, Materia.GERAL, null, null);
        partidaController.criarSala(criar);

        var entrar = partidaController.entrarSala(new EntrarSalaRequest("API003", "", "Jogador Pronto"));
        String jogadorId = entrar.getBody().id();

        var response = partidaController.alternarPronto("API003", jogadorId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jogadorId, response.getBody().jogadorId());
        assertEquals(true, response.getBody().pronto());
    }

    @Test
    @DisplayName("API: Cadastrar questão via POST retorna 201 CREATED com MensagemResponse")
    void testeCadastrarQuestaoEndpoint() {
        CadastrarQuestaoRequest request = new CadastrarQuestaoRequest(
                "Quem escreveu Dom Casmurro?",
                Materia.PORTUGUES,
                Dificuldade.FACIL,
                List.of("Machado de Assis", "José de Alencar", "Clarice Lispector", "Guimarães Rosa"),
                0
        );

        var response = questaoController.cadastrarQuestao(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Questão cadastrada com sucesso!", response.getBody().mensagem());
    }

    @Test
    @DisplayName("API: Reiniciar histórico do torneio retorna 200 OK")
    void testeReiniciarTorneioEndpoint() {
        var response = questaoController.reiniciarQuestoesTorneio();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().mensagem());
    }
}
