package com.bug.parking.activity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bug.parking.R;
import com.bug.parking.adapter.TextAdapter;
import com.bug.parking.adapter.TimeAdapter;
import com.bug.parking.camera.CameraPreviewManager;
import com.bug.parking.camera.MyCamera;
import com.bug.parking.data.FloorData;
import com.bug.parking.data.TimePeriodsData;
import com.bug.parking.fragment.SettingFragment;
import com.bug.parking.manager.ThemeManager;
import com.bug.parking.widget.MyWidgetProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import antistatic.spinnerwheel.AbstractWheel;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(View view, Boolean value, int index) {
            view.setEnabled(value);
        }
    };
    private static final String ACTION_START_ALARM = "com.bug.parking.action.START_ALARM";
    private static final String ACTION_STOP_ALARM = "com.bug.parking.action.STOP_ALARM";
    @Bind(R.id.cameraLayout)
    protected FrameLayout cameraLayout;
    @Bind(R.id.cameraPreview)
    protected FrameLayout cameraPreviewLayout;
    @Bind(R.id.pictureView)
    protected ImageView pictureView;
    @Bind(R.id.floorController)
    protected AbstractWheel floorController;
    @Bind(R.id.floor_indicator)
    protected View floorIndicator;
    @Bind(R.id.memo)
    protected EditText memoController;
    @Bind(R.id.parking)
    protected Button parkingButton;
    @Bind(R.id.time_hour)
    protected AbstractWheel timeHourController;
    @Bind(R.id.time_minute)
    protected AbstractWheel timeMinuteController;
    @Bind(R.id.time_periods)
    protected AbstractWheel timePeriodsController;
    @Bind(R.id.adView)
    protected AdView adView;
    @Bind({R.id.floorController, R.id.time_periods, R.id.time_hour, R.id.time_minute, R.id.memo, R.id.leftAngle, R.id.rightAngle})
    List<View> dataControllers;
    private MyCamera myCamera;
    private CameraPreviewManager cameraPreviewManager;
    private float whRatio = 2.0f / 3.0f;
    private boolean pictureTaking = false;
    private boolean pictureTaken = false;
    public Callback afterTakePicture = new Callback() {
        @Override
        public void callback() {
            Drawable picture = myCamera.getPicture();
            pictureView.setImageDrawable(picture);
            pictureView.setVisibility(View.VISIBLE);
            cameraPreviewManager.stopPreview();
            pictureTaking = false;
            pictureTaken = true;
        }
    };
    private boolean parked = false;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initActionBar();
        initTheme();
        initAD();
        initCamera();
        initCameraLayout();
        initFloorController();
        initTimeController();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        turnOnCamera();
        reloadData();
    }

    // init

    @Override
    protected void onStop() {
        super.onStop();

        turnOffCamera();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setCustomView(R.layout.custom_actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ImageButton settingButton = (ImageButton) actionBar.getCustomView().findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingFragment();
            }
        });
    }

    private void initCameraLayout() {
        cameraLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = cameraLayout.getWidth();
                int height = Math.round(width * whRatio);
                ViewGroup.LayoutParams layoutParams = cameraLayout.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                cameraLayout.setLayoutParams(layoutParams);
            }
        });
    }

    private void initCamera() {
        myCamera = new MyCamera(this, afterTakePicture, whRatio);
        cameraPreviewManager = new CameraPreviewManager(this);
    }

    private void turnOnCamera() {
        myCamera.initCamera();
        cameraPreviewManager.initCameraPreview(myCamera.getCamera());
        cameraPreviewLayout.addView(cameraPreviewManager.getCameraPreview());
    }

    private void turnOffCamera() {
        cameraPreviewLayout.removeView(cameraPreviewManager.getCameraPreview());
        cameraPreviewManager.destroyCameraPreview();
        myCamera.destroyCamera();
    }

    private void initTheme() {
        themeManager = new ThemeManager(this);
        applyTheme();
    }

    public void applyTheme() {
        SharedPreferences sharedPref = getMyPreferences();
        int themeIndex = sharedPref.getInt("theme", 0);
        themeManager.setCurrentThemeIndex(themeIndex);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(themeManager.getCurrentPrimaryColor()));
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(themeManager.getCurrentPrimaryDarkColor());
        }
        floorIndicator.setBackgroundColor(themeManager.getCurrentPrimaryColor());
        parkingButton.setBackgroundResource(themeManager.getCurrentButtonStyleResource());
    }

    private void initAD() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("F65DD339F469FA0EEC3E6EB7924A1FA9").build();
        adView.loadAd(adRequest);
    }

    private void initFloorController() {
        floorController.setViewAdapter(new TextAdapter(this, FloorData.getData(), R.layout.floor_item, R.id.floorText, Typeface.BOLD));
        floorController.setFrictionFactor(7.0f);
        floorController.setCurrentItem(5);
    }

    private void initTimeController() {
        timePeriodsController.setViewAdapter(new TextAdapter(this, TimePeriodsData.getData(), R.layout.time_item, R.id.timeText));
        timePeriodsController.setFrictionFactor(5.0f);
        timePeriodsController.setVisibleItems(0);
        timeHourController.setViewAdapter(new TimeAdapter(this, 1, 12));
        timeHourController.setFrictionFactor(5.0f);
        timeHourController.setVisibleItems(0);

        timeMinuteController.setViewAdapter(new TimeAdapter(this, 0, 59, "%02d"));
        timeMinuteController.setFrictionFactor(5.0f);
        timeMinuteController.setVisibleItems(0);
        timeMinuteController.setCyclic(true);
    }

    // picture

    @OnClick(R.id.cameraButton)
    void onCameraButtonClick() {
        if (parked)
            return;

        if (!pictureTaken) {
            if (!pictureTaking) {
                pictureTaking = true;
                myCamera.takePicture();
                startWidgetAlarm();
            }
        } else {
            resetPictureView();
            stopWidgetAlarm();
        }
    }

    private void resetPictureView() {
        cameraPreviewManager.startPreview();
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

        setControllersEnabled(!parked);
        updateWidget();

        this.parked = parked;
    }

    @OnClick(R.id.parking)
    void onParkingButtonClick() {
        if (!isParked()) {
            if (!pictureTaken) {
                Toast.makeText(this, "Please take a picture", Toast.LENGTH_SHORT).show();
                return;
            }
            saveData();
        } else {
            clearData();
        }
    }

    // save & load

    public SharedPreferences getMyPreferences() {
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
        int hour = timeHourController.getCurrentItem();
        int minute = timeMinuteController.getCurrentItem();
        int period = timePeriodsController.getCurrentItem();
        editor.putInt("hour", hour);
        editor.putInt("minute", minute);
        editor.putInt("period", period);

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.AM_PM, period);
        cal.set(Calendar.HOUR, hour == 11 ? 0 : hour + 1);
        cal.set(Calendar.MINUTE, minute);
        Date parkingDate = cal.getTime();
        editor.putLong("parkingTime", parkingDate.getTime());

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
                cameraPreviewManager.stopPreview();
                pictureTaken = true;
            }

            // floor
            int floor = sharedPref.getInt("floor", -1);
            if (floor != -1) {
                floorController.setCurrentItem(floor);
            }

            // time
            int period = sharedPref.getInt("period", -1);
            int hour = sharedPref.getInt("hour", -1);
            int minute = sharedPref.getInt("minute", -1);
            if (period != -1) {
                timePeriodsController.setCurrentItem(period);
            }
            if (hour != -1) {
                timeHourController.setCurrentItem(hour);
            }
            if (minute != -1) {
                timeMinuteController.setCurrentItem(minute);
            }

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

    private void reloadData() {
        SharedPreferences sharedPref = getMyPreferences();

        if (sharedPref.getBoolean("parked", false) != parked)
            loadData();
    }

    private void clearData() {
        getMyPreferences().edit().putBoolean("parked", false).commit();
        setParked(false);
        clearDataView();
        setControllersEnabled(true);
    }

    private void clearDataView() {
        // picture
        resetPictureView();
        // floor to "1F"
        floorController.setCurrentItem(5);

        // time to current time
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int hourIndex = calendar.get(Calendar.HOUR) == 0 ? 11 : calendar.get(Calendar.HOUR) - 1;
        timeHourController.setCurrentItem(hourIndex);
        timeMinuteController.setCurrentItem(calendar.get(Calendar.MINUTE));
        timePeriodsController.setCurrentItem(calendar.get(Calendar.AM_PM));

        // clear memo
        memoController.setText("");
    }

    private void setControllersEnabled(boolean enable) {
        ButterKnife.apply(dataControllers, ENABLED, enable);
    }

    // controller
    @OnClick(R.id.rightAngle)
    public void increaseFloor() {
        int currentItem = floorController.getCurrentItem();
        if (currentItem < floorController.getViewAdapter().getItemsCount())
            floorController.setCurrentItem(currentItem + 1, true);
    }

    @OnClick(R.id.leftAngle)
    public void decreaseFloor() {
        int currentItem = floorController.getCurrentItem();
        if (currentItem > 0)
            floorController.setCurrentItem(currentItem - 1, true);
    }

    private void updateWidget() {
        Intent intent = new Intent(this, MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MyWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    // widget

    private void startWidgetAlarm() {
        Intent intent = new Intent(this, MyWidgetProvider.class);
        intent.setAction(ACTION_START_ALARM);
        sendBroadcast(intent);
    }

    private void stopWidgetAlarm() {
        Intent intent = new Intent(this, MyWidgetProvider.class);
        intent.setAction(ACTION_STOP_ALARM);
        sendBroadcast(intent);
    }

    // setting

    private void openSettingFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag("dialog");
        if (prevFragment != null) {
            fragmentTransaction.remove(prevFragment);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment settingFragment = new SettingFragment();
        settingFragment.show(fragmentTransaction, "dialog");
    }

    // theme

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // etc

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public interface Callback {
        void callback();
    }
}
