package com.example.demo.validator;

import com.example.demo.entity.Jogador;
import com.example.demo.entity.Partida;
import com.example.demo.enums.ModoJogo;
import com.example.demo.enums.StatusPartida;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

@Component
public class PartidaValidator {

    public void validarSalaExistente(Partida partida, String codigo) {
        if (partida == null) {
            throw new ResourceNotFoundException("Sala não encontrada com o código: " + codigo);
        }
    }

    public void validarSenhaAcesso(Partida partida, String senhaInformada) {
        if (partida.getSenha() != null && !partida.getSenha().isBlank()) {
            if (!partida.getSenha().equals(senhaInformada)) {
                throw new UnauthorizedException("Senha incorreta para a sala: " + partida.getCodigo());
            }
        }
    }

    public void validarDisponibilidadeEntrada(Partida partida) {
        if (partida.getStatus() != StatusPartida.AGUARDANDO_JOGADORES) {
            throw new BadRequestException("A partida já foi iniciada ou finalizada.");
        }
    }

    public void validarCapacidadeSala(boolean adicionadoComSucesso) {
        if (!adicionadoComSucesso) {
            throw new ConflictException("Sala cheia! O limite de 2 jogadores já foi atingido.");
        }
    }

    public void validarJogadorPresente(Jogador jogador) {
        if (jogador == null) {
            throw new ResourceNotFoundException("Jogador não encontrado na sala.");
        }
    }

    public void validarPartidaEmAndamento(Partida partida) {
        if (partida.getStatus() != StatusPartida.EM_ANDAMENTO) {
            throw new BadRequestException("A partida não está em andamento (Status atual: " + partida.getStatus() + ").");
        }
    }

    public void validarQuestaoDisponivel(int indiceAtual, int totalQuestoes) {
        if (indiceAtual >= totalQuestoes) {
            throw new ConflictException("Todas as questões disponíveis nesta partida já foram respondidas!");
        }
    }

    public void validarTurnoJogador(Partida partida, String jogadorId) {
        if (partida.getModoJogo() == ModoJogo.TURNO_LOCAL_1PC) {
            if (partida.getTurnoAtualJogadorId() != null && !partida.getTurnoAtualJogadorId().equals(jogadorId)) {
                throw new BadRequestException("Não é a vez deste jogador! Aguarde o seu turno.");
            }
        }
    }

    public void validarPodeIniciarSegundoTurno(Partida partida) {
        if (partida.getStatus() != StatusPartida.INTERVALO_TRANSICAO) {
            throw new BadRequestException("A partida não está no intervalo de transição para o 2º turno.");
        }
    }
}
