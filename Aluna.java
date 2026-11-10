package br.com.estudioanaelizabeth.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alunas")
@Getter
@Setter
@NoArgsConstructor
public class Aluna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    /** Somente dígitos, com DDI (ex.: 5511999999999) — mesmo formato que o WhatsApp usa. */
    @Column(nullable = false, unique = true, length = 20)
    private String telefone;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelCorrida nivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ObjetivoCorrida objetivo;

    @Column(nullable = false)
    private boolean ativa = true;

    /** Consentimento (opt-in) para receber mensagens pelo WhatsApp. Sem isso, nada é enviado. */
    @Column(nullable = false)
    private boolean aceitaWhatsapp;

    /** Data do último treino concluído (ou do cadastro, se ainda não treinou). */
    @Column(nullable = false)
    private LocalDate ultimaAtividadeEm;

    private LocalDate ultimoAvisoEm;

    @Column(nullable = false)
    private LocalDate criadaEm;

    public String primeiroNome() {
        if (nome == null || nome.isBlank()) {
            return "";
        }
        return nome.trim().split("\\s+")[0];
    }
}
