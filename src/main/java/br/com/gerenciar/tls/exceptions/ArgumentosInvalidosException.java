package br.com.gerenciar.tls.exceptions;

public class ArgumentosInvalidosException extends GeradorTlsException{
    public ArgumentosInvalidosException(String mensagemUsuario) {
        super(ExitCodes.ARGUMENTOS_INVALIDOS, mensagemUsuario);
    }
}
