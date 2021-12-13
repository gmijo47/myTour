package com.gmijo.mytour.ui.profil;

import java.util.ArrayList;

public interface interfaceProfilFragment {
    void displayData(ArrayList<String> data);

    void ErrDialog(String state);

    void setError(String errFailedToGetData);

}
