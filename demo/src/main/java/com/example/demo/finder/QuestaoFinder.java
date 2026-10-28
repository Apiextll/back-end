package com.example.demo.finder;

import com.example.demo.entity.Questao;
import com.example.demo.enums.Materia;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.QuestaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestaoFinder {

    private final QuestaoRepository questaoRepository;

    @Transactional(readOnly = true)
    public Questao buscarPorIdOuFalhar(Long id) {
        return questaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Questao> listarTodas() {
        return questaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Questao> buscarPorMateria(Materia materia) {
        if (materia == null || materia == Materia.GERAL) {
            return questaoRepository.findAll();
        }
        return questaoRepository.findByMateria(materia);
    }

    @Transactional(readOnly = true)
    public List<Questao> buscarPorMaterias(List<Materia> materias) {
        if (materias == null || materias.isEmpty()) {
            return questaoRepository.findAll();
        }
        return questaoRepository.findByMateriaIn(materias);
    }
}
