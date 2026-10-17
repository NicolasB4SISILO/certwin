package br.com.certwin.exceptions;

public final class PortaIndisponivelException extends GeradorTlsException {
    public PortaIndisponivelException(String mensagemUsuario) {
        super(ExitCodes.PORTA_INDISPONIVEL, mensagemUsuario);
    }
}
