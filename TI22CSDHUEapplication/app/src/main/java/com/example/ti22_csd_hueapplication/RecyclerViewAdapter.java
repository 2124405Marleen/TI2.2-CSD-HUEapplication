package com.example.ti22_csd_hueapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Lamp> lamps;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ViewHolder viewHolder;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, ArrayList<Lamp> lamps) {
        this.mInflater = LayoutInflater.from(context);
        this.lamps = lamps;
    }

//    public ViewHolder getViewHolder() {
//        return viewHolder;
//    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_lamp_item, parent, false);
//        View rvLightsItemID = view.findViewById(R.id.rvLampsItemID);
//        rvLightsItemID.setTe
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lamp l = lamps.get(position);
        TextView TextViewNO = holder.TextViewNO;
        Log.d("________LAMP", "onBindViewHolder: " + l.getId());
        TextViewNO.setText(String.valueOf(l.getId()));

        TextView TextViewAvailable = holder.TextViewAvailable;
        TextViewAvailable.setText((l.isOn() ? "true" : "false"));
        ImageView ImageViewColor = holder.ImageViewColor;
        int colorHSB = HSBC.getRGBFromHSB(l.getHue(), l.getSat(), l.getBri());
        ImageViewColor.setColorFilter(colorHSB);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return lamps.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView TextViewNO;
        public TextView TextViewAvailable;
        public ImageView ImageViewColor;

        public void setImageViewColor(ImageView imageViewColor) {
            ImageViewColor = imageViewColor;
        }

        ViewHolder(View itemView) {
            super(itemView);
            TextViewNO = itemView.findViewById(R.id.textViewNo);
            TextViewAvailable = itemView.findViewById(R.id.textViewAvailable);
            ImageViewColor = itemView.findViewById(R.id.imageViewColor);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Lamp getItem(int id) {
        return lamps.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
