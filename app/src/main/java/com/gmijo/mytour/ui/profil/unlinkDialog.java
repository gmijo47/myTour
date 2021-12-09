package com.gmijo.mytour.ui.profil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.gmijo.mytour.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;


public class unlinkDialog extends DialogFragment {

    //Firebase inicijalizacija
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Builder klasa za pravljenje dialoga
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Dobavljanje firebase-a
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        builder.setMessage(R.string.phoneUnlink)
                .setNegativeButton(R.string.pApplyCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Korisnik odbacuje brisanje broja, gasi dialog, prikazuje poruku
                        getDialog().cancel();
                        Snackbar.make(getDialog().getOwnerActivity().getWindow().getDecorView(), R.string.pCanceled, Snackbar.LENGTH_LONG).show();

                    }
                })
                .setPositiveButton(R.string.phoneRemove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                       //Korisnik uklanja broj telefona, gasi dialog, setuje value na NEVERIFIKOVAN
                        firebaseUser.unlink(PhoneAuthProvider.PROVIDER_ID);
                        Snackbar.make(getDialog().getOwnerActivity().getWindow().getDecorView(), R.string.phoneUnlinked, Snackbar.LENGTH_LONG).show();
                        getDialog().cancel();

                    }
                });
        //Vraća napravljen dialog
        return builder.create();
    }
}
