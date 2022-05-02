package ua.com.qascript.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.qascript.android.HashtagsActivity;
import ua.com.qascript.android.PhotoViewActivity;
import ua.com.qascript.android.ProfileActivity;
import ua.com.qascript.android.R;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.constants.Constants;
import ua.com.qascript.android.model.Answer;
import ua.com.qascript.android.util.AnswerInterface;
import ua.com.qascript.android.util.CustomRequest;
import ua.com.qascript.android.util.TagClick;
import ua.com.qascript.android.util.TagSelectingTextview;
import ua.com.qascript.android.view.ResizableImageView;

public class StreamListAdapter extends BaseAdapter implements Constants, TagClick {

	private Activity activity;
	private LayoutInflater inflater;
	private List<Answer> answersList;

    TagSelectingTextview mTagSelectingTextview;

    public static int hashTagHyperLinkEnabled = 1;
    public static int hashTagHyperLinkDisabled = 0;

    private AnswerInterface responder;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

	public StreamListAdapter(Activity activity, List<Answer> answersList, AnswerInterface responder) {

		this.activity = activity;
		this.answersList = answersList;
        this.responder = responder;

        mTagSelectingTextview = new TagSelectingTextview();
	}

	@Override
	public int getCount() {

		return answersList.size();
	}

	@Override
	public Object getItem(int location) {

		return answersList.get(location);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	static class ViewHolder {

        public TextView questionText;
        public TextView answerText;
		public TextView questionAuthor;
        public TextView answerReplyAt;
        public TextView answerLikesCount;
		public ImageView answerAuthor;
        public ImageView answerLike;
        public ResizableImageView answerImg;
	        
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.reply_list_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.answerAuthor = (ImageView) convertView.findViewById(R.id.answerAuthor);
            viewHolder.answerImg = (ResizableImageView) convertView.findViewById(R.id.answerImg);
            viewHolder.answerLike = (ImageView) convertView.findViewById(R.id.answerLike);
            viewHolder.answerLikesCount = (TextView) convertView.findViewById(R.id.answerLikesCount);
			viewHolder.questionText = (TextView) convertView.findViewById(R.id.questionText);
            viewHolder.answerText = (TextView) convertView.findViewById(R.id.answerText);
            viewHolder.questionAuthor = (TextView) convertView.findViewById(R.id.questionAuthor);
            viewHolder.answerReplyAt = (TextView) convertView.findViewById(R.id.answerReplyAt);

//            viewHolder.questionRemove.setTag(position);
            convertView.setTag(viewHolder);

            viewHolder.answerAuthor.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();
                    ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.answerAction);
                    // TODO Auto-generated method stub

                    Answer answer = answersList.get(getPosition);

                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("profileId", answer.getToUserId());
                    activity.startActivity(intent);
                }
            });

            viewHolder.questionAuthor.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();

                    Answer answer = answersList.get(getPosition);

                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("profileId", answer.getFromUserId());
                    activity.startActivity(intent);
                }
            });

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

//        viewHolder.questionRemove.setTag(position);
        viewHolder.questionText.setTag(position);
        viewHolder.questionAuthor.setTag(position);
        viewHolder.answerReplyAt.setTag(position);
        viewHolder.answerAuthor.setTag(position);
        viewHolder.answerText.setTag(position);
        viewHolder.answerImg.setTag(position);
        viewHolder.answerLikesCount.setTag(position);
        viewHolder.answerLike.setTag(position);
        viewHolder.answerAuthor.setTag(R.id.answerAuthor, viewHolder);
		
		final Answer answer = answersList.get(position);

        if (answer.getToUserPhotoUrl().length() != 0) {

            viewHolder.answerAuthor.setImageResource(R.drawable.profile_default_photo);
            imageLoader.get(answer.getToUserPhotoUrl(), ImageLoader.getImageListener(viewHolder.answerAuthor, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            viewHolder.answerAuthor.setImageResource(R.drawable.profile_default_photo);
        }

        viewHolder.questionText.setText(answer.getQuestionText());

        if (answer.getFromUserId() != 0) {

            viewHolder.questionAuthor.setVisibility(View.VISIBLE);
            viewHolder.questionAuthor.setText(answer.getFromUserFullname());

        } else {

            viewHolder.questionAuthor.setVisibility(View.GONE);
        }

        if (answer.getAnswerText().length() > 0) {

            viewHolder.answerText.setMovementMethod(LinkMovementMethod.getInstance());

            String textHtml = answer.getAnswerText().replaceAll("<br>", "\n");

            viewHolder.answerText.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

//            viewHolder.answerText.setText( answer.getAnswerText().replaceAll("<br>", "\n"));
            viewHolder.answerText.setVisibility(View.VISIBLE);

        } else {

            viewHolder.answerText.setVisibility(View.GONE);
        }

        if (answer.getImgUrl().length() > 0) {

            imageLoader.get(answer.getImgUrl(), ImageLoader.getImageListener(viewHolder.answerImg, R.drawable.img_loading, R.drawable.img_loading));
            viewHolder.answerImg.setVisibility(View.VISIBLE);

            viewHolder.answerImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(activity, PhotoViewActivity.class);
                    i.putExtra("imgUrl", answer.getImgUrl());
                    activity.startActivity(i);
                }
            });

        } else {

            viewHolder.answerImg.setVisibility(View.GONE);
        }

        //		 Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                answer.getReplyAt() * 1000l,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        viewHolder.answerReplyAt.setText(timeAgo);

        viewHolder.answerLikesCount.setVisibility(View.GONE);
        viewHolder.answerLikesCount.setText(Integer.toString(answer.getLikesCount()));
        viewHolder.answerLike.setImageResource(R.drawable.like);

        if (answer.getLikesCount() != 0) {

            viewHolder.answerLikesCount.setVisibility(View.VISIBLE);

            if (answer.isMyLike()) {

                viewHolder.answerLike.setImageResource(R.drawable.like_active);
            }
        }

        viewHolder.answerLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final int getPosition = (Integer) v.getTag();

                if (App.getInstance().isConnected()) {

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ANSWERS_LIKE, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        if (response.getBoolean("error") == false) {

                                            responder.like(getPosition, response);

                                            answer.setLikesCount(response.getInt("likesCount"));
                                            answer.setMyLike(response.getBoolean("myLike"));
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        notifyDataSetChanged();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

//                        Toast.makeText(activity.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("accountId", Long.toString(App.getInstance().getId()));
                            params.put("accessToken", App.getInstance().getAccessToken());
                            params.put("answerId", Long.toString(answer.getId()));

                            return params;
                        }
                    };

                    App.getInstance().addToRequestQueue(jsonReq);

                } else {

                    Toast.makeText(activity.getApplicationContext(), activity.getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

		return convertView;
	}

    @Override
    public void clickedTag(CharSequence tag) {
        // TODO Auto-generated method stub

        Intent i = new Intent(activity, HashtagsActivity.class);
        i.putExtra("hashtag", tag);
        activity.startActivity(i);
    }
}