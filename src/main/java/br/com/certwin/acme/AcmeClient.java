package br.com.certwin.acme;

import java.security.KeyPair;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.Session;

import br.com.certwin.arquivos.FormatoDaSaida;
import br.com.certwin.exceptions.GeradorTlsException;

public final class AcmeClient {
    private static final String DIRETORIO_PRODUCAO = "https://acme-v02.api.letsencrypt.org/directory";
    private static final String DIRETORIO_STAGING = "https://acme-staging-v02.api.letsencrypt.org/directory";

    private final Session session;
    private final FormatoDaSaida formatoDaSaida;

    public AcmeClient(boolean staging, FormatoDaSaida formatoDaSaida) {
        this.session = new Session(staging ? DIRETORIO_STAGING : DIRETORIO_PRODUCAO);
        this.formatoDaSaida = formatoDaSaida;
    }

    public IssuedCertificate issue(
        String email, 
        String dominio, 
        PKCS10CertificationRequest csr,
        KeyPair domainKeyPair
    ) throws GeradorTlsException {
        Account account = AcmeAccount.pegarOuCriarConta(session, email, formatoDaSaida);
        return new AcmeOrder(account).request(dominio, csr, domainKeyPair);
    }
}
