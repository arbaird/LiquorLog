package com.austinbaird.liquorlog;

/**
 * Created by austinbaird on 5/13/17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class HorizontalDrinkAdapter extends RecyclerView.Adapter< HorizontalDrinkAdapter.ViewHolder> {

    ArrayList<DrinkRecipe> alName;
    Context context;

    public HorizontalDrinkAdapter(Context context, ArrayList<DrinkRecipe> alName) {
        super();
        this.context = context;
        this.alName = alName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycle_items, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvSpecies.setText(alName.get(i).getName());
        viewHolder.imgThumbnail.setImageResource(alName.get(i).getImageID());
        viewHolder.downloadCount.setText(alName.get(i).getDownloads());

    }

    @Override
    public int getItemCount() {
        return alName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView imgThumbnail;
        public TextView tvSpecies;
        public TextView downloadCount;
        //private ItemClickListener clickListener;

        public ViewHolder(View itemView)
        {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvSpecies = (TextView) itemView.findViewById(R.id.dbdrinkName);
            downloadCount = (TextView) itemView.findViewById(R.id.dbdrinkDownloads);
        }

    }

}