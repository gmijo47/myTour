package com.gmijo.mytour.ui.pocetna;

import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteAttractionDataHelper;
import com.gmijo.mytour.database.SQLiteCityDataHelper;
import com.gmijo.mytour.database.SQLiteNParkController;
import com.gmijo.mytour.database.SQLiteNParkDataHelper;
import com.gmijo.mytour.database.SQLiteVillageDataHelper;

import static com.gmijo.mytour.database.SQLiteAttractionController.*;
import static com.gmijo.mytour.database.SQLiteCityController.*;
import static com.gmijo.mytour.database.SQLiteNParkController.NPARK_DATABASE_NAME;
import static com.gmijo.mytour.database.SQLiteVillageController.VILLAGE_DATABASE_NAME;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class PocetnaFragment extends Fragment {

    View view;
    RecyclerView cityFeatured, attractionFeatured, villageFeatured, nParkFeatured;
    FeaturedAttractionAdapter attractionAdapter;
    FeaturedCityAdapter featuredCityAdapter;
    FeaturedVillageAdapter featuredVillageAdapter;
    FeaturedNParkAdapter featuredNParkAdapter;
    List<Pair<Pair<String, String>, Pair<String, Pair<String, String>>>> dataCity = new ArrayList<>();
    List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> dataAttraction, dataVillage, dataNPark = new ArrayList<>();
    SQLiteCityDataHelper liteCityDataHelper;
    SQLiteAttractionDataHelper liteAttractionDataHelper;
    SQLiteVillageDataHelper liteVillageDataHelper;
    SQLiteNParkDataHelper liteNParkDataHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_pocetna, container, false);
        liteCityDataHelper = new SQLiteCityDataHelper(getActivity());
        liteAttractionDataHelper = new SQLiteAttractionDataHelper(getActivity());
        liteVillageDataHelper = new SQLiteVillageDataHelper(getActivity());
        liteNParkDataHelper = new SQLiteNParkDataHelper(getActivity());
        cityFeatured = (RecyclerView) view.findViewById(R.id.fCityRecyclerView);
        attractionFeatured = (RecyclerView) view.findViewById(R.id.fAttractionRecyclerView);
        villageFeatured = (RecyclerView) view.findViewById(R.id.fVillageRecyclerView);
        nParkFeatured = (RecyclerView) view.findViewById(R.id.fNParkRecyclerView);

         return  view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       //Kopiranje baze iz assetsa, u /data/data/com.gmijo.mytour/databases
       try{

           //Dobavljanje IO streamova
           InputStream openRawCityResource = getActivity().getAssets().open("raw.db");
           FileOutputStream fileCityOutputStream = new FileOutputStream(getActivity().getDatabasePath(CITY_DATABASE_NAME));

           //Kopira po 1024 byta istovremeno, loopuje sve dok ne prođe kroz sve
           byte[] bArr = new byte[1024];
           while (true) {
               int read = openRawCityResource.read(bArr);
               if (read <= 0) {
                   break;
               }
               fileCityOutputStream.write(bArr, 0, read);
           }

           //Flushuje O stream, pa zatvara IO streamove
           fileCityOutputStream.flush();
           fileCityOutputStream.close();
           openRawCityResource.close();

       } catch (IOException e) {

           //Error
           Log.e("Error", String.valueOf(e));
           setErr();
      }

            try{

                dataCity = liteCityDataHelper.getData();

            }catch (Exception e){

                //Error
                Log.e("Error", String.valueOf(e));
                setErr();

            }
        if (dataCity != null){

            try {

                //Isto kod kao i gore iznad samo se izvršava nad drugom bazom
                InputStream openRawAttractionResource = getActivity().getAssets().open("rawatrakcije.db");
                FileOutputStream fileAttractionOutputStream = new FileOutputStream(getActivity().getDatabasePath(ATTRACTION_DATABASE_NAME));

                //Kopira po 1024 byta istovremeno, loopuje sve dok ne prođe kroz sve
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = openRawAttractionResource.read(bArr);
                    if (read <= 0) {
                        break;
                    }
                    fileAttractionOutputStream.write(bArr, 0, read);
                }


                fileAttractionOutputStream.flush();
                fileAttractionOutputStream.close();
                openRawAttractionResource.close();

            } catch (IOException e) {

                //Error
                Log.e("Error", String.valueOf(e));
                setErr();

            }
            try {

                dataAttraction = liteAttractionDataHelper.getData();
                if (dataAttraction != null){

                    try {

                        //Isto kod kao i gore iznad samo se izvršava nad drugom bazom
                        InputStream openRawVillageResource = getActivity().getAssets().open("rawsela.db");
                        FileOutputStream fileVillageOutputStream = new FileOutputStream(getActivity().getDatabasePath(VILLAGE_DATABASE_NAME));

                        //Kopira po 1024 byta istovremeno, loopuje sve dok ne prođe kroz sve
                        byte[] bArr = new byte[1024];
                        while (true) {
                            int read = openRawVillageResource.read(bArr);
                            if (read <= 0) {
                                break;
                            }
                            fileVillageOutputStream.write(bArr, 0, read);
                        }


                        fileVillageOutputStream.flush();
                        fileVillageOutputStream.close();
                        openRawVillageResource.close();

                    } catch (IOException e) {

                        //Error
                        Log.e("Error", String.valueOf(e));
                        setErr();

                    }
                    try{

                        dataVillage = liteVillageDataHelper.getData();

                    }catch (Exception e){

                        //Error
                        Log.e("Error", String.valueOf(e));
                        setErr();

                    }
                    if (dataVillage != null){

                        try {

                            //Isto kod kao i gore iznad samo se izvršava nad drugom bazom
                            InputStream openRawNParkResource = getActivity().getAssets().open("rawnpark.db");
                            FileOutputStream fileNParkOutputStream = new FileOutputStream(getActivity().getDatabasePath(NPARK_DATABASE_NAME));

                            //Kopira po 1024 byta istovremeno, loopuje sve dok ne prođe kroz sve
                            byte[] bArr = new byte[1024];
                            while (true) {
                                int read = openRawNParkResource.read(bArr);
                                if (read <= 0) {
                                    break;
                                }
                                fileNParkOutputStream.write(bArr, 0, read);
                            }


                            fileNParkOutputStream.flush();
                            fileNParkOutputStream.close();
                            openRawNParkResource.close();

                        } catch (IOException e) {

                            //Error
                            Log.e("Error", String.valueOf(e));
                            setErr();

                        }
                        try{

                            dataNPark = liteNParkDataHelper.getData();

                        }catch (Exception e){

                            //Error
                            Log.e("Error", String.valueOf(e));
                            setErr();

                        }
                        if (dataNPark != null){

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().findViewById(R.id.loaderBox).setVisibility(View.GONE);
                                    getActivity().findViewById(R.id.fragmentProgBar).setVisibility(View.VISIBLE);
                                    getActivity().findViewById(R.id.errImg).setVisibility(View.GONE);
                                    getActivity().findViewById(R.id.loaderError).setVisibility(View.GONE);

                                    displayData();
                                }
                            }, 3000);

                        }

                    }


                }else {
                    //Error
                   setErr();
                }
            }catch (Exception e){

                //Error
                Log.e("Error", String.valueOf(e));
                setErr();
            }
        }else {

            //Error
            setErr();

        }
    }

    private void displayData() {

        cityFeatured.setHasFixedSize(true);
        cityFeatured.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredCityAdapter = new FeaturedCityAdapter(getContext(), dataCity);
        cityFeatured.setAdapter(featuredCityAdapter);

        attractionFeatured.setHasFixedSize(true);
        attractionFeatured.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        attractionAdapter = new FeaturedAttractionAdapter(getContext(), dataAttraction);
        attractionFeatured.setAdapter(attractionAdapter);

        villageFeatured.setHasFixedSize(true);
        villageFeatured.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredVillageAdapter = new FeaturedVillageAdapter(getContext(), dataVillage);
        villageFeatured.setAdapter(featuredVillageAdapter);

        nParkFeatured.setHasFixedSize(true);
        nParkFeatured.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        featuredNParkAdapter = new FeaturedNParkAdapter(getContext(), dataNPark);
        nParkFeatured.setAdapter(featuredNParkAdapter);

    }
    private void setErr(){
        getActivity().findViewById(R.id.loaderBox).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.fragmentProgBar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.errImg).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.loaderError).setVisibility(View.VISIBLE);
    }
}