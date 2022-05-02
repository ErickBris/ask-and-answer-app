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
import ua.com.qascript.android.constants.Constants;
import ua.com.qascript.android.model.Notify;

public class NotifyListAdapter extends BaseAdapter implements Parcelable, Constants {

	private Activity activity;
	private LayoutInflater inflater;
	private List<Notify> notifyList;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

	public NotifyListAdapter(Activity activity, List<Notify> notifyList) {

		this.activity = activity;
		this.notifyList = notifyList;
	}

	@Override
	public int getCount() {

		return notifyList.size();
	}

	@Override
	public Object getItem(int location) {

		return notifyList.get(location);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	static class ViewHolder {

        public TextView notifyTitle;
        public TextView notifyCreateAt;
		public ImageView notifyAuthor;
	        
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.notify_list_row, null);
			
			viewHolder = new ViewHolder();

            viewHolder.notifyAuthor = (ImageView) convertView.findViewById(R.id.notifyAuthor);
            viewHolder.notifyTitle = (TextView) convertView.findViewById(R.id.notifyTitle);
			viewHolder.notifyCreateAt = (TextView) convertView.findViewById(R.id.notifyCreateAt);

            convertView.setTag(viewHolder);

            viewHolder.notifyAuthor.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();

                    Notify notify = notifyList.get(getPosition);

                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("profileId", notify.getFromUserId());
                    activity.startActivity(intent);
                }
            });

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        viewHolder.notifyTitle.setTag(position);
        viewHolder.notifyCreateAt.setTag(position);
        viewHolder.notifyAuthor.setTag(position);
        viewHolder.notifyAuthor.setTag(R.id.notifyAuthor, viewHolder);
		
		final Notify notify = notifyList.get(position);

        if (notify.getType() == NOTIFY_TYPE_LIKE) {

            viewHolder.notifyTitle.setText(notify.getFromUserFullname() + " " + activity.getText(R.string.label_likes_answer));

        } else {

            viewHolder.notifyTitle.setText(notify.getFromUserFullname() + " " + activity.getText(R.string.label_answered_question));
        }

        if (notify.getFromUserPhotoUrl().length() > 0) {

            imageLoader.get(notify.getFromUserPhotoUrl(), ImageLoader.getImageListener(viewHolder.notifyAuthor, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            viewHolder.notifyAuthor.setImageResource(R.drawable.profile_default_photo);
        }

        //		 Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                notify.getCreateAt() * 1000l,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        viewHolder.notifyCreateAt.setText(timeAgo);

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