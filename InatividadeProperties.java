package br.com.estudioanaelizabeth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Regras do aviso de inatividade.
 *
 * @param diasLimite          a partir de quantos dias sem treinar a aluna é avisada (padrão: 3)
 * @param intervaloReavisoDias de quantos em quantos dias avisar de novo se ela continuar parada
 */
@ConfigurationProperties(prefix = "estudio.inatividade")
public record InatividadeProperties(int diasLimite, int intervaloReavisoDias) {
}
