package br.com.certwin.arquivos;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LocalizarJar {
    private static final Logger LOG = Logger.getLogger(LocalizarJar.class.getName());

    private LocalizarJar() {};

    public static Path jarDiretorio() {
        try {
            File local = new File(LocalizarJar.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File pasta = local.isFile() ? local.getParentFile() : local;
            return pasta.toPath();
        } catch(URISyntaxException | NullPointerException e) {
            LOG.log(Level.FINE, "Não foi possivel localizar a pasta jar, usando o diretorio atual.", e);
            return Paths.get("").toAbsolutePath();
        } 
    };
}
