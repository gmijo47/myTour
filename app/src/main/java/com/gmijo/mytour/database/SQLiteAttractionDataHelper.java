package com.gmijo.mytour.database;

import static com.gmijo.mytour.database.SQLiteAttractionController.*;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SQLiteAttractionDataHelper {
    Context context;
    SQLiteAttractionController liteAttractionController;
    List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> data = new ArrayList<>();
    SQLiteDatabase liteDatabase;
    List<Integer> brojevi = new ArrayList<>();


    public SQLiteAttractionDataHelper(Context context){

        this.context = context;
        liteAttractionController = new SQLiteAttractionController(context);

    }
    public void setBrojevi() {
        for(int i = 0; i < 10; i++){
            Random random = new Random();
            brojevi.add(i, random.nextInt(268));
        }
    }
    public  List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> getDataByCity(String cityName){
        liteDatabase = liteAttractionController.getWritableDatabase();
        try {
                int i = 0;
                String check_query = "SELECT * FROM " + TAB_A_NAME +  " WHERE " + COL_N_CITY + " = " + "'" + cityName+ "'" ;
                Cursor cursor = null;
                if (liteDatabase != null) {
                    cursor = liteDatabase.rawQuery(check_query, null);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            data.add(i, new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_ATTRACTION)), cursor.getString(cursor.getColumnIndex(COL_TYPE))), new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_SDESC)), cursor.getString(cursor.getColumnIndex(COL_LDESC))), new Pair( cursor.getString(cursor.getColumnIndex(COL_N_CITY)), cursor.getString(cursor.getColumnIndex(COL_LINK))))));
                            i++;
                        }
                    } else {
                       data = null;
                    }
                }
        } catch(Exception e){
            Log.e("SQLite", e.toString());
            e.printStackTrace();

        }
        return  data;
    }
 public  List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> getData(){
     setBrojevi();
     liteDatabase = liteAttractionController.getWritableDatabase();
     try {
         for (int i = 0; i < brojevi.size();) {
             String check_query = "SELECT * FROM " + TAB_A_NAME +  " WHERE " + COL_ID + " = " + brojevi.get(i);
             Cursor cursor = null;
             if (liteDatabase != null) {
                 cursor = liteDatabase.rawQuery(check_query, null);
                 if (cursor.getCount() != 0) {
                     while (cursor.moveToNext()) {
                         data.add(i, new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_ATTRACTION)), cursor.getString(cursor.getColumnIndex(COL_TYPE))), new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_SDESC)), cursor.getString(cursor.getColumnIndex(COL_LDESC))), new Pair( cursor.getString(cursor.getColumnIndex(COL_N_CITY)), cursor.getString(cursor.getColumnIndex(COL_LINK))))));
                         i++;
                     }
                 } else {
                     break;
                 }
             }
         }
     } catch(Exception e){
         Log.e("SQLite", e.toString());
         e.printStackTrace();

     }
     return  data;
 }

    public  List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> exploreQuery(String query){
        liteDatabase = liteAttractionController.getWritableDatabase();
        try {
                int i = 0;
                String check_query = "SELECT * FROM " + TAB_A_NAME +  " WHERE lower(" + COL_ATTRACTION + ")" + " LIKE '%" + query.toLowerCase() + "%'" + " OR lower(" + COL_N_CITY + ")" + " LIKE '%" + query.toLowerCase() + "%'";
                Cursor cursor = null;
                if (liteDatabase != null) {
                    cursor = liteDatabase.rawQuery(check_query, null);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            data.add(i, new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_ATTRACTION)), cursor.getString(cursor.getColumnIndex(COL_TYPE))), new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_SDESC)), cursor.getString(cursor.getColumnIndex(COL_LDESC))), new Pair( cursor.getString(cursor.getColumnIndex(COL_N_CITY)), cursor.getString(cursor.getColumnIndex(COL_LINK))))));
                            i++;
                        }
                    } else {

                }
            }
        } catch(Exception e){
            Log.e("SQLite", e.toString());
            e.printStackTrace();

        }
        return  data;
    }
}
