package com.gmijo.mytour.ui.leaderboard.nav_fragments;
import static com.gmijo.mytour.database.SQLiteController.TAB_TOKEN_STATS;
import static com.gmijo.mytour.database.SQLiteController.COL_COUNT;
import static com.gmijo.mytour.database.SQLiteController.COL_USERNAME;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteDataHelper;
import com.gmijo.mytour.ui.leaderboard.CustomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class lbTokens extends Fragment {

    //Definisanje elemenata
    RecyclerView recyclerView;
    SQLiteDataHelper dataHelper;
    ArrayList<String> username_list, count_value_list;
    FirebaseFirestore firebaseFirestore;
    CustomAdapter customAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataHelper = new SQLiteDataHelper(getActivity());
        username_list = new ArrayList<String>();
        count_value_list = new ArrayList<String>();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return inflater.inflate(R.layout.fragment_lb_tokens, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Boolean dataExists = dataHelper.checkTableDatExt(TAB_TOKEN_STATS);
        if (!dataExists){

            //Odabir kolekcije u firestore-u (bazi)
            CollectionReference usersRef = firebaseFirestore.collection("users");

            //Query komanda
            Query query = usersRef.orderBy("achievementData.myTourTokens", Query.Direction.DESCENDING).limit(15);

            //Poziva query
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            //Ubacuje podatke u SQL lite tabelu
                            dataHelper.insertData(TAB_TOKEN_STATS, COL_USERNAME, COL_COUNT, document.get("personalData.Username").toString(), document.get("achievementData.myTourTokens").toString());

                        }
                    } else {

                        //todo error

                    }
                }
            });


        }else {

            displayData();

        }


        super.onViewCreated(view, savedInstanceState);
    }

    private void displayData() {

        ArrayList<String> user_list = new ArrayList<>();
        ArrayList<String> count_list = new ArrayList<>();

        List<Pair<String, String>> data = dataHelper.obtainData(TAB_TOKEN_STATS);
        for (int i = 0; i< data.size(); i++){
            user_list.add(i, data.get(i).first);
            count_list.add(i, data.get(i).second);
        }
        customAdapter = new CustomAdapter(getActivity(), user_list, count_list);

    }
}
