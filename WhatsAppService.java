package br.com.estudioanaelizabeth.whatsapp;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.estudioanaelizabeth.config.WhatsAppProperties;
import br.com.estudioanaelizabeth.domain.Aluna;
import lombok.extern.slf4j.Slf4j;

/** Envio de mensagens pela WhatsApp Business Cloud API. */
@Slf4j
@Service
public class WhatsAppService {

    private final WhatsAppProperties props;
    private final RestClient client;

    public WhatsAppService(WhatsAppProperties props, RestClient.Builder builder) {
        this.props = props;
        this.client = builder.baseUrl(props.apiBaseUrl()).build();
    }

    /**
     * Aviso de inatividade. Como é uma mensagem iniciada pelo estúdio (fora da janela de 24h),
     * o WhatsApp exige um TEMPLATE previamente aprovado. Corpo esperado do template, com 2 variáveis:
     * "Oi, {{1}}! Faz {{2}} dias que você não treina por aqui. ..."
     */
    public void enviarAvisoInatividade(Aluna aluna, long diasSemTreinar) {
        if (!configurado()) {
            log.warn("WhatsApp não configurado (token/phone-number-id vazios); aviso para aluna id={} ignorado",
                    aluna.getId());
            return;
        }

        Map<String, Object> corpo = Map.of("type", "body", "parameters", List.of(
                Map.of("type", "text", "text", aluna.primeiroNome()),
                Map.of("type", "text", "text", String.valueOf(diasSemTreinar))));

        Map<String, Object> template = Map.of(
                "name", props.templateInatividade(),
                "language", Map.of("code", props.templateLanguage()),
                "components", List.of(corpo));

        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", aluna.getTelefone(),
                "type", "template",
                "template", template);

        enviar(payload);
    }

    /** Texto livre. Só funciona dentro de 24h após a última mensagem da aluna (ex.: resposta a um "TREINEI"). */
    public void enviarTexto(String telefone, String texto) {
        if (!configurado()) {
            log.warn("WhatsApp não configurado; texto não enviado");
            return;
        }
        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", telefone,
                "type", "text",
                "text", Map.of("body", texto));
        enviar(payload);
    }

    private void enviar(Map<String, Object> payload) {
        client.post()
                .uri("/{phoneNumberId}/messages", props.phoneNumberId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + props.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    private boolean configurado() {
        return props.accessToken() != null && !props.accessToken().isBlank()
                && props.phoneNumberId() != null && !props.phoneNumberId().isBlank();
    }
}
