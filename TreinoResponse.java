package br.com.estudioanaelizabeth.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.estudioanaelizabeth.domain.StatusTreino;
import br.com.estudioanaelizabeth.domain.TipoTreino;
import br.com.estudioanaelizabeth.domain.Treino;

public record TreinoResponse(
        Long id,
        Long alunaId,
        String titulo,
        String descricao,
        TipoTreino tipo,
        BigDecimal distanciaPrevistaKm,
        LocalDate dataPrevista,
        StatusTreino status,
        BigDecimal distanciaRealizadaKm,
        Integer duracaoMinutos,
        LocalDate concluidoEm) {

    public static TreinoResponse de(Treino t) {
        return new TreinoResponse(t.getId(), t.getAluna().getId(), t.getTitulo(), t.getDescricao(), t.getTipo(),
                t.getDistanciaPrevistaKm(), t.getDataPrevista(), t.getStatus(), t.getDistanciaRealizadaKm(),
                t.getDuracaoMinutos(), t.getConcluidoEm());
    }
}
