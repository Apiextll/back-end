package com.example.demo.dto.request;

import com.example.demo.enums.Materia;
import com.example.demo.enums.ModoJogo;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CriarSalaRequest(
        String codigo,

        String senha,

        ModoJogo modoJogo,

        Materia materia,

        String nomeJogador1,

        String nomeJogador2
) {
    @JsonCreator
    public CriarSalaRequest(
            @JsonProperty("codigo") String codigo,
            @JsonProperty("senha") String senha,
            @JsonProperty("modoJogo") ModoJogo modoJogo,
            @JsonProperty("materia") Materia materia,
            @JsonProperty("materias") List<Materia> materias,
            @JsonProperty("nomeJogador1") String nomeJogador1,
            @JsonProperty("nomeJogador2") String nomeJogador2
    ) {
        this(
                codigo,
                senha,
                modoJogo != null ? modoJogo : ModoJogo.DUPLO_PC_REDE,
                materia != null ? materia : (materias != null && !materias.isEmpty() ? materias.get(0) : Materia.GERAL),
                nomeJogador1,
                nomeJogador2
        );
    }

    public CriarSalaRequest(String codigo, String senha, Materia materia) {
        this(codigo, senha, ModoJogo.DUPLO_PC_REDE, materia != null ? materia : Materia.GERAL, null, null);
    }

    public CriarSalaRequest(String codigo, String senha, List<Materia> materias) {
        this(codigo, senha, ModoJogo.DUPLO_PC_REDE, (materias != null && !materias.isEmpty()) ? materias.get(0) : Materia.GERAL, null, null);
    }

    public CriarSalaRequest(String codigo, String senha, ModoJogo modoJogo, List<Materia> materias, String nomeJogador1, String nomeJogador2) {
        this(codigo, senha, modoJogo, (materias != null && !materias.isEmpty()) ? materias.get(0) : Materia.GERAL, nomeJogador1, nomeJogador2);
    }
}
