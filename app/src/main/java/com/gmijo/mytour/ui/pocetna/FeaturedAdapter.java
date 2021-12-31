package com.gmijo.mytour.ui.pocetna;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.gmijo.mytour.R;
import com.gmijo.mytour.ui.profil.EditProfile;

import java.util.ArrayList;
import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedHolder> {

    Context context;
    List<Pair<Pair<String, String>, Pair<String, String>>> data;

    public FeaturedAdapter(Context context, List<Pair<Pair<String, String>, Pair<String, String>>> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public FeaturedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.adapter_featured_layout, parent, false);

        return new FeaturedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedHolder holder, int position) {

        holder.placeName.setText(String.valueOf(data.get(position).first.first));
        holder.placePopulation.setText(context.getResources().getString(R.string.hPopulation) +" " + String.valueOf(data.get(position).first.second));
        holder.placeInfo.setText(String.valueOf(data.get(position).second.first));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(context, EditProfile.class);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static final class FeaturedHolder extends RecyclerView.ViewHolder{

        ImageView placeImg;
        TextView placeName, placePopulation, placeInfo;

        public FeaturedHolder(@NonNull View itemView) {
            super(itemView);

            placeImg = itemView.findViewById(R.id.placeImg);
            placeName = itemView.findViewById(R.id.placeName);
            placePopulation = itemView.findViewById(R.id.placePop);
            placeInfo = itemView.findViewById(R.id.placeInfo);

        }
    }}