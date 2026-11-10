package br.com.estudioanaelizabeth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estudioanaelizabeth.domain.StatusTreino;
import br.com.estudioanaelizabeth.domain.Treino;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    List<Treino> findByAlunaIdOrderByDataPrevistaDesc(Long alunaId);

    List<Treino> findByAlunaIdAndStatus(Long alunaId, StatusTreino status);
}
