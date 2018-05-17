package com.demo.instaoff;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;
import static com.demo.instaoff.CONSTANTS.APIURL;
import static com.demo.instaoff.CONSTANTS.AUTHURL;
import static com.demo.instaoff.CONSTANTS.CLIENT_ID;
import static com.demo.instaoff.CONSTANTS.CLIENT_SECRET;
import static com.demo.instaoff.CONSTANTS.REDIRECT_URI;
import static com.demo.instaoff.CONSTANTS.SP;
import static com.demo.instaoff.CONSTANTS.SP_USER_BIO;
import static com.demo.instaoff.CONSTANTS.SP_USER_FOLLOWERS;
import static com.demo.instaoff.CONSTANTS.SP_USER_FOLLOWS;
import static com.demo.instaoff.CONSTANTS.SP_USER_POSTS;
import static com.demo.instaoff.CONSTANTS.SP_USER_PROFILER;
import static com.demo.instaoff.CONSTANTS.SP_USER_FULL_NAME;
import static com.demo.instaoff.CONSTANTS.SP_TOKEN;
import static com.demo.instaoff.CONSTANTS.SP_USER_WEBSITE;
import static com.demo.instaoff.CONSTANTS.TOKENURL;

public class InstagramRequest extends AsyncTask<URL, Integer, Long> {
    private static final String TAG = "InstagramRequest";
    public enum RequestType {TOKEN, GET_USER, GET_RECENTS}
    String code;
    RequestType type;
    SharedPreferences spUser;
    SharedPreferences.Editor spEdit;
    public static final String authURLFull = AUTHURL + "client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&display=touch";
    private String tokenURLFull = TOKENURL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";
    private String apiURL = APIURL;
    private String accessTokenString;
    private String profiler;
    private String fullName;
    private String bio;
    private String website;
    private String posts;
    private String follows;
    private String followers;
    private Activity parentActivity;

    //define callback interface
    public interface MyCallbackInterface {
        void onCallback(String result);
    }

    MyCallbackInterface callback;

    public InstagramRequest(String code, RequestType type, Activity parentActivity, MyCallbackInterface callback) {
        Log.d(TAG, "Async: " + code);
        this.code = code;
        this.type = type;
        this.parentActivity = parentActivity;
        this.callback = callback;
        spUser = parentActivity.getSharedPreferences(SP, MODE_PRIVATE);
    }

    protected Long doInBackground(URL... urls) {
        long result = 0;
        String requestURL = "";
        String httpRequestType = "";
        String outputStreamData = "";
        try {
            switch (this.type) {
                case TOKEN:
                    requestURL = tokenURLFull;
                    httpRequestType = "POST";
                    outputStreamData = "client_id=" + CLIENT_ID +
                            "&client_secret=" + CLIENT_SECRET +
                            "&grant_type=authorization_code" +
                            "&redirect_uri=" + REDIRECT_URI +
                            "&code=" + code;
                    break;
                case GET_USER:
                    requestURL = apiURL + "/users/self/?access_token=" + spUser.getString(SP_TOKEN, "");
                    httpRequestType = "GET";
                    outputStreamData = "";
                    break;
                case GET_RECENTS:
                    requestURL = apiURL + "/users/self/media/recent/?access_token=" + spUser.getString(SP_TOKEN, "");
                    httpRequestType = "GET";
                    outputStreamData = "";
                    break;
                    ///users/self/media/recent
            }
            URL url = new URL(requestURL);
            Log.i(TAG, url.toString());
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod(httpRequestType);
            httpsURLConnection.setDoInput(true);
            if(httpRequestType=="POST"){
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(outputStreamData);
                outputStreamWriter.flush();
            }
            String response = streamToString(httpsURLConnection.getInputStream());
            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
            parseResponse(jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void parseResponse(JSONObject jsonObject){
        spEdit = spUser.edit();
        try{
            Log.i(TAG, jsonObject.toString(1));
            switch (this.type) {
                case TOKEN:
                    accessTokenString = jsonObject.getString("access_token"); //Here is your ACCESS TOKEN
                    profiler = jsonObject.getJSONObject("user").getString("profile_picture");
                    fullName = jsonObject.getJSONObject("user").getString("full_name"); //This is how you can get the user info. You can explore the JSON sent by Instagram as well to know what info you got in a response
                    spEdit.putString(SP_TOKEN, accessTokenString);
                    spEdit.putString(SP_USER_FULL_NAME, fullName);
                    spEdit.putString(SP_USER_PROFILER, profiler);
                    spEdit.commit();
                    break;
                case GET_USER:
                    bio = jsonObject.getJSONObject("user").getString("bio");
                    website = jsonObject.getJSONObject("user").getString("website");
                    posts = jsonObject.getJSONObject("user").getJSONObject("counts").getString("media");
                    follows = jsonObject.getJSONObject("user").getJSONObject("counts").getString("follows");
                    followers = jsonObject.getJSONObject("user").getJSONObject("counts").getString("followed_by");
                    spEdit.putString(SP_USER_BIO, bio);
                    spEdit.putString(SP_USER_WEBSITE, website);
                    spEdit.putString(SP_USER_POSTS, posts);
                    spEdit.putString(SP_USER_FOLLOWS, follows);
                    spEdit.putString(SP_USER_FOLLOWERS, followers);
                    break;
                case GET_RECENTS:
                    break;

                ///users/self/media/recent
            }
        }catch (Exception e){

        }
    }

    protected void onPostExecute(Long result) {
        Log.d(TAG, "Async: onPostExecute");
        this.callback.onCallback("Success");
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
}
