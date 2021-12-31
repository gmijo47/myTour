package com.gmijo.mytour.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class SQLiteCityController extends SQLiteOpenHelper {

    //Podatci o bazi, kolonama, tabeli
    public static final String DATABASE_NAME = "gradoviba";
    public static final String TAB_C_NAME = "gradovi";
    public static final int DATABASE_VERSION = 1;
    public static final String COL_CITY = "city";
    public static final String COL_SDESC = "short_desc";
    public static final String COL_LDESC =  "long_desc";
    public static final String COL_LINK =  "link";
    public static final String COL_POP = "population";
    public static final String COL_ID = "_id";

    //Kreiranje baze
    public SQLiteCityController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //onCreate meteoda vrši kreiranje tabela i odgovarajučih kolona
    @Override
    public void onCreate(SQLiteDatabase liteDatabase){

       //String create_query = "CREATE TABLE " + TAB_C_NAME +"(" +
       //        COL_CITY + " VARCHAR(17) NOT NULL," +
       //        COL_POP + 	" NUMERIC(9, 3)," +
       //        COL_SDESC +	" VARCHAR(49)," +
       //        COL_LDESC +	" VARCHAR(1128)," +
       //        COL_LINK + 	" VARCHAR(2)," +
       //        COL_ID +	 "INTEGER PRIMARY KEY AUTOINCREMENT); ";
       //try {

       //    //Kreiranje tabele
       //    liteDatabase.execSQL(create_query);

       //} catch (Exception e){

       //    //Error
       //    Log.e("Error", String.valueOf(e));
       //}
    }

    //Upgrade metoda, kada se mijenja verzija baze, obriše tabelu
    @Override
    public void onUpgrade(SQLiteDatabase liteDatabase, int oldVersion, int newVersion){
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_C_NAME);
        onCreate(liteDatabase);

    }

}