package com.example.demo.enums;

public enum CategoriaMateria {
    GERAL("Matérias Gerais"),
    LINGUAGENS("Linguagens e suas Tecnologias"),
    EXATAS("Ciências Exatas"),
    NATUREZA("Ciências da Natureza"),
    HUMANAS("Ciências Humanas");

    private final String descricao;

    CategoriaMateria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
