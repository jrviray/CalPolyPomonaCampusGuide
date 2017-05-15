package com.a480.cs.cpp.calpolypomonacampusguide;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wxy03 on 5/14/2017.
 */

public class OpenSpace extends PoI {

    private String name;

    public OpenSpace(String name,String imageName, LatLng location, String description) {
        super(imageName, location, description);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
