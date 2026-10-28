package com.example.demo.mapper;

import com.example.demo.entity.Jogador;
import com.example.demo.entity.ResultadoJogador;
import com.example.demo.entity.ResultadoPartida;
import org.springframework.stereotype.Component;

@Component
public class ResultadoMapper {

    public ResultadoJogador toResultadoJogador(Jogador jogador) {
        if (jogador == null) {
            return null;
        }
        return new ResultadoJogador(jogador);
    }

    public ResultadoPartida toResultadoPartida(String codigo, ResultadoJogador j1, ResultadoJogador j2) {
        return new ResultadoPartida(codigo, j1, j2);
    }
}
