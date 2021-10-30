package com.gmijo.mytour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;



//SplashScreen Acitivity

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        //Postavljanje orijentacije ekrana na prortret (vertikalno)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Postavljanje boje nanvigacijskih gumbova
        getWindow().setNavigationBarColor(getColor(R.color.black_grad));

        //Animacija, getovanje slika
        ImageView planeta = (ImageView) findViewById(R.id.planeta),
                  bannermid = (ImageView) findViewById(R.id.bannermid);

        //Odabir i pokretanje animacije
        Animation fall_half = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fallhalf_down);
        planeta.startAnimation(fall_half);

        //Postavljanje lisenera na animacije, kako bi se znalo kada da sljedeca animacija pocne
        fall_half.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Pokretanje sljedece, nakon zavrsetka prethodne
                Animation bounce_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_up);
                planeta.startAnimation(bounce_up);
                bounce_up.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //Pokretanje sljedece, nakon zavrsetka prethodne
                        Animation bounce_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_down);
                        planeta.startAnimation(bounce_down);
                        bounce_down.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //Pokretanje sljedece, nakon zavrsetka prethodne
                                Animation fall_full = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fallfull_down);
                                planeta.startAnimation(fall_full);

                                Animation b_fall_half = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fallhalf_down_banner);
                                bannermid.startAnimation(b_fall_half);
                                b_fall_half.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        //Prebacivanje nakon odredenog delaya na drugi activity
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(SplashScreen.this, Login.class));
                                                finish();
                                            }
                                        }, 2800);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
