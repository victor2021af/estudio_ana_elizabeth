package br.com.estudioanaelizabeth.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.estudioanaelizabeth.domain.Aluna;
import br.com.estudioanaelizabeth.domain.NivelCorrida;
import br.com.estudioanaelizabeth.domain.ObjetivoCorrida;
import br.com.estudioanaelizabeth.domain.TipoTreino;
import br.com.estudioanaelizabeth.domain.Treino;

class PlanoSemanalServiceTest {

    private static final LocalDate SEGUNDA = LocalDate.of(2026, 11, 9);

    private final PlanoSemanalService service = new PlanoSemanalService();

    @Test
    void inicianteDeCincoKmTemTresTreinosELongaoNoSabado() {
        List<Treino> treinos = service.gerar(aluna(NivelCorrida.INICIANTE, ObjetivoCorrida.PRIMEIROS_5K), SEGUNDA);

        assertThat(treinos).hasSize(3);
        Treino longao = treinos.get(2);
        assertThat(longao.getTipo()).isEqualTo(TipoTreino.LONGAO);
        assertThat(longao.getDataPrevista()).isEqualTo(LocalDate.of(2026, 11, 14));
        assertThat(longao.getDistanciaPrevistaKm()).isEqualByComparingTo("4.0");
    }

    @Test
    void avancadoTemCincoTreinosEDistanciasMaiores() {
        List<Treino> treinos = service.gerar(aluna(NivelCorrida.AVANCADO, ObjetivoCorrida.MEIA_MARATONA), SEGUNDA);

        assertThat(treinos).hasSize(5);
        Treino longao = treinos.get(4);
        assertThat(longao.getDataPrevista()).isEqualTo(LocalDate.of(2026, 11, 15));
        assertThat(longao.getDistanciaPrevistaKm()).isEqualByComparingTo("16.8");
    }

    private static Aluna aluna(NivelCorrida nivel, ObjetivoCorrida objetivo) {
        Aluna aluna = new Aluna();
        aluna.setNome("Teste");
        aluna.setNivel(nivel);
        aluna.setObjetivo(objetivo);
        return aluna;
    }
}
