package com.bug.parking.manager;

import android.content.Context;
import android.content.res.Resources;

import com.bug.parking.R;

import java.util.List;

/**
 * Created by json on 15. 8. 30..
 */
public class ThemeManager {
    int[] themeRess = { R.style.PinkTheme, R.style.DeepOrangeTheme, R.style.AmberTheme, R.style.LightBlueTheme, R.style.DeepPurpleTheme};
    int[] primaryColors;
    int[] primaryColorRess = { R.color.pink_500, R.color.deepOrange_500, R.color.amber_500, R.color.lightBlue_500, R.color.deepPurple_500};
    int[] buttonStyleRess = { R.drawable.button_pink, R.drawable.button_deeporange, R.drawable.button_amber, R.drawable.button_lightblue, R.drawable.button_deeppurple };

    private int currentThemeIndex = 0;

    public ThemeManager(Context context) {
        Resources resources = context.getResources();

        primaryColors = new int[primaryColorRess.length];
        for (int i = 0; i < primaryColors.length; ++i) {
            primaryColors[i] = resources.getColor(primaryColorRess[i]);
        }
    }

    public void setCurrentThemeIndex(int index) {
        currentThemeIndex = index;
    }

    public int[] getThemeRess() {
        return themeRess;
    }

    public int[] getPrimaryColorResources() {
        return primaryColorRess;
    }

    public int[] getPrimaryColors() {
        return primaryColors;
    }

    public int[] getButtonStyles() {
        return buttonStyleRess;
    }

    public int getThemeResource(int index) {
        return themeRess[index];
    }

    public int getPrimaryColorResource(int index) {
        return primaryColorRess[index];
    }

    public int getPrimaryColor(int index) {
        return primaryColors[index];
    }

    public int getButtonStyleResource(int index) {
        return buttonStyleRess[index];
    }

    public int getCurrentThemeResource() {
        return getThemeResource(currentThemeIndex);
    }

    public int getCurrentPrimaryColorResource() {
        return getPrimaryColorResource(currentThemeIndex);
    }

    public int getCurrentPrimaryColor() {
        return getPrimaryColor(currentThemeIndex);
    }

    public int getCurrentButtonStyleResource() {
        return getButtonStyleResource(currentThemeIndex);
    }

}