package com.gmijo.mytour.ui.pocetna;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteAttractionDataHelper;
import com.gmijo.mytour.dataparser.FeaturedImgParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PlaceInfoActivity extends AppCompatActivity {

    //Inicijalizacija elemenata
    ImageView featuredImg;
    ImageButton backBtn, dirBtn;
    TextView placeTitle, placeText, loaderErr, placeHeader, placeSubHeader, errNoNearBy, nearByTitle;
    ConstraintLayout loader;
    RecyclerView cityImages, nearByAttr;
    OtherImagesAdapter otherImagesAdapter;
    Bundle dataBundle;
    Button showOnMap;
    Intent intent;
    String imgUrl;
    Context ctx;
    List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> data = new ArrayList<>();


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
            imgUrl = "https://www.qwant.com/?t=images&q="+dataBundle.get("placename").toString();
        }

        //Dodjelivanje vrijednosti inicijaliziranim elementima
        backBtn = (ImageButton) findViewById(R.id.placeInfoBack);
        featuredImg = (ImageView) findViewById(R.id.placeInfoFeaturedImg);
        placeTitle = (TextView) findViewById(R.id.placeInfoTitle);
        placeText = (TextView) findViewById(R.id.placeInfoText);
        loaderErr = (TextView) findViewById(R.id.placeLoaderError);
        loader = (ConstraintLayout) findViewById(R.id.loaderPlace);
        cityImages = (RecyclerView) findViewById(R.id.morePhotosRecycler);
        placeHeader = (TextView) findViewById(R.id.placeHeader);
        placeSubHeader = (TextView) findViewById(R.id.placeSubHeader);
        showOnMap = (Button) findViewById(R.id.showOnMapBtn);
        dirBtn = (ImageButton) findViewById(R.id.directionsBtn);
        nearByAttr = (RecyclerView) findViewById(R.id.attractionsInCity);
        errNoNearBy = (TextView) findViewById(R.id.errNoAttr);
        nearByTitle = (TextView) findViewById(R.id.nearByTitle);

        //Lisener na back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Završava activity
                finish();

            }
        });

        //Prikazuje mijesto na mapi
        showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Kreiranje intenta, za startovanje gmm
                Uri gmmIntentUri = Uri.parse("geo:0,0?q= "+ dataBundle.get("placename").toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                //Startuje map activity
                startActivity(mapIntent);
            }
        });

        //Prikazuje upute do tog mijesta
        dirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Kreiranje intenta, za startovanje gmn
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+dataBundle.get("placename").toString()+"&avoid=f");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                //Startuje map activity
                startActivity(mapIntent);
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

                            }
                        });
                    }
                }).execute(imgUrl);

            }
        }, 20);

        //Postavljanje texta u texview-e
        placeTitle.setText(dataBundle.get("placename").toString());
        placeText.setText((dataBundle.get("placetext")).toString());
        placeHeader.setText(dataBundle.get("placename").toString());
        placeSubHeader.setText(dataBundle.get("placedesc").toString());

        //Type je null, znači radi se o gradu
        if (dataBundle.get("type") == null){
            SQLiteAttractionDataHelper liteAttractionDataHelper = new SQLiteAttractionDataHelper(this);
            if(liteAttractionDataHelper != null){
                data = liteAttractionDataHelper.getDataByCity(dataBundle.get("placename").toString());
            }
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   if (data != null){

                       nearByAttr.setHasFixedSize(true);
                       nearByAttr.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
                       FeaturedAttractionAdapter featuredAttractionAdapter = new FeaturedAttractionAdapter(ctx, data);
                       nearByAttr.setAdapter(featuredAttractionAdapter);

                   }else {

                       nearByAttr.setVisibility(View.GONE);
                       errNoNearBy.setVisibility(View.VISIBLE);

                   }
               }
           }, 200);
        }else {
            nearByAttr.setVisibility(View.GONE);
            nearByTitle.setVisibility(View.GONE);
        }
        //Uklanjanje loadinga
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeLoading();
            }
        }, 1000);

    }

    //Metoda za uklanjanje loadinga
    public void removeLoading(){
        loader.setVisibility(View.GONE);
       loaderErr.setVisibility(View.GONE);
    }

}