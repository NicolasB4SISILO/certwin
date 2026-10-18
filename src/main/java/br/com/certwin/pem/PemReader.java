package br.com.certwin.pem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.certwin.exceptions.ArquivoException;

public final class PemReader {
    private static final Pattern BLOCK_PATTERN = 
            Pattern.compile("-----BEGIN ([^-]+)-----(.*?)-----END \\1-----", Pattern.DOTALL);

    private PemReader() {};

    public static PrivateKey readPrivateKey(Path path) throws ArquivoException {
        String conteudo = readFile(path);
        Matcher matcher = BLOCK_PATTERN.matcher(conteudo);

        if(!matcher.find()) throw new ArquivoException("Nenhum bloco PEM encontrado em: " + path);
        String label = matcher.group(1).trim();

        if("RSA PRIVATE KEY".equals(label)) {
            throw new ArquivoException("A chave em " + path + " esta no formato antigo PKCS#1 "
                    + "(-----BEGIN RSA PRIVATE KEY-----). Converta para PKCS#8 antes de usar "
                    + "(ex: openssl pkcs8 -topk8 -nocrypt -in chave.pem -out chave_pkcs8.pem).");
        }

        if(!"PRIVATE KEY".equals(label)) {
            throw new ArquivoException("Bloco PEM inesperado em " + path + ": " + label
                    + " (esperado PRIVATE KEY).");
        }

        byte[] der = decodeBase64(matcher.group(2));

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo RSA indisponivel na JVM - nao deveria acontecer.", e);
        } catch (InvalidKeySpecException e) {
            throw new ArquivoException("Chave privada invalida em: " + path, e);
        }
    }

    public static List<X509Certificate> readCertificateChain(Path path) throws ArquivoException {
        String conteudo = readFile(path);
        Matcher matcher = BLOCK_PATTERN.matcher(conteudo);
        List<X509Certificate> certificados = new ArrayList<>();

        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            while(matcher.find()) {
                if(!"CERTIFICATE".equals(matcher.group(1).trim())) continue;

                byte[] der = decodeBase64(matcher.group(2));
                Certificate certificado = factory.generateCertificate(new ByteArrayInputStream(der));
                certificados.add((X509Certificate) certificado);
            }
        } catch(CertificateException e) {
            throw new ArquivoException("Certificado invalido em: " + path, e);
        }

        if(certificados.isEmpty()) {
            throw new ArquivoException("Nenhum certificado encontrado em: " + path);
        }

        return certificados;
    }

    public static byte[] decodeBase64(String base64ComQuebrasDeLinha) {
        String limpo = base64ComQuebrasDeLinha.replaceAll("\\s", "");
        return Base64.getDecoder().decode(limpo);
    }

    public static String readFile(Path path) throws ArquivoException {
        try {
            return Files.readString(path);
        } catch(IOException e) {
            throw new ArquivoException("Não foi possivel ler o arquivo: " + path, e);
        }
    }
}
