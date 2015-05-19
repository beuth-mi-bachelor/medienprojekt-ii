package net.nanocosmos.PlayerActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import net.nanocosmos.NanostreamPlayerTest.R;


/**
 * A login screen that offers login via email/password.
 */
public class StreamSelectActivity extends Activity {


    // UI references.
    private AutoCompleteTextView streamView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_select);

        // Set up the login form.
        streamView = (AutoCompleteTextView) findViewById(R.id.stream);


        Button mEmailSignInButton = (Button) findViewById(R.id.stream_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(StreamSelectActivity.this,PlayerActivity.class);
                startActivity(in);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


}
