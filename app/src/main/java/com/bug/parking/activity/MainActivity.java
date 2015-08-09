package com.bug.parking.activity;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bug.parking.R;
import com.bug.parking.camera.CameraPreview;
import com.bug.parking.camera.MyCamera;

import butterknife.*;

public class MainActivity extends AppCompatActivity {
    private MyCamera myCamera;
    private CameraPreview cameraPreview;
    private boolean pictureTaking = false;
    private boolean pictureTaken = false;

    @Bind(R.id.cameraLayout)
    protected  FrameLayout cameraLayout;
    @Bind(R.id.cameraPreview)
    protected  FrameLayout cameraPreviewLayout;
    @Bind(R.id.pictureView)
    protected ImageView pictureView;

    public interface Callback {
        public void callback();
    }
    public Callback afterTakePicture = new Callback() {
        @Override
        public void callback() {
            Drawable picture = myCamera.getPicture();
            pictureView.setImageDrawable(picture);
            pictureView.setVisibility(View.VISIBLE);
            cameraPreview.stopPreview();
            pictureTaking = false;
            pictureTaken = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        myCamera = new MyCamera(this, afterTakePicture);
        cameraPreview = new CameraPreview(this,  myCamera.getCamera());
        cameraPreviewLayout.addView(cameraPreview);
    }

    @Override
    protected void onStop() {
        super.onStop();

        myCamera = null;
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

//    @Override
//    protected void onPause() {
//        super.onPause();
//    }

    @OnClick(R.id.cameraButton) void onCameraButtonClick() {
        if (!pictureTaken) {
            if (!pictureTaking) {
                pictureTaking = true;
                myCamera.takePicture();
            }
        } else {
            resetPictureView();
        }
    }

    private void resetPictureView() {
        cameraPreview.startPreview();
        pictureView.setVisibility(View.INVISIBLE);
        pictureTaking = false;
        pictureTaken = false;
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
