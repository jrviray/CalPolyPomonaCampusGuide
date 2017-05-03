package com.a480.cs.cpp.calpolypomonacampusguide;


import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by wxy03 on 5/1/2017.
 */

public class MapController {

     private GoogleMap map;

    private boolean locationPermission;

    private Location curLocation;

    private boolean isNormalMode;

    private List onMap_dataEntryList;

    private List onMap_markerList;

    private AppCompatActivity mainActivity;

    private List routePoint;

    private CameraPosition navigationCamera;

    private CameraPosition normalCamera;

    private Polyline routeLine;

    private GoogleMap.CancelableCallback cameraCallback;

    private boolean isCameraFollowing;


    public MapController(GoogleMap map, boolean permission,AppCompatActivity mainActivity)
    {

        this.map = map;
        isCameraFollowing = false;
        this.mainActivity = mainActivity;
        cameraCallback = new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                isCameraFollowing = true;
            }

            @Override
            public void onCancel() {
                isCameraFollowing = false;
            }
        };
        setPermission(permission);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showInfoDialog(marker);
                return false;
            }
        });

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                isCameraFollowing = false;
            }
        });
        enterNormalMode(new LatLng(34.0565,-117.821));
    }

    public void setPermission(boolean permission)
    {
        locationPermission=permission;
        FloatingActionButton myLocationButton = (FloatingActionButton) mainActivity.findViewById(R.id.b_my_location_button);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (curLocation != null)
                {
                    if(isNormalMode)
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(normalCamera),cameraCallback);
                    else
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(navigationCamera),cameraCallback);
                }
                isCameraFollowing = true;
            }
        });
        try{
            if(locationPermission)
            {
                    map.setMyLocationEnabled(true);
            }
            else {
                map.setMyLocationEnabled(false);
                myLocationButton.setVisibility(View.GONE);
            }
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
    }

    public void enterNormalMode(LatLng centerLatLng)
    {
        mainActivity.findViewById(R.id.b_naviagtion_exit_button).setVisibility(View.GONE);
        isNormalMode = true;
        //when enter the normal view, always centers Cap Poly Pomona
        if(curLocation==null)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.0565284,-117.8215295),18));
    }

    public void enterNavigationMode(LatLng destination) throws ExecutionException, InterruptedException {
        removeMarkersFromMap();
        isNormalMode = false;
        LatLng origin = getCurLocation();
        getRoute(origin,destination);
        FloatingActionButton exitButton = (FloatingActionButton) mainActivity.findViewById(R.id.b_naviagtion_exit_button);
        exitButton.setVisibility(View.VISIBLE);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitNavigation();
            }
        });
    }

    private void exitNavigation()
    {
        routeLine.remove();
        routeLine=null;
        routePoint=null;
        navigationCamera=null;
        enterNormalMode(getCurLocation());
        changeMarkersOnMap(onMap_dataEntryList);
    }



    private void getRoute(LatLng start,LatLng destination) throws ExecutionException, InterruptedException {
        routePoint =  new StartAsyncTask(start,destination).execute().get();
    }


    public void changeMarkersOnMap(List after_filter_list)
    {
        removeMarkersFromMap();
        onMap_dataEntryList = after_filter_list;

        if(after_filter_list!=null) {
            onMap_markerList = new ArrayList();
            for (int i = 0; i < after_filter_list.size(); i++) {
                DataEntry thisEntry = (DataEntry) after_filter_list.get(i);
                Marker newMarker = map.addMarker(new MarkerOptions().position(thisEntry.getLocation()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                newMarker.setTag(thisEntry);
                onMap_markerList.add(newMarker);
            }
        }
    }

    private void removeMarkersFromMap()
    {
        if(onMap_markerList!=null)
        {
            for(int i=0;i<onMap_markerList.size();i++)
            {
                Marker thisMarker = (Marker)onMap_markerList.get(i);
                thisMarker.remove();
            }
        }
    }


    private void realTimeRouting()
    {
        if(routePoint!=null)
        {
            if(routeLine==null)
            {
                routeLine = map.addPolyline(new PolylineOptions().addAll(routePoint));
                routeLine.setColor(R.color.polyline);
                routeLine.setWidth(20);





            }
            else
            {
                //update animation
            }


        }
    }



    public void curLocationUpdate(Location newLocation)
    {
        curLocation = newLocation;
        if(isNormalMode)
        {
            normalCamera = new CameraPosition.Builder().target(getCurLocation()).zoom(18).build();
            if(isCameraFollowing)
                map.animateCamera(CameraUpdateFactory.newCameraPosition(normalCamera),cameraCallback);
        }

        //navigation animation
        else
        {
            float bearing;
            if(routePoint!=null)
            {
                bearing = (float) SphericalUtil.computeHeading((LatLng)routePoint.get(0),(LatLng)routePoint.get(1));
                boolean justEnterNavigation = navigationCamera == null;
                navigationCamera = new CameraPosition.Builder().target(getCurLocation()).zoom(19).bearing(bearing).build();

                if(justEnterNavigation || isCameraFollowing)
                {
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(navigationCamera),cameraCallback);
                }
            }
            realTimeRouting();
        }
    }

    /**
     * Thi method will be called when a marker is clicked by user
     * @param marker
     */
    private void showInfoDialog(Marker marker) {
        final DataEntry entry = (DataEntry) marker.getTag();
        final MaterialDialog infoDialog = new MaterialDialog.Builder(mainActivity).customView(R.layout.info_layout, true)
                .title(entry.getTitle()).show();
        View infoView = infoDialog.getCustomView();
        TextView description = (TextView) infoView.findViewById(R.id.tv_entry_description);
        description.setText(entry.getDescription());
        ImageView image = (ImageView) infoView.findViewById(R.id.iv_entry_image);
        image.setImageResource(mainActivity.getResources().getIdentifier(entry.getImageName(), "mipmap",mainActivity.getPackageName()));

        Button routeButton = (Button) infoView.findViewById(R.id.b_route_button);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationPermission && curLocation!=null) {
                    try {
                        enterNavigationMode(entry.getLocation());
                        infoDialog.cancel();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public LatLng getCurLocation()
    {
        if(curLocation==null)
            return null;
        else
        return new LatLng(curLocation.getLatitude(),curLocation.getLongitude());
    }


}
