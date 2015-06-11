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
import java.util.HashMap;
import java.util.Iterator;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.Events;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;
import de.beuth_hochschule.Schabuu.util.Player;

public class RoomActivity extends Activity {

    private ServerConnector _server;
    private ArrayList<TextView> views = new ArrayList<TextView>();
    private ArrayList<String> playerArray;
    private HashMap<String,Player> playerList;
    private String username;


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

        username = intent.getStringExtra("USERNAME");

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
                                _server.goBackToLobby(new Emitter.Listener() {
                                    @Override
                                    public void call(Object... args) {
                                        goBackToMain();
                                    }
                                });
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

                        _server.addListener(Events.GAME_READY, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                final JSONObject data = (JSONObject) args[0];
                                getPlayerHashMap(data);
                                createActivity();
                            }
                        });

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
                        updatePlayerList(data);
                    }
                }
        );
    }


    private void updatePlayerList(final JSONObject data) {

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

                        _server.addListener(Events.GAME_READY, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                final JSONObject data = (JSONObject) args[0];
                                getPlayerHashMap(data);
                                createActivity();
                            }
                        });

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
                        updatePlayerList(data);
                    }
                },
                new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Room is already full", Toast.LENGTH_SHORT).show();
                            }
                        });
                        RoomActivity.this.goBackToMain();
                    }
                }
        );
    }

    private void createActivity() {
        Player player = playerList.get(username);
        Intent intent = new Intent();
        if (player.role.equals("guesser")) {
            intent = new Intent(RoomActivity.this, GameActivity.class);
        }
        if (player.role.equals("audio")) {
            intent = new Intent(RoomActivity.this, GameAvActivity.class);
        }
        if (player.role.equals("video")) {
            intent = new Intent(RoomActivity.this, GameAvActivity.class);
        }
        intent.putExtra("USERNAME", player.name);
        intent.putExtra("ROLE", player.role);
        intent.putExtra("TEAM", player.team);
        intent.putExtra("STREAM_AUDIO", player.streamAudio);
        intent.putExtra("STREAM_VIDEO", player.streamVideo);

        startActivity(intent);
            }

    private void getPlayerHashMap(JSONObject data) {
        try {
            final JSONObject players = (JSONObject) data.get("players");
            Iterator x = players.keys();

            while (x.hasNext()) {
                String key = (String) x.next();
                JSONObject player = (JSONObject) players.get(key);
                Player newPlayer = new Player((String) player.get("name"),(String) player.get("role"),(String) player.get("team"),(String) player.get("streamAudio"),(String) player.get("streamVideo"));
                playerList.put((String) player.get("name"), newPlayer);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goBackToMain() {
        Intent intent = new Intent(RoomActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        _server.setPlayerInActive();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Always call the superclass method first
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
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        _server.setPlayerActive();
    }

}
