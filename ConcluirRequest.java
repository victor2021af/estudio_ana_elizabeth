package br.com.estudioanaelizabeth.app.data.model;

public class ConcluirRequest {
    public final double distanciaKm;
    public final int duracaoMinutos;

    public ConcluirRequest(double distanciaKm, int duracaoMinutos) {
        this.distanciaKm = distanciaKm;
        this.duracaoMinutos = duracaoMinutos;
    }
}
