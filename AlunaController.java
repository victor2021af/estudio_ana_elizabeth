package br.com.estudioanaelizabeth.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.estudioanaelizabeth.service.AlunaService;
import br.com.estudioanaelizabeth.web.dto.AlunaRequest;
import br.com.estudioanaelizabeth.web.dto.AlunaResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/alunas")
public class AlunaController {

    private final AlunaService service;

    public AlunaController(AlunaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlunaResponse criar(@Valid @RequestBody AlunaRequest request) {
        return AlunaResponse.de(service.criar(request));
    }

    @GetMapping
    public List<AlunaResponse> listar() {
        return service.listar().stream().map(AlunaResponse::de).toList();
    }

    @GetMapping("/{id}")
    public AlunaResponse buscar(@PathVariable Long id) {
        return AlunaResponse.de(service.buscar(id));
    }
}
