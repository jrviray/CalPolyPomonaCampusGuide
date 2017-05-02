package com.a480.cs.cpp.calpolypomonacampusguide;

import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
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

    private List dataEntryList;

    private AppCompatActivity mainActivity;

    private List route;

    private CameraPosition navigationCamera;


    public MapController(GoogleMap map, boolean permission,AppCompatActivity mainActivity)
    {

        this.map = map;
        setPermission(permission);
        this.mainActivity = mainActivity;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(false);
        getDataReady();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showInfoDialog(marker);
                return false;
            }
        });
        normalMode();
    }

    public void setPermission(boolean permission)
    {
        locationPermission=permission;
        try{
            if(locationPermission)
            {
                    map.setMyLocationEnabled(true);
                    map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            if (curLocation != null)
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 18));
                            if (!isNormalMode) {
                                map.animateCamera(CameraUpdateFactory.newCameraPosition(navigationCamera));
                            }
                            return true;
                        }
                    });
                }
            else
                map.setMyLocationEnabled(false);
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }


    }

    public void normalMode()
    {
        isNormalMode = true;
        //when enter the normal view, always centers Cap Poly Pomona
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.056514, -117.821452),15));
        filter_all();
    }

    public void navigationMode(LatLng destination) throws ExecutionException, InterruptedException {
        isNormalMode = false;
        LatLng origin = new LatLng(curLocation.getLatitude(),curLocation.getLongitude());
        getRoute(origin,destination);
       navigationCamera = new CameraPosition.Builder().tilt(75).target(origin).zoom(20).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(navigationCamera));
    }

    private void getDataReady()
    {
        //process data from entry_data file to a list of entry
        DataProcessor dataProcessor = new DataProcessor();
        try {
            dataEntryList = dataProcessor.parse(mainActivity.getResources().openRawResource(R.raw.entry_data));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getRoute(LatLng start,LatLng destination) throws ExecutionException, InterruptedException {
        route =  new StartAsyncTask(start,destination).execute().get();
    }

    private void filter_all()
    {
        addMarkersToMap(dataEntryList);
    }

    private void addMarkersToMap(List after_filter_list)
    {
        for(int i=0;i<after_filter_list.size();i++)
        {
            DataEntry thisEntry = (DataEntry) after_filter_list.get(i);
            Marker newMarker = map.addMarker(new MarkerOptions().position(thisEntry.getLocation()));
            newMarker.setTag(thisEntry);
            newMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        }
    }



    private Polyline routeLine;
    private Circle userLocationCircle;

    public void curLocationUpdate(Location newLocation)
    {


        if(isNormalMode)
        curLocation = newLocation;

        //navigation animation
        else
        {
            if(route!=null)
            {
                if(routeLine!=null)
                {
                    routeLine.remove();
                }
                routeLine = map.addPolyline(new PolylineOptions().addAll(route));
            }
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
                if(locationPermission) {
                    try {
                        navigationMode(entry.getLocation());
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

}
