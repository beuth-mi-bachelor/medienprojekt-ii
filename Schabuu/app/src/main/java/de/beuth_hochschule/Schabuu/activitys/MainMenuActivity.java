package de.beuth_hochschule.Schabuu.activitys;

/**
 * Created by angi on 31.05.15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Iterator;

import de.beuth_hochschule.Schabuu.R;

public class MainMenuActivity extends Activity {

    //Server config
    public static final String SERVER_ADDRESS = "192.168.1.3";
    public static final int PORT_NUMBER = 1337;

    public static com.github.nkzawa.socketio.client.Socket socket;

    String username="RandomUser";
    String roomName="bla";
    StringBuffer stringBuffer = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserNameAlert();

        setContentView(R.layout.activity_main_menu);

        View playAloneView= findViewById(R.id.play_alone);
        playAloneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sEmit("get_random_room", null);
                JSONObject room = new JSONObject();
                try {
                    room.put("name", roomName);
                } catch(JSONException ex) {
                    System.err.println(ex.getMessage());
                }
                sEmit("switch_room", room);
               // startActivity(new Intent(MainMenuActivity.this, RoomActivity.class));
            }
        });

        View playWithFriendsView= findViewById(R.id.play_friends);
        playWithFriendsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, PlayWithFriendsActivity.class));
            }
        });

        View settingsView= findViewById(R.id.settings);
        settingsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    private void connectServer() {

        if (socket == null) {
            try {
                socket = IO.socket("http://" + SERVER_ADDRESS + ":" + PORT_NUMBER);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT, onConnect);
        socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on("receive_random_room", onRandRoomEvent);
        socket.on("update_room", onRoomEvent);

        socket.connect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Connected!");
            JSONObject playerData = new JSONObject();
            try {
                playerData.put("name", username);
            } catch (JSONException e) {
                System.err.println(e.getMessage());
            }
            sEmit("new_player", playerData);
        }
    };

    private Emitter.Listener onRandRoomEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            setText(roomName, "room received!\n");

            final String roomData = (String) args[0];
            setText(roomName, roomData + "\n");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(roomData);
                }
            });

        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            System.out.println("ERROR!");
            System.out.println(args[0].toString());
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            System.out.println("Disconnected!");

        }
    };

    public static void sEmit(String event, JSONObject obj){
        if(socket.connected()){
            socket.emit(event, obj);
        }
    }

    private Emitter.Listener onRoomEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject roomData = (JSONObject) args[0];
            JSONObject players;

            try {
                players = (JSONObject) roomData.get("players");
                Iterator x = players.keys();
                int i = 0;

                String roomName = (String) roomData.get("name");

                if (!roomName.equals("lobby")) {
                    Intent intent = new Intent(MainMenuActivity.this, RoomActivity.class);
                    while (x.hasNext()) {
                        String key = (String) x.next();

                        i++;
                        intent.putExtra(("player" + i), (String) players.get(key));
                    }
                    intent.putExtra(("roomName"), roomName);
                    MainMenuActivity.this.startActivity(intent);
                }

            } catch (JSONException ex) {
                System.err.println(ex.getMessage());
            }

        }
    };


    private void getUserNameAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Set Name");
        alert.setMessage("Please enter your name");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                username = input.getText().toString();
                if (socket == null || !socket.connected()) {
                    connectServer();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //TO DO random name generator or cant play without name
                username = "Random";
                if (socket == null || !socket.connected()) {
                    connectServer();
                }
            }
        });
        alert.show();
    }
    private void setText(final String roomNameOld, final String value){
        stringBuffer.append(roomNameOld);
        stringBuffer.append(value);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roomName = stringBuffer.toString();
            }
        });
        System.out.println(stringBuffer.toString());
    }

}
