package ua.com.qascript.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.com.qascript.android.adapter.NavDrawerListAdapter;
import ua.com.qascript.android.adapter.StreamListAdapter;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.model.Answer;
import ua.com.qascript.android.model.NavDrawerItem;
import ua.com.qascript.android.util.AnswerInterface;
import ua.com.qascript.android.util.CustomRequest;


public class MainActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, AnswerInterface {

    Toolbar toolbar;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    SwipeRefreshLayout mContentScreen;
    RelativeLayout mErrorScreen, mLoadingScreen;
    LinearLayout mContainer, mStreamAdMobCont;

    ListView streamListView;

    private ArrayList<Answer> streamList;

    private StreamListAdapter streamAdapter;

    int replyAt = 0;
    int arrayLength = 0;
    Boolean loadingMore = false;
    Boolean viewMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mStreamAdMobCont = (LinearLayout) findViewById(R.id.streamAdMobCont);
        mErrorScreen = (RelativeLayout) findViewById(R.id.StreamErrorScreen);
        mLoadingScreen = (RelativeLayout) findViewById(R.id.StreamLoadingScreen);
        mContentScreen = (SwipeRefreshLayout) findViewById(R.id.StreamContentScreen);
        mContentScreen.setOnRefreshListener(this);

        mContainer = (LinearLayout) findViewById(R.id.Container);

        streamListView = (ListView) findViewById(R.id.streamListView);

        streamList = new ArrayList<Answer>();
        streamAdapter = new StreamListAdapter(MainActivity.this, streamList, this);

        streamListView.setAdapter(streamAdapter);

        streamListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ( (lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mContentScreen.isRefreshing()) ) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getStream();
                    }
                }
            }
        });

        mTitle = mDrawerTitle = getSupportActionBar().getTitle();

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Profile
        navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_profile).toString(), navMenuIcons.getResourceId(1, -1)));

        // Stream
        // navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_stream).toString(), navMenuIcons.getResourceId(7, -1)));

        // Feed
        navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_feed).toString(), navMenuIcons.getResourceId(5, -1)));
        // Friends
        navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_friends).toString(), navMenuIcons.getResourceId(2, -1)));
        // Questions
        navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_questions).toString(), navMenuIcons.getResourceId(4, -1)));
        // Notifications
        // navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_notifications).toString(), navMenuIcons.getResourceId(3, -1), true, "50+"));
        navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_notifications).toString(), navMenuIcons.getResourceId(3, -1)));
        // Answers
        navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_answers).toString(), navMenuIcons.getResourceId(6, -1)));
        // Search
        navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_search).toString(), navMenuIcons.getResourceId(8, -1)));
        // Settings
        navDrawerItems.add(new NavDrawerItem(getText(R.string.drawer_settings).toString(), navMenuIcons.getResourceId(0, -1)));


        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, //nav menu toggle icon
                R.string.drawer_stream, // nav drawer open - description for accessibility
                R.string.drawer_stream // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {

                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {

                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (App.getInstance().isConnected()) {

            showLoadingScreen();
            getStream();

        } else {

            showErrorScreen();
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            replyAt = 0;
            getStream();

        } else {

            mContentScreen.setRefreshing(false);
        }
    }

    public void like(int position, JSONObject data) {

//        if (streamListView.getCount() == 0) {

//            Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
//        }
    }

    private class SlideMenuClickListener implements  ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
//            displayView(position);

            switch (position) {

                case 0: {

                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    i.putExtra("profileId", App.getInstance().getId());
                    startActivity(i);

                    mDrawerLayout.closeDrawers();

                    break;
                }

                case 1: {

                    Intent i = new Intent(MainActivity.this, FeedActivity.class);
                    startActivity(i);

                    mDrawerLayout.closeDrawers();

                    break;
                }

                case 2: {

                    Intent i = new Intent(MainActivity.this, FriendsActivity.class);
                    startActivity(i);

                    mDrawerLayout.closeDrawers();

                    break;
                }

                case 3: {

                    Intent i = new Intent(MainActivity.this, QuestionsActivity.class);
                    startActivity(i);

                    mDrawerLayout.closeDrawers();

                    break;
                }

                case 4: {

                    Intent i = new Intent(MainActivity.this, NotifyLikesActivity.class);
                    startActivity(i);

                    mDrawerLayout.closeDrawers();

                    break;
                }

                case 5: {

                    Intent i = new Intent(MainActivity.this, NotifyAnswersActivity.class);
                    startActivity(i);

                    mDrawerLayout.closeDrawers();

                    break;
                }

                case 6: {

                    Intent i = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(i);

                    mDrawerLayout.closeDrawers();

                    break;
                }

                case 7: {

                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i);

                    mDrawerLayout.closeDrawers();

                    break;
                }

                default: {

                    mDrawerLayout.closeDrawers();

                    break;
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FRIENDS_FIND && resultCode == RESULT_OK && null != data) {

            Toast.makeText(getApplicationContext(), "FRIENDS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(Gravity.START|Gravity.LEFT)) {

            mDrawerLayout.closeDrawers();

            return;

        } else {

            finish();
        }

        super.onBackPressed();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void getStream() {

        if (loadingMore) {

            mContentScreen.setRefreshing(true);
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_STREAM_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!loadingMore) {

                            streamList.clear();
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

                                            streamList.add(answer);
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
                params.put("language", "en");

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

        streamAdapter.notifyDataSetChanged();

        if (loadingMore) {

            loadingMore = false;
        }

        showContentScreen();

        if (mContentScreen.isRefreshing()) {

            mContentScreen.setRefreshing(false);
        }
    }

    public void showLoadingScreen() {

        mContainer.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mContainer.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);

        mErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mContainer.setVisibility(View.VISIBLE);
        mContentScreen.setVisibility(View.VISIBLE);

        if (ADMOB) {

            mStreamAdMobCont.setVisibility(View.VISIBLE);

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }
}
