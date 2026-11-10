package br.com.estudioanaelizabeth.domain;

/** Performance/meta escolhida pela aluna. */
public enum ObjetivoCorrida {

    CONDICIONAMENTO(5),
    PRIMEIROS_5K(5),
    DEZ_K(8),
    MEIA_MARATONA(14),
    MARATONA(24);

    private final int kmLongao;

    ObjetivoCorrida(int kmLongao) {
        this.kmLongao = kmLongao;
    }

    /** Distância de referência do treino longo (antes do ajuste por nível). */
    public int getKmLongao() {
        return kmLongao;
    }
}
