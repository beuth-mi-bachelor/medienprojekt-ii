package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import de.beuth_hochschule.Schabuu.R;

/**
 * Created by angi on 31.05.15.
 */
public class PlayWithFriendsActivity extends Activity {

    Editable value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_with_friends);

        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PlayWithFriendsActivity.this, MainMenuActivity.class));
            }
        });

        View joinButton = findViewById(R.id.join);
        joinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert("Join Room","Enter Roomname",AlertType.JOIN);
            }
        });

        View createButton = findViewById(R.id.create);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert("Create Room","Enter Roomname",AlertType.CREATE);
            }
        });


    }

    private void alert(final String title, String msg, final AlertType type){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(msg);

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                value = input.getText();

                switch (type){
                    case CREATE:
                        //TO DO CREATE ROOM LOGIC
                        startActivity(new Intent(PlayWithFriendsActivity.this, RoomActivity.class));
                        break;
                    case JOIN:
                        //TO DO ADD FIND ROOM LOGIC
                        startActivity(new Intent(PlayWithFriendsActivity.this, RoomActivity.class));
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
}

