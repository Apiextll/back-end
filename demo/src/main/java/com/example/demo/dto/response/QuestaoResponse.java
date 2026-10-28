package com.example.demo.dto.response;

import java.util.List;

public record QuestaoResponse(
        Long id,
        String enunciado,
        String materia,
        String categoria,
        String dificuldade,
        int pontosBase,
        List<String> alternativas
) {
}
