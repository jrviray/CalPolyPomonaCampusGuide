package com.a480.cs.cpp.calpolypomonacampusguide;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.List;

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

    public MapController(GoogleMap map, boolean permission,AppCompatActivity mainActivity)
    {
        this.map = map;
        locationPermission=permission;
        this.mainActivity = mainActivity;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
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

    public void normalMode()
    {
        isNormalMode = true;
        //when enter the normal view, always centers Cap Poly Pomona
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.056514, -117.821452),15));
        filter_all();
        if(locationPermission) {
            try {
                map.setMyLocationEnabled(true);
                map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        if(curLocation!=null)
                            goToCurrentLocation();
                        return false;
                    }
                });
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }
        }

        LatLng startLatLng = new LatLng(34.058233,-117.825143);
        LatLng endLatLng = new LatLng(34.058667, -117.825248);
        new StartAsyncTask(startLatLng,endLatLng).execute();
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




    public void curLocationUpdate(Location newLocation)
    {
        if(isNormalMode)
        curLocation = newLocation;
    }

    /**
     * Thi method will be called when a marker is clicked by user
     * @param marker
     */
    private void showInfoDialog(Marker marker) {
        DataEntry entry = (DataEntry) marker.getTag();
        MaterialDialog infoDialog = new MaterialDialog.Builder(mainActivity).customView(R.layout.info_layout, true)
                .title(entry.getTitle()).show();
        View infoView = infoDialog.getCustomView();
        TextView description = (TextView) infoView.findViewById(R.id.tv_entry_description);
        description.setText(entry.getDescription());
        ImageView image = (ImageView) infoView.findViewById(R.id.iv_entry_image);
        image.setImageResource(mainActivity.getResources().getIdentifier(entry.getImageName(), "mipmap",mainActivity.getPackageName()));
    }


    /**
     * This method is used to update the camera to center to current location
     */
    private void goToCurrentLocation()
    {
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(curLocation.getLatitude(),curLocation.getLongitude())));
    }
}
