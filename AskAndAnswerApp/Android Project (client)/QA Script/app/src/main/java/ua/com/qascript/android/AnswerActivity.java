package ua.com.qascript.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import ua.com.qascript.android.adapter.StreamListAdapter;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.model.Answer;
import ua.com.qascript.android.util.AnswerInterface;
import ua.com.qascript.android.util.CustomRequest;


public class AnswerActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, AnswerInterface {

    Toolbar toolbar;

    SwipeRefreshLayout mContentScreen;
    RelativeLayout mErrorScreen, mLoadingScreen;

    ListView mListView;

    TextView mErrorScreenMsg;

    private long answerId;

    private ArrayList<Answer> answerList;

    private StreamListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent i = getIntent();

        answerId = i.getLongExtra("answerId", 0);

        mErrorScreen = (RelativeLayout) findViewById(R.id.AnswerErrorScreen);
        mLoadingScreen = (RelativeLayout) findViewById(R.id.AnswerLoadingScreen);
        mContentScreen = (SwipeRefreshLayout) findViewById(R.id.AnswerContentScreen);
        mContentScreen.setOnRefreshListener(this);

        mErrorScreenMsg = (TextView) findViewById(R.id.AnswersErrorScreenMsg);

        mListView = (ListView) findViewById(R.id.AnswerListView);

        answerList = new ArrayList<Answer>();
        adapter = new StreamListAdapter(AnswerActivity.this, answerList, this);

        mListView.setAdapter(adapter);

        showLoadingScreen();

        if (App.getInstance().isConnected()) {

            getAnswer();

        }  else {

            showErrorScreen();
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            getAnswer();

        } else {

            Toast.makeText(getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
            mContentScreen.setRefreshing(false);
        }
    }

    public void like(int position, JSONObject data) {

//        if (mListView.getCount() == 0) {

//            Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_answer, menu);
        return true;
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

    public void getAnswer() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ANSWERS_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            answerList.clear();

                            if (response.getBoolean("error") == false) {

                                if (response.has("answer")) {

                                    JSONArray answerArray = response.getJSONArray("answer");

                                    if (answerArray.length() > 0) {

                                        for (int i = 0; i < answerArray.length(); i++) {

                                            JSONObject answerObj = (JSONObject) answerArray.get(i);

                                            Answer answer = new Answer(answerObj);

                                            answerList.add(answer);
                                        }
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
                params.put("answerId", Long.toString(answerId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        mContentScreen.setRefreshing(false);

        if (adapter.getCount() <= 0) {

            mErrorScreenMsg.setText(getText(R.string.error_answer_not_exists));
            showErrorScreen();

        } else {

            showContentScreen();
        }
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

        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mContentScreen.setVisibility(View.VISIBLE);
    }
}
