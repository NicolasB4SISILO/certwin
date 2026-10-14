package br.com.gerenciar.tls.fluxo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Logger;

import br.com.gerenciar.tls.exceptions.ArquivoException;
import br.com.gerenciar.tls.pem.PemReader;
import br.com.gerenciar.tls.pkcs12.Pkcs12Generator;

public final class ConverterParaP12 {
    private static final Logger LOG = Logger.getLogger(ConverterParaP12.class.getName());
    private static final String ALIAS_PADRAO = "certificado";

    public void execute(
        PrivateKey privateKey,
        List<X509Certificate> chain,
        String senha,
        Path saida,
        String alias
    ) throws ArquivoException {
        Pkcs12Generator.generate(saida, privateKey, chain, senha, alias);
        LOG.info("Arquivo .p12 gravado em: " + saida);
    };

    public void executeFromFiles(
        Path entrada,
        String senha,
        Path saida
    ) throws ArquivoException {
        if(!Files.isDirectory(entrada)) {
            throw new ArquivoException("Pasta de entrada nao encontrada: " + entrada
                    + ". Informe --entrada apontando para a pasta com privkey.pem e fullchain.pem "
                    + "(se o .pem foi gerado por -C/-CP, ele esta em certs/<dominio>/pem/).");
        }

        PrivateKey privateKey = PemReader.readPrivateKey(entrada.resolve("privkey.pem"));
        List<X509Certificate> chain = PemReader.readCertificateChain(entrada.resolve("fullchain.pem"));
        execute(privateKey, chain, senha, saida, ALIAS_PADRAO);
    };
}
