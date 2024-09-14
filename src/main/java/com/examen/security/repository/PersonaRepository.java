package com.examen.security.repository;

import com.examen.security.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonaRepository extends JpaRepository<PersonaEntity, Long> {

    Optional<PersonaEntity> findByNumeroDocumento(String dni);
    Optional<List<PersonaEntity>> findByEstado(Integer state);
}
