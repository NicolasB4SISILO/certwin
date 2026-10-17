package br.com.certwin.acme;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.List;

public record IssuedCertificate (
    KeyPair domainKeyPair,
    List<X509Certificate> chain
){}
