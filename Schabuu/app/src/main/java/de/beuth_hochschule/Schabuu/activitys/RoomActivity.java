package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;
import de.beuth_hochschule.Schabuu.ui.Typewriter;
import de.beuth_hochschule.Schabuu.util.Player;

public class RoomActivity extends Activity {

    private ServerConnector _server;
    private ArrayList<TextView> views = new ArrayList<TextView>();
    private ArrayList<ProgressBar> progBars = new ArrayList<ProgressBar>();
    private ArrayList<String> playerArray;
    private HashMap<String, Player> playerList;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Typeface geoBold = Typeface.createFromAsset(getAssets(), "font/geomanist_font_family/Geomanist-Bold.otf");
        Typeface awesome = Typeface.createFromAsset(getAssets(), "font/font_awesome/FontAwesome.otf");
        Intent intent = getIntent();
        setContentView(R.layout.activity_room);

        _server = ServerConnectorImplementation.getInstance();

        views.add((TextView) findViewById(R.id.player_one));
        views.add((TextView) findViewById(R.id.player_two));
        views.add((TextView) findViewById(R.id.player_three));
        views.add((TextView) findViewById(R.id.player_four));

        progBars.add((ProgressBar) findViewById(R.id.loadingBar_1));
        progBars.add((ProgressBar) findViewById(R.id.loadingBar_2));
        progBars.add((ProgressBar) findViewById(R.id.loadingBar_3));
        progBars.add((ProgressBar) findViewById(R.id.loadingBar_4));


        username = intent.getStringExtra("USERNAME");

        if (intent.getStringExtra("ROOM_MODE").equals("Random Room"))
            randomRoomSetup();
        else newRoomSetup(intent.getStringExtra("ROOM_NAME"));

        // BACK BUTTON
        Button backButton = (Button)findViewById(R.id.back_button);
        backButton.setTypeface(awesome);
        backButton.setTextColor(Color.parseColor("#ffffff"));
        backButton.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        backButton.setTextSize(36);
        backButton.setText("\uF060");
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

        // PLAYER ONE
        TextView playerOneView = (TextView) findViewById(R.id.player_one);
        playerOneView.setTypeface(geoBold);
        playerOneView.setTextSize(48);
        playerOneView.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        playerOneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newIntent = new Intent(RoomActivity.this, GameAvActivity.class);
                newIntent.putExtra("MODE", "NOCAM");
                startActivity(newIntent);
            }
        });

        //PLAYER TWO
        TextView playerTwoView = (TextView)findViewById(R.id.player_two);
        playerTwoView.setTypeface(geoBold);
        playerTwoView.setTextSize(48);
        playerTwoView.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        playerTwoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newIntent = new Intent(RoomActivity.this, GameAvActivity.class);
                newIntent.putExtra("MODE", "CAM");
                startActivity(newIntent);
            }
        });

        // PLAYER THREE
        TextView playerThreeView = (TextView)findViewById(R.id.player_three);
        playerThreeView.setTypeface(geoBold);
        playerThreeView.setTextSize(48);
        playerThreeView.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        playerThreeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(RoomActivity.this, GameActivity.class));
            }
        });

        // PLAYER FOUR
        TextView playerFourView = (TextView)findViewById(R.id.player_four);
        playerFourView.setTypeface(geoBold);
        playerFourView.setTextSize(48);
        playerFourView.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        playerFourView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(RoomActivity.this, GameActivity.class));
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


                        final JSONObject data = (JSONObject) args[0];
                        getPlayerHashMap(data);
                        createActivity();


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
                        views.get(i).setText(playerArray.get(i));
                        progBars.get(i).setVisibility(View.INVISIBLE);
                    } else {
                        views.get(i).setText(getString(R.string.waiting_for_player));
                        progBars.get(i).setVisibility(View.VISIBLE);
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

                        final JSONObject data = (JSONObject) args[0];
                        System.out.println("HALLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLOOOOOOOOOOOOOO");
                        getPlayerHashMap(data);
                        createActivity();

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
        System.out.println("ALLE DAAAAA");
        Player player = playerList.get(username);
        Intent intent = new Intent();
        System.out.println(player.toString());
        if (player.role.equals("guesser")) {
            intent = new Intent(RoomActivity.this, GameActivity.class);
            intent.putExtra("MODE", "AUDIO");
        }
        if (player.role.equals("audio")) {
            intent = new Intent(RoomActivity.this, GameAvActivity.class);
            intent.putExtra("MODE", "AUDIO");
        }
        if (player.role.equals("video")) {
            intent = new Intent(RoomActivity.this, GameAvActivity.class);
            intent.putExtra("MODE", "CAM");
        }
        intent.putExtra("USERNAME", ""+player.name);
        intent.putExtra("ROLE", ""+player.role);
        intent.putExtra("TEAM", ""+player.team);
        intent.putExtra("STREAM_AUDIO", ""+player.streamAudio);
        intent.putExtra("STREAM_VIDEO", ""+player.streamVideo);

        startActivity(intent);
    }

    private void getPlayerHashMap(JSONObject data) {
        playerList = new HashMap<String, Player>();
        try {
            System.out.println("PPPPPPPPPPPPP" + data.toString());
            final JSONObject game = (JSONObject) data.get("game");
            final JSONArray playersArray = (JSONArray) game.get("players");


            final JSONObject streamNameArray = (JSONObject) game.get("streamNames");
            final String streamAudio = (String) streamNameArray.get("audio");
            final String streamVideo = (String) streamNameArray.get("video");



            for (int i=0; i < playersArray.length(); i++) {
                JSONObject player = (JSONObject) playersArray.get(i);
                Player newPlayer = new Player((String) player.get("name"), (String) player.get("role"), player.get("team").toString(),streamAudio,streamVideo);
                playerList.put((String) player.get("name"), newPlayer);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goBackToMain() {
        Intent intent = new Intent(RoomActivity.this, MainMenuActivity.class);
        startActivity(intent);
        RoomActivity.this.finish();
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
