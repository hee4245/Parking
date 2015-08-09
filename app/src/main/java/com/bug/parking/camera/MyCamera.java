package com.bug.parking.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

import com.bug.parking.activity.MainActivity;

import java.io.File;
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

    public MyCamera(Context context, MainActivity.Callback afterTakePicture) {
        try {
            this.context = context;
            this.afterTakePicture = afterTakePicture;

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
                File file = new File(context.getExternalFilesDir(null), fileName);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);

                    Matrix matrix = new Matrix();
                    matrix.setRotate(90.0f);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap cropedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getHeight() * 3 / 5, bitmap.getHeight(), matrix, true);
                    cropedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

//                    fos.write(data);
//                    fos.flush();
                    fos.close();

                    afterTakePicture.callback();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
            }
        };
    }

    private String getPicturePath() {
        return "" + context.getExternalFilesDir(null).toString() + "/" + fileName;
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
