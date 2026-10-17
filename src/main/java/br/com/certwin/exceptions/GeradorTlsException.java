package br.com.certwin.exceptions;

public abstract class GeradorTlsException extends Exception {
    private final int exitCode;

    protected GeradorTlsException(int exitCode, String mensagemUsuario) {
        super(mensagemUsuario);
        this.exitCode = exitCode;
    }

    protected GeradorTlsException(int exitCode, String mensagemUsuario, Throwable causa) {
        super(mensagemUsuario, causa);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
