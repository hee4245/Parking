package com.bug.parking.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.bug.parking.R;
import com.bug.parking.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by json on 15. 8. 25..
 */
public class SettingFragment extends DialogFragment {

    private enum SettingMode {THEME, FONT}

//    @Bind(R.id.setting_theme_button)
//    Button themeButton;
//    @Bind(R.id.setting_font_button)
//    Button fontButton;

    //    @BindColor(R.color.light_gray_background)
//    int activeColor;
//    @BindColor(R.color.white)
//    int defaultColor;
    @Bind(R.id.setting_content_theme)
    FrameLayout contentTheme;
    @Bind(R.id.setting_content_font)
    FrameLayout contentFont;
    @Bind(R.id.setting_ok)
    Button okButton;

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
        okButton.setBackgroundResource(((MainActivity) getActivity()).getThemeManager().getCurrentButtonStyleResource());
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.setting_content_theme, themeFragment, "theme");
        fragmentTransaction.replace(R.id.setting_content_font, fontFragment, "font");
        fragmentTransaction.commit();

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
        switch (mode) {
            case THEME:
                contentTheme.setVisibility(View.VISIBLE);
                contentFont.setVisibility(View.INVISIBLE);
                break;
            case FONT:
                contentTheme.setVisibility(View.INVISIBLE);
                contentFont.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick(R.id.setting_ok)
    public void OnOkButtonClicked() {
        ThemeFragment themeFragment = (ThemeFragment) getChildFragmentManager().findFragmentByTag("theme");
        int currentTheme = themeFragment.themeController.getCurrentItem();

        MainActivity activity = (MainActivity) getActivity();
        SharedPreferences sharedPref = activity.getMyPreferences();
        if (sharedPref.getInt("theme", 0) != currentTheme) {
            sharedPref.edit().putInt("theme", currentTheme).commit();
            activity.recreate();
        }

        this.dismiss();
    }
}
