package com.demo.instaoff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    private SwipeRefreshLayout swipeContainer;


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
            Glide.with(this).load(dp).into(_igProfiler);
        }

        Log.d(TAG, " onCreate");

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    refreshAllData();
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

    private void refreshAllData(){
        getUserData();
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
        getUserData();
        getRecentPosts();
    }

    private void getUserData(){
        new InstagramRequest("", InstagramRequest.RequestType.GET_USER, this, new InstagramRequest.MyCallbackInterface() {
        @Override
        public void onCallback(String result) {
            gotUserData();
        }
        }).execute();
    }

    private void gotUserData() {
        Log.i(TAG, "gotUserData");
        getRecentPosts();
    }

    @OnClick(R.id.recent_button)
    void getRecentPosts() {
        new InstagramRequest("", InstagramRequest.RequestType.GET_RECENTS, this, new InstagramRequest.MyCallbackInterface() {
            @Override
            public void onCallback(String result) {
                gotRecentPosts();
            }
        }).execute();
    }

    private void gotRecentPosts() {
        Log.i(TAG, "gotRecentPosts");
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }

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
