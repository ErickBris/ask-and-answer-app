package ua.com.qascript.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.com.qascript.android.R;
import ua.com.qascript.android.adapter.NotifyListAdapter;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.model.Notify;
import ua.com.qascript.android.util.CustomRequest;


public class NotifyLikesActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;

    SwipeRefreshLayout mContentScreen;
    RelativeLayout mErrorScreen, mLoadingScreen;
    TextView mErrorScreenMsg;

    ListView mListView;

    View mListViewFooter;

    private ArrayList<Notify> notifyList;

    private NotifyListAdapter adapter;

    private int createAt = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_likes);

        //        Initialize Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLoadingScreen = (RelativeLayout) findViewById(R.id.NotifyLikesLoadingScreen);
        mErrorScreen = (RelativeLayout) findViewById(R.id.NotifyLikesErrorScreen);

        mErrorScreenMsg = (TextView) findViewById(R.id.NotifyLikesErrorScreenMsg);

        mContentScreen = (SwipeRefreshLayout) findViewById(R.id.NotifyLikesContentScreen);
        mContentScreen.setOnRefreshListener(this);

        mListView = (ListView) findViewById(R.id.NotifyLikesListView);

        mListViewFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);

        mListView.addFooterView(mListViewFooter, null, false);

        mListViewFooter.setVisibility(View.GONE);

        notifyList = new ArrayList<Notify>();
        adapter = new NotifyListAdapter(NotifyLikesActivity.this, notifyList);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Notify notify = (Notify) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(NotifyLikesActivity.this, AnswerActivity.class);
                intent.putExtra("answerId", notify.getAnswerId());
                startActivity(intent);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ( (lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mContentScreen.isRefreshing()) ) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getLikes();
                    }
                }
            }
        });

        if (App.getInstance().isConnected()) {

            showLoadingScreen();

            getLikes();

        } else {

            showErrorScreen();
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            createAt = 0;

            getLikes();

        } else {

            mContentScreen.setRefreshing(false);
            Toast.makeText(getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_SHORT).show();
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

    public void getLikes() {

        if (loadingMore) {

            mListViewFooter.setVisibility(View.VISIBLE);
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_NOTIFY_GETLIKES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            arrayLength = 0;

                            if (!loadingMore) {

                                notifyList.clear();
                            }

                            if (response.getBoolean("error") == false) {

                                createAt = response.getInt("createAt");

                                JSONArray notifyLikesArray = response.getJSONArray("likes");

                                arrayLength = notifyLikesArray.length();

                                if (arrayLength > 0) {

                                    for (int i = 0; i < notifyLikesArray.length(); i++) {

                                        JSONObject notifyObj = (JSONObject) notifyLikesArray.get(i);

                                        Notify notify = new Notify(notifyObj);

                                        notifyList.add(notify);
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("createAt", Integer.toString(createAt));

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

        adapter.notifyDataSetChanged();

        if (adapter.getCount() > 0) {

            showContentScreen();

        } else {

            mErrorScreenMsg.setText(getText(R.string.label_list_is_empty));
            showErrorScreen();
        }

        loadingMore = false;
        mContentScreen.setRefreshing(false);

        mListViewFooter.setVisibility(View.GONE);
    }

    public void showLoadingScreen() {

        mContentScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mContentScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);

        mErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mErrorScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);

        mContentScreen.setVisibility(View.VISIBLE);
    }
}
