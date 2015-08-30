package com.bug.parking.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bug.parking.R;
import com.bug.parking.activity.MainActivity;
import com.bug.parking.adapter.ColorBarAdapter;
import com.bug.parking.adapter.FloorAdapter;
import com.bug.parking.data.FloorData;
import com.bug.parking.manager.ThemeManager;

import antistatic.spinnerwheel.AbstractWheel;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by json on 15. 8. 28..
 */
public class ThemeFragment extends Fragment {

    @Bind(R.id.theme_controller)
    AbstractWheel themeController;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.theme_fragment, container, false);
        ButterKnife.bind(this, view);

        themeController.setViewAdapter(new ColorBarAdapter(inflater, ((MainActivity)getActivity()).getThemeManager().getPrimaryColors()));

        MainActivity activity = (MainActivity)getActivity();
        SharedPreferences sharedPref = activity.getMyPreferences();
        themeController.setCurrentItem(sharedPref.getInt("theme", 0));

        return view;
    }

}
