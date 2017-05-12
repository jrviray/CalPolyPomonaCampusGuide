package com.a480.cs.cpp.calpolypomonacampusguide;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wxy03 on 5/11/2017.
 */

public class Building extends PoI {

    private String buildingNum;

    private boolean hasRestroom;

    private boolean hasFood;

    private String optionalName;

    private String[] subdivision;

    public Building(String buildingNum,String imageName,LatLng location,String description,boolean hasRestroom,boolean hasFood)
    {
        super(imageName,location,description);
        this.hasRestroom = hasRestroom;
        this.buildingNum = buildingNum;
        this.hasFood = hasFood;

    }

    public String getBuildingNum() {
        return buildingNum;
    }

    public boolean hasRestroom() {
        return hasRestroom;
    }

    public boolean hasFood()
    {
        return hasFood;
    }


    public String getOptionalName() {
        return optionalName;
    }

    public void setOptionalName(String optionalName) {
        this.optionalName = optionalName;
    }

    public String[] getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivisionList) {

        this.subdivision = subdivisionList.split(",");;
    }
}
