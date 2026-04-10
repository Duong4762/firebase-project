package com.example.class_ex;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MovieTicketApp extends Application {

    public static final String CHANNEL_REMINDER = "movie_reminders";
    public static final String CHANNEL_BOOKING = "movie_booking";
    public static final String CHANNEL_FCM = "fcm_default";

    @Override
    public void onCreate() {
        super.onCreate();
        createChannels();
    }

    private void createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm == null) {
            return;
        }
        NotificationChannel reminder = new NotificationChannel(
                CHANNEL_REMINDER,
                getString(R.string.channel_reminder_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        reminder.setDescription(getString(R.string.channel_reminder_desc));
        nm.createNotificationChannel(reminder);

        NotificationChannel booking = new NotificationChannel(
                CHANNEL_BOOKING,
                getString(R.string.channel_booking_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        booking.setDescription(getString(R.string.channel_booking_desc));
        nm.createNotificationChannel(booking);

        NotificationChannel fcm = new NotificationChannel(
                CHANNEL_FCM,
                getString(R.string.channel_fcm_name),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        nm.createNotificationChannel(fcm);
    }
}
