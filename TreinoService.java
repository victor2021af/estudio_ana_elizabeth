package br.com.estudioanaelizabeth.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estudioanaelizabeth.domain.Aluna;
import br.com.estudioanaelizabeth.domain.StatusTreino;
import br.com.estudioanaelizabeth.domain.Treino;
import br.com.estudioanaelizabeth.exception.RecursoNaoEncontradoException;
import br.com.estudioanaelizabeth.repository.TreinoRepository;
import br.com.estudioanaelizabeth.web.dto.ConcluirTreinoRequest;
import br.com.estudioanaelizabeth.web.dto.EvolucaoResponse;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final AlunaService alunaService;
    private final PlanoSemanalService planoSemanalService;
    private final Clock clock;

    public TreinoService(TreinoRepository treinoRepository, AlunaService alunaService,
            PlanoSemanalService planoSemanalService, Clock clock) {
        this.treinoRepository = treinoRepository;
        this.alunaService = alunaService;
        this.planoSemanalService = planoSemanalService;
        this.clock = clock;
    }

    /** Gera e salva os treinos da semana (a que contém {@code referencia}; padrão: semana atual). */
    @Transactional
    public List<Treino> gerarSemana(Long alunaId, LocalDate referencia) {
        Aluna aluna = alunaService.buscar(alunaId);
        LocalDate base = referencia != null ? referencia : LocalDate.now(clock);
        LocalDate segunda = base.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return treinoRepository.saveAll(planoSemanalService.gerar(aluna, segunda));
    }

    @Transactional(readOnly = true)
    public List<Treino> listar(Long alunaId) {
        alunaService.buscar(alunaId);
        return treinoRepository.findByAlunaIdOrderByDataPrevistaDesc(alunaId);
    }

    @Transactional
    public Treino concluir(Long treinoId, ConcluirTreinoRequest request) {
        Treino treino = treinoRepository.findById(treinoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Treino", treinoId));
        LocalDate hoje = LocalDate.now(clock);

        treino.setStatus(StatusTreino.CONCLUIDO);
        treino.setDistanciaRealizadaKm(request.distanciaKm());
        treino.setDuracaoMinutos(request.duracaoMinutos());
        treino.setConcluidoEm(hoje);

        // Treinou: zera a contagem de inatividade.
        treino.getAluna().setUltimaAtividadeEm(hoje);
        return treino;
    }

    @Transactional(readOnly = true)
    public EvolucaoResponse evolucao(Long alunaId) {
        alunaService.buscar(alunaId);
        List<Treino> concluidos = treinoRepository.findByAlunaIdAndStatus(alunaId, StatusTreino.CONCLUIDO);

        BigDecimal totalKm = BigDecimal.ZERO;
        int totalMinutos = 0;
        for (Treino t : concluidos) {
            if (t.getDistanciaRealizadaKm() != null) {
                totalKm = totalKm.add(t.getDistanciaRealizadaKm());
            }
            if (t.getDuracaoMinutos() != null) {
                totalMinutos += t.getDuracaoMinutos();
            }
        }

        BigDecimal pace = null;
        if (totalKm.signum() > 0) {
            pace = BigDecimal.valueOf(totalMinutos).divide(totalKm, 2, RoundingMode.HALF_UP);
        }
        return new EvolucaoResponse(alunaId, concluidos.size(), totalKm, totalMinutos, pace);
    }
}
