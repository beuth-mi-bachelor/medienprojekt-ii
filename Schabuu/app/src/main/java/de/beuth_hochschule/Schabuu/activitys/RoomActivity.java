package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class RoomActivity extends Activity {

    private ServerConnector _server;
    private ArrayList<TextView> views = new ArrayList<TextView>();
    private ArrayList<String> playerArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_room);

        _server = ServerConnectorImplementation.getInstance();

        views.add((TextView) findViewById(R.id.player_one));
        views.add((TextView) findViewById(R.id.player_two));
        views.add((TextView) findViewById(R.id.player_three));
        views.add((TextView) findViewById(R.id.player_four));

        if (intent.getStringExtra("ROOM_MODE").equals("Random Room"))
            randomRoomSetup();
        else newRoomSetup(intent.getStringExtra("ROOM_NAME"));

        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _server.goBackToLobby(new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final JSONObject data = (JSONObject) args[0];

                        // just to display it on device for debugging
                        System.out.println("room was switched: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    }
                });
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

    public void randomRoomSetup() {
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
                            playerArray = new ArrayList<String>();

                            while (x.hasNext()) {
                                String key = (String) x.next();
                                JSONObject player = (JSONObject) players.get(key);
                                String name = (String) player.get("name");
                                playerArray.add(name);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // just to display it on device for debugging
                        System.out.println("room updated: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Integer maxPlayers;
                                try {
                                    maxPlayers = (Integer) data.get("maxPlayers");
                                } catch (JSONException e) {
                                    maxPlayers = 4;
                                    e.printStackTrace();
                                }
                                for (int i = 0; i < maxPlayers; i++) {
                                    if (i < playerArray.size()) {
                                        (views.get(i)).setText(playerArray.get(i));
                                    } else {
                                        (views.get(i)).setText(getString(R.string.waiting_for_player));
                                    }
                                }
                                Toast.makeText(getApplicationContext(), "room updated: " + data.toString(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
        );
    }

    public void newRoomSetup(String roomName) {
        _server.switchRoom(roomName,
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
                            playerArray = new ArrayList<String>();

                            while (x.hasNext()) {
                                String key = (String) x.next();
                                JSONObject player = (JSONObject) players.get(key);
                                String name = (String) player.get("name");
                                playerArray.add(name);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // just to display it on device for debugging
                        System.out.println("room updated: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = playerArray.size() - 1; i >= 0; i--) {
                                    (views.get(i)).setText(playerArray.get(i));
                                    System.out.println(playerArray.get(i) + "" + i);
                                }
                                Toast.makeText(getApplicationContext(), "room updated: " + data.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );
    }


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
