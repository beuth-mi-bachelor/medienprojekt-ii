package de.beuth_hochschule.Schabuu.activitys;

/**
 * Created by angi on 31.05.15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.beuth_hochschule.Schabuu.R;

public class MainMenuActivity extends Activity {

   public static com.github.nkzawa.socketio.client.Socket socket;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = loadUserNameFromStoarge("username",getApplicationContext());

        System.out.println("!!!! Username:"+username);

        setContentView(R.layout.activity_main_menu);

        View playAloneView= findViewById(R.id.play_alone);
        playAloneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               // startActivity(new Intent(MainMenuActivity.this, RoomActivity.class));
            }
        });

        View playWithFriendsView= findViewById(R.id.play_friends);
        playWithFriendsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, PlayWithFriendsActivity.class));
            }
        });

        View settingsView= findViewById(R.id.settings);
        settingsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }



    private void getUserNameAlert(String title,String msg){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(msg);

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                username = input.getText().toString();
                if (username.equals("")){
                   getUserNameAlert("Set Name","Please enter a valid name");
                    return;
                }
                saveUserNameInStoarge(username);



            }
        });
        alert.create().show();
    }
    public void saveUserNameInStoarge(String username){
       try {
            FileOutputStream fos = getApplicationContext().openFileOutput("username",Context.MODE_PRIVATE);
            fos.write(username.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadUserNameFromStoarge(String fileName, Context context)
    {
        String stringToReturn = " ";


        try
        {
            String sfilename = fileName;
                InputStream inputStream = context.openFileInput(sfilename);

                if ( inputStream != null )
                {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null )
                    {
                        stringBuilder.append(receiveString);
                    }
                    inputStream.close();
                    stringToReturn = stringBuilder.toString();
                }

        }
        catch (FileNotFoundException e)
        {
            getUserNameAlert("Set Name","Please enter a valid name");
            Log.e("TAG", "File not found: " + e.toString());
        }
        catch (IOException e)
        {
            Log.e("TAG", "Can not read file: " + e.toString());
        }

        return stringToReturn;
    }

}
