package com.trignsoft.propertybuilder.NotificationUtils;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.trignsoft.propertybuilder.HomeActivity;
import com.trignsoft.propertybuilder.InspectionRequestActivity;
import com.trignsoft.propertybuilder.Models.UserModel;
import com.example.propertybuilder.R;
import com.trignsoft.propertybuilder.SharedPreference.SharedPrefManager;
import com.trignsoft.propertybuilder.utils.AppointmentActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String ADMIN_CHANNEL_ID ="admin_channel";
    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {

        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        String typeCustomer = usersModel.getTypeUser();
        String user = remoteMessage.getData().get("topic");

        if (typeCustomer.equals(user)){
            Log.i(TAG, "onMessageReceived: "+remoteMessage);
            final Intent intent = new Intent(this, HomeActivity.class);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationID = new Random().nextInt(3000);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels(notificationManager);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);

            Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("message"))
                    .setAutoCancel(true)
                    .setSound(notificationSoundUri)
                    .setContentIntent(pendingIntent);

            //Set notification color to match your app color template
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                notificationBuilder.setColor(getResources().getColor(R.color.primaryColor));
            }
            notificationManager.notify(notificationID, notificationBuilder.build());
        }else {
            String topic = remoteMessage.getData().get("topic");
            if (topic!=null){
                if (topic.equals("booking")){
                    Log.i(TAG, "onMessageReceived: "+remoteMessage);
                    final Intent intent = new Intent(this, InspectionRequestActivity.class);
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    int notificationID = new Random().nextInt(3000);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setupChannels(notificationManager);
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher);

                    Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(largeIcon)
                            .setContentTitle(remoteMessage.getData().get("title"))
                            .setContentText(remoteMessage.getData().get("message"))
                            .setAutoCancel(true)
                            .setSound(notificationSoundUri)
                            .setContentIntent(pendingIntent);

                    //Set notification color to match your app color template
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        notificationBuilder.setColor(getResources().getColor(R.color.primaryColor));
                    }
                    notificationManager.notify(notificationID, notificationBuilder.build());
            }else if (topic.equals("approve")||topic.equals("edited")){
                    Log.i(TAG, "onMessageReceived: "+remoteMessage);
                    final Intent intent = new Intent(this, AppointmentActivity.class);
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    int notificationID = new Random().nextInt(3000);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setupChannels(notificationManager);
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher);

                    Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(largeIcon)
                            .setContentTitle(remoteMessage.getData().get("title"))
                            .setContentText(remoteMessage.getData().get("message"))
                            .setAutoCancel(true)
                            .setSound(notificationSoundUri)
                            .setContentIntent(pendingIntent);

                    //Set notification color to match your app color template
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        notificationBuilder.setColor(getResources().getColor(R.color.primaryColor));
                    }
                    notificationManager.notify(notificationID, notificationBuilder.build());
            }

            }
        }



        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to devie notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}