package ua.com.qascript.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import ua.com.qascript.android.AnswerActivity;
import ua.com.qascript.android.FriendsActivity;
import ua.com.qascript.android.NotifyLikesActivity;
import ua.com.qascript.android.QuestionsActivity;
import ua.com.qascript.android.R;
import ua.com.qascript.android.app.App;
import ua.com.qascript.android.constants.Constants;


/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class GcmService extends GcmListenerService implements Constants {

    public GcmService() {

    }

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String msg = data.getString("msg");
        int type = data.getInt("type");

        generateNotification(getApplicationContext(), data);
        Log.e("Message", "Could not parse malformed JSON: \"" + data.toString() + "\"");
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {

        Log.e("Message", "Could not parse malformed JSON: \"" + msg + "\"");
    }

    /**
     * Create a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, Bundle data) {

        String message = data.getString("msg");
        String type = data.getString("type");
        String actionId = data.getString("id");
        String accountId = data.getString("accountId");

        int icon = R.drawable.ic_action_chat;
        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);

        switch (Integer.valueOf(type)) {

            case GCM_NOTIFY_ANSWER: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    message = context.getString(R.string.gcm_answer);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new Notification(icon, message, when);

                    Intent notificationIntent = new Intent(context, AnswerActivity.class);

                    notificationIntent.putExtra("answerId", Long.valueOf(actionId).longValue());
                    notificationIntent.putExtra("action", "show");

                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setLatestEventInfo(context, title, message, intent);
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, notification);
                }

                break;
            }

            case GCM_NOTIFY_QUESTION: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    message = context.getString(R.string.gcm_question);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new Notification(icon, message, when);

                    Intent notificationIntent = new Intent(context, QuestionsActivity.class);

                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setLatestEventInfo(context, title, message, intent);
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, notification);
                }

                break;
            }

            case GCM_NOTIFY_LIKE: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    message = context.getString(R.string.gcm_like);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new Notification(icon, message, when);

                    Intent notificationIntent = new Intent(context, NotifyLikesActivity.class);

                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setLatestEventInfo(context, title, message, intent);
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, notification);
                }

                break;
            }

            case GCM_NOTIFY_FOLLOWER: {

                if (App.getInstance().getId() != 0 && Long.toString(App.getInstance().getId()).equals(accountId)) {

                    message = context.getString(R.string.gcm_follower);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new Notification(icon, message, when);

                    Intent notificationIntent = new Intent(context, FriendsActivity.class);

                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setLatestEventInfo(context, title, message, intent);
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, notification);
                }

                break;
            }

            default: {

                break;
            }
        }
    }
}
