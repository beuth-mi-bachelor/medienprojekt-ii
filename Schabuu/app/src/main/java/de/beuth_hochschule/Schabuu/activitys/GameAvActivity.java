package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.Events;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;
import de.beuth_hochschule.Schabuu.util.Player;
import de.beuth_hochschule.Schabuu.util.StreamingUtils;

public class GameAvActivity extends Activity {

    private String license_stream = "nlic:1.2:LiveEnc:1.1:LivePlg=1,H264ENC=1,MP4=1,RTMPsrc=1,RtmpMsg=1,RTMPm=4,RTMPx=3,Resz=1,Demo=1,Ic=1,NoMsg=1:adr:20150429,20151213::0:0:nanocosmos-471231-28:ncpt:28cd49a163eaf61a48484c9e17a5d808";
    private String serverUrl = "rtmp://ws2.nanocosmos.net/live";
    private String streamName = "foobar";


    private static final String license = "nlic:1.2:LiveEnc:3.0:LvApp=1,LivePlg=1,H264DEC=1,H264ENC=1,RTMPsrc=1,RtmpMsg=1,RTMPx=3,NoMsg=1,Ic=0:adr,ios:20150409,20150707::0:0:smartfrog-431775-1:ncpt:90ddf42ffe204d9e1e6ac99e9df92aba";
    private static final String strStreamUrl = "rtmp://ws2.nanocosmos.net/live";
    private String strStreamname = "PaulTest";

    private static String authUser = "";
    private static String authPass = "";



    private static final String LOG_TAG = "GameAvActivity";

    private ServerConnector _server;

    private StreamingUtils utils = null;

    private TextView descriptionTextView;
    private TextView teamTextView;
    private TextView iconView;
    private LinearLayout loadingBackground;
    private ArrayList<TextView> views = new ArrayList<TextView>();
    private Intent intent;
    Typeface awesome;
    TextView solution;
    TextView time_left;

    TextView score1;
    TextView score2;

    private HashMap<String, Player> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface geoBold = Typeface.createFromAsset(getAssets(), "font/geomanist_font_family/Geomanist-Bold.otf");
        awesome = Typeface.createFromAsset(getAssets(), "font/font_awesome/FontAwesome.otf");
         intent = getIntent();
        _server = ServerConnectorImplementation.getInstance();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gamescreen_av);


        TextView word_one = (TextView) findViewById(R.id.word_one);
        TextView word_two = (TextView) findViewById(R.id.word_two);
        TextView word_three = (TextView) findViewById(R.id.word_three);
        TextView word_four = (TextView) findViewById(R.id.word_four);
        TextView word_five = (TextView) findViewById(R.id.word_five);
        TextView word_six = (TextView) findViewById(R.id.word_six);

        views.add(word_one);
        views.add(word_two);
        views.add(word_three);
        views.add(word_four);
        views.add(word_five);
        views.add(word_six);


        score1 = (TextView) findViewById(R.id.score1);
        score2 = (TextView) findViewById(R.id.score2);

        score1.setText(intent.getStringExtra("SCORE0"));
        score2.setText(intent.getStringExtra("SCORE1"));

         time_left = (TextView) findViewById(R.id.time_left);
         solution = (TextView) findViewById(R.id.loesungswort);
        TextView player_name = (TextView) findViewById(R.id.player_name);

        word_one.setTypeface(geoBold);
        word_two.setTypeface(geoBold);
        word_three.setTypeface(geoBold);
        word_four.setTypeface(geoBold);
        word_five.setTypeface(geoBold);
        word_six.setTypeface(geoBold);
        score1.setTypeface(geoBold);
        score2.setTypeface(geoBold);
        time_left.setTypeface(geoBold);
        solution.setTypeface(geoBold);
        player_name.setTypeface(geoBold);

        player_name.setAlpha(0.5f);

        word_one.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_two.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_three.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_four.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_five.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_six.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        score1.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        score2.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        time_left.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        solution.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));

        time_left.setText("00:00");


        createLoadingScreen();
        //setTimeOut();


        startGame();

        /*

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!STREAM"+intent.getStringExtra("STREAM_VIDEO"));
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!AUDIO"+intent.getStringExtra("STREAM_AUDIO"));
       if(intent.getStringExtra("MODE").equals("CAM")){
           utils = new StreamingUtils(serverUrl, intent.getStringExtra("STREAM_VIDEO"), license, authUser, authPass, (SurfacePlayerView) findViewById(R.id.view), getApplicationContext(),false);
           utils.toggleStreaming();
       }
       if(intent.getStringExtra("MODE").equals("AUDIO")){
           //utils = new StreamingUtils(serverUrl, intent.getStringExtra("STREAM_AUDIO"), license, authUser, authPass, (SurfacePlayerView) findViewById(R.id.view_audio), getApplicationContext(),true);
           //utils.toggleStreaming();


           RecievingUtils utils = new RecievingUtils(this, license, strStreamUrl,intent.getStringExtra("STREAM_VIDEO") , authUser, authPass);
           SurfacePlayerView surfaceView = (SurfacePlayerView) findViewById(R.id.view);
           surfaceView.getHolder().addCallback(utils.GetPlayer());
           utils.StartPlayer();


       }
       */

    }
    public void setTimeOut() {
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //startGame();
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
        if (intent.getStringExtra("MODE").equals("CAM")) {
            iconView.setText("\uf183");
        }else{
            iconView.setText("\uf130");
        }
        loadingBackground = (LinearLayout) findViewById(R.id.loading_screen);
        if (intent.getStringExtra("TEAM").equals("0")) {
            loadingBackground.setBackgroundColor(getResources().getColor(R.color.schabuu_green));
        } else {
            loadingBackground.setBackgroundColor(getResources().getColor(R.color.schabuu_blue));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingBackground.setVisibility(View.GONE);
            }
        });
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createActivity() {
        Player player = playerList.get(intent.getStringExtra("USERNAME"));
        Intent intent = new Intent();
        System.out.println(player.toString());
        if (player.role.equals("guesser")) {
            intent = new Intent(GameAvActivity.this, GameActivity.class);
            intent.putExtra("MODE", "AUDIO");
        }
        if (player.role.equals("audio")) {
            intent = new Intent(GameAvActivity.this, GameAvActivity.class);
            intent.putExtra("MODE", "AUDIO");
        }
        if (player.role.equals("video")) {
            intent = new Intent(GameAvActivity.this, GameAvActivity.class);
            intent.putExtra("MODE", "CAM");
        }
        intent.putExtra("USERNAME", ""+player.name);
        intent.putExtra("ROLE", ""+player.role);
        intent.putExtra("TEAM", ""+player.team);
        intent.putExtra("STREAM_AUDIO", ""+player.streamAudio);
        intent.putExtra("STREAM_VIDEO", ""+player.streamVideo);
        intent.putExtra("USERNAME",intent.getStringExtra("USERNAME"));
        intent.putExtra("SCORE0",1);
        intent.putExtra("SCORE1",1);

        startActivity(intent);
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
                        try {
                            JSONObject words = (JSONObject) gameData.get("word");
                            System.out.println(words);
                            Iterator<?> keys = words.keys();

                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                JSONArray names = (JSONArray) words.get(key);

                                System.out.println(key);
                                System.out.println(names.toString());
                                solution.setText(key);
                                for (int i = 0; i < views.size(); i++) {
                                    views.get(i).setText(names.getInt(i));
                                }

                            }


                        } catch (JSONException e1) {
                            e1.printStackTrace();
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
                        createActivity();

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
