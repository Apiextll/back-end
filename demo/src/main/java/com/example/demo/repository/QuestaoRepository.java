package com.example.demo.repository;

import com.example.demo.entity.Questao;
import com.example.demo.enums.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestaoRepository extends JpaRepository<Questao, Long> {

    List<Questao> findByMateriaIn(List<Materia> materias);

    List<Questao> findByMateria(Materia materia);
}
