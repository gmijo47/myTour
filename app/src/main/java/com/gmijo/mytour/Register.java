package com.gmijo.mytour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;


public class Register extends AppCompatActivity{
    //Inicijaliziranje i definisanje elemenata
    EditText rUsername, rEmail, rPassword, rPasswordRepeat;
    Button rBtn;
    ProgressBar rProgressBar;
    TextView rErrorMsg, rTerms;
    String rEmailData, rUsernameData, rPasswordData, rPasswordRepeatData, errUnknownCode;

    //FireBaseAuth i FirebaseUser inicijaliziranje
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    //Broji errore
    int errCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Postavljanje boje nanvigacijskih gumbova
        getWindow().setNavigationBarColor(getColor(R.color.white));

        //Dodjelivanje elemenata "varijablama"
        rUsername = (EditText) findViewById(R.id.RegisterUsername);

        rEmail = (EditText) findViewById(R.id.RegisterEmail);

        rPassword = (EditText) findViewById(R.id.RegisterPassword);

        rPasswordRepeat = (EditText) findViewById(R.id.RegisterPasswordAgain);

        rBtn = (Button) findViewById(R.id.RegisterBtn);
        rProgressBar = (ProgressBar) findViewById(R.id.RegisterProgressBar);

        rErrorMsg = (TextView) findViewById(R.id.RegisterErrorMessage);
        rTerms = (TextView) findViewById(R.id.RegisterTermsOfUse);

        //Dobavljanje firebaseAuth-a
        firebaseAuth = FirebaseAuth.getInstance();


        //Lisener na register button
        rBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rUsernameData = rUsername.getText().toString().trim();
                rEmailData = rEmail.getText().toString().trim();
                rPasswordData = rPassword.getText().toString().trim();
                rPasswordRepeatData = rPasswordRepeat.getText().toString().trim();

                if (rUsernameData.isEmpty() || rUsernameData.length() < 4){

                    //Error sa korisničkim imenom
                    setError("errUsername");

                } else if (rEmailData.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(rEmailData).matches()){

                    //Error sa emailom
                    setError("errEmail");

                } else if (rPasswordData.length() <=8 || rPasswordData.isEmpty() || rPasswordRepeatData.isEmpty() || !rPasswordData.matches(rPasswordRepeatData)){

                    //Error sa lozinkom (passwordom)
                    setError("errPassword");


                }else {
                    rProgressBar.setVisibility(View.VISIBLE);
                    //Firebase kreiranje naloga i onComplete (kada USPJEŠNO završi kreiranje)...
                    firebaseAuth.createUserWithEmailAndPassword(rEmailData, rPasswordData)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //Nakon uspješnog kreiranja, getuje korisnika i šalje mu verifikacioni email
                                            firebaseUser = firebaseAuth.getCurrentUser();
                                            if (firebaseUser != null){
                                                    firebaseUser.sendEmailVerification().addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {

                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            //Nakon USPJEŠNO poslatog emaila delaya 3000ms i prebacuje na Login actitvity, te šalje poruku da je nalog uspješno kreiran
                                                            Toast.makeText(getApplicationContext(), R.string.rAccCreated, Toast.LENGTH_LONG).show();
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    startActivity(new Intent(Register.this, Login.class));
                                                                    finish();
                                                                }
                                                            }, 3000);
                                                            }
                                                    });
                                            }
                                    //Hendolovanje exceptiona
                                    } else {
                                        //Exception za multinalog (nalog je već registrovan)
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException){

                                            setError("errUserAlreadyExist");
                                            rProgressBar.setVisibility(View.INVISIBLE);

                                            //Exception za internet (nema internetske veze)
                                        } else if(task.getException() instanceof FirebaseNetworkException){

                                            setError("errConnection");

                                            //Svi ostali exceptioni
                                        }else {

                                            errUnknownCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                            setError("errUnknownCode");

                                        }

                                    }
                                }
                            });
                }
                //Za brojanje erora, te prikaz tips fragmenta
                if (errCounter >= 4){
                    showRegTipFragment();
                }
            }
        });
    }
    //Prikaz fragmenta za tips
    public void showRegTipFragment() {
        rDataTips rDataTips = new rDataTips();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        rDataTips.show(fragmentTransaction, "");
        errCounter = 0;
    }
    // Provjera da li je na startu (početku) trenutni korisnik prijavljen(logovan) ili nije odnosno korisnik je null.
    public void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
        }
    }
    //Postavljanje errora, odnoso njihov UI prikaz
    public void setError(String errCode, int timeOut){
        switch (errCode){
            //Problem sa username
            case "errUsername": {
                rUsername.setBackgroundResource(R.drawable.text_field_error);
                rErrorMsg.setText(R.string.errUsername );
                rErrorMsg.setVisibility(View.VISIBLE);
                errCounter++;
                break;
            }
            //Problem sa emailom
            case "errEmail":{
                rEmail.setBackgroundResource(R.drawable.text_field_error);
                rErrorMsg.setText(R.string.errEmail);
                rErrorMsg.setVisibility(View.VISIBLE);
                errCounter++;
                break;
            }
            //Problem sa lozinkom
            case "errPassword":{
                rPassword.setBackgroundResource(R.drawable.text_field_error);
                rPasswordRepeat.setBackgroundResource(R.drawable.text_field_error);
                rErrorMsg.setText(R.string.errPass);
                rErrorMsg.setVisibility(View.VISIBLE);
                errCounter++;
                break;
            }
            //Problem sa multinalogm (korisnik već postoji)
            case "errUserAlreadyExist":{
                rEmail.setBackgroundResource(R.drawable.text_field_error);
                rErrorMsg.setText(R.string.errUserAlreadyExist);
                rErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Problem sa internetskom vezom
            case "errConnection":{
                rErrorMsg.setText(R.string.errConn);
                rErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Svi ostali errori odnosno exceptioni
            case "errUnknownCode":{
                rErrorMsg.setText(errUnknownCode);
                rErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
        }
        //Čišćenje, odnoso rollbackovanje UI na default nakon određeno timeouta
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rUsername.setBackgroundResource(R.drawable.text_field);
                rEmail.setBackgroundResource(R.drawable.text_field);
                rPassword.setBackgroundResource(R.drawable.text_field);
                rPasswordRepeat.setBackgroundResource(R.drawable.text_field);
                rProgressBar.setVisibility(View.INVISIBLE);
                rErrorMsg.setVisibility(View.INVISIBLE);
            }
        }, timeOut);
    }
    //Metod overloading
    public void setError(String errCode){
        setError(errCode, 3200);
    }
}
