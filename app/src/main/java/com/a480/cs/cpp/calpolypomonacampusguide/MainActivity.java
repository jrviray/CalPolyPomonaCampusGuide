package com.a480.cs.cpp.calpolypomonacampusguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private MapFragment mapFragment;

    private List entryList;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get a reference of map
       mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

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

        //add markers on the map according to data in entryList
        for(int i=0;i<entryList.size();i++)
        {
            DataEntry thisEntry = (DataEntry) entryList.get(i);
            Marker newMarker = googleMap.addMarker(new MarkerOptions().position(thisEntry.getLocation()));
            newMarker.setTag(thisEntry);
            newMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        LatLng building_eight = new LatLng(34.058378, -117.825395);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(building_eight));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
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
    private void showInfoDialog(Marker marker)
    {
        DataEntry entry = (DataEntry) marker.getTag();
        MaterialDialog infoDialog = new MaterialDialog.Builder(this).customView(R.layout.info_layout,true)
                .title(entry.getTitle()).show();
        View infoView = infoDialog.getCustomView();
        TextView description = (TextView)infoView.findViewById(R.id.tv_entry_description);
        description.setText(entry.getDescription());
        ImageView image = (ImageView)infoView.findViewById(R.id.iv_entry_image);
        image.setImageResource(getResources().getIdentifier(entry.getImageName(),"mipmap",getPackageName()));
    }
}
