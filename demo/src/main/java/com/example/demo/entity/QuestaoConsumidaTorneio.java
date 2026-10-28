package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_questoes_consumidas_torneio")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestaoConsumidaTorneio {

    @Id
    @Column(name = "questao_id", nullable = false)
    private Long questaoId;

    @Builder.Default
    @Column(name = "consumida_em", nullable = false)
    private Instant consumidaEm = Instant.now();
}
