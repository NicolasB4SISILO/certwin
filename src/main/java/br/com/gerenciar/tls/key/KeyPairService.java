package br.com.gerenciar.tls.key;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public final class KeyPairService {
    private static final int TAMANHO_CHAVE_BITS = 2048;

    private KeyPairService() {};

    public static KeyPair geradorRsaKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(TAMANHO_CHAVE_BITS);
            return generator.generateKeyPair();
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritimo RSA indisponivel na JVM - nao deveria acontecer.", e);
        }
    }
}
