package com.example.demo.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_resultados_partida")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_partida", length = 20, nullable = false, unique = true)
    private String codigoPartida;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "jogadorId", column = @Column(name = "j1_jogador_id", length = 36)),
            @AttributeOverride(name = "nome", column = @Column(name = "j1_nome", length = 100)),
            @AttributeOverride(name = "pontuacaoFinal", column = @Column(name = "j1_pontuacao_final")),
            @AttributeOverride(name = "totalRespondidas", column = @Column(name = "j1_total_respondidas")),
            @AttributeOverride(name = "totalAcertos", column = @Column(name = "j1_total_acertos")),
            @AttributeOverride(name = "totalErros", column = @Column(name = "j1_total_erros")),
            @AttributeOverride(name = "taxaAcertoPorcentagem", column = @Column(name = "j1_taxa_acerto")),
            @AttributeOverride(name = "tempoMedioRespostaSegundos", column = @Column(name = "j1_tempo_medio"))
    })
    private ResultadoJogador jogador1;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "jogadorId", column = @Column(name = "j2_jogador_id", length = 36)),
            @AttributeOverride(name = "nome", column = @Column(name = "j2_nome", length = 100)),
            @AttributeOverride(name = "pontuacaoFinal", column = @Column(name = "j2_pontuacao_final")),
            @AttributeOverride(name = "totalRespondidas", column = @Column(name = "j2_total_respondidas")),
            @AttributeOverride(name = "totalAcertos", column = @Column(name = "j2_total_acertos")),
            @AttributeOverride(name = "totalErros", column = @Column(name = "j2_total_erros")),
            @AttributeOverride(name = "taxaAcertoPorcentagem", column = @Column(name = "j2_taxa_acerto")),
            @AttributeOverride(name = "tempoMedioRespostaSegundos", column = @Column(name = "j2_tempo_medio"))
    })
    private ResultadoJogador jogador2;

    @Column(name = "vencedor_id", length = 36)
    private String vencedorId;

    @Column(name = "nome_vencedor", length = 100)
    private String nomeVencedor;

    @Column(name = "motivo_vitoria", columnDefinition = "TEXT")
    private String motivoVitoria;

    @Builder.Default
    @Column(name = "finalizada_em", nullable = false)
    private Instant finalizadaEm = Instant.now();

    public ResultadoPartida(String codigoPartida, ResultadoJogador j1, ResultadoJogador j2) {
        this.codigoPartida = codigoPartida;
        this.jogador1 = j1;
        this.jogador2 = j2;
        this.finalizadaEm = Instant.now();
        determinarVencedor();
    }

    private void determinarVencedor() {
        if (jogador1 == null || jogador2 == null) {
            this.nomeVencedor = "Indefinido";
            this.motivoVitoria = "Jogadores incompletos";
            return;
        }

        if (jogador1.getPontuacaoFinal() > jogador2.getPontuacaoFinal()) {
            this.vencedorId = jogador1.getJogadorId();
            this.nomeVencedor = jogador1.getNome();
            this.motivoVitoria = "Maior pontuação (" + jogador1.getPontuacaoFinal() + " vs " + jogador2.getPontuacaoFinal() + " pts)";
            return;
        } else if (jogador2.getPontuacaoFinal() > jogador1.getPontuacaoFinal()) {
            this.vencedorId = jogador2.getJogadorId();
            this.nomeVencedor = jogador2.getNome();
            this.motivoVitoria = "Maior pontuação (" + jogador2.getPontuacaoFinal() + " vs " + jogador1.getPontuacaoFinal() + " pts)";
            return;
        }

        if (jogador1.getTotalAcertos() > jogador2.getTotalAcertos()) {
            this.vencedorId = jogador1.getJogadorId();
            this.nomeVencedor = jogador1.getNome();
            this.motivoVitoria = "Desempate por mais acertos (" + jogador1.getTotalAcertos() + " acertos)";
            return;
        } else if (jogador2.getTotalAcertos() > jogador1.getTotalAcertos()) {
            this.vencedorId = jogador2.getJogadorId();
            this.nomeVencedor = jogador2.getNome();
            this.motivoVitoria = "Desempate por mais acertos (" + jogador2.getTotalAcertos() + " acertos)";
            return;
        }

        if (jogador1.getTempoMedioRespostaSegundos() < jogador2.getTempoMedioRespostaSegundos()) {
            this.vencedorId = jogador1.getJogadorId();
            this.nomeVencedor = jogador1.getNome();
            this.motivoVitoria = "Desempate por agilidade (Tempo médio: " + jogador1.getTempoMedioRespostaSegundos() + "s)";
            return;
        } else if (jogador2.getTempoMedioRespostaSegundos() < jogador1.getTempoMedioRespostaSegundos()) {
            this.vencedorId = jogador2.getJogadorId();
            this.nomeVencedor = jogador2.getNome();
            this.motivoVitoria = "Desempate por agilidade (Tempo médio: " + jogador2.getTempoMedioRespostaSegundos() + "s)";
            return;
        }

        this.vencedorId = null;
        this.nomeVencedor = "Empate Técnico";
        this.motivoVitoria = "Pontuação, acertos e tempo médio rigorosamente iguais";
    }
}
