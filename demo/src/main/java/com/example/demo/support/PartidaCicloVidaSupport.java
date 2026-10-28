package com.example.demo.support;

import com.example.demo.config.RegrasJogoConfig;
import com.example.demo.entity.Partida;
import com.example.demo.entity.ResultadoJogador;
import com.example.demo.entity.ResultadoPartida;
import com.example.demo.enums.ModoJogo;
import com.example.demo.enums.StatusPartida;
import com.example.demo.mapper.ResultadoMapper;
import com.example.demo.repository.PartidaRepository;
import com.example.demo.repository.ResultadoPartidaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartidaCicloVidaSupport {

    private final PartidaRepository partidaRepository;
    private final ResultadoPartidaRepository resultadoPartidaRepository;
    private final ResultadoMapper resultadoMapper;

    @Transactional
    public void atualizarCicloDeVida(Partida partida) {
        Instant agora = Instant.now();

        // 1. Transição de CONTAGEM_REGRESSIVA (3s) para EM_ANDAMENTO
        if (partida.getStatus() == StatusPartida.CONTAGEM_REGRESSIVA && partida.getInicioContagem() != null) {
            long segundosContagem = Duration.between(partida.getInicioContagem(), agora).getSeconds();
            if (segundosContagem >= RegrasJogoConfig.CONTAGEM_REGRESSIVA_INICIAL_SEGUNDOS) {
                partida.setStatus(StatusPartida.EM_ANDAMENTO);
                if (partida.getInicioPartida() == null) {
                    partida.setInicioPartida(agora);
                }
                partida.setInicioTurnoAtual(agora);

                if (partida.getModoJogo() == ModoJogo.TURNO_LOCAL_1PC && partida.getTurnoAtualJogadorId() == null) {
                    partida.setTurnoAtualJogadorId(partida.getJogador1().getId());
                }

                partidaRepository.save(partida);
                log.info("Partida {} iniciada em modo {}. Turno ativo: {}",
                        partida.getCodigo(), partida.getModoJogo(), partida.getTurnoAtualJogadorId());
            }
        }

        // 2. Verificação de tempo esgotado no turno em andamento
        if (partida.getStatus() == StatusPartida.EM_ANDAMENTO && partida.isTempoTurnoEsgotado()) {
            if (partida.getModoJogo() == ModoJogo.TURNO_LOCAL_1PC) {
                if (partida.getJogador1() != null && partida.getJogador1().getId().equals(partida.getTurnoAtualJogadorId())) {
                    partida.setStatus(StatusPartida.INTERVALO_TRANSICAO);
                    partidaRepository.save(partida);
                    log.info("Turno do Jogador 1 finalizado na sala {}. Entrando em INTERVALO_TRANSICAO", partida.getCodigo());
                } else {
                    finalizarPartida(partida);
                }
            } else {
                finalizarPartida(partida);
            }
        }
    }

    @Transactional
    public void iniciarSegundoTurno(Partida partida) {
        partida.setStatus(StatusPartida.CONTAGEM_REGRESSIVA);
        partida.setTurnoAtualJogadorId(partida.getJogador2().getId());
        partida.setInicioContagem(Instant.now());
        partidaRepository.save(partida);
        log.info("Transição para o Jogador 2 acionada na sala {}. Contagem regressiva de 3s iniciada", partida.getCodigo());
    }

    @Transactional
    public ResultadoPartida finalizarPartida(Partida partida) {
        if (partida.getStatus() == StatusPartida.FINALIZADA) {
            return resultadoPartidaRepository.findByCodigoPartidaIgnoreCase(partida.getCodigo()).orElse(null);
        }

        partida.setStatus(StatusPartida.FINALIZADA);
        partida.setFimPartida(Instant.now());

        ResultadoJogador r1 = resultadoMapper.toResultadoJogador(partida.getJogador1());
        ResultadoJogador r2 = resultadoMapper.toResultadoJogador(partida.getJogador2());
        ResultadoPartida resultado = resultadoMapper.toResultadoPartida(partida.getCodigo(), r1, r2);

        partidaRepository.save(partida);
        resultadoPartidaRepository.save(resultado);
        log.info("Partida {} finalizada com sucesso! Vencedor: {}", partida.getCodigo(), resultado.getNomeVencedor());

        return resultado;
    }
}
