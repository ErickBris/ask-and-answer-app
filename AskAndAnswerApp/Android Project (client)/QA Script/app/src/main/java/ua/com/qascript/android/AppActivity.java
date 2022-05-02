package ua.com.qascript.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.FacebookSdk;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.service.RegistrationIntentService;
import ua.com.qascript.android.util.CustomRequest;


public class AppActivity extends ActivityBase {

    Button loginBtn, signupBtn;

    TextView appTextLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        startService(new Intent(this, RegistrationIntentService.class));

        setContentView(R.layout.activity_app);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);

        appTextLabel = (TextView) findViewById(R.id.appTextLabel);

        hideButtons();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void  onStart() {

        super.onStart();

        if (!App.getInstance().isConnected()) {

            showLabel();

        } else if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

             CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_AUTH_AUTHORIZE, null,
                     new Response.Listener<JSONObject>() {
                         @Override
                         public void onResponse(JSONObject response) {

                             if (App.getInstance().authorize(response)) {

                                 if (App.getInstance().getState() == ACCOUNT_STATE_ENABLED) {

                                     ActivityCompat.finishAffinity(AppActivity.this);

                                     Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                     startActivity(i);

                                 } else {

                                     App.getInstance().logout();
                                     showButtons();
                                 }

                             } else {

                                 showButtons();
                             }
                         }
                     }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {

//                     Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                     showButtons();
                 }
             }) {

                 @Override
                 protected Map<String, String> getParams() {
                     Map<String, String> params = new HashMap<String, String>();
                     params.put("accountId", Long.toString(App.getInstance().getId()));
                     params.put("accessToken", App.getInstance().getAccessToken());
                     params.put("gcm_regId", App.getInstance().getGcmToken());

                     return params;
                 }
             };

             App.getInstance().addToRequestQueue(jsonReq);

        } else {

             showButtons();
        }
    }

    public void showLabel() {

        appTextLabel.setVisibility(View.VISIBLE);
    }

    public void showButtons() {

        loginBtn.setVisibility(View.VISIBLE);
        signupBtn.setVisibility(View.VISIBLE);
    }

    public void hideButtons() {

        loginBtn.setVisibility(View.GONE);
        signupBtn.setVisibility(View.GONE);
    }
}
