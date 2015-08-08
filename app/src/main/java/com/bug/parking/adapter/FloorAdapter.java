package com.bug.parking.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bug.parking.R;

/**
 * Created by json on 15. 8. 7..
 */
public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.ViewHolder> {
    private String[] dataset = new String[]{"B5", "B4", "B3", "B2", "B1", "1F", "2F", "3F", "4F", "5F"};

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public FloorAdapter() {
    }

    @Override
    public FloorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.floor_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView textView = (TextView) holder.view;
        textView.setText(dataset[position]);
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }
}
