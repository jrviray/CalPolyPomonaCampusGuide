package com.a480.cs.cpp.calpolypomonacampusguide;

import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxy03 on 5/14/2017.
 */

public class Filter {

    private List all_PoI_list;

    private List building_list;

    private List parking_list;

    private List open_space_list;

    private List food_list;

    public Filter(List all_PoI_list)
    {
        updateFilterData(all_PoI_list);
    }

    /**
     * This method is usde to update the data for filter if there is some update in database
     * @param new_PoI_list
     */
    public void updateFilterData(List new_PoI_list)
    {
        all_PoI_list = new ArrayList();
        building_list = new ArrayList();
        parking_list = new ArrayList();
        open_space_list = new ArrayList();
        food_list = new ArrayList();

        for(int i=0;i<new_PoI_list.size();i++)
        {
            PoI thisPoI = (PoI) new_PoI_list.get(i);
            if(thisPoI!=null)
            {
                all_PoI_list.add(thisPoI);
                if(thisPoI instanceof Building)
                {
                    building_list.add(thisPoI);
                    if(((Building) thisPoI).hasFood())
                        food_list.add(thisPoI);
                }
                else if(thisPoI instanceof ParkingLot)
                    parking_list.add(thisPoI);
                else if(thisPoI instanceof OpenSpace)
                    open_space_list.add(thisPoI);
            }
        }
    }

    public List getAll_PoI_list() {
        return all_PoI_list;
    }

    public List getBuilding_list() {
        return building_list;
    }

    public List getParking_list() {
        return parking_list;
    }

    public List getOpen_space_list() {
        return open_space_list;
    }

    public List getFood_list() {
        return food_list;
    }
}
