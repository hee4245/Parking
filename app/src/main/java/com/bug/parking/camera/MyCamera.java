package com.bug.parking.camera;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by json on 15. 8. 1..
 */
public class MyCamera {
    private static final String TAG = "MyCamera";

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
            c.setDisplayOrientation(90);
        } catch (Exception e) {
            Log.d(TAG, "Error get camera: " + e.getMessage());
        }
        return c;
    }


}
