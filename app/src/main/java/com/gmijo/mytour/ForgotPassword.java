package com.gmijo.mytour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;


public class ForgotPassword extends AppCompatActivity {

    //Inicijaliziranje i definisanje elemenata
    EditText rsEmail;
    TextView rsErrorMsg;
    Button rsBtn;
    String rsEmailData;
    ProgressBar rsProgressBar;
    int sent = 0;

    //FireBaseAuth inicijaliziranje
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Dodjelivanje elemenata "varijablama"
        rsEmail = (EditText) findViewById(R.id.ResetPasswordEmail);

        rsBtn = (Button) findViewById(R.id.ResetPasswordBtn);

        rsErrorMsg = (TextView) findViewById(R.id.ResetPasswordError);

        rsProgressBar = (ProgressBar) findViewById(R.id.ResetPasswordProgressBar);

        //Dobavljanje firebaseAuth-a
        firebaseAuth = FirebaseAuth.getInstance();

        //Lisener na ResetPassword button
        rsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rsEmailData = rsEmail.getText().toString().trim();
                rsProgressBar.setVisibility(View.VISIBLE);

                //Provjera da li email podliježe regexu odnosno patternu za email
                if (Patterns.EMAIL_ADDRESS.matcher(rsEmailData).matches()){

                    //Fetchuje metode prijave za unijeti email, te stavlja lisener na to
                    firebaseAuth.fetchSignInMethodsForEmail(rsEmailData).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                /*Ukoliko je broj metoda jednak 1 ili veći od 1 šalje email, odnosno to potvrđuje
                                    da korisnik postoji
                                 */
                                if (task.getResult().getSignInMethods().size() >= 1) {
                                    if (sent < 3) {
                                        //Slanje password reset emaila
                                        firebaseAuth.sendPasswordResetEmail(rsEmailData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            /*Ukoliko je uspješno poslat i ovo je prvi put u ovoj sesiji da ga šalje
                                                izbacuje poruku da je uspješno poslat*/
                                                if (task.isSuccessful()) {

                                                    setError("rsPasswordEmail");
                                                    sent++;
                                                    //Hendlovanje exceptiona konkretno problem sa konekcijom
                                                } else if (task.getException() instanceof FirebaseNetworkException) {

                                                    setError("errConn");

                                                    //Svi ostali exeptioni
                                                } else {

                                                    setError("rsUnknownErr");

                                                 }
                                            }
                                        });
                                        //Poslato više od 3 emaila
                                    } else {

                                        setError("rsAlreadySent");

                                    }
                                    //Fetch vratio vrijednost manju od 1 odnosno 0, što znači da korisnik ne postoji
                                }else {

                                    setError("errNoUserExist");

                                }
                            //Fetch metoda nije uspio
                            }else {

                                setError("rsUnknownErr");

                            }
                        }
                    });
                    //Uneseni email nije ispravan
                }else {

                    setError("errEmail");

                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void setError(String errCode, int timeOut){
        switch (errCode) {
            //Email je poslat
            case "rsPasswordEmail":{
                rsErrorMsg.setTextColor(Color.GREEN);
                rsErrorMsg.setText(R.string.rsPasswordEmail);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Korisnik ne postoji
            case "errNoUserExist":{
                rsErrorMsg.setText(R.string.errNoUserExist);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Problem sa konekcijom
            case "errConn":{
                rsErrorMsg.setText(R.string.errConn);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Ostali problemi (exceptioni)
            case "rsUnknownErr": {
                rsErrorMsg.setText(R.string.rsUnknownErr);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Email je već poslat odnosno došlo je do prekoračenja broja slanja iz jedne sesije (5)
            case "rsAlreadySent": {
                rsErrorMsg.setText(R.string.rsAlreadySent);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            //Uneseni email nije validan (regex)
            case "errEmail": {
                rsErrorMsg.setText(R.string.errEmail);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
        }
        //Čišćenje, odnoso rollbackovanje UI na default nakon određeno timeouta
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rsErrorMsg.setTextColor(Color.RED);
                rsErrorMsg.setVisibility(View.INVISIBLE);
                rsProgressBar.setVisibility(View.INVISIBLE);
            }
        }, timeOut);
    }

    //Metod overloading
    public void setError(String errCode){
        setError(errCode, 3200);
    }
}