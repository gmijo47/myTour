package com.gmijo.mytour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity  {

    //Inicijaliziranje i definisanje elemenata
    EditText lEmail, lPassword, lSenderEmail;
    Button lBtn, lBtnGoogle;
    TextView lForgotPassword, lRegisterRedirect, lErrorMsg;
    CheckBox lRemember;
    String lEmailData, lPasswordData, errUnknownCode;
    ProgressBar lProgressBar;
    Boolean canSendVerifyEmail = false;
    int antiBruteForece = 0;


    //FireBaseAuth i FirebaseUser inicijaliziranje
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Postavljanje boje nanvigacijskih gumbova
        getWindow().setNavigationBarColor(getColor(R.color.white));

        //Dodjelivanje elemenata "varijablama"
        lEmail = (EditText) findViewById(R.id.LoginEmail);

        lPassword=  (EditText) findViewById(R.id.LoginPassword);

        lRemember = (CheckBox) findViewById(R.id.LoginRememberPassword);

        lForgotPassword = (TextView) findViewById(R.id.LoginForgotPassword);

        lRegisterRedirect = (TextView) findViewById(R.id.LoginCreateACCRedirect);

        lErrorMsg = (TextView) findViewById(R.id.LoginErrorMessage);

        lBtn = (Button) findViewById(R.id.LoginBtn);

        lBtnGoogle = (Button) findViewById(R.id.LoginGoogleBtn);

        lProgressBar = (ProgressBar) findViewById(R.id.LoginProgressBar);

        //Dobavljanje firebaseAuth-a
        firebaseAuth = FirebaseAuth.getInstance();

        //Lisener za startanje ForgotPassworda
        lForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });
        //Lisener za startovanje Registera
    lRegisterRedirect.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Login.this, Register.class));
        }
    });

        //Lisener na poruku da nalog nije verifkiovan te slanje verifikacije
        lErrorMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canSendVerifyEmail && firebaseUser != null){
                lErrorMsg.setVisibility(View.INVISIBLE);
                lEmail.setBackgroundResource(R.drawable.text_field);
                lProgressBar.setVisibility(View.INVISIBLE);
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            setError("succVerifySent", 10000);
                        }else {
                            setError("errVerifyNotSent", 10000);
                        }
                    }
                });
                }
            }
        });

    //Lisener na Login button
    lBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lEmailData = lEmail.getText().toString().trim();
            lPasswordData = lPassword.getText().toString().trim();

        if (lEmailData.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(lEmailData).matches()){

            //Error sa emailom
            setError("errEmail");

        } else if (lPassword.length() <=8 || lPasswordData.isEmpty()){

            //Error sa lozinkom (passwordom)
            setError("errPassword");

        }else if(antiBruteForece > 3){

            //Protiv brutefocovanja lozinke
            setError("errBrute", 300000);

        } else{
            //Firebase prijavlivanje na nalog i onComplete (kada USPJEŠNO završi prijavu)...
            lProgressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(lEmailData, lPasswordData)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null && !firebaseUser.isEmailVerified()){

                                //Email nije verifikovan
                                setError("errEmailNoVerify", 20000);

                            }else if(firebaseUser != null && firebaseUser.isEmailVerified()){

                                //Email je verifikovan, startuje LandingActivity
                                startActivity(new Intent(Login.this, LandingActivity.class));
                                finish();

                            }
                    }else {
                        //Hendolovanje exceptiona
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){

                            //Lozinka nije validna
                            setError("errInvalidPass");
                            antiBruteForece++;

                        } else if(task.getException() instanceof FirebaseAuthInvalidUserException){

                            //Nalog ne postoji
                            setError("errInvalidUser");

                        } else if(task.getException() instanceof FirebaseNetworkException){

                            //Problem sa konekcijom
                            setError("errConnection");

                        } else {

                            //Svi ostali exceptioni
                            errUnknownCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            setError("errUnknownCode");

                        }
                    }
                }
            });
        }

        }
    });
    }
    //Postavljanje errora, odnoso njihov UI prikaz
    public void setError(String errCode, int timeOut){
        switch (errCode) {
            //Neisparavan email (formatting)
            case "errEmail":{
                lErrorMsg.setText(R.string.errEmail);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Neispravna lozinka (formatting)
            case "errPassword":{
                lErrorMsg.setText(R.string.errPass);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Nalog nije verifikovan
            case "errEmailNoVerify":{
                lEmail.setBackgroundResource(R.drawable.text_field_error);
                lErrorMsg.setText(R.string.errEmailNoVerify);
                lErrorMsg.setVisibility(View.VISIBLE);
                canSendVerifyEmail = true;
                break;
            }
            //Verifikacioni email je poslat
            case "succVerifySent":{
                lErrorMsg.setTextColor(Color.GREEN);
                lErrorMsg.setText(R.string.lResendedMail);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Verifikacioni email nije poslat
            case "errVerifyNotSent":{
                lErrorMsg.setText(R.string.lNotResendedMail);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Lozinka nije validna
            case "errInvalidPass":{
                lPassword.setBackgroundResource(R.drawable.text_field_error);
                lErrorMsg.setText(R.string.errInvalidPass);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Korisnik ne postoji
            case "errInvalidUser":{
                lEmail.setBackgroundResource(R.drawable.text_field_error);
                lErrorMsg.setText(R.string.errInvalidUser);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Problem sa internetkom konekcijom
            case "errConnection":{
                lErrorMsg.setText(R.string.errConn);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Svi ostali errori odnosno exceptioni
            case "errUnknownCode":{
                lErrorMsg.setText(errUnknownCode);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Brute force
            case "errBrute":{
                lErrorMsg.setText(R.string.errBruteForce);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
        }
        //Čišćenje, odnoso rollbackovanje UI na default nakon određeno timeouta
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lErrorMsg.setTextColor(Color.RED);
                lEmail.setBackgroundResource(R.drawable.text_field);
                lPassword.setBackgroundResource(R.drawable.text_field);
                lErrorMsg.setVisibility(View.INVISIBLE);
                lProgressBar.setVisibility(View.INVISIBLE);
                canSendVerifyEmail = false;
            }
        }, timeOut);
        }
    //Metod overloading
    public void setError(String errCode){
        setError(errCode, 3200);
    }


    }
