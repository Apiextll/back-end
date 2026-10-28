package com.example.demo.repository;

import com.example.demo.entity.ResultadoPartida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultadoPartidaRepository extends JpaRepository<ResultadoPartida, Long> {

    Optional<ResultadoPartida> findByCodigoPartidaIgnoreCase(String codigoPartida);
}
