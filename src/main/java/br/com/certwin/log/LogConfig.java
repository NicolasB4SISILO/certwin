package br.com.certwin.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogConfig {
    public static final String LOGGER_RAIZ = "br.com.gerenciar.tls";
    private static final int TAMANHO_MAXIMO_ARQUIVO_BYTES = 5_000_000;
    private static final int QUANTIDADE_ARQUIVOS_ROTACAO = 10;

    private LogConfig() {};

    public static void configure(Path logsDir) throws IOException {
        Files.createDirectories(logsDir);

        Logger raiz = Logger.getLogger(LOGGER_RAIZ);
        for(Handler handler : raiz.getHandlers()) {
            handler.close();
            raiz.removeHandler(handler);
        }

        raiz.setUseParentHandlers(false);
        raiz.setLevel(Level.INFO);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new ConsoleFormatter());
        raiz.addHandler(consoleHandler);

        String padraoArquivo = logsDir.resolve("gerenciar-tls-%g.log").toString();
        FileHandler fileHandler = new FileHandler(
            padraoArquivo, TAMANHO_MAXIMO_ARQUIVO_BYTES, QUANTIDADE_ARQUIVOS_ROTACAO, true
        );
        
        fileHandler.setLevel(Level.INFO);
        fileHandler.setFormatter(new FileLogFormatter());
        raiz.addHandler(fileHandler);
    }
}
