package ua.com.qascript.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ua.com.qascript.android.app.App;
import ua.com.qascript.android.common.ActivityBase;
import ua.com.qascript.android.util.CustomRequest;


public class ReplyActivity extends ActivityBase {

    Toolbar toolbar;

    EditText answerEdit;
    ImageView choiceAnswerImg;

    long questionId;
    int listPosition;
    String answerText = "", answerImg = "";
    private String selectedAnswerImg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        //        Инициализируем Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent i = getIntent();
        questionId = i.getLongExtra("questionId", 0);
        listPosition = i.getIntExtra("listPosition", -1);

        answerEdit = (EditText) findViewById(R.id.answerEdit);
        choiceAnswerImg = (ImageView) findViewById(R.id.choiceAnswerImg);

        choiceAnswerImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedAnswerImg.length() == 0) {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent, getText(R.string.dialog_select_image)), QUESTION_SELECT_ANSWER_IMG);

                } else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReplyActivity.this);
                    alertDialog.setTitle(getText(R.string.action_remove));

                    alertDialog.setMessage(getText(R.string.label_remove_picture));
                    alertDialog.setCancelable(true);

                    alertDialog.setNeutralButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    alertDialog.setPositiveButton(getText(R.string.action_remove), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            choiceAnswerImg.setImageResource(R.drawable.ic_action_picture);
                            selectedAnswerImg = "";
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QUESTION_SELECT_ANSWER_IMG && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            // String selectedPhoto contains the path of selected Image
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            selectedAnswerImg = cursor.getString(columnIndex);
            cursor.close();

            choiceAnswerImg.setImageURI(selectedImage);
        }
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reply, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_answer: {

                if (App.getInstance().isConnected()) {

                    answerText = answerEdit.getText().toString();
                    answerText = answerText.trim();

                    if (selectedAnswerImg.length() != 0) {

                        showpDialog();
                        UploadImg uploadAnswerImg = new UploadImg();
                        uploadAnswerImg.execute();

                    } else {

                        if (answerText.length() > 0) {

                            showpDialog();
                            sendAnswer();

                        } else {

                            Toast toast= Toast.makeText(getApplicationContext(), getText(R.string.msg_enter_answer), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }

                } else {

                    Toast toast= Toast.makeText(getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
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

    public void sendAnswer() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_QUESTIONS_REPLY, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {


                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            sendAnswerSuccess();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                sendAnswerSuccess();

//                     Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("questionId", Long.toString(questionId));
                params.put("answerText", answerText);
                params.put("answerImg", answerImg);

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void sendAnswerSuccess() {

        hidepDialog();

        Intent i = new Intent();
        i.putExtra("listPosition", listPosition);
        setResult(RESULT_OK, i);

        Toast.makeText(getApplicationContext(), getText(R.string.msg_answer_has_been_published), Toast.LENGTH_SHORT).show();

        finish();
    }

    class UploadImg extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void...params) {

            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(METHOD_QUESTIONS_UPLOADIMG);

            File sourceFile = new File(selectedAnswerImg);

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

            try {

                JSONObject response = new JSONObject(result);

                if (response.getBoolean("error") == false) {

                    answerImg = response.getString("imgUrl");
                }

                Log.d("My App", response.toString());

            } catch (Throwable t) {

                Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");

            } finally {

                sendAnswer();
            }

            super.onPreExecute();
        }
    }
}
