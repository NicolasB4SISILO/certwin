package br.com.certwin.exceptions;

public class ArgumentosInvalidosException extends GeradorTlsException{
    public ArgumentosInvalidosException(String mensagemUsuario) {
        super(ExitCodes.ARGUMENTOS_INVALIDOS, mensagemUsuario);
    }
}
