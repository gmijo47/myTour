package com.gmijo.mytour.ui.pocetna;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class FeaturedNParkAdapter extends RecyclerView.Adapter<FeaturedNParkAdapter.NParkHolder> {

    //Inicijalizacija za podatke
    Context context;
    List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> data;
    List<String> urlDataArr = new ArrayList<>();
    String urlData;



    public FeaturedNParkAdapter(Context context, List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> data) {
        this.data = data;
        this.context = context;

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
    public NParkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Kreiranje viewholdera
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_npark_layout, parent, false);

        return new NParkHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NParkHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.placeName.setText(String.valueOf(data.get(position).first.first));
        holder.placeInfo.setText(String.valueOf(data.get(position).second.first.first));

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
                                Picasso.with(context).load(output).placeholder(R.drawable.img_npark_holder)
                                        .error(R.drawable.img_error_holder)
                                        .into(holder.placeImg);

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
                dataBundle.putString("img_url", data.get(position).second.second.second);
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

    public static final class NParkHolder extends RecyclerView.ViewHolder{

        //Inicijalizacija elemenata

        ImageView placeImg;
        TextView placeName, placeInfo;

        public NParkHolder(@NonNull View itemView) {
            super(itemView);

            //Dobavljanje elemenata
            placeImg = itemView.findViewById(R.id.nParkImg);
            placeName = itemView.findViewById(R.id.nParkName);
            placeInfo = itemView.findViewById(R.id.nParkDesc);

        }
    }}