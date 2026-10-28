package com.example.demo.mapper;

import com.example.demo.dto.response.JogadorResumoResponse;
import com.example.demo.entity.Jogador;
import org.springframework.stereotype.Component;

@Component
public class JogadorMapper {

    public JogadorResumoResponse toResumoResponse(Jogador jogador) {
        if (jogador == null) {
            return null;
        }
        return new JogadorResumoResponse(
                jogador.getId(),
                jogador.getNome(),
                jogador.isPronto(),
                jogador.getPontuacaoTotal(),
                jogador.getQuestoesAcertadas(),
                jogador.getTotalQuestoesRespondidas()
        );
    }

    public Jogador toEntity(String id, String nome) {
        return new Jogador(id, nome);
    }
}
