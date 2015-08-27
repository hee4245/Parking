package com.bug.parking.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.bug.parking.R;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by json on 15. 8. 25..
 */
public class SettingFragment extends DialogFragment{

    private enum SettingMode { THEME, FONT };

//    @Bind(R.id.setting_theme_button)
//    Button themeButton;
//    @Bind(R.id.setting_font_button)
//    Button fontButton;

//    @BindColor(R.color.light_gray_background)
//    int activeColor;
//    @BindColor(R.color.white)
//    int defaultColor;
    Fragment themeFragment = new ThemeFragment();
    Fragment fontFragment = new FontFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init fragment
        View view = inflater.inflate(R.layout.setting_dialog, container, false);
        ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setFragment(SettingMode.THEME);

        return view;
    }

    @OnClick(R.id.setting_theme_button)
    public void onThemeButtonClicked() {
        setFragment(SettingMode.THEME);

//        themeButton.setBackgroundColor(activeColor);
//        fontButton.setBackgroundColor(defaultColor);
    }

    @OnClick(R.id.setting_font_button)
    public void onFontButtonClicked() {
        setFragment(SettingMode.FONT);
    }

    private void setFragment(SettingMode mode) {
        Fragment fragment = null;

        switch (mode) {
            case THEME:
                fragment = themeFragment;
                break;
            case FONT:
                fragment = fontFragment;
                break;
        }

        if (fragment != null)
            getChildFragmentManager().beginTransaction().replace(R.id.setting_content, fragment).commit();
    }

    @OnClick(R.id.setting_ok)
    public void OnOkButtonClicked() {
        this.dismiss();
    }
}
