package ua.com.qascript.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import ua.com.qascript.android.adapter.QuestionsListAdapter;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.model.Question;
import ua.com.qascript.android.util.CustomRequest;
import ua.com.qascript.android.util.ResponderInterface;


public class QuestionsActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, ResponderInterface {

    Toolbar toolbar;

    SwipeRefreshLayout mQuestionsContentScreen;

    RelativeLayout mQuestionsLoadingScreen, mQuestionsErrorScreen, mQuestionsWelcomeScreen;

    ListView questionsListView;

    Button mQuestionsWelcomeScreenBtn;

    private ArrayList<Question> questionsList;

    private QuestionsListAdapter adapter;

    int addedToListAt = 0;
    int arrayLength = 0;
    Boolean loadingMore = false;
    Boolean viewMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        //        Инициализируем Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mQuestionsWelcomeScreen = (RelativeLayout) findViewById(R.id.QuestionsWelcomeScreen);
        mQuestionsErrorScreen = (RelativeLayout) findViewById(R.id.QuestionsErrorScreen);
        mQuestionsLoadingScreen = (RelativeLayout) findViewById(R.id.QuestionsLoadingScreen);

        mQuestionsContentScreen = (SwipeRefreshLayout) findViewById(R.id.QuestionsContentScreen);
        mQuestionsContentScreen.setOnRefreshListener(this);

        questionsListView = (ListView) findViewById(R.id.questionsListView);

        mQuestionsWelcomeScreenBtn = (Button) findViewById(R.id.QuestionsWelcomeScreenBtn);

        questionsList = new ArrayList<Question>();
        adapter = new QuestionsListAdapter(QuestionsActivity.this, questionsList, this);

        questionsListView.setAdapter(adapter);

        questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//                    ImageView deleteIcon = (ImageView) view.findViewById(R.id.questionRemove);
//
//                    deleteIcon.setTag(position);
//
//                    deleteIcon.setOnClickListener(new View.OnClickListener(){
//
//                        public void onClick(View v) {
//
//                            int getPosition = (Integer) v.getTag();
//
//                            questionsList.remove(getPosition);
//                            adapter.notifyDataSetChanged();
//                        }
//                    });

//                    Toast.makeText(getApplicationContext(), Integer.toString(view.getId()), Toast.LENGTH_SHORT).show();

//                if (position != 0) {


//                }

                Question question = (Question) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(QuestionsActivity.this, ReplyActivity.class);
                intent.putExtra("questionId", question.getId());
                intent.putExtra("listPosition", position);
                startActivityForResult(intent, QUESTION_ANSWER);
            }
        });

        questionsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ( (lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mQuestionsContentScreen.isRefreshing()) ) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getQuestions();
                    }
                }
            }
        });

        mQuestionsWelcomeScreenBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (App.getInstance().isConnected()) {

                    getRandom();

                } else {

                    Toast.makeText(getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (App.getInstance().isConnected()) {

            setLoadingScreen();
            getQuestions();

        } else {

            setErrorScreen();
        }
    }

    public void listViewItemChange() {

        if (questionsListView.getCount() == 0) {

            setWelcomeScreen();
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            addedToListAt = 0;

            getQuestions();

        } else {

            mQuestionsContentScreen.setRefreshing(false);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QUESTION_ANSWER && resultCode == RESULT_OK && null != data) {

            questionsList.remove(data.getIntExtra("listPosition", -1));
            adapter.notifyDataSetChanged();

            this.listViewItemChange();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_questions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_random_question: {

                if (App.getInstance().isConnected()) {

                    getRandom();

                } else {

                    Toast.makeText(getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            case android.R.id.home: {

                finish();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void getRandom() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_QUESTIONS_RANDOM, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {

                                Question question = new Question(response);

                                questionsList.add(0, question);
                                adapter.notifyDataSetChanged();

                                questionsListView.smoothScrollToPosition(0);

                                setContentScreen();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getQuestions() {

        if (loadingMore) {

            mQuestionsContentScreen.setRefreshing(true);
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_QUESTIONS_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            arrayLength = 0;

                            if (!loadingMore) {

                                questionsList.clear();
                            }

                            if (response.getBoolean("error") == false) {

                                addedToListAt = response.getInt("addedToListAt");

                                JSONArray questionsArray = response.getJSONArray("questions");

                                arrayLength = questionsArray.length();

                                if (arrayLength > 0) {

                                    for (int i = 0; i < questionsArray.length(); i++) {

                                        JSONObject questionObj = (JSONObject) questionsArray.get(i);

                                        Question question = new Question(questionObj);

                                        questionsList.add(question);
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
                params.put("addedToListAt", Integer.toString(addedToListAt));

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

        if (loadingMore) {

            loadingMore = false;
        }

        if (adapter.getCount() != 0) {

            setContentScreen();

        } else {

            setWelcomeScreen();
        }

        if (mQuestionsContentScreen.isRefreshing()) {

            mQuestionsContentScreen.setRefreshing(false);
        }
    }

    public void showLoadingScreen() {

        mQuestionsLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showWelcomeScreen() {

        mQuestionsWelcomeScreen.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mQuestionsErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mQuestionsContentScreen.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen() {

        mQuestionsLoadingScreen.setVisibility(View.GONE);
    }

    public void hideWelcomeScreen() {

        mQuestionsWelcomeScreen.setVisibility(View.GONE);
    }

    public void hideErrorScreen() {

        mQuestionsErrorScreen.setVisibility(View.GONE);
    }

    public void hideContentScreen() {

        mQuestionsContentScreen.setVisibility(View.GONE);
    }

    public void setLoadingScreen() {

        hideErrorScreen();
        hideWelcomeScreen();
        hideContentScreen();

        showLoadingScreen();
    }

    public void setWelcomeScreen() {

        hideErrorScreen();
        hideContentScreen();
        hideLoadingScreen();

        showWelcomeScreen();
    }

    public void setErrorScreen() {

        hideLoadingScreen();
        hideWelcomeScreen();
        hideContentScreen();

        showErrorScreen();
    }

    public void setContentScreen() {

        hideLoadingScreen();
        hideErrorScreen();
        hideWelcomeScreen();

        showContentScreen();
    }
}
