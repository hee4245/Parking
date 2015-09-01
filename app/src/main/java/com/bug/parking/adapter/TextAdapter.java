package com.bug.parking.adapter;

import android.content.Context;
import android.graphics.Typeface;

import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

/**
 * Created by json on 15. 8. 9..
 */
public class TextAdapter extends AbstractWheelTextAdapter {
    private String data[];

    public TextAdapter(Context context, String[] data, int itemResource, int itemTextResource) {
        this(context, data, itemResource, itemTextResource, Typeface.NORMAL);
    }

    public TextAdapter(Context context, String[] data, int itemResource, int itemTextResource, int textStyle) {
        super(context, itemResource, itemTextResource);

        this.data = data;

        setTextTypeface(Typeface.create(Typeface.SANS_SERIF, textStyle));
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
