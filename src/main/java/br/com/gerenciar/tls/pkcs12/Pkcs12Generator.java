package br.com.gerenciar.tls.pkcs12;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import br.com.gerenciar.tls.exceptions.ArquivoException;

public final class Pkcs12Generator {
    private Pkcs12Generator() {};

    public static void generate(
        Path saida, 
        PrivateKey privateKey, 
        List<X509Certificate> chain,
        String senha,
        String alias
    ) throws ArquivoException {
        try{
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, null);
            keyStore.setKeyEntry(alias, privateKey, senha.toCharArray(), chain.toArray(new X509Certificate[0]));

            Path parent = saida.getParent();
            if(parent != null) {
                Files.createDirectories(parent);
            }

            try (OutputStream out = Files.newOutputStream(saida)) {
                keyStore.store(out, senha.toCharArray());
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new ArquivoException("Falha ao gerar o arquivo .p12: " + saida, e);
        }
    }
}
