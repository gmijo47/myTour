package com.gmijo.mytour.database;




import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

public class SQLiteController extends SQLiteOpenHelper{
    //Verzija baze
    private static final int DB_VER = 1;

    //Podatci o bazi, kolonama, tabeli
    public static final String DB_NAME = "users.db";
    public static final String TAB_NAME = "userData";
    public static final String COL_NO = "_no";
    public static final String COL_GPUUID = "googleUUID";
    public static final String COL_FNAME = "fullName";
    public static final String COL_USERNAME = "username";
    public static final String COL_CITYEXP = "cities_explored";
    public static final String COL_NAPEXP = "nacionalpark_explored";
    public static final String COL_NATEXP = "naturepoint_explored";
    public static final String COL_VILEXP = "villages_explored";
    public static final String COL_MYTT = "mytour_tokens";
    public static final String COL_GROUP = "userGroup";

    //Query komande
    String create_query, check_query;

    //Kreiranje baze
    public SQLiteController(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    //onCreate meteoda vrši kreiranje table i odgovarajučih kolona
    @Override
    public void onCreate(SQLiteDatabase liteDatabase) {
        create_query = "CREATE TABLE " + TAB_NAME +
                " (" + COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_GPUUID + " TEXT, " + COL_FNAME + " TEXT, " + COL_USERNAME + " TEXT, " +
                COL_CITYEXP + " INTEGER, " + COL_NAPEXP + " INTEGER, " + COL_NATEXP + " INTEGER, " + COL_VILEXP + " INTEGER, " + COL_MYTT + " INTEGER, " + COL_GROUP + " TEXT); ";
        liteDatabase.execSQL(create_query);

    }
    //Upgrade metoda, kada se mijenja verzija baze, obriše tabelu
    @Override
    public void onUpgrade(SQLiteDatabase liteDatabase, int oldVersion, int newVersion) {
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_NAME );
            onCreate(liteDatabase);
    }


}