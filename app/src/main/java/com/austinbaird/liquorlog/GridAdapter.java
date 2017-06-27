package com.austinbaird.liquorlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Used to display drink images in Edit Drink Image Activity
 */

public class GridAdapter extends BaseAdapter{

    private int icons[];

    private String descriptions[];

    private Context context;

    private LayoutInflater inflater;

    public GridAdapter(Context context, int icons[], String descriptions[]){
        this.context = context;
        this.icons = icons;
        this.descriptions=descriptions;
    }

    @Override
    public int getCount() {
        return descriptions.length;
    }

    @Override
    public Object getItem(int position) {
        return descriptions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View gridView = convertView;

        if(convertView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.custom_layout,null);
        }

        ImageView icon = (ImageView) gridView.findViewById(R.id.icons);
        icon.setImageResource(icons[position]);

        return gridView;
    }
}
