package br.com.estudioanaelizabeth.whatsapp;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.estudioanaelizabeth.service.AlunaService;
import lombok.extern.slf4j.Slf4j;

/** Interpreta os eventos recebidos do WhatsApp. */
@Slf4j
@Service
public class WhatsAppWebhookService {

    private final ObjectMapper objectMapper;
    private final AlunaService alunaService;
    private final WhatsAppService whatsAppService;

    public WhatsAppWebhookService(ObjectMapper objectMapper, AlunaService alunaService,
            WhatsAppService whatsAppService) {
        this.objectMapper = objectMapper;
        this.alunaService = alunaService;
        this.whatsAppService = whatsAppService;
    }

    public void processar(byte[] corpo) throws IOException {
        JsonNode raiz = objectMapper.readTree(corpo);
        for (JsonNode entrada : raiz.path("entry")) {
            for (JsonNode mudanca : entrada.path("changes")) {
                for (JsonNode mensagem : mudanca.path("value").path("messages")) {
                    tratarMensagem(
                            mensagem.path("from").asText(""),
                            mensagem.path("text").path("body").asText(""));
                }
            }
        }
    }

    private void tratarMensagem(String de, String texto) {
        if (de.isBlank()) {
            return;
        }
        // Fluxo de exemplo: a aluna responde "TREINEI" e a contagem de inatividade é zerada.
        if (normalizar(texto).contains("treinei")) {
            alunaService.registrarAtividadePorTelefone(de).ifPresentOrElse(
                    aluna -> whatsAppService.enviarTexto(de,
                            "Boa, " + aluna.primeiroNome() + "! Treino registrado. Continue assim! 🏃‍♀️"),
                    () -> log.info("Mensagem de número não cadastrado (final {})", finalDoNumero(de)));
        } else {
            log.info("Mensagem sem comando reconhecido (final {})", finalDoNumero(de));
        }
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    /** Não registramos o telefone inteiro nos logs (dado pessoal). */
    private static String finalDoNumero(String telefone) {
        return telefone.length() <= 4 ? "****" : "****" + telefone.substring(telefone.length() - 4);
    }
}
