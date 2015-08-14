package com.bug.parking.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

import com.bug.parking.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by json on 15. 8. 14..
 */
public class MyWidgetProvider extends AppWidgetProvider {
    public static final String TAG = "ParkingWidget";
    private static final String fileName = "park.png";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        Bitmap picture = getPicture(context);

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget);

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

    private Bitmap getPicture(Context context) {
        String picturePath = "" + context.getExternalFilesDir(null).toString() + "/" + fileName;

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
                remoteViews.setTextViewText(R.id.widget_floor, "" + floor);
            }

            // time
            remoteViews.setTextViewText(R.id.widget_time, "PM 10:32");

            // memo
            String memo = sharedPref.getString("memo", "");
            if (!memo.isEmpty()) {
                remoteViews.setTextViewText(R.id.widget_memo, memo);
            }
        } else {
            remoteViews.setTextViewText(R.id.widget_floor, "");
            remoteViews.setTextViewText(R.id.widget_time, "");
            remoteViews.setTextViewText(R.id.widget_memo, "");
        }
    }

}
