//package com.example.ti22_csd_hueapplication;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//public class LampAdapter extends RecyclerView.Adapter<LampAdapter.LampViewHolder> {
//
//    private ArrayList<Lamp> lamps;
//
//    public LampAdapter(Context context, ArrayList<Lamp> lamps){
//        this.lamps = lamps;
//    }
//
//    @NonNull
//    @Override
//    public LampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
//        View view = layoutInflater.inflate(R.layout.activity_lamp_object, parent, false);
//        return new LampViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull LampViewHolder lampViewHolder, int position) {
//
//        Lamp lamp = lamps.get(position);
//        lampViewHolder.lampName.setText(lamp.getName());
//        Log.d("!!!!!!!!!!lamp", String.valueOf(getItemCount()));
//    }
//
//    @Override
//    public int getItemCount() {
//        return this.lamps.size();
//    }
//
//    public class LampViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView lampName;
//
//        public LampViewHolder(@NonNull View itemView) {
//            super(itemView);
//            this.lampName = itemView.findViewById(R.id.name_lamp_lamp_object);
//        }
//    }
//}
