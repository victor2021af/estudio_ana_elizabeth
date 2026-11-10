package br.com.estudioanaelizabeth.whatsapp;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import br.com.estudioanaelizabeth.config.WhatsAppProperties;

/**
 * Valida o cabeçalho {@code X-Hub-Signature-256} que a Meta envia em cada chamada ao webhook
 * (HMAC-SHA256 do corpo bruto usando o "App Secret"). Sem isso, qualquer pessoa poderia forjar mensagens.
 */
@Component
public class WhatsAppSignatureValidator {

    private static final String PREFIXO = "sha256=";
    private static final String ALGORITMO = "HmacSHA256";

    private final WhatsAppProperties props;

    public WhatsAppSignatureValidator(WhatsAppProperties props) {
        this.props = props;
    }

    public boolean valida(byte[] corpo, String cabecalho) {
        String segredo = props.appSecret();
        if (segredo == null || segredo.isBlank() || cabecalho == null || !cabecalho.startsWith(PREFIXO)) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance(ALGORITMO);
            mac.init(new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), ALGORITMO));
            byte[] esperado = mac.doFinal(corpo);
            byte[] recebido = HexFormat.of().parseHex(cabecalho.substring(PREFIXO.length()));
            return MessageDigest.isEqual(esperado, recebido);
        } catch (IllegalArgumentException e) {
            return false; // hexadecimal inválido
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao calcular HMAC", e);
        }
    }
}
