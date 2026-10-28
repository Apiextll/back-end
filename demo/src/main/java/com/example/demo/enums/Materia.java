package com.example.demo.enums;

public enum Materia {
    // Matérias Gerais (todas as matérias)
    GERAL("Matérias Gerais", CategoriaMateria.GERAL),

    // Linguagens e suas tecnologias
    PORTUGUES("Português", CategoriaMateria.LINGUAGENS),
    LINGUAS_ESTRANGEIRAS("Línguas Estrangeiras", CategoriaMateria.LINGUAGENS),
    REDACAO("Redação", CategoriaMateria.LINGUAGENS),
    EDUCACAO_FISICA("Educação Física", CategoriaMateria.LINGUAGENS),
    ARTES("Artes", CategoriaMateria.LINGUAGENS),

    // Ciências Exatas
    MATEMATICA("Matemática", CategoriaMateria.EXATAS),

    // Ciências da Natureza
    BIOLOGIA("Biologia", CategoriaMateria.NATUREZA),
    FISICA("Física", CategoriaMateria.NATUREZA),
    QUIMICA("Química", CategoriaMateria.NATUREZA),

    // Ciências Humanas
    FILOSOFIA("Filosofia", CategoriaMateria.HUMANAS),
    SOCIOLOGIA("Sociologia", CategoriaMateria.HUMANAS),
    GEOGRAFIA("Geografia", CategoriaMateria.HUMANAS),
    HISTORIA("História", CategoriaMateria.HUMANAS);

    private final String nomeExibicao;
    private final CategoriaMateria categoria;

    Materia(String nomeExibicao, CategoriaMateria categoria) {
        this.nomeExibicao = nomeExibicao;
        this.categoria = categoria;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public CategoriaMateria getCategoria() {
        return categoria;
    }
}
