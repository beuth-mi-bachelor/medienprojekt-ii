package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

        username = loadUserNameFromStoarge("username", getApplicationContext());

        System.out.println("!!!! Username:" + username);

        setContentView(R.layout.activity_main_menu);

        _server = ServerConnectorImplementation.getInstance("192.168.1.3", 1337);
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
                        _server.initPlayer(username, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                JSONObject data = (JSONObject) args[0];
                                initDone(data);
                            }
                        });
                        System.out.println("CONNECTED TO SERVER");
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

        View playAloneView = findViewById(R.id.play_alone);
        playAloneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, RoomActivity.class);
                intent.putExtra("ROOM_MODE", "Random Room");
                startActivity(intent);
            }
        });

        View playWithFriendsView = findViewById(R.id.play_friends);
        playWithFriendsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, PlayWithFriendsActivity.class));
            }
        });

        View settingsView = findViewById(R.id.settings);
        settingsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
            }
        });

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
                    getUserNameAlert("Set Name", "Please enter a valid name");
                    return;
                }
                saveUserNameInStoarge(username);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadUserNameFromStoarge(String fileName, Context context) {
        String stringToReturn = " ";


        try {
            String sfilename = fileName;
            InputStream inputStream = context.openFileInput(sfilename);

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

        } catch (FileNotFoundException e) {
            getUserNameAlert("Set Name", "Please enter a valid name");
            Log.e("TAG", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("TAG", "Can not read file: " + e.toString());
        }

        return stringToReturn;
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

}
