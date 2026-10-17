package br.com.certwin.exceptions;

public final class ArquivoException extends GeradorTlsException {
    public ArquivoException(String mensagemUsuario) {
        super(ExitCodes.ERRO_ARQUIVO, mensagemUsuario);
    }

    public ArquivoException(String mensagemUsuario, Throwable causa) {
        super(ExitCodes.ERRO_ARQUIVO, mensagemUsuario, causa);
    }
}
