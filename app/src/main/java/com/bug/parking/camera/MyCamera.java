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
import android.util.Log;

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
    private int statusBarHeight;
    private PointF scaleRatio = new PointF();
    private Camera.PictureCallback pictureCallback;
    private MainActivity.Callback afterTakePicture;
    private float whRatio;

    public MyCamera(Context context, MainActivity.Callback afterTakePicture, float whRatio, int statusBarHeight) {
        try {
            this.context = context;
            this.afterTakePicture = afterTakePicture;
            this.whRatio = whRatio;
            this.statusBarHeight = statusBarHeight;

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

        pictureSize = camera.new Size(0, 0);
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        for (Camera.Size size : sizes) {
            if (size.height <= displaySize.x && size.height > pictureSize.height && size.width > pictureSize.width) {
                pictureSize = size;
            }
        }
        if (pictureSize.width <= 0 || pictureSize.height <= 0) {
            pictureSize = sizes.get(0);
        }
        params.setPictureSize(pictureSize.width, pictureSize.height);

        camera.setParameters(params);

        scaleRatio.x = (float)pictureSize.height / (float)(displaySize.x);
        scaleRatio.y = (float)pictureSize.width / (float)(displaySize.y - statusBarHeight);
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

            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;

            Bitmap sampledBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            Matrix matrix = new Matrix();
            matrix.setScale(1.0f / scaleRatio.y, 1.0f / scaleRatio.x);
            matrix.postRotate(90.0f);

            int actionBarHeight = ((MainActivity)context).getSupportActionBar().getHeight();
            int picturePosX = Math.round(scaleRatio.y * (actionBarHeight));
            int picturePosY = 0;
            int pictureWidth = Math.round(scaleRatio.y * sampledBitmap.getHeight() * whRatio);
            int pictureHeight = Math.round(scaleRatio.x * sampledBitmap.getHeight());

            Bitmap croppedBitmap = Bitmap.createBitmap(sampledBitmap, picturePosX, picturePosY, pictureWidth, pictureHeight, matrix, true);
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
