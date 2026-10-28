package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_jogadores")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Jogador {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "pronto", nullable = false)
    private boolean pronto;

    @Column(name = "pontuacao_total", nullable = false)
    private int pontuacaoTotal;

    @Column(name = "questoes_acertadas", nullable = false)
    private int questoesAcertadas;

    @Column(name = "questoes_erradas", nullable = false)
    private int questoesErradas;

    @Column(name = "indice_questao_atual", nullable = false)
    private int indiceQuestaoAtual;

    @Builder.Default
    @OneToMany(mappedBy = "jogador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Resposta> historicoRespostas = new ArrayList<>();

    public Jogador(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.pronto = false;
        this.pontuacaoTotal = 0;
        this.questoesAcertadas = 0;
        this.questoesErradas = 0;
        this.indiceQuestaoAtual = 0;
        this.historicoRespostas = new ArrayList<>();
    }

    public void registrarResposta(Resposta resposta) {
        resposta.setJogador(this);
        this.historicoRespostas.add(resposta);
        if (resposta.isCorreta()) {
            this.questoesAcertadas++;
            this.pontuacaoTotal += resposta.getPontosObtidos();
        } else {
            this.questoesErradas++;
        }
        this.indiceQuestaoAtual++;
    }

    public double getTempoMedioRespostaSegundos() {
        if (historicoRespostas == null || historicoRespostas.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Resposta r : historicoRespostas) {
            total += r.getTempoGastoSegundos();
        }
        return Math.round((total / historicoRespostas.size()) * 100.0) / 100.0;
    }

    public int getTotalQuestoesRespondidas() {
        return questoesAcertadas + questoesErradas;
    }
}
