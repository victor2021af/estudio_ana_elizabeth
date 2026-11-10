package br.com.estudioanaelizabeth.web.dto;

import java.time.LocalDate;

import br.com.estudioanaelizabeth.domain.Aluna;
import br.com.estudioanaelizabeth.domain.NivelCorrida;
import br.com.estudioanaelizabeth.domain.ObjetivoCorrida;

public record AlunaResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        NivelCorrida nivel,
        ObjetivoCorrida objetivo,
        boolean ativa,
        boolean aceitaWhatsapp,
        LocalDate ultimaAtividadeEm) {

    public static AlunaResponse de(Aluna a) {
        return new AlunaResponse(a.getId(), a.getNome(), a.getTelefone(), a.getEmail(), a.getNivel(),
                a.getObjetivo(), a.isAtiva(), a.isAceitaWhatsapp(), a.getUltimaAtividadeEm());
    }
}
