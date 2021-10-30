package com.gmijo.mytour;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class rDataTips extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.rDataTipsTitle);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View newFileView = inflater.inflate(R.layout.registertips_fragment, null);

        builder.setView(newFileView);

        builder.setPositiveButton(R.string.alright,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
            }
        });

        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedIstanceState){
        super.onViewCreated(view, savedIstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
