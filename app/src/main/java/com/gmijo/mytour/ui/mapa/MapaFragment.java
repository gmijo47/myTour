package com.gmijo.mytour.ui.mapa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gmijo.mytour.R;

public class MapaFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View kom = inflater.inflate(R.layout.fragment_mapa, container, false);

        return  kom;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}