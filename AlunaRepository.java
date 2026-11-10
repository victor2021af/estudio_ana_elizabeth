package br.com.estudioanaelizabeth.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estudioanaelizabeth.domain.Aluna;

public interface AlunaRepository extends JpaRepository<Aluna, Long> {

    Optional<Aluna> findByTelefone(String telefone);

    boolean existsByTelefone(String telefone);

    /** Alunas ativas, com opt-in de WhatsApp, cuja última atividade foi em {@code limite} ou antes. */
    List<Aluna> findByAtivaTrueAndAceitaWhatsappTrueAndUltimaAtividadeEmLessThanEqual(LocalDate limite);
}
