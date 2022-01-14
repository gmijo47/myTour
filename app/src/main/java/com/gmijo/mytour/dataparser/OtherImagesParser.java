package com.gmijo.mytour.dataparser;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class OtherImagesParser extends AsyncTask<String, Void, Void> {

    String url;
    int position;
    //Interface za async task response
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;


    public OtherImagesParser(AsyncResponse delegate, int pos){
        this.delegate = delegate;
        this.position = pos;
    }

    @Override
    protected Void doInBackground(String... data) {

        try {

            Document doc = Jsoup.connect(String.valueOf(data[0])).get();
            Log.e("LINKall", String.valueOf(data[0]));


            //Parsovanje html elemenata
            Element htmlElement = doc.select("img").get(position);
            String hElement = htmlElement.toString();

            //Selectovanje i filtriranje urla
            int startIndex = hElement.indexOf("src=\"");
            int endIndex = hElement.indexOf("\" alt=", startIndex);

            //Vraćanje rezultata async taska
           delegate.processFinish(hElement.substring(startIndex + 5, endIndex));




        }catch (Exception e){
            Log.e("err", String.valueOf(e));
            e.printStackTrace();


        }
        return null;
    }
}
