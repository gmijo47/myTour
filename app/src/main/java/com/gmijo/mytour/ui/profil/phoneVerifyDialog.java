package com.gmijo.mytour.ui.profil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gmijo.mytour.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class phoneVerifyDialog extends DialogFragment {
    EditText phoneNumber, codeNumber;
    ProgressBar progressBar;
    Button verifyBtn, checkCodeBtn;
    String numberData;
    View newFileView;
    FirebaseAuth firebaseAuth;
    TextView phoneTitle, codeTitle;
    String codeUser, codeSys;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Builder klasa za pravljenje dialoga
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        newFileView = inflater.inflate(R.layout.dialog_phone_verify, null);
        builder.setView(newFileView);

        //Vraća napravljen dialog
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Inicializiranje lemenata
        phoneNumber = (EditText) getDialog().findViewById(R.id.phoneNumber);
        codeNumber = (EditText) getDialog().findViewById(R.id.codeNumber);
        verifyBtn = (Button) getDialog().findViewById(R.id.SendVefifyBtn);
        checkCodeBtn = (Button) getDialog().findViewById(R.id.CheckVefiy);
        progressBar = (ProgressBar) getDialog().findViewById(R.id.progressVerify);
        phoneTitle = (TextView) getDialog().findViewById(R.id.phoneVerifySubTitle);
        codeTitle = (TextView) getDialog().findViewById(R.id.phoneVerifyCodeSubTitle);
        firebaseAuth = FirebaseAuth.getInstance();

        //Lisener za verify button
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifyBtn.setEnabled(false);
                numberData = phoneNumber.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                phoneNumber.setEnabled(false);
                if (!Patterns.PHONE.matcher(numberData).matches()){

                    //Broj telefona nije validan
                    Snackbar.make(newFileView, R.string.phoneNotValid, Snackbar.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    phoneNumber.setEnabled(true);
                    verifyBtn.setEnabled(false);

                }else {

                    //Broj telefona je validan, poziva metodu za slanje poruke
                    sendVerificationMessage();

                }
            }
        });
    }

    private void sendVerificationMessage() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                numberData,
                60,
                TimeUnit.SECONDS,
                getActivity(),
                mCallback

        );
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            //Verifikacija je uspijela
            progressBar.setVisibility(View.GONE);
            codeUser = phoneAuthCredential.getSmsCode();

            signInCred(codeSys, codeUser);


        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            //Verifikacija nije uspijela, zatvara dijalog
            progressBar.setVisibility(View.GONE);
            Snackbar.make(newFileView, R.string.phoneVerifyFiled, Snackbar.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    phoneVerifyDialog.super.onCancel(getDialog());
                }
            }, 3500);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);


            codeSys = s;
            progressBar.setVisibility(View.GONE);

            //Kod je poslat
            Snackbar.make(newFileView, R.string.phoneCodeSent, Snackbar.LENGTH_LONG).show();

            try {

                //Pozivanje meteode za prikaz  UI-a za upis koda
                showCodeUI();

            } catch (Exception e){

                //Došlo je do pogreške, ne radi ništa

            }
            checkCodeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    codeUser = codeNumber.getText().toString().trim();
                    progressBar.setVisibility(View.VISIBLE);
                    checkCodeBtn.setEnabled(false);

                    //Provjera da li je kod ispravan
                    if (codeUser.length() < 6 || codeUser.isEmpty()){

                        //Kod nije ispravan
                        Snackbar.make(newFileView, R.string.phoneCodeInvalid, Snackbar.LENGTH_LONG).show();

                    }else {

                        //Kod je ispravan, poziva metodu za autentifikaciju
                        signInCred(codeSys, codeUser);

                    }
                }
            });

        }

        //Meteoda za prikaz UI-a za upis koda
        private void showCodeUI() {
            phoneNumber.setVisibility(View.GONE);
            codeNumber.setVisibility(View.VISIBLE);
            phoneTitle.setVisibility(View.GONE);
            codeTitle.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.GONE);
            checkCodeBtn.setVisibility(View.VISIBLE);

        }
    };

    //Metoda za autentifkiaciju kredencijalima
    private void signInCred(String codeS, String codeU) {

        //Kreira kredencijale na osnovu serverskog i klijentskog koda
       PhoneAuthCredential authCredential =  PhoneAuthProvider.getCredential(codeS, codeU);

       //Disable, za kucanje koda, prikaz spinnera
       codeNumber.setEnabled(false);
       progressBar.setVisibility(View.VISIBLE);

       //Vrsi autentifikaciju
      firebaseAuth.getCurrentUser().linkWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){

                   //Autentifikacija uspjela
                   progressBar.setVisibility(View.GONE);
                   Snackbar.make(newFileView, R.string.phoneVerified, Snackbar.LENGTH_LONG).show();

                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {

                           //Nakon 3500ms, gasi UI za verifikaciju.
                           getDialog().cancel();

                       }
                   }, 3500);

               }else {

                   //Autentifikacija nije uspijela
                   if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){

                       //Kod nije validan, pali txt box i button
                       Snackbar.make(newFileView, R.string.phoneCodeInvalid, Snackbar.LENGTH_LONG).show();
                       checkCodeBtn.setEnabled(true);
                       codeNumber.setEnabled(true);

                   }
               }
           }
       });
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
