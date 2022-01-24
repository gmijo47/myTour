package com.gmijo.mytour.ui.pocetna;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.gmijo.mytour.R;
import com.gmijo.mytour.dataparser.FeaturedImgParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FeaturedAttractionAdapter extends RecyclerView.Adapter<FeaturedAttractionAdapter.FeaturedHolder> {

    //Inicijalizacija za podatke
    Context context;
    Boolean enableReview;
    List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> data;
    List<String> urlDataArr = new ArrayList<>();
    String urlData;



    public FeaturedAttractionAdapter(Context context, List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> data, Boolean review) {
        this.data = data;
        this.context = context;
        this.enableReview = review;

        //Ubacivanje linkova u array
        for(int i = 0; i<data.size(); i++){
            if(data.get((i)).second.second.second == null){
                urlDataArr.add(urlData = "https://www.qwant.com/?t=images&q="+ data.get(i).first.first);
            }else {
                urlDataArr.add(data.get(i).second.second.second);
            }
        }
    }

    @NonNull
    @Override
    public FeaturedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Kreiranje viewholdera
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_nearby_attractions, parent, false);

        return new FeaturedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.placeName.setText(String.valueOf(data.get(position).first.first));
        holder.placeDesc.setText(String.valueOf(data.get(position).second.first.first));

        //Optimalni delay, da ne bi došlo do blokiranja od strane SE za slike
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Pozivanje async taska za dobavljanje slika
                new FeaturedImgParser(new FeaturedImgParser.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //Prikaz slike na osnovu URLa
                                Picasso.with(context).load(output).placeholder(R.drawable.img_nature_holder)
                                        .error(R.drawable.img_error_holder)
                                        .into(holder.attractionImg);

                            }
                        });
                    }
                }).execute(urlDataArr.get(position));
            }
        }, 20);

        //Startuje novi activity sa prikazom grada
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaceInfoActivity.class);

              //Smiještanje podataka u bundle
              Bundle dataBundle = new Bundle();
              dataBundle.putString("placename", data.get(position).first.first);
              dataBundle.putString("type", data.get(position).first.second);
              dataBundle.putString("placedesc", data.get(position).second.first.first);
              dataBundle.putString("placetext", data.get(position).second.first.second);
              dataBundle.putString("ncity", data.get(position).second.second.first);
              dataBundle.putString("img_url", data.get(position).second.second.second);
              if (enableReview){

                  dataBundle.putBoolean("enable_reviews", true);

              }else {
                  dataBundle.putBoolean("enable_reviews", false);
              }

              intent.putExtras(dataBundle);
              //Startovanje acitivijta
              ((Activity)context).startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static final class FeaturedHolder extends RecyclerView.ViewHolder{

        //Inicijalizacija elemenata

        ImageView attractionImg;
        TextView placeName, placeDesc;

        public FeaturedHolder(@NonNull View itemView) {
            super(itemView);

            //Dobavljanje elemenata
            attractionImg = itemView.findViewById(R.id.attractionImg);
            placeName = itemView.findViewById(R.id.attractionName);
            placeDesc = itemView.findViewById(R.id.attractionDesc);


        }
    }}