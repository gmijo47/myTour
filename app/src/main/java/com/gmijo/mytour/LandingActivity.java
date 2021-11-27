package com.gmijo.mytour;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gmijo.mytour.databinding.ActivityLandingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LandingActivity extends AppCompatActivity {

    private ActivityLandingBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    TextView errTxt;
    ImageView errImg;
    ConstraintLayout loaderBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLandingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.fragmentProgBar);
        loaderBox = (ConstraintLayout) findViewById(R.id.loaderBox);
        errTxt = (TextView) findViewById(R.id.loaderError);
        errImg = (ImageView) findViewById(R.id.errImg);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_leaderboard, R.id.navigation_explore, R.id.navigation_map, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_landing);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                errImg.setVisibility(View.GONE);
                errTxt.setVisibility(View.GONE);
                loaderBox.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        //Dobavljanje firebase usera
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Porjera da li korisnik postoji u bazi, te shodno tome statrovanje activitja
        if (firebaseUser != null) {
            firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), R.string.errSessionExipired, Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        startActivity(new Intent(LandingActivity.this, Login.class));
                        finish();
                    }

                }
            });

        }else {
                Toast.makeText(getApplicationContext(), R.string.errSessionExipired, Toast.LENGTH_LONG).show();
                startActivity(new Intent(LandingActivity.this, Login.class));
                finish();
        }
    }
}