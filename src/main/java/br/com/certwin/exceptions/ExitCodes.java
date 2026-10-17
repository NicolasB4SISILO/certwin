package br.com.certwin.exceptions;

public final class ExitCodes {
    private ExitCodes() {};

    public static final int SUCESSO = 0;
    public static final int ARGUMENTOS_INVALIDOS = 1;
    public static final int PORTA_INDISPONIVEL = 2;
    public static final int ACME_VALIDACAO_FALHOU = 3;
    public static final int ACME_SERVIDOR_ERRO = 4;
    public static final int ERRO_REDE = 5;
    public static final int ERRO_ARQUIVO = 6;
    public static final int ERRO_INESPERADO = 99;
}
