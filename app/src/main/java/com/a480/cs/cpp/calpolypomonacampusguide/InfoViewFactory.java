package com.a480.cs.cpp.calpolypomonacampusguide;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.concurrent.ExecutionException;

/**
 * Created by wxy03 on 5/14/2017.
 */

 public class InfoViewFactory {


    public static MaterialDialog getInfoDialog(Context mainContext,PoI thisPoI,MapRouteListener routeListener)
    {
        final MaterialDialog infoDialog = new MaterialDialog.Builder(mainContext).customView(R.layout.info_layout, true).build();
        View infoView = infoDialog.getCustomView();
        inflateInfoView(mainContext,infoView,thisPoI,routeListener);
        return infoDialog;
    }

    private static void inflateInfoView(Context mainContext, View infoView, final PoI thisPoI, final MapRouteListener routeListener)
    {
        String title;
        String description=thisPoI.getDescription();;
        boolean hasRestroom = false;
        boolean hasFood = false;
        String[] sub_division = null;
        String optional_name = null;
        String availability = null;

        if(thisPoI instanceof Building)
        {
            title = "Building "+((Building) thisPoI).getBuildingNum();
            hasRestroom=((Building) thisPoI).hasRestroom();
            hasFood=((Building) thisPoI).hasFood();
            optional_name=((Building) thisPoI).getAltName();
            sub_division=((Building) thisPoI).getSubdivision();
        }
        else if(thisPoI instanceof ParkingLot)
        {
            title = "Parking Lot "+((ParkingLot)thisPoI).getParkingLotNum();
            availability = ((ParkingLot)thisPoI).getAvailability();
        }
        else
            title = ((OpenSpace)thisPoI).getName();

        //load title
        TextView tv_title = (TextView) infoView.findViewById(R.id.tv_poi_title);
        tv_title.setText(title);
        //load description
        TextView tv_description = (TextView) infoView.findViewById(R.id.tv_entry_description);
        tv_description.setText(description);
        //load image
        ImageView iv_image = (ImageView) infoView.findViewById(R.id.iv_poi_image);
        iv_image.setImageResource(mainContext.getResources().getIdentifier(thisPoI.getImageName(), "drawable",mainContext.getPackageName()));
        //load optional name
        TextView tv_optional_name  = (TextView)infoView.findViewById(R.id.tv_optional_name);
        if(optional_name!=null)
            tv_optional_name.setText(optional_name);
        else
            tv_optional_name.setVisibility(View.GONE);
        //setup restroom icon
        ImageView iv_restroom = (ImageView) infoView.findViewById(R.id.iv_restroom);
        if(!hasRestroom)
            iv_restroom.setVisibility(View.GONE);
        //setup food icon
        ImageView iv_food = (ImageView) infoView.findViewById(R.id.iv_food);
        if(!hasFood)
            iv_food.setVisibility(View.GONE);
        if((!hasFood) && (!hasRestroom))
            infoView.findViewById(R.id.ll_icon_layout).setVisibility(View.GONE);
        //setup subdivision
        ExpandableHeightListView lv_subdivision = (ExpandableHeightListView) infoView.findViewById(R.id.lv_sub_division_list);
        if(sub_division!=null)
        {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mainContext,android.R.layout.simple_list_item_1,sub_division);
            lv_subdivision.setAdapter(adapter);
            lv_subdivision.setExpanded(true);
        }
        else
            lv_subdivision.setVisibility(View.GONE);
        //setup availability
        TextView avail_title = (TextView) infoView.findViewById(R.id.tv_availability_title);
        TextView avail_content = (TextView) infoView.findViewById(R.id.tv_availability_content);
        if(availability!=null)
            avail_content.setText(availability);
        else
        {
            avail_title.setVisibility(View.GONE);
            avail_content.setVisibility(View.GONE);
        }

        //setup route button
        Button routeButton = (Button) infoView.findViewById(R.id.b_route_button);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeListener.startRoute(thisPoI.getLocation());
            }
        });

    }
}
