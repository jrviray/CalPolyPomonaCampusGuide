package com.a480.cs.cpp.calpolypomonacampusguide;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by king on 5/1/17.
 */
public class StartAsyncTask extends AsyncTask<Void, Void, List> {
        private LatLng startPoint;
        private LatLng endPoint;

        StartAsyncTask(LatLng start,LatLng end) {
            super();
            startPoint=start;
            endPoint=end;
        }
        protected List doInBackground(Void... params) {
             JSONParser jParser= new JSONParser();
            String json = jParser.getJSONFromUrl(jParser.makeURL(startPoint.latitude,startPoint.longitude,endPoint.latitude,endPoint.longitude));
            return jParser.getList(json);
        }

}


