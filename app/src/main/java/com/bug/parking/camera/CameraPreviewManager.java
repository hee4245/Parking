package com.bug.parking.camera;

import android.content.Context;
import android.hardware.Camera;

/**
 * Created by json on 15. 9. 13..
 */
public class CameraPreviewManager {

    private Context context;
    private CameraPreview cameraPreview;
    private boolean initOn = true;

    public CameraPreviewManager(Context context) {
        this.context = context;
    }

    public void initCameraPreview(Camera camera) {
        cameraPreview = new CameraPreview(context, camera, initOn);
    }

    public CameraPreview getCameraPreview() {
        return cameraPreview;
    }

    public void destroyCameraPreview() {
        cameraPreview = null;
        initOn = true;
    }

    public void startPreview() {
        if (cameraPreview != null)
            cameraPreview.startPreview();
        else
            initOn = true;
    }

    public void stopPreview() {
        if (cameraPreview != null)
            cameraPreview.stopPreview();
        else
            initOn = false;
    }
}
