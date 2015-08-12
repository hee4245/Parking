package com.bug.parking.adapter;

import android.content.Context;

import com.bug.parking.R;

import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

/**
 * Created by json on 15. 8. 9..
 */
public class FloorAdapter extends AbstractWheelTextAdapter {
    private String floors[];

    public FloorAdapter(Context context, String[] floors) {
        super(context, R.layout.floor_item, NO_RESOURCE);

        this.floors = floors;
        setItemTextResource(R.id.floorText);
    }

    @Override
    public CharSequence getItemText(int index) {
        return floors[index];
    }

    @Override
    public int getItemsCount() {
        return floors.length;
    }
}
