package com.example.demo.dto.response;

public record JogadorResumoResponse(
        String id,
        String nome,
        boolean pronto,
        int pontuacaoTotal,
        int totalAcertos,
        int totalRespondidas
) {
}
