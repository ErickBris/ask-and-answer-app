package ua.com.qascript.android;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.util.CustomRequest;
import ua.com.qascript.android.util.Helper;


public class PasswordRecoveryActivity extends ActivityBase {

    private static final String TAG = "password_recovery_activity";

    Toolbar toolbar;

    Button mContinueBtn;
    EditText mEmail;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mEmail = (EditText) findViewById(R.id.PasswordRecoveryEmail);

        mContinueBtn = (Button) findViewById(R.id.PasswordRecoveryBtn);

        mContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = mEmail.getText().toString();

                if (!App.getInstance().isConnected()) {

                    Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();

                } else {

                    Helper helper = new Helper();

                    if (helper.isValidEmail(email)) {

                        recovery();

                    } else {

                        Toast.makeText(getApplicationContext(), getText(R.string.error_email), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed(){

        finish();
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

    public void recovery() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_APP_PASSWORD_RECOVERY, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {


                                Toast.makeText(getApplicationContext(), getText(R.string.msg_password_reset_link_sent), Toast.LENGTH_SHORT).show();
                                finish();

                            } else {

                                Toast.makeText(getApplicationContext(), getText(R.string.msg_no_such_user_in_bd), Toast.LENGTH_SHORT).show();
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

                Toast.makeText(getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("email", email);

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }
}
