package br.com.estudioanaelizabeth.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estudioanaelizabeth.domain.Aluna;
import br.com.estudioanaelizabeth.exception.ConflitoException;
import br.com.estudioanaelizabeth.exception.RecursoNaoEncontradoException;
import br.com.estudioanaelizabeth.repository.AlunaRepository;
import br.com.estudioanaelizabeth.web.dto.AlunaRequest;

@Service
public class AlunaService {

    private final AlunaRepository repository;
    private final Clock clock;

    public AlunaService(AlunaRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public Aluna criar(AlunaRequest request) {
        String telefone = normalizarTelefone(request.telefone());
        if (repository.existsByTelefone(telefone)) {
            throw new ConflitoException("Já existe uma aluna cadastrada com este telefone");
        }
        LocalDate hoje = LocalDate.now(clock);

        Aluna aluna = new Aluna();
        aluna.setNome(request.nome().trim());
        aluna.setTelefone(telefone);
        aluna.setEmail(request.email());
        aluna.setNivel(request.nivel());
        aluna.setObjetivo(request.objetivo());
        aluna.setAceitaWhatsapp(request.aceitaWhatsapp());
        aluna.setCriadaEm(hoje);
        aluna.setUltimaAtividadeEm(hoje);
        return repository.save(aluna);
    }

    @Transactional(readOnly = true)
    public List<Aluna> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Aluna buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluna", id));
    }

    /** Marca que a aluna treinou hoje (usado quando ela responde "TREINEI" no WhatsApp). */
    @Transactional
    public Optional<Aluna> registrarAtividadePorTelefone(String telefone) {
        return buscarPorTelefoneWhatsApp(telefone).map(aluna -> {
            aluna.setUltimaAtividadeEm(LocalDate.now(clock));
            return aluna;
        });
    }

    /**
     * Busca pelo número que chega no webhook. Números brasileiros podem chegar com ou sem o 9º dígito,
     * então tentamos as duas variações.
     */
    @Transactional(readOnly = true)
    public Optional<Aluna> buscarPorTelefoneWhatsApp(String telefone) {
        String t = telefone == null ? "" : telefone.replaceAll("\\D", "");
        Optional<Aluna> direto = repository.findByTelefone(t);
        if (direto.isPresent()) {
            return direto;
        }
        if (t.startsWith("55") && t.length() == 12) {
            // 55 + DDD + 8 dígitos -> tenta com o 9 inserido
            return repository.findByTelefone(t.substring(0, 4) + "9" + t.substring(4));
        }
        if (t.startsWith("55") && t.length() == 13) {
            // 55 + DDD + 9 + 8 dígitos -> tenta sem o 9
            return repository.findByTelefone(t.substring(0, 4) + t.substring(5));
        }
        return Optional.empty();
    }

    /** Deixa só dígitos; assume Brasil (55) se o DDI não foi informado. */
    static String normalizarTelefone(String bruto) {
        String digitos = bruto == null ? "" : bruto.replaceAll("\\D", "");
        if (digitos.length() == 10 || digitos.length() == 11) {
            digitos = "55" + digitos;
        }
        if (digitos.length() < 12 || digitos.length() > 15) {
            throw new IllegalArgumentException("Telefone inválido. Use DDI + DDD + número, ex.: 5511999999999");
        }
        return digitos;
    }
}
