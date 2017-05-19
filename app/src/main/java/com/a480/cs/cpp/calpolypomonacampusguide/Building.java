package com.a480.cs.cpp.calpolypomonacampusguide;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wxy03 on 5/11/2017.
 */

public class Building extends PoI {

    private String buildingName;

    private boolean hasRestroom;

    private boolean hasFood;

    private String altName;

    private String[] subdivision;

    public Building(String buildingNum,String imageName,LatLng location,String description,boolean hasRestroom,boolean hasFood)
    {
        super(imageName,location,description);
        this.hasRestroom = hasRestroom;
        this.buildingName = "Building "+buildingNum;
        this.hasFood = hasFood;

    }

    public String getBuildingName() {
        return buildingName;
    }

    public boolean hasRestroom() {
        return hasRestroom;
    }

    public boolean hasFood()
    {
        return hasFood;
    }


    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public String[] getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivisionList) {

        this.subdivision = subdivisionList.split(",");;
    }

    public String getSubInString()
    {
        if(subdivision==null)
            return null;
        else
        {
            String newString = "";
            for(String nextString:subdivision)
            {
                newString+=nextString;
                newString+=", ";
            }
            return newString.substring(0,newString.length()-2);
        }

    }
}
