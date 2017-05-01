package com.a480.cs.cpp.calpolypomonacampusguide;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wearable.DataEvent;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private final int LOCATION_PERMISSION_CODE = 1;

    private MapFragment mapFragment;

    private List entryList;

    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;

    private Location curLocation;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get a reference of map
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        //create a
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }
        googleApiClient.connect();

        //process data from entry_data file to a list of entry
        DataProcessor dataProcessor = new DataProcessor();
        try {
            entryList = dataProcessor.parse(getResources().openRawResource(R.raw.entry_data));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        //when a map is ready, always centers Cap Poly Pomona
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(34.056514, -117.821452)));
        //check for permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        }
        else
            map.setMyLocationEnabled(true);

        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);


        //add markers on the map according to data in entryList
        List newList = new ArrayList();
        for (int i = 0; i < entryList.size(); i++) {
            DataEntry thisEntry = (DataEntry) entryList.get(i);
            Marker newMarker = googleMap.addMarker(new MarkerOptions().position(thisEntry.getLocation()));
            newMarker.setTag(thisEntry);
            newMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            newList.add(newMarker);
        }
        entryList = newList;

        map.moveCamera(CameraUpdateFactory.zoomTo(15));


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showInfoDialog(marker);
                return false;
            }
        });

    }

    /**
     * Thi method will be called when a marker is clicked by user
     * @param marker
     */
    private void showInfoDialog(Marker marker) {
        DataEntry entry = (DataEntry) marker.getTag();
        MaterialDialog infoDialog = new MaterialDialog.Builder(this).customView(R.layout.info_layout, true)
                .title(entry.getTitle()).show();
        View infoView = infoDialog.getCustomView();
        TextView description = (TextView) infoView.findViewById(R.id.tv_entry_description);
        description.setText(entry.getDescription());
        ImageView image = (ImageView) infoView.findViewById(R.id.iv_entry_image);
        image.setImageResource(getResources().getIdentifier(entry.getImageName(), "mipmap", getPackageName()));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //create a location request
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            //check for permission
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_CODE);
            }
            else {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
            curLocation =location;  //update current location
    }

    /**
     * This method is used to update the camera to center to current location
     */
    private void updateLocationOnMap()
    {
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(curLocation.getLatitude(),curLocation.getLongitude())));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //if the location is permitted
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                        map.setMyLocationEnabled(true);
                    }
                    catch (SecurityException e)
                    {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }
    }
}
