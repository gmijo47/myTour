package com.gmijo.mytour.ui.profil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmijo.mytour.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {

    //Inicijaliziranje i definisanje elemenata
    ImageButton editProfileBackBtn, editProfileSaveBtn;
    ImageView editAvatar;
    TextView emailData, usernameData, phoneData, phoneAction;
    EditText editFullName, editPassword, editPasswordRepeat;
    Intent dataFromFragment;
    ArrayList<String> rawData;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    ConstraintLayout view;
    ProfilFragment profilFragment;
    Intent resIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        //Dobavljanje intenta, i podataka u intentu;
        dataFromFragment = getIntent();
        resIntent = getIntent();
        rawData = dataFromFragment.getStringArrayListExtra("data");
        //View
        view = (ConstraintLayout) findViewById(R.id.editProfileLayout);

        //Setuje default datu u intent
        resIntent.putExtra("dataChanged", false);

       //Buttoni
        editProfileBackBtn = (ImageButton) findViewById(R.id.editProfileBack);
        editProfileSaveBtn = (ImageButton) findViewById(R.id.editProfileSave);
        editAvatar = (ImageView) findViewById(R.id.editAvatar);

        //Polja
        editFullName = (EditText) findViewById(R.id.editFullName);
        usernameData = (TextView) findViewById(R.id.editUsername);
        emailData = (TextView) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPass);
        editPasswordRepeat = (EditText) findViewById(R.id.editPassRepeat);
        phoneData = (TextView) findViewById(R.id.displayPhone);
        phoneAction = (TextView) findViewById(R.id.verifyPhone);

        //Pozivanje metode za prikaz podataka
        displayData();


        //Lisener na strelicu za povratak
        editProfileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Lisener za edit avatara
        editAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
        //Lisener za verifikaciju telefona
        phoneAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phoneData.getText().toString().trim().matches(getString(R.string.phoneNVerify))) {

                    //Pokretanje novog dialog boxa, jer korisnik nema verifikovan broj telefona
                    phoneVerifyDialog verifyDialog = new phoneVerifyDialog();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    verifyDialog.show(fragmentTransaction, "");

                } else {

                   //Pokretanje novog dialog boxa, za unlinkovanje telefona
                    unlinkDialog unLink = new unlinkDialog();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    unLink.show(fragmentTransaction, "");

                }
            }
        });

        //Lisener na save
        editProfileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    checkChanges();
                }catch (Exception e){

                }
                if (changes.contains(true)){

                        determineChanges();

                }else {
                    Snackbar.make(view, R.string.pNoChangesMade, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Metoda koja se poziva kada korisnik klinke back na UI-u samog uređaja
    @Override
    public void onBackPressed() {
        try{

            //Pozivanje metode za provjeru izmjena
            checkChanges();

        }catch (Exception e){

            //Došlo je do pogreške
            Snackbar.make(view, R.string.errLoader, Snackbar.LENGTH_SHORT).show();

        }
        //Provjere da li izmjene postoje
       if (changes.contains(true)){

           //Izmjene postoje, pokaži dialog za save
           unsavedDialog unSavedDialog = new unsavedDialog();
           FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
           unSavedDialog.show(fragmentTransaction, "");

       }else {

           //Nema izmjena, završava activity
           setResult(Activity.RESULT_OK, resIntent);
           super.onBackPressed();

       }
    }

    //Metoda za prikaz podataka (runuje se u novom threadu)
    public void displayData(){
       Thread thread = new Thread(new Runnable() {
           @Override
           public void run() {
            editFullName.setText(rawData.get(1));
            usernameData.setText(rawData.get(0));
            emailData.setText(firebaseUser.getEmail());
               if (firebaseUser.getPhoneNumber() != null) {
                   phoneData.setText(firebaseUser.getPhoneNumber());
                   phoneAction.setTextColor(Color.RED);
                   phoneAction.setText(R.string.phoneRemove);
               } else {
                   phoneData.setText(R.string.phoneNVerify);
                   phoneAction.setTextColor(Color.BLUE);
                   phoneAction.setText(R.string.phoneVerify);
               }
           }
       });
        thread.start();
        try{
            thread.join();

        }catch (Exception e){

        }
    }

    //Metoda za provjeru izmjena
    ArrayList<Boolean> changes = new ArrayList<Boolean>();
    public ArrayList<Boolean> checkChanges(){
                if (!rawData.get(1).matches(editFullName.getText().toString())){

                    changes.add(0, true);

                }else {

                    changes.add(0, false);

                }
                if (!editPassword.getText().toString().matches("")){

                    changes.add(1, true);

                }else{

                    changes.add(1, false);

                }
        return changes;
    }

    //Metoda za određivanje izmjena, odnosno gdje su se taćno izmjene desile
    public void determineChanges() {
        if (changes.get(0) == true) {
            if (changes.get(1) == true) {
                if (!editPassword.getText().toString().matches(editPasswordRepeat.getText().toString()) || editPassword.getText().toString().length() <=8){


                    //Lozinke se ne podudaraju, ili je lozinka prekratka
                    Snackbar.make(view, R.string.pErrPasswrd, Snackbar.LENGTH_SHORT).show();

                }else {

                updateData(3);
                }


            } else {

                updateData(1);

            }
        } else if (changes.get(1) == true) {
            if (!editPassword.getText().toString().matches(editPasswordRepeat.getText().toString()) || editPassword.getText().toString().length() <=8 ){

                //Lozinke se ne podudaraju, ili je lozinka prekratka
                Snackbar.make(view, R.string.pErrPasswrd, Snackbar.LENGTH_SHORT).show();

            }else {
                updateData(2);
            }


        } else {
            setResult(Activity.RESULT_OK, resIntent);
            super.onBackPressed();
        }
    }

    //Metoda za update podataka, na osnovu onih podataka koje je korisnik unjeo
    public void updateData(int code){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
        if (code == 3){
        try{
            //Update imena
            updateData(1);

            try{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Update passworda, nakon delaya od 1600ms
                        updateData(2);
                    }
                }, 1600);


            }catch (Exception e){

                //Došlo je do pogreške
                Snackbar.make(view, R.string.errLoader, Snackbar.LENGTH_SHORT).show();
            }
        }catch (Exception e){

            //Došlo je do pogreške
            Snackbar.make(view, R.string.errLoader, Snackbar.LENGTH_SHORT).show();
        }
     } else if (code == 2){
            //Prvo poziva metodu za provjeru authprovidera, te ukoliko je google odbija reset passworda
            firebaseAuth.fetchSignInMethodsForEmail(Objects.requireNonNull(firebaseUser.getEmail())).addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                @Override
                public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                    List<String> signInMethods = signInMethodQueryResult.getSignInMethods();

                    if (signInMethods.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)) {

                        //Sifra ne moze biti promjenjena, logovan je googlom
                        changes.set(1, false);
                        editPassword.setText("");
                        editPasswordRepeat.setText("");
                        Snackbar.make(view, R.string.pGoogleDeny, Snackbar.LENGTH_LONG).show();

                    }else {
                       firebaseUser.updatePassword(editPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){

                                   //Lozinka je promjenjena
                                   changes.set(1, false);
                                   editPassword.setText("");
                                   editPasswordRepeat.setText("");
                                  Snackbar.make(view, R.string.pPassChanged, Snackbar.LENGTH_SHORT).show();


                               }else {

                                   //Nije promjena
                                   changes.set(1, false);
                                   editPassword.setText("");
                                   editPasswordRepeat.setText("");
                                   Snackbar.make(view, R.string.errLoader, Snackbar.LENGTH_SHORT).show();

                               }
                           }
                       });
                    }
                }
            });

     } else {
            //Update korisničkog imena
         documentReference.update("personalData.FullName", editFullName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){

                     //Update uspješan
                     rawData.set(1, editFullName.getText().toString());
                     changes.set(0, false);
                     Snackbar.make(view, R.string.pNameChanged, Snackbar.LENGTH_SHORT).show();
                     resIntent.putExtra("dataChanged", true);

                 }else{

                     //Update nije uspješan
                     rawData.set(1, editFullName.getText().toString());
                     changes.set(0, false);
                     Snackbar.make(view, R.string.errLoader, Snackbar.LENGTH_SHORT).show();

                 }
             }
         });

        }
    }
}
