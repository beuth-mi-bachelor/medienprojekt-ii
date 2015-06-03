package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import de.beuth_hochschule.Schabuu.R;

/**
 * Created by angi on 31.05.15.
 */
public class RoomActivity extends Activity {

    TextView player1View;
    TextView player2View;
    TextView player3View;
    TextView player4View;

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        player1View = (TextView) findViewById(R.id.player_one);
        player2View = (TextView) findViewById(R.id.player_two);
        player3View = (TextView) findViewById(R.id.player_three);
        player4View = (TextView) findViewById(R.id.player_four);

        MainMenuActivity.socket.on("room_list",onRoomList);
        MainMenuActivity.sEmit("get_rooms", null);

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String p1 = extras.getString("player1");
            String p2 = extras.getString("player2");
            String p3 = extras.getString("player3");
            String p4 = extras.getString("player4");
            if (p1 != null) {
                player1View.setText(p1);
            }
            if (p2 != null) {
                player1View.setText(p2);
            }
            if (p3 != null) {
                player1View.setText(p3);
            }
            if (p4 != null) {
                player1View.setText(p4);
            }
        }



        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(RoomActivity.this, MainMenuActivity.class));
            }
        });

        View playerOneView = findViewById(R.id.player_one);
        playerOneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(RoomActivity.this, GameActivity.class));
            }
        });

        View playerTwoView = findViewById(R.id.player_two);
        playerTwoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(RoomActivity.this, GameAvActivity.class));
            }
        });
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
    private Emitter.Listener onRoomList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject rooms = (JSONObject) args[0];

            try {
                Iterator x = rooms.keys();
                while (x.hasNext()) {
                    String key = (String) x.next();
                    JSONObject currentRoom = (JSONObject) rooms.get(key);
                    arrayList.add((String) currentRoom.get("name"));
                }
            } catch (JSONException ex) {
                System.err.println(ex.getMessage());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

}
