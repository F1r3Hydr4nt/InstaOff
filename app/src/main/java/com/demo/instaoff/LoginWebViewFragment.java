package com.demo.instaoff;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import static com.demo.instaoff.CONSTANTS.REDIRECT_URI;

public class LoginWebViewFragment extends DialogFragment {
    private static final String TAG = "LoginWebViewFragment";
    private Dialog dialog;
    private String code;

    public LoginWebViewFragment() {
    }

    public static LoginWebViewFragment newInstance() {
        LoginWebViewFragment fragment = new LoginWebViewFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        setupWebviewDialog(InstagramRequest.authURLFull, "anotheremail321", "1Asdfgh!");
        return textView;
    }

    public void onBackPressedFragment() {
        Log.d(TAG, "OnBackPressed");
        dismiss();
    }

    MyWVClient wvClient;

    /*****  Show Instagram login page in a dialog *****************************/
    public void setupWebviewDialog(String url, String uN, String pW) {
        dialog = new Dialog(getContext()) {//Create the dialog to contain the WebView
            @Override
            public void onBackPressed() { //We must override the web view dialog to close its containing fragment onBack()
                //do your stuff
                onBackPressedFragment();
                super.onBackPressed();
            }
        };

        dialog.setTitle("Insta Login");
        Log.i(TAG, url);
        WebView webView = new WebView(getContext());
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        wvClient = new MyWVClient();
        wvClient.injectUserCreds(uN, pW, this);
        webView.setWebViewClient(wvClient);//ForceLogin here
        webView.getSettings().setJavaScriptEnabled(true);
        Log.d(TAG, "setupWebviewDialog " + url);
        webView.loadUrl(url);
        dialog.setContentView(webView);
    }

    /*****  A client to know about WebView navigations  ***********************/
    class MyWVClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //
            view.setVisibility(View.VISIBLE);
            if (url.startsWith(REDIRECT_URI)) {
                Log.d(TAG, "Overriding " + url);//
                handleUrl(url);
                view.setVisibility(View.GONE);
                return true;
            }
            Log.d(TAG, "Not overriding " + url);
            return false;
        }

        private String notLoginFailedCondition = "(document.getElementById('alerts')==null && document.getElementById('id_username')!==null" +
                "&& document.getElementById('id_password')!==null && document.getElementById('login-form')!==null)";

        private String username, password;
        // The java script string to execute in web view after page loaded
        // First line put a value in input box
        // Second line submit the form
        String js = "";

        public void injectUserCreds(String uN, String pW, LoginWebViewFragment frag) {
            username = uN;
            password = pW;
            Log.d(TAG, username + " " + password);
            Log.d(TAG, js);
            js = "javascript:" +
                    "if(" + notLoginFailedCondition + "){" +//No alert so we can try submit the form
                    "document.getElementById('id_username').value='" + username + "';" +
                    "document.getElementById('id_password').value='" + password + "';}";
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Log.d(TAG, s + " WebView Finished");
                }
            });
            dialog.show();
        }
    }

    /*****  Check webview url for access token code or error ******************/
    public void handleUrl(String url) {
        if (url.contains("code")) {
            String temp[] = url.split("=");
            code = temp[1];
            //new MyAsyncTask(code).execute();
            new InstagramRequest(code, InstagramRequest.RequestType.TOKEN, getActivity(), new InstagramRequest.MyCallbackInterface() {
                @Override
                public void onCallback(String result) {
                    onLoginSuccess();
                }
            }).execute();
        } else if (url.contains("error")) {
            String temp[] = url.split("=");
            Log.e(TAG, "Login error: " + temp[temp.length - 1]);
        }
    }

    public void onLoginSuccess() {
        dialog.dismiss();
        Intent i = new Intent(getContext(), ProfileView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        //getActivity().getFragmentManager().beginTransaction().remove((Fragment)this).commit();
    }
}
