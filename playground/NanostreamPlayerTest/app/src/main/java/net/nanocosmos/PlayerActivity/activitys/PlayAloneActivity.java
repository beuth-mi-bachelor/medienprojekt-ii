package net.nanocosmos.PlayerActivity.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.nanocosmos.NanostreamPlayerTest.R;

/**
 * Created by angi on 31.05.15.
 */
public class PlayAloneActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alone);

        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PlayAloneActivity.this, MainMenuActivity.class));
            }
        });

        View playerOneView = findViewById(R.id.player_one);
        playerOneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PlayAloneActivity.this, GameActivity.class));
            }
        });

        View playerTwoView = findViewById(R.id.player_two);
        playerTwoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PlayAloneActivity.this, GameAvActivity.class));
            }
        });
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
