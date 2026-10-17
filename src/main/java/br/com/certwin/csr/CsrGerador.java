package br.com.certwin.csr;

import java.io.IOException;
import java.security.KeyPair;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

public final class CsrGerador {
    private static final String ALGOTITMO_ASSINATURA = "SHA256withRSA";

    private CsrGerador() {};

    public static PKCS10CertificationRequest gerador(String dominio, KeyPair keypair) {
        X500Name subject = new X500Name("CN=" + dominio);
        JcaPKCS10CertificationRequestBuilder builder = 
                new JcaPKCS10CertificationRequestBuilder(subject, keypair.getPublic());
        builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, buildSanExtension(dominio));

        try{
            ContentSigner signer = new JcaContentSignerBuilder(ALGOTITMO_ASSINATURA).build(keypair.getPrivate());
            return builder.build(signer);
        } catch(OperatorCreationException e) {
            throw new IllegalStateException("Falha ao assinar o CSR - nao deveria acontecer", e);
        }
    } 

    public static byte[] toDer(PKCS10CertificationRequest csr) {
        try {
            return csr.getEncoded();
        } catch(IOException e) {
            throw new IllegalStateException("Falha ao codificar o CSR em DER - nao deveria acontecer", e);
        }
    }

    public static Extensions buildSanExtension(String dominio) {
        try {
            GeneralNames subjectAltnames = new GeneralNames(new GeneralName(GeneralName.dNSName, dominio));
            ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
            extensionsGenerator.addExtension(Extension.subjectAlternativeName, false, subjectAltnames);
            return extensionsGenerator.generate();
        } catch(IOException e) {
            throw new IllegalStateException("Falha ao montar a extensao SAN do CSR - nao deveria acontecer.", e);
        }
    }
}
