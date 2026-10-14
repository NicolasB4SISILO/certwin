package br.com.gerenciar.tls.pem;

import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;

import br.com.gerenciar.tls.exceptions.ArquivoException;

public final class CertificateBundleWriter {
    private CertificateBundleWriter() {};

    public static void write(
        Path pemDir, 
        PrivateKey privateKey, 
        List<X509Certificate> chain
    ) throws ArquivoException {
        PemWriter.write(pemDir.resolve("privKey.pem"), PemWriter.encode("PRIVATE KEY", privateKey.getEncoded()));
        PemWriter.write(pemDir.resolve("cert.pem"), encodeCertificate(chain.get(0)));

        StringBuilder chainPem = new StringBuilder();
        for(int i = 1; i < chain.size(); i++) {
            chainPem.append(encodeCertificate(chain.get(1)));
        }

        PemWriter.write(pemDir.resolve("chain.pem"), chainPem.toString());
        StringBuilder fullchainPem = new StringBuilder();
        for(X509Certificate certificate : chain) {
            fullchainPem.append(encodeCertificate(certificate));
        }

        PemWriter.write(pemDir.resolve("fullchain.pem"), fullchainPem.toString());
    }

    private static String encodeCertificate(X509Certificate certificate) throws ArquivoException {
        try {
            return PemWriter.encode("CERTIFICATE", certificate.getEncoded());
        } catch(CertificateEncodingException e) {
            throw new ArquivoException("Falha ao codificar certificado.", e);
        }
    }
}
