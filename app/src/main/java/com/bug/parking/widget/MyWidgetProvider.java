package com.bug.parking.widget;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by json on 15. 8. 14..
 */
public class MyWidgetProvider extends AppWidgetProvider {
    public static final String TAG = "ParkingWidget";
    private static final String ACTION_FIND_CLICK = "com.bug.parking.action.FIND_CLICK";
    private static final String fileName = "park.png";

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

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
            callOnUpdate(context);
        }
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
                remoteViews.setTextViewText(R.id.widget_floor, "" + FloorData.getItem(floor));
            }

            // time
            remoteViews.setTextViewText(R.id.widget_time, "PM 10:32");

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
            remoteViews.setTextViewText(R.id.widget_memo, "");
            remoteViews.setViewVisibility(R.id.widget_find, View.INVISIBLE);
        }
    }

    private void find(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        sharedPref.edit().putBoolean("parked", false).commit();
    }
}
