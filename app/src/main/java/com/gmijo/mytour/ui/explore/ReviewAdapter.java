package com.gmijo.mytour.ui.explore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmijo.mytour.R;



import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    //Inicijalizacija za podatke
    Context context;
    List<Pair<String, Pair<Long, String>>> data;



    public ReviewAdapter(Context context, List<Pair<String, Pair<Long, String>>> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Kreiranje viewholdera
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_reviews, parent, false);

        return new ReviewAdapter.ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewHolder holder, @SuppressLint("RecyclerView") int position) {

       //Prikaz podataka prosljeđenih u viewholder
       holder.rating.setRating(data.get(position).second.first);
       holder.userName.setText("myTour korisnik");
       holder.reviewText.setText(data.get(position).second.second);

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static final class ReviewHolder extends RecyclerView.ViewHolder{

        //Inicijalizacija elemenata

        ImageView userImg;
        TextView userName, reviewText;
        RatingBar rating;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            //Dobavljanje elemenata
            userImg = itemView.findViewById(R.id.reviewAvatar);
            userName = itemView.findViewById(R.id.reviewUsername);
            reviewText = itemView.findViewById(R.id.reviewText);
            rating = itemView.findViewById(R.id.reviewRating);



        }
    }}
