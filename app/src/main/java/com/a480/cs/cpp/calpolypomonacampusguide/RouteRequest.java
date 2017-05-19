package com.a480.cs.cpp.calpolypomonacampusguide;

import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by king on 5/1/17.
 */
public class RouteRequest extends AsyncTask<Void, Void, List> {
        private LatLng startPoint;
        private LatLng endPoint;

        private boolean connectionFail;

        RouteRequest(LatLng start, LatLng end) {
            super();
            startPoint=start;
            endPoint=end;
            connectionFail=false;
        }
        protected List doInBackground(Void... params)  {
             JSONParser jParser= new JSONParser();
            String json = null;
            try {
                json = jParser.getJSONFromUrl(jParser.makeURL(startPoint.latitude,startPoint.longitude,endPoint.latitude,endPoint.longitude));
            } catch (SocketTimeoutException | UnknownHostException e) {
                connectionFail=true;
            }
            finally {
                if(connectionFail)
                    return null;
                else
                    return jParser.getList(json);
            }

        }
}


