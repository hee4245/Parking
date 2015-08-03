package com.bug.parking.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by json on 15. 8. 1..
 */
public class MyCamera {
    private static final String TAG = "MyCamera";
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String fileName = "park.png";

    private Context context;
    private Camera camera;
    private Camera.PictureCallback pictureCallback;

    public MyCamera(Context context) {
        try {
            this.context = context;

            initCamera();
            initPicktureCallback();
        } catch (Exception e) {
            Log.d(TAG, "Error init camera: " + e.getMessage());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        camera.release();
    }

    private void initCamera() {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(90);
        camera.setParameters(params);
    }

    private void initPicktureCallback() {
        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File file = new File(context.getExternalFilesDir(null), fileName);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);

//                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                    Bitmap cropedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
//                    cropedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                    fos.write(data);
                    fos.flush();
                    fos.close();

                    Toast.makeText(context, "Take Picture!", Toast.LENGTH_LONG);
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
            }
        };
    }

    public Camera getCamera() {
        return camera;
    }

    public void takePicture() {
        camera.takePicture(null, null, pictureCallback);
    }
}
