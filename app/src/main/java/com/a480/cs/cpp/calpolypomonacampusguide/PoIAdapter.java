package com.a480.cs.cpp.calpolypomonacampusguide;

import android.content.Context;
import android.graphics.ColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wxy03 on 5/18/2017.
 */

public class PoIAdapter extends RecyclerView.Adapter<PoIAdapter.ViewHolder> {

    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(PoI clickedPoI);
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_title;
        public TextView tv_component;
        public ImageView iv_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_item_title);
            tv_component = (TextView) itemView.findViewById(R.id.tv_item_component);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_poi_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        listener.onItemClick(mPoIs.get(getAdapterPosition()));
                }
            });
        }
    }


    private List<PoI> mPoIs;

    private Context mContext;

    public PoIAdapter(List<PoI> PoIList, Context context,OnItemClickListener listener)
    {
        mPoIs=PoIList;
        mContext = context;
        this.listener = listener;
    }

    @Override
    public PoIAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View poiListView = inflater.inflate(R.layout.item_poi_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(poiListView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PoIAdapter.ViewHolder holder, int position) {
        PoI poi = mPoIs.get(position);
        setData(holder.tv_title,holder.tv_component,holder.iv_icon,poi);
    }

    @Override
    public int getItemCount() {
        return mPoIs.size();
    }

    private void setData(TextView title,TextView component,ImageView icon, PoI poi)
    {

        if(poi instanceof Building)
        {
            icon.setImageResource(R.drawable.ic_filter_building);
            String building_title = ((Building) poi).getBuildingName();
            if(((Building) poi).getAltName()!=null)
                building_title+=" - "+((Building) poi).getAltName();
            title.setText(building_title);
            if(((Building) poi).getSubInString()!=null)
            {
                component.setText(((Building) poi).getSubInString());
            }
            else
                component.setVisibility(View.GONE);
        }
        else if(poi instanceof ParkingLot)
        {
            icon.setImageResource(R.drawable.ic_filter_parking);
            title.setText(((ParkingLot) poi).getParkingLotName());
            component.setText(((ParkingLot) poi).getAvailability());
        }
        else if(poi instanceof OpenSpace)
        {
            icon.setImageResource(R.drawable.ic_filter_open_space);
            title.setText(((OpenSpace) poi).getName());
            component.setVisibility(View.GONE);
        }
        icon.setColorFilter(ContextCompat.getColor(mContext,R.color.search_dialog_icon));
    }
}
