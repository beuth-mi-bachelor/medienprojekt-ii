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

    public void getRoomList(Emitter.Listener roomListCallback);

    public void switchRoom(String roomName, Emitter.Listener switchedCallback, Emitter.Listener gameReadyCallback, Emitter.Listener roomUpdateCallback);

    public void goBackToLobby(Emitter.Listener switchedCallback);

    public void getRandomRoom(Emitter.Listener getRoomCallback);

    public void joinRandomRoom(Emitter.Listener joinCallback, Emitter.Listener gameReadyCallback, Emitter.Listener roomUpdateCallback);

    public void connectToServer(Emitter.Listener connectCallback, Emitter.Listener connectError);

    public void initPlayer(String playername, Emitter.Listener initDone);

    public void setPlayerActive();

    public void setPlayerInActive();

    public void clientIsReady(Emitter.Listener gameStartedCallback);

    public void addListener(String event, Emitter.Listener callback);
}
