package br.com.estudioanaelizabeth.domain;

public enum NivelCorrida {

    INICIANTE(0.8, 3),
    INTERMEDIARIO(1.0, 4),
    AVANCADO(1.2, 5);

    private final double fator;
    private final int sessoesPorSemana;

    NivelCorrida(double fator, int sessoesPorSemana) {
        this.fator = fator;
        this.sessoesPorSemana = sessoesPorSemana;
    }

    /** Multiplicador aplicado às distâncias do plano. */
    public double getFator() {
        return fator;
    }

    public int getSessoesPorSemana() {
        return sessoesPorSemana;
    }
}
