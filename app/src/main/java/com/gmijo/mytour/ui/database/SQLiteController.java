package com.gmijo.mytour.ui.database;



import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SQLiteController extends SQLiteOpenHelper{
    //Verzija baze
    private static final int DB_VER = 1;

    //Podatci o bazi, kolonama, tabeli
    private static final String DB_NAME = "users.db";
    private static final String TAB_NAME = "userData";

    private static final String COL_NO = "_no";
    private static final String COL_GPUUID = "googleUUID";
    private static final String COL_FNAME = "fullName";
    private static final String COL_USERNAME = "username";

    //Query komande
    String create_query;

    //Varijabla za indikaciju izvršenosti querija, uspješan/neuspješan
    long result;


    //Kreiranje baze
    public SQLiteController(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    //onCreate meteoda vrši kreiranje table i odgovarajučih kolona, odnosno googlePlayId-a, punog imena, te korisničkog imena
    @Override
    public void onCreate(SQLiteDatabase liteDatabase) {
        create_query = "CREATE TABLE " + TAB_NAME +
                " (" + COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_GPUUID + " TEXT, " + COL_FNAME + " TEXT, " + COL_USERNAME + " TEXT); ";
        liteDatabase.execSQL(create_query);

    }
    //Upgrade metoda, kada se mijenja verzija baze, obriše tabelu
    @Override
    public void onUpgrade(SQLiteDatabase liteDatabase, int oldVersion, int newVersion) {
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_NAME );
            onCreate(liteDatabase);
    }

    //Metoda za insetrovanje podataka u bazu (lokalnu) na registeru
    public void registerUser(String gUUID, String fName, String username){
        SQLiteDatabase liteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_GPUUID, gUUID);
        values.put(COL_FNAME, fName);
        values.put(COL_USERNAME, username);

       result  = liteDatabase.insert(TAB_NAME, null, values);
        if (result == -1){
            Log.d("SQLlite", "Podatci NISU insertovani");
        }else {
            Log.d("SQLlite", "Podatci SU insertovani");
        }
    }
    //Metoda koja vrača rezultat querija uspješan/neuspješan
    public long getResult(){
        return result;
    }
}