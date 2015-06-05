package de.beuth_hochschule.Schabuu.data;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * implemented as a SINGLETON
 */
public class ServerConnectorImplementation implements ServerConnector {

    private static ServerConnectorImplementation instance;

    private String ip;
    private int port;
    private Socket socket;

    private ServerConnectorImplementation() {
        this.ip = "178.63.189.173";
        this.port = 1337;
        try {
            socket = IO.socket("http://" + this.ip + ":" + this.port);
        } catch (URISyntaxException e) {
            System.err.println("--> " + e.getMessage());
        }
    }

    private ServerConnectorImplementation(String ip) {
        this.ip = ip;
        this.port = 1337;
        try {
            socket = IO.socket("http://" + this.ip + ":" + this.port);
        } catch (URISyntaxException e) {
            System.err.println("--> " + e.getMessage());
        }
    }

    private ServerConnectorImplementation(int port) {
        this.ip = "178.63.189.173";
        this.port = port;
        try {
            socket = IO.socket("http://" + this.ip + ":" + this.port);
        } catch (URISyntaxException e) {
            System.err.println("--> " + e.getMessage());
        }
    }

    private ServerConnectorImplementation(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            socket = IO.socket("http://" + this.ip + ":" + this.port);
        } catch (URISyntaxException e) {
            System.err.println("--> " + e.getMessage());
        }
    }

    public static ServerConnectorImplementation getInstance () {
        if (ServerConnectorImplementation.instance == null) {
            ServerConnectorImplementation.instance = new ServerConnectorImplementation ();
        }
        return ServerConnectorImplementation.instance;
    }

    public static ServerConnectorImplementation getInstance (String ip, int port) {
        if (ServerConnectorImplementation.instance == null) {
            ServerConnectorImplementation.instance = new ServerConnectorImplementation (ip, port);
        }
        return ServerConnectorImplementation.instance;
    }

    public static ServerConnectorImplementation getInstance (String ip) {
        if (ServerConnectorImplementation.instance == null) {
            ServerConnectorImplementation.instance = new ServerConnectorImplementation (ip);
        }
        return ServerConnectorImplementation.instance;
    }

    public static ServerConnectorImplementation getInstance (int port) {
        if (ServerConnectorImplementation.instance == null) {
            ServerConnectorImplementation.instance = new ServerConnectorImplementation (port);
        }
        return ServerConnectorImplementation.instance;
    }

    @Override
    public void switchRoom(String roomName, final Emitter.Listener switchedCallback) {
        HashMap<String, String> items = new HashMap<String, String>();
        items.put("name", roomName);
        JSONObject data = this.jsonObjectHelper(items);
        this.emit(Events.SWITCH_ROOM, data);
        socket.on(Events.SWITCH_ROOM_CALLBACK, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.off(Events.SWITCH_ROOM_CALLBACK);
                switchedCallback.call(args);
            }
        });
    }

    @Override
    public Socket getSocket() {
        return this.socket;
    }

    @Override
    public void getRandomRoom(final Emitter.Listener getRoomCallback) {
        this.emit(Events.RANDOM_ROOM, null);
        socket.on(Events.RANDOM_ROOM_CALLBACK, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.off(Events.RANDOM_ROOM_CALLBACK);
                getRoomCallback.call(args);
            }
        });
    }

    @Override
    public void joinRandomRoom(Emitter.Listener joinCallback) {

    }

    @Override
    public void connectToServer(Emitter.Listener connectCallback, Emitter.Listener connectError) {
        socket.on(Socket.EVENT_CONNECT, connectCallback);
        socket.on(Socket.EVENT_CONNECT_ERROR, connectCallback);
        socket.connect();
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    public boolean emit(String event, JSONObject obj) {
        if (socket.connected()) {
            socket.emit(event, obj);
            return true;
        }
        return false;
    }

    public void initPlayer(String playername, final Emitter.Listener initDone) {
        HashMap<String, String> items = new HashMap<String, String>();
        items.put("name", playername);
        JSONObject data = this.jsonObjectHelper(items);
        this.emit(Events.NEW_PLAYER, data);
        socket.on(Events.NEW_PLAYER_CALLBACK, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.off(Events.NEW_PLAYER_CALLBACK);
                initDone.call(args);
            }
        });
    }

    public JSONObject jsonObjectHelper(HashMap<String, String> input) {
        JSONObject data = new JSONObject();
        try {
            for (String key : input.keySet()) {
                data.put(key, input.get(key));
            }
        } catch (JSONException e) {
            System.err.println("--> " + e.getMessage());
        }
        return data;
    }

    @Override
    public String toString() {
        return "ServerConnectorImplementation{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", socket=" + socket +
                '}';
    }
}
