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

import ua.com.qascript.android.adapter.FriendsListAdapter;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.model.Profile;
import ua.com.qascript.android.util.CustomRequest;


public class FriendsActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;

    ListView friendsListView;

    View friendsListViewFooter, friendsListViewHeader;

    SwipeRefreshLayout FriendsContentScreen;
    RelativeLayout FriendsLoadingScreen, FriendsWelcomeScreen, FriendsErrorScreen;

    Button FriendsWelcomeScreenSearchBtn;

    TextView friendsCountTextView;

    private ArrayList<Profile> friendsList;

    private FriendsListAdapter adapter;

    Boolean loadingMore = false;
    Boolean viewMore = false;

    public int createAt = 0;
    int arrayLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //        Инициализируем Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FriendsWelcomeScreen = (RelativeLayout) findViewById(R.id.FriendsWelcomeScreen);
        FriendsLoadingScreen = (RelativeLayout) findViewById(R.id.FriendsLoadingScreen);
        FriendsErrorScreen = (RelativeLayout) findViewById(R.id.FriendsErrorScreen);

        FriendsContentScreen = (SwipeRefreshLayout) findViewById(R.id.FriendsContentScreen);
        FriendsContentScreen.setOnRefreshListener(this);

        FriendsWelcomeScreenSearchBtn = (Button) findViewById(R.id.FriendsWelcomeScreenSearchBtn);

        friendsListView = (ListView) findViewById(R.id.friendsListView);
        friendsListViewHeader = getLayoutInflater().inflate(R.layout.friends_listview_header, null);
        friendsListViewFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);

        friendsCountTextView = (TextView) friendsListViewHeader.findViewById(R.id.friendsCountTextView);

        friendsListView.addHeaderView(friendsListViewHeader);
        friendsListView.addFooterView(friendsListViewFooter, null, false);
        friendsListView.removeFooterView(friendsListViewFooter);

        friendsList = new ArrayList<Profile>();
        adapter = new FriendsListAdapter(FriendsActivity.this, friendsList);

        friendsListView.setAdapter(adapter);

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (position != 0) {

                    Profile profile = (Profile) adapterView.getItemAtPosition(position);

                    Intent intent = new Intent(FriendsActivity.this, ProfileActivity.class);
                    intent.putExtra("profileId", profile.getId());
                    startActivity(intent);

                } else {

                    findFriends();
                }
            }
        });

        friendsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(FriendsContentScreen.isRefreshing())) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getFriends();
                    }
                }
            }
        });

        FriendsWelcomeScreenSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findFriends();
            }
        });

        if (App.getInstance().isConnected()) {

            setLoadingScreen();
            getFriends();

        } else {

            setErrorScreen();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FRIENDS_FIND && resultCode == RESULT_OK && null != data) {

            if (App.getInstance().isConnected()) {

                createAt = 0;

                setLoadingScreen();
                getFriends();
            }
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            createAt = 0;
            getFriends();

        } else {

            FriendsContentScreen.setRefreshing(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_settings: {

                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);

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

    public void getFriends() {

        if (loadingMore) {

            friendsListView.addFooterView(friendsListViewFooter, null, false);
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_FRIENDS_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            arrayLength = 0;

                            if (response.getBoolean("error") == false) {

                                if (!loadingMore) {

                                    friendsList.clear();
                                }

                                createAt = response.getInt("createAt");

                                if (response.has("friends")) {

                                    JSONArray friendsArray = response.getJSONArray("friends");

                                    arrayLength = friendsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < friendsArray.length(); i++) {

                                            JSONObject profileObj = (JSONObject) friendsArray.get(i);

                                            Profile profile = new Profile(profileObj);

                                            friendsList.add(profile);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
                Toast.makeText(getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_SHORT).show();
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

        if (loadingMore) {

            loadingMore = false;
            friendsListView.removeFooterView(friendsListViewFooter);
        }

        if (adapter.getCount() > 0) {

            setContentScreen();

        } else {

            setWelcomeScreen();
        }

        if (FriendsContentScreen.isRefreshing()) {

            FriendsContentScreen.setRefreshing(false);
        }
    }

    public void findFriends() {

        Intent i = new Intent(FriendsActivity.this, SearchActivity.class);
        i.putExtra("action", FRIENDS_FIND);
        startActivityForResult(i, FRIENDS_FIND);
    }

    public void showErrorScreen() {

        FriendsErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        FriendsContentScreen.setVisibility(View.VISIBLE);
    }

    public void showLoadingScreen() {

        FriendsLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showWelcomeScreen() {

        FriendsWelcomeScreen.setVisibility(View.VISIBLE);
    }

    public void hideErrorScreen() {

        FriendsErrorScreen.setVisibility(View.GONE);
    }

    public void hideContentScreen() {

        FriendsContentScreen.setVisibility(View.GONE);
    }

    public void hideLoadingScreen() {

        FriendsLoadingScreen.setVisibility(View.GONE);
    }

    public void hideWelcomeScreen() {

        FriendsWelcomeScreen.setVisibility(View.GONE);
    }

    public void setErrorScreen() {

        hideLoadingScreen();
        hideContentScreen();
        hideWelcomeScreen();

        showErrorScreen();
    }

    public void setContentScreen() {

        hideLoadingScreen();
        hideWelcomeScreen();
        hideErrorScreen();

        showContentScreen();
    }

    public void setLoadingScreen() {

        hideErrorScreen();
        hideWelcomeScreen();
        hideContentScreen();

        showLoadingScreen();
    }

    public void setWelcomeScreen() {

        hideErrorScreen();
        hideLoadingScreen();
        hideContentScreen();

        showWelcomeScreen();
    }
}
