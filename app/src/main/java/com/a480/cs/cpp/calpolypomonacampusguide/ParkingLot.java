package com.a480.cs.cpp.calpolypomonacampusguide;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wxy03 on 5/14/2017.
 */

public class ParkingLot extends PoI {

    private String parkingLotName;

    private String availability;


    public ParkingLot(String parkingLotNum,String imageName, LatLng location, String description,String availability) {
        super(imageName, location, description);
        this.parkingLotName="Parking Lot "+parkingLotNum;
        this.availability = availability;
    }

    public String getAvailability() {
        return availability;
    }

    public String getParkingLotName() {
        return parkingLotName;
    }


}
