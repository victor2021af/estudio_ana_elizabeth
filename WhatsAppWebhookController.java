package br.com.estudioanaelizabeth.whatsapp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.estudioanaelizabeth.config.WhatsAppProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * Webhook da WhatsApp Cloud API.
 *
 * <ul>
 *   <li>GET: verificação feita pela Meta ao cadastrar a URL (hub.challenge).</li>
 *   <li>POST: eventos (mensagens recebidas, status de entrega...), com assinatura HMAC.</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/webhook/whatsapp")
public class WhatsAppWebhookController {

    private final WhatsAppProperties props;
    private final WhatsAppSignatureValidator validator;
    private final WhatsAppWebhookService webhookService;

    public WhatsAppWebhookController(WhatsAppProperties props, WhatsAppSignatureValidator validator,
            WhatsAppWebhookService webhookService) {
        this.props = props;
        this.validator = validator;
        this.webhookService = webhookService;
    }

    @GetMapping
    public ResponseEntity<String> verificar(
            @RequestParam("hub.mode") String modo,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String desafio) {

        String esperado = props.verifyToken();
        boolean tokenOk = esperado != null && !esperado.isBlank()
                && MessageDigest.isEqual(esperado.getBytes(StandardCharsets.UTF_8),
                        token.getBytes(StandardCharsets.UTF_8));

        if ("subscribe".equals(modo) && tokenOk) {
            return ResponseEntity.ok(desafio);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public ResponseEntity<Void> receber(
            @RequestBody byte[] corpo,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String assinatura) {

        if (!validator.valida(corpo, assinatura)) {
            log.warn("Webhook do WhatsApp rejeitado: assinatura ausente ou inválida");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            webhookService.processar(corpo);
        } catch (Exception e) {
            // Responder 200 mesmo assim evita que a Meta fique reenviando o mesmo evento.
            log.error("Erro ao processar evento do WhatsApp", e);
        }
        return ResponseEntity.ok().build();
    }
}
