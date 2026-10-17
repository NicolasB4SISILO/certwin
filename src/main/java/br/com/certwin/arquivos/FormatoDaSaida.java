package br.com.certwin.arquivos;

import java.nio.file.Path;

public class FormatoDaSaida {
    private final Path raizDasChaves;

    public FormatoDaSaida(Path raizDasChaves) {
        this.raizDasChaves = raizDasChaves;
    }

    public Path pemDir(String dominio) {
        return raizDasChaves.resolve(dominio).resolve("pem");
    }

    public Path p12DirParaCriarEConverter(String dominio) {
        return raizDasChaves.resolve(dominio).resolve("p12");
    }

    public Path accountKeyPath() {
        return raizDasChaves.resolve("account").resolve("account.key");
    }

    public Path logsDir() {
        return raizDasChaves.resolve("logs");
    }

    public Path entradaPradao() {
        return raizDasChaves;
    }
}
