package br.com.gerenciar.tls.acme;

import java.security.KeyPair;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.Session;

import br.com.gerenciar.tls.arquivos.FormatoDaSaida;
import br.com.gerenciar.tls.exceptions.GeradorTlsException;
import br.com.gerenciar.tls.exceptions.PortaIndisponivelException;
import br.com.gerenciar.tls.network.ChecarDisponibilidadeDaPorta;

public final class AcmeClient {
    private static final String DIRETORIO_PRODUCAO = "https://acme-v02.api.letsencrypt.org/directory";
    private static final String DIRETORIO_STAGING = "https://acme-staging-v02.api.letsencrypt.org/directory";
    private static final int PORTA_DESAFIO = 80;

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
        if(!ChecarDisponibilidadeDaPorta.estaDisponivel(PORTA_DESAFIO)) {
            throw new PortaIndisponivelException("A porta 80 ja esta em uso por outro processo. "
                    + "A validacao HTTP-01 do Let's Encrypt exige a porta 80 livre nesta maquina "
                    + "durante toda a execucao. Verifique com 'netstat -ano' (no Windows, tambem vale "
                    + "conferir 'netsh int ipv4 show excludedportrange protocol=tcp', que pode reservar "
                    + "faixas de porta mesmo sem um processo visivel usando-a).");
        }

        Account account = AcmeAccount.pegarOuCriarConta(session, email, formatoDaSaida);
        return new AcmeOrder(account).request(dominio, csr, domainKeyPair);
    }
}
