package br.com.estudioanaelizabeth.service;

import static br.com.estudioanaelizabeth.domain.TipoTreino.INTERVALADO;
import static br.com.estudioanaelizabeth.domain.TipoTreino.LEVE;
import static br.com.estudioanaelizabeth.domain.TipoTreino.LONGAO;
import static br.com.estudioanaelizabeth.domain.TipoTreino.REGENERATIVO;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.estudioanaelizabeth.domain.Aluna;
import br.com.estudioanaelizabeth.domain.TipoTreino;
import br.com.estudioanaelizabeth.domain.Treino;

/**
 * Gera a semana de treinos de uma aluna a partir do nível e do objetivo escolhido.
 *
 * <p>ATENÇÃO: é uma regra simples, só para o esqueleto funcionar de ponta a ponta. A periodização
 * real (progressão semanal, semanas de descanso, provas-alvo) deve ser definida pela treinadora
 * e substituir esta classe.
 */
@Service
public class PlanoSemanalService {

    private record Sessao(DayOfWeek dia, TipoTreino tipo) {
    }

    /** @param segundaFeira primeiro dia da semana a ser planejada */
    public List<Treino> gerar(Aluna aluna, LocalDate segundaFeira) {
        double kmLongao = aluna.getObjetivo().getKmLongao() * aluna.getNivel().getFator();

        List<Treino> treinos = new ArrayList<>();
        for (Sessao sessao : sessoes(aluna.getNivel().getSessoesPorSemana())) {
            Treino treino = new Treino();
            treino.setAluna(aluna);
            treino.setTipo(sessao.tipo());
            treino.setTitulo(titulo(sessao.tipo()));
            treino.setDescricao(descricao(sessao.tipo()));
            treino.setDistanciaPrevistaKm(distancia(sessao.tipo(), kmLongao));
            treino.setDataPrevista(segundaFeira.with(TemporalAdjusters.nextOrSame(sessao.dia())));
            treinos.add(treino);
        }
        return treinos;
    }

    private static List<Sessao> sessoes(int quantidade) {
        return switch (quantidade) {
            case 3 -> List.of(
                    new Sessao(TUESDAY, LEVE),
                    new Sessao(THURSDAY, INTERVALADO),
                    new Sessao(SATURDAY, LONGAO));
            case 4 -> List.of(
                    new Sessao(MONDAY, LEVE),
                    new Sessao(WEDNESDAY, INTERVALADO),
                    new Sessao(FRIDAY, LEVE),
                    new Sessao(SUNDAY, LONGAO));
            default -> List.of(
                    new Sessao(MONDAY, LEVE),
                    new Sessao(TUESDAY, INTERVALADO),
                    new Sessao(THURSDAY, REGENERATIVO),
                    new Sessao(FRIDAY, LEVE),
                    new Sessao(SUNDAY, LONGAO));
        };
    }

    private static BigDecimal distancia(TipoTreino tipo, double kmLongao) {
        double proporcao = switch (tipo) {
            case LEVE -> 0.45;
            case INTERVALADO -> 0.5;
            case REGENERATIVO -> 0.3;
            case LONGAO -> 1.0;
        };
        return BigDecimal.valueOf(kmLongao * proporcao).setScale(1, RoundingMode.HALF_UP);
    }

    private static String titulo(TipoTreino tipo) {
        return switch (tipo) {
            case LEVE -> "Corrida leve";
            case INTERVALADO -> "Intervalado";
            case REGENERATIVO -> "Regenerativo";
            case LONGAO -> "Longão";
        };
    }

    private static String descricao(TipoTreino tipo) {
        return switch (tipo) {
            case LEVE -> "Ritmo confortável: você deve conseguir conversar durante todo o treino.";
            case INTERVALADO -> "10 min de aquecimento, 6 tiros de 2 min fortes com 2 min de trote, 5 min de desaquecimento.";
            case REGENERATIVO -> "Trote bem solto para ajudar na recuperação. Sem pressa.";
            case LONGAO -> "Distância mais longa em ritmo controlado. Hidrate-se e priorize terminar bem.";
        };
    }
}
