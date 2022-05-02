package ua.com.qascript.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ua.com.qascript.android.app.App;
import ua.com.qascript.android.constants.Constants;
import ua.com.qascript.android.util.CustomRequest;


public class SettingsActivity extends ActionBarActivity implements Constants {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //        Инициализируем Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.settings_content, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.\

        switch (item.getItemId()) {

            case R.id.action_settings: {

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SettingsFragment extends PreferenceFragment {

        private Preference logoutPreference, aboutPreference, changePassword, itemTerms, itemThanks;
        private CheckBoxPreference allowAnonymousQuestions;

        private ProgressDialog pDialog;

        LinearLayout aboutDialogContent;
        TextView aboutDialogAppName, aboutDialogAppVersion, aboutDialogAppCopyright;

        int mAnonymousQuestions;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            initpDialog();

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);

            Preference pref = findPreference("settings_version");

            pref.setTitle(APP_NAME + " v" + APP_VERSION);

            pref = findPreference("settings_logout");

            pref.setSummary(App.getInstance().getUsername());

            pref = findPreference("settings_copyright_info");

            pref.setSummary(APP_COPYRIGHT + " © " + APP_YEAR);

            logoutPreference = findPreference("settings_logout");
            aboutPreference = findPreference("settings_version");
            changePassword = findPreference("settings_change_password");
            itemTerms = findPreference("settings_terms");
            itemThanks = findPreference("settings_thanks");

            itemThanks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {

                    Intent i = new Intent(getActivity(), WebViewActivity.class);
                    i.putExtra("url", METHOD_APP_THANKS);
                    i.putExtra("title", getText(R.string.settings_thanks));
                    startActivity(i);

                    return true;
                }
            });

            itemTerms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {

                    Intent i = new Intent(getActivity(), WebViewActivity.class);
                    i.putExtra("url", METHOD_APP_TERMS);
                    i.putExtra("title", getText(R.string.settings_terms));
                    startActivity(i);

                    return true;
                }
            });

            allowAnonymousQuestions = (CheckBoxPreference) getPreferenceManager().findPreference("allowAnonymousQuestions");

            allowAnonymousQuestions.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (newValue instanceof Boolean) {

                        Boolean value = (Boolean) newValue;

                        if (value) {

                            mAnonymousQuestions = 1;

                        } else {

                            mAnonymousQuestions = 0;
                        }

                        if (App.getInstance().isConnected()) {

                            setAnonymousQuestions();

                        } else {

                            Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    return true;
                }
            });

            aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getText(R.string.action_about));

                    aboutDialogContent = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);

                    alertDialog.setView(aboutDialogContent);

                    aboutDialogAppName = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppName);
                    aboutDialogAppVersion = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppVersion);
                    aboutDialogAppCopyright = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppCopyright);

                    aboutDialogAppName.setText(APP_NAME);
                    aboutDialogAppVersion.setText("Version " + APP_VERSION);
                    aboutDialogAppCopyright.setText("Copyright © " + APP_YEAR + " " + APP_COPYRIGHT);

//                    alertDialog.setMessage("Version " + APP_VERSION + "/r/n" + APP_COPYRIGHT);
                    alertDialog.setCancelable(true);
                    alertDialog.setNeutralButton(getText(R.string.lang_button_ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    alertDialog.show();

                    return false;
                }
            });

            logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {

                    App.getInstance().logout();

                    ActivityCompat.finishAffinity(getActivity());

                    Intent i = new Intent(getActivity(), AppActivity.class);
                    startActivity(i);

//                    getActivity().finish();

                    return true;
                }
            });

            changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
                    startActivity(i);

                    return true;
                }
            });

            checkAnonymousQuestions(App.getInstance().getAllowAnonymousQuestions());
        }

        public void checkAnonymousQuestions(int value) {

            if (value == 1) {

                allowAnonymousQuestions.setChecked(true);
                mAnonymousQuestions = 1;

            } else {

                allowAnonymousQuestions.setChecked(false);
                mAnonymousQuestions = 0;
            }
        }

        public void setAnonymousQuestions() {

            showpDialog();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_ANONYMOUSQUESTIONS, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (response.getBoolean("error") == false) {

                                    App.getInstance().setAllowAnonymousQuestions(response.getInt("anonymousQuestions"));

                                    checkAnonymousQuestions(App.getInstance().getAllowAnonymousQuestions());
                                }

//                                 Toast.makeText(getActivity().getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                hidepDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                     Toast.makeText(getActivity().getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());
                    params.put("anonymousQuestions", Integer.toString(mAnonymousQuestions));

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }

        protected void initpDialog() {

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.msg_loading));
            pDialog.setCancelable(false);
        }

        protected void showpDialog() {

            if (!pDialog.isShowing())
                pDialog.show();
        }

        protected void hidepDialog() {

            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}
