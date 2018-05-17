package com.demo.instaoff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.demo.instaoff.CONSTANTS.SP;
import static com.demo.instaoff.CONSTANTS.SP_USER_PROFILER;
import static com.demo.instaoff.CONSTANTS.SP_USER_FULL_NAME;
import static com.demo.instaoff.CONSTANTS.SP_TOKEN;

public class ProfileView extends AppCompatActivity {
    private static final String TAG = "ProfileView";

    @BindView(R.id.logout_button)
    Button _loginButton;
    @BindView(R.id.user_button)
    Button _get_user_button;
    @BindView(R.id.recent_button)
    Button _get_recent_button;
    @BindView(R.id.ig_profiler)
    ImageView _igProfiler;
    @BindView(R.id.ig_full_name)
    TextView igName;

    SharedPreferences spUser;
    String name, dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        ButterKnife.bind(this);
        spUser = getSharedPreferences(SP, MODE_PRIVATE);
        name = spUser.getString(SP_USER_FULL_NAME, null);
        dp = spUser.getString(SP_USER_PROFILER, null);

        if (name != null) {
            igName.setText(name);
            //Glide.with(this).load(dp).into(ivProfile);
        }

    }

    @OnClick(R.id.logout_button)
    void onLogoutClick() {
        deleteToken();
        clearCookies();
        Intent intent = new Intent(ProfileView.this, LoginScreen.class);
        //shouldExitAppHERE
        startActivity(intent);
    }

    @OnClick(R.id.user_button)
    void onUserClick() {
        new InstagramRequest("", InstagramRequest.RequestType.GET_USER, this, new InstagramRequest.MyCallbackInterface() {
            @Override
            public void onCallback(String result) {
                gotUserData();
            }
        }).execute();
    }

    private void gotUserData() {
        Log.i(TAG, "gotUserData");
    }

    @OnClick(R.id.recent_button)
    void onRecentClick() {
        /*new InstagramRequest("", InstagramRequest.RequestType.GET_RECENTS, this, new InstagramRequest.MyCallbackInterface() {
            @Override
            public void onCallback(String result) {
                gotRecentData();
            }
        }).execute();*/
    }

    private void gotRecentData() {
        Log.i(TAG, "gotRecentData");
    }

    private void deleteToken() {
        SharedPreferences spUser;
        SharedPreferences.Editor spEdit;
        spUser = getSharedPreferences(SP, MODE_PRIVATE);
        spEdit = spUser.edit();
        spEdit.remove(SP_TOKEN);
        spEdit.commit();
    }

    private void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
            public void onReceiveValue(Boolean b) {
                Log.d(TAG, b + " CookieManager Remove Finished");
            }
        });
    }
}
