package com.gmijo.mytour.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteVillageController extends SQLiteOpenHelper{


    //Podatci o bazi, kolonama, tabeli
    public static final String VILLAGE_DATABASE_NAME = "selaba";
    public static final String TAB_V_NAME = "sela";
    public static final int DATABASE_VERSION = 1;
    public static final String COL_VILLAGE = "village";
    public static final String COL_SDESC = "sdesc";
    public static final String COL_LDESC =  "ldesc";
    public static final String COL_LINK =  "link";
    public static final String COL_N_CITY = "ncity";
    public static final String COL_TYPE= "type";
    public static final String COL_ID = "_id";

    //Kreiranje baze
    public SQLiteVillageController(Context context) {
        super(context, VILLAGE_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase liteDatabase){
    }

    //Upgrade metoda, kada se mijenja verzija baze, obriše tabelu
    @Override
    public void onUpgrade(SQLiteDatabase liteDatabase, int oldVersion, int newVersion){
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_V_NAME);
        onCreate(liteDatabase);

    }

}
