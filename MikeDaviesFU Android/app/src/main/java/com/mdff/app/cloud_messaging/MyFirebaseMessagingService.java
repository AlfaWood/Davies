package com.mdff.app.cloud_messaging;

import android.content.Intent;
import android.util.Log;

import com.mdff.app.activity.MessageDetails;
import com.mdff.app.model.MessageItems;
import com.mdff.app.utility.AppUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Swati.Gupta on 6/19/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    AppUtil appUtil;
    MessageItems messageItems;
    String str_RemoteData;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("------------notification message------//" +remoteMessage.toString());
        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
        appUtil = new AppUtil(this);
        if (remoteMessage.getData().size() > 0) {
            System.out.println("------------notification message1------//" + remoteMessage.getData());
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                System.out.print(e);
            }
        }else{
            System.out.println("------------notification message2------//" + remoteMessage.getData());
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

}
