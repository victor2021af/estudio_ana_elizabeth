package br.com.estudioanaelizabeth.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estudioanaelizabeth.config.InatividadeProperties;
import br.com.estudioanaelizabeth.domain.Aluna;
import br.com.estudioanaelizabeth.repository.AlunaRepository;
import br.com.estudioanaelizabeth.whatsapp.WhatsAppService;
import lombok.extern.slf4j.Slf4j;

/** Avisa pelo WhatsApp as alunas que ficaram {@code diasLimite} dias ou mais sem treinar. */
@Slf4j
@Service
public class InatividadeService {

    private final AlunaRepository alunaRepository;
    private final WhatsAppService whatsAppService;
    private final InatividadeProperties props;
    private final Clock clock;

    public InatividadeService(AlunaRepository alunaRepository, WhatsAppService whatsAppService,
            InatividadeProperties props, Clock clock) {
        this.alunaRepository = alunaRepository;
        this.whatsAppService = whatsAppService;
        this.props = props;
        this.clock = clock;
    }

    /** @return quantas alunas foram avisadas */
    @Transactional
    public int notificarInativas() {
        LocalDate hoje = LocalDate.now(clock);
        LocalDate limite = hoje.minusDays(props.diasLimite());

        List<Aluna> candidatas = alunaRepository
                .findByAtivaTrueAndAceitaWhatsappTrueAndUltimaAtividadeEmLessThanEqual(limite);

        int avisadas = 0;
        for (Aluna aluna : candidatas) {
            if (!deveAvisar(aluna, hoje)) {
                continue;
            }
            long dias = ChronoUnit.DAYS.between(aluna.getUltimaAtividadeEm(), hoje);
            try {
                whatsAppService.enviarAvisoInatividade(aluna, dias);
                aluna.setUltimoAvisoEm(hoje);
                avisadas++;
            } catch (RuntimeException e) {
                // Uma falha numa aluna não pode impedir o aviso das demais.
                log.error("Falha ao avisar a aluna id={}", aluna.getId(), e);
            }
        }
        log.info("Aviso de inatividade: {} candidata(s), {} avisada(s)", candidatas.size(), avisadas);
        return avisadas;
    }

    /** Evita mandar mensagem todo dia: só avisa de novo depois de {@code intervaloReavisoDias}. */
    private boolean deveAvisar(Aluna aluna, LocalDate hoje) {
        LocalDate ultimoAviso = aluna.getUltimoAvisoEm();
        if (ultimoAviso == null || ultimoAviso.isBefore(aluna.getUltimaAtividadeEm())) {
            return true;
        }
        return ChronoUnit.DAYS.between(ultimoAviso, hoje) >= props.intervaloReavisoDias();
    }
}
