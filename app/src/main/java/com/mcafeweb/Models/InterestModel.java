package com.mcafeweb.Models;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class InterestModel {
    private String InterestName;
    private int InterestID;

    public int getInterestID() {
        return InterestID;
    }

    public void setInterestID(int interestID) {
        this.InterestID = interestID;
    }

    private boolean isSelected = false;

    public InterestModel()
    {

    }

    public InterestModel(String text) {
        this.InterestName = text;
    }

    public void setInterestName(String interestName) {
        this.InterestName = interestName;
    }

    public String getInterestName() {
        return InterestName;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;

    }


    public boolean isSelected() {
        return isSelected;
    }
}
