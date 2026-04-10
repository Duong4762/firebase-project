package com.example.class_ex;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TicketNotifications {

    private TicketNotifications() {}

    /** Thông báo ngay khi đặt vé (local notification, giống push trên máy). */
    public static void showBookingConfirmed(
            @NonNull Context context,
            @NonNull String ticketId,
            @NonNull String movieTitle,
            @NonNull String theaterName,
            int seatCount,
            long showTimeMillis
    ) {
        int nid = Math.abs(("booking" + ticketId).hashCode());

        Intent open = new Intent(context, MyTicketsActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(
                context,
                nid,
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String when = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(showTimeMillis));
        String text = context.getString(
                R.string.notif_booking_text,
                movieTitle,
                theaterName,
                seatCount,
                when
        );

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, MovieTicketApp.CHANNEL_BOOKING)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(context.getString(R.string.notif_booking_title))
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pi);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(nid, b.build());
        }
    }
}
