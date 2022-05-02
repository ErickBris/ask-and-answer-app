package ua.com.qascript.android;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.ads.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.com.qascript.android.adapter.AnswersListAdapter;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.constants.Constants;
import ua.com.qascript.android.dialogs.ProfileReportDialog;
import ua.com.qascript.android.dialogs.ProfileReportDialog.AlertPositiveListener;
import ua.com.qascript.android.model.Answer;
import ua.com.qascript.android.model.Profile;
import ua.com.qascript.android.util.CustomRequest;


public class ProfileActivity extends ActivityBase implements Constants, SwipeRefreshLayout.OnRefreshListener, AlertPositiveListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private static final int SELECT_PHOTO = 1;
    private static final int SELECT_COVER = 2;
    private static final int PROFILE_EDIT = 3;

    String [] names = {};

    Toolbar toolbar;

    Button profileFollowBtn, profileUnFollowBtn, profileAskBtn;

    TextView profileFullname, profileUsername, profileFollowersCount, profileAnswersCount, mProfileWallMsg, mProfileErrorScreenMsg, mProfileDisabledScreenMsg;
    TextView mAnswersCount, mLikesCount, mFollowersCount;

    SwipeRefreshLayout mProfileContentScreen;
    RelativeLayout mProfileLoadingScreen, mProfileErrorScreen, mProfileDisabledScreen;
    LinearLayout mProfileContainer, mProfileAdMobCont;

    ListView profileListView;
    View profileListViewHeader;

    ImageView profilePhoto, profileCover;

    Profile profile;

    private ArrayList<Answer> answersList;

    private AnswersListAdapter adapter;

    private String selectedPhoto, selectedCover;

    private Boolean loadingComplete = false;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;

    public long profile_id;
    int replyAt = 0;
    int arrayLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //        Initialize Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent i = getIntent();
        profile_id = i.getLongExtra("profileId", 0);

        mProfileContentScreen = (SwipeRefreshLayout) findViewById(R.id.profileContentScreen);
        mProfileContentScreen.setOnRefreshListener(this);

        mProfileAdMobCont = (LinearLayout) findViewById(R.id.profileAdMobCont);

        mProfileLoadingScreen = (RelativeLayout) findViewById(R.id.profileLoadingScreen);
        mProfileErrorScreen = (RelativeLayout) findViewById(R.id.profileErrorScreen);
        mProfileDisabledScreen = (RelativeLayout) findViewById(R.id.profileDisabledScreen);

        mProfileContainer = (LinearLayout) findViewById(R.id.profileContainer);

        mProfileErrorScreenMsg = (TextView) findViewById(R.id.profileErrorScreenMsg);
        mProfileDisabledScreenMsg = (TextView) findViewById(R.id.profileDisabledScreenMsg);

        profileListView = (ListView) findViewById(R.id.profileListView);
        profileListViewHeader = getLayoutInflater().inflate(R.layout.profile_listview_header, null);

        profileListView.addHeaderView(profileListViewHeader);

        profileFollowBtn = (Button) profileListViewHeader.findViewById(R.id.profileFollowBtn);
        profileUnFollowBtn = (Button) profileListViewHeader.findViewById(R.id.profileUnFollowBtn);
        profileAskBtn = (Button) profileListViewHeader.findViewById(R.id.profileAskBtn);

        profileFullname = (TextView) profileListViewHeader.findViewById(R.id.profileFullname);
        profileUsername = (TextView) profileListViewHeader.findViewById(R.id.profileUsername);
        profileFollowersCount = (TextView) profileListViewHeader.findViewById(R.id.profileFollowersCount);
        profileAnswersCount = (TextView) profileListViewHeader.findViewById(R.id.profileAnswersCount);

        mAnswersCount = (TextView) profileListViewHeader.findViewById(R.id.answersCount);
        mLikesCount = (TextView) profileListViewHeader.findViewById(R.id.likesCount);
        mFollowersCount = (TextView) profileListViewHeader.findViewById(R.id.followersCount);

        mProfileWallMsg = (TextView) profileListViewHeader.findViewById(R.id.profileWallMsg);

        profilePhoto = (ImageView) profileListViewHeader.findViewById(R.id.profilePhoto);
        profileCover = (ImageView) profileListViewHeader.findViewById(R.id.profileCover);

        answersList = new ArrayList<Answer>();
        adapter = new AnswersListAdapter(ProfileActivity.this, answersList);

        profileListView.setAdapter(adapter);

        profileListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ( (lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mProfileContentScreen.isRefreshing()) ) {

                    loadingMore = true;

                    getAnswers();
                }
            }
        });

        profile = new Profile();
        profile.setId(profile_id);

        profileFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addFollower();
            }
        });

        profileUnFollowBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addFollower();
            }
        });

        profileAskBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfileActivity.this, AskActivity.class);
                intent.putExtra("profileId", profile.getId());
                intent.putExtra("anonymousQuestions", profile.getAnonymousQuestions());
                startActivity(intent);
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (profile.getNormalPhotoUrl().length() > 0) {

                    Intent i = new Intent(ProfileActivity.this, PhotoViewActivity.class);
                    i.putExtra("imgUrl", profile.getNormalPhotoUrl());
                    startActivity(i);
                }
            }
        });

        if (App.getInstance().isConnected()) {

            showLoadingScreen();
            getData();

        } else {

            showErrorScreen();
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            getData();

        } else {

            mProfileContentScreen.setRefreshing(false);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        if (loadingComplete) {

            if (profile.getState() != ACCOUNT_STATE_ENABLED) {

                //hide all menu items
                hideMenuItems(menu, false);

                return true;
            }

            if (App.getInstance().getId() == profile.getId()) {

                // your profile
                menu.removeItem(R.id.action_profile_report);

            } else {

                menu.removeItem(R.id.action_profile_edit_photo);
                menu.removeItem(R.id.action_profile_edit_cover);
                menu.removeItem(R.id.action_profile_edit);
            }

            //show all menu items
            hideMenuItems(menu, true);

        } else {

            //hide all menu items
            hideMenuItems(menu, false);
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_profile_report: {

                /** Getting the fragment manager */
                FragmentManager manager = getFragmentManager();

                /** Instantiating the DialogFragment class */
                ProfileReportDialog alert = new ProfileReportDialog();

                /** Creating a bundle object to store the selected item's index */
                Bundle b  = new Bundle();

                /** Storing the selected item's index in the bundle object */
                b.putInt("position", 0);

                /** Setting the bundle object to the dialog fragment object */
                alert.setArguments(b);

                /** Creating the dialog fragment object, which will in turn open the alert dialog window */
                alert.show(manager, "alert_dialog_radio");

                return true;
            }

            case R.id.action_profile_copy_link: {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("qascript", profile.getUrl());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), getText(R.string.label_profile_link_copied), Toast.LENGTH_SHORT).show();

                return true;
            }

            case R.id.action_profile_open_link: {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(profile.getUrl()));
                startActivity(browserIntent);

                return true;
            }

            case R.id.action_profile_refresh: {

                profileListView.smoothScrollToPosition(0);
                mProfileContentScreen.setRefreshing(true);
                onRefresh();

                return true;
            }

            case R.id.action_profile_edit_photo: {

                selectPhoto();

//                Intent i = new Intent(this, PhotoUploadActivity.class);
//                startActivityForResult(i, 1);

                return true;
            }

            case R.id.action_profile_edit_cover: {

                selectCover();

                return true;
            }

            case R.id.action_profile_edit: {

                Intent i = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                i.putExtra("profileId", App.getInstance().getId());
                i.putExtra("fullname", profile.getFullname());
                startActivityForResult(i, PROFILE_EDIT);

                return true;
            }

            case R.id.action_profile_settings: {

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            // String selectedPhoto contains the path of selected Image
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            selectedPhoto = cursor.getString(columnIndex);
            cursor.close();

            UploadPhoto uploadProfilePhoto = new UploadPhoto();
            uploadProfilePhoto.execute();

        } else if (requestCode == SELECT_COVER && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            // String selectedCover contains the path of selected Image
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            selectedCover = cursor.getString(columnIndex);
            cursor.close();

            UploadCover uploadProfileCover = new UploadCover();
            uploadProfileCover.execute();

        } else if (requestCode == PROFILE_EDIT && resultCode == RESULT_OK) {

            profile.setFullname(data.getStringExtra("fullname"));
            updateFullname();

        } else {

            return;
        }
    }

    private void hideMenuItems(Menu menu, boolean visible) {

        for(int i = 0; i < menu.size(); i++){

            menu.getItem(i).setVisible(visible);

        }
    }

    public void updateProfile() {

        profileUsername.setText(profile.getUsername());

        // hide follow button is your profile
        if (profile.getId() == App.getInstance().getId()) {

            profileFollowBtn.setVisibility(View.GONE);
            profileUnFollowBtn.setVisibility(View.GONE);
        }

        updateFullname();
        updateFollowersCount();
        updateAnswersCount();
        updateLikesCount();

        showPhoto(profile.getLowPhotoUrl());
        showCover(profile.getNormalCoverUrl());

        supportInvalidateOptionsMenu();
    }

    private void updateFullname() {

        if (profile.getFullname().length() == 0) {

            profileFullname.setText(profile.getUsername());
            getSupportActionBar().setTitle(profile.getUsername());

        } else {

            profileFullname.setText(profile.getFullname());
            getSupportActionBar().setTitle(profile.getFullname());
        }

        if (!profile.isVerify()) {

            profileFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void updateLikesCount() {

        mLikesCount.setText(Integer.toString(profile.getLikesCount()));
    }

    private void updateFollowersCount() {

        mFollowersCount.setText(Integer.toString(profile.getFollowersCount()));
    }

    private void updateAnswersCount() {

        if (profile.getAnswersCount() == 0) {

            mProfileWallMsg.setVisibility(View.VISIBLE);

            if (App.getInstance().getId() == profile.getId()) {

                mProfileWallMsg.setText(getText(R.string.label_you_havent_answers));

            } else {

                mProfileWallMsg.setText(getText(R.string.label_user_havent_answers));
            }

        } else {

            mProfileWallMsg.setVisibility(View.GONE);
        }

        mAnswersCount.setText(Integer.toString(profile.getAnswersCount()));
    }

    public void updateFollowButton(Boolean follow) {

        updateFollowersCount();

        if (follow) {

//            profileFollowBtn.setText(R.string.action_unfollow);
            profileFollowBtn.setVisibility(View.GONE);
            profileUnFollowBtn.setVisibility(View.VISIBLE);

        } else {

//            profileFollowBtn.setText(R.string.action_follow);
            profileFollowBtn.setVisibility(View.VISIBLE);
            profileUnFollowBtn.setVisibility(View.GONE);
        }
    }

    public void getData() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {

                                profile = new Profile(response);

                                if (profile.getAnswersCount() > 0) {

                                    getAnswers();
                                }

                                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                                    showContentScreen();

                                    updateFollowButton(profile.isFollow());
                                    updateProfile();

                                } else {

                                    showDisabledScreen();
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showErrorScreen();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void addFollower() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_ADDFOLLOWER, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {

                                profile.setFollowersCount(response.getInt("followersCount"));

                                updateFollowButton(response.getBoolean("follow"));
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
                params.put("profileId", Long.toString(profile_id));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void showPhoto(String photoUrl) {

        if (photoUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(photoUrl, ImageLoader.getImageListener(profilePhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
        }
    }

    public void showCover(String coverUrl) {

        if (coverUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(coverUrl, ImageLoader.getImageListener(profileCover, R.drawable.profile_default_cover, R.drawable.profile_default_cover));
        }
    }

    /** Defining button click listener for the OK button of the alert dialog window */
    @Override
    public void onPositiveClick(final int position) {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_REPORTABUSE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {

//                                profile.setId(response.getLong("id"));
//                                profile.setUsername(response.getString("username"));
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Toast.makeText(getApplicationContext(), getText(R.string.label_profile_reported), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                     Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));
                params.put("abuseId", Integer.toString(position));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    private void selectPhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.dialog_select_image)), SELECT_PHOTO);
    }

    private void selectCover() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.dialog_select_image)), SELECT_COVER);
    }

    class UploadPhoto extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            showpDialog();
        }

        @Override
        protected String doInBackground(Void...params) {

            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(METHOD_PROFILE_UPLOADPHOTO);

            File sourceFile = new File(selectedPhoto);

            try {

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.STRICT);
                FileBody fileBody = new FileBody(sourceFile);
                builder.addPart("uploaded_file", fileBody);
                builder.addPart("accountId", new StringBody(Long.toString(App.getInstance().getId()), ContentType.TEXT_PLAIN));
                builder.addPart("accessToken", new StringBody(App.getInstance().getAccessToken(), ContentType.TEXT_PLAIN));

                HttpEntity entity = builder.build();

                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {

                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                } else {

                    responseString = "Error occurred! Http Status Code: "  + statusCode;
                }

            } catch (ClientProtocolException e) {

                responseString = e.toString();

            } catch (IOException e) {

                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            hidepDialog();
//            showAlert(result);

            try {

                JSONObject response = new JSONObject(result);

                if (response.getBoolean("error") == false) {

                    profile.setLowPhotoUrl(response.getString("lowPhotoUrl"));
                    profile.setBigPhotoUrl(response.getString("bigPhotoUrl"));
                    profile.setNormalCoverUrl(response.getString("normalPhotoUrl"));
                }

                Log.d("My App", response.toString());

            } catch (Throwable t) {

                Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");

            } finally {

                showPhoto(profile.getLowPhotoUrl());
            }

            super.onPreExecute();
        }
    }

    class UploadCover extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            showpDialog();
        }

        @Override
        protected String doInBackground(Void...params) {

            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(METHOD_PROFILE_UPLOADCOVER);

            File sourceFile = new File(selectedCover);

            try {

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.STRICT);
                FileBody fileBody = new FileBody(sourceFile);
                builder.addPart("uploaded_file", fileBody);
                builder.addPart("accountId", new StringBody(Long.toString(App.getInstance().getId()), ContentType.TEXT_PLAIN));
                builder.addPart("accessToken", new StringBody(App.getInstance().getAccessToken(), ContentType.TEXT_PLAIN));

                HttpEntity entity = builder.build();

                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {

                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                } else {

                    responseString = "Error occurred! Http Status Code: "  + statusCode;
                }

            } catch (ClientProtocolException e) {

                responseString = e.toString();

            } catch (IOException e) {

                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            hidepDialog();
//            showAlert(result);

            try {

                JSONObject response = new JSONObject(result);

                if (response.getBoolean("error") == false) {

                    profile.setNormalCoverUrl(response.getString("normalCoverUrl"));
                }

                Log.d("My App", response.toString());

            } catch (Throwable t) {

                Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");

            } finally {

                showCover(profile.getNormalCoverUrl());
            }

            super.onPreExecute();
        }
    }

    private void showAlert(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getAnswers() {

        if (loadingMore) {

            mProfileContentScreen.setRefreshing(true);

        } else{

            replyAt = 0;
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_WALL_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!loadingMore) {

                                answersList.clear();
                            }

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

                                            answersList.add(answer);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile.getId()));
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

        adapter.notifyDataSetChanged();

        mProfileContentScreen.setRefreshing(false);

        loadingMore = false;
    }

    public void showLoadingScreen() {

        getSupportActionBar().setTitle(getText(R.string.title_activity_profile));

        mProfileContainer.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);

        mProfileLoadingScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;

        supportInvalidateOptionsMenu();
    }

    public void showErrorScreen() {

        getSupportActionBar().setTitle(getText(R.string.title_activity_profile));

        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileContainer.setVisibility(View.GONE);

        mProfileErrorScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;

        supportInvalidateOptionsMenu();
    }

    public void showDisabledScreen() {

        if (profile.getState() == ACCOUNT_STATE_BLOCKED) {

            mProfileDisabledScreenMsg.setText(getText(R.string.label_profile_blocked));

        } else {

            mProfileDisabledScreenMsg.setText(getText(R.string.label_profile_disabled));
        }

        getSupportActionBar().setTitle(getText(R.string.account_disabled));

        mProfileContainer.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);

        mProfileDisabledScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;

        supportInvalidateOptionsMenu();
    }

    public void showContentScreen() {

        getSupportActionBar().setTitle(profile.getFullname());

        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);

        mProfileContainer.setVisibility(View.VISIBLE);
        mProfileContentScreen.setVisibility(View.VISIBLE);
        mProfileContentScreen.setRefreshing(false);

        loadingComplete = true;

        supportInvalidateOptionsMenu();

        if (ADMOB) {

            mProfileAdMobCont.setVisibility(View.VISIBLE);

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }
}
