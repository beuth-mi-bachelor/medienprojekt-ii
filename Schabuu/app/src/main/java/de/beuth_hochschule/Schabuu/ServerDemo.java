package de.beuth_hochschule.Schabuu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import de.beuth_hochschule.Schabuu.data.Events;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;


public class ServerDemo extends Activity {


    // TODO: THIS VARIABLES ARE NORMALLY FROM SETTINGS
    public String playername = "testplayer";

    // EXAMPLE-BUTTONS
    Button getRandomRoom;
    Button switchRoomToTestroom;
    Button switchRoomToTadaroom;
    Button switchRoomToLobby;
    Button switchRoomToRandomRoom;
    Button getListOfRooms;
    Button readyToPlay;

    private ServerConnector _server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_demo);

        // instance of ServerConnector needed
        _server = ServerConnectorImplementation.getInstance("192.168.1.102", 1337);

        /**
         * ESTABLISHING CONNECTION
         */
        // how to connect to Server
        _server.connectToServer(
                // callback for connection established successfully
                new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // no arguments given

                        /**
                         * First thing after connection is established once
                         * -> create Player on Server and move him to lobby
                         */
                        _server.initPlayer(playername, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                JSONObject data = (JSONObject) args[0];
                                initDone(data);
                            }
                        });
                    }
                },
                // callback for connection error
                new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // no arguments given
                        System.err.println("a connection error occurred");
                    }
                }
        );

        /**
         * EXAMPLE: Get a random room name which is as full as possible
         */
        getRandomRoom = (Button) findViewById(R.id.button_demo_random_room);
        getRandomRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _server.getRandomRoom(new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // here is the roomName you wanted
                        final String roomName = (String) args[0];

                        // just to display it on device for debugging
                        System.out.println(roomName);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), roomName, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        });

        /**
         * EXAMPLE: Switching Room to TestRoom
         */
        switchRoomToTestroom = (Button) findViewById(R.id.button_switch_testroom);
        switchRoomToTestroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _server.switchRoom("testroom",
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

                                // just to display it on device for debugging
                                System.out.println("room updated: " + data.toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "room updated: " + data.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },
                        null
                );
            }
        });

        /**
         * EXAMPLE: Switching Room to TadaRoom
         */
        switchRoomToTadaroom = (Button) findViewById(R.id.button_switch_tadaroom);
        switchRoomToTadaroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _server.switchRoom("tadaroom",
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

                                // just to display it on device for debugging
                                System.out.println("room updated: " + data.toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "room updated: " + data.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },
                        null
                );
            }
        });

        /**
         * EXAMPLE: Switching Room back to Lobby
         */
        switchRoomToLobby = (Button) findViewById(R.id.button_switch_lobby);
        switchRoomToLobby.setOnClickListener(new View.OnClickListener() {
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
                            }
                        });
                    }
                });
            }
        });

        /**
         * EXAMPLE: Switching Room to a random room
         */
        switchRoomToRandomRoom = (Button) findViewById(R.id.button_switch_randomroom);
        switchRoomToRandomRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        /**
         * EXAMPLE: get list of all rooms
         */
        getListOfRooms = (Button) findViewById(R.id.button_list_rooms);
        getListOfRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _server.getRoomList(new Emitter.Listener() {
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
        });

        readyToPlay = (Button) findViewById(R.id.button_ready);
        readyToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _server.clientIsReady(new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // no args supplied

                        _server.addListener(Events.GAME_UPDATE, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                final JSONObject time = (JSONObject) args[0];
                                // just to display it on device for debugging
                                System.out.println("gametime is: " + time.toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "gametime is: " + time.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        // just to display it on device for debugging
                        System.out.println("game started");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "game started", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

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

    public void initDone(final JSONObject data) {
        // Only for debugging
        System.out.println(data.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Connected successfully: " + data.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server_demo, menu);
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
}
