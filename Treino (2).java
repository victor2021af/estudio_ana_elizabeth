package br.com.estudioanaelizabeth.app.data.model;

/** Espelha o TreinoResponse do backend. */
public class Treino {
    public long id;
    public long alunaId;
    public String titulo;
    public String descricao;
    public String tipo;
    public Double distanciaPrevistaKm;
    public String dataPrevista;      // "2026-11-10"
    public String status;            // PENDENTE | CONCLUIDO | PULADO
    public Double distanciaRealizadaKm;
    public Integer duracaoMinutos;
    public String concluidoEm;

    public boolean isConcluido() {
        return "CONCLUIDO".equals(status);
    }
}
