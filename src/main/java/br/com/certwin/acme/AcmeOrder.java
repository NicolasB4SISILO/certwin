package br.com.certwin.acme;

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Problem;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.exception.AcmeLazyLoadingException;
import org.shredzone.acme4j.exception.AcmeProtocolException;

import br.com.certwin.csr.CsrGerador;
import br.com.certwin.exceptions.AcmeExceptionTranslator;
import br.com.certwin.exceptions.AcmeValidacaoException;
import br.com.certwin.exceptions.GeradorTlsException;

public final class AcmeOrder {
    private static final Logger LOG = Logger.getLogger(AcmeOrder.class.getName());
    private static final int PORTA_DESAFIO = 80;
    private static final Duration TIMEOUT_ESPERA = Duration.ofSeconds(30);

    private final Account account;

    public AcmeOrder(Account account) {
        this.account = account;
    }

    public IssuedCertificate request(
        String dominio, 
        PKCS10CertificationRequest csr, 
        KeyPair domainDKeyPair
    ) throws GeradorTlsException {
        try {
            LOG.info("Criando pedido de certificado para " + dominio + "...");
            Order order = account.newOrder().domain(dominio).create();
            Authorization authorization = order.getAuthorizations().get(0);
            Http01Challenge challenge = authorization.findChallenge(Http01Challenge.class)
                    .orElseThrow(() -> new AcmeValidacaoException(
                        "O Let's Encrypt nao ofereceu um desafio HTTP-01 para " + dominio + "."
                    ));
            
            validarChallenge(dominio, challenge);
            finalizarOrder(dominio, order, csr);

            List<X509Certificate> chain = order.getCertificate().getCertificateChain();
            LOG.info("Certificado emitido com sucesso para " + dominio + ".");
            return new IssuedCertificate(domainDKeyPair, chain);
        } catch (AcmeException e) {
            throw AcmeExceptionTranslator.translate(e);
        } catch (AcmeProtocolException e) {
            throw AcmeExceptionTranslator.translate(e);
        } catch (AcmeLazyLoadingException e) {
            throw AcmeExceptionTranslator.translate(e);
        }
    } 

    public void validarChallenge(
        String dominio,
        Http01Challenge challenge
    ) throws AcmeException, AcmeValidacaoException {
        try(StandaloneChallengeServer server = 
                new StandaloneChallengeServer(PORTA_DESAFIO, challenge.getToken(), challenge.getAuthorization())
            ) {
                server.iniciar();
                challenge.trigger();

                Status status = challenge.waitForCompletion(TIMEOUT_ESPERA);
                if(status != Status.VALID) {
                    throw new AcmeValidacaoException(buildValidationFailureMessage(status, challenge.getError(), dominio));
                }
            } catch(IOException e) {
                throw new AcmeValidacaoException("Nao foi possivel iniciar o servidor de desafio na porta"
                        + PORTA_DESAFIO + ": " + e.getMessage());
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AcmeValidacaoException("A espera pela validacao do desafio foi interrompida.", e);
            }
    }

    private void finalizarOrder(
        String dominio, 
        Order order, 
        PKCS10CertificationRequest csr
    ) throws AcmeException, AcmeValidacaoException {
        order.execute(CsrGerador.toDer(csr));

        Status status;
        try {
            status = order.waitForCompletion(TIMEOUT_ESPERA);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AcmeValidacaoException("A espera pela finalizacao do pedido foi interrompida.", e);
        }

        if(status != Status.VALID) {
            throw new AcmeValidacaoException(buildValidationFailureMessage(status, order.getError(), dominio));
        }
    }

    static String buildValidationFailureMessage(Status status, Optional<Problem> erro, String dominio) {
        String detalhe = erro.map(Problem::toString).orElse("Sem detalhes adicionais do Let's Encrypt.");
        return "Validacao do dominio " + dominio + " falhou (status " + status + "). " + detalhe
                + " Verifique se o dominio aponta para este servidor e se a porta 80 esta acessivel "
                + "pela internet (nao so localmente).";
    }
}
