package ua.com.qascript.android.model;

import android.app.Application;
import android.util.Log;
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


public class Answer extends Application implements Constants {

    private long id, toUserId, fromUserId;
    private int type, replyAt, likesCount;
    private String questionText, answerText, fromUserFullname, fromUserUsername, previewImgUrl, imgUrl, toUserPhotoUrl;
    private Boolean myLike;

    public Answer() {

    }

    public Answer(JSONObject jsonData) {

        try {

            if (jsonData.getBoolean("error") == false) {

                this.setId(jsonData.getLong("id"));
                this.setToUserId(jsonData.getLong("toUserId"));
                this.setToUserPhotoUrl(jsonData.getString("toUserPhotoUrl"));
                this.setFromUserId(jsonData.getLong("fromUserId"));
                this.setFromUserFullname(jsonData.getString("fromUserFullname"));
                this.setFromUserUsername(jsonData.getString("fromUserUsername"));
                this.setType(jsonData.getInt("questionType"));
                this.setLikesCount(jsonData.getInt("likesCount"));
                this.setMyLike(jsonData.getBoolean("myLike"));
                this.setQuestionText(jsonData.getString("question"));
                this.setAnswerText(jsonData.getString("answer"));
                this.setReplyAt(jsonData.getInt("replyAt"));
                this.setPreviewImgUrl(jsonData.getString("previewImgUrl"));
                this.setImgUrl(jsonData.getString("imgUrl"));
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

    public void setLikesCount(int likesCount) {

        this.likesCount = likesCount;
    }

    public int getLikesCount() {

        return this.likesCount;
    }

    public void setMyLike(Boolean myLike) {

        this.myLike = myLike;
    }

    public Boolean isMyLike() {

        return this.myLike;
    }

    public void setReplyAt(int replyAt) {

        this.replyAt = replyAt;
    }

    public int getReplyAt() {

        return this.replyAt;
    }

    public void setQuestionText(String questionText) {

        this.questionText = questionText;
    }

    public String getQuestionText() {

        return this.questionText;
    }

    public void setAnswerText(String answerText) {

        this.answerText = answerText;
    }

    public String getAnswerText() {

        return this.answerText;
    }

    public void setToUserId(long toUserId) {

        this.toUserId = toUserId;
    }

    public long getToUserId() {

        return this.toUserId;
    }

    public void setToUserPhotoUrl(String toUserPhotoUrl) {

        this.toUserPhotoUrl = toUserPhotoUrl;
    }

    public String getToUserPhotoUrl() {

        return this.toUserPhotoUrl;
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

    public void setFromUserUsername(String fromUserUsername) {

        this.fromUserUsername = fromUserUsername;
    }

    public String getFromUserUsername() {

        return this.fromUserUsername;
    }

    public void setImgUrl(String imgUrl) {

        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {

        return this.imgUrl;
    }

    public void setPreviewImgUrl(String previewImgUrl) {

        this.previewImgUrl = previewImgUrl;
    }

    public String getPreviewImgUrl() {

        return this.previewImgUrl;
    }












    public void remove() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ANSWERS_REMOVE, null,
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
                params.put("answerId", Long.toString(getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void like() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ANSWERS_LIKE, null,
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

                     Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("answerId", Long.toString(getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }
}
