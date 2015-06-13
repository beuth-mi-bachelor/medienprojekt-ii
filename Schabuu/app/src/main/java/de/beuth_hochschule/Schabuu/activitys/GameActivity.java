package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.Events;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;
import de.beuth_hochschule.Schabuu.util.Player;
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
    private TextView iconView;
    private LinearLayout loadingBackground;
    private Vibrator myVib;
    private Intent intent;
    private Typeface awesome;
    TextView time_left;
    private Typeface geoBold;
    private static final String LOG_TAG = "GameActivity";

    private ServerConnector _server;
    private SolutionHolder solutionHolder;
    TextView score1;
    TextView score2;

    private HashMap<String, Player> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        geoBold = Typeface.createFromAsset(getAssets(), "font/geomanist_font_family/Geomanist-Bold.otf");
         awesome = Typeface.createFromAsset(getAssets(), "font/fontello.ttf");

        _server = ServerConnectorImplementation.getInstance();

        intent = getIntent();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_screen_guesser);



         score1 = (TextView) findViewById(R.id.score1);
         score2 = (TextView) findViewById(R.id.score2);

        score1.setText(intent.getStringExtra("SCORE0"));
        score2.setText(intent.getStringExtra("SCORE1"));

         time_left = (TextView) findViewById(R.id.time_left);
        Button buttonDelete = (Button) findViewById(R.id.buttonDelete);
        Button buttonRenew = (Button) findViewById(R.id.buttonRenew);

        score1.setTypeface(geoBold);
        score2.setTypeface(geoBold);
        time_left.setTypeface(geoBold);

        buttonDelete.setTypeface(awesome);
        buttonRenew.setTypeface(awesome);

        buttonDelete.setText("\ue80d");
        buttonRenew.setText("\ue80f");

        buttonDelete.setTextSize(34);
        buttonRenew.setTextSize(36);

        buttonDelete.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        buttonRenew.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));

        buttonDelete.setTextColor(Color.parseColor("#ffffff"));
        buttonRenew.setTextColor(Color.parseColor("#ffffff"));


        score1.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        score2.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        time_left.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));

        time_left.setText("00:00");





        //Button deleteButton = (Button) findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solutionHolder.deleteChar();
                myVib.vibrate(50);
            }
        });

        //Button renewButton = (Button) findViewById(R.id.buttonRenew);
        buttonRenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solutionHolder.deleteWord();
                myVib.vibrate(50);
            }
        });

        /*
        RecievingUtils utils = new RecievingUtils(this, license, strStreamUrl, intent.getStringExtra("STREAM_VIDEO"), authUser, authPass);
        SurfacePlayerView surfaceView = (SurfacePlayerView) findViewById(R.id.view);
        surfaceView.getHolder().addCallback(utils.GetPlayer());

        RecievingUtils utils2 = new RecievingUtils(this, license, strStreamUrl, intent.getStringExtra("STREAM_AUDIO"), authUser, authPass);
        SurfacePlayerView surfaceView2 = (SurfacePlayerView) findViewById(R.id.view_sound);
        surfaceView2.getHolder().addCallback(utils.GetPlayer());

        utils.StartPlayer();
        utils2.StartPlayer();
        */
        //getLetters("KATZE",20);
        createLoadingScreen();
        //setTimeOut();

        if(intent.getStringExtra("FIRSTROUND").equals("YES")){
            startGame();
        }else{
            startNewRound();
        }
    }

    private void createLoadingScreen() {
        descriptionTextView = (TextView) findViewById(R.id.description);
        descriptionTextView.setTypeface(awesome);
        descriptionTextView.setTextColor(Color.parseColor("#ffffff"));
        descriptionTextView.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        descriptionTextView.setTextSize(36);
        descriptionTextView.setText(getResources().getString(R.string.guesser_description));



        teamTextView = (TextView) findViewById(R.id.team_value);
        teamTextView.setTypeface(awesome);
        teamTextView.setTextColor(Color.parseColor("#ffffff"));
        teamTextView.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        teamTextView.setTextSize(36);
        teamTextView.append(" "+intent.getStringExtra("TEAM"));


        iconView = (TextView) findViewById(R.id.imageView);
        iconView.setTypeface(awesome);
        iconView.setTextColor(Color.parseColor("#ffffff"));
        iconView.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        iconView.setTextSize(36);
        iconView.setText("\uf11c");

        loadingBackground = (LinearLayout) findViewById(R.id.loading_screen);
/*
        if (intent.getStringExtra("TEAM").equals("0")) {
            loadingBackground.setBackgroundColor(getResources().getColor(R.color.schabuu_green));
        } else {
            loadingBackground.setBackgroundColor(getResources().getColor(R.color.schabuu_blue));
        }
*/

        /* DELETE AFTER IT */
        LinearLayout linLaySolution = (LinearLayout) findViewById(R.id.solutionLayout);
        solutionHolder = new SolutionHolder(linLaySolution, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                _server.emit(Events.GAME_SOLUTION, null);
            }
        }, GameActivity.this, "HAUS", geoBold);
        getLetters("HAUS",16);
        /* DELETE AFTER IT */

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingBackground.setVisibility(View.GONE);
            }
        });
    }

    private void getPlayerHashMap(JSONObject data) {
        playerList = new HashMap<String, Player>();
        try {
            System.out.println("PPPPPPPPPPPPP" + data.toString());

            final JSONArray playersArray = (JSONArray) data.get("players");
            for (int i=0; i < playersArray.length(); i++) {
                JSONObject player = (JSONObject) playersArray.get(i);
                Player newPlayer = new Player((String) player.get("name"), (String) player.get("role"), player.get("team").toString(),intent.getStringExtra("STREAM_AUDIO"),intent.getStringExtra("STREAM_VIDEO"));
                playerList.put((String) player.get("name"), newPlayer);
            }

            JSONObject scores = (JSONObject) data.get("score");
            String score1 = scores.getString("0");
            String score2 = scores.getString("1");

            createActivity(score1,score2);


        } catch (JSONException e) {
            e.printStackTrace();
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
                    sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startGame();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingBackground.setVisibility(View.GONE);
                        }
                    });

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
        int i = 0;
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.button_panel);
        LinearLayout buttonLayout2 = (LinearLayout) findViewById(R.id.button_panel2);
        for (String s : result){
            i++;
            if(i >8){
                createButton(s, buttonLayout2);
            } else {
                createButton(s,buttonLayout);
            }
        }
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
    public void createButton(String letter,LinearLayout buttonLayout) {
        Typeface geoBold = Typeface.createFromAsset(getAssets(), "font/geomanist_font_family/Geomanist-Bold.otf");
        Button btn = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;
        params.setMargins(2, 0, 2, 0);
        //btn.setLayoutParams(params);
        //btn.setBackgroundResource(R.drawable.buttoncolor1);
        btn.setBackgroundColor(Color.parseColor("#0D485C"));
        btn.setText(letter);
        btn.setTag("button_" + letter);
        btn.setTypeface(geoBold);
        btn.setTextColor(Color.parseColor("#ffffff"));
        btn.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String buttonText = b.getText().toString();
                solutionHolder.addChar(buttonText);
                myVib.vibrate(50);
            }
        });
        btn.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        buttonLayout.addView(btn,params);
    }



    private void createActivity(String score1,String score2) {
        Player player = playerList.get(intent.getStringExtra("USERNAME"));
        Intent intent = new Intent();
        System.out.println(player.toString());
        if (player.role.equals("guesser")) {
            intent = new Intent(GameActivity.this, GameActivity.class);
            intent.putExtra("MODE", "AUDIO");
        }
        if (player.role.equals("audio")) {
            intent = new Intent(GameActivity.this, GameAvActivity.class);
            intent.putExtra("MODE", "AUDIO");
        }
        if (player.role.equals("video")) {
            intent = new Intent(GameActivity.this, GameAvActivity.class);
            intent.putExtra("MODE", "CAM");
        }
        intent.putExtra("USERNAME", ""+player.name);
        intent.putExtra("ROLE", ""+player.role);
        intent.putExtra("TEAM", ""+player.team);
        intent.putExtra("STREAM_AUDIO", ""+player.streamAudio);
        intent.putExtra("STREAM_VIDEO", ""+player.streamVideo);
        intent.putExtra("USERNAME",intent.getStringExtra("USERNAME"));
        intent.putExtra("SCORE0",score1);
        intent.putExtra("SCORE1",score2);

        intent.putExtra("FIRSTROUND","NO");

        startActivity(intent);
    }

    private  void startNewRound(){
        _server.addListener(Events.GAME_ROUND_START,new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject gameData = (JSONObject) args[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "game started: " + gameData.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject words = (JSONObject) gameData.get("word");
                            System.out.println(words);
                            Iterator<?> keys = words.keys();

                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                JSONArray names = (JSONArray) words.get(key);
                                System.out.println(key);
                                System.out.println(names.toString());

                                getLetters(key,16);

                                LinearLayout linLaySolution = (LinearLayout) findViewById(R.id.solutionLayout);
                                solutionHolder = new SolutionHolder(linLaySolution, new Emitter.Listener() {
                                    @Override
                                    public void call(Object... args) {
                                        _server.emit(Events.GAME_SOLUTION, null);
                                    }
                                }, GameActivity.this, key, geoBold);

                            }
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });

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
                        try {
                            time_left.setText(data.get("time").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });



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
                        try {
                            JSONObject words = (JSONObject) gameData.get("word");
                            System.out.println(words);
                            Iterator<?> keys = words.keys();

                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                JSONArray names = (JSONArray) words.get(key);
                                System.out.println(key);
                                System.out.println(names.toString());

                                getLetters(key,16);

                                LinearLayout linLaySolution = (LinearLayout) findViewById(R.id.solutionLayout);
                                solutionHolder = new SolutionHolder(linLaySolution, new Emitter.Listener() {
                                    @Override
                                    public void call(Object... args) {
                                        _server.emit(Events.GAME_SOLUTION, null);
                                    }
                                }, GameActivity.this, key, geoBold);

                            }
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
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
                                try {
                                    time_left.setText(data.get("time").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

                        getPlayerHashMap(data);

                        // just to display it on device for debugging
                        System.out.println("round ended: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "round ended: " + data.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        _server.removeListener(Events.GAME_ROUND_START);
                        _server.removeListener(Events.GAME_ROUND_END);
                        _server.removeListener(Events.GAME_UPDATE);
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
