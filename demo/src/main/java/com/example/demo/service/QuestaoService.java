package com.example.demo.service;

import com.example.demo.dto.request.CadastrarQuestaoRequest;
import com.example.demo.dto.response.MateriaResponse;
import com.example.demo.dto.response.QuestaoResponse;
import com.example.demo.entity.Questao;
import com.example.demo.enums.Materia;
import com.example.demo.finder.QuestaoFinder;
import com.example.demo.mapper.QuestaoMapper;
import com.example.demo.repository.QuestaoRepository;
import com.example.demo.support.HistoricoQuestoesTorneioSupport;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestaoService {

    private final QuestaoRepository questaoRepository;
    private final QuestaoFinder questaoFinder;
    private final QuestaoMapper questaoMapper;
    private final HistoricoQuestoesTorneioSupport historicoQuestoesTorneioSupport;

    @PostConstruct
    public void verificarQuestoesIniciais() {
        log.info("Flyway / Banco de Questões: {} questões disponíveis no acervo", questaoRepository.count());
    }

    @Transactional
    public void cadastrarQuestao(CadastrarQuestaoRequest request) {
        Questao questao = questaoMapper.toEntity(null, request);
        questaoRepository.save(questao);
        log.info("Nova questão cadastrada no banco com ID {}: {}", questao.getId(), questao.getEnunciado());
    }

    @Transactional(readOnly = true)
    public List<Questao> obterQuestoesAleatorias(Materia materia, int quantidadeDesejada) {
        List<Questao> questoesDisponiveis = questaoFinder.buscarPorMateria(materia);

        if (questoesDisponiveis.isEmpty()) {
            questoesDisponiveis = questaoFinder.listarTodas();
        }

        // 1. Filtrar as que ainda NÃO foram respondidas no torneio (Inéditas)
        List<Questao> ineditas = historicoQuestoesTorneioSupport.filtrarIneditas(questoesDisponiveis);
        Collections.shuffle(ineditas);

        if (ineditas.size() >= quantidadeDesejada) {
            return new ArrayList<>(ineditas.subList(0, quantidadeDesejada));
        }

        // 2. Opção B: Esgotaram as inéditas desta matéria -> Recicla as já respondidas em ordem aleatória
        log.warn("Questões inéditas esgotadas para a matéria {}! Reciclando questões conforme a Opção B.", materia != null ? materia : Materia.GERAL);
        List<Questao> resultado = new ArrayList<>(ineditas);

        List<Questao> recicladas = questoesDisponiveis.stream()
                .filter(q -> historicoQuestoesTorneioSupport.isRespondida(q.getId()))
                .collect(Collectors.toList());
        Collections.shuffle(recicladas);

        for (Questao q : recicladas) {
            if (resultado.size() >= quantidadeDesejada) {
                break;
            }
            resultado.add(q);
        }

        while (resultado.size() < quantidadeDesejada && !questoesDisponiveis.isEmpty()) {
            resultado.add(questoesDisponiveis.get((int) (Math.random() * questoesDisponiveis.size())));
        }

        return resultado;
    }

    @Transactional(readOnly = true)
    public List<MateriaResponse> listarMaterias() {
        return Arrays.stream(Materia.values())
                .map(m -> new MateriaResponse(
                        m.name(),
                        m.getNomeExibicao(),
                        m.getCategoria().getDescricao()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestaoResponse> listarTodas() {
        return questaoFinder.listarTodas().stream()
                .map(questaoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuestaoResponse buscarPorId(Long id) {
        return questaoMapper.toResponse(questaoFinder.buscarPorIdOuFalhar(id));
    }

    @Transactional
    public void reiniciarTorneio() {
        historicoQuestoesTorneioSupport.reiniciarHistoricoTorneio();
    }

    @Transactional(readOnly = true)
    public List<Questao> obterQuestoesPorMaterias(List<Materia> materias, int quantidadeDesejada) {
        Materia materia = (materias != null && !materias.isEmpty()) ? materias.get(0) : Materia.GERAL;
        return obterQuestoesAleatorias(materia, quantidadeDesejada);
    }
}
