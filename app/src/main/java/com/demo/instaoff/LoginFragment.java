package com.demo.instaoff;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;
import static com.demo.instaoff.CONSTANTS.AUTHURL;
import static com.demo.instaoff.CONSTANTS.CLIENT_ID;
import static com.demo.instaoff.CONSTANTS.CLIENT_SECRET;
import static com.demo.instaoff.CONSTANTS.REDIRECT_URI;
import static com.demo.instaoff.CONSTANTS.SP;
import static com.demo.instaoff.CONSTANTS.SP_USER_PROFILER;
import static com.demo.instaoff.CONSTANTS.SP_USER_FULL_NAME;
import static com.demo.instaoff.CONSTANTS.SP_TOKEN;
import static com.demo.instaoff.CONSTANTS.TOKENURL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends DialogFragment {
    private static final String TAG = "LoginFragment";

    @BindView(R.id.input_username)
    EditText _usernameText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;

    private Dialog dialog;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    SharedPreferences spUser;
    SharedPreferences.Editor spEdit;
    private Unbinder unbinder;
    private String authURLFull;
    private String tokenURLFull;
    private String code;
    private String accessTokenString;
    private String dp;
    private String fullName;
    //Use AccountManager to store credentials??

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        authURLFull = AUTHURL + "client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&display=touch";
        tokenURLFull = TOKENURL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";
    }

    @OnClick(R.id.btn_login)
    public void loginButtonClick(View view) {
        Log.i(TAG, "Button Clicked");
        /*if (!validate()) {
            onLoginFailed();
            return;
        }*/

        _loginButton.setEnabled(false);

       /* final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.FullscreenTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();*/

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        //progressDialog.dismiss();
                    }
                }, 3000);*/

        setupWebviewDialog(authURLFull, username, password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        _usernameText.setText("anotheremail321");
        _passwordText.setText("1Asdfgh!");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean validate() {
        boolean valid = true;

        String email = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _usernameText.setError("enter a valid email address");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 24) {
            _passwordText.setError("between 4 and 24 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    MyWVClient wvClient;

    /*****  Show Instagram login page in a dialog *****************************/
    public void setupWebviewDialog(String url, String uN, String pW) {
        dialog = new Dialog(getContext());
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

        progressBar.setVisibility(View.VISIBLE);
        /*WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setSaveFormData(false);
        webView.clearCache(true);*/

        webView.loadUrl(url);
        dialog.setContentView(webView);
    }

    /*****  A client to know about WebView navigations  ***********************/
    class MyWVClient extends WebViewClient {
        LoginFragment parentFrag;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
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
            //https://instagram.com/accounts/logout/
            //https://www.instagram.com/oauth/authorize/?client_id=4216ac46ab584340adacc95d60a58944&redirect_uri=https://www.random.ie/instagram/access-token&response_type=code&display=touch
            //https://www.instagram.com/accounts/login/?force_classic_login=&client_id=4216ac46ab584340adacc95d60a58944&next=/oauth/authorize/%3Fclient_id%3D4216ac46ab584340adacc95d60a58944%26redirect_uri%3Dhttps%3A//www.random.ie/instagram/access-token%26response_type%3Dcode%26display%3Dtouch
            return false;
        }

        private String notLoginFailedCondition = "(document.getElementById('alerts')==null && document.getElementById('id_username')!==null" +
                "&& document.getElementById('id_password')!==null && document.getElementById('login-form')!==null)";

        private String username, password;
        private Intent parent;

        public void injectUserCreds(String uN, String pW, LoginFragment frag) {
            username = uN;
            password = pW;
            parentFrag = frag;
            Log.d(TAG, username + " " + password);
            Log.d(TAG, js);
            js = "javascript:" +
                    "var allElements = document.getElementsByTagName(" + '"' + "*" + '"' + "); console.log(allElements.length);" +
                    "for (var i = 0, n = allElements.length; i < n; ++i) {var el = allElements[i];if (el.id) { console.log(el.id);}}" +
                    "console.log(document.getElementById('alerts'));" +
                    "if(" + notLoginFailedCondition + "){" +//No alert so we can try submit the form
                    "document.getElementById('id_username').value='" + username + "';" +
                    "document.getElementById('id_password').value='" + password + "';" +
                    "document.getElementById('login-form').submit();" +
                    "(function() { return " + '"' + "LoginATTEMPTED" + '"' + "; })()}" +// Has to be encapsulated in a function
                    "else if(document.getElementById('login-form')!==null){" + "(function() { return " + '"' + "LoginFAILED" + '"' + "; })()};";// Has to be encapsulated in a function
        }

        // The java script string to execute in web view after page loaded
        // First line put a value in input box
        // Second line submit the form
        String js = "";

        final String js_login = "javascript:" +
                "document.getElementById('login-form').submit();";

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Log.d(TAG, s + " WebView Finished");
                    if (s.contains("FAILED")) {
                        Log.d(TAG, s + " WebView Finished");
                        //Log.d(TAG,view.getParent());
                        //getParent().finish();
                        //parentFrag.onLoginFailed();
                        onLoginFailed();
                    }
                }
            });

            progressBar.setVisibility(View.INVISIBLE);
            dialog.show();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

            Log.d(TAG, errorCode + " " + description + " onReceivedError");
        }

        private void submitLoginForm(WebView view) {
            view.evaluateJavascript(js_login, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Log.d(TAG, s + " Login Form Submit Result");
                }
            });
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
            Log.e(TAG, "Login error: " + temp[temp.length - 1]);
        }
    }

    /*****  AsyncTast to get user details after successful authorization ******/
    public class MyAsyncTask extends AsyncTask<URL, Integer, Long> {
        String code;

        public MyAsyncTask(String code) {
            Log.d(TAG, "Async: " + code);
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

                SharedPreferences.Editor spEdit;
                spUser = getActivity().getSharedPreferences(SP, MODE_PRIVATE);
                spEdit = spUser.edit();
                spEdit.putString(SP_TOKEN, accessTokenString);
                spEdit.putString(SP_USER_FULL_NAME, fullName);
                spEdit.putString(SP_USER_PROFILER, dp);
                spEdit.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(Long result) {
            Log.d(TAG, "Async: onPostExecute");
            progressBar.setVisibility(View.INVISIBLE);
            onLoginSuccess();
            //finish();
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

    public void onLoginSuccess() {
        dialog.dismiss();
        Intent i = new Intent(getContext(), ProfileView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        //getActivity().getFragmentManager().beginTransaction().remove((Fragment)this).commit();
    }

    public void onLoginFailed() {
        dialog.dismiss();
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();
        //();
        progressBar.setVisibility(View.INVISIBLE);
        _loginButton.setEnabled(true);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
