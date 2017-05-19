package com.a480.cs.cpp.calpolypomonacampusguide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import static com.a480.cs.cpp.calpolypomonacampusguide.R.id.search_button;
import static com.a480.cs.cpp.calpolypomonacampusguide.R.id.search_close_btn;
import static com.a480.cs.cpp.calpolypomonacampusguide.R.id.search_src_text;

/**
 * Created by wxy03 on 5/15/2017.
 */

public class ToolbarController {

    private Toolbar mainToolbar;
    private ActionBar actionBar;
    private Context mainContext;
    private SearchView sv_search;
    private TextView tv_direction;
    private ImageButton ib_close_navigation;


    public ToolbarController(Context mainContext,Toolbar mainToolbar,ActionBar actionBar)
    {
        this.mainContext=mainContext;
        this.mainToolbar = mainToolbar;
        this.actionBar = actionBar;
        sv_search = (SearchView) ((Activity)mainContext).findViewById(R.id.sv_search);
        tv_direction = (TextView) ((Activity)mainContext).findViewById(R.id.tv_direction);
        ib_close_navigation = (ImageButton) ((Activity)mainContext).findViewById(R.id.ib_close_naviagtion);
        actionBar.setDisplayShowTitleEnabled(false);

        SearchManager searchManager = (SearchManager) mainContext.getSystemService(Context.SEARCH_SERVICE);
        sv_search.setSearchableInfo(searchManager.getSearchableInfo(((Activity) mainContext).getComponentName()));
    }

    public void normalMode()
    {
        actionBar.setDisplayHomeAsUpEnabled(true);
        mainToolbar.setNavigationIcon(R.drawable.ic_menu);
        tv_direction.setVisibility(View.GONE);
        ib_close_navigation.setVisibility(View.GONE);
        sv_search.setVisibility(View.VISIBLE);
        final AutoCompleteTextView editText = (AutoCompleteTextView) sv_search.findViewById(search_src_text);
        editText.setTextColor(Color.WHITE);
        ImageView search_icon = (ImageView) sv_search.findViewById(search_button);
        search_icon.setColorFilter(Color.WHITE);
        ImageView search_close = (ImageView) sv_search.findViewById(search_close_btn);
        search_close.setColorFilter(Color.WHITE);


    }

    public void enterNavigationMode(View.OnClickListener exitListener)
    {
        actionBar.setDisplayHomeAsUpEnabled(false);
        tv_direction.setVisibility(View.VISIBLE);
        ib_close_navigation.setVisibility(View.VISIBLE);
        ib_close_navigation.setOnClickListener(exitListener);
        sv_search.setVisibility(View.GONE);
    }
}
