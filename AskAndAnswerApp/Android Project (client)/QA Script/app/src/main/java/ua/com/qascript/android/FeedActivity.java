package ua.com.qascript.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

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


public class FeedActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, AnswerInterface {

    Toolbar toolbar;

    SwipeRefreshLayout mContentScreen;
    RelativeLayout mErrorScreen, mLoadingScreen, mWelcomeScreen;

    ListView mFeedListView;

    TextView mErrorScreenMsg;
    Button mWelcomeScreenBtn;

    View mListViewFooter;

    private ArrayList<Answer> feedList;

    private StreamListAdapter feedAdapter;

    int replyAt = 0;
    int arrayLength = 0;
    Boolean loadingMore = false;
    Boolean viewMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mErrorScreen = (RelativeLayout) findViewById(R.id.FeedErrorScreen);
        mLoadingScreen = (RelativeLayout) findViewById(R.id.FeedLoadingScreen);
        mWelcomeScreen = (RelativeLayout) findViewById(R.id.FeedWelcomeScreen);
        mContentScreen = (SwipeRefreshLayout) findViewById(R.id.FeedContentScreen);
        mContentScreen.setOnRefreshListener(this);

        mErrorScreenMsg = (TextView) findViewById(R.id.FeedErrorScreenMsg);
        mWelcomeScreenBtn = (Button) findViewById(R.id.FeedWelcomeScreenBtn);

        mFeedListView = (ListView) findViewById(R.id.feedListView);

        mListViewFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
        mListViewFooter.setVisibility(View.GONE);

        mFeedListView.addFooterView(mListViewFooter, null, false);

        feedList = new ArrayList<Answer>();
        feedAdapter = new StreamListAdapter(FeedActivity.this, feedList, this);

        mFeedListView.setAdapter(feedAdapter);

        mFeedListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ( (lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mContentScreen.isRefreshing())) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getFeed();
                    }
                }
            }
        });

        mWelcomeScreenBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(FeedActivity.this, FriendsActivity.class);
                startActivity(i);

                finish();
            }
        });

        if (App.getInstance().isConnected()) {

            showLoadingScreen();
            getFeed();

        } else {

            showErrorScreen();
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            replyAt = 0;
            getFeed();

        } else {

            mContentScreen.setRefreshing(false);
        }
    }

    public void like(int position, JSONObject data) {

//        if (streamListView.getCount() == 0) {

//            Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
//        }
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

    public void getFeed() {

        if (loadingMore) {

            mListViewFooter.setVisibility(View.VISIBLE);
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_FEED_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!loadingMore) {

                            feedList.clear();
                        }

                        try {

                            arrayLength = 0;

                            if (response.getBoolean("error") == false) {

                                replyAt = response.getInt("replyAt");

                                if (response.has("answers")) {

                                    JSONArray answersArray = response.getJSONArray("answers");

                                    arrayLength = answersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < answersArray.length(); i++) {

                                            JSONObject answerObj = (JSONObject) answersArray.get(i);

                                            Answer answer = new Answer(answerObj);

                                            feedList.add(answer);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
//                            Toast.makeText(getApplicationContext(), Integer.toString(arrayLength), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
//                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("replyAt", Integer.toString(replyAt));

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

        feedAdapter.notifyDataSetChanged();

        if (loadingMore) {

            loadingMore = false;
            mListViewFooter.setVisibility(View.GONE);
//            streamListView.removeFooterView(streamListViewFooter);
        }

        if (feedAdapter.getCount() > 0) {

            showContentScreen();

        } else {

            showWelcomeScreen();
        }

        if (mContentScreen.isRefreshing()) {

            mContentScreen.setRefreshing(false);
        }
    }

    public void showWelcomeScreen() {

        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);
        mContentScreen.setVisibility(View.GONE);

        mWelcomeScreen.setVisibility(View.VISIBLE);
    }

    public void showLoadingScreen() {

        mContentScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);
        mWelcomeScreen.setVisibility(View.GONE);

        mLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mContentScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);
        mWelcomeScreen.setVisibility(View.GONE);

        mErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);
        mWelcomeScreen.setVisibility(View.GONE);

        mContentScreen.setVisibility(View.VISIBLE);
    }
}
