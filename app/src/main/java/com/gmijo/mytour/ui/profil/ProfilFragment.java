package com.gmijo.mytour.ui.profil;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gmijo.mytour.Login;
import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteDataHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class ProfilFragment extends Fragment implements interfaceProfilFragment {

    //Inicijaliziranje i definisanje elemenata
    ScrollView userProfile, userStats;
    ImageButton userProfileBtn, userStatsBtn, userSettingsBtn, userLogOutBtn,
            userVerifyBadgeBtn, userAdminBadgeBtn, userDeveloperBadgeBtn, userEditProfileBtn;
    TextView userFullName, userUsername, profileFullName, profileUsername, userEmail, userPhone, userGroup, statsTokenCount,
            statsCityExploredCount, statsVillageExploredCount, statsNaturePointExplored, statsNationalParkExplored;
    //Kada je 1 selektovan userinfo, kada je 2 stats
    int selected = 1;
    //Swipe refresh
    SwipeRefreshLayout swipeRefreshLayout;

    //Snackbar
    Snackbar snackbarData, snackbarConn;

    View view = null;
    //Thread (runovanje kreiranja view-a)
    Thread t;

    //Stringovi za polja
    String userUUID, userEmailData = null, usernameData, fullNameData, groupData, tokenCount, cityExplored, villageExplored, naturePointExplored, nationalParkExplored;
   //Array list za podatke
    ArrayList<String> dataR;
    //Helper SQLite
    SQLiteDataHelper liteDataHelper;

    //FireBaseAuth i FirebaseUser, FirebaseFireStore inicijaliziranje
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Otvaranje threada
       t = new Thread(new Runnable() {
            @Override
            public void run() {
                //Kreiranje fragmenta
                view = inflater.inflate(R.layout.fragment_profil, container, false);
                try {
                    getElements();
                } catch (Exception e) {
                    ErrDialog("setErr");
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            ErrDialog("setErr");
        }

        return view;
    }

    //Dobavljanje konteksta za interface
    @Override
    public void onResume() {
        super.onResume();
        liteDataHelper.setDisplayer(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        liteDataHelper.setDisplayer(null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Pozivanje metode za provjeru da li postoje podatci o korisniku u lokalnoj bazi
        boolean locUserExists = liteDataHelper.checkLocalData(userUUID);
        if (locUserExists) {
           dataR = liteDataHelper.getData(userUUID);
                if (dataR != null){

                    //Podatci postoje, prikaz istih
                    displayData(dataR);

                }
        } else {

            //Podatci ne postoje
            syncCloudLocal(userUUID);

        }

        //Lisener na button za informacije o korisniku
        userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected == 2) {
                    userStats.setVisibility(View.GONE);
                    userStatsBtn.setImageResource(R.drawable.stats_def_icon);
                    userProfile.setVisibility(View.VISIBLE);
                    userProfileBtn.setImageResource(R.drawable.personalinfo_selected_icon);
                    selected = 1;
                }
            }
        });

        //Lisener na button za statistiku korisnika
        userStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected == 1) {
                    userProfile.setVisibility(View.GONE);
                    userProfileBtn.setImageResource(R.drawable.personalinfo_def_icon);
                    userStats.setVisibility(View.VISIBLE);
                    userStatsBtn.setImageResource(R.drawable.stats_selected_icon);
                    selected = 2;
                }
            }
        });

        //Lisener za postavke (otvaranje)
        userSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons();
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        //Lisener za odjavu (logout)
        userLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
                Snackbar.make(getView(), R.string.errLoader, Snackbar.LENGTH_SHORT).show();
            }
        });

        //Lisener za uređivanje korisničkog profila
        userEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons();
                Intent editProfileIntent = new Intent(getActivity(), EditProfile.class);
                editProfileIntent.putStringArrayListExtra("data", dataR);
                startActivityForResult(editProfileIntent, 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setMinimumHeight(30);
                syncCloudLocal(userUUID);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                boolean dataChanged = data.getBooleanExtra("dataChanged", false);
                if (dataChanged){
                    syncCloudLocal(userUUID);
                }

            }
        }
    }
    //Metoda za prikaz podataka
    public void displayData(ArrayList<String> data){
                if (data.size() != 0) {
                    profileUsername.setText(data.get(0));
                    userUsername.setText(data.get(0));
//
                    //FullName
                    profileFullName.setText(data.get(1));
                    userFullName.setText(data.get(1));
//
                    //Email
                    userEmail.setText(firebaseUser.getEmail());
//
                    //Phone (Ukoliko korisnik ima verifikovan telefon, prikazuje mu badge)
                    if (firebaseUser.getPhoneNumber() != null) {
                        userPhone.setText(firebaseUser.getPhoneNumber());
                        userVerifyBadgeBtn.setVisibility(View.VISIBLE);
                    } else {
                        userPhone.setText(R.string.phoneNVerify);
                        userVerifyBadgeBtn.setVisibility(View.GONE);
                    }
//
                    //Group (Ukoliko korisnik ima grupu administrator_2 ili 1 shodno tome prikazuje mu badge)
                    String str = data.get(2);
                    if (str.matches("Administrator_2")) {
                        userAdminBadgeBtn.setVisibility(View.VISIBLE);
                        userDeveloperBadgeBtn.setVisibility(View.VISIBLE);
                        String[] ugr = str.split("_");
                        userGroup.setText(ugr[0]);
                    } else if (str.matches("Adminstrator_1")) {
                        userAdminBadgeBtn.setVisibility(View.VISIBLE);
                        String[] ugr = str.split("_");
                        userGroup.setText(ugr[0]);
                    } else {
                        userGroup.setText(data.get(2));
                    }

//
                    //Stats
                    statsCityExploredCount.setText(data.get(3));
                    statsVillageExploredCount.setText(data.get(4));
                    statsNationalParkExplored.setText(data.get(5));
                    statsNaturePointExplored.setText(data.get(6));
                    statsTokenCount.setText(data.get(7));

                    //Nakon setovanja sklanja loader
                    ErrDialog("rsState");
                    swipeRefreshLayout.setRefreshing(false);

                    dataR = data;
                }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //Metoda za setovanje errora
    public void setError(String errCode){ switch (errCode){
        case "errFailedToGetData":{
            snackbarData = (Snackbar) Snackbar.make(getView(), R.string.errDataLoading, 6000).setAction(R.string.pRef, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    syncCloudLocal(userUUID);
                    snackbarData.dismiss();
                }
            });
            snackbarData.setAnchorView(R.id.nav_view);
            snackbarData.show();
            break;
        }
        case "errConnection":{
            snackbarConn = (Snackbar) Snackbar.make(getView(), R.string.errConn, 6000).setAction(R.string.pRef, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    syncCloudLocal(userUUID);
                    snackbarConn.dismiss();

                }
            });
            snackbarConn.setAnchorView(R.id.nav_view);
            snackbarConn.show();
            break;
        }
        }
    }

    //Metoda sinhronizuje podatke sa clouda i one u lokalnoj bazi
    public void syncCloudLocal(String userUUID) {
        FirebaseFirestore.getInstance().collection("users").document(userUUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {

                        usernameData = documentSnapshot.get("personalData.Username").toString();
                        if(documentSnapshot.get("personalData.FullName") == null){
                           fullNameData = "";

                        }else {
                            fullNameData = documentSnapshot.get("personalData.FullName").toString();
                        }

                        cityExplored = documentSnapshot.get("achievementData.cityExplored").toString();
                        nationalParkExplored = documentSnapshot.get("achievementData.nationalParkExplored").toString();
                        naturePointExplored = documentSnapshot.get("achievementData.naturepointExplored").toString();
                        villageExplored = documentSnapshot.get("achievementData.villageExplored").toString();
                        tokenCount = documentSnapshot.get("achievementData.myTourTokens").toString();
                        groupData = documentSnapshot.get("personalData.userType").toString();

                        liteDataHelper.registerUser(userUUID, fullNameData, usernameData, Integer.parseInt(cityExplored),
                                Integer.parseInt(nationalParkExplored), Integer.parseInt(naturePointExplored),
                                Integer.parseInt(villageExplored), Integer.parseInt(tokenCount), groupData, true);
                    } else {
                        setError("errFailedToGetData");
                    }
               } else if(task.getException() instanceof FirebaseNetworkException) {

                    setError("errConnection");

                } else{
                    setError("errFailedToGetData");
                }
            }
        });
    }

    //Metoda za dobavljanje elemenata
    public void getElements() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //Dugmći
                userProfile = (ScrollView) view.findViewById(R.id.viewPersonalInfo);
                userStats = (ScrollView) view.findViewById(R.id.viewPersonalStats);
                userProfileBtn = (ImageButton) view.findViewById(R.id.profileInfo);
                userStatsBtn = (ImageButton) view.findViewById(R.id.profileStats);
                userSettingsBtn = (ImageButton) view.findViewById(R.id.profileSettings);
                userLogOutBtn = (ImageButton) view.findViewById(R.id.profileLogOut);
                userEditProfileBtn = (ImageButton) view.findViewById(R.id.profileEdit);

                //Polja
                profileFullName = (TextView) view.findViewById(R.id.pName);
                profileUsername = (TextView) view.findViewById(R.id.pUsername);

                userUsername = (TextView) view.findViewById(R.id.pInfoUsername);
                userFullName = (TextView) view.findViewById(R.id.pInfoName);
                userEmail = (TextView) view.findViewById(R.id.profileInfoEmail);
                userPhone = (TextView) view.findViewById(R.id.profileInfoPhone);
                userGroup = (TextView) view.findViewById(R.id.profileInfoGroup);

                statsTokenCount = (TextView) view.findViewById(R.id.statsPointsCount);
                statsCityExploredCount = (TextView) view.findViewById(R.id.statsCityExploredCount);
                statsVillageExploredCount = (TextView) view.findViewById(R.id.statsVillageExploredCount);
                statsNaturePointExplored = (TextView) view.findViewById(R.id.statsNaturePointExploredCount);
                statsNationalParkExplored = (TextView) view.findViewById(R.id.statsNacionalParkExploredCount);

                //Beđevi
                userVerifyBadgeBtn = (ImageButton)  view.findViewById(R.id.profileVerifyBadge);
                userAdminBadgeBtn = (ImageButton) view.findViewById(R.id.profileAdminBadge);
                userDeveloperBadgeBtn = (ImageButton) view.findViewById(R.id.profileDeveloperBadge);

                //Swipe refresh
                swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

                //FirebaseAuth, FirebaseFirestore
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();

                //Dobijanje korisničkog emaila
                userEmailData = firebaseUser.getEmail();
                userUUID = firebaseUser.getUid();

                liteDataHelper = new SQLiteDataHelper(getActivity());

            }
        });
        thread.start();
        thread.join();
    }

    //Metoda za prikaz/sklanjanje error/loading dialoga
    public void ErrDialog(String state){
        switch (state){
            case ("setErr"):{

                getActivity().findViewById(R.id.fragmentProgBar).setVisibility(View.GONE);
                getActivity().findViewById(R.id.errImg).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.loaderError).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.loaderBox).setVisibility(View.VISIBLE);
                break;
            }
            case ("rsState"):{

                getActivity().findViewById(R.id.loaderBox).setVisibility(View.GONE);
                getActivity().findViewById(R.id.fragmentProgBar).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.errImg).setVisibility(View.GONE);
                getActivity().findViewById(R.id.loaderError).setVisibility(View.GONE);

            }
        }
    }
    //Gasi buttone, radi prevencije bagovanja
    public void disableButtons() {
        userProfile.setEnabled(false);
        userStats.setEnabled(false);
        userProfileBtn.setEnabled(false);
        userStatsBtn.setEnabled(false);
        userSettingsBtn.setEnabled(false);
        userLogOutBtn.setEnabled(false);
        userEditProfileBtn.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                userProfile.setEnabled(true);
                userStats.setEnabled(true);
                userProfileBtn.setEnabled(true);
                userStatsBtn.setEnabled(true);
                userSettingsBtn.setEnabled(true);
                userLogOutBtn.setEnabled(true);
                userEditProfileBtn.setEnabled(true);
            }
        }, 500);

    }

}