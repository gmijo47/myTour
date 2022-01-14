package com.gmijo.mytour.dataparser;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FeaturedImgParser extends AsyncTask<String, Void, Void> {

    //Interface za async task
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public FeaturedImgParser(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(String... rawUrl) {
        try {

            //Otvara url
            Document doc = Jsoup.connect(rawUrl[0]).get();
            Log.e("LINKall", String.valueOf(rawUrl[0]));

            //Parsovanje html elemenata
            Element htmlElement = doc.select("img").first();
            String hElement = htmlElement.toString();

           //Selectovanje i filtriranje urla
           int startIndex = hElement.indexOf("src=\"");
           int endIndex = hElement.indexOf("\" alt=", startIndex);
           String imageUrl = hElement.substring(startIndex + 5, endIndex);


           //Vraća url
           delegate.processFinish(imageUrl);


        }catch (Exception e){
            Log.e("err", String.valueOf(e));
            e.printStackTrace();


        }
        return null;
    }
}
