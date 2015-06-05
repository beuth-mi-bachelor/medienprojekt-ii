package de.beuth_hochschule.Schabuu.data;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.util.HashMap;

public interface ServerConnector {
    public Socket getSocket();
    public boolean isConnected();
    public boolean emit(String event, JSONObject obj);
    public JSONObject jsonObjectHelper(HashMap<String, String> input);
    public void switchRoom(String roomName, Emitter.Listener switchedCallback);
    public void getRandomRoom(Emitter.Listener getRoomCallback);
    public void joinRandomRoom(Emitter.Listener joinCallback);
    public void connectToServer(Emitter.Listener connectCallback, Emitter.Listener connectError);
    public void initPlayer(String playername, Emitter.Listener initDone);
}
