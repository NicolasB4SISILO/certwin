package br.com.certwin.acme;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public final class StandaloneChallengeServer implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(StandaloneChallengeServer.class.getName());
    private static final String CAMINHO_BASE = "/.well-known/acme-challenge/";

    private final HttpServer server;

    public StandaloneChallengeServer(
        int porta, 
        String token, 
        String keyAuthorization
    ) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(porta), 0);
        server.createContext(CAMINHO_BASE + token, exchange -> responder(exchange, keyAuthorization));
        server.setExecutor(null);
    }

    public void iniciar() {
        server.start();
        LOG.info("Servidor de desafio HTTP-01 no ar na porta " + server.getAddress().getPort() + ".");
    }

    @Override 
    public void close() {
        server.stop(0);
        LOG.info("Servidor de desafio HTTP-01 encerrado.");
    }

    public static void responder(
        HttpExchange exchange, 
        String keyAuthorization
    ) throws IOException {
        byte[] corpo = keyAuthorization.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, corpo.length);
        try(OutputStream out = exchange.getResponseBody()) {
            out.write(corpo);
        }
    } 
}
