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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Login extends AppCompatActivity  {

    //Inicijaliziranje i definisanje elemenata
    EditText lEmail, lPassword;
    Button lBtn, lBtnGoogle;
    TextView lForgotPassword, lRegisterRedirect, lErrorMsg;
    String lEmailData, lPasswordData, errUnknownCode, userUUID, gName, gSurname, gUsername;
    ProgressBar lProgressBar;
    Boolean canSendVerifyEmail = false;
    int antiBruteForece = 0;


    //FireBaseAuth i FirebaseUser, Firestore inicijaliziranje
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    //Google OAUTH inicijaliziranje
    GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Postavljanje boje nanvigacijskih gumbova
        getWindow().setNavigationBarColor(getColor(R.color.white));

        //Dodjelivanje elemenata "varijablama"
        lEmail = (EditText) findViewById(R.id.LoginEmail);

        lPassword=  (EditText) findViewById(R.id.LoginPassword);

        lForgotPassword = (TextView) findViewById(R.id.LoginForgotPassword);

        lRegisterRedirect = (TextView) findViewById(R.id.LoginCreateACCRedirect);

        lErrorMsg = (TextView) findViewById(R.id.LoginErrorMessage);

        lBtn = (Button) findViewById(R.id.LoginBtn);

        lBtnGoogle = (Button) findViewById(R.id.LoginGoogleBtn);

        lProgressBar = (ProgressBar) findViewById(R.id.LoginProgressBar);

        //Dobavljanje firebaseAuth-a i Firestore-a
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Konfiguracija prijave putem Googla
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webcl_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);



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
            finish();
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
                            if (!firebaseUser.isEmailVerified()){

                                //Email nije verifikovan
                                setError("errEmailNoVerify", 20000);

                            }else {

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

                        } else if(task.getException() instanceof FirebaseTooManyRequestsException) {

                            //Previše requesova sa jednog IP
                            setError("errTooManyReq");

                        }else {

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

    //Lisener na google login button, pozivanje metode googleSignIn
        lBtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
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
                lPassword.setBackgroundResource(R.drawable.text_field_error);
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
            //Previše zahtjeva (requestova) ka firebase serveru (Limit 200 po IP za 24h)
            case "errTooManyReq": {
                lErrorMsg.setText(R.string.errTooManyReq);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Pogreška prilikom google prijave
            case "errGoogleSignIn": {
                lErrorMsg.setText(R.string.errGoogleSignIn);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Pogreška prilikom google prijave
            case "signInFailed": {
                lErrorMsg.setText(R.string.errSignInFailed);
                lErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Svi ostali exceptioni
            case "errUnknown":{
                lErrorMsg.setText(R.string.errUnknown);
                lErrorMsg.setVisibility(View.VISIBLE);
            }//Već je prijavljen jednom od metoda
            case "errAlreadySignedInPass":{
                lErrorMsg.setText(R.string.errAlreadySignedInPass);
                lErrorMsg.setVisibility(View.VISIBLE);
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
                antiBruteForece = 0;
            }
        }, timeOut);
    }

    //Metod overloading
    public void setError(String errCode){
        setError(errCode, 3200);
    }

    // Provjera da li je na startu (početku) trenutni korisnik prijavljen(logovan) ili nije odnosno korisnik je null

   public void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
                        if (firebaseUser.isEmailVerified()) {

                            //Email je verifikovan, startuje LandingActivity
                            startActivity(new Intent(Login.this, LandingActivity.class));
                            finish();

                        }


        }
    }

    //Google prijava, pokretanje Google intenta za odabir mejla
    private void googleSignIn() {
    firebaseUser = firebaseAuth.getCurrentUser();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null){
            lEmailData = googleSignInAccount.getEmail();
        }
    }

    //Hendlovanje odabira iz metode iznad
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    firebaseAuth.fetchSignInMethodsForEmail(lEmailData).addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
        @Override
        public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
            List<String> signInMethods = signInMethodQueryResult.getSignInMethods();
            if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)){

                setError("errAlreadySignedInPass", 6000);

            }else{

                //Rezultat vracen iz pokretanja intenta za odabir mejla
                if (requestCode == RC_SIGN_IN) {

                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    try {

                        //Google prijava uspješna, poziva metodu ispod odnosno firebaseAuthGoogle
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthGoogle(account);

                    } catch (ApiException e) {

                        //Google prijava neuspješna, izbacuje error
                        setError("errGoogleSignIn");
                    }
                }
            }
        }
    });
    }

    //Ukoliko "prođe" odnosno uspije metodu iznad prelazi na samu autentifikaciju sa firebaseom
    private void firebaseAuthGoogle(GoogleSignInAccount acccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Dobavljanje podataka od GoogleNaloga
                            getGData();

                        } else {

                            //Prijava neuspješna, izbacuje error
                            setError("signInFailed");

                        }
                    }
                });
    }
    //Metoda za dobavljanje podataka od Google Naloga,
    private void getGData(){
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            lEmailData = googleSignInAccount.getEmail();
            String[] rawData = lEmailData.split("@");
            gUsername = rawData[0];
            gName = googleSignInAccount.getGivenName();
            gSurname = googleSignInAccount.getFamilyName();
            userUUID = googleSignInAccount.getId();
           gName = googleSignInAccount.getGivenName();
            checkUsernameCloud();
        }
    }
    //Provjera da li postoji username zapisan negdje na cloudu, da ne budu 2 korisnika sa istim username-om
    private void checkUsernameCloud(){
        //Odabir kolekcije na cloudfilestore-u
        CollectionReference usersRef = firebaseFirestore.collection("users");

        //Query komanda za provjeru da li username postoj
        Query query = usersRef.whereEqualTo("personalData.Username", gUsername);

        //Pozivanje/slanje querija
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    //Na cloudu ne postoje podatci o tom username-u, upisuje ih  tamo
                    if (task.getResult().size() <= 0 ){
                        DocumentReference documentReference = firebaseFirestore.collection("users").document(userUUID);
                        Map<String, Object> user1 = new HashMap<>();
                        Map<String, Object> data = new HashMap<>();
                        data.put("Username", gUsername);
                        data.put("Email", lEmailData);
                        data.put("userType", "Korisnik");
                        user1.put("personalData", data);
                        Map<String, Object> achievementData = new HashMap<>();
                        achievementData.put("myTourTokens", 5);
                        achievementData.put("cityExplored", 0);
                        achievementData.put("villageExplored", 0);
                        achievementData.put("neturepointExplored", 0);
                        achievementData.put("nationalParkExplored", 0);
                        user1.put("achievementData", achievementData);
                        documentReference.set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //Nakon uspješnog upisa, prebacuje ga na LandingActivity
                                    Toast.makeText(getApplicationContext(), R.string.lSuccess, Toast.LENGTH_LONG).show();
                                    lProgressBar.setVisibility(View.GONE);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                                startActivity(new Intent(Login.this, LandingActivity.class));
                                                finish();


                                        }
                                    }, 3000);

                                    //Problem sa konekcijom i odjava jer je korisnik prijavljen
                                } else if (task.getException() instanceof FirebaseNetworkException) {

                                    FirebaseAuth.getInstance().signOut();
                                    setError("errConnection");

                                    //Ostali exceptioni i odjava, jer je korisnik je prijavljen
                                } else {

                                    FirebaseAuth.getInstance().signOut();
                                    setError("errUnknown");

                                }
                            }
                        });
                        //Na cloudu postoji userame, genenerisanje novog
                    }else if (task.getResult().size() > 0){
                        Random random = new Random();
                        gUsername = gUsername + random.nextInt(100);
                        checkUsernameCloud();

                    }

                }else {

                    //Ostali exceptioni
                    setError("errUnknown");

                }
            }
        });
    }

}

