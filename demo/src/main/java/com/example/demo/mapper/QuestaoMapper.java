package com.example.demo.mapper;

import com.example.demo.dto.request.CadastrarQuestaoRequest;
import com.example.demo.dto.response.QuestaoResponse;
import com.example.demo.entity.Questao;
import org.springframework.stereotype.Component;

@Component
public class QuestaoMapper {

    public QuestaoResponse toResponse(Questao questao) {
        if (questao == null) {
            return null;
        }
        return new QuestaoResponse(
                questao.getId(),
                questao.getEnunciado(),
                questao.getMateria().getNomeExibicao(),
                questao.getMateria().getCategoria().getDescricao(),
                questao.getDificuldade().getDescricao(),
                questao.getDificuldade().getPontosBase(),
                questao.getAlternativas()
        );
    }

    public Questao toEntity(Long id, CadastrarQuestaoRequest request) {
        if (request == null) {
            return null;
        }
        return new Questao(
                id,
                request.enunciado(),
                request.materia(),
                request.dificuldade(),
                request.alternativas(),
                request.indiceCorreto()
        );
    }
}
