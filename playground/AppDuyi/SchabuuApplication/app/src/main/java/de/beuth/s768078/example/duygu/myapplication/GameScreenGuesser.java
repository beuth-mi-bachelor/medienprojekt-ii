package de.beuth.s768078.example.duygu.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Random;

;


public class GameScreenGuesser extends Activity {
    private Random randomGenerator = new Random();
    private final String[] easylist = {"cat", "sun", "cup", "ghost", "pie", "cow", "banana",
            "bug", "book", "jar",
            "snake", "light", "tree",
            "lips", "apple", "slide",
            "socks", "smile", "swing",
            "coat", "shoe", "water",
            "heart", "hat", "ocean",
            "kite", "dog", "mouth",
            "milk", "duck", "eyes", "bird", "boy",
            "apple", "person", "girl",
            "mouse", "ball", "house",
            "star", "nose", "bed",
            "whale", "jacket", "shirt",
            "beach", "egg",
            "face", "cookie", "cheese",
            "dance", "skip", "jumping", "jack",
            "shark", "chicken", "alligator",
            "chair", "robot", "head",
            "smile", "baseball", "bird",
            "happy", "scissors", "cheek",
            "back", "jump", "drink",
            "ice", "cream", "cone", "car", "airplane",
            "clap", "circle", "pillow",
            "pinch", "kick", "dog",
            "basketball", "sleep", "camera",
            "prayer", "elephant", "blink",
            "doll", "spider", "point",
            "kite", "homework", "ladybug",
            "bed", "bird", "gum",
            "book", "dress", "queen",
            "puppy", "happy", "doctor"
    };


    private final String[] hardlist = {
            "vision", "loiterer", "observatory",
            "century", "kilogram",
            "neutron", "stowaway", "psychologist",
            "exponential", "aristocrat", "eureka",
            "parody", "cartography",
            "philosopher", "tinting", "overture",
            "opaque", "ironic",
            "zero", "landfill", "implode",
            "czar", "armada", "crisp",
            "stockholder", "inquisition", "mooch",
            "gallop", "archaeologist", "blacksmith",
            "addendum", "upgrade",
            "acre", "twang", "mine",
            "protestant", "brunette", "stout",
            "quarantine", "tutor", "positive",
            "champion", "pastry", "tournament",
            "rainwater", "random",
            "lyrics", "ice", "clue",
            "slump", "ligament",
            "siesta", "pomp",
            "mine", "shaft", "dismantle", "weed", "killer",
            "tachometer", "unemployed", "portfolio",
            "pomp", "evolution", "apathy",
            "advertise", "roundabout", "sandbox",
            "conversation", "negotiate",
            "silhouette", "aisle", "pendulum",
            "retaliate", "mascot",
            "shipwreck", "comfort", "zone",
            "alphabetize", "application", "college",
            "lifestyle", "level", "invitation",
            "applesauce", "crumb", "loyalty",
            "corduroy", "shrink", "ray"};


    private final ArrayList<String> easyWords = new ArrayList<String>(Arrays.asList(easylist));
    private final ArrayList<String> hardWords = new ArrayList<String>(Arrays.asList(hardlist));
    private int curlevel = 0;
    private int curMan = 0;
    private ArrayList<Boolean> curAnswer;
    private String key;

    private void inputLetter(char c) {
        boolean isContain = false;
        for (int i = 0; i < key.length(); ++i) {
            char ans = key.charAt(i);
            if (c == ans) {
                isContain = true;
                curAnswer.set(i, true);
            }
        }
        if (curMan > 0 && isContain) {
            curMan--;
        }
        disableLetter(c);
    }

    private void disableLetter(char c) {
        char C = Character.toUpperCase(c);
        String buttonID = "button" + C;
        int resID = getResources().getIdentifier(buttonID, "id", "com.example.hangu");
        Button b = (Button) findViewById(resID);
        b.setEnabled(false);
    }

    private String getCurAnser() {
        String result = new String();
        for (int i = 0; i < curAnswer.size(); ++i) {
            if (curAnswer.get(i)) {
                result += (key.charAt(i) + " ");
            } else {
                result += "_ ";
            }
        }
        Log.d("test", result);

        return result;
    }

    private void selectKey() {
        int numOfBlanks = curlevel + 3;
        switch (curlevel) {
            case 0:
                key = easyWords.get(randomGenerator.nextInt(easyWords.size()));
                break;
            case 1:
                key = easyWords.get(randomGenerator.nextInt(easyWords.size()));
                break;
            case 2:
                key = hardWords.get(randomGenerator.nextInt(hardWords.size()));
                break;
        }

        Log.d("test", key);

        curAnswer = new ArrayList<Boolean>();
        for (int i = 0; i < key.length(); i++) {
            curAnswer.add(false);
        }
        HashSet<Character> letterSet = new HashSet<Character>();
        for (int i = 0; i < key.length(); ++i) {
            letterSet.add(key.charAt(i));
        }

        int numOfLetters = letterSet.size();
        int numOfShow = 0;
        if (numOfLetters > numOfBlanks) {
            curMan = 0;
            numOfShow = numOfLetters - numOfBlanks;
        } else if (numOfLetters < numOfBlanks) {
            curMan = numOfBlanks - numOfLetters;
            numOfShow = 0;
        }


        Log.d("test", "curMan" + curMan);

        Log.d("test", "numOfShow" + numOfShow);

        for (int i = 0; i < numOfShow; ++i) {
            int itemIndex = randomGenerator.nextInt(letterSet.size());
            int j = 0;
            for (Character c : letterSet) {
                if (j == itemIndex) {
                    inputLetter(c);
                    letterSet.remove(c);
                    break;
                }
                ++j;
            }
        }
    }

    private void checkResult() {
        boolean isComplete = true;
        for (boolean b : curAnswer) {
            if (!b) {
                isComplete = false;
                break;
            }
        }
        TextView textFill = (TextView) findViewById(R.id.textFill);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen_guesser);
        Intent intent = getIntent();
        curlevel = intent.getIntExtra("level",0);


            TextView textLevel=(TextView)findViewById(R.id.score2);
            TextView textFill = (TextView)findViewById(R.id.textFill);

            selectKey();
            switch (curlevel)
            {
                case 0:
                    textLevel.setText("Easy");
                    break;
                case 1:
                    textLevel.setText("Hard");
                    break;
                case 2:
                    textLevel.setText("Expert");
                    break;
            }
            textFill.setText(getCurAnser());

            checkResult();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_screen_guesser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
      //          NavUtil.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickLetter(View view) {
        curMan++;
        switch (view.getId())
        {
            case R.id.buttonA:  inputLetter('a');
                break;
            case R.id.buttonB:  inputLetter('b');
                break;
            case R.id.buttonC:  inputLetter('c');
                break;
            case R.id.buttonD:  inputLetter('d');
                break;
            case R.id.buttonE:  inputLetter('e');
                break;
            case R.id.buttonF:  inputLetter('f');
                break;
            case R.id.buttonG:  inputLetter('g');
                break;
            case R.id.buttonH:  inputLetter('h');
                break;
            case R.id.buttonI:  inputLetter('i');
                break;
            case R.id.buttonJ:  inputLetter('j');
                break;
            case R.id.buttonK:  inputLetter('k');
                break;
            case R.id.buttonL:  inputLetter('l');
                break;
            case R.id.buttonM:  inputLetter('m');
                break;
            case R.id.buttonN:  inputLetter('n');
                break;
            case R.id.buttonO:  inputLetter('o');
                break;
            case R.id.buttonP:  inputLetter('p');
                break;
            case R.id.buttonQ:  inputLetter('q');
                break;
            case R.id.buttonR:  inputLetter('r');
                break;
            case R.id.buttonS:  inputLetter('s');
                break;
            case R.id.buttonT:  inputLetter('t');
                break;
            case R.id.buttonU:  inputLetter('u');
                break;
            case R.id.buttonV:  inputLetter('v');
                break;
            case R.id.buttonW:  inputLetter('w');
                break;
            case R.id.buttonX:  inputLetter('x');
                break;
            case R.id.buttonY:  inputLetter('y');
                break;
            case R.id.buttonZ:  inputLetter('z');
                break;
        }

        checkResult();
    }
    //just an example
    public void renew(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //TODO
    }
    //just an example
    public void delete(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("level", curlevel);
        startActivity(intent);
        //TODO
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
