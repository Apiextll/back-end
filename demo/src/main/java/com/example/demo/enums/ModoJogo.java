package com.example.demo.enums;

public enum ModoJogo {
    DUPLO_PC_REDE("2 Computadores (Rede Local / Online)"),
    TURNO_LOCAL_1PC("1 Computador (Turnos Sequenciais)");

    private final String descricao;

    ModoJogo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
