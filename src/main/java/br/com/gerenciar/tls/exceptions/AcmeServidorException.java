package br.com.gerenciar.tls.exceptions;

public final class AcmeServidorException extends GeradorTlsException {
    public AcmeServidorException(String mensagemUsuario, Throwable causa) {
        super(ExitCodes.ACME_SERVIDOR_ERRO, mensagemUsuario, causa);
    }
}
