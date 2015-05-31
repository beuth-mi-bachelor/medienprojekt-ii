package net.nanocosmos.PlayerActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.nanocosmos.NanostreamPlayerTest.R;

/**
 * Created by angi on 31.05.15.
 */
public class PlayWithFriendsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_with_friends);

        View playAloneView= findViewById(R.id.back_button);
        playAloneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PlayWithFriendsActivity.this, MainMenuActivity.class));
            }
        });
    }
}
