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
import static com.demo.instaoff.CONSTANTS.SP_DP;
import static com.demo.instaoff.CONSTANTS.SP_NAME;
import static com.demo.instaoff.CONSTANTS.SP_TOKEN;

public class ProfileView extends AppCompatActivity {
    private static final String TAG = "ProfileView";

    @BindView(R.id.logout_button)
    Button _loginButton;
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
        name = spUser.getString(SP_NAME, null);
        dp = spUser.getString(SP_DP, null);

        if (name != null){
            igName.setText(name);
            //Glide.with(this).load(dp).into(ivProfile);
        }

    }

    @OnClick(R.id.logout_button) void onLogoutClick() {

        deleteToken();
        clearCookies();

        Intent intent = new Intent(ProfileView.this, LoginScreen.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("EXIT", true);
        startActivity(intent);
    }
    private void deleteToken(){
        SharedPreferences spUser;
        SharedPreferences.Editor spEdit;
        spUser = getSharedPreferences(SP, MODE_PRIVATE);

        spEdit = spUser.edit();
        spEdit.remove(SP_TOKEN);
        spEdit.commit();
    }

    private void clearCookies(){
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                public void onReceiveValue(Boolean b) {
                    Log.d(TAG,b+" CookieManager Remove Finished");
                }
            });
    }
}
