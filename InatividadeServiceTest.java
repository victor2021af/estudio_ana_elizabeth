package br.com.estudioanaelizabeth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.estudioanaelizabeth.config.InatividadeProperties;
import br.com.estudioanaelizabeth.domain.Aluna;
import br.com.estudioanaelizabeth.repository.AlunaRepository;
import br.com.estudioanaelizabeth.whatsapp.WhatsAppService;

@ExtendWith(MockitoExtension.class)
class InatividadeServiceTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 10);
    private static final LocalDate LIMITE = HOJE.minusDays(3);

    @Mock
    private AlunaRepository alunaRepository;

    @Mock
    private WhatsAppService whatsAppService;

    private InatividadeService service;

    @BeforeEach
    void setUp() {
        // 12:00 UTC = 09:00 em São Paulo, ainda dia 10/11/2026
        Clock clock = Clock.fixed(Instant.parse("2026-11-10T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        service = new InatividadeService(alunaRepository, whatsAppService, new InatividadeProperties(3, 3), clock);
    }

    @Test
    void avisaAlunaQueEstaHaTresDiasSemTreinar() {
        Aluna aluna = aluna(HOJE.minusDays(3), null);
        when(alunaRepository.findByAtivaTrueAndAceitaWhatsappTrueAndUltimaAtividadeEmLessThanEqual(LIMITE))
                .thenReturn(List.of(aluna));

        int avisadas = service.notificarInativas();

        assertThat(avisadas).isEqualTo(1);
        verify(whatsAppService).enviarAvisoInatividade(aluna, 3L);
        assertThat(aluna.getUltimoAvisoEm()).isEqualTo(HOJE);
    }

    @Test
    void naoAvisaDeNovoAntesDoIntervaloDeReaviso() {
        Aluna aluna = aluna(HOJE.minusDays(9), HOJE.minusDays(1));
        when(alunaRepository.findByAtivaTrueAndAceitaWhatsappTrueAndUltimaAtividadeEmLessThanEqual(LIMITE))
                .thenReturn(List.of(aluna));

        int avisadas = service.notificarInativas();

        assertThat(avisadas).isZero();
        verifyNoInteractions(whatsAppService);
    }

    @Test
    void avisaDeNovoDepoisDoIntervaloDeReaviso() {
        Aluna aluna = aluna(HOJE.minusDays(9), HOJE.minusDays(3));
        when(alunaRepository.findByAtivaTrueAndAceitaWhatsappTrueAndUltimaAtividadeEmLessThanEqual(LIMITE))
                .thenReturn(List.of(aluna));

        int avisadas = service.notificarInativas();

        assertThat(avisadas).isEqualTo(1);
        verify(whatsAppService).enviarAvisoInatividade(aluna, 9L);
    }

    @Test
    void falhaNoEnvioNaoImpedeAvisoDasDemais() {
        Aluna primeira = aluna(HOJE.minusDays(4), null);
        Aluna segunda = aluna(HOJE.minusDays(5), null);
        when(alunaRepository.findByAtivaTrueAndAceitaWhatsappTrueAndUltimaAtividadeEmLessThanEqual(LIMITE))
                .thenReturn(List.of(primeira, segunda));
        doThrow(new RuntimeException("falha de rede"))
                .when(whatsAppService).enviarAvisoInatividade(primeira, 4L);

        int avisadas = service.notificarInativas();

        assertThat(avisadas).isEqualTo(1);
        assertThat(primeira.getUltimoAvisoEm()).isNull();
        assertThat(segunda.getUltimoAvisoEm()).isEqualTo(HOJE);
        verify(whatsAppService).enviarAvisoInatividade(segunda, 5L);
    }

    private static Aluna aluna(LocalDate ultimaAtividade, LocalDate ultimoAviso) {
        Aluna aluna = new Aluna();
        aluna.setNome("Maria Silva");
        aluna.setTelefone("5511999999999");
        aluna.setAceitaWhatsapp(true);
        aluna.setUltimaAtividadeEm(ultimaAtividade);
        aluna.setUltimoAvisoEm(ultimoAviso);
        return aluna;
    }
}
