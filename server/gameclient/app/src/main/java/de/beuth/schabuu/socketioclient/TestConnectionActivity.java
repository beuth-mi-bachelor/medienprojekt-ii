package de.beuth.schabuu.socketioclient;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class TestConnectionActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String SERVER_ADDRESS = "192.168.1.102";
    public static final int PORT_NUMBER = 1337;

    Socket socket;
    Button connect;
    Button disconnect;
    Button join;
    Button leave;
    EditText roomName;
    EditText name;
    TextView log;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_connection);

        roomName = (EditText) findViewById(R.id.input_roomname);
        name = (EditText) findViewById(R.id.input_name);

        log = (TextView) findViewById(R.id.text_view);

        connect = (Button) findViewById(R.id.button_connect);
        connect.setOnClickListener(this);

        disconnect = (Button) findViewById(R.id.button_disconnect);
        disconnect.setOnClickListener(this);

        join = (Button) findViewById(R.id.button_join);
        join.setOnClickListener(this);

        leave = (Button) findViewById(R.id.button_leave);
        leave.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_connect:
                System.out.println("pressed connect button");
                if (socket == null || !socket.connected()) {
                    connect();
                }
                break;
            case R.id.button_disconnect:
                System.out.println("pressed disconnect button");
                if (socket != null) {
                    socket.emit("disconnection");
                    socket.disconnect();
                }
                break;
            case R.id.button_join:
                if (socket == null || !socket.connected()) {
                    connect();
                }
                socket.emit("join_room", roomName.getText());
                break;
            case R.id.button_leave:
                if (socket == null || !socket.connected()) {
                    connect();
                }
                socket.emit("leave_room", roomName.getText());
                break;
        }
    }

    private void connect() {

        if (socket == null) {
            try {
                socket = IO.socket("http://" + SERVER_ADDRESS + ":" + PORT_NUMBER);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on("room", onRoomEvent);

        socket.connect();
    }

    private Emitter.Listener onRoomEvent = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            setText(log, "room called!\n");
            setText(log, args[0].toString() + "\n");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            System.out.println("ERROR!");
            System.out.println(args[0].toString());
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            System.out.println("Connected!");
            JSONObject playerData = new JSONObject();
            try {
                playerData.put("name", name.getText());
            } catch (JSONException e) {
                System.err.println(e.getMessage());
            }
            socket.emit("new_player", playerData);
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            System.out.println("Disconnected!");

        }
    };

    private void setText(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.append(value);
            }
        });
    }

}
