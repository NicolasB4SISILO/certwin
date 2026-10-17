package br.com.certwin.acme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.logging.Logger;

import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.exception.AcmeProtocolException;

import br.com.certwin.arquivos.FormatoDaSaida;
import br.com.certwin.exceptions.AcmeExceptionTranslator;
import br.com.certwin.exceptions.ArquivoException;
import br.com.certwin.exceptions.GeradorTlsException;
import br.com.certwin.key.KeyPairService;
import br.com.certwin.pem.PemReader;
import br.com.certwin.pem.PemWriter;


public final class AcmeAccount {
    private static final Logger LOG = Logger.getLogger(AcmeAccount.class.getName());

    private AcmeAccount() {};

    public static Account pegarOuCriarConta(
        Session session, 
        String email, 
        FormatoDaSaida formatoDaSaida
    ) throws GeradorTlsException {
        KeyPair accountKeyPair = carregarOuCriarContaKeyPair(formatoDaSaida.accountKeyPath());

        try {
            return new AccountBuilder()
                        .addEmail(email)
                        .agreeToTermsOfService()
                        .useKeyPair(accountKeyPair)
                        .create(session);
        } catch (AcmeException e) {
            throw AcmeExceptionTranslator.translate(e);
        } catch (AcmeProtocolException e) {
            throw AcmeExceptionTranslator.translate(e);
        }
    }

    private static KeyPair carregarOuCriarContaKeyPair(Path accountKeyPath) throws ArquivoException {
        if(Files.exists(accountKeyPath)) {
            LOG.info("Reutilizando chave de conta ACME existente: " + accountKeyPath);
            PrivateKey privateKey = PemReader.readPrivateKey(accountKeyPath);
            PublicKey publicKey = derivaPublicKey(privateKey, accountKeyPath);

            return new KeyPair(publicKey, privateKey);
        }

        LOG.info("Nenhuma chave de conta ACME encontrada, gerando uma nova em: " + accountKeyPath);
        KeyPair keyPair = KeyPairService.geradorRsaKeyPair();
        PemWriter.write(accountKeyPath, PemWriter.encode("PRIVATE KEY", keyPair.getPrivate().getEncoded()));
        return keyPair;
    }

    private static PublicKey derivaPublicKey(PrivateKey privateKey, Path origem) throws ArquivoException {
        if(!(privateKey instanceof RSAPrivateCrtKey crtKey)) {
            throw new ArquivoException("A chave de conta em " + origem + "nao esta no formato RSA esperado.");
        }

        try {
            RSAPublicKeySpec spec = new RSAPublicKeySpec(crtKey.getModulus(), crtKey.getPublicExponent());
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo RSA indisponivel na JVM - nao deveria acontecer.", e);
        } catch (InvalidKeySpecException e) {
            throw new ArquivoException("Nao foi possivel derivar a chave publica da conta a partir de: " + origem, e);
        }
    }
}
