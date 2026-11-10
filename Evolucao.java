package br.com.estudioanaelizabeth.app.data.model;

/** Espelha o EvolucaoResponse do backend. */
public class Evolucao {
    public long alunaId;
    public int totalTreinosConcluidos;
    public double totalKm;
    public int totalMinutos;
    public Double paceMedioMinPorKm; // null se ainda não há treinos concluídos
}
