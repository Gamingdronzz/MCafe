package com.mcafeweb;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;

import android.widget.Toast;

/**
 * Created by Balpreet on 19-May-17.
 */

public class PopUpMenuEventHandle implements PopupMenu.OnMenuItemClickListener {
    Context context;
    public PopUpMenuEventHandle(Context context){
        this.context =context;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        /*if (item.getItemId()==R.id.) {
            Toast.makeText(context,"Login is admin",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId()==R.id.){

            Toast.makeText(context,"login is user",Toast.LENGTH_SHORT).show();
        }
        */
        return false;

    }
}
