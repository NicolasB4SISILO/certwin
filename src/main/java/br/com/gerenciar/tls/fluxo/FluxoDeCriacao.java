package br.com.gerenciar.tls.fluxo;

import java.security.KeyPair;
import java.util.logging.Logger;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import br.com.gerenciar.tls.acme.AcmeClient;
import br.com.gerenciar.tls.acme.IssuedCertificate;
import br.com.gerenciar.tls.arquivos.FormatoDaSaida;
import br.com.gerenciar.tls.csr.CsrGerador;
import br.com.gerenciar.tls.exceptions.GeradorTlsException;
import br.com.gerenciar.tls.exceptions.PortaIndisponivelException;
import br.com.gerenciar.tls.key.KeyPairService;
import br.com.gerenciar.tls.network.ChecarDisponibilidadeDaPorta;
import br.com.gerenciar.tls.pem.CertificateBundleWriter;

public final class FluxoDeCriacao {
    private static final Logger LOG = Logger.getLogger(FluxoDeCriacao.class.getName());
    private final FormatoDaSaida formatoDaSaida;
    private final boolean staging;
    private final int PORTA_PADRAO = 80;

    public FluxoDeCriacao(FormatoDaSaida formatoDaSaida, boolean staging) {
        this.formatoDaSaida = formatoDaSaida;
        this.staging = staging;
    }

    public IssuedCertificate execute(String email, String dominio) throws GeradorTlsException {
        verificarPortaPadrao();
        
        LOG.info("Gerando chave RSA para " + dominio + "...");
        KeyPair keyPair = KeyPairService.geradorRsaKeyPair();
        PKCS10CertificationRequest csr = CsrGerador.gerador(dominio, keyPair);

        AcmeClient acmeclient = new AcmeClient(staging, formatoDaSaida);
        IssuedCertificate issued = acmeclient.issue(email, dominio, csr, keyPair);

        CertificateBundleWriter.write(formatoDaSaida.pemDir(dominio), issued.domainKeyPair().getPrivate(), issued.chain());
        LOG.info("Arquivos .pem gravados em: " + formatoDaSaida.pemDir(dominio));
        
        return issued;
    }

    private void verificarPortaPadrao() throws GeradorTlsException {
        if(!ChecarDisponibilidadeDaPorta.estaDisponivel(PORTA_PADRAO)) {
            throw new PortaIndisponivelException("A porta 80 ja esta em uso por outro processo. "
                    + "A validacao HTTP-01 do Let's Encrypt exige a porta 80 livre nesta maquina "
                    + "durante toda a execucao. Verifique com 'netstat -ano' (no Windows, tambem vale "
                    + "conferir 'netsh int ipv4 show excludedportrange protocol=tcp', que pode reservar "
                    + "faixas de porta mesmo sem um processo visivel usando-a).");
        };
    };
}
