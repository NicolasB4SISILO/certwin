package br.com.certwin.cli;

import br.com.certwin.exceptions.ArgumentosInvalidosException;

public final class CliValidador {
    private CliValidador() {};

    public static Modo validar(CliOpcoes opcoes) throws ArgumentosInvalidosException {
        Modo modo = determinarModo(opcoes);
        validarCamposObrigatorios(modo, opcoes);
        return modo;
    }

    static Modo determinarModo(CliOpcoes opcoes) throws ArgumentosInvalidosException {
        int selecionados = 0;

        if(opcoes.isCriar()) selecionados++;
        if(opcoes.isConverter()) selecionados++;
        if(opcoes.isCriarEConverter()) selecionados++;

        if(selecionados == 0) throw new ArgumentosInvalidosException("Informe exatamente um modo: -C, -P ou -CP.");
        if(selecionados > 1) throw new ArgumentosInvalidosException("Informe apenas um modo por execucao: -C, -P ou -CP");
        
        if(opcoes.isCriar()) return Modo.CRIAR;
        if(opcoes.isConverter()) return Modo.CONVERTER;

        return Modo.CRIAR_E_CONVERTER;
    }

    static void validarCamposObrigatorios(Modo modo, CliOpcoes opcoes) throws ArgumentosInvalidosException {
        switch(modo) {
            case CRIAR -> {
                obrigatorio(opcoes.getEmail(), "--email e obrigatorio no modo -C.");
                obrigatorio(opcoes.getDominio(), "--dominio e obrigatorio no modo -C.");
            }
            case CONVERTER -> obrigatorio(opcoes.getSenha(), "--senha e obrigatorio no modo -P.");
            case CRIAR_E_CONVERTER -> {
               obrigatorio(opcoes.getEmail(), "--email e obrigatorio no modo -CP.");
               obrigatorio(opcoes.getDominio(), "--dominio e obrigatorio no modo -CP.");
               obrigatorio(opcoes.getSenha(), "--senha e obrigatorio no modo -CP.");
            }
        }
    }

    public static void obrigatorio(String valor, String mensagemErro) throws ArgumentosInvalidosException {
        if(valor == null || valor.isBlank()) {
            throw new ArgumentosInvalidosException(mensagemErro);
        }
    }
}
