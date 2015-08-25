package com.bug.parking.adapter;

import android.content.Context;

import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

/**
 * Created by json on 15. 8. 9..
 */
public class FloorAdapter extends AbstractWheelTextAdapter {
    private String data[];

    public FloorAdapter(Context context, String[] data, int itemResource, int itemTextResource) {
        super(context, itemResource, itemTextResource);

        this.data = data;
    }

    @Override
    public CharSequence getItemText(int index) {
        return data[index];
    }

    @Override
    public int getItemsCount() {
        return data.length;
    }
}
