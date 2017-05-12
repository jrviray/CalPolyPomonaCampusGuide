package com.a480.cs.cpp.calpolypomonacampusguide;


import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by wxy03 on 5/1/2017.
 */

public class MapController {

    private enum MODE {NORMAL,NAVIGATION};

     private GoogleMap map;

    private boolean locationPermission;

    private Location curLocation;

    private MODE mode;

    private FloatingActionButton myLocationButton;

    private FloatingActionButton navigationExitButton;

    private List<Marker> onMap_markerList;

    private AppCompatActivity mainActivity;

    private CameraPosition navigationCamera;

    private CameraPosition normalCamera;

    private MapNavigator navigator;

    private boolean isCameraFollowing;

    private List<PoI> cache_PoIList;

    public MapController(AppCompatActivity mainActivity,GoogleMap map, boolean permission,FloatingActionButton myLocButton,FloatingActionButton exitButton)
    {

        this.map = map;
        isCameraFollowing = false;
        this.mainActivity = mainActivity;
        myLocationButton = myLocButton;
        navigationExitButton = exitButton;
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
        enterNormalMode();
    }

    /**
     * This method is used to setup the listener on my location button
     */
    private void setupMyLocationButton()
    {
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (curLocation != null) {
                    if (mode == MODE.NORMAL)
                       moveCamera(normalCamera);
                    else {
                        if(navigationCamera!=null)
                        moveCamera(navigationCamera);
                    }
                }
                isCameraFollowing = true;
            }
        });
    }

    /**
     * This method is used to move the camera to a certain position
     * @param cameraPosition
     */
    private void moveCamera(CameraPosition cameraPosition)
    {
        GoogleMap.CancelableCallback cameraCallbackControl = new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                isCameraFollowing = true;
            }

            @Override
            public void onCancel() {
                isCameraFollowing = false;
            }
        };
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), cameraCallbackControl);
    }

    /**
     * This method is used to set the location permission, if permitted, my location button will be set up
     * and display; otherwise, it will not show on the screen
     * @param permission
     */
    public void setPermission(boolean permission)
    {
        locationPermission=permission;

        try{
            if(locationPermission)
            {
                map.setMyLocationEnabled(true);
                myLocationButton.setVisibility(View.VISIBLE);
                setupMyLocationButton();
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


    /**
     * Calling this method, the map will enter to the normal mode
     */
    private void enterNormalMode()
    {
        navigationExitButton.setVisibility(View.GONE);
        mode = MODE.NORMAL;

        //if there is no current position available, point to the center of cal poly pomona
        if(curLocation==null)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.0565284,-117.8215295),18));

    }

    /**
     * Calling this method the map will enter to navigation mode
     * @param destination
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void enterNavigationMode(LatLng destination) throws ExecutionException, InterruptedException {
        removeMarkersFromMap();
        mode = MODE.NAVIGATION;
        navigator = new MapNavigator(map,getCurLocation(),destination);
        navigationExitButton.setVisibility(View.VISIBLE);
        navigationExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitNavigationMode();
            }
        });
    }

    /**
     * Calling the method the map will exit the navigation and enter normal mode
     */
    private void exitNavigationMode()
    {
        navigator.exit();
        navigator=null;
        navigationCamera=null;
        changeMarkersOnMap(cache_PoIList);
        enterNormalMode();
    }





    public void changeMarkersOnMap(List<PoI> filtered_list)
    {
        removeMarkersFromMap();

        if(filtered_list!=null) {
            cache_PoIList = filtered_list;
            onMap_markerList = new ArrayList();
            for (int i = 0; i < filtered_list.size(); i++) {
                PoI thisPoI = filtered_list.get(i);
                Marker newMarker = map.addMarker(new MarkerOptions().position(thisPoI.getLocation()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                newMarker.setTag(thisPoI);
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




    public void curLocationUpdate(Location newLocation)
    {
        //update the current location
        curLocation = newLocation;

        //update the normal camera
        if(mode ==MODE.NORMAL)
        {
            normalCamera = new CameraPosition.Builder().target(getCurLocation()).zoom(18).build();
            if(isCameraFollowing)
                moveCamera(normalCamera);
        }

        //update the navigation camera and update the navigator
        else
        {
            if(navigator!=null)
            {
                //update the navigator with new location and check whether the destination arrived
                boolean arrive_dest = navigator.updateCurLocation(getCurLocation());
                if(arrive_dest) {
                    exitNavigationMode();
                    return;
                }
                boolean justEnterNavigation = (navigationCamera == null);
                navigationCamera = navigator.getNavigationCamera(getCurLocation());
                if(justEnterNavigation || isCameraFollowing)
                {
                    moveCamera(navigationCamera);
                }
            }
        }
    }

    /**
     * Thi method will be called when a marker is clicked by user
     * @param marker
     */
    private void showInfoDialog(Marker marker) {
        final PoI thisPoI = (PoI) marker.getTag();
        String title=null;
        String description=null;
        boolean hasRestroom = false;
        boolean hasFood = false;
        String[] sub_division = null;
        String optional_name = null;

        if(thisPoI instanceof Building)
        {
            title = "Building "+((Building) thisPoI).getBuildingNum();
            description = thisPoI.getDescription();
            hasRestroom=((Building) thisPoI).hasRestroom();
            hasFood=((Building) thisPoI).hasFood();
            optional_name=((Building) thisPoI).getOptionalName();
            sub_division=((Building) thisPoI).getSubdivision();
        }

        final MaterialDialog infoDialog = new MaterialDialog.Builder(mainActivity).customView(R.layout.info_layout, true)
                .title(title).show();
        View infoView = infoDialog.getCustomView();

        //load description
        TextView tv_description = (TextView) infoView.findViewById(R.id.tv_entry_description);
        tv_description.setText(description);
        //load image
        ImageView iv_image = (ImageView) infoView.findViewById(R.id.iv_poi_image);
        iv_image.setImageResource(mainActivity.getResources().getIdentifier(thisPoI.getImageName(), "mipmap",mainActivity.getPackageName()));
        //load optional name
        TextView tv_optional_name  = (TextView)infoView.findViewById(R.id.tv_optional_name);
        if(optional_name!=null)
            tv_optional_name.setText(optional_name);
        else
            tv_optional_name.setVisibility(View.GONE);
        //setup restroom icon
        ImageView iv_restroom = (ImageView) infoView.findViewById(R.id.iv_restroom);
        if(!hasRestroom)
            iv_restroom.setVisibility(View.GONE);
        //setup food icon
        ImageView iv_food = (ImageView) infoView.findViewById(R.id.iv_food);
        if(!hasFood)
            iv_food.setVisibility(View.GONE);
        //setup subdivision
        ListView lv_subdivision = (ListView) infoView.findViewById(R.id.lv_sub_division_list);
        if(sub_division!=null)
        {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity,android.R.layout.simple_list_item_1,sub_division);
            lv_subdivision.setAdapter(adapter);
        }
        else
            lv_subdivision.setVisibility(View.GONE);

        //setup route button
        Button routeButton = (Button) infoView.findViewById(R.id.b_route_button);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationPermission && curLocation!=null) {
                    try {
                        enterNavigationMode(thisPoI.getLocation());
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
