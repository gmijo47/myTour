package com.gmijo.mytour.ui.profil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.gmijo.mytour.R;


public class unsavedDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Builder klasa za pravljenje dialoga
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.pUnsavedData)
                .setNegativeButton(R.string.pApplyCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Korisnik odbacuje promjene, završava activity
                        getActivity().finish();

                    }
                })
                .setPositiveButton(R.string.pApplySave, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Korisnih sprema promjene
                ((EditProfile) getActivity()).determineChanges();

            }
        });
        //Vraća napravljen dialog
        return builder.create();
    }
}
