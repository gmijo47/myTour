package com.gmijo.mytour.database;

import static com.gmijo.mytour.database.SQLiteAttractionController.COL_ATTRACTION;
import static com.gmijo.mytour.database.SQLiteAttractionController.COL_LDESC;
import static com.gmijo.mytour.database.SQLiteAttractionController.COL_LINK;
import static com.gmijo.mytour.database.SQLiteAttractionController.COL_N_CITY;
import static com.gmijo.mytour.database.SQLiteAttractionController.COL_SDESC;
import static com.gmijo.mytour.database.SQLiteAttractionController.COL_TYPE;
import static com.gmijo.mytour.database.SQLiteCityController.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SQLiteCityDataHelper {
    Context context;
    SQLiteCityController liteCityController;
    List<Pair<Pair<String, String>, Pair<String, Pair<String, String>>>> data = new ArrayList<>();
    List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> dataDouble = new ArrayList<>();
    List<Integer> brojevi =  new ArrayList<>();
    SQLiteDatabase liteDatabase;


    public SQLiteCityDataHelper(Context context){

        this.context = context;
        liteCityController = new SQLiteCityController(context);

    }
    public void setBrojevi() {
        for(int i = 0; i < 10; i++){
            Random random = new Random();
            brojevi.add(i, random.nextInt(101));
        }
    }
    public List<Pair<Pair<String, String>, Pair<String, Pair<String, String>>>> getData(){
        setBrojevi();
        liteDatabase = liteCityController.getWritableDatabase();
        try {
            for (int i = 0; i < brojevi.size();) {
                String check_query = "SELECT * FROM " + TAB_C_NAME +  " WHERE " + COL_ID + " = " + brojevi.get(i);
                Cursor cursor = null;
                if (liteDatabase != null) {
                    cursor = liteDatabase.rawQuery(check_query, null);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            data.add(i, new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_CITY)), cursor.getString(cursor.getColumnIndex(COL_POP))), new Pair(cursor.getString(cursor.getColumnIndex(COL_SDESC)), new Pair(cursor.getString(cursor.getColumnIndex(COL_LDESC)), cursor.getString(cursor.getColumnIndex(COL_LINK))))));
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
        liteDatabase = liteCityController.getReadableDatabase();
        data.clear();
        try{
            int i = 0;
            String exploreQuery = "SELECT * FROM " + TAB_C_NAME + " WHERE " + "lower("+ COL_CITY + ") LIKE " + "'%"+ query.toLowerCase() +"%'";
            Cursor cursor = null;
            if (liteDatabase != null){
                cursor = liteDatabase.rawQuery(exploreQuery, null);
                if(cursor.getCount() != 0){
                    while (cursor.moveToNext()){
                        dataDouble.add(i, new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_CITY)), "city"), new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_SDESC)), cursor.getString(cursor.getColumnIndex(COL_LDESC))), new Pair(null, cursor.getString(cursor.getColumnIndex(COL_LINK))))));
                        i++;
                    }
                }
            }

        }catch (Exception e){

            e.printStackTrace();
            Log.e("SQLite", e.toString());

        }
        return dataDouble;
    }


}
