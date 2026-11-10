package br.com.estudioanaelizabeth.web.dto;

import java.math.BigDecimal;

/**
 * Resumo da evolução da aluna.
 *
 * @param paceMedioMinPorKm minutos por km (média geral); {@code null} se ainda não há treinos concluídos
 */
public record EvolucaoResponse(
        Long alunaId,
        int totalTreinosConcluidos,
        BigDecimal totalKm,
        int totalMinutos,
        BigDecimal paceMedioMinPorKm) {
}
