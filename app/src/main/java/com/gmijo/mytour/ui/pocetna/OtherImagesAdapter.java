package com.gmijo.mytour.ui.pocetna;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmijo.mytour.R;
import com.gmijo.mytour.dataparser.OtherImagesParser;
import com.squareup.picasso.Picasso;



    public class OtherImagesAdapter extends RecyclerView.Adapter<OtherImagesAdapter.OtherHolder> {

        //Inicijalizacija za podatke
        Context context;
        String imgsUrl;

        //Broj dodatnih slika
        static int otherPicNum = 6;



        public OtherImagesAdapter(Context context, String imgsUrl) {
            this.imgsUrl = imgsUrl;
            this.context = context;
        }

        @NonNull
        @Override
        public OtherImagesAdapter.OtherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            //Kreiranje viewholdera
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_more_photos, parent, false);

            return new OtherImagesAdapter.OtherHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OtherImagesAdapter.OtherHolder holder, @SuppressLint("RecyclerView") int position) {
            //Optimalni delay, da ne bi došlo do blokiranja od strane SE za slike

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //Pozivanje async taska za dobavljanje slika
                    new OtherImagesParser(new OtherImagesParser.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //Prikaz slike na osnovu URLa
                                    Picasso.with(context).load(output).placeholder(R.drawable.img_city_holder)
                                            .error(R.drawable.img_error_holder)
                                            .into(holder.placesImg);

                                }
                            });

                        }
                    }, position).execute(imgsUrl);
                }
            }, 20);

            //Setovanje slike kao featured kada klikne na nju
            holder.placesImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView img = ((Activity)context).findViewById(R.id.placeInfoFeaturedImg);
                    img.setImageDrawable(holder.placesImg.getDrawable());
                }
            });

        }


        @Override
        public int getItemCount() {
            return otherPicNum;
        }

        public static final class OtherHolder extends RecyclerView.ViewHolder{

            //Inicijalizacija elemenata

            ImageView placesImg;

            public OtherHolder(@NonNull View itemView) {
                super(itemView);

                //Dobavljanje elemenata
                placesImg = itemView.findViewById(R.id.placesImg);


            }
        }}
