package com.a480.cs.cpp.calpolypomonacampusguide;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.List;

/**
 * Created by wxy03 on 5/11/2017.
 */

public class DatabaseHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "cpp_guide_db.db";
    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //for debug usage
        setForcedUpgrade();
    }

}
