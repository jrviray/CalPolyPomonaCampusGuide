package com.a480.cs.cpp.calpolypomonacampusguide;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wxy03 on 5/11/2017.
 */

public abstract class PoI {

    private String imageName;

    private LatLng location;

    private String description;

    public PoI(String imageName,LatLng location,String description)
    {
        this.imageName = imageName;
        this.location = location;
        this.description = description;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getDescription()
    {
        return description;
    }


    public String getImageName() {
        return imageName;
    }
}
