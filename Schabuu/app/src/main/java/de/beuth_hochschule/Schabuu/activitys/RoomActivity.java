package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;

/**
 * Created by angi on 31.05.15.
 */
public class RoomActivity extends Activity {

    TextView player1View;
    TextView player2View;
    TextView player3View;
    TextView player4View;

    private ServerConnector _server;

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        _server = ServerConnectorImplementation.getInstance();

        player1View = (TextView) findViewById(R.id.player_one);
        player2View = (TextView) findViewById(R.id.player_two);
        player3View = (TextView) findViewById(R.id.player_three);
        player4View = (TextView) findViewById(R.id.player_four);

        _server.joinRandomRoom(
                new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final JSONObject data = (JSONObject) args[0];

                        // just to display it on device for debugging
                        System.out.println("room was switched: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },
                new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // no args supplied

                        // just to display it on device for debugging
                        System.out.println("game is ready");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "game is ready", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                },
                new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final JSONObject data = (JSONObject) args[0];
                        try {
                            final JSONObject players = (JSONObject) data.get("players");
                            Iterator x = players.keys();
                            ArrayList<String> playerArray = new ArrayList<String>();

                            while (x.hasNext()){
                                String key = (String) x.next();
                                JSONObject player = (JSONObject) players.get(key);
                                String name = (String) player.get("name");
                                playerArray.add(name);
                            }
                            System.out.println(playerArray);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // just to display it on device for debugging
                        System.out.println("room updated: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "room updated: " + data.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );

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
                player2View.setText(p2);
            }
            if (p3 != null) {
                player3View.setText(p3);
            }
            if (p4 != null) {
                player4View.setText(p4);
            }
        }


        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        _server.setPlayerInActive();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        _server.setPlayerActive();
    }

}
