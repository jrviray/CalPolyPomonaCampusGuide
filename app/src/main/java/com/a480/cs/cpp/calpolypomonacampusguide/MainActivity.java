package com.a480.cs.cpp.calpolypomonacampusguide;


import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import layout.MapNodeContentFragment;

public class MainActivity extends AppCompatActivity implements MapNodeContentFragment.OnFragmentInteractionListener,OnMapReadyCallback{

    private MapNodeContentFragment mapNodeContentFragment;

    private MapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void onClickMarker(Marker node)
    {
        mapNodeContentFragment = MapNodeContentFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.node_content_frame, mapNodeContentFragment).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        LatLng building_eight = new LatLng(34.058378, -117.825395);
        googleMap.addMarker(new MarkerOptions().position(building_eight));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(building_eight));
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                onClickMarker(marker);
                return false;
            }
        });

    }
}
