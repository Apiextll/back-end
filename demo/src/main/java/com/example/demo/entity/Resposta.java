package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_respostas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogador_id", nullable = false)
    private Jogador jogador;

    @Column(name = "questao_id", nullable = false)
    private Long questaoId;

    @Column(name = "indice_escolhido", nullable = false)
    private int indiceEscolhido;

    @Column(name = "tempo_gasto_segundos", nullable = false)
    private double tempoGastoSegundos;

    @Column(name = "correta", nullable = false)
    private boolean correta;

    @Column(name = "pontos_obtidos", nullable = false)
    private int pontosObtidos;

    @Builder.Default
    @Column(name = "instante_resposta", nullable = false)
    private Instant instanteResposta = Instant.now();

    public Resposta(Long questaoId, int indiceEscolhido, double tempoGastoSegundos, boolean correta, int pontosObtidos) {
        this.questaoId = questaoId;
        this.indiceEscolhido = indiceEscolhido;
        this.tempoGastoSegundos = tempoGastoSegundos;
        this.correta = correta;
        this.pontosObtidos = pontosObtidos;
        this.instanteResposta = Instant.now();
    }
}
