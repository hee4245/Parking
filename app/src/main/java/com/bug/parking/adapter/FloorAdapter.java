package com.bug.parking.adapter;

import android.content.Context;
import android.view.View;

import com.bug.parking.R;

import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

/**
 * Created by json on 15. 8. 9..
 */
public class FloorAdapter extends AbstractWheelTextAdapter {
    private String floors[] = new String[]{"B5", "B4", "B3", "B2", "B1", "1F", "2F", "3F", "4F", "5F"};

    public FloorAdapter(Context context) {
        super(context, R.layout.floor_item, NO_RESOURCE);

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
