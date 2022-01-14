package com.gmijo.mytour.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteNParkController extends SQLiteOpenHelper{



        //Podatci o bazi, kolonama, tabeli
        public static final String NPARK_DATABASE_NAME = "nparkoviba";
        public static final String TAB_NP_NAME = "npark";
        public static final int DATABASE_VERSION = 1;
        public static final String COL_NP = "nacional_park";
        public static final String COL_SDESC = "short_desc";
        public static final String COL_LDESC =  "long_desc";
        public static final String COL_LINK =  "link";
        public static final String COL_TYPE =  "type";
        public static final String COL_N_CITY =  "ncity";
        public static final String COL_ID = "_id";

        //Kreiranje baze
        public SQLiteNParkController(Context context) {
            super(context, NPARK_DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase liteDatabase){

        }

        //Upgrade metoda, kada se mijenja verzija baze, obriše tabelu
        @Override
        public void onUpgrade(SQLiteDatabase liteDatabase, int oldVersion, int newVersion){
            liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_NP_NAME);
            onCreate(liteDatabase);

        }

}
