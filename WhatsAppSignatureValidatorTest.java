package br.com.estudioanaelizabeth.whatsapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import br.com.estudioanaelizabeth.config.WhatsAppProperties;

class WhatsAppSignatureValidatorTest {

    private static final String SEGREDO = "segredo-de-teste";
    private static final byte[] CORPO = "{\"entry\":[]}".getBytes(StandardCharsets.UTF_8);

    private final WhatsAppSignatureValidator validator = validatorCom(SEGREDO);

    @Test
    void aceitaAssinaturaCorreta() throws Exception {
        assertThat(validator.valida(CORPO, assinar(CORPO, SEGREDO))).isTrue();
    }

    @Test
    void rejeitaAssinaturaDeOutroSegredo() throws Exception {
        assertThat(validator.valida(CORPO, assinar(CORPO, "outro"))).isFalse();
    }

    @Test
    void rejeitaCorpoAdulterado() throws Exception {
        String assinatura = assinar(CORPO, SEGREDO);
        assertThat(validator.valida("{\"entry\":[1]}".getBytes(StandardCharsets.UTF_8), assinatura)).isFalse();
    }

    @Test
    void rejeitaCabecalhoAusenteOuMalFormado() {
        assertThat(validator.valida(CORPO, null)).isFalse();
        assertThat(validator.valida(CORPO, "sha256=zzzz")).isFalse();
        assertThat(validator.valida(CORPO, "abc")).isFalse();
    }

    @Test
    void rejeitaTudoQuandoAppSecretNaoEstaConfigurado() throws Exception {
        WhatsAppSignatureValidator semSegredo = validatorCom("");
        assertThat(semSegredo.valida(CORPO, assinar(CORPO, ""))).isFalse();
    }

    private static WhatsAppSignatureValidator validatorCom(String segredo) {
        return new WhatsAppSignatureValidator(
                new WhatsAppProperties("http://localhost", "id", "token", "verify", segredo, "tpl", "pt_BR"));
    }

    private static String assinar(byte[] corpo, String segredo) throws Exception {
        if (segredo.isEmpty()) {
            return "sha256=" + "00".repeat(32);
        }
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return "sha256=" + HexFormat.of().formatHex(mac.doFinal(corpo));
    }
}
