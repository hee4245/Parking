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

    private SurfaceHolder surfaceHolder;
    private Camera camera;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        this.camera = camera;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (holder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {

        }

        try {
            Camera.Parameters params = camera.getParameters();
            Log.d(TAG, params.getPreviewSize().toString());
            //params.setPreviewSize(500, 300);
            camera.setParameters(params);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
