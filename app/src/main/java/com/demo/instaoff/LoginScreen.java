package com.demo.instaoff;

import com.demo.instaoff.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.net.Uri;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.demo.instaoff.CONSTANTS.*;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LoginScreen extends AppCompatActivity{
       // implements LoginFragment.OnFragmentInteractionListener{
    private static final String TAG = "LoginScreen";
    private String authURLFull;
    private String tokenURLFull;
    private String code;
    private String accessTokenString;
    private String dp;
    private String fullName;

    private Dialog dialog;
    ProgressBar progressBar;

    SharedPreferences spUser;
    SharedPreferences.Editor spEdit;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    //private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
   // private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    //private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    //private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
   // private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        spUser = getSharedPreferences(SP, MODE_PRIVATE);

        if (isLoggedIn()){
            startActivity(new Intent(this, ProfileView.class));
            finish();
        }

        authURLFull = AUTHURL + "client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&display=touch";
        tokenURLFull = TOKENURL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        spEdit = spUser.edit();
        spEdit.clear();
        spEdit.commit();
/*
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });
*/
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.dummy_button).setOnClickListener(mLoginButtonClickListener);
    }
    //https://stackoverflow.com/questions/16998492/androidauto-login-for-instagram-api\
        /*
        email instaoffagram@yandex.com
        insta UID instaoffagram
        passwords 1Asdfgh!

        Redirect URI https://www.showoff.ie/instagram/access-token
        Client ID  4216ac46ab584340adacc95d60a58944
        Client Secret 88818fa446e84e909086fff4390799e5
         */

    /*****  Show Instagram login page in a dialog *****************************/
    public void setupWebviewDialog(String url) {
        dialog = new Dialog(this);
        dialog.setTitle("Insta Login");
        Log.i(TAG,url);
        WebView webView = new WebView(this);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new MyWVClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        dialog.setContentView(webView);
    }

    /*****  A client to know about WebView navigations  ***********************/
    class MyWVClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            view.clearCache(true);

            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(REDIRECT_URI)) {
                handleUrl(url);
                return true;
            }
            return false;
        }

        // The java script string to execute in web view after page loaded
        // First line put a value in input box
        // Second line submit the form
        final String js = "";/*"javascript:"+
                "document.getElementById('id_username').value='instaoffagram';"+
                "document.getElementById('id_password').value='1Asdfgh!';";*/

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Log.d(TAG,s+" WebView Finished");
                }
            });

            progressBar.setVisibility(View.INVISIBLE);
            dialog.show();
        }
    }

    /*****  Check webview url for access token code or error ******************/
    public void handleUrl(String url) {
        if (url.contains("code")) {
            String temp[] = url.split("=");
            code = temp[1];
            new MyAsyncTask(code).execute();

        } else if (url.contains("error")) {
            String temp[] = url.split("=");
            Log.e(TAG, "Login error: "+temp[temp.length - 1]);
        }
    }

    /*****  AsyncTast to get user details after successful authorization ******/
    public class MyAsyncTask extends AsyncTask<URL, Integer, Long> {
        String code;

        public MyAsyncTask(String code) {
            Log.d(TAG,code);
            this.code = code;
        }

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                URL url = new URL(tokenURLFull);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write("client_id=" + CLIENT_ID +
                        "&client_secret=" + CLIENT_SECRET +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=" + REDIRECT_URI +
                        "&code=" + code);

                outputStreamWriter.flush();
                String response = streamToString(httpsURLConnection.getInputStream());
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                accessTokenString = jsonObject.getString("access_token"); //Here is your ACCESS TOKEN
                dp = jsonObject.getJSONObject("user").getString("profile_picture");
                fullName = jsonObject.getJSONObject("user").getString("full_name"); //This is how you can get the user info. You can explore the JSON sent by Instagram as well to know what info you got in a response

                spEdit = spUser.edit();
                spEdit.putString(SP_TOKEN, accessTokenString);
                spEdit.putString(SP_NAME, fullName);
                spEdit.putString(SP_DP, dp);
                spEdit.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(Long result) {
            dialog.dismiss();
            progressBar.setVisibility(View.INVISIBLE);
            startActivity(new Intent(LoginScreen.this, ProfileView.class));
            finish();
        }
    }

    /*****  Converting stream to string ***************************************/
    public static String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

            } finally {
                is.close();
            }
            str = sb.toString();
        }
        return str;
    }

    private boolean isLoggedIn(){
        String token = spUser.getString(SP_TOKEN, null);
        if (token != null){
        //    return true;
        }
        return false;
    }
/*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }*/


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
   /*View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };*/

    View.OnClickListener mLoginButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"Button Clicked");
            //showLoginDialog();
               setupWebviewDialog(authURLFull);
                progressBar.setVisibility(View.VISIBLE);
        }
    };
/*
    private void showLoginDialog(){
        FragmentManager fm = getSupportFragmentManager();
        LoginFragment mLoginFragment = LoginFragment.newInstance();
        mLoginFragment.show(fm, "login_fragment");
    }


        //rest code is omitted

        @Override
        public void onFragmentInteraction(Uri uri){
            //you can leave it empty
        }*/
/*
    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };
*/
    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    /*private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }*/

}
