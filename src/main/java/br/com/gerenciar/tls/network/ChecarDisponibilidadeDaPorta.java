package br.com.gerenciar.tls.network;

import java.io.IOException;
import java.net.ServerSocket;

public final class ChecarDisponibilidadeDaPorta {
    private ChecarDisponibilidadeDaPorta() {};

    public static boolean estaDisponivel(int porta) {
        try(ServerSocket socket = new ServerSocket(porta)) {
            return true;
        } catch(IOException e) {
            return false;
        }
    }
}
