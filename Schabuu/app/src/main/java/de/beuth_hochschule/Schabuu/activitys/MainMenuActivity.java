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
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.Iterator;

import de.beuth_hochschule.Schabuu.R;

public class MainMenuActivity extends Activity {
/*String FILENAME = "username";
        String string = "hello world!";


        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();

            FileInputStream foo = openFileInput(FILENAME);

            byte[] b = new byte[1];
            foo.read(b);
            fos.close();
            String message = new String(b);
            System.out.print("!!!!!"+message);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    //Server config
    public static final String SERVER_ADDRESS = "192.168.1.3";
    public static final int PORT_NUMBER = 1337;

    public static com.github.nkzawa.socketio.client.Socket socket;

    String username;
    String roomName="bla";
    StringBuffer stringBuffer = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadUser();
        System.out.println("!!!!!!!!!!!!!!!!!!"+ username);



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

    private void loadUser(){
        File myFile = new File(Environment.getExternalStorageDirectory().getPath()+"/textfile.txt");
        if(!myFile.exists()){
            System.out.println("1");
            getUserNameAlert("Set Name","Please enter your name");
            return;
        }
        System.out.println("2");

        try {
            BufferedReader br = new BufferedReader(new FileReader(myFile));
            StringBuilder sb = new StringBuilder();
            String line  = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            if(line == null){
                getUserNameAlert("Set Name","Please enter your name");
                return;
            }
            System.out.println("3");
            username = line;

        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            return;
        }
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
                System.out.println("HUHUHUHU"+username);
                saveUserNameInStoarge(username);



            }
        });
        alert.create().show();
    }
    public void saveUserNameInStoarge(String username){
        System.out.println("SAVED");
        try {
            File myFile = new File(Environment.getExternalStorageDirectory().getPath()+"/textfile.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(username);
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
