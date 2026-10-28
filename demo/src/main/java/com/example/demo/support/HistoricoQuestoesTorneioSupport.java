package com.example.demo.support;

import com.example.demo.entity.Questao;
import com.example.demo.entity.QuestaoConsumidaTorneio;
import com.example.demo.repository.QuestaoConsumidaTorneioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoricoQuestoesTorneioSupport {

    private final QuestaoConsumidaTorneioRepository questaoConsumidaTorneioRepository;

    @Transactional
    public void registrarResposta(Long questaoId) {
        if (questaoId != null && !questaoConsumidaTorneioRepository.existsById(questaoId)) {
            QuestaoConsumidaTorneio registro = QuestaoConsumidaTorneio.builder()
                    .questaoId(questaoId)
                    .consumidaEm(Instant.now())
                    .build();
            questaoConsumidaTorneioRepository.save(registro);
            log.info("Questão {} registrada como consumida no banco de dados", questaoId);
        }
    }

    @Transactional(readOnly = true)
    public boolean isRespondida(Long questaoId) {
        return questaoId != null && questaoConsumidaTorneioRepository.existsById(questaoId);
    }

    @Transactional(readOnly = true)
    public List<Questao> filtrarIneditas(List<Questao> questoes) {
        if (questoes == null || questoes.isEmpty()) {
            return List.of();
        }
        return questoes.stream()
                .filter(q -> !questaoConsumidaTorneioRepository.existsById(q.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void reiniciarHistoricoTorneio() {
        long total = questaoConsumidaTorneioRepository.count();
        questaoConsumidaTorneioRepository.deleteAllInBatch();
        log.info("Histórico de questões do torneio resetado no banco de dados ({} registros removidos)", total);
    }

    @Transactional(readOnly = true)
    public long getTotalQuestoesConsumidas() {
        return questaoConsumidaTorneioRepository.count();
    }
}
