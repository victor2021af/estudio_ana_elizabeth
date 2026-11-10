package br.com.estudioanaelizabeth.web.dto;

import br.com.estudioanaelizabeth.domain.NivelCorrida;
import br.com.estudioanaelizabeth.domain.ObjetivoCorrida;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlunaRequest(
        @NotBlank String nome,
        @NotBlank String telefone,
        @Email String email,
        @NotNull NivelCorrida nivel,
        @NotNull ObjetivoCorrida objetivo,
        boolean aceitaWhatsapp) {
}
