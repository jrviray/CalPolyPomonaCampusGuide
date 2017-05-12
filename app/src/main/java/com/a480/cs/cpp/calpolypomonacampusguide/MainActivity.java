package com.a480.cs.cpp.calpolypomonacampusguide;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener
{

    public static String API_KEY = "AIzaSyCz-BTwm8HrINpXaRfgOOvvzJuKnxswdaM";

    private final int LOCATION_TIME_INTERVAL = 1000;

    private final int LOCATION_PERMISSION_CODE = 1;

    private boolean locationPermission;

    private MapController mapController;

    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;

    private DBController databaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check google play service
        final int googlePlayServiceCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        //google play service is not available
        if(googlePlayServiceCode!=ConnectionResult.SUCCESS)
        {
            new AlertDialog.Builder(this).setMessage("Google Play Service Error: "+
                    GoogleApiAvailability.getInstance().getErrorString(googlePlayServiceCode)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(googlePlayServiceCode);
                }
            }).setCancelable(false).show();
        }
        //google play service is available, app starts
        else {
            setContentView(R.layout.activity_main);

            DatabaseHelper database = new DatabaseHelper(this);
            databaseController = new DBController(database.getReadableDatabase(),database.getWritableDatabase());
            //get a reference of map
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);
            //get permission
            getPermission();
            connectApiClient();
        }

    }

    /**
     * This method is used to check and grant location permission from the user
     */
    protected void getPermission()
    {
        locationPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        if(!locationPermission)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        }
    }

    /**
     * This method is used to connect google api client in order to use gps location
     */
    private void connectApiClient()
    {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }
        if(!googleApiClient.isConnected())
            googleApiClient.connect();

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mapController = new MapController(this,googleMap,locationPermission,(
                FloatingActionButton)findViewById(R.id.b_my_location_button),(FloatingActionButton)findViewById(R.id.b_naviagtion_exit_button));
        mapController.changeMarkersOnMap(databaseController.getAll());
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //create a location request
        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_TIME_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_TIME_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getPermission();
            //check for permission
            if (locationPermission) {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mapController!=null)
            mapController.curLocationUpdate(location);  //update current location in the map

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                   locationPermission = true;
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    }
                    catch (SecurityException e)
                    {
                        e.printStackTrace();
                    }

                }
                else {
                    locationPermission = false;
                }
                if(mapController!=null)
                    mapController.setPermission(locationPermission);
            }
        }
    }


    @Override
    public void onRestart()
    {
        super.onRestart();
        getPermission();
    }

}
