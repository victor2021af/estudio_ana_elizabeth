package br.com.estudioanaelizabeth.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.estudioanaelizabeth.service.InatividadeService;

/**
 * Dispara a verificação diária de inatividade.
 *
 * <p>Se um dia o backend rodar em mais de uma instância, use um lock distribuído (ex.: ShedLock)
 * para que o job não rode em duplicidade.
 */
@Component
public class InatividadeScheduler {

    private final InatividadeService inatividadeService;

    public InatividadeScheduler(InatividadeService inatividadeService) {
        this.inatividadeService = inatividadeService;
    }

    @Scheduled(cron = "${estudio.inatividade.cron}", zone = "${estudio.timezone}")
    public void executar() {
        inatividadeService.notificarInativas();
    }
}
