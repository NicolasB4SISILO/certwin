package br.com.certwin.pem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import br.com.certwin.exceptions.ArquivoException;

public final class PemWriter {
    private static final int LINE_LENGTH = 64;
    
    private PemWriter() {};

    public static String encode(String label, byte[] derBytes) {
        String base64 = Base64.getMimeEncoder(LINE_LENGTH, "\n".getBytes(StandardCharsets.US_ASCII))
                .encodeToString(derBytes);

        return "-----BEGIN " + label + "-----\n" + base64 + "\n-----END " + label + "-----\n";
    }

    public static void write(Path path, String pemContent) throws ArquivoException {
        try {
            Path parent = path.getParent();
            if(parent != null) Files.createDirectories(parent);
            
            Files.writeString(path, pemContent, StandardCharsets.US_ASCII);
        } catch(IOException e) {
            throw new ArquivoException("Não foi possivel escrever o arquivo: " + path, e);
        }
    }
}
