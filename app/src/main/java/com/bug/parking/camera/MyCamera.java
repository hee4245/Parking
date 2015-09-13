package com.bug.parking.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

import com.bug.parking.activity.MainActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by json on 15. 8. 1..
 */
public class MyCamera {
    private static final String TAG = "MyCamera";
    private static final String fileName = "park.png";

    private Context context;
    private Camera camera;
    private Camera.PictureCallback pictureCallback;
    private MainActivity.Callback afterTakePicture;
    private float whRatio;

    public MyCamera(Context context, MainActivity.Callback afterTakePicture, float whRatio) {
        try {
            this.context = context;
            this.afterTakePicture = afterTakePicture;
            this.whRatio = whRatio;

            initCamera();
            initPicktureCallback();
        } catch (Exception e) {
            Log.d(TAG, "Error init camera: " + e.getMessage());
        }
    }

    private void initCamera() {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(90);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);
    }

    public void destoryCamera() {
        camera.release();
    }

    private void initPicktureCallback() {
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

            Point displaySize = new Point();
            ((Activity) context).getWindowManager().getDefaultDisplay().getRealSize(displaySize);
            int displayWidth = displaySize.x;
            int displayHeight = displaySize.y;

            int inSampleSize = 1;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(data, 0, data.length, options);
            if (options.outWidth > 0 || options.outHeight > 0) {

                float scaleWidth = (float) options.outHeight / (float) displayWidth;
                float scaleHeight = (float) options.outWidth / (float) displayHeight;
                float scale = Math.min(scaleWidth, scaleHeight);

                while (inSampleSize * 2 <= scale)
                    inSampleSize *= 2;
            }

            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;

            Bitmap sampledBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            Matrix matrix = new Matrix();
            matrix.setRotate(90.0f);

            Bitmap croppedBitmap = Bitmap.createBitmap(sampledBitmap, 0, 0, Math.round(sampledBitmap.getHeight() * whRatio), sampledBitmap.getHeight(), matrix, true);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);

            fos.close();

            afterTakePicture.callback();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
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
