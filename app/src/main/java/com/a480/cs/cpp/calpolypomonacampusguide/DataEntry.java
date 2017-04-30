package com.a480.cs.cpp.calpolypomonacampusguide;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wxy03 on 4/29/2017.
 */

public class DataEntry {

    private String title;

    private String description;

    private String imageName;

    private LatLng location;

    public DataEntry(String title,String description,String imageName,double latitude,double longitude)
    {
        this.title=title;
        this.description=description;
        this.imageName=imageName;
        this.location = new LatLng(latitude,longitude);
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getImageName()
    {
        return imageName;
    }

    public LatLng getLocation()
    {
        return location;
    }
}
