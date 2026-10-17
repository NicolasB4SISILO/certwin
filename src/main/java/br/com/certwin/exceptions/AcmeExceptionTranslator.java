package br.com.certwin.exceptions;

import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.exception.AcmeLazyLoadingException;
import org.shredzone.acme4j.exception.AcmeNetworkException;
import org.shredzone.acme4j.exception.AcmeProtocolException;
import org.shredzone.acme4j.exception.AcmeRateLimitedException;
import org.shredzone.acme4j.exception.AcmeServerException;
import org.shredzone.acme4j.exception.AcmeUnauthorizedException;
import org.shredzone.acme4j.exception.AcmeUserActionRequiredException;

public final class AcmeExceptionTranslator {
    private AcmeExceptionTranslator() {};

    public static GeradorTlsException translate(AcmeException e) {
        if(e instanceof AcmeNetworkException) {
            return new RedeException("Falha de rede ao falar com o Let's Encrypt: " + e.getMessage(), e);
        }

        if(e instanceof AcmeRateLimitedException rateLimited) {
            String retryAfter = rateLimited.getRetryAfter()
                    .map(instance -> " Tente novamente apos " + instance + ".")
                    .orElse("");

            return new AcmeServidorException("Limite de requisicoes do Let's Encrypt atingido."
                    + retryAfter + " Use --staging para testes repetido.", e);
        }

        if(e instanceof AcmeUserActionRequiredException) {
            return new AcmeServidorException("O Let's Encrypt exige uma acao manual"
                    + " antes de continuar" + e.getMessage(), e);
        }

        if(e instanceof AcmeUnauthorizedException) {
            return new AcmeServidorException("Nao autorizado pelo Let's Encrypt: "
                    + e.getMessage(), e);
        }

        if(e instanceof AcmeServerException) {
            return new AcmeServidorException("Erro do servidor Let's Encrypt: " 
                    + e.getMessage(), e);
        }

        return new AcmeServidorException("Erro ao falar com o Let's Encrypt: "
                + e.getMessage(), e);
    };

    public static GeradorTlsException translate(AcmeProtocolException e) {
        return new AcmeServidorException("Resposta inesperada do Let's Encrypt: " + e.getMessage(), e);
    }

    public static GeradorTlsException translate(AcmeLazyLoadingException e) {
        Throwable causa = e.getCause();

        if(causa instanceof AcmeException acmeCausa) {
            return translate(acmeCausa);
        }

        return new AcmeServidorException("Falha ao carregar um recurso ACME: "
                + e.getMessage(), e);
    }
}
