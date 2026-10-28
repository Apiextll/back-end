package com.example.demo.entity;

import com.example.demo.enums.Dificuldade;
import com.example.demo.enums.Materia;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_questoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enunciado", columnDefinition = "TEXT", nullable = false)
    private String enunciado;

    @Enumerated(EnumType.STRING)
    @Column(name = "materia", length = 50, nullable = false)
    private Materia materia;

    @Enumerated(EnumType.STRING)
    @Column(name = "dificuldade", length = 30, nullable = false)
    private Dificuldade dificuldade;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_questao_alternativas", joinColumns = @JoinColumn(name = "questao_id"))
    @Column(name = "alternativa", columnDefinition = "TEXT", nullable = false)
    @OrderColumn(name = "ordem")
    private List<String> alternativas = new ArrayList<>();

    @Column(name = "indice_correto", nullable = false)
    private int indiceCorreto;

    public boolean isCorreta(int indiceEscolhido) {
        return this.indiceCorreto == indiceEscolhido;
    }
}
