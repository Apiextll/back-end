package com.example.demo.controller;

import com.example.demo.dto.request.CadastrarQuestaoRequest;
import com.example.demo.dto.response.MateriaResponse;
import com.example.demo.dto.response.MensagemResponse;
import com.example.demo.dto.response.QuestaoResponse;
import com.example.demo.service.QuestaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/questoes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class QuestaoController {

    private final QuestaoService questaoService;

    @GetMapping("/materias")
    public ResponseEntity<List<MateriaResponse>> listarMaterias() {
        return ResponseEntity.ok(questaoService.listarMaterias());
    }

    @GetMapping
    public ResponseEntity<List<QuestaoResponse>> listarTodas() {
        return ResponseEntity.ok(questaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(questaoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MensagemResponse> cadastrarQuestao(@Valid @RequestBody CadastrarQuestaoRequest request) {
        questaoService.cadastrarQuestao(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensagemResponse("Questão cadastrada com sucesso!"));
    }

    @PostMapping("/torneio/reiniciar")
    public ResponseEntity<MensagemResponse> reiniciarQuestoesTorneio() {
        questaoService.reiniciarTorneio();
        return ResponseEntity.ok(new MensagemResponse(
                "Histórico de questões do torneio resetado com sucesso! Todas as questões estão inéditas novamente."
        ));
    }
}
