package com.mcafeweb.Models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by Balpreet on 29-Mar-17.
 */

public class ProfileItemModel {
    String ProfileItemName;
    String ProfileItemValue;

    public ProfileItemModel() {

    }

    public ProfileItemModel(String profileItemName, String profileItemValue) {
        ProfileItemName = profileItemName;
        ProfileItemValue = profileItemValue;

    }

    public String getProfileItemValue() {

        return ProfileItemValue;
    }

    public void setProfileItemValue(String profileItemValue) {
        ProfileItemValue = profileItemValue;
    }

    public String getProfileItemName() {
        return ProfileItemName;
    }

    public void setProfileItemName(String profileItemName) {
        ProfileItemName = profileItemName;
    }
}
