package br.com.certwin.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "chaves",
    mixinStandardHelpOptions = true,
    version = "gerenciador-ssl 1.0.0",
    description = "Gera chaves SSL e certificados assinados pelo Let's Encrypt(ACME v2)"
)
public class CliOpcoes {
    @Option(names = "-C", description = "Gera chave + CSR e assina via Let's Encrypt, salvando em .pem")
    boolean criar;

    @Option(names = "-P", description = "Converte um .pem existente para .p12")
    boolean converter;

    @Option(names = "-CP", description = "Executa -C e em seguida converte o resultado para .p12.")
    boolean criarEConverter;

    @Option(names = "--email", description = "E-mail vinculado a conta no Let's Encrypt. Obrigatorio em -C e -CP.")
    String email;

    @Option(names = "--dominio", description = "Dominio para o qual o certificado sera emitido. Obrigatorio em -C e -CP.")
    String dominio;

    @Option(names = "--senha", description = "Senha do arquivo .p12 gerado. Obrigatorio em -P e -CP.")
    String senha;

    @Option(names = "--entrada", description = "Pasta com os .pem existentes (modo -P). Default: pasta certs ao lado do jar.")
    String entrada;

    @Option(names = "--saida", description = "Caminho e nome do .p12 de saida. Default: privateKey.p12 ao lado do jar.")
    String saida;

    @Option(names = "--staging", description = "Usa o ambiente de staging do Let's Encrypt em vez de producao.")
    boolean staging;

    @Option(names = "--alias", description = "Alias da entrada no .p12 gerado. Default: Alias cerificado.pem")
    String alias;

    public boolean isCriar() {
        return criar;
    }

    public boolean isConverter() {
        return converter;
    }

    public boolean isCriarEConverter() {
        return criarEConverter;
    }

    public boolean isStaging() {
        return staging;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getDominio() {
        return dominio;
    }
    
    public String getEntrada() {
        return entrada;
    }

    public String getSaida() {
        return saida;
    }

    public String getAlias() {
        return alias;
    }
}
