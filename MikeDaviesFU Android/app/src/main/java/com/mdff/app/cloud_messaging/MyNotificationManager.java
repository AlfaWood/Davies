package com.mdff.app.cloud_messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import com.mdff.app.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by swati gupta.
 */

public class MyNotificationManager {

    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;
    private static int currentNotificationID = 0;
    private Context mCtx;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    public void showBigNotification(String title, String message, String url, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_BIG_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(getBitmapFromURL(url));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.app_icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.app_icon))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_BIG_NOTIFICATION, notification);
    }

    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    public void showSmallNotification(String title, String message, Intent intent) {
        int requestCode = ("someString" + System.currentTimeMillis()).hashCode();
        intent.putExtra("randomRequestCode", requestCode);
        intent.setAction("DISTINCT.ACTION.HERE") ;
        intent.addCategory("DISTINCT.CATEGORY.HERE");
        intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //For Oreo and advanced version
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);


        String channelId = "M_D_F_F_ID";
        String channelName = "Channel MDFFApp";
        int importance = NotificationManager.IMPORTANCE_HIGH;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, channelId);
        Notification notification;
        mBuilder.setSmallIcon(R.drawable.app_icon_blue);
        mBuilder.setColor(mCtx.getResources().getColor(R.color.header_bg));

        notification = mBuilder
                .setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
//                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.app_icon_blue))
                .setContentText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setSmallIcon(R.drawable.app_icon)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        currentNotificationID++;
        int notificationId = currentNotificationID;
        if (notificationId == Integer.MAX_VALUE - 1)
            notificationId = 0;
//        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
        notificationManager.notify(notificationId, notification);
    }

    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
