package com.bug.parking.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bug.parking.R;
import com.bug.parking.adapter.FloorAdapter;
import com.bug.parking.camera.CameraPreview;
import com.bug.parking.camera.MyCamera;
import com.bug.parking.data.FloorData;
import com.bug.parking.widget.MyWidgetProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import antistatic.spinnerwheel.AbstractWheel;
import butterknife.*;

public class MainActivity extends AppCompatActivity {
    private MyCamera myCamera;
    private CameraPreview cameraPreview;
    private float whratio = 3.0f / 5.0f;
    private boolean pictureTaking = false;
    private boolean pictureTaken = false;
    private boolean parked = false;

    @Bind(R.id.cameraLayout)
    protected  FrameLayout cameraLayout;
    @Bind(R.id.cameraPreview)
    protected  FrameLayout cameraPreviewLayout;
    @Bind(R.id.pictureView)
    protected ImageView pictureView;
    @Bind(R.id.floorController)
    protected AbstractWheel floorController;
    @Bind(R.id.memo)
    protected EditText memoController;
    @Bind(R.id.parking)
    protected Button parkingButton;

    public interface Callback {
        public void callback();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initFloorController();
        initAD();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        removeCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initCameraLayout();
        loadData();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//    }

    // init

    private void initFloorController() {
//        floorController.setVisibleItems(7);
        floorController.setViewAdapter(new FloorAdapter(this, FloorData.getData()));
        floorController.setCurrentItem(5);
    }

    private void initCameraLayout() {
        cameraLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = cameraLayout.getWidth();
                int height = Math.round(width * whratio);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                cameraLayout.setLayoutParams(layoutParams);
            }
        });
    }

    private void initCamera() {
        myCamera = new MyCamera(this, afterTakePicture);
        cameraPreview = new CameraPreview(this, myCamera.getCamera());
        cameraPreviewLayout.addView(cameraPreview);
    }

    private void removeCamera() {
        cameraPreviewLayout.removeView(cameraPreview);
        cameraPreview = null;
        myCamera.destoryCamera();
        myCamera = null;
    }

    private void initAD() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("F65DD339F469FA0EEC3E6EB7924A1FA9").build();
        mAdView.loadAd(adRequest);
    }

    // picture

    @OnClick(R.id.cameraButton)
    void onCameraButtonClick() {
        if (!pictureTaken) {
            if (!pictureTaking) {
                pictureTaking = true;
                myCamera.takePicture();
            }
        } else {
            resetPictureView();
        }
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

    private void resetPictureView() {
        cameraPreview.startPreview();
        pictureView.setVisibility(View.INVISIBLE);
        pictureTaking = false;
        pictureTaken = false;
    }

    // parking

    private boolean isParked() {
        return parked;
    }

    private void setParked(boolean parked) {
        // change parking button
        if (parked) {
            parkingButton.setText(getResources().getString(R.string.find));
        } else {
            parkingButton.setText(getResources().getString(R.string.parking));
        }

        updateWidget();

        this.parked = parked;
    }

    @OnClick(R.id.parking)
    void onParkingButtonClick() {
        if (!isParked()) {
            saveData();
        } else {
            clearData();
        }
    }

    // save & load

    private SharedPreferences getMyPreferences() {
        return getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
    }

    private void saveData() {
        SharedPreferences sharedPref = getMyPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();

        // picture is already saved at local storage

        // floor
        int floor = floorController.getCurrentItem();
        editor.putInt("floor", floor);

        // time

        // memo
        String memo = memoController.getText().toString();
        editor.putString("memo", memo);

        editor.putBoolean("parked", true);
        editor.commit();

        setParked(true);
    }

    private void loadData() {
        SharedPreferences sharedPref = getMyPreferences();

        if (sharedPref.getBoolean("parked", false)) {
            // picture
            Drawable picture = myCamera.getPicture();
            if (picture != null) {
                pictureView.setImageDrawable(picture);
                pictureView.setVisibility(View.VISIBLE);
                cameraPreview.stopPreview();
                pictureTaken = true;
            }

            // floor
            int floor = sharedPref.getInt("floor", -1);
            if (floor != -1) {
                floorController.setCurrentItem(floor);
            }

            // time

            // memo
            String memo = sharedPref.getString("memo", "");
            if (!memo.isEmpty()) {
                memoController.setText(memo);
            }

            setParked(true);
        } else {
            clearData();
        }
    }

    private void clearData() {
        getMyPreferences().edit().putBoolean("parked", false).commit();
        setParked(false);
        clearDataView();
    }

    private void clearDataView() {
        // picture
        resetPictureView();
        // floor to "1F"
        floorController.setCurrentItem(5);
        // time to current time
        // clear memo
        memoController.setText("");
    }

    // widget

    private void updateWidget() {
        Intent intent = new Intent(this,MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MyWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    // etc

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
