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

import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity {

    EditText rsEmail;
    TextView rsErrorMsg;
    Button rsBtn;
    String rsEmailData;
    ProgressBar rsProgressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        rsEmail = (EditText) findViewById(R.id.ResetPasswordEmail);

        rsBtn = (Button) findViewById(R.id.ResetPasswordBtn);

        rsErrorMsg = (TextView) findViewById(R.id.ResetPasswordError);

        rsProgressBar = (ProgressBar) findViewById(R.id.ResetPasswordProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        rsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rsEmailData = rsEmail.getText().toString().trim();
                rsProgressBar.setVisibility(View.VISIBLE);
                if (Patterns.EMAIL_ADDRESS.matcher(rsEmailData).matches()){

                    firebaseAuth.fetchSignInMethodsForEmail(rsEmailData).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            if(task.getResult().getSignInMethods().size() == 1){

                                firebaseAuth.sendPasswordResetEmail(rsEmailData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                            setError("rsPasswordEmail");

                                        }else if(task.getException() instanceof FirebaseNetworkException) {

                                            setError("errConn");

                                        } else {

                                            setError("rsUnknownErr");

                                        }

                                    }
                                });

                            } else{

                                setError("errNoUserExist");

                            }
                        }
                    });
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
            case "rsPasswordEmail":{
                rsErrorMsg.setTextColor(Color.GREEN);
                rsErrorMsg.setText(R.string.rsPasswordEmail);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            case "errNoUserExist":{
                rsErrorMsg.setText(R.string.rsPasswordEmail);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            case "errConn":{
                rsErrorMsg.setText(R.string.errConn);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
            }
            case "rsUnknownErr":
                rsErrorMsg.setText(R.string.rsUnknownErr);
                rsErrorMsg.setVisibility(View.VISIBLE);
                break;
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