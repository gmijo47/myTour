package com.gmijo.mytour.ui.leaderboard.nav_fragments;

import static com.gmijo.mytour.database.SQLiteController.COL_COUNT;
import static com.gmijo.mytour.database.SQLiteController.COL_USERNAME;
import static com.gmijo.mytour.database.SQLiteController.TAB_VILLAGE_STATS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteDataHelper;
import com.gmijo.mytour.ui.leaderboard.CustomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class lbVillage extends Fragment implements navInterface {

    //Definisanje elemenata
    RecyclerView recyclerView;
    SQLiteDataHelper dataHelper;
    View view;
    ArrayList<String> username_list, count_value_list;
    FirebaseFirestore firebaseFirestore;
    CustomAdapter customAdapter;
    ConstraintLayout loader;
    TextView titleVillage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inicijaliziranje elemenata
        dataHelper = new SQLiteDataHelper(getActivity());
        username_list = new ArrayList<String>();
        count_value_list = new ArrayList<String>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        view = inflater.inflate(R.layout.fragment_lb_village, container, false);
        recyclerView =  (RecyclerView)  view.findViewById(R.id.villageRecView);
        loader = (ConstraintLayout) view.findViewById(R.id.loaderVillage);
        titleVillage = (TextView) view.findViewById(R.id.titleVillage);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Boolean dataExists = dataHelper.checkTableDatExt(TAB_VILLAGE_STATS);
        Log.d("Mijo2", String.valueOf(dataExists));
        if (!dataExists){

            //Odabir kolekcije u firestore-u (bazi)
            CollectionReference usersRef = firebaseFirestore.collection("users");

            //Query komanda
            Query query = usersRef.orderBy("achievementData.villageExplored", Query.Direction.DESCENDING).limit(15);

            //Poziva query
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        //Loopuje kroz rezultate dobivene iz querija
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            try {

                                //Ubacuje podatke u SQL lite tabelu
                                dataHelper.insertData(TAB_VILLAGE_STATS, COL_USERNAME, COL_COUNT, document.get("personalData.Username").toString(), document.get("achievementData.villageExplored").toString(), true);

                            } catch (Exception e) {

                                //Došlo je do pogreške, pirkaz errora
                                setError();

                            }

                        }
                    } else {

                        //Došlo je do pogreške, pirkaz errora
                        setError();

                    }
                }
            });


        }else {

            //Podatci postoje lokalno, poziva metodu za prikaz istih
            displayData();

        }

        super.onViewCreated(view, savedInstanceState);

    }

    //Metoda za prikaz podatataka kada su oni dostupni lokalno
    public void displayData() {

        //Fetch iz lokalne baze u listu koja sarži parove (username-count)
        List<Pair<String, String>> data = dataHelper.obtainData(TAB_VILLAGE_STATS);

        //Loop kroz listu, ubacivanje padataka u 2 razlicita array liste;
        for (int i = 0; i< data.size(); i++){
            username_list.add(i, data.get(i).first);
            count_value_list.add(i, data.get(i).second);
        }

        //Pozivanje custom adaptera i prikaz podataka
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        customAdapter = new CustomAdapter(username_list, count_value_list);
        recyclerView.setAdapter(customAdapter);

        loader.setVisibility(View.GONE);
        titleVillage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

    }

    //Prosljeđivanje konteksta u data helper
    @Override
    public void onResume() {
        super.onResume();
        //Prosljeđuje kontekst u datahelper preko interface-a
        dataHelper.setFragmentDisplayer(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Prosljeđuje null u datahelper preko interface-a
        dataHelper.setFragmentDisplayer(null);
    }

    //Metoda za prikaz errora
    public void setError(){
        Snackbar.make(getView(), R.string.errLoader, Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.nav_view)
                .show();
    }
}