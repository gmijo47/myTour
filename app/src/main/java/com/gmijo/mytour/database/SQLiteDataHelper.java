package com.gmijo.mytour.database;

import static com.gmijo.mytour.database.SQLiteController.COL_CITYEXP;
import static com.gmijo.mytour.database.SQLiteController.COL_COUNT;
import static com.gmijo.mytour.database.SQLiteController.COL_FNAME;
import static com.gmijo.mytour.database.SQLiteController.COL_GPUUID;
import static com.gmijo.mytour.database.SQLiteController.COL_GROUP;
import static com.gmijo.mytour.database.SQLiteController.COL_MYTT;
import static com.gmijo.mytour.database.SQLiteController.COL_NAPEXP;
import static com.gmijo.mytour.database.SQLiteController.COL_NATEXP;
import static com.gmijo.mytour.database.SQLiteController.COL_NO;
import static com.gmijo.mytour.database.SQLiteController.COL_USERNAME;
import static com.gmijo.mytour.database.SQLiteController.COL_VILEXP;
import static com.gmijo.mytour.database.SQLiteController.TAB_USER_DATA;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;


import com.gmijo.mytour.ui.leaderboard.nav_fragments.navInterface;
import com.gmijo.mytour.ui.profil.interfaceProfilFragment;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDataHelper {
    private Context context;
    SQLiteController sqLiteController;
    ArrayList<String> data = new ArrayList<String>();
    int result;

    private interfaceProfilFragment interfaceProfilFragment = null;
    private navInterface navInterface = null;

    public SQLiteDataHelper(Context context){
        this.context = context;
        sqLiteController = new SQLiteController(context);

    }
    public void setDisplayer(interfaceProfilFragment d) {
        interfaceProfilFragment = d;
    }
    public void setFragmentDisplayer(navInterface d) {
        navInterface = d;
    }
    //Metoda za insetrovanje podataka u bazu (lokalnu) na registeru
    public void registerUser(String gUUID, String fName, String username, int cityExplored, int nacionalParkExplored, int naturePointExplored, int villageExplored, int mytourTokens, String group, Boolean refresh){
        try {
            boolean dataUpdate = checkLocalData(gUUID);
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

            if (dataUpdate){

                int res = (int) liteDatabase.update(TAB_USER_DATA, values, COL_GPUUID + " = " + "'" + gUUID + "'", null);
                    if (res == 1){
                        ArrayList<String> data = getData(gUUID);
                        if (data != null) {
                            if (interfaceProfilFragment != null) {
                                interfaceProfilFragment.displayData(data);
                                interfaceProfilFragment.ErrDialog("rsState");
                            }
                        }
                    }

            } else {

                result = (int) liteDatabase.insert(TAB_USER_DATA, null, values);
                if (result == 1) {
                    if (refresh) {
                        ArrayList<String> data = getData(gUUID);
                        if (data != null) {
                            if (interfaceProfilFragment != null) {
                                interfaceProfilFragment.displayData(data);
                                interfaceProfilFragment.ErrDialog("rsState");
                            }
                        }
                    }
                    Log.d("SQLlite", "Podatci NISU insertovani");
                } else {
                    Log.d("SQLlite", "Podatci SU insertovani");
                }
            }
        }catch (Exception e){
            if (interfaceProfilFragment != null) {
                interfaceProfilFragment.setError("errFailedToGetData");
            }

        }
    }
    //Metoda koja vrača rezultat querija uspješan/neuspješan
    public long getResult(){
        return result;
    }

    public boolean checkLocalData(String  userUUID){
        boolean ret = false;
        try{
        String check_query = "SELECT * FROM " + TAB_USER_DATA + " WHERE " + COL_GPUUID + " = '" + userUUID +"'";
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

            if (interfaceProfilFragment != null) {
                interfaceProfilFragment.setError("errFailedToGetData");
            }

        }
       return ret;
    }

    //Metoda koja dobavlja podatke iz lokalne baze, pakuje ih u array list i vraća podatke u arraylistu
    public ArrayList<String> getData(String  userUUID){
        try {
            String check_query = "SELECT * FROM " + TAB_USER_DATA + " WHERE " + COL_GPUUID + " = '" + userUUID + "'";
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
                    if (interfaceProfilFragment != null) {
                        interfaceProfilFragment.setError("errFailedToGetData");
                    }
                }
            }
        } catch (Exception e){
            if (interfaceProfilFragment != null) {
                interfaceProfilFragment.setError("errFailedToGetData");
            }

        }
        return  data;
    }

    //Metoda za provjeru postojanja tabele za leaderboard
    public boolean checkTableDatExt(String table_name){

        boolean ret = false;
        try{
            String check_query = "SELECT * FROM " + table_name;
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

            Log.d("SQLite", "task failed!");

        }
        return ret;
    }

    //Metoda za insert podataka za leaderboard
    public void insertData(String table, String userCol, String countCol, String username, String count, boolean update) {
        try {
                    SQLiteDatabase liteDatabase = sqLiteController.getWritableDatabase();
                    if (liteDatabase != null) {

                        //Query za insertovanje podataka, smiješta podatke u contentvalues, ubacuje u bazu
                        ContentValues values = new ContentValues();
                        values.put(userCol, username);
                        values.put(countCol, count);

                        long result = liteDatabase.insert(table, null, values);

                        if (result == 1){
                            displayHelper();
                        }

                    }

        }catch (Exception e){

                  if (navInterface != null){

                      //Prikaz erora
                      navInterface.setError();

                  }

        }

    }

    private void displayHelper(){
        if (navInterface != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //Pikaz podataka nakon 1000ms
                    navInterface.displayData();

                }
            }, 1000);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //Pikaz podataka nakon 1000ms
                  displayHelper();

                }
            }, 5000);
        }
    }
    public void updateData(String table, String username, String count, int _no){

        SQLiteDatabase liteDatabase = sqLiteController.getWritableDatabase();

        //Query za update podataka
        String update_query = "UPDATE " + table + " SET " + COL_USERNAME + "=" + "'" + username + "'" + "," +
                " " + COL_COUNT + "=" + "'" + count + "'" + " WHERE " + COL_NO + "=" + "'" + _no + "'";

            try{

                //Execute querija
                liteDatabase.execSQL(update_query);

            }catch (Exception e){

                //Loguje exeception
                Log.e("SQLite", String.valueOf(e));

            }
    }

    //Metoda za izvlacenje podataka iz baze na osnovu imena tabele
    public List<Pair<String, String>> obtainData(String table){

        List<Pair<String, String>> data = new ArrayList<>();
        SQLiteDatabase liteDatabase = sqLiteController.getReadableDatabase();

        if (liteDatabase != null) {

            //Query za citanje iz baze
            String query_read = "SELECT " + COL_USERNAME + ", " + COL_COUNT + " FROM " + table;

            //Executer querija
            Cursor cursor = liteDatabase.rawQuery(query_read, null);
            if (cursor.getCount() != 0){
                //Loop kroz cursor
                while (cursor.moveToNext()){

                   //Dodavanje u listu u vidu parova
                    data.add(new Pair(cursor.getString(cursor.getColumnIndex(COL_USERNAME)), cursor.getString(cursor.getColumnIndex(COL_COUNT))));

                }
            }

        }

        //Vraca listu
        return data;

    }

}

