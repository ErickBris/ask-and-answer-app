package ua.com.qascript.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import ua.com.qascript.android.ProfileActivity;
import ua.com.qascript.android.R;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.model.Question;
import ua.com.qascript.android.util.ResponderInterface;

public class QuestionsListAdapter extends BaseAdapter implements Parcelable {

	private Activity activity;
	private LayoutInflater inflater;
	private List<Question> questionsList;
    private ResponderInterface responder;

	ImageLoader imageLoader = App.getInstance().getImageLoader();

	public QuestionsListAdapter(Activity activity, List<Question> questionsList, ResponderInterface responder) {

		this.activity = activity;
		this.questionsList = questionsList;
        this.responder = responder;
	}

	@Override
	public int getCount() {

		return questionsList.size();
	}

	@Override
	public Object getItem(int location) {

		return questionsList.get(location);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	static class ViewHolder {

        public TextView questionText;
		public TextView questionAuthor;
        public TextView questionCreateAt;
		public ImageView questionRemove;
	        
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.question_list_row, null);
			
			final Question q = questionsList.get(position);
			
			viewHolder = new ViewHolder();
			
			viewHolder.questionRemove = (ImageView) convertView.findViewById(R.id.questionRemove);
			viewHolder.questionText = (TextView) convertView.findViewById(R.id.questionText);
            viewHolder.questionAuthor = (TextView) convertView.findViewById(R.id.questionAuthor);
            viewHolder.questionCreateAt = (TextView) convertView.findViewById(R.id.questionCreateAt);

//            viewHolder.questionRemove.setTag(position);
            convertView.setTag(viewHolder);
			
			viewHolder.questionRemove.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					int getPosition = (Integer) v.getTag();
					ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.questionRemove);
					// TODO Auto-generated method stub

                    Question q = questionsList.get(getPosition);
                    q.remove();

                    questionsList.remove(getPosition);
                    notifyDataSetChanged();

                    responder.listViewItemChange();
				}
			});

            viewHolder.questionAuthor.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();

                    Question q = questionsList.get(getPosition);

                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("profileId", q.getFromUserId());
                    activity.startActivity(intent);
                }
            });

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

//        viewHolder.questionRemove.setTag(position);
        viewHolder.questionText.setTag(position);
        viewHolder.questionAuthor.setTag(position);
        viewHolder.questionCreateAt.setTag(position);
        viewHolder.questionRemove.setTag(position);
        viewHolder.questionRemove.setTag(R.id.questionRemove, viewHolder);
		
		final Question q = questionsList.get(position);

        if (q.getFromUserId() != 0) {

            viewHolder.questionAuthor.setVisibility(View.VISIBLE);
            viewHolder.questionAuthor.setText(q.getFromUserFullname());

        } else {

            viewHolder.questionAuthor.setVisibility(View.GONE);
        }

        viewHolder.questionText.setText(q.getText());

        //		 Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                q.getCreateAt() * 1000l,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        viewHolder.questionCreateAt.setText(timeAgo);

		return convertView;
	}

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}