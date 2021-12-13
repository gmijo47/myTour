package com.gmijo.mytour.ui.profil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.gmijo.mytour.Login;
import com.gmijo.mytour.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity{
    ImageButton settingsBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        //Dobavljanje buttona
        settingsBackBtn = (ImageButton) findViewById(R.id.settingsBack);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        settingsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        //Inicijalizacija elemnata
        Preference delProfile;
        FirebaseFirestore firebaseFirestore;
        FirebaseUser firebaseUser;
        FirebaseAuth firebaseAuth;


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            //Dobavljanje firebase-a
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            firebaseFirestore = FirebaseFirestore.getInstance();

            //Dobavljanje preference, i lisener na istu
            delProfile = (Preference) findPreference("delProfile");
            delProfile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    //Brisanje podataka u bazi
                    firebaseFirestore.collection("users").document(firebaseUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                //Brisanje samog usera
                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            //Kreiranje novog intenta, i završavanje svih activija, i statovanje logina
                                            Intent rs = new Intent(getActivity(), Login.class);
                                            getActivity().finishAffinity();
                                            startActivity(rs);
                                        }
                                    }
                                });
                            }
                        }
                    });

                    return false;
                }
            });
        }
    }
}