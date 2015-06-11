package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;

public class PlayWithFriendsActivity extends Activity {

    Editable value;
    private ServerConnector _server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Typeface geoBold = Typeface.createFromAsset(getAssets(), "font/geomanist_font_family/Geomanist-Bold.otf");
        Typeface awesome = Typeface.createFromAsset(getAssets(), "font/font_awesome/FontAwesome.otf");
        _server = ServerConnectorImplementation.getInstance();

        setContentView(R.layout.activity_play_with_friends);

        Button backButton = (Button)findViewById(R.id.back_button);
        backButton.setTypeface(awesome);
        backButton.setTextColor(Color.parseColor("#ffffff"));
        backButton.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        backButton.setTextSize(36);
        backButton.setText("\uF060");
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PlayWithFriendsActivity.this, MainMenuActivity.class));
                PlayWithFriendsActivity.this.finish();
            }
        });

        TextView joinButton = (TextView)findViewById(R.id.join);
        joinButton.setTypeface(geoBold);
        joinButton.setTextSize(60);
        joinButton.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        joinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert("Join Room", "Enter Roomname", AlertType.JOIN);
            }
        });

        TextView createButton = (TextView)findViewById(R.id.create);
        createButton.setTypeface(geoBold);
        createButton.setTextSize(60);
        createButton.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert("Create Room", "Enter Roomname", AlertType.CREATE);
            }
        });


    }

    private void alert(final String title, String msg, final AlertType type) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(msg);

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                value = input.getText();

                switch (type) {
                    case CREATE:
                        //TO DO CREATE ROOM LOGIC
                        Intent intent = new Intent(PlayWithFriendsActivity.this, RoomActivity.class);
                        intent.putExtra("ROOM_NAME", value.toString());
                        intent.putExtra("ROOM_MODE", "PlayWithFriendsActivity");
                        startActivity(intent);

                        break;
                    case JOIN:
                        //TO DO ADD FIND ROOM LOGIC
                        Intent intent2 = new Intent(PlayWithFriendsActivity.this, RoomActivity.class);
                        intent2.putExtra("ROOM_NAME", value.toString());
                        intent2.putExtra("ROOM_MODE", "PlayWithFriendsActivity");
                        startActivity(intent2);
                        break;
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public enum AlertType {
        CREATE, JOIN
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

