package com.gmijo.mytour.ui.pocetna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteAttractionDataHelper;
import com.gmijo.mytour.dataparser.FeaturedImgParser;
import com.gmijo.mytour.ui.explore.ReviewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PlaceInfoActivity extends AppCompatActivity {

    //Inicijalizacija elemenata
    ImageView featuredImg, badgeImg;
    ImageButton backBtn, dirBtn, viewMoreReviews;
    TextView placeTitle, placeText, loaderErr, placeHeader, placeSubHeader, errNoNearBy, nearByTitle,
                badgeText, ratingText;
    ConstraintLayout loader, reviewBox;
    RecyclerView cityImages, nearByAttr;
    OtherImagesAdapter otherImagesAdapter;
    Bundle dataBundle;
    Button showOnMap;
    Intent intent;
    String imgUrl;
    Context ctx;
    FirebaseDatabase firebaseDatabase;
    RatingBar ratingBar;

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
        reviewBox = (ConstraintLayout) findViewById(R.id.reviewBox);
        viewMoreReviews = (ImageButton) findViewById(R.id.viewReviews);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingText = (TextView) findViewById(R.id.numRating);
        badgeImg = (ImageView) findViewById(R.id.badgemyTourCertify);
        badgeText = (TextView) findViewById(R.id.certifyText);

        firebaseDatabase = FirebaseDatabase.getInstance();

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
        //Prikaz više review-a
        viewMoreReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Kreira intent sa podatcima o mijestu na koje je kliknuo
                Intent reviewsIntent = new Intent(PlaceInfoActivity.this, ReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("place", dataBundle.get("placename").toString());

                    String str = String.valueOf(dataBundle.get("type"));

                   if (str.matches("Nacionalni park")) {

                        bundle.putString("type", "npark");
                        reviewsIntent.putExtras(bundle);
                        startActivity(reviewsIntent);


                    } else if (str.matches("Etno selo") ) {

                        bundle.putString("type", "vill");
                        reviewsIntent.putExtras(bundle);
                        startActivity(reviewsIntent);


                    } else if (str.matches("city")) {

                        bundle.putString("type", "city");
                        reviewsIntent.putExtras(bundle);
                        startActivity(reviewsIntent);
                    }else {
                       bundle.putString("type", "attr");
                       reviewsIntent.putExtras(bundle);
                       startActivity(reviewsIntent);
                   }





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
            if (dataBundle.get("type") == null) {
                SQLiteAttractionDataHelper liteAttractionDataHelper = new SQLiteAttractionDataHelper(this);
                if (liteAttractionDataHelper != null) {
                    data = liteAttractionDataHelper.getDataByCity(dataBundle.get("placename").toString());
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (data != null) {

                            nearByAttr.setHasFixedSize(true);
                            nearByAttr.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
                            if(dataBundle.getBoolean("enable_reviews")) {
                                FeaturedAttractionAdapter featuredAttractionAdapter = new FeaturedAttractionAdapter(ctx, data, true);
                                nearByAttr.setAdapter(featuredAttractionAdapter);
                                reviewBox.setVisibility(View.VISIBLE);
                                getRating(dataBundle.get("placename").toString());
                            }else {
                                FeaturedAttractionAdapter featuredAttractionAdapter = new FeaturedAttractionAdapter(ctx, data, false);
                                nearByAttr.setAdapter(featuredAttractionAdapter);
                                //Uklanjanje loadinga
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeLoading();
                                    }
                                }, 1000);
                            }


                            } else {

                            nearByAttr.setVisibility(View.GONE);
                            errNoNearBy.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    removeLoading();
                                }
                            }, 1000);
                        }
                    }
                }, 200);
            } else {

                nearByAttr.setVisibility(View.GONE);
                nearByTitle.setVisibility(View.GONE);

                if(dataBundle.getBoolean("enable_reviews") == true) {

                    reviewBox.setVisibility(View.VISIBLE);
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) reviewBox.getLayoutParams();
                    layoutParams.setMargins(0, 60, 0, 0);
                    reviewBox.setLayoutParams(layoutParams);

                    getRating(dataBundle.get("placename").toString());

                }else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            removeLoading();
                        }
                    }, 1000);
                }

            }

    }

    public void getRating(String placename) {
        DatabaseReference documentReference = firebaseDatabase.getReferenceFromUrl("https://mytour-df457-default-rtdb.europe-west1.firebasedatabase.app").child("attr_reviews").child(placename);
       documentReference.addListenerForSingleValueEvent(new ValueEventListener() {
           float sum;
           float count;
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                       count = (int) snapshot.getChildrenCount();
                       String str = snapshot.child(dataSnapshot.getKey()).child("rating").getValue().toString();

                           int digit = Integer.parseInt(str);
                           sum += digit;

                   }

                   float rating = sum / count;
                   if(rating >= 4.5){
                       badgeImg.setVisibility(View.VISIBLE);
                       badgeText.setVisibility(View.VISIBLE);
                   }
                   ratingBar.setRating(rating);
                   ratingText.setText(String.valueOf(ratingBar.getRating()));
                   //Uklanjanje loadinga
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           removeLoading();
                       }
                   }, 1000);

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    //Metoda za uklanjanje loadinga
    public void removeLoading(){
        loader.setVisibility(View.GONE);
       loaderErr.setVisibility(View.GONE);
    }

}