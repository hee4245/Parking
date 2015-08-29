package com.bug.parking.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bug.parking.R;
import com.bug.parking.adapter.ColorBarAdapter;
import com.bug.parking.adapter.FloorAdapter;
import com.bug.parking.data.FloorData;

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

        themeController.setViewAdapter(new ColorBarAdapter(inflater, new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA}));
        themeController.setCurrentItem(2);

        return view;
    }

}
