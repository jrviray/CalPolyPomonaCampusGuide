package com.a480.cs.cpp.calpolypomonacampusguide;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by king on 5/1/17.
 */
public class StartAsyncTask extends AsyncTask<Void, Void, String> {
        String url;

        StartAsyncTask(String urlPass) {
            super();
            url = urlPass;
        }
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        protected void onPostExecute(String result) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            Log.d("JSON", json);
            super.onPostExecute(result);
        }
}


