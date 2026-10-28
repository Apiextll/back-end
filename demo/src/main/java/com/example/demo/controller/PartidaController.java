package com.example.demo.controller;

import com.example.demo.dto.request.CriarSalaRequest;
import com.example.demo.dto.request.EntrarSalaRequest;
import com.example.demo.dto.request.SubmeterRespostaRequest;
import com.example.demo.dto.response.AlternarProntoResponse;
import com.example.demo.dto.response.FeedbackRespostaResponse;
import com.example.demo.dto.response.JogadorResumoResponse;
import com.example.demo.dto.response.PartidaStatusResponse;
import com.example.demo.entity.ResultadoPartida;
import com.example.demo.service.PartidaService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/partidas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PartidaController {

    private final PartidaService partidaService;

    @PostMapping
    public ResponseEntity<PartidaStatusResponse> criarSala(@Valid @RequestBody CriarSalaRequest request) {
        PartidaStatusResponse response = partidaService.criarSala(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/entrar")
    public ResponseEntity<JogadorResumoResponse> entrarSala(@Valid @RequestBody EntrarSalaRequest request) {
        JogadorResumoResponse response = partidaService.entrarSala(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{codigo}/pronto/{jogadorId}")
    public ResponseEntity<AlternarProntoResponse> alternarPronto(
            @PathVariable String codigo,
            @PathVariable String jogadorId) {
        boolean pronto = partidaService.alternarPronto(codigo, jogadorId);
        return ResponseEntity.ok(new AlternarProntoResponse(jogadorId, pronto));
    }

    @PostMapping("/{codigo}/iniciar-segundo-turno")
    public ResponseEntity<PartidaStatusResponse> iniciarSegundoTurno(@PathVariable String codigo) {
        PartidaStatusResponse response = partidaService.iniciarSegundoTurno(codigo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{codigo}/status")
    public ResponseEntity<PartidaStatusResponse> obterStatus(
            @PathVariable String codigo,
            @RequestParam(required = false) String jogadorId) {
        PartidaStatusResponse status = partidaService.obterStatus(codigo, jogadorId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/responder")
    public ResponseEntity<FeedbackRespostaResponse> responder(@Valid @RequestBody SubmeterRespostaRequest request) {
        FeedbackRespostaResponse feedback = partidaService.submeterResposta(request);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/{codigo}/resultado")
    public ResponseEntity<ResultadoPartida> obterResultado(@PathVariable String codigo) {
        ResultadoPartida resultado = partidaService.obterResultado(codigo);
        return ResponseEntity.ok(resultado);
    }
}
