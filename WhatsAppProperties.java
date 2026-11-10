package br.com.estudioanaelizabeth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurações da WhatsApp Business Cloud API (Meta).
 * Todos os valores vêm de variáveis de ambiente (ver application.yml e .env.example).
 */
@ConfigurationProperties(prefix = "whatsapp")
public record WhatsAppProperties(
        String apiBaseUrl,
        String phoneNumberId,
        String accessToken,
        String verifyToken,
        String appSecret,
        String templateInatividade,
        String templateLanguage) {
}
