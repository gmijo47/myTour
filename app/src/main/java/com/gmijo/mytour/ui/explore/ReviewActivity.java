package com.gmijo.mytour.ui.explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmijo.mytour.R;
import com.gmijo.mytour.rDataTips;
import com.gmijo.mytour.ui.pocetna.FeaturedCityAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    //Inicijalizacija elemenata
    ImageButton backBtn, addReview;
    TextView placeName;
    Intent intent;
    RecyclerView reviewRecycler;
    ReviewAdapter reviewAdapter;
    Context ctx;

    FirebaseDatabase firebaseDatabase;
    List<Pair<String, Pair<Long, String>>> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ctx = this;

        //Dodjelivanje vrijedosti elementima

        backBtn = (ImageButton) findViewById(R.id.reviewBackBtn);
        addReview = (ImageButton) findViewById(R.id.addReview);
        placeName = (TextView) findViewById(R.id.reviewPlaceTitle);
        intent = getIntent();
        reviewRecycler = (RecyclerView) findViewById(R.id.recyclerReviews);
        placeName.setText(intent.getExtras().getString("place"));
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Lisener na back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Lisenser na addreview
        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Provjera da li je korisnik već dao review
                firebaseDatabase.getReferenceFromUrl("https://mytour-df457-default-rtdb.europe-west1.firebasedatabase.app").child("attr_reviews").child(intent.getStringExtra("place")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //Korisnik je dao review
                        if (snapshot.exists()){

                            //Prikaz poruke da je dao
                            Toast.makeText(ctx, R.string.eReviewedAlready, Toast.LENGTH_LONG).show();

                        } else {

                            //Prikaz dialoga za davanje review-a
                            DialogAddReview dialogAddReview = new DialogAddReview(intent.getExtras().getString("place"), intent.getExtras().getString("type"));
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            dialogAddReview.show(fragmentTransaction, "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //Vršenje querija nad bazom za review-e te dobavljanje istih
        DatabaseReference documentReference = firebaseDatabase.getReferenceFromUrl("https://mytour-df457-default-rtdb.europe-west1.firebasedatabase.app").child("attr_reviews").child(intent.getStringExtra("place"));
        documentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                        //Ubacivanje review-a u listu
                        int i = 0;
                        data.add(i, new Pair(dataSnapshot.getKey(), new Pair(snapshot.child(dataSnapshot.getKey()).child("rating").getValue(), snapshot.child(dataSnapshot.getKey()).child("comment").getValue() )));
                       i++;

                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //Prikaz recycler viewa nako 250ms
                            reviewRecycler.setHasFixedSize(true);
                            reviewRecycler.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));
                            reviewAdapter = new ReviewAdapter(ctx, data);
                            reviewRecycler.setAdapter(reviewAdapter);

                        }
                    }, 250);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}