package com.gmijo.mytour.database;

import static com.gmijo.mytour.database.SQLiteVillageController.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class SQLiteVillageDataHelper {
    Context context;
    SQLiteVillageController liteVillageController;
    List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> data = new ArrayList<>();

    SQLiteDatabase liteDatabase;

    public SQLiteVillageDataHelper(Context context){

        this.context = context;
        liteVillageController = new SQLiteVillageController(context);

    }
    public  List<Pair<Pair<String, String>, Pair<Pair<String, String>, Pair<String, String>>>> getData(){
        liteDatabase = liteVillageController.getWritableDatabase();
        try {
                int i = 0;
                String check_query = "SELECT * FROM " + TAB_V_NAME;
                Cursor cursor = null;
                if (liteDatabase != null) {
                    cursor = liteDatabase.rawQuery(check_query, null);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            data.add(i, new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_VILLAGE)), cursor.getString(cursor.getColumnIndex(COL_TYPE))), new Pair(new Pair(cursor.getString(cursor.getColumnIndex(COL_SDESC)), cursor.getString(cursor.getColumnIndex(COL_LDESC))), new Pair( cursor.getString(cursor.getColumnIndex(COL_N_CITY)), cursor.getString(cursor.getColumnIndex(COL_LINK))))));
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
