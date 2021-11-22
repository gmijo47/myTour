package com.gmijo.mytour.ui.profil;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.gmijo.mytour.Login;
import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteDataHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class ProfilFragment extends Fragment {

    //Inicijaliziranje i definisanje elemenata
    ScrollView userProfile, userStats;
    ImageButton userProfileBtn, userStatsBtn, userSettingsBtn, userLogOutBtn,
            userVerifyBadgeBtn, userAdminBadgeBtn, userDeveloperBadgeBtn, userEditProfileBtn;
    TextView userFullName, userUsername, profileFullName, profileUsername, userEmail, userPhone, userGroup, statsTokenCount,
            statsCityExploredCount, statsVillageExploredCount, statsNaturePointExplored, statsNationalParkExplored;

    //Kada je 1 selektovan userinfo, kada je 2 stats
    int selected = 1;

    View view;


    String userUUID, userEmailData = null, usernameData, fullNameData, groupData, tokenCount, cityExplored, villageExplored, naturePointExplored, nationalParkExplored;
    SQLiteDataHelper liteDataHelper;

    //FireBaseAuth i FirebaseUser, FirebaseFireStore inicijaliziranje
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profil, container, false);
        getElements();
        //FirebaseAuth, FirebaseFirestore
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Dobijanje korisničkog emaila
        userEmailData = firebaseUser.getEmail();
        userUUID = firebaseUser.getUid();

        //Prosljeđivanje konteksta u SQLiteController
        liteDataHelper = new SQLiteDataHelper(getActivity());


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Pozivanje metode za provjeru da li postoje podatci o korisniku u lokalnoj bazi
        boolean locUserExists = liteDataHelper.checkLocalData(userUUID);

        if (locUserExists) {
            ArrayList<String> data = liteDataHelper.getData(userUUID);
                if (data != null){

                        displayData(data);

                }


        } else {
            //Podatci ne postoje
            syncCloudLocal(userUUID);

        }

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
        userSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        userLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
                Toast.makeText(getActivity().getApplicationContext(), "Odjavljeni ste", Toast.LENGTH_LONG).show();
            }
        });
        userEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });
    }
    public void displayData(ArrayList<String> data){
       Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Mijo", "etoda je pozvan");
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
                    //Phone
                    if (firebaseUser.getPhoneNumber() != null) {
                        userPhone.setText(firebaseUser.getPhoneNumber());
                        userVerifyBadgeBtn.setVisibility(View.VISIBLE);
                    } else {
                        userPhone.setText(R.string.phoneNVerify);
                    }
//
                    //Group
                    userGroup.setText(data.get(2));
//
                    //Stats
                    statsCityExploredCount.setText(data.get(3));
                    statsVillageExploredCount.setText(data.get(4));
                    statsNationalParkExplored.setText(data.get(5));
                    statsNaturePointExplored.setText(data.get(6));
                    statsTokenCount.setText(data.get(7));

                }
            }
        });

        thread.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

        public void setError(String errCode){
        switch (errCode){
            case "errFailedToGetData":{
                //Snackbar
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
                        Object objFullName = documentSnapshot.get("personalData.FullName");
                        if (fullNameData == null) {
                            fullNameData = "";
                        }else {
                            fullNameData = objFullName.toString();
                        }
                        cityExplored = documentSnapshot.get("achievementData.cityExplored").toString();
                        nationalParkExplored = documentSnapshot.get("achievementData.nationalParkExplored").toString();
                        naturePointExplored = documentSnapshot.get("achievementData.naturepointExplored").toString();
                        villageExplored = documentSnapshot.get("achievementData.villageExplored").toString();
                        tokenCount = documentSnapshot.get("achievementData.myTourTokens").toString();
                        groupData = documentSnapshot.get("personalData.userType").toString();

                        liteDataHelper.registerUser(userUUID, fullNameData, usernameData, Integer.parseInt(cityExplored),
                                Integer.parseInt(nationalParkExplored), Integer.parseInt(naturePointExplored),
                                Integer.parseInt(villageExplored), Integer.parseInt(tokenCount), groupData);



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
    public void getElements(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                userProfile = (ScrollView) view.findViewById(R.id.viewPersonalInfo);
                userStats = (ScrollView) view.findViewById(R.id.viewPersonalStats);

                //Dugmći
                userProfileBtn = (ImageButton) view.findViewById(R.id.profileInfo);
                userStatsBtn = (ImageButton) view.findViewById(R.id.profileStats);
                userSettingsBtn = (ImageButton) view.findViewById(R.id.profileSettings);
                userLogOutBtn = (ImageButton) view.findViewById(R.id.profileLogOut);

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

                userVerifyBadgeBtn = (ImageButton)  view.findViewById(R.id.profileVerifyBadge);
                userAdminBadgeBtn = (ImageButton) view.findViewById(R.id.profileAdminBadge);
                userDeveloperBadgeBtn = (ImageButton) view.findViewById(R.id.profileDeveloperBadge);

                userEditProfileBtn = (ImageButton) view.findViewById(R.id.profileEdit);
            }
        });
        thread.start();
    }

}