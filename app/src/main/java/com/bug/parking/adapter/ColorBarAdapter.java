package com.bug.parking.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.bug.parking.R;

import antistatic.spinnerwheel.adapters.AbstractWheelCustomAdapter;

/**
 * Created by json on 15. 8. 29..
 */
public class ColorBarAdapter extends AbstractWheelCustomAdapter {
    private int data[];

    public ColorBarAdapter(LayoutInflater inflater, int[] data) {
        super(inflater, R.layout.setting_color_item);

        this.data = data;
    }

    @Override
    protected void configureItemView(View view, int index) {
        View colorBarView = view.findViewById(R.id.setting_color_bar);

        colorBarView.setBackgroundColor(data[index]);
    }

    @Override
    public int getItemsCount() {
        return data.length;
    }
}
