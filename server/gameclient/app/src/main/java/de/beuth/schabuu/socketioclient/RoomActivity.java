package de.beuth.schabuu.socketioclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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


public class RoomActivity extends ActionBarActivity implements View.OnClickListener {

    Context context;
    Button lobby;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms);

        lobby = (Button) findViewById(R.id.lobby);
        lobby.setOnClickListener(this);

        context = this.getApplicationContext();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String p1 = extras.getString("player1");
            String p2 = extras.getString("player2");
            String p3 = extras.getString("player3");
            String p4 = extras.getString("player4");
            String room = extras.getString("roomName");
            TextView text = (TextView) findViewById(R.id.textView);
            if (room != null) {
                text.append("You are in room: " + room);
                text.append("\n");
            }
            if (p1 != null) {
                text.append(p1);
                text.append("\n");
            }
            if (p2 != null) {
                text.append(p2);
                text.append("\n");
            }
            if (p3 != null) {
                text.append(p3);
                text.append("\n");
            }
            if (p4 != null) {
                text.append(p4);
                text.append("\n");
            }
        }

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
        switch (v.getId()) {
            case R.id.lobby:
                JSONObject room = new JSONObject();
                try {
                    room.put("name", "lobby");
                } catch(JSONException ex) {
                    System.err.println(ex.getMessage());
                }
                TestConnectionActivity.socket.emit("switch_room", room);
                System.out.println(room.toString());
                Intent intent = new Intent(RoomActivity.this, TestConnectionActivity.class);
                RoomActivity.this.startActivity(intent);
                break;
        }
    }

}
