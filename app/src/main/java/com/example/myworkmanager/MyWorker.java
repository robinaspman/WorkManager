package com.example.myworkmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    public static final String TAG = "TAG";
    public static final String DATA_KEY = "data_key";
    public static final String MESSAGE_CHANNEL = "message_channel";
    public static final String TASK_NOTIFICATION = "task_notification";
    public static final int MESSAGE_ID = 1001;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "dDoing great work!");
        Data data = new Data.Builder()
                .putString(DATA_KEY, "Hello from doneWork!")
                .build();
        String response = getInputData().getString(MyWorker.DATA_KEY);
        Log.i(TAG, "Received: " + response);

        displayNotification(response, data.getString(MyWorker.DATA_KEY));

        return Result.success(data);
    }

    private void displayNotification(String title, String message) {

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MESSAGE_CHANNEL,
                    TASK_NOTIFICATION, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(getApplicationContext(), ShowDetails.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, notificationIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(
                getApplicationContext(),
                MESSAGE_CHANNEL)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.teal_200))
                .setAutoCancel(true);
        notificationManager.notify(MESSAGE_ID, notification.build());

    }
}
