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

import com.gmijo.mytour.database.SQLiteController;
import com.gmijo.mytour.database.SQLiteDataHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity{
    //Inicijaliziranje i definisanje elemenataneki
    EditText rUsername, rEmail, rPassword, rPasswordRepeat;
    Button rBtn;
    ProgressBar rProgressBar;
    TextView rErrorMsg, rTerms;
    String rEmailData, rUsernameData, rPasswordData, rPasswordRepeatData, errUnknownCode, userUUID;

    //FireBaseAuth i FirebaseUser, FirebaseFireStore inicijaliziranje
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    boolean disabled_back = false;

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

        //Dobavljanje firebaseAuth-a i firebasefirestore-a
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //SQL lite
        SQLiteController DBcontroller = new SQLiteController(Register.this);


        //Lisener na register button
        rBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disabled_back = true;
                rUsernameData = rUsername.getText().toString().trim();
                rEmailData = rEmail.getText().toString().trim();
                rPasswordData = rPassword.getText().toString().trim();
                rPasswordRepeatData = rPasswordRepeat.getText().toString().trim();
                rProgressBar.setVisibility(View.VISIBLE);

                if (rUsernameData.isEmpty() || rUsernameData.length() < 4) {

                    //Error sa korisničkim imenom
                    setError("errUsername");

                }else if (rEmailData.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(rEmailData).matches()){

                    //Error sa emailom
                    setError("errEmail");

                } else if (rPasswordData.length() <=8 || rPasswordData.isEmpty() || rPasswordRepeatData.isEmpty() || !rPasswordData.matches(rPasswordRepeatData)){

                    //Error sa lozinkom (passwordom)
                    setError("errPassword");

                }else {
                    //Odabir kolekcije u firestore-u (bazi)
                    CollectionReference usersRef = firebaseFirestore.collection("users");

                    //Query komanda za provjeru da li username postoji
                    Query query = usersRef.whereEqualTo("personalData.Username", rUsernameData);

                    //Pozivanje/dobavljanje querija, te lisener na complete/završetak ovog taska
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() == 0) {
                                    //Firebase kreiranje naloga i onComplete (kada USPJEŠNO završi kreiranje)...
                                    firebaseAuth.createUserWithEmailAndPassword(rEmailData, rPasswordData)
                                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        //Nakon uspješnog kreiranja, getuje korisnika i šalje mu verifikacioni email
                                                        firebaseUser = firebaseAuth.getCurrentUser();
                                                        firebaseUser.sendEmailVerification().addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {

                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    userUUID = firebaseAuth.getCurrentUser().getUid();
                                                                    DocumentReference documentReference = firebaseFirestore.collection("users").document(userUUID);
                                                                    Map<String, Object> user = new HashMap<>();
                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("Username", rUsernameData);
                                                                    data.put("Email", rEmailData);
                                                                    data.put("FullName", null);
                                                                    data.put("userType", "Korisnik");
                                                                    user.put("personalData", data);
                                                                    Map<String, Object> achievementData = new HashMap<>();
                                                                    achievementData.put("myTourTokens", 5);
                                                                    achievementData.put("cityExplored", 0);
                                                                    achievementData.put("villageExplored", 0);
                                                                    achievementData.put("naturepointExplored", 0);
                                                                    achievementData.put("nationalParkExplored", 0);
                                                                    user.put("achievementData", achievementData);
                                                                    documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {

                                                                                //Nakon USPJEŠNO poslatog emaila delaya 3000ms i prebacuje na Login actitvity, te šalje poruku da je nalog uspješno kreiran
                                                                                Toast.makeText(getApplicationContext(), R.string.rAccCreated, Toast.LENGTH_LONG).show();
                                                                                rProgressBar.setVisibility(View.GONE);
                                                                                new Handler().postDelayed(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {

                                                                                        //SQlite helper

                                                                                        SQLiteDataHelper liteDataHelper = new SQLiteDataHelper(Register.this);
                                                                                        //Pozivanje metode za ubacivanje usera u bazu
                                                                                        liteDataHelper.registerUser(userUUID, null, rUsernameData, 0, 0, 0, 0, 5, "Korisnik", false);
                                                                                        if (liteDataHelper.getResult() != -1) {
                                                                                            startActivity(new Intent(Register.this, Login.class));
                                                                                            finish();
                                                                                        } else {

                                                                                            //Problem sa SQLlite-om odnosno podatci nisu upisani
                                                                                            setError("errUnknown");
                                                                                        }


                                                                                    }
                                                                                }, 800);

                                                                                //Problem sa konekcijom
                                                                            } else if (task.getException() instanceof FirebaseNetworkException) {

                                                                                setError("errConnection");

                                                                                //Otali exceptioni
                                                                            } else {

                                                                                setError("errUnknown");

                                                                            }
                                                                        }
                                                                    });

                                                                    //Problem sa konekcijom
                                                                } else if (task.getException() instanceof FirebaseNetworkException) {

                                                                    setError("errConnection");

                                                                    //Ostali exceptioni
                                                                } else {

                                                                    setError("errUnknown");

                                                                }
                                                            }
                                                        });

                                                        //Hendolovanje exceptiona
                                                    } else {
                                                        //Exception za multinalog (nalog je već registrovan)
                                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                                            setError("errUserAlreadyExist");
                                                            rProgressBar.setVisibility(View.INVISIBLE);

                                                            //Exception za internet (nema internetske veze)
                                                        } else if (task.getException() instanceof FirebaseNetworkException) {

                                                            setError("errConnection");

                                                            //Previše requesova sa jednog IP
                                                        } else if (task.getException() instanceof FirebaseTooManyRequestsException) {

                                                            setError("errTooManyReq");

                                                            //Svi ostali exceptioni
                                                        } else {

                                                            errUnknownCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                                            setError("errUnknownCode");

                                                        }

                                                    }
                                                }
                                            });
                                }else{

                                    //Korisničko ime postoji
                                    setError("errUsernameExists");



                                }

                            }else {

                                //Task nije uspio
                                setError("errUnknown");



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
        disabled_back = false;
        rDataTips rDataTips = new rDataTips();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        rDataTips.show(fragmentTransaction, "");
        errCounter = 0;
    }
    //Postavljanje errora, odnoso njihov UI prikaz
    public void setError(String errCode, int timeOut){
        disabled_back = false;
        switch (errCode){
            //Problem sa username
            case "errUsername": {
                rUsername.setBackgroundResource(R.drawable.text_field_error);
                rErrorMsg.setText(R.string.errUsername );
                rErrorMsg.setVisibility(View.VISIBLE);
                errCounter++;
                break;
            }
            //Username postoji
            case "errUsernameExists":{
                rErrorMsg.setText(R.string.errUsernameExists);
                rUsername.setBackgroundResource(R.drawable.text_field_error);
                rErrorMsg.setVisibility(View.VISIBLE);
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
            case "errTooManyReq": {
                rErrorMsg.setText(R.string.errTooManyReq);
                rErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            case "errUnknown":{
                rErrorMsg.setText(R.string.errUnknown);
                rErrorMsg.setVisibility(View.VISIBLE);
                break;
            }

        }
        //Čišćenje, odnoso rollbackovanje UI na default nakon određeno timeouta
        rProgressBar.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rUsername.setBackgroundResource(R.drawable.text_field);
                rEmail.setBackgroundResource(R.drawable.text_field);
                rPassword.setBackgroundResource(R.drawable.text_field);
                rPasswordRepeat.setBackgroundResource(R.drawable.text_field);
                rErrorMsg.setVisibility(View.INVISIBLE);
            }
        }, timeOut);
    }

    @Override
    public void onBackPressed() {
        if (disabled_back){
            Toast.makeText(getApplicationContext(), R.string.errActionUnfinished, Toast.LENGTH_LONG).show();
        }else {
            super.onBackPressed();
        }
    }

    //Metod overloading
    public void setError(String errCode){
        setError(errCode, 3200);
    }
}
