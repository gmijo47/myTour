package com.gmijo.mytour;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity{
    //Inicijaliziranje elemenata
    EditText rUsername, rEmail, rPassword, rPasswordRepeat;
    Button rBtn;
    ProgressBar rProgressBar;
    TextView rErrorMsg, rTerms;
    //FireBase inicijaliziranje
    FirebaseAuth firebaseAuth;
    String rEmailData, rUsernameData, rPasswordData, rPasswordRepeatData;

    //Broji errore, kada dođe do 4 izbacuje dijalog o pravilima prilikom registracije
    int errCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Postavljanje boje nanvigacijskih gumbova
        getWindow().setNavigationBarColor(getColor(R.color.white));
        //TODO ubaciti btn za back
        //Dodjelivanje elemenata "varijablama"
        rUsername = (EditText) findViewById(R.id.RegisterUsername);

        rEmail = (EditText) findViewById(R.id.RegisterEmail);

        rPassword = (EditText) findViewById(R.id.RegisterPassword);

        rPasswordRepeat = (EditText) findViewById(R.id.RegisterPasswordAgain);

        rBtn = (Button) findViewById(R.id.RegisterBtn);
        rProgressBar = (ProgressBar) findViewById(R.id.RegisterProgressBar);

        rErrorMsg = (TextView) findViewById(R.id.RegisterErrorMessage);
        rTerms = (TextView) findViewById(R.id.RegisterTermsOfUse);

        firebaseAuth.getInstance();



        rBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rUsernameData = rUsername.getText().toString().trim();
                rEmailData = rEmail.getText().toString().trim();
                rPasswordData = rPassword.getText().toString().trim();
                rPasswordRepeatData = rPasswordRepeat.getText().toString().trim();

                if (rUsernameData.isEmpty() || rUsernameData.length() <= 4){
                    rUsername.setBackgroundResource(R.drawable.text_field_error);
                    rErrorMsg.setText(R.string.errUsername ); //TODO sting u fajl
                    rErrorMsg.setVisibility(View.VISIBLE);
                    errCounter++;

                } else if (rEmailData.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(rEmailData).matches()){
                    rEmail.setBackgroundResource(R.drawable.text_field_error);
                    rErrorMsg.setText(R.string.errEmail); //TODO sting u fajl
                    rErrorMsg.setVisibility(View.VISIBLE);
                    errCounter++;

                } else if (rPasswordData.length() <=8 || rPasswordData.isEmpty() || rPasswordRepeatData.isEmpty() || !rPasswordData.matches(rPasswordRepeatData)){
                    rPassword.setBackgroundResource(R.drawable.text_field_error);
                    rPasswordRepeat.setBackgroundResource(R.drawable.text_field_error);
                    rErrorMsg.setText(R.string.errPass); //TODO sting u fajl
                    rErrorMsg.setVisibility(View.VISIBLE);
                    errCounter++;

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rUsername.setBackgroundResource(R.drawable.text_field);
                        rEmail.setBackgroundResource(R.drawable.text_field);
                        rPassword.setBackgroundResource(R.drawable.text_field);
                        rPasswordRepeat.setBackgroundResource(R.drawable.text_field);
                        rErrorMsg.setVisibility(View.INVISIBLE);
                    }
                }, 3200);
                if (errCounter >= 4){
                    showRegTipFragment();
                }
            }


        });
    }
    public void showRegTipFragment(){
        rDataTips rDataTips = new rDataTips();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        rDataTips.show(fragmentTransaction, "");
        errCounter = 0;
    }




    }
