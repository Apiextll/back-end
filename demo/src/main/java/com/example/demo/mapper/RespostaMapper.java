package com.example.demo.mapper;

import com.example.demo.entity.Jogador;
import com.example.demo.entity.Resposta;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RespostaMapper {

    public Resposta toEntity(
            Jogador jogador,
            Long questaoId,
            int indiceEscolhido,
            double tempoGastoSegundos,
            boolean correta,
            int pontosObtidos
    ) {
        return Resposta.builder()
                .jogador(jogador)
                .questaoId(questaoId)
                .indiceEscolhido(indiceEscolhido)
                .tempoGastoSegundos(tempoGastoSegundos)
                .correta(correta)
                .pontosObtidos(pontosObtidos)
                .instanteResposta(Instant.now())
                .build();
    }
}
