package com.bug.parking.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by json on 15. 8. 1..
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";

    private Camera camera;
    private boolean initOn = true;

    public CameraPreview(Context context, Camera camera, boolean initOn) {
        super(context);

        this.camera = camera;
        this.initOn = initOn;

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    private void initPreview(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void startPreview() {
        try {
            camera.stopPreview();
        } catch (Exception e) {

        }

        try {
            camera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void stopPreview() {
        camera.stopPreview();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        initPreview(holder);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (holder.getSurface() == null) {
            return;
        }

        if (initOn)
            startPreview();
    }
}
