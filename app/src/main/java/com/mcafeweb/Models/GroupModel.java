package com.mcafeweb.Models;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class GroupModel {

    private String groupTitle;


    private int groupID;
    private Bitmap Group_Image;
    private String GroupPrivacy;

    private int GroupShares;


    public Date getGroupCreatedDate() {
        return groupCreatedDate;
    }

    public void setGroupCreatedDate(Date groupCreatedDate) {
        this.groupCreatedDate = groupCreatedDate;
    }

    Date groupCreatedDate;

    public int getGroupShares() {
        return GroupShares;
    }

    public void setGroupShares(int groupShares) {
        GroupShares = groupShares;
    }

    public int getGroupCategory() {
        return GroupCategory;
    }

    public void setGroupCategory(int groupCategory) {
        GroupCategory = groupCategory;
    }

    private int GroupCategory;

    private String GroupDescription;
    private int GroupMembers;
    private int GroupCreatedByUserID;

    public String getGroupPrivacy() {
        return GroupPrivacy;
    }

    public void setGroupPrivacy(String groupPrivacy) {
        GroupPrivacy = groupPrivacy;
    }

    public int getGroupCreatedByUserID() {

        return GroupCreatedByUserID;
    }

    public void setGroupCreatedByUserID(int groupCreatedByUserID) {
        GroupCreatedByUserID = groupCreatedByUserID;
    }

    static final String PRIVACY_CLOSED = "closed";
    static final String PRIVACY_OPEN = "open";

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getGroupDescription() {
        return GroupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        GroupDescription = groupDescription;
    }

    public int getGroupMembers() {
        return GroupMembers;
    }

    public void setGroupMembers(int groupMembers) {
        this.GroupMembers = groupMembers;
    }


    public GroupModel() {

    }

    public GroupModel(String title, Bitmap Image, int group_Members, String description) {
        setGroupTitle(title);
        setGroup_Image(Image);
        setGroupMembers(group_Members);
        setGroupDescription(description);
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public Bitmap getGroup_Image() {
        return Group_Image;
    }

    public void setGroup_Image(Bitmap Group_Image) {
        this.Group_Image = Group_Image;
    }


}
