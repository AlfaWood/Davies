package com.mdff.app.cloud_messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;



import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mdff.app.R;
import com.mdff.app.activity.MessageDetails;
import com.mdff.app.model.MessageItems;
import com.mdff.app.utility.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private String title, message, click_action;
    private String CHANNEL_ID = "MyApp";
    private MessageItems messageItems;
    AppUtil appUtil;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        appUtil = new AppUtil(this);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            JSONObject data = new JSONObject(remoteMessage.getData());
            sendPushNotification(data);
            Log.e("@#@#@#@#@#@#","********************"+data);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle(); //get title
            message = remoteMessage.getNotification().getBody(); //get message
            click_action = remoteMessage.getNotification().getClickAction(); //get click_action

            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + message);
            Log.d(TAG, "Notification click_action: " + click_action);

            Intent intent1 = new Intent("notification");
            // add data
            intent1.putExtra("title", title);
            intent1.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
         //   sendNotification(title, message, click_action);
        }
    }

    private void sendPushNotification(JSONObject jsonObject) {
        messageItems = new MessageItems();
        try {
            JSONObject jsonObject1 = jsonObject.getJSONObject("asset");
            if (jsonObject1.length() > 0) {
                //  customResourceListAdapter.clear();
                messageItems.setId(jsonObject1.getString("id"));
                messageItems.setFrom(jsonObject1.getString("from"));
                messageItems.setTo(jsonObject1.getString("to"));
                messageItems.setTitle(jsonObject1.getString("title"));
                messageItems.setType(jsonObject1.getString("type"));
                messageItems.setAttachment_url(jsonObject1.getString("attachment_url"));
//                                                        messageItems.setCreated_at(appUtil.getFormattedDateTime(jsonObject1.getString("created_at")));
                messageItems.setCreated_at(jsonObject1.getString("created_at"));
                messageItems.setThumbnail_url(jsonObject1.getString("thumbnail_url"));
                messageItems.setDescription(appUtil.modifyString(jsonObject1.getString("description")));
                messageItems.setMessage_read(jsonObject1.getInt("message_read"));
                messageItems.setTextDescription(jsonObject1.getString("description_text"));

//                                                        setListAdapter();
            }
            Intent intent = new Intent(getApplicationContext(), MessageDetails.class);
            intent.putExtra("messagedetails", (Serializable) messageItems);
            intent.putExtra("comeFrom", "notify");
            appUtil.setPrefrence("comeFrom", "notify");
            appUtil.setPrefrence("unread_count", jsonObject1.getString("unreadMessages"));
            Intent in = new Intent();
            in.setAction("message.came");
            sendBroadcast(in);
            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());//displaying small notification
            mNotificationManager.showSmallNotification(messageItems.getTitle(), messageItems.getTextDescription(), intent);

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}