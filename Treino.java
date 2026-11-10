package br.com.estudioanaelizabeth.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "treinos")
@Getter
@Setter
@NoArgsConstructor
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluna_id")
    private Aluna aluna;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoTreino tipo;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal distanciaPrevistaKm;

    @Column(nullable = false)
    private LocalDate dataPrevista;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTreino status = StatusTreino.PENDENTE;

    // --- o que a aluna realmente fez (base da evolução) ---

    @Column(precision = 5, scale = 2)
    private BigDecimal distanciaRealizadaKm;

    private Integer duracaoMinutos;

    private LocalDate concluidoEm;
}
