package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;

public class GameEndActivity extends Activity {

    private TextView winnerIs;
    private TextView winnerTeam;
    private TextView scoreOne;
    private TextView scoreTwo;
    private TextView trophySmall;
    private TextView trophyBig;
    private Button endButton;
    private Typeface awesome;
    private Intent intent;

    private ServerConnector _server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _server = ServerConnectorImplementation.getInstance();

        intent = getIntent();

        Typeface geoBold = Typeface.createFromAsset(getAssets(), "font/geomanist_font_family/Geomanist-Bold.otf");
        awesome = Typeface.createFromAsset(getAssets(), "font/font_awesome/FontAwesome.otf");

        setContentView(R.layout.activity_game_end);

        winnerIs = (TextView) findViewById(R.id.winnerIs);
        winnerTeam = (TextView) findViewById(R.id.teamWin);
        scoreOne = (TextView) findViewById(R.id.scoreOne);
        scoreTwo = (TextView) findViewById(R.id.scoreTwo);
        trophySmall = (TextView) findViewById(R.id.smallTrophy);
        trophyBig = (TextView) findViewById(R.id.bigTrophy);
        endButton = (Button) findViewById(R.id.button_end);

        winnerIs.setTypeface(geoBold);
        winnerTeam.setTypeface(geoBold);
        scoreOne.setTypeface(geoBold);
        scoreTwo.setTypeface(geoBold);
        trophySmall.setTypeface(awesome);
        trophyBig.setTypeface(awesome);
        endButton.setTypeface(geoBold);

        scoreOne.setText("Score \n");
        scoreTwo.setText("Score \n");
        trophySmall.setText("\uf091");
        trophyBig.setText("\uf091");
        winnerTeam.setText("");

        winnerIs.setTextSize(40);
        scoreOne.setTextSize(20);
        scoreTwo.setTextSize(20);
        trophySmall.setTextSize(70);
        trophyBig.setTextSize(100);

        endButton.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));

        winnerTeam.setText(intent.getStringExtra("WINNER_TEAM"));
        System.out.println(intent.getStringExtra("SCORE_1"));
        scoreOne.append(intent.getStringExtra("SCORE_1"));
        scoreTwo.append(intent.getStringExtra("SCORE_2"));
        winnerIs.setText(intent.getStringExtra("WINNER_TEAM"));

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _server.goBackToLobby(new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final JSONObject data = (JSONObject) args[0];

                        // just to display it on device for debugging
                        System.out.println("room was switched: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
                                _server.goBackToLobby(new Emitter.Listener() {
                                    @Override
                                    public void call(Object... args) {
                                        goBackToMain();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void goBackToMain() {
        Intent intent = new Intent(GameEndActivity.this, MainMenuActivity.class);
        startActivity(intent);
        GameEndActivity.this.finish();
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        _server.setPlayerInActive();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Always call the superclass method first
        _server.goBackToLobby(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];

                // just to display it on device for debugging
                System.out.println("room was switched: " + data.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
