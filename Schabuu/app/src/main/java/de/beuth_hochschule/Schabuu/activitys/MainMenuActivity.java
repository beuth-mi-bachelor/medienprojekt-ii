package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;

public class MainMenuActivity extends Activity {

    private ServerConnector _server;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Typeface geoBold = Typeface.createFromAsset(getAssets(), "font/geomanist_font_family/Geomanist-Bold.otf");

        username = loadUserNameFromStoarge("username", getApplicationContext());

        setContentView(R.layout.activity_main_menu);

        TextView playAloneView = (TextView)findViewById(R.id.play_alone);
        playAloneView.setTextSize(60);
        playAloneView.setTypeface(geoBold);
        playAloneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, RoomActivity.class);
                intent.putExtra("ROOM_MODE", "Random Room");
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });

        TextView playWithFriendsView = (TextView)findViewById(R.id.play_friends);
        playWithFriendsView.setTextSize(60);
        playWithFriendsView.setTypeface(geoBold);
        playWithFriendsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, PlayWithFriendsActivity.class));
            }
        });

        TextView settingsView = (TextView)findViewById(R.id.settings);
        settingsView.setTextSize(60);
        settingsView.setTypeface(geoBold);
        settingsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeName(getString(R.string.user_name_title), getString(R.string.user_name_msg));
            }
        });

    }

    private void connectToServer() {
        _server = ServerConnectorImplementation.getInstance("178.63.189.173", 80);
        /**
         * ESTABLISHING CONNECTION
         */
        // how to connect to Server
        if (!_server.isConnected()) {
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
                            _server.initPlayer(username, new Emitter.Listener() {
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
        }
    }

    private void getUserNameAlert(String title, String msg) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(msg);

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                username = input.getText().toString();
                if (username.equals("")) {
                    getUserNameAlert(getString(R.string.user_name_title), getString(R.string.user_name_msg));
                    return;
                }
                saveUserNameInStoarge(username);
            }
        });
        alert.create().show();
    }

    private void changeName(String title, String msg) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(msg);

        final EditText input = new EditText(this);
        input.setText(username);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                username = input.getText().toString();
                saveUserNameInStoarge(username);
                _server.changePlayername(username);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.create().show();
    }

    public void saveUserNameInStoarge(String username) {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("username", Context.MODE_PRIVATE);
            fos.write(username.getBytes());
            fos.flush();
            fos.close();
            loadUserNameFromStoarge("username", getApplicationContext());
            MainMenuActivity.this.connectToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadUserNameFromStoarge(String fileName, Context context) {
        String stringToReturn = " ";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                stringToReturn = stringBuilder.toString();
            }

            MainMenuActivity.this.connectToServer();

        } catch (FileNotFoundException e) {
            getUserNameAlert(getString(R.string.user_name_title), getString(R.string.user_name_msg));
            Log.e("TAG", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("TAG", "Can not read file: " + e.toString());
        }

        return stringToReturn;
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        if (_server != null) {
            _server.setPlayerInActive();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (_server != null) {
            _server.setPlayerActive();
        }
    }

    @Override
    public void onBackPressed() {
        MainMenuActivity.this.finish();
        System.exit(0);
    }

    public void initDone(final JSONObject data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
