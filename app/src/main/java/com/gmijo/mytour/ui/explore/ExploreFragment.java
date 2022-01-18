package com.gmijo.mytour.ui.explore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteAttractionDataHelper;
import com.gmijo.mytour.database.SQLiteCityDataHelper;
import com.gmijo.mytour.database.SQLiteNParkDataHelper;
import com.gmijo.mytour.database.SQLiteVillageDataHelper;
import com.gmijo.mytour.databinding.AdapterExploreBinding;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class ExploreFragment extends Fragment {

    //Inicijalizacija elemenata
    View view;
    Chip cityC, attrC, villC, nparkC;
    TextView wlcmTitle, exploreMessage;
    ProgressBar exploreProgress;
    ConstraintLayout messageBox;
    androidx.appcompat.widget.SearchView searchBox;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String selectedChip = "cityC";
    SQLiteCityDataHelper liteCityDataHelper;
    SQLiteAttractionDataHelper liteAttractionDataHelper;
    SQLiteVillageDataHelper liteVillageDataHelper;
    SQLiteNParkDataHelper liteNParkDataHelper;
    RecyclerView exploreRes;
    ExploreAdapter exploreAdapter;
    List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> data = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        //Dodjelivanje vrijednosti elementima
        view = inflater.inflate(R.layout.fragment_explore, container, false);
        cityC = (Chip) view.findViewById(R.id.gradoviChip);
        attrC = (Chip) view.findViewById(R.id.attrChip);
        villC = (Chip) view.findViewById(R.id.villChip);
        nparkC = (Chip) view.findViewById(R.id.nParkChip);
        wlcmTitle = (TextView) view.findViewById(R.id.wellcomeTxt);
        searchBox = (androidx.appcompat.widget.SearchView) view.findViewById(R.id.searchData);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        exploreRes = (RecyclerView) view.findViewById(R.id.exploreResults);
        exploreMessage = (TextView) view.findViewById(R.id.exploreMessage);
        exploreProgress = (ProgressBar) view.findViewById(R.id.exploreProgress);
        messageBox = (ConstraintLayout) view.findViewById(R.id.exploreMessageBox);
        liteCityDataHelper = new SQLiteCityDataHelper(getActivity());
        liteAttractionDataHelper = new SQLiteAttractionDataHelper(getActivity());
        liteNParkDataHelper = new SQLiteNParkDataHelper(getActivity());
        liteVillageDataHelper = new SQLiteVillageDataHelper(getActivity());

        return  view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Postavljanje dobrodošlice sa imenom korisnika
        wlcmTitle.setText(getContext().getString(R.string.eTitle) + " " + firebaseUser.getDisplayName());

        //Liseneri na promjene chipova
        cityC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChips();
                cityC.setChipIconTint(getContext().getColorStateList(R.color.blue_grad2));
                cityC.setChipStrokeColor(getContext().getColorStateList(R.color.blue_grad2));
                selectedChip = "cityC";
                querySubmit(searchBox.getQuery().toString());

            }
        });
        attrC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChips();
                attrC.setChipIconTint(getContext().getColorStateList(R.color.blue_grad2));
                attrC.setChipStrokeColor(getContext().getColorStateList(R.color.blue_grad2));
                selectedChip = "attrC";
                querySubmit(searchBox.getQuery().toString());
            }
        });
        villC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChips();
                villC.setChipIconTint(getContext().getColorStateList(R.color.blue_grad2));
                villC.setChipStrokeColor(getContext().getColorStateList(R.color.blue_grad2));
                selectedChip = "villC";
                querySubmit(searchBox.getQuery().toString());
            }
        });
        nparkC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChips();
                nparkC.setChipIconTint(getContext().getColorStateList(R.color.blue_grad2));
                nparkC.setChipStrokeColor(getContext().getColorStateList(R.color.blue_grad2));
                selectedChip = "nparkC";
                querySubmit(searchBox.getQuery().toString());
            }
        });
        searchBox.setQuery("Sarajevo", false);
        querySubmit("Sarajevo");
        searchBox.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                querySubmit(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                querySubmit(newText);
                return false;
            }
        });


        getActivity().findViewById(R.id.loaderBox).setVisibility(View.GONE);
        getActivity().findViewById(R.id.fragmentProgBar).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.errImg).setVisibility(View.GONE);
        getActivity().findViewById(R.id.loaderError).setVisibility(View.GONE);
    }
    private void resetChips(){
        cityC.setChipIconTint(getContext().getColorStateList(R.color.gray));
        attrC.setChipIconTint(getContext().getColorStateList(R.color.gray));
        villC.setChipIconTint(getContext().getColorStateList(R.color.gray));
        nparkC.setChipIconTint(getContext().getColorStateList(R.color.gray));
        cityC.setChipStrokeColor(getContext().getColorStateList(R.color.white));
        attrC.setChipStrokeColor(getContext().getColorStateList(R.color.white));
        villC.setChipStrokeColor(getContext().getColorStateList(R.color.white));
        nparkC.setChipStrokeColor(getContext().getColorStateList(R.color.white));

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //Metoda za prikaz podataka,
    private void displayData( List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> data){

        exploreRes.setHasFixedSize(true);
        exploreRes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        exploreAdapter = new ExploreAdapter(getContext(), data);
        exploreRes.setAdapter(exploreAdapter);
        exploreRes.setVisibility(View.VISIBLE);
        messageBox.setVisibility(View.GONE);
    }
    private void querySubmit(String query){
        exploreRes.setVisibility(View.GONE);
        messageBox.setVisibility(View.VISIBLE);
        exploreProgress.setVisibility(View.VISIBLE);
        exploreMessage.setVisibility(View.GONE);
        if(query != null){

            //Čišcenje arraya
            if (data.size() != 0){
                data.clear();
            }

            //Provjera koji je chip selectovan
            switch (selectedChip){
                case ("cityC"):{

                    //Dobavljanje podataka
                    data = liteCityDataHelper.exploreQuery(query);

                    if (data.size() != 0){

                        //Prikaz podataka
                        displayData(data);

                    }else{

                        //Nema podataka, prikaz errora
                        setErr("noData");

                    }
                    break;
                }
                case ("attrC"):{

                    //Dobavljanje podataka
                    data = liteAttractionDataHelper.exploreQuery(query);

                    if (data.size() != 0){

                        //Prikaz podataka
                        displayData(data);

                    }else{

                        //Nema podataka, prikaz errora
                        setErr("noData");

                    }
                    break;
                }
                case ("villC"):{

                    //Dobavljanje podataka
                    data = liteVillageDataHelper.exploreQuery(query);

                    if (data.size() != 0){

                        //Prikaz podataka
                        displayData(data);

                    }else{

                        //Nema podataka, prikaz errora
                        setErr("noData");

                    }
                    break;
                }
                case ("nparkC"):{

                    //Dobavljanje podataka
                    data = liteNParkDataHelper.exploreQuery(query);

                    if (data.size() != 0){

                        //Prikaz podataka
                        displayData(data);

                    }else{

                        //Nema podataka, prikaz errora
                        setErr("noData");

                    }
                    break;
                }
            }
        }else{

            setErr("noData");

        }
    }

    private void setErr(String errCode) {
        switch (errCode){

            case ("noData"):{
                //Nema rezultata pretrage.
                exploreRes.setVisibility(View.GONE);
                messageBox.setVisibility(View.VISIBLE);
                exploreMessage.setText(getContext().getString(R.string.eNothingFound));
                exploreMessage.setVisibility(View.VISIBLE);
                exploreProgress.setVisibility(View.GONE);
                break;
            }


        }

    }
}