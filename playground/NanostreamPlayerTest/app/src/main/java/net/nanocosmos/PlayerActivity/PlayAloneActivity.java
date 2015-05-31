package net.nanocosmos.PlayerActivity;

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

        View playAloneView= findViewById(R.id.back_button);
        playAloneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(PlayAloneActivity.this, MainMenuActivity.class));
            }
        });

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent i = new Intent("net.nanocosmos.PlayerActivity.GameActivity");
                    startActivity(i);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
