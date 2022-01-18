package com.gmijo.mytour.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteAttractionController extends SQLiteOpenHelper{


        //Podatci o bazi, kolonama, tabeli
        public static final String ATTRACTION_DATABASE_NAME = "atrakcijeba";
        public static final String TAB_A_NAME = "atrakcije";
        public static final int DATABASE_VERSION = 1;
        public static final String COL_ATTRACTION = "attraction";
        public static final String COL_SDESC = "short_desc";
        public static final String COL_LDESC =  "long_desc";
        public static final String COL_LINK =  "link";
        public static final String COL_TYPE = "type";
        public static final String COL_N_CITY = "ncity";
        public static final String COL_ID = "_id";

        //Kreiranje baze
        public SQLiteAttractionController(Context context) {
            super(context, ATTRACTION_DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase liteDatabase){

        }

        //Upgrade metoda, kada se mijenja verzija baze, obriše tabelu
        @Override
        public void onUpgrade(SQLiteDatabase liteDatabase, int oldVersion, int newVersion){
            liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_A_NAME);
            onCreate(liteDatabase);

        }

}
