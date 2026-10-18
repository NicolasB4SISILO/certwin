package br.com.certwin;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.certwin.acme.IssuedCertificate;
import br.com.certwin.arquivos.FormatoDaSaida;
import br.com.certwin.arquivos.LocalizarJar;
import br.com.certwin.cli.CliOpcoes;
import br.com.certwin.cli.CliValidador;
import br.com.certwin.cli.Modo;
import br.com.certwin.exceptions.ExitCodes;
import br.com.certwin.exceptions.GeradorTlsException;
import br.com.certwin.fluxo.ConverterParaP12;
import br.com.certwin.fluxo.FluxoDeCriacao;
import br.com.certwin.log.LogConfig;
import picocli.CommandLine;

public final class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static final String NOME_PASTA_CONVERSAO = "converts";
    private static final String NOME_PASTA_PADRAO = "certs";
    private static final String ALIAS_PADRAO = "certificado";
    private static final String FORMATO_P12 = ".p12";

    public static void main(String[] args) {
        System.exit(run(args));
    };

    static int run(String[] args) {
        CliOpcoes options = new CliOpcoes();
        CommandLine cmd = new CommandLine(options);

        try {
            cmd.parseArgs(args);
        } catch (CommandLine.ParameterException e) {
            System.err.println(e.getMessage());
            return ExitCodes.ARGUMENTOS_INVALIDOS;
        }

        if(cmd.isUsageHelpRequested()) {
            cmd.usage(System.out);
            return ExitCodes.SUCESSO;
        }

        if(cmd.isVersionHelpRequested()) {
            cmd.printVersionHelp(System.out);
            return ExitCodes.SUCESSO;
        }

        Path caminhoRaizCerts = LocalizarJar.jarDiretorio().resolve(NOME_PASTA_PADRAO);
        FormatoDaSaida formatoDaSaida = new FormatoDaSaida(caminhoRaizCerts);
        
        try {
            LogConfig.configure(formatoDaSaida.logsDir());   
        } catch(IOException e) {
            System.err.println("Nao foi possivel iniciar o log em " + formatoDaSaida.logsDir() + ": " + e.getMessage());
            return ExitCodes.ERRO_ARQUIVO;
        }

        try {
            Modo modo = CliValidador.validar(options);
            realizarProcesso(modo, options, formatoDaSaida);
            return ExitCodes.SUCESSO;
        } catch (GeradorTlsException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            return e.getExitCode();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Erro inesperado. Veja o log em " + formatoDaSaida.logsDir() + " para detalhes.", e);
            return ExitCodes.ERRO_INESPERADO;
        }
    }

    private static void realizarProcesso(Modo modo, CliOpcoes options, FormatoDaSaida formatoDaSaida) throws GeradorTlsException {
        switch(modo) {
            case CRIAR -> new FluxoDeCriacao(formatoDaSaida, options.isStaging())
                    .execute(options.getEmail(), options.getDominio());

            case CONVERTER -> {
                Path entrada = options.getEntrada() != null 
                        ? Paths.get(options.getEntrada()) 
                        : formatoDaSaida.entradaPradao();

                String alias = options.getAlias() + FORMATO_P12;

                Path saida = options.getSaida() != null 
                        ? Paths.get(options.getSaida()) 
                        : LocalizarJar.jarDiretorio().resolve(NOME_PASTA_CONVERSAO).resolve(alias);

                new ConverterParaP12().executeFromFiles(entrada, options.getSenha(), saida, options.getAlias());
            }

            case CRIAR_E_CONVERTER -> {
                IssuedCertificate issued = new FluxoDeCriacao(formatoDaSaida, options.isStaging())
                        .execute(options.getEmail(), options.getDominio());

                String alias = (options.getAlias() != null && !options.getAlias().isBlank())
                        ? options.getAlias()
                        : ALIAS_PADRAO;
                                                                                                                      
                Path saida = options.getSaida() != null 
                        ? Paths.get(options.getSaida())
                        : formatoDaSaida.p12DirParaCriarEConverter(options.getDominio()).resolve(alias + FORMATO_P12);

                new ConverterParaP12().execute(
                    issued.domainKeyPair().getPrivate(),
                    issued.chain(),
                    options.getSenha(),
                    saida,
                    alias
                );
            }
        }
    }
};