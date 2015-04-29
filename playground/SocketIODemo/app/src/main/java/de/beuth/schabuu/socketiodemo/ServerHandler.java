package de.beuth.schabuu.socketiodemo;

import java.net.MalformedURLException;
import java.net.SocketException;

import io.socket.SocketIO;

public class ServerHandler {

    private final static String SERVER_URL = "http://192.168.1.101";
    private final static String SERVER_PORT = "1337";

    private SocketIO socket;

    public ServerHandler() {
        try {
            this.socket = new SocketIO(SERVER_URL + ":" + SERVER_PORT);
        } catch (MalformedURLException msg) {
            this.socket = null;
        }
    }

    public void connectToServer() {
        if (this.socket == null) {
            System.out.println("Could not bind socket");
        } else {
            this.socket.connect(new CallbackHandler());
            this.socket.send("Hello Server!");
        }
    }

}
