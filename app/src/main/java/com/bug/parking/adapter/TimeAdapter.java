package com.bug.parking.adapter;

import android.content.Context;

import com.bug.parking.R;

import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

/**
 * Created by 세희 on 2015-08-16.
 */
public class TimeAdapter extends NumericWheelAdapter {

    public TimeAdapter(Context context, int min, int max){
        this(context, min, max, null);
    }

    public TimeAdapter(Context context, int min, int max, String format){
        super(context, min, max, format);
        setItemResource(R.layout.time_item);
        setItemTextResource(R.id.timeText);
    }

}
