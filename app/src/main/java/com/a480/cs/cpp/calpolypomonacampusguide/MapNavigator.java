package com.a480.cs.cpp.calpolypomonacampusguide;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This class is for aiding the real time navigation on google map
 * Created by wxy03 on 5/11/2017.
 */

public class MapNavigator {


    private final int ROUTE_COLOR =Color.argb(0xFF,46,155,202);
    private GoogleMap map;

    private final double MIN_TOLERANCE = 15f;

    private final double MAX_TOLERANCE = 15f;

    private LatLng destination;

    private LatLng lastPosition;

    private float bearing;

    private Polyline route;

    /**
     * This is the constructor of {@link MapNavigator}
     * @param map
     * @param origin
     * @param dest
     * @throws SocketTimeoutException
     * if the internet connection failed
     */
    public MapNavigator (GoogleMap map, LatLng origin,LatLng dest) throws SocketTimeoutException {
        this.map = map;
        destination = dest;
        getNewRoute(origin,dest);

    }

    /**
     * This method should be called whenever there is location update
     * @param curLoc
     * @return {@code true} if the navigation has done,
     * {@code false} if not
     * @throws SocketTimeoutException
     */
    public boolean updateCurLocation(LatLng curLoc) throws SocketTimeoutException
    {
        double distance_from_dest = SphericalUtil.computeDistanceBetween(curLoc,destination);
        if(distance_from_dest<=MIN_TOLERANCE)
            return true;
        else {
            double distance_from_last = SphericalUtil.computeDistanceBetween(curLoc, lastPosition);
            if (distance_from_last > MAX_TOLERANCE) {
                route.remove();
                getNewRoute(curLoc, destination);
            }
            return false;
        }
    }

    /**
     *
     * @param start
     * @param destination
     * @throws SocketTimeoutException
     */
    private void getNewRoute(LatLng start,LatLng destination) throws SocketTimeoutException {
        List routePointList;
        try {
            routePointList = new RouteRequest(start,destination).execute().get();
            if(routePointList==null)
                throw new SocketTimeoutException();
            else {
                lastPosition = start;
                if (routePointList.size() > 1)
                    bearing = (float) SphericalUtil.computeHeading((LatLng) routePointList.get(0), (LatLng) routePointList.get(1));
                route = map.addPolyline(new PolylineOptions().addAll(routePointList).width(10).color(ROUTE_COLOR));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is used to get the appropriate camera position and bearing during the navigation
     * @param curLocation
     * @return
     */
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
