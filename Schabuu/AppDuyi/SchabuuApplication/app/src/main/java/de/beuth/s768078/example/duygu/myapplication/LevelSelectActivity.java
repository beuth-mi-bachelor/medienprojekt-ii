package de.beuth.s768078.example.duygu.myapplication;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class LevelSelectActivity extends Activity {

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the text view as the activity layout
        setContentView(R.layout.activity_level_select);
    }

    /**
     * Set up the {@link ActionBar}, if the API is available.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
     //           android.support.v4.app.NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void level_easy(View view) {
        Intent intent = new Intent(this, GameScreenGuesser.class);
        intent.putExtra("level", 0);
        startActivity(intent);
    }


    public void level_middle(View view) {
        Intent intent = new Intent(this, GameScreenGuesser.class);
        intent.putExtra("level", 1);
        startActivity(intent);
    }


    public void level_heavy(View view) {
        Intent intent = new Intent(this, GameScreenGuesser.class);
        intent.putExtra("level", 2);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
