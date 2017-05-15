package com.a480.cs.cpp.calpolypomonacampusguide;


import android.content.Context;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by wxy03 on 5/1/2017.
 */

public class MapController implements MapRouteListener {



    protected enum MODE {NORMAL,NAVIGATION};

     private GoogleMap map;

    private boolean locationPermission;

    private Location curLocation;

    private MODE mode;

    private FloatingActionButton myLocationButton;

    private FloatingActionButton navigationExitButton;

    private List<Marker> onMap_markerList;

    private Context mainCntext;

    private CameraPosition navigationCamera;

    private CameraPosition normalCamera;

    private MapNavigator navigator;

    private boolean isCameraFollowing;

    private List<PoI> cache_PoIList;

    private MapModeListener modeListener;

    private MaterialDialog cache_dialog;

    /**
     * This is the constructor of {@link MapController}
     * @param mainCntext
     *          the activity where the map is located at and where the resource can be obtained
     * @param map
     *          the google map which is going to be controlled
     * @param permission
     *          indicates the permission of accessing user's location
     * @param listener
     *          a {@link MapModeListener} that is used to listen the the {@link #mode} change
     */
    public MapController(final AppCompatActivity mainCntext, GoogleMap map, boolean permission, MapModeListener listener)
    {

        this.map = map;
        isCameraFollowing = false;
        this.mainCntext = mainCntext;
        myLocationButton = (FloatingActionButton)mainCntext.findViewById(R.id.b_my_location_button);
        navigationExitButton =(FloatingActionButton)mainCntext.findViewById(R.id.b_naviagtion_exit_button);
        this.modeListener = listener;
        setPermission(permission);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                getInfoDialog((PoI) marker.getTag());
                cache_dialog.show();
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

    private void getInfoDialog(PoI thisPoI)
    {
        cache_dialog = InfoViewFactory.getInfoDialog(mainCntext,thisPoI,this);
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
     * This method should be called when the mode is change so that the
     * {@link #modeListener} could listen to the mode change
     * @param newMode
     */
    private void changeMode(MODE newMode)
    {
        mode = newMode;
        modeListener.onModeChange(newMode);
    }

    /**
     * Calling this method, the map will enter to the normal mode
     */
    private void enterNormalMode()
    {
        navigationExitButton.setVisibility(View.GONE);
        changeMode(MODE.NORMAL);
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

        try {
            navigator = new MapNavigator(map, getCurLocation(), destination);
        }
        catch (SocketTimeoutException e) {
            failConnection();
            return;
        }
            removeMarkersFromMap();
        onMap_markerList.add(map.addMarker(new MarkerOptions().
                        position(destination).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))));

            changeMode(MODE.NAVIGATION);
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
        if(navigator!=null)
            navigator.exit();

        navigator=null;
        navigationCamera=null;
        changeMarkersOnMap(cache_PoIList);
        enterNormalMode();
    }

    /**
     * This method should be called when internet connection fail to display
     * an error message and exit the navigation
     */
    private void failConnection()
    {
        Toast.makeText(mainCntext,"Connection failed!", Toast.LENGTH_LONG).show();
        exitNavigationMode();
    }


    /**
     * This method is used to change the markers on the map based on the list given
     * @param filtered_list
     * if {@code null}, all the marker will be removed
     */
    public void changeMarkersOnMap(List<PoI> filtered_list)
    {
        removeMarkersFromMap();

        if(filtered_list!=null) {
            cache_PoIList = filtered_list;
            onMap_markerList = new ArrayList();
            for (int i = 0; i < filtered_list.size(); i++) {
                PoI thisPoI = filtered_list.get(i);
                if(thisPoI!=null) {
                    Marker newMarker = map.addMarker(new MarkerOptions().
                            position(thisPoI.getLocation()).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    newMarker.setTag(thisPoI);
                    onMap_markerList.add(newMarker);
                }
            }
        }
    }

    /**
     * This is a helper method to remove all the markers from the map
     */
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

    /**
     * This is a core method for the {@link MapController} to control to {@link #map}.
     * Whenever the user's current location is updated, this method should be called to
     * determine the action of the map either in normal mode or navigation mode. If it's
     * in normal mode, the {@link #normalCamera}will be updated. If it's in navigation mode, the
     * {@link #navigationCamera} along with the update of{@link #navigator}.
     * Also, this method will control the arrival situation in navigation mode.
     * @param newLocation
     */
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
                boolean arrive_dest;
                try {
                    arrive_dest = navigator.updateCurLocation(getCurLocation());
                } catch (SocketTimeoutException e) {
                    //connection fail
                    failConnection();
                    return;
                }
                //the destination is arrived, need to exit the navigation mode
                if(arrive_dest) {
                    Toast.makeText(mainCntext,"You've arrived!", Toast.LENGTH_LONG).show();
                    exitNavigationMode();
                    return;
                }

                //check whether it's the first time called after enter navigation mode
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
     * This is a helper method to get the current location in {@link LatLng}
     * @return
     */
    public LatLng getCurLocation()
    {
        if(curLocation==null)
            return null;
        else
        return new LatLng(curLocation.getLatitude(),curLocation.getLongitude());
    }

    @Override
    public void startRoute(LatLng destination) {
        if(locationPermission && curLocation!=null) {
            try {
                enterNavigationMode(destination);
                cache_dialog.cancel();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
