package com.example.demo.enums;

public enum Dificuldade {
    FACIL("Fácil", 100),
    MEDIO("Médio", 200),
    DIFICIL("Difícil", 300);

    private final String descricao;
    private final int pontosBase;

    Dificuldade(String descricao, int pontosBase) {
        this.descricao = descricao;
        this.pontosBase = pontosBase;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPontosBase() {
        return pontosBase;
    }
}
