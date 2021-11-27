package com.gmijo.mytour.database;

import static com.gmijo.mytour.database.SQLiteController.COL_CITYEXP;
import static com.gmijo.mytour.database.SQLiteController.COL_FNAME;
import static com.gmijo.mytour.database.SQLiteController.COL_GPUUID;
import static com.gmijo.mytour.database.SQLiteController.COL_GROUP;
import static com.gmijo.mytour.database.SQLiteController.COL_MYTT;
import static com.gmijo.mytour.database.SQLiteController.COL_NAPEXP;
import static com.gmijo.mytour.database.SQLiteController.COL_NATEXP;
import static com.gmijo.mytour.database.SQLiteController.COL_USERNAME;
import static com.gmijo.mytour.database.SQLiteController.COL_VILEXP;
import static com.gmijo.mytour.database.SQLiteController.TAB_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gmijo.mytour.ui.profil.ProfilFragment;

import java.util.ArrayList;

public class SQLiteDataHelper {
    private Context context;
    ProfilFragment profilFragment;
    SQLiteController sqLiteController;
    ArrayList<String> data = new ArrayList<String>();
    int result;

    public SQLiteDataHelper(Context context){
        this.context = context;
        sqLiteController = new SQLiteController(context);
        profilFragment = new ProfilFragment();
    }
    //Metoda za insetrovanje podataka u bazu (lokalnu) na registeru
    public void registerUser(String gUUID, String fName, String username, int cityExplored, int nacionalParkExplored, int naturePointExplored, int villageExplored, int mytourTokens, String group, Boolean refresh){
        try {
            SQLiteDatabase liteDatabase = sqLiteController.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_GPUUID, gUUID);
            values.put(COL_FNAME, fName);
            values.put(COL_USERNAME, username);
            values.put(COL_CITYEXP, cityExplored);
            values.put(COL_NAPEXP, nacionalParkExplored);
            values.put(COL_NATEXP, naturePointExplored);
            values.put(COL_VILEXP, villageExplored);
            values.put(COL_MYTT, mytourTokens);
            values.put(COL_GROUP, group);

            result = (int) liteDatabase.insert(TAB_NAME, null, values);
            if (result == 1) {
                if (refresh){
                    ArrayList<String> data = getData(gUUID);
                    if (data != null){
                    profilFragment.displayData(data);
                    }
                }
                Log.d("SQLlite", "Podatci NISU insertovani");
            } else {
                Log.d("SQLlite", "Podatci SU insertovani");
            }
        }catch (Exception e){

            profilFragment.setError("errFailedToGetData");

        }
    }
    //Metoda koja vrača rezultat querija uspješan/neuspješan
    public long getResult(){
        return result;
    }

    public boolean checkLocalData(String  userUUID){
        boolean ret = false;
        try{
        String check_query = "SELECT * FROM " + TAB_NAME + " WHERE " + COL_GPUUID + " = '" + userUUID +"'";
        SQLiteDatabase liteDatabase = sqLiteController.getReadableDatabase();
        Cursor cursor = null;
        if (liteDatabase != null) {
            cursor = liteDatabase.rawQuery(check_query, null);
            cursor.moveToFirst();
        }
        if(cursor.getCount() == 0){
            ret = false;
        }else {
            ret = true;
        }
        }catch (Exception e){

            profilFragment.setError("errFailedToGetData");

        }
       return ret;
    }

    //Metoda koja dobavlja podatke iz lokalne baze, pakuje ih u array list i vraća podatke u arraylistu
    public ArrayList<String> getData(String  userUUID){
        try {
            String check_query = "SELECT * FROM " + TAB_NAME + " WHERE " + COL_GPUUID + " = '" + userUUID + "'";
            SQLiteDatabase liteDatabase = sqLiteController.getReadableDatabase();
            Cursor cursor = null;
            if (liteDatabase != null) {
                cursor = liteDatabase.rawQuery(check_query, null);
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        data.add(0, cursor.getString(cursor.getColumnIndex(COL_USERNAME)));
                        data.add(1, cursor.getString(cursor.getColumnIndex(COL_FNAME)));
                        data.add(2, cursor.getString(cursor.getColumnIndex(COL_GROUP)));
                        data.add(3, cursor.getString(cursor.getColumnIndex(COL_CITYEXP)));
                        data.add(4, cursor.getString(cursor.getColumnIndex(COL_VILEXP)));
                        data.add(5, cursor.getString(cursor.getColumnIndex(COL_NAPEXP)));
                        data.add(6, cursor.getString(cursor.getColumnIndex(COL_NATEXP)));
                        data.add(7, cursor.getString(cursor.getColumnIndex(COL_MYTT)));
                    }
                } else {
                    profilFragment.setError("errFailedToGetData");
                }
            }
        } catch (Exception e){

            profilFragment.setError("errFailedToGetData");

        }
        return  data;
    }
}
