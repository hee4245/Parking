package com.bug.parking.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
            remoteViews.setImageViewBitmap(R.id.widget_picture, picture);

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

        File file =new File(picturePath);
        Bitmap picture = null;

        try {
            picture = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Error get picture: " + e.getMessage());
        }

        return picture;
    }

}
