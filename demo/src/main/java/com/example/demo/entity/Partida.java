package com.example.demo.entity;

import com.example.demo.enums.Materia;
import com.example.demo.enums.ModoJogo;
import com.example.demo.enums.StatusPartida;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_partidas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Partida {

    @Id
    @Column(name = "codigo", length = 20, nullable = false, unique = true)
    private String codigo;

    @Column(name = "senha", length = 100)
    private String senha;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "modo_jogo", length = 30, nullable = false)
    private ModoJogo modoJogo = ModoJogo.DUPLO_PC_REDE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private StatusPartida status = StatusPartida.AGUARDANDO_JOGADORES;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "materia", length = 50)
    private Materia materia = Materia.GERAL;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "jogador1_id")
    private Jogador jogador1;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "jogador2_id")
    private Jogador jogador2;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_partida_questoes_j1",
            joinColumns = @JoinColumn(name = "partida_codigo"),
            inverseJoinColumns = @JoinColumn(name = "questao_id")
    )
    @OrderColumn(name = "ordem")
    private List<Questao> questoesJogador1 = new ArrayList<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_partida_questoes_j2",
            joinColumns = @JoinColumn(name = "partida_codigo"),
            inverseJoinColumns = @JoinColumn(name = "questao_id")
    )
    @OrderColumn(name = "ordem")
    private List<Questao> questoesJogador2 = new ArrayList<>();

    @Column(name = "turno_atual_jogador_id", length = 36)
    private String turnoAtualJogadorId;

    @Column(name = "inicio_contagem")
    private Instant inicioContagem;

    @Column(name = "inicio_partida")
    private Instant inicioPartida;

    @Column(name = "inicio_turno_atual")
    private Instant inicioTurnoAtual;

    @Column(name = "fim_partida")
    private Instant fimPartida;

    @Builder.Default
    @Column(name = "duracao_total_segundos", nullable = false)
    private int duracaoTotalSegundos = 120;

    @Builder.Default
    @Column(name = "tempo_por_questao_segundos", nullable = false)
    private int tempoPorQuestaoSegundos = 15;

    public Partida(String codigo, String senha, ModoJogo modoJogo, Materia materia) {
        this.codigo = codigo;
        this.senha = senha;
        this.modoJogo = modoJogo != null ? modoJogo : ModoJogo.DUPLO_PC_REDE;
        this.materia = materia != null ? materia : Materia.GERAL;
        this.status = StatusPartida.AGUARDANDO_JOGADORES;
        this.questoesJogador1 = new ArrayList<>();
        this.questoesJogador2 = new ArrayList<>();
        this.duracaoTotalSegundos = 120;
        this.tempoPorQuestaoSegundos = 15;
    }

    public Partida(String codigo, String senha, ModoJogo modoJogo, List<Materia> materiasSelecionadas) {
        this(codigo, senha, modoJogo, (materiasSelecionadas != null && !materiasSelecionadas.isEmpty()) ? materiasSelecionadas.get(0) : Materia.GERAL);
    }

    public boolean isMateriasGerais() {
        return this.materia == null || this.materia == Materia.GERAL;
    }

    public synchronized boolean adicionarJogador(Jogador jogador) {
        if (jogador1 == null) {
            jogador1 = jogador;
            return true;
        } else if (jogador2 == null) {
            jogador2 = jogador;
            return true;
        }
        return false;
    }

    public boolean ambosProntos() {
        return jogador1 != null && jogador1.isPronto() &&
               jogador2 != null && jogador2.isPronto();
    }

    public List<Questao> getQuestoesDoJogador(String jogadorId) {
        if (jogador1 != null && jogador1.getId().equals(jogadorId)) {
            return questoesJogador1;
        }
        if (jogador2 != null && jogador2.getId().equals(jogadorId)) {
            return questoesJogador2;
        }
        return List.of();
    }

    public int getTempoRestanteTurnoSegundos() {
        if (status != StatusPartida.EM_ANDAMENTO) {
            return duracaoTotalSegundos;
        }

        Instant referenciaInicio = (modoJogo == ModoJogo.TURNO_LOCAL_1PC && inicioTurnoAtual != null)
                ? inicioTurnoAtual
                : inicioPartida;

        if (referenciaInicio == null) {
            return duracaoTotalSegundos;
        }

        long segundosPassados = Duration.between(referenciaInicio, Instant.now()).getSeconds();
        int restante = duracaoTotalSegundos - (int) segundosPassados;
        return Math.max(0, restante);
    }

    public boolean isTempoTurnoEsgotado() {
        return getTempoRestanteTurnoSegundos() <= 0;
    }

    public Jogador getJogadorPorId(String id) {
        if (jogador1 != null && jogador1.getId().equals(id)) return jogador1;
        if (jogador2 != null && jogador2.getId().equals(id)) return jogador2;
        return null;
    }

    public Jogador getJogadorDaVez() {
        if (turnoAtualJogadorId == null) {
            return jogador1;
        }
        return getJogadorPorId(turnoAtualJogadorId);
    }

    public Jogador getAdversario(String id) {
        if (jogador1 != null && jogador1.getId().equals(id)) return jogador2;
        if (jogador2 != null && jogador2.getId().equals(id)) return jogador1;
        return null;
    }
}
