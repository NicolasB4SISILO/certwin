package br.com.certwin.exceptions;

public final class AcmeValidacaoException extends GeradorTlsException {
    public AcmeValidacaoException(String mensagemUsuario) {
        super(ExitCodes.ACME_VALIDACAO_FALHOU, mensagemUsuario);
    }

    public AcmeValidacaoException(String mensagemUsuario, Throwable causa) {
        super(ExitCodes.ACME_VALIDACAO_FALHOU, mensagemUsuario, causa);
    }
}
