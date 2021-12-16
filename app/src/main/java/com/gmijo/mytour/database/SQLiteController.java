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

    public static final String TAB_USER_DATA = "userData";
    public static final String TAB_TOKEN_STATS = "tokenStats";
    public static final String TAB_CITY_STATS = "cityStats";
    public static final String TAB_VILLAGE_STATS = "villageStats";
    public static final String TAB_NPOINT_STATS = "npointStats";
    public static final String TAB_NPARK_STATS = "nparkStats";

    public static final String COL_NO = "_no";
    public static String COL_GPUUID = "googleUUID";
    public static final String COL_FNAME = "fullName";
    public static final String COL_USERNAME = "username";
    public static final String COL_CITYEXP = "cities_explored";
    public static final String COL_NAPEXP = "nacionalpark_explored";
    public static final String COL_NATEXP = "naturepoint_explored";
    public static final String COL_VILEXP = "villages_explored";
    public static final String COL_MYTT = "mytour_tokens";
    public static final String COL_GROUP = "userGroup";
    public static final String COL_COUNT = "data_count";

    //Query stringovi
    String create_user_query, create_token_stats_query, create_city_stats_query, create_village_stats_query,
            create_npoint_stats_query, create_npark_stats_query;

    //Kreiranje baze
    public SQLiteController(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    //onCreate meteoda vrši kreiranje tabela i odgovarajučih kolona
    @Override
    public void onCreate(SQLiteDatabase liteDatabase) {

        //Query komande
        create_user_query = "CREATE TABLE " + TAB_USER_DATA +
                " (" + COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_GPUUID + " TEXT, " + COL_FNAME + " TEXT, " + COL_USERNAME + " TEXT, " +
                COL_CITYEXP + " INTEGER, " + COL_NAPEXP + " INTEGER, " + COL_NATEXP + " INTEGER, " + COL_VILEXP + " INTEGER, " + COL_MYTT + " INTEGER, " + COL_GROUP + " TEXT); ";
        create_token_stats_query = "CREATE TABLE " + TAB_TOKEN_STATS +
                " (" + COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME + " TEXT, " + COL_COUNT + " INTEGER); ";
        create_city_stats_query = "CREATE TABLE " + TAB_CITY_STATS +
                " (" + COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME + " TEXT, " + COL_COUNT + " INTEGER); ";
        create_village_stats_query = "CREATE TABLE " + TAB_VILLAGE_STATS +
                " (" + COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME + " TEXT, " + COL_COUNT + " INTEGER); ";
        create_npark_stats_query = "CREATE TABLE " + TAB_NPARK_STATS +
                " (" + COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME + " TEXT, " + COL_COUNT + " INTEGER); ";
        create_npoint_stats_query = "CREATE TABLE " + TAB_NPOINT_STATS +
                " (" + COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME + " TEXT, " + COL_COUNT + " INTEGER); ";

        //Execution queryija iznad
        liteDatabase.execSQL(create_user_query);
        liteDatabase.execSQL(create_token_stats_query);
        liteDatabase.execSQL(create_city_stats_query);
        liteDatabase.execSQL(create_village_stats_query);
        liteDatabase.execSQL(create_npark_stats_query);
        liteDatabase.execSQL(create_npoint_stats_query);

    }
    //Upgrade metoda, kada se mijenja verzija baze, obriše tabelu
    @Override
    public void onUpgrade(SQLiteDatabase liteDatabase, int oldVersion, int newVersion) {
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_USER_DATA );
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_TOKEN_STATS );
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_CITY_STATS );
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_VILLAGE_STATS );
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_NPOINT_STATS );
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_NPARK_STATS );
            onCreate(liteDatabase);
    }


}