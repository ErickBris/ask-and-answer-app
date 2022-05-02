package ua.com.qascript.android;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import ua.com.qascript.android.adapter.SearchListAdapter;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.model.Profile;
import ua.com.qascript.android.util.CustomRequest;
import ua.com.qascript.android.util.Helper;


public class SearchActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;

    SearchView searchView = null;

    ListView searchListView;

    View searchListViewHeader, searchListViewFooter;

    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout searchStartScreen, searchLoadScreen;

    TextView searchResults, searchStartScreenMsg;

    Intent intent;

    private ArrayList<Profile> searchList;

    private SearchListAdapter adapter;

    public String queryText, currentQuery, oldQuery;

    public int currentPage = 0, maxPage, itemCount, nextPage = 0, action;
    Boolean loadingMore = false;
    Boolean viewMore = false;
    private int arrayLength = 0;
    private int createAt = 0;

    CustomRequest searchJsonReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //        Initialize Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        intent = getIntent();
        action = intent.getIntExtra("action", 0);

        searchStartScreen = (LinearLayout) findViewById(R.id.searchStartScreen);
        searchStartScreenMsg = (TextView) findViewById(R.id.searchStartScreenMsg);

        searchLoadScreen = (LinearLayout) findViewById(R.id.searchLoadScreen);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.searchRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        searchListView = (ListView) findViewById(R.id.searchListView);
        searchListViewHeader = getLayoutInflater().inflate(R.layout.search_listview_header, null);
        searchListViewFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);

        searchListView.addHeaderView(searchListViewHeader);
//        searchListView.addFooterView(searchListViewFooter);

        searchResults = (TextView) searchListViewHeader.findViewById(R.id.searchResults);

        searchList = new ArrayList<Profile>();
        adapter = new SearchListAdapter(SearchActivity.this, searchList);

        searchListView.setAdapter(adapter);

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (position != 0) {

                    Profile profile = (Profile) adapterView.getItemAtPosition(position);

                    Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                    intent.putExtra("profileId", profile.getId());
                    startActivity(intent);
                }
            }
        });

        searchListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mSwipeRefreshLayout.isRefreshing())) {

                    currentQuery = getCurrentQuery();

                    if (currentQuery.equals(oldQuery) ) {

                        loadingMore = true;

                        search();
                    }
                }
            }
        });

        mSwipeRefreshLayout.setVisibility(View.GONE);
        searchLoadScreen.setVisibility(View.GONE);
        searchStartScreen.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {

        currentQuery = getCurrentQuery();

        if (currentQuery.equals(oldQuery) ) {

            createAt = 0;

            search();

        } else {

            searchCompleted();
        }
    }

    public void searchStart() {

        currentQuery = getCurrentQuery();

        currentQuery = currentQuery.trim();

        if (isValidCurrentQuery()) {

            if (App.getInstance().isConnected()) {

                showLoadScreen();

                search();

            } else {

                Toast.makeText(getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
            }

        } else {


        }
    }

    public void searchCompleted() {

        loadingMore = false;

        mSwipeRefreshLayout.setRefreshing(false);

        searchResults.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));

        if (itemCount > 0) {

            searchStartScreen.setVisibility(View.GONE);
            searchLoadScreen.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);

        } else {

            showNoResultsScreen();
        }
    }

    public void showStartScreen() {

        searchStartScreen.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        searchLoadScreen.setVisibility(View.GONE);

        searchStartScreenMsg.setText(getText(R.string.label_search_start_screen_msg));
    }

    public void showLoadScreen() {

        mSwipeRefreshLayout.setVisibility(View.GONE);
        searchStartScreen.setVisibility(View.GONE);
        searchLoadScreen.setVisibility(View.VISIBLE);
    }

    public void showNoResultsScreen() {

        searchStartScreen.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        searchLoadScreen.setVisibility(View.GONE);

        searchStartScreenMsg.setText(getText(R.string.label_search_no_results));
    }

    public String getCurrentQuery() {

        String searchText = searchView.getQuery().toString();
        searchText = searchText.trim();

        return searchText;
    }

    public Boolean isValidCurrentQuery() {

        Helper helper = new Helper();

        return helper.isValidSearchQuery(getCurrentQuery());
    }

    public void showListViewFooter() {

        searchListView.addFooterView(searchListViewFooter, null, false);
    }

    public void hideListViewFooter() {

        searchListView.removeFooterView(searchListViewFooter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.options_menu_main_search);

        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }

        if (searchView != null) {

            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);

            SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchAutoComplete.setHint(getText(R.string.placeholder_search));
            searchAutoComplete.setHintTextColor(getResources().getColor(R.color.light_gray));
//            searchAutoComplete.setTextSize(14);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {

                    if (newText.length() == 0) {

                        showStartScreen();
                    }

                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {

                    queryText = query;
                    searchStart();

                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

                killActivity();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed(){

        killActivity();
    }

    public void killActivity() {

        if (action == FRIENDS_FIND) {

            Intent i = new Intent();
            i.putExtra("action", FRIENDS_FIND);
            setResult(RESULT_OK, i);
        }

        finish();
    }

    public void search() {

        if (loadingMore) {

            showListViewFooter();
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_APP_SEARCH, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!loadingMore) {

                                searchList.clear();
                            }

                            arrayLength = 0;

                            if (response.getBoolean("error") == false) {

                                itemCount = response.getInt("itemCount");
                                oldQuery = response.getString("query");
                                createAt = response.getInt("createAt");

                                if (response.has("users")) {

                                    JSONArray usersArray = response.getJSONArray("users");

                                    arrayLength = usersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject profileObj = (JSONObject) usersArray.get(i);

                                            Profile profile = new Profile(profileObj);

                                            searchList.add(profile);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();

//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
                Toast.makeText(getApplicationContext(), getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("query", currentQuery);
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

        searchResults.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));

        loadingMore = false;

        hideListViewFooter();

        mSwipeRefreshLayout.setRefreshing(false);

        if (adapter.getCount() > 0) {

            searchStartScreen.setVisibility(View.GONE);
            searchLoadScreen.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);

        } else {

            showNoResultsScreen();
        }
    }
}
