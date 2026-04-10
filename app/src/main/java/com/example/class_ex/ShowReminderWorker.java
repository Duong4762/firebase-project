package com.example.class_ex;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowReminderWorker extends Worker {

    public static final String KEY_MOVIE_TITLE = "movieTitle";
    public static final String KEY_SHOW_TIME_MILLIS = "showTimeMillis";
    public static final String KEY_TICKET_ID = "ticketId";

    public ShowReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString(KEY_MOVIE_TITLE);
        long showTime = getInputData().getLong(KEY_SHOW_TIME_MILLIS, 0L);
        String ticketId = getInputData().getString(KEY_TICKET_ID);

        Context ctx = getApplicationContext();
        Intent open = new Intent(ctx, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int req = ticketId != null ? ticketId.hashCode() : (int) (showTime % Integer.MAX_VALUE);
        PendingIntent pi = PendingIntent.getActivity(
                ctx,
                req,
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String when = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(showTime));

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, MovieTicketApp.CHANNEL_REMINDER)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(ctx.getString(R.string.notif_reminder_title))
                .setContentText(ctx.getString(R.string.notif_reminder_body, title != null ? title : "", when))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pi);

        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(req, b.build());
        }
        return Result.success();
    }
}
