package net.nanocosmos.nanoStream.activities;

import net.nanocosmos.nanoStream.R;
import net.nanocosmos.nanoStream.ui.PreferenceFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class PreferenceActivity extends FragmentActivity {
	Fragment prefs = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference);
	

        Fragment existingFragment = getFragmentManager().findFragmentById(R.id.preferenceLayout);
        if (existingFragment == null || !existingFragment.getClass().equals(PreferenceFragment.class))
        {
            getFragmentManager().beginTransaction()
                .replace(R.id.preferenceLayout, new PreferenceFragment())
                .commit();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference, menu);
		return true;
	}

}
