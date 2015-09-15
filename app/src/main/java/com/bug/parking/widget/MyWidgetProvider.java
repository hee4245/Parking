package com.bug.parking.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.bug.parking.R;
import com.bug.parking.data.FloorData;
import com.bug.parking.data.TimePeriodsData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by json on 15. 8. 14..
 */
public class MyWidgetProvider extends AppWidgetProvider {
    public static final String TAG = "ParkingWidget";
    private static final String ACTION_FIND_CLICK = "com.bug.parking.action.FIND_CLICK";
    private static final String ACTION_UPDATE_BY_ALARM = "com.bug.parking.action.UPDATE_BY_ALARM";
    private static final String ACTION_START_ALARM = "com.bug.parking.action.START_ALARM";
    private static final String ACTION_STOP_ALARM = "com.bug.parking.action.STOP_ALARM";
    private static final String fileName = "park.png";

    @Override
    public void onDisabled(Context context) {
        stopAlarm(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int widgetId : appWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget);
            remoteViews.setOnClickPendingIntent(R.id.widget_find, getPendingSelfIntent(context, ACTION_FIND_CLICK));

            updateWidget(context, remoteViews);

//            Intent intent = new Intent(context, SimpleWidgetProvider.class);
//            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_FIND_CLICK.equals(intent.getAction())) {
            find(context);
            stopAlarm(context);
            callOnUpdate(context);
        } else if (ACTION_UPDATE_BY_ALARM.equals(intent.getAction())) {
            callOnUpdate(context);
        } else if (ACTION_START_ALARM.equals(intent.getAction())) {
            startAlarm(context);
        } else if (ACTION_STOP_ALARM.equals(intent.getAction())) {
            stopAlarm(context);
        }
    }

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void callOnUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        // Uses getClass().getName() rather than MyWidget.class.getName() for portability into any App Widget Provider Class
        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(), getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private Bitmap getPicture(Context context) {
        String picturePath = context.getFilesDir().toString() + "/" + fileName;

        File file = new File(picturePath);
        Bitmap picture = null;

        try {
            picture = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Error get picture: " + e.getMessage());
        }

        return picture;
    }

    private void updateWidget(Context context, RemoteViews remoteViews) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean("parked", false)) {
            // picture
            Bitmap picture = getPicture(context);
            remoteViews.setImageViewBitmap(R.id.widget_picture, picture);

            // floor
            int floor = sharedPref.getInt("floor", -1);
            if (floor != -1) {
                remoteViews.setTextViewText(R.id.widget_floor, FloorData.getItem(floor));
            }

            // time
            int period = sharedPref.getInt("period", -1);
            int hour = sharedPref.getInt("hour", -1);
            int minute = sharedPref.getInt("minute", -1);
            if (period != -1 && hour != -1 && minute != -1) {
                String time = String.format("%s %02d : %02d", TimePeriodsData.getItem(period), hour+1, minute);
                remoteViews.setTextViewText(R.id.widget_time, time);
            }

            long parkingTime = sharedPref.getLong("parkingTime", 0);
            if (parkingTime != 0) {
                Date currentDate = new Date(System.currentTimeMillis());

                long timeDiff = currentDate.getTime() - parkingTime;
                long timeDiffHour = TimeUnit.MILLISECONDS.toHours(timeDiff);
                timeDiff -= TimeUnit.HOURS.toMillis(timeDiffHour);
                long timeDiffMinute = TimeUnit.MILLISECONDS.toMinutes(timeDiff);

                String timeDiffString = String.format("%02d : %02d", timeDiffHour, timeDiffMinute);
                remoteViews.setTextViewText(R.id.widget_time_diff, timeDiffString);
            }

            // memo
            String memo = sharedPref.getString("memo", "");
            if (!memo.isEmpty()) {
                remoteViews.setTextViewText(R.id.widget_memo, memo);
            }

            // find
            remoteViews.setViewVisibility(R.id.widget_find, View.VISIBLE);

        } else {
            remoteViews.setTextViewText(R.id.widget_floor, "");
            remoteViews.setTextViewText(R.id.widget_time, "");
            remoteViews.setTextViewText(R.id.widget_time_diff, "");
            remoteViews.setTextViewText(R.id.widget_memo, "");
            remoteViews.setViewVisibility(R.id.widget_find, View.INVISIBLE);
        }
    }

    private void find(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        sharedPref.edit().putBoolean("parked", false).commit();
    }

    private void startAlarm(Context context) {
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context);
        appWidgetAlarm.startAlarm();
    }

    private void stopAlarm(Context context) {
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context);
        appWidgetAlarm.stopAlarm();
    }

    public class AppWidgetAlarm
    {
        private final int ALARM_ID = 0;
        private final int INTERVAL_MILLIS = 60000;

        private Context context;

        public AppWidgetAlarm(Context context) {
            this.context = context;

        }

        public void startAlarm()
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, INTERVAL_MILLIS);

            Intent alarmIntent = new Intent(ACTION_UPDATE_BY_ALARM);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // RTC does not wake the device up
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_MILLIS, pendingIntent);
        }


        public void stopAlarm()
        {
            Intent alarmIntent = new Intent(ACTION_UPDATE_BY_ALARM);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }
}
