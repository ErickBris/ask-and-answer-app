package ua.com.qascript.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;


public class WelcomeActivity extends ActivityBase {

    private static final String TAG = "welcome_activity";

    Toolbar toolbar;

    RelativeLayout mAccountStateDisabledScreen, mWelcomeScreen;

    Button mDisabledScreenActivateBtn, mWelcomeScreenBtn, mDisabledScreenLogoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        mAccountStateDisabledScreen = (RelativeLayout) findViewById(R.id.AccountStateDisabledScreen);
        mWelcomeScreen = (RelativeLayout) findViewById(R.id.WelcomeScreen);

        mDisabledScreenActivateBtn = (Button) findViewById(R.id.DisabledScreenActivateBtn);
        mWelcomeScreenBtn = (Button) findViewById(R.id.WelcomeScreenBtn);
        mDisabledScreenLogoutBtn = (Button) findViewById(R.id.DisabledScreenLogoutBtn);

        mDisabledScreenActivateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                accountStart();
            }
        });

        mWelcomeScreenBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                accountStart();
            }
        });

        mDisabledScreenLogoutBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                accountLogout();
            }
        });

        switch (App.getInstance().getState()) {

            case ACCOUNT_STATE_DISABLED: {

                getSupportActionBar().setTitle(getText(R.string.action_account_activate));

                showDisabledScreen();

                break;
            }

            default: {

                showWelcomeScreen();

                break;
            }
        }
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home: {

                App.getInstance().logout();

                finish();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void accountLogout() {

        App.getInstance().logout();

        ActivityCompat.finishAffinity(WelcomeActivity.this);

        Intent i = new Intent(getApplicationContext(), AppActivity.class);
        startActivity(i);
    }

    public void accountStart() {

        if (App.getInstance().isConnected()) {

            App.getInstance().accountSetState(ACCOUNT_STATE_ENABLED);
        }

        ActivityCompat.finishAffinity(WelcomeActivity.this);

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    public void showWelcomeScreen() {

        mAccountStateDisabledScreen.setVisibility(View.GONE);
        mWelcomeScreen.setVisibility(View.VISIBLE);
    }

    public void showDisabledScreen() {

        mAccountStateDisabledScreen.setVisibility(View.VISIBLE);
        mWelcomeScreen.setVisibility(View.GONE);
    }
}
