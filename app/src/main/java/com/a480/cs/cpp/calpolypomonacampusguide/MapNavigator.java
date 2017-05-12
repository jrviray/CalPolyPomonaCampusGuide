package com.a480.cs.cpp.calpolypomonacampusguide;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This class is for aiding the real time navigation on google map
 * Created by wxy03 on 5/11/2017.
 */

public class MapNavigator {


    private final int ROUTE_COLOR =Color.argb(0xFF,46,155,202);
    private GoogleMap map;

    private final double MIN_TOLERANCE = 5f;

    private final double MAX_TOLERANCE = 15f;

    private LatLng destination;

    private LatLng lastPosition;


    private float bearing;

    private Polyline route;

    public MapNavigator(GoogleMap map, LatLng origin,LatLng dest)
    {
        this.map = map;
            getNewRoute(origin,dest);

    }

    /**
     *
     * @param curLoc
     * @return {@code true} if the navigation has done,
     * {@code false} if not
     */
    public boolean updateCurLocation(LatLng curLoc)
    {
        double distance_from_dest = SphericalUtil.computeDistanceBetween(curLoc,destination);
        if(distance_from_dest<=MIN_TOLERANCE)
            return true;
        else {
            double distance_from_last = SphericalUtil.computeDistanceBetween(curLoc, lastPosition);
            if (distance_from_last > MAX_TOLERANCE) {
                route.remove();
                getNewRoute(curLoc, destination);
                Log.d("request", "request");

            }
            return false;
        }
    }


    private void getNewRoute(LatLng start,LatLng destination) {
        List routePointList = null;
        try {
            routePointList = new RouteRequest(start,destination).execute().get();
            lastPosition=start;
            this.destination= (LatLng) routePointList.get(routePointList.size()-1);
            if(routePointList.size()>1)
            bearing = (float) SphericalUtil.computeHeading((LatLng)routePointList.get(0), (LatLng)routePointList.get(1));
            route = map.addPolyline(new PolylineOptions().addAll(routePointList).width(10).color(ROUTE_COLOR));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public CameraPosition getNavigationCamera(LatLng curLocation)
    {
        if(route!=null) {
            return new CameraPosition.Builder().target(curLocation).zoom(19).bearing(bearing).build();
        }
        else
            return null;
    }

    public void exit()
    {
        route.remove();
    }
}
