package com.gmijo.mytour.ui.profil;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gmijo.mytour.LandingActivity;
import com.gmijo.mytour.Login;
import com.gmijo.mytour.R;
import com.gmijo.mytour.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

public class ProfilFragment extends Fragment {

    //Inicijaliziranje i definisanje elemenata
    ScrollView userProfile, userStats;
    ImageButton userProfileBtn, userStatsBtn, userSettingsBtn, userLogOutBtn,
            userVerifyBadgeBtn, userAdminBadgeBtn, userDeveloperBadgeBtn, userEditProfileBtn;
    TextView userFullName, userUsername, profileFullName, profileUsername, userEmail, userPhone, userGroup, statsTokenCount,
            statsCityExploredCount, statsVillageExploredCount, statsNaturePointExplored, statsNationalParkExplored;

    //Kada je 1 selektovan userinfo, kada je 2 stats
    int selected = 1;

    String userUUID;

    //FireBaseAuth i FirebaseUser, FirebaseFireStore inicijaliziranje
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        userProfile = (ScrollView) view.findViewById(R.id.viewPersonalInfo);
        userStats = (ScrollView) view.findViewById(R.id.viewPersonalStats);

        //Dugmći
        userProfileBtn = (ImageButton) view.findViewById(R.id.profileInfo);
        userStatsBtn = (ImageButton) view.findViewById(R.id.profileStats);
        userSettingsBtn = (ImageButton) view.findViewById(R.id.profileSettings);
        userLogOutBtn = (ImageButton) view.findViewById(R.id.profileLogOut);

        userVerifyBadgeBtn = (ImageButton)  view.findViewById(R.id.profileVerifyBadge);
        userAdminBadgeBtn = (ImageButton) view.findViewById(R.id.profileAdminBadge);
        userDeveloperBadgeBtn = (ImageButton) view.findViewById(R.id.profileDeveloperBadge);

        userEditProfileBtn = (ImageButton) view.findViewById(R.id.profileEdit);

        //Polja
        profileFullName = (TextView) view.findViewById(R.id.profileName);
        profileUsername = (TextView) view.findViewById(R.id.profileUsername);

        userUsername = (TextView) view.findViewById(R.id.profileInfoUsername);
        userFullName = (TextView) view.findViewById(R.id.profileInfoName);
        userEmail = (TextView) view.findViewById(R.id.profileInfoEmail);
        userPhone = (TextView) view.findViewById(R.id.profileInfoPhone);
        userGroup = (TextView) view.findViewById(R.id.profileInfoGroup);

        statsTokenCount = (TextView) view.findViewById(R.id.statsPointsCount);
        statsCityExploredCount = (TextView) view.findViewById(R.id.statsCityExploredCount);
        statsVillageExploredCount = (TextView) view.findViewById(R.id.statsVillageExploredCount);
        statsNaturePointExplored = (TextView) view.findViewById(R.id.statsNaturePointExploredCount);
        statsNationalParkExplored = (TextView) view.findViewById(R.id.statsNacionalParkExploredCount);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userUUID = firebaseUser.getUid();

        //Razmotriti neki cache ili savedpreference
        getData();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected == 1){
                    //Ne radi ništa jer je taj view vec
                }else if (selected == 2) {
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
                if (selected == 1){
                    userProfile.setVisibility(View.GONE);
                    userProfileBtn.setImageResource(R.drawable.personalinfo_def_icon);
                    userStats.setVisibility(View.VISIBLE);
                    userStatsBtn.setImageResource(R.drawable.stats_selected_icon);
                    selected = 2;
                }else if (selected == 2) {
                    //ne radi ništa
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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void getData(){
        DocumentReference documentReference =    firebaseFirestore.collection("users").document(userUUID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                profileFullName.setText(value.getString("personalData.fullName"));
                userFullName.setText(value.getString("personalData.fullName"));
                profileUsername.setText(value.getString("personalData.Username"));
                userUsername.setText(value.getString("personalData.Username"));
            }
        });
    }
}