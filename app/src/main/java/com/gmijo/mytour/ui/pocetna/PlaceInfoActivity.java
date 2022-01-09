package com.gmijo.mytour.ui.pocetna;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.gmijo.mytour.R;
import com.gmijo.mytour.dataparser.FeaturedImgParser;
import com.gmijo.mytour.dataparser.OtherImagesParser;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PlaceInfoActivity extends AppCompatActivity {

    //Inicijalizacija elemenata
    ImageView featuredImg;
    ImageButton backBtn;
    TextView placeTitle, placeText, loaderErr;
    ConstraintLayout loader;
    RecyclerView cityImages;
    OtherImagesAdapter otherImagesAdapter;
    Bundle dataBundle;
    Intent intent;
    String imgUrl;
    Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        ctx = this;

        //Dobavljanje intenta, i bundle-a poslatog u intentu
        intent = getIntent();
        dataBundle = intent.getExtras();

        //Određivanje da li će slike vući sa predefinisanog linka, ili ce generisati sam shodno imenu grada
        if (dataBundle.get("img_url") != null){
            imgUrl = dataBundle.get("img_url").toString();
        }else {
            imgUrl = "https://www.qwant.com/?t=images&q="+dataBundle.get("placename").toString()+"&size=medium";
        }

        //Dodjelivanje vrijednosti inicijaliziranim elementima
        backBtn = (ImageButton) findViewById(R.id.placeInfoBack);
        featuredImg = (ImageView) findViewById(R.id.placeInfoFeaturedImg);
        placeTitle = (TextView) findViewById(R.id.placeInfoTitle);
        placeText = (TextView) findViewById(R.id.placeInfoText);
        loaderErr = (TextView) findViewById(R.id.placeLoaderError);
        loader = (ConstraintLayout) findViewById(R.id.loaderPlace);
        cityImages = (RecyclerView) findViewById(R.id.morePhotosRecycler);

        //Lisener na back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Završava activity
                finish();

            }
        });

        //Postavljanje optimalnog delaya kako bi se izbjeglo blokiranje od strane SE
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                new FeaturedImgParser(new FeaturedImgParser.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Prikaz slike na osnovu URL-a
                                Picasso.with(getApplicationContext()).load(output)
                                        .error(R.drawable.img_error_holder)
                                        .into(featuredImg);

                                //Pozivanje recycler viewa
                                cityImages.setHasFixedSize(true);
                                cityImages.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                otherImagesAdapter = new OtherImagesAdapter(PlaceInfoActivity.this, imgUrl);
                                cityImages.setAdapter(otherImagesAdapter);

                                //Uklanjanje loadinga
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                removeLoading();
                                            }
                                        }, 500);
                            }
                        });
                    }
                }).execute(imgUrl);

            }
        }, 20);

        placeTitle.setText(dataBundle.get("placename").toString());
        placeText.setText((dataBundle.get("placetext")).toString());


    }

    //Metoda za uklanjanje loadinga
    public void removeLoading(){
        loader.setVisibility(View.GONE);
       loaderErr.setVisibility(View.GONE);
    }

}