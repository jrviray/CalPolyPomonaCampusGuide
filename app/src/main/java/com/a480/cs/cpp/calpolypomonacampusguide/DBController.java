package com.a480.cs.cpp.calpolypomonacampusguide;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by wxy03 on 5/11/2017.
 */

public class DBController {

    private final String POI_TABLE_NAME = "poi";

    private final String SQL_SEQUENCE_TABLE = "sqlite_sequence";

    private final String SEQ_COL = "seq";

    private final String ID_COL = "_id";

    private final String TYPE_COL = "type";

    private final String POSTFIX_COL = "postfix";

    private final String DESCRIPTION_COL = "description";

    private final String LATITUDE_COL = "latitude";

    private final String LONGITUDE_COL = "longitude";

    private final String ALT_NAME_COL = "alternative_name";

    private final String IMAGE_NAME_COL = "image_name";

    private final String RESTROOM_COL = "has_restroom";

    private final String FOOD_COL = "has_food";

    private final String SUB_DIV_COL = "sub_division";

    private SQLiteDatabase readableDB;

    private SQLiteDatabase writableDB;

    private int numOfPoi;

    public DBController(SQLiteDatabase readable, SQLiteDatabase writable)
    {
        this.readableDB = readable;
        this.writableDB = writable;

        Cursor cursor = readableDB.query(SQL_SEQUENCE_TABLE,null,null,null,null,null,null);
        cursor.moveToFirst();
        String seqNum = cursor.getString(cursor.getColumnIndexOrThrow(SEQ_COL));
        numOfPoi=Integer.parseInt(seqNum)+1;
    }

    /**
     * this method is used to get all the data inside the database
     * @return
     * A map of PoI which contains all the point of interest in the database
     * the key is the unique ID of poi
     */
    public List getAll()
    {
        Cursor cursor = readableDB.query(POI_TABLE_NAME,null,null,null,null,null,null);
        List cache_data = new ArrayList();
        cursor.moveToFirst();
        PoI[] tempArray = new PoI[numOfPoi];
        while(!cursor.isAfterLast())
        {
            tempArray[cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL))]=makePoI(cursor);
            cursor.moveToNext();
        }
        return Arrays.asList(tempArray);
    }

    /**
     * This method is used to make an object of {@link PoI} from one entry in the database
     * @param cursor
     * @return
     */
    private PoI makePoI(Cursor cursor)
    {
        //get the name of the image
        String image_name = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_NAME_COL));
        //get description
        String description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_COL));
        //get the location of building
        double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUDE_COL));
        double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUDE_COL));
        LatLng position = new LatLng(lat,lng);

        //determine type
        String thisType = cursor.getString(cursor.getColumnIndexOrThrow(TYPE_COL));
        if(thisType.equals("BUILDING"))
        {
            //get building number
            String postfix = cursor.getString(cursor.getColumnIndexOrThrow(POSTFIX_COL));
            //check whether the building has restroom
            boolean has_restroom = cursor.getString(cursor.getColumnIndexOrThrow(RESTROOM_COL)).equals("T")? true:false;
            //check whether the building has food
            boolean has_food = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COL)).equals("T")? true:false;
            //get the optional name
            String optional_name = cursor.getString(cursor.getColumnIndexOrThrow(ALT_NAME_COL));
            //get subdivision
            String subdivision = cursor.getString(cursor.getColumnIndexOrThrow(SUB_DIV_COL));
            Building thisBuilding = new Building(postfix,image_name,position,description,has_restroom,has_food);
            if(optional_name!=null)
                thisBuilding.setOptionalName(optional_name);
            if(subdivision!=null)
                thisBuilding.setSubdivision(subdivision);
            return thisBuilding;
        }

        else
            return null;








    }
}
