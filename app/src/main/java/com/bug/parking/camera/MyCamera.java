package com.bug.parking.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.Toast;

import com.bug.parking.activity.MainActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by json on 15. 8. 1..
 */
public class MyCamera {
    private static final String TAG = "MyCamera";
    private static final String fileName = "park.png";

    private Context context;
    private Camera camera;
    private Camera.Size pictureSize;
    private Point displaySize = new Point();
    private int statusBarHeight = 0;
    private PointF scaleRatio = new PointF();
    private Camera.PictureCallback pictureCallback;
    private MainActivity.Callback afterTakePicture;
    private float whRatio;

    public MyCamera(Context context, MainActivity.Callback afterTakePicture, float whRatio) {
        try {
            this.context = context;
            this.afterTakePicture = afterTakePicture;
            this.whRatio = whRatio;

//            initCamera();
            initPictureCallback();
        } catch (Exception e) {
            Log.d(TAG, "Error init camera: " + e.getMessage());
        }
    }

    public void initCamera() {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(90);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        ((Activity) context).getWindowManager().getDefaultDisplay().getRealSize(displaySize);
        statusBarHeight = Math.round(25 * context.getResources().getDisplayMetrics().density);

        pictureSize = camera.new Size(Integer.MAX_VALUE, Integer.MAX_VALUE);
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        for (Camera.Size size : sizes) {
            if (size.height >= displaySize.x && size.width <= pictureSize.width && size.height <= pictureSize.height) {
                pictureSize = size;
            }
        }
        if (pictureSize.width > 0 && pictureSize.height > 0) {
            params.setPictureSize(pictureSize.width, pictureSize.height);
        }

        camera.setParameters(params);

        scaleRatio.x = (float)(displaySize.x) / (float)pictureSize.height;
        scaleRatio.y = (float)(displaySize.y - statusBarHeight) / (float)pictureSize.width;
    }

    public void destroyCamera() {
        camera.release();
        camera = null;
    }

    private void initPictureCallback() {
        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Handler handler = new Handler();
                handler.post(new SavePhotoRunnable(data));
            }
        };
    }

    private class SavePhotoRunnable implements Runnable {
        byte[] data;

        public SavePhotoRunnable(byte[] data) {
            this.data = data;
        }

        public void run() {
            savePhoto(data);
        }
    }

    private void savePhoto(byte[] data) {

        FileOutputStream fos;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            int inSampleSize = calculateInSampleSize(data);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
//            options.inJustDecodeBounds = false;

            Bitmap sampledBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            Matrix matrix = new Matrix();
            matrix.setRotate(90.0f);
            matrix.postScale(scaleRatio.x / (float) inSampleSize, scaleRatio.y / (float) inSampleSize);

            int actionBarHeight = 0;
            ActionBar actionBar = ((MainActivity) context).getSupportActionBar();
            if (actionBar != null)
                actionBarHeight = actionBar.getHeight();

            int picturePosX = Math.round((float)actionBarHeight / scaleRatio.y);
            int picturePosY = 0;
            int pictureWidth = Math.round(sampledBitmap.getHeight() * whRatio / scaleRatio.y);
            int pictureHeight = Math.round(sampledBitmap.getHeight() / scaleRatio.x);

            Bitmap croppedBitmap = Bitmap.createBitmap(sampledBitmap, picturePosX, picturePosY, pictureWidth, pictureHeight, matrix, true);
            sampledBitmap = null;
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            croppedBitmap = null;

            fos.close();

            afterTakePicture.callback();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Error accessing file: " + e.getMessage());
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private int calculateInSampleSize(byte[] data) {
        int inSampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        if (options.outWidth > 0 || options.outHeight > 0) {

            float scaleWidth = (float) options.outHeight / (float) displaySize.x;
            float scaleHeight = (float) options.outWidth / (float) displaySize.y;
            float scale = Math.min(scaleWidth, scaleHeight);

            while (inSampleSize * 2 <= scale)
                inSampleSize *= 2;
        }
        return inSampleSize;
    }

    private String getPicturePath() {
//        return "" + context.getExternalFilesDir(null).toString() + "/" + fileName;
        return context.getFilesDir().toString() + "/" + fileName;
    }

    public Camera getCamera() {
        return camera;
    }

    public Drawable getPicture() {
        return Drawable.createFromPath(getPicturePath());
    }

    public void takePicture() {
        camera.takePicture(null, null, pictureCallback);
    }
}
