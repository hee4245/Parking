package com.bug.parking.activity;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bug.parking.R;
import com.bug.parking.camera.CameraPreview;
import com.bug.parking.camera.MyCamera;

import butterknife.*;

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;

    @Bind(R.id.cameraLayout)
    protected  RelativeLayout cameraLayout;
    @Bind(R.id.cameraPreview)
    protected  FrameLayout cameraPreviewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        camera = MyCamera.getCameraInstance();
        cameraPreview = new CameraPreview(this, camera);
        cameraPreviewLayout.addView(cameraPreview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = cameraLayout.getWidth();
                int height = width * 3 / 5;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                cameraLayout.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
