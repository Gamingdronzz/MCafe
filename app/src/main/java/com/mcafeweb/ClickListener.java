package com.mcafeweb;

import android.view.View;

/**
 * Created by Balpreet on 03-Apr-17.
 */

public interface ClickListener {
    public void onClick(View view,int position);
    public void onLongClick(View view, int position);
}
