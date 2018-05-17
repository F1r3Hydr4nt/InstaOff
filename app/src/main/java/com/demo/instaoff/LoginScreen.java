package com.demo.instaoff;

import com.demo.instaoff.util.SystemUiHider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import static com.demo.instaoff.CONSTANTS.*;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LoginScreen extends AppCompatActivity {
    private static final String TAG = "LoginScreen";
    SharedPreferences spUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        spUser = getSharedPreferences(SP, MODE_PRIVATE);

        if (isLoggedIn()) {
            startActivity(new Intent(this, ProfileView.class));
            finish();
        }
        findViewById(R.id.dummy_button).setOnClickListener(mLoginButtonClickListener);
    }

    private boolean isLoggedIn() {
        String token = spUser.getString(SP_TOKEN, null);
        if (token != null) {
            Log.d(TAG, "Token found");
            return true;
        }
        Log.d(TAG, "NO Token");
        return false;
    }

    View.OnClickListener mLoginButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Button Clicked");
            showLoginDialog();
        }
    };

    private void showLoginDialog() {
        FragmentManager fm = getSupportFragmentManager();
        LoginWebViewFragment mLoginFragment = LoginWebViewFragment.newInstance();
        mLoginFragment.show(fm, "login_fragment");
    }
}
