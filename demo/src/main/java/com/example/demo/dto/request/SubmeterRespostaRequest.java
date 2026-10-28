package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmeterRespostaRequest(
        @NotBlank(message = "O código da sala é obrigatório")
        String codigoSala,

        @NotBlank(message = "O ID do jogador é obrigatório")
        String jogadorId,

        @NotNull(message = "O ID da questão é obrigatório")
        Long questaoId,

        int indiceEscolhido,

        double tempoGastoSegundos
) {
}
