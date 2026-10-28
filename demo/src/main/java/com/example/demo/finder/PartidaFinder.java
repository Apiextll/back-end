package com.example.demo.finder;

import com.example.demo.entity.Jogador;
import com.example.demo.entity.Partida;
import com.example.demo.entity.ResultadoPartida;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PartidaRepository;
import com.example.demo.repository.ResultadoPartidaRepository;
import com.example.demo.validator.PartidaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PartidaFinder {

    private final PartidaRepository partidaRepository;
    private final ResultadoPartidaRepository resultadoPartidaRepository;
    private final PartidaValidator partidaValidator;

    @Transactional(readOnly = true)
    public Partida buscarPorCodigoOuFalhar(String codigo) {
        Partida partida = partidaRepository.findByCodigoIgnoreCase(codigo != null ? codigo.trim() : null)
                .orElse(null);
        partidaValidator.validarSalaExistente(partida, codigo);
        return partida;
    }

    public Jogador buscarJogadorNaSalaOuFalhar(Partida partida, String jogadorId) {
        Jogador jogador = partida.getJogadorPorId(jogadorId);
        partidaValidator.validarJogadorPresente(jogador);
        return jogador;
    }

    @Transactional(readOnly = true)
    public Optional<ResultadoPartida> buscarResultadoPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return resultadoPartidaRepository.findByCodigoPartidaIgnoreCase(codigo.trim());
    }

    @Transactional(readOnly = true)
    public ResultadoPartida buscarResultadoPorCodigoOuFalhar(String codigo) {
        return buscarResultadoPorCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado da partida ainda não disponível para: " + codigo));
    }
}
