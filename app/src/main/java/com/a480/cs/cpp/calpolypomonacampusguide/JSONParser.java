package com.a480.cs.cpp.calpolypomonacampusguide;

import android.content.res.Resources;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.a480.cs.cpp.calpolypomonacampusguide.R.string.google_maps_key;

/**
 * Created by king on 5/1/17.
 */

public class JSONParser {
        InputStream is = null;
        String json = "";

        // constructor
        public JSONParser() {
        }

    public String makeURL(double sourcelat, double sourcelog, double destlat,
                          double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&mode=walking&key=");
        urlString.append(MainActivity.API_KEY);
        return urlString.toString();
    }

        public String getJSONFromUrl(String urlString) {
            HttpURLConnection urlConnection;
            // Making HTTP request
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                is = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();

                urlConnection.disconnect();
            }catch(IOException e) {
                e.printStackTrace();
            }
            return json;
        }

        public List getList(String result){
            List<LatLng>list = null;
            try {
                // Tranform the string into a json object
                final JSONObject json = new JSONObject(result);
                JSONArray routeArray = json.getJSONArray("routes");
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes
                        .getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                list = decodePoly(encodedString);

                return list;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }

