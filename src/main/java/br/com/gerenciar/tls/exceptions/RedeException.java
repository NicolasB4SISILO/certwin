package br.com.gerenciar.tls.exceptions;

public final class RedeException extends GeradorTlsException {
    public RedeException(String mensagemUsuario, Throwable causa) {
        super(ExitCodes.ERRO_REDE, mensagemUsuario, causa);
    }
}
