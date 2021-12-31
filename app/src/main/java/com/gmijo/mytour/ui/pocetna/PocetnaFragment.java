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
import com.gmijo.mytour.database.SQLiteCityDataHelper;

import static com.gmijo.mytour.database.SQLiteCityController.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class PocetnaFragment extends Fragment {

    View view;
    RecyclerView cityFeatured, natureFeatured;
    FeaturedAdapter featuredAdapter;
    List<Pair<Pair<String, String>, Pair<String, String>>> dataCity = new ArrayList<>();
    SQLiteCityDataHelper liteCityDataHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_pocetna, container, false);
        liteCityDataHelper = new SQLiteCityDataHelper(getActivity());
        cityFeatured = (RecyclerView) view.findViewById(R.id.fCityRecyclerView);
         return  view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().findViewById(R.id.loaderBox).setVisibility(View.GONE);
                getActivity().findViewById(R.id.fragmentProgBar).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.errImg).setVisibility(View.GONE);
                getActivity().findViewById(R.id.loaderError).setVisibility(View.GONE);
            }
        }, 2000);

       //Kopiranje baze iz assetsa, u /data/data/com.gmijo.mytour/databases
       try{

           //Dobavljanje IO streamova
           InputStream openRawResource = getActivity().getAssets().open("raw.db");
           FileOutputStream fileOutputStream = new FileOutputStream(getActivity().getDatabasePath(DATABASE_NAME));

           //Kopira po 1024 byta istovremeno, loopuje sve dok ne prođe kroz sve
           byte[] bArr = new byte[1024];
           while (true) {
               int read = openRawResource.read(bArr);
               if (read <= 0) {
                   break;
               }
               fileOutputStream.write(bArr, 0, read);
           }

           //Flushuje O stream, pa zatvara IO streamove
           fileOutputStream.flush();
           fileOutputStream.close();
           openRawResource.close();

       } catch (IOException e) {

          //Error
              Log.e("Error", String.valueOf(e));
      }

            try{

                dataCity = liteCityDataHelper.getData();

            }catch (Exception e){

                Log.e("Error", String.valueOf(e));
            }
        if (dataCity != null){
            displayData();
        }else {

        }
    }

    private void displayData() {

        cityFeatured.setHasFixedSize(true);
        cityFeatured.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredAdapter = new FeaturedAdapter(getContext(), dataCity);
        cityFeatured.setAdapter(featuredAdapter);

    }
}