package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoJogador {

    @Column(name = "jogador_id", length = 36)
    private String jogadorId;

    @Column(name = "nome", length = 100)
    private String nome;

    @Column(name = "pontuacao_final")
    private int pontuacaoFinal;

    @Column(name = "total_respondidas")
    private int totalRespondidas;

    @Column(name = "total_acertos")
    private int totalAcertos;

    @Column(name = "total_erros")
    private int totalErros;

    @Column(name = "taxa_acerto_porcentagem")
    private double taxaAcertoPorcentagem;

    @Column(name = "tempo_medio_resposta_segundos")
    private double tempoMedioRespostaSegundos;

    public ResultadoJogador(Jogador jogador) {
        if (jogador != null) {
            this.jogadorId = jogador.getId();
            this.nome = jogador.getNome();
            this.pontuacaoFinal = jogador.getPontuacaoTotal();
            this.totalRespondidas = jogador.getTotalQuestoesRespondidas();
            this.totalAcertos = jogador.getQuestoesAcertadas();
            this.totalErros = jogador.getQuestoesErradas();
            this.taxaAcertoPorcentagem = totalRespondidas > 0
                    ? Math.round(((double) totalAcertos / totalRespondidas) * 10000.0) / 100.0
                    : 0.0;
            this.tempoMedioRespostaSegundos = jogador.getTempoMedioRespostaSegundos();
        }
    }
}
