package com.gmijo.mytour.ui.profil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gmijo.mytour.R;

public class EditProfile extends AppCompatActivity {

    //Inicijaliziranje i definisanje elemenata
    ImageButton editProfileBackBtn, editProfileSaveBtn;
    ImageView editAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editProfileBackBtn = (ImageButton) findViewById(R.id.editProfileBack);
        editProfileSaveBtn = (ImageButton) findViewById(R.id.editProfileSave);
        editAvatar = (ImageView) findViewById(R.id.editAvatar);

        editProfileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Isti dialog za save
            }
        });
        editAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}