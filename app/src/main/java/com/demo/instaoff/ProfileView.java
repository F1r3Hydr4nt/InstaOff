package com.demo.instaoff;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.demo.instaoff.CONSTANTS.SP;
import static com.demo.instaoff.CONSTANTS.SP_USER_BIO;
import static com.demo.instaoff.CONSTANTS.SP_USER_FOLLOWERS;
import static com.demo.instaoff.CONSTANTS.SP_USER_FOLLOWS;
import static com.demo.instaoff.CONSTANTS.SP_USER_POSTS;
import static com.demo.instaoff.CONSTANTS.SP_USER_PROFILER;
import static com.demo.instaoff.CONSTANTS.SP_USER_FULL_NAME;
import static com.demo.instaoff.CONSTANTS.SP_TOKEN;
import static com.demo.instaoff.CONSTANTS.SP_USER_RECENT_JSON;

public class ProfileView extends AppCompatActivity {
    private static final String TAG = "ProfileView";
    @BindView(R.id.logout_button)
    Button _loginButton;
    @BindView(R.id.ig_profiler)
    ImageView _igProfiler;
    @BindView(R.id.ig_full_name)
    TextView igName;
    @BindView(R.id.ig_bio)
    TextView igBio;
    @BindView(R.id.ig_posts)
    TextView igPosts;
    @BindView(R.id.ig_following)
    TextView igFollowing;
    @BindView(R.id.ig_followers)
    TextView igFollowers;
    SharedPreferences spUser;
    String name;
    String profiler;
    String bio;
    String posts;
    String followers;
    String following;
    private SwipeRefreshLayout swipeContainer;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, " onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        ButterKnife.bind(this);
        spUser = getSharedPreferences(SP, MODE_PRIVATE);
        name = spUser.getString(SP_USER_FULL_NAME, null);
        igName.setText(name);
        profiler = spUser.getString(SP_USER_PROFILER, null);
        Glide.with(this).load(profiler).into(_igProfiler);
        //View
        loadProfileValues();
        if(spUser.getString(SP_USER_FOLLOWERS, null)==null){
            getUserData();
        }
        if(spUser.getString(SP_USER_RECENT_JSON, null)==null){
            getRecentPosts();
        }
        mAdapter = new PostViewAdapter(parseRecentPosts(spUser.getString(SP_USER_RECENT_JSON, null)),Glide.with(this));


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

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mAdapter.notifyDataSetChanged();
        }

        private void loadProfileValues(){
            bio = spUser.getString(SP_USER_BIO, null);
            posts = spUser.getString(SP_USER_POSTS, null);
            followers = spUser.getString(SP_USER_FOLLOWERS, null);
            following = spUser.getString(SP_USER_FOLLOWS, null);
            igBio.setText(bio);
            igPosts.setText(posts);
            igFollowers.setText(followers);
            igFollowing.setText(following);
            Log.i(TAG,"loadProfileValues");
        }

    private RecentPost[] parseRecentPosts(String jsonString){
        List<RecentPost> recentPosts = new ArrayList<>();
        try {
            Log.i(TAG,jsonString.toString());
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject!=null){
                    String caption = "";

                    //Log.i(TAG,"WTF?  ?? "+jsonObject.getString("caption"));
                    if( jsonObject.getString("caption")!="null"){
                        JSONObject captionObject = jsonObject.getJSONObject("caption");
                        caption = (captionObject!=null)?captionObject.getString("text"):"";
                    }

                    //Log.i(TAG,caption);

                    String unixTime = jsonObject.getString("created_time");
                    Date date = new Date(Long.parseLong(unixTime) *1000);
                    String dateString = date.toString();
                    String likeCount = jsonObject.getJSONObject("likes").getString("count");
                    String commentCount = jsonObject.getJSONObject("comments").getString("count");
                    String imageURL = jsonObject.getJSONObject("images").getJSONObject("standard_resolution").getString("url");//  HARD CODING the index here :|
                    RecentPost rP = new RecentPost(imageURL,caption,dateString,likeCount,commentCount);
                    //Log.i(TAG,rP.debug());
                    recentPosts.add(rP);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            refreshAllData();
        }
        return recentPosts.toArray(new RecentPost[0]);// Odd Java Optimisation https://stackoverflow.com/questions/4042434/converting-arrayliststring-to-string-in-java
    }

    private void refreshAllData(){
        getUserData();
        getRecentPosts();
    }

    private void getUserData(){
        new InstagramRequest("", InstagramRequest.RequestType.GET_USER, getSharedPreferences(SP, MODE_PRIVATE), new InstagramRequest.MyCallbackInterface() {
        @Override
        public void onCallback(String result) {
            gotUserData();
        }
        }).execute();
    }

    private void gotUserData() {
        loadProfileValues();
    }

    void getRecentPosts() {
        new InstagramRequest("", InstagramRequest.RequestType.GET_RECENTS, getSharedPreferences(SP, MODE_PRIVATE), new InstagramRequest.MyCallbackInterface() {
            @Override
            public void onCallback(String result) {
                gotRecentPosts();
            }
        }).execute();
    }

    private void gotRecentPosts() {
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
        ((PostViewAdapter)mRecyclerView.getAdapter()).recentPosts = parseRecentPosts(spUser.getString(SP_USER_RECENT_JSON, null));
        mRecyclerView.getAdapter().notifyDataSetChanged();
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

    @OnClick(R.id.logout_button)
    void onLogoutClick() {
        deleteToken();
        clearCookies();
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
