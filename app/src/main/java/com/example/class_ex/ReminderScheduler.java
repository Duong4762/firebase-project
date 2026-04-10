package com.example.class_ex;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public final class ReminderScheduler {

    private static final long REMINDER_BEFORE_MS = 60L * 60 * 1000;

    private ReminderScheduler() {}

    public static void scheduleShowReminder(
            @NonNull Context context,
            @NonNull String ticketId,
            @NonNull String movieTitle,
            long showTimeMillis
    ) {
        long fireAt = showTimeMillis - REMINDER_BEFORE_MS;
        long delay = fireAt - System.currentTimeMillis();
        if (delay < TimeUnit.MINUTES.toMillis(1)) {
            delay = TimeUnit.MINUTES.toMillis(1);
        }
        Data input = new Data.Builder()
                .putString(ShowReminderWorker.KEY_MOVIE_TITLE, movieTitle)
                .putLong(ShowReminderWorker.KEY_SHOW_TIME_MILLIS, showTimeMillis)
                .putString(ShowReminderWorker.KEY_TICKET_ID, ticketId)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(ShowReminderWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(input)
                .addTag("reminder_" + ticketId)
                .build();

        WorkManager.getInstance(context).enqueue(req);
    }
}
