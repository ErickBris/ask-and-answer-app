package ua.com.qascript.android.model;

import android.app.Application;
import android.util.Log;

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


public class Question extends Application implements Constants {

    private long id, toUserId, fromUserId;
    private Boolean fromAccount;
    private int type, createAt, addedToListAt;
    private String text, fromUserFullname;

    public Question() {

    }

    public Question(JSONObject jsonData) {

        try {

            if (jsonData.getBoolean("error") == false) {

                this.setId(jsonData.getLong("id"));
                this.setToUserId(jsonData.getLong("toUserId"));
                this.setFromUserId(jsonData.getLong("fromUserId"));
                this.setFromUserFullname(jsonData.getString("fromUserFullname"));
                this.setFromAccount(jsonData.getBoolean("fromAccount"));
                this.setType(jsonData.getInt("questionType"));
                this.setText(jsonData.getString("question"));
                this.setAddedToListAt(jsonData.getInt("addedToListAt"));
                this.setCreateAt(jsonData.getInt("createAt"));
            }

        } catch (Throwable t) {

            Log.e("Question", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Question", jsonData.toString());
        }
    }

    public void setId(long questionId) {

        this.id = questionId;
    }

    public long getId() {

        return this.id;
    }

    public void setType(int questionType) {

        this.type = questionType;
    }

    public int getType() {

        return this.type;
    }

    public void setCreateAt(int createAt) {

        this.createAt = createAt;
    }

    public int getCreateAt() {

        return this.createAt;
    }

    public void setAddedToListAt(int addedToListAt) {

        this.addedToListAt = addedToListAt;
    }

    public int getAddedToListAt() {

        return this.addedToListAt;
    }

    public void setText(String questionText) {

        this.text = questionText;
    }

    public String getText() {

        return this.text;
    }

    public void setToUserId(long toUserId) {

        this.toUserId = toUserId;
    }

    public long getToUserId() {

        return this.toUserId;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public long getFromUserId() {

        return this.fromUserId;
    }

    public void setFromUserFullname(String fromUserFullname) {

        this.fromUserFullname = fromUserFullname;
    }

    public String getFromUserFullname() {

        return this.fromUserFullname;
    }

    public void setFromAccount(Boolean fromAccount) {

        this.fromAccount = fromAccount;
    }

    public Boolean isFromAccount() {

        return this.fromAccount;
    }












    public void remove() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_QUESTIONS_REMOVE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {


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

//                     Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("questionId", Long.toString(getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }
}
