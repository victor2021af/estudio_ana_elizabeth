package br.com.estudioanaelizabeth.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.estudioanaelizabeth.service.TreinoService;
import br.com.estudioanaelizabeth.web.dto.ConcluirTreinoRequest;
import br.com.estudioanaelizabeth.web.dto.EvolucaoResponse;
import br.com.estudioanaelizabeth.web.dto.TreinoResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class TreinoController {

    private final TreinoService service;

    public TreinoController(TreinoService service) {
        this.service = service;
    }

    /** Gera os treinos da semana a partir do nível/objetivo da aluna. {@code semana} = qualquer data dela (opcional). */
    @PostMapping("/alunas/{alunaId}/treinos/semana")
    @ResponseStatus(HttpStatus.CREATED)
    public List<TreinoResponse> gerarSemana(
            @PathVariable Long alunaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semana) {
        return service.gerarSemana(alunaId, semana).stream().map(TreinoResponse::de).toList();
    }

    @GetMapping("/alunas/{alunaId}/treinos")
    public List<TreinoResponse> listar(@PathVariable Long alunaId) {
        return service.listar(alunaId).stream().map(TreinoResponse::de).toList();
    }

    @PostMapping("/treinos/{treinoId}/concluir")
    public TreinoResponse concluir(@PathVariable Long treinoId, @Valid @RequestBody ConcluirTreinoRequest request) {
        return TreinoResponse.de(service.concluir(treinoId, request));
    }

    @GetMapping("/alunas/{alunaId}/evolucao")
    public EvolucaoResponse evolucao(@PathVariable Long alunaId) {
        return service.evolucao(alunaId);
    }
}
