package com.bug.parking.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bug.parking.R;

/**
 * Created by json on 15. 8. 28..
 */
public class ThemeFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.theme_fragment, container, false);

        return view;
    }

}
