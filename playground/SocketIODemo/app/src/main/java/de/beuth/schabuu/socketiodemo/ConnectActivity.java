package de.beuth.schabuu.socketiodemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import de.beuth.schabuu.user.User;

public class ConnectActivity extends Activity {

    private User currentUser = null;

    TelephonyManager teleManager;

    Button buttonConnect,
           buttonNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        teleManager = (TelephonyManager)getSystemService(ConnectActivity.TELEPHONY_SERVICE);


        buttonConnect = (Button) findViewById(R.id.connect_button);
        buttonNewUser = (Button) findViewById(R.id.new_user_button);

        buttonConnect.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if (currentUser != null) {
                    currentUser.setClientInstance(new ClientTask("192.168.1.101", 1337, getApplicationContext()));
                    currentUser.getClientInstance().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "no User is set", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonNewUser.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0) {
                currentUser = new User(teleManager.getDeviceId());
                Toast.makeText(getApplicationContext(), "logged in as" + currentUser.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
