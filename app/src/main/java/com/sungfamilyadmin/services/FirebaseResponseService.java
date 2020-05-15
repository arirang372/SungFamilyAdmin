package com.sungfamilyadmin.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.GsonBuilder;
import com.sungfamilyadmin.ItemListActivity;
import com.sungfamilyadmin.R;
import com.sungfamilyadmin.SungFamilyAdminApp;
import com.sungfamilyadmin.constants.Constant;
import com.sungfamilyadmin.events.ChatMessageArrivedEvent;
import com.sungfamilyadmin.events.ChatMessageReceivedEvent;
import com.sungfamilyadmin.events.ReviewReceivedEvent;
import com.sungfamilyadmin.models.Admin;
import com.sungfamilyadmin.models.ChatMessage;
import com.sungfamilyadmin.models.Review;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class FirebaseResponseService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Map<String,String> data = remoteMessage.getData();
        if (data.containsKey("message"))
        {
            String action = data.get("action");
            if(action.equals("write_review"))
            {
                String message = data.get("message");
                Review review = new GsonBuilder().create().fromJson(message, Review.class);
                if(review != null)
                {
                    Admin.getInstance().addReview(review);
                    EventBus.getDefault().post(new ReviewReceivedEvent(review));
                }
            }
            else
            {
                String message = data.get("message");
                if(message.equals("open_app"))
                {
                    //open a dialog...
                    notifyUserWithAction();
                }
                else
                {
                    notifyUser(message);
                    ChatMessage chatMessage = new ChatMessage(message, false, false);
                    chatMessage.setRecipientToken(Constant.RECIPIENT_FB_TOKEN);

                    String currentScreen = SungFamilyAdminApp.getApp().getCurrentScreen();
                    if(currentScreen.equals("ItemListActivity"))
                    {
                        EventBus.getDefault().post(new ChatMessageArrivedEvent(chatMessage));
                    }
                    else if(currentScreen.equals("ChatActivity"))
                    {
                        EventBus.getDefault().post(new ChatMessageReceivedEvent(chatMessage));
                    }
                }
            }
        }
    }

    private void notifyUser(String messageBody)
    {
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("MY_NOTIFICATION_CHANNEL_ID", "Notification Channel",
                    NotificationManager.IMPORTANCE_MAX);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setWhen(System.currentTimeMillis());
            notificationBuilder.setChannelId("MY_NOTIFICATION_CHANNEL_ID");
        }

        notificationManager.notify(9999, notificationBuilder.build());

    }

    private void notifyUserWithAction()
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getResponseIntent(),
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("John Sung is calling you, would you like to open the app?")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("MY_NOTIFICATION_CHANNEL_ID", "Notification Channel",
                    NotificationManager.IMPORTANCE_MAX);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setWhen(System.currentTimeMillis());
            notificationBuilder.setChannelId("MY_NOTIFICATION_CHANNEL_ID");
        }

        notificationManager.notify(9999, notificationBuilder.build());

    }

    private Intent getResponseIntent()
    {
        Intent intent = new Intent(this, ItemListActivity.class);
        return intent;
    }

}
