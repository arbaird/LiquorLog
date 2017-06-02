package com.austinbaird.liquorlog;

/**
 * Adapter used for horizontal Recycler View holding card views for a Drink Recipe
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

    ArrayList<DrinkRecipe> data;
    Context context;

    public HorizontalDrinkAdapter(Context context, ArrayList<DrinkRecipe> alName) {
        super();
        this.context = context;
        this.data= alName;
    }

    //inflate view group and return viewholder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycle_items, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    //update the displayed name, image, and download count
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.drinkName.setText(data.get(i).getName());
        viewHolder.imgThumbnail.setImageResource(data.get(i).getImageID());
        viewHolder.downloadCount.setText(data.get(i).getDownloads());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //holds the imageview for image, and the textviews for download count and name
    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView imgThumbnail;
        public TextView drinkName;
        public TextView downloadCount;

        public ViewHolder(View itemView)
        {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            drinkName = (TextView) itemView.findViewById(R.id.dbdrinkName);
            downloadCount = (TextView) itemView.findViewById(R.id.dbdrinkDownloads);
        }

    }

}