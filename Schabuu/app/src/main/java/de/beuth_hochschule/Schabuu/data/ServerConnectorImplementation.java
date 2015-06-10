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
    public static String USERID = "";

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

    public static ServerConnectorImplementation getInstance() {
        if (ServerConnectorImplementation.instance == null) {
            ServerConnectorImplementation.instance = new ServerConnectorImplementation();
        }
        return ServerConnectorImplementation.instance;
    }

    public static ServerConnectorImplementation getInstance(String ip, int port) {
        if (ServerConnectorImplementation.instance == null) {
            ServerConnectorImplementation.instance = new ServerConnectorImplementation(ip, port);
        }
        return ServerConnectorImplementation.instance;
    }

    public static ServerConnectorImplementation getInstance(String ip) {
        if (ServerConnectorImplementation.instance == null) {
            ServerConnectorImplementation.instance = new ServerConnectorImplementation(ip);
        }
        return ServerConnectorImplementation.instance;
    }

    public static ServerConnectorImplementation getInstance(int port) {
        if (ServerConnectorImplementation.instance == null) {
            ServerConnectorImplementation.instance = new ServerConnectorImplementation(port);
        }
        return ServerConnectorImplementation.instance;
    }

    @Override
    public void switchRoom(final String roomName, final Emitter.Listener switchedCallback, final Emitter.Listener gameReadyCallback, final Emitter.Listener roomUpdateCallback, final Emitter.Listener roomCanNotBeJoinedCallback) {
        HashMap<String, String> items = new HashMap<String, String>();
        items.put("name", roomName);
        final JSONObject data = this.jsonObjectHelper(items);

        socket.on(Events.ROOM_CHECK_CALLBACK, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // remove old event listeners
                socket.off(Events.ROOM_CHECK_CALLBACK);
                JSONObject cbObject = (JSONObject) args[0];
                try {
                    Boolean roomIsJoinable = (Boolean) cbObject.get("canJoin");
                    if (roomIsJoinable) {
                        joinRoom(data, switchedCallback, gameReadyCallback, roomUpdateCallback);
                    } else {
                        if (roomCanNotBeJoinedCallback != null) {
                            roomCanNotBeJoinedCallback.call(args);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        this.emit(Events.ROOM_CHECK, data);
    }

    public void joinRoom(final JSONObject data, final Emitter.Listener switchedCallback, final Emitter.Listener gameReadyCallback, final Emitter.Listener roomUpdateCallback) {
        socket.on(Events.SWITCH_ROOM_CALLBACK, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.off(Events.SWITCH_ROOM_CALLBACK);
                switchedCallback.call(args);
            }
        });
        socket.on(Events.GAME_READY, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.off(Events.GAME_READY);
                gameReadyCallback.call(args);
            }
        });
        socket.on(Events.ROOM_UPDATE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                roomUpdateCallback.call(args);
            }
        });
        this.emit(Events.SWITCH_ROOM, data);
    }


    @Override
    public void goBackToLobby(final Emitter.Listener switchedCallback) {
        HashMap<String, String> items = new HashMap<String, String>();
        items.put("name", "lobby");
        JSONObject data = this.jsonObjectHelper(items);
        // remove old event listeners
        this.emit(Events.SWITCH_ROOM, data);
        socket.on(Events.SWITCH_ROOM_CALLBACK, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.off(Events.SWITCH_ROOM_CALLBACK);
                switchedCallback.call(args);
                socket.off(Events.ROOM_UPDATE);
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
    public void joinRandomRoom(final Emitter.Listener joinCallback, final Emitter.Listener gameReadyCallback, final Emitter.Listener roomUpdateCallback) {
        // remove old event listeners
        this.getRandomRoom(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String roomName = (String) args[0];
                ServerConnectorImplementation.this.switchRoom(roomName, joinCallback, gameReadyCallback, roomUpdateCallback, null);
            }
        });

    }

    @Override
    public void addListener(String event, Emitter.Listener callback) {
        socket.on(event, callback);
    }

    @Override
    public void removeListener(String event) {
        socket.off(event);
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
                JSONObject player = (JSONObject) args[0];
                try {
                    USERID = (String) player.get("playerId");
                    System.out.println(USERID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initDone.call(args);
            }
        });
    }

    @Override
    public void setPlayerActive() {
        this.emit(Events.PLAYER_ACTIVE, null);
    }

    @Override
    public void setPlayerInActive() {
        this.emit(Events.PLAYER_INACTIVE, null);
    }

    @Override
    public void clientIsReady(final Emitter.Listener gameStartedCallback) {
        this.emit(Events.PLAYER_READY, null);
        socket.on(Events.GAME_START, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.off(Events.GAME_START);
                gameStartedCallback.call(args);
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
    public void getRoomList(final Emitter.Listener roomListCallback) {
        this.emit(Events.ROOM_LIST, null);
        socket.on(Events.ROOM_LIST_CALLBACK, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.off(Events.ROOM_LIST_CALLBACK);
                roomListCallback.call(args);
            }
        });
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
