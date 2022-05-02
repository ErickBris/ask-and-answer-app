package ua.com.qascript.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.com.qascript.android.adapter.StreamListAdapter;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.model.Answer;
import ua.com.qascript.android.util.AnswerInterface;
import ua.com.qascript.android.util.CustomRequest;


public class HashtagsActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, AnswerInterface {

    Toolbar toolbar;

    SwipeRefreshLayout mContentContainer;
    RelativeLayout mErrorScreen, mLoadingScreen, mEmptyScreen;
    LinearLayout mContentScreen, mAdMobCont;

    ListView hashtagsListView;
    Button mRetryBtn;


    private ArrayList<Answer> hashtagsList;

    private StreamListAdapter hashtagsAdapter;

    long answerId = 0;
    int arrayLength = 0;
    Boolean loadingMore = false;
    Boolean viewMore = false;

    String hashtag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtags);

        Intent i = getIntent();

        hashtag = i.getStringExtra("hashtag");

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        if (hashtag != null) {

            getSupportActionBar().setTitle(hashtag);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mAdMobCont = (LinearLayout) findViewById(R.id.AdMobCont);
        mEmptyScreen = (RelativeLayout) findViewById(R.id.emptyScreen);
        mErrorScreen = (RelativeLayout) findViewById(R.id.errorScreen);
        mLoadingScreen = (RelativeLayout) findViewById(R.id.loadingScreen);
        mContentContainer = (SwipeRefreshLayout) findViewById(R.id.contentContainer);
        mContentContainer.setOnRefreshListener(this);

        mContentScreen = (LinearLayout) findViewById(R.id.contentScreen);

        mRetryBtn = (Button) findViewById(R.id.retryBtn);

        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().isConnected()) {

                    showLoadingScreen();

                    answerId = 0;
                    getHashtags();
                }
            }
        });

        hashtagsListView = (ListView) findViewById(R.id.hashtagsListView);

        hashtagsList = new ArrayList<Answer>();
        hashtagsAdapter = new StreamListAdapter(HashtagsActivity.this, hashtagsList, this);

        hashtagsListView.setAdapter(hashtagsAdapter);

        hashtagsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mContentContainer.isRefreshing())) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getHashtags();
                    }
                }
            }
        });

        if (App.getInstance().isConnected()) {

            showLoadingScreen();
            getHashtags();

        } else {

            showErrorScreen();
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            answerId = 0;
            getHashtags();

        } else {

            mContentContainer.setRefreshing(false);
        }
    }

    public void getHashtags() {

        if (loadingMore) {

            mContentContainer.setRefreshing(true);
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_HASHTAGS_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!loadingMore) {

                            hashtagsList.clear();
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                answerId = response.getInt("answerId");
                                hashtag = response.getString("query");

                                if (response.has("answers")) {

                                    JSONArray answersArray = response.getJSONArray("answers");

                                    arrayLength = answersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < answersArray.length(); i++) {

                                            JSONObject answerObj = (JSONObject) answersArray.get(i);

                                            Answer answer = new Answer(answerObj);

                                            hashtagsList.add(answer);
                                        }
                                    }

                                    loadingComplete();
                                }

                            } else {

                                if (!loadingMore) {

                                    showErrorScreen();

                                } else {

                                    loadingComplete();
                                }
                            }

                        } catch (JSONException e) {

                            if (!loadingMore) {

                                showErrorScreen();

                            } else {

                                loadingComplete();
                            }

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!loadingMore) {

                    showErrorScreen();

                } else {

                    loadingComplete();
                }

//                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("answerId", Long.toString(answerId));
                params.put("language", "en");
                params.put("hashtag", hashtag);

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        hashtagsAdapter.notifyDataSetChanged();

        if (loadingMore) {

            loadingMore = false;
        }

        if (hashtagsListView.getAdapter().getCount() == 0) {

            showEmptyScreen();

        } else {

            showContentScreen();
        }

        if (mContentContainer.isRefreshing()) {

            mContentContainer.setRefreshing(false);
        }
    }

    public void showLoadingScreen() {

        mContentScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);
        mEmptyScreen.setVisibility(View.GONE);

        mLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showEmptyScreen() {

        mContentScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mEmptyScreen.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mContentScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);
        mEmptyScreen.setVisibility(View.GONE);

        mErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);
        mEmptyScreen.setVisibility(View.GONE);

        mContentScreen.setVisibility(View.VISIBLE);

        if (ADMOB) {

            mAdMobCont.setVisibility(View.VISIBLE);

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    public void like(int position, JSONObject data) {

//        if (streamListView.getCount() == 0) {

//            Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
//        }
    }

    public void remove(int position) {

        hashtagsList.remove(position);
        hashtagsAdapter.notifyDataSetChanged();

        if (hashtagsList.size() == 0) {

            showEmptyScreen();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }
}
