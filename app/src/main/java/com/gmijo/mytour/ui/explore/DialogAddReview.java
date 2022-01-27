package com.gmijo.mytour.ui.explore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmijo.mytour.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DialogAddReview extends DialogFragment {

    RatingBar ratingBar;
    String placename;
    String typeCc;
    TextView ratingTxt, errMsg;
    EditText reviewText;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    int randomNum;
    public DialogAddReview(String placename, String type){
        this.placename = placename;
        this.typeCc = type;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.eReviewTitle);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View newFileView = inflater.inflate(R.layout.dialog_add_review, null);

        builder.setView(newFileView);

        ratingBar = (RatingBar) newFileView.findViewById(R.id.rating);
        ratingTxt = (TextView) newFileView.findViewById(R.id.ratingTxt);
        reviewText = (EditText) newFileView.findViewById(R.id.reviewAddText);
        errMsg = (TextView) newFileView.findViewById(R.id.reviewErrMsg);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch ((int) rating){
                    case 1:{
                        ratingTxt.setText(getString(R.string.eReviewOneStar));
                        break;
                    }
                    case 2:{
                        ratingTxt.setText(getString(R.string.eReviewTwoStar));
                        break;
                    }
                    case 3:{
                        ratingTxt.setText(getString(R.string.eReviewThreeStar));
                        break;
                    }
                    case 4:{
                        ratingTxt.setText(getString(R.string.eReviewFourStar));
                        break;
                    }
                    case 5:{
                        ratingTxt.setText(getString(R.string.eReviewFiveStar));
                        break;
                    }
                }
            }
        });

        builder.setPositiveButton(R.string.eReviewAdd, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = reviewText.getText().toString();
                if (text.length() > 15){
                    int rating = (int) ratingBar.getRating();
                    String uuid = firebaseAuth.getCurrentUser().getUid();
                    Map<String, String> data = new HashMap();
                    data.put("rating", String.valueOf(Integer.parseInt(String.valueOf(rating))));
                    data.put("comment", text);
                    DatabaseReference  db = firebaseDatabase.getReferenceFromUrl("https://mytour-df457-default-rtdb.europe-west1.firebasedatabase.app").child("attr_reviews").child(placename).child(uuid);
                                db.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                        Integer someVal;
                                                        Integer tokenCount;
                                                        Random random = new Random();
                                                        randomNum = random.nextInt(32);
                                                        tokenCount = Integer.parseInt(documentSnapshot.get("achievementData.myTourTokens").toString());

                                                        switch (typeCc){
                                                            case ("city"):{
                                                                someVal = Integer.parseInt(documentSnapshot.get("achievementData.cityExplored").toString());
                                                                submitData("city", "cityExplored", someVal+1, tokenCount+randomNum);
                                                                break;
                                                            }
                                                            case ("vill"):{
                                                                someVal = Integer.parseInt(documentSnapshot.get("achievementData.villageExplored").toString());
                                                                submitData("vill", "villageExplored", someVal+1, tokenCount+randomNum);
                                                                break;
                                                            }
                                                            case ("npark"):{
                                                                someVal = Integer.parseInt(documentSnapshot.get("achievementData.nationalParkExplored").toString());
                                                                submitData("npark", "nationalParkExplored", someVal+1, tokenCount+randomNum);
                                                                break;
                                                            }
                                                            case ("attr"):{
                                                                someVal = Integer.parseInt(documentSnapshot.get("achievementData.naturepointExplored").toString());
                                                                submitData("attr", "naturepointExplored", someVal+1, tokenCount+randomNum);
                                                                break;
                                                            }
                                                        }

                                                    }else {

                                                        Toast.makeText(getActivity(), R.string.eReviewErrUnkw, Toast.LENGTH_LONG).show();

                                                    }
                                                }

                                                private void submitData(String type, String exploreColumn, int exploreValue, int tokenValue) {
                                                    Map<String, Object> data = new HashMap<>();
                                                    Map<String, Object> collection = new HashMap<>();
                                                    data.put("myTourTokens", tokenValue);
                                                    data.put(exploreColumn, exploreValue);
                                                    collection.put("achievementData", data);

                                                    firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(collection, SetOptions.merge());

                                                }
                                            });


                                        }else {
                                            Toast.makeText(getActivity(), R.string.eReviewErrUnkw, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                }else {
                    Toast.makeText(getActivity(), R.string.eReviewErrTxt, Toast.LENGTH_LONG).show();

                }
            }
        });

        builder.setNegativeButton(R.string.eReviewCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
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
