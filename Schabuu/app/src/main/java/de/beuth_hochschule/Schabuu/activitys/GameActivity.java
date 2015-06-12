package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import java.util.Random;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.Events;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;
import de.beuth_hochschule.Schabuu.ui.SurfacePlayerView;
import de.beuth_hochschule.Schabuu.util.RecievingUtils;
import de.beuth_hochschule.Schabuu.util.SolutionHolder;

public class GameActivity extends Activity {

    private static final String license = "nlic:1.2:LiveEnc:3.0:LvApp=1,LivePlg=1,H264DEC=1,H264ENC=1,RTMPsrc=1,RtmpMsg=1,RTMPx=3,NoMsg=1,Ic=0:adr,ios:20150409,20150707::0:0:smartfrog-431775-1:ncpt:90ddf42ffe204d9e1e6ac99e9df92aba";
    // rtmp://ws2.nanocosmos.net/live
    // rtmps://55087e44b8b38.streamlock.net/vod
    private static final String strStreamUrl = "rtmp://ws2.nanocosmos.net/live";
    private String strStreamname = "PaulTest";

    private static String authUser = "";
    private static String authPass = "";

    private TextView textView;
    private TextView descriptionTextView;
    private TextView teamTextView;
    private ImageView iconView;
    private LinearLayout loadingBackground;

    private Intent intent;


    private static final String LOG_TAG = "GameActivity";

    private ServerConnector _server;
    private SolutionHolder solutionHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _server = ServerConnectorImplementation.getInstance();

        intent = getIntent();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_screen_guesser);
        //loadingBackground.setVisibility(View.GONE);
        createLoadingScreen();

        getLetters("KATZE", 10);

        LinearLayout linLaySolution = (LinearLayout) findViewById(R.id.solutionLayout);
        solutionHolder = new SolutionHolder(linLaySolution, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("!!!!!!!!!!!! SOLUTION DONE!");
            }
        }, GameActivity.this, "KATZE");

        ImageView deleteButton = (ImageView) findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solutionHolder.deleteChar();
            }
        });

        ImageView renewButton = (ImageView) findViewById(R.id.buttonRenew);
        renewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solutionHolder.deleteWord();
            }
        });

        RecievingUtils utils = new RecievingUtils(this, license, strStreamUrl, strStreamname, authUser, authPass);
        SurfacePlayerView surfaceView = (SurfacePlayerView) findViewById(R.id.view);
        surfaceView.getHolder().addCallback(utils.GetPlayer());

        utils.StartPlayer();
        //setTimeOut();
    }

    private void createLoadingScreen() {
        descriptionTextView = (TextView) findViewById(R.id.description);
        teamTextView = (TextView) findViewById(R.id.team_value);
        iconView = (ImageView) findViewById(R.id.imageView);
        loadingBackground = (LinearLayout) findViewById(R.id.loading_screen);
        descriptionTextView.setText(getResources().getString(R.string.guesser_description));

        if (intent.getStringExtra("TEAM") != null && intent.getStringExtra("TEAM").equals("0")) {
            loadingBackground.setBackgroundColor(getResources().getColor(R.color.schabuu_green));
        } else
            loadingBackground.setBackgroundColor(getResources().getColor(R.color.schabuu_blue));
        if (intent.getStringExtra("TEAM") != null) {
            teamTextView.append(intent.getStringExtra("TEAM"));
        }
        iconView.setImageDrawable(getResources().getDrawable(R.drawable.guesser_icon));
        if (intent.getStringExtra("STREAM_VIDEO") != null) {
            strStreamname = intent.getStringExtra("STREAM_VIDEO");
        }
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

    public void setTimeOut() {
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
/*
                    startGame();
*/
                    loadingBackground.setVisibility(View.GONE);
                }
            }
        };
        timerThread.start();
    }

    //gets the word to be described and the maximum number of the buttons to be shown
    //creates then a new button in our game screen layout guesser
    public void getLetters(String word, int numberOfMaximumLetters) {
        String[] letters = new String[word.length()];
        for (int i = 0; i < word.length(); i++) {
            letters[i] = Character.toString(word.charAt(i));
        }
        int numberFillLetters = numberOfMaximumLetters - letters.length;
        String[] randomLetters = getRandomLetters(numberFillLetters);
        String result[] = new String[randomLetters.length + letters.length];
        for (int i = 0, j = 0; j < result.length; ++i) {
            if (i < randomLetters.length) {
                result[j++] = randomLetters[i];
            }
            if (i < letters.length) {
                result[j++] = letters[i];
            }
        }
        for (String s : result) createButton(s);
    }

    //returns array with random capital letters by given number
    public String[] getRandomLetters(int number) {
        Random r = new Random();
        String[] randomLetters = new String[number];

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < number; i++) {
            randomLetters[i] = Character.toString(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        return randomLetters;
    }

    //creates a new Button in the LinearLayout button_panel in activity_gamescreen_av.xml and sets
    //letter from itself in TextView when clicked
    public void createButton(String letter) {
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.button_panel);
        Button btn = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.width = 0;
        params.setMargins(1, 1, 1, 1);
        btn.setLayoutParams(params);
        btn.setBackgroundResource(R.drawable.buttoncolor1);
        btn.setText(letter);
        btn.setTag("button_" + letter);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String buttonText = b.getText().toString();
                solutionHolder.addChar(buttonText);
            }
        });
        buttonLayout.addView(btn);
    }

    private void startGame() {

        // here player tells server that he wants to start the game
        _server.clientIsReady(new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                final JSONObject gameData = (JSONObject) args[0];

                // just to display it on device for debugging
                System.out.println("game started: " + gameData.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "game started: " + gameData.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                _server.addListener(Events.GAME_UPDATE, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final JSONObject data = (JSONObject) args[0];
                        // just to display it on device for debugging
                        System.out.println("gametime is: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "gametime is: " + data.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                _server.addListener(Events.GAME_ROUND_START, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        final JSONObject data = (JSONObject) args[0];
                        // just to display it on device for debugging
                        System.out.println("next round started: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "next round started: " + data.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                _server.addListener(Events.GAME_ROUND_END, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        final JSONObject data = (JSONObject) args[0];
                        // just to display it on device for debugging
                        System.out.println("round ended: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "round ended: " + data.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                _server.addListener(Events.GAME_END, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        _server.removeListener(Events.GAME_UPDATE);
                        _server.removeListener(Events.GAME_ROUND_END);
                        _server.removeListener(Events.GAME_ROUND_START);
                        _server.removeListener(Events.GAME_END);
                        // just to display it on device for debugging
                        System.out.println("game has ended");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "game has ended", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


            }
        });

    }
}
