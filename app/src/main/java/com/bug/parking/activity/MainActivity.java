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
import com.bug.parking.widget.MyWidgetProvider;

import antistatic.spinnerwheel.AbstractWheel;
import butterknife.*;

public class MainActivity extends AppCompatActivity {
    private MyCamera myCamera;
    private CameraPreview cameraPreview;
    private boolean pictureTaking = false;
    private boolean pictureTaken = false;
    private boolean parked = false;

    String[] floors = new String[]{"B5", "B4", "B3", "B2", "B1", "1F", "2F", "3F", "4F", "5F"};

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

    }

    @Override
    protected void onStart() {
        super.onStart();

        initCamera();
        initFloorController();
    }

    @Override
    protected void onStop() {
        super.onStop();

        cameraPreviewLayout.removeView(cameraPreview);
        cameraPreview = null;
        myCamera.destoryCamera();
        myCamera = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();

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

    // init

    private void initCamera() {
        myCamera = new MyCamera(this, afterTakePicture);
        cameraPreview = new CameraPreview(this,  myCamera.getCamera());
        cameraPreviewLayout.addView(cameraPreview);
    }

    private void initFloorController() {
//        floorController.setVisibleItems(7);
        floorController.setViewAdapter(new FloorAdapter(this, floors));
        floorController.setCurrentItem(5);
    }

    private boolean isParked() {
        return parked;
    }

    private void setParked(boolean parked) {
        // change parking button
        if (parked) {
            parkingButton.setText("Find");
        } else {
            parkingButton.setText("Parking");
        }

        updateWidget();

        this.parked = parked;
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

    private void resetPictureView() {
        cameraPreview.startPreview();
        pictureView.setVisibility(View.INVISIBLE);
        pictureTaking = false;
        pictureTaken = false;
    }

    // save & load

    @OnClick(R.id.parking)
    void onParkingButtonClick() {
        if (!isParked()) {
            saveData();
        } else {
            clearData();
        }
    }

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
        }
    }

    private void clearData() {
        getMyPreferences().edit().putBoolean("parked", false).commit();
        setParked(false);

        // picture
        resetPictureView();

        floorController.setCurrentItem(5);
        // time to current time
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
