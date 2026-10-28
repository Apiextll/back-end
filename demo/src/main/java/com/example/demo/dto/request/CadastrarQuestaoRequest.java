package com.example.demo.dto.request;

import com.example.demo.enums.Dificuldade;
import com.example.demo.enums.Materia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CadastrarQuestaoRequest(
        @NotBlank(message = "Enunciado é obrigatório")
        String enunciado,

        @NotNull(message = "Matéria é obrigatória")
        Materia materia,

        @NotNull(message = "Dificuldade é obrigatória")
        Dificuldade dificuldade,

        @NotNull(message = "Alternativas são obrigatórias")
        @Size(min = 4, max = 4, message = "Devem ser fornecidas exatamente 4 alternativas")
        List<String> alternativas,

        int indiceCorreto
) {
}
