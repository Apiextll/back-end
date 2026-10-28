package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EntrarSalaRequest(
        @NotBlank(message = "O código da sala é obrigatório")
        String codigo,

        String senha,

        @NotBlank(message = "O nome do jogador é obrigatório")
        String nomeJogador
) {
}
