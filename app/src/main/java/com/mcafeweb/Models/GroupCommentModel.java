package com.mcafeweb.Models;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class GroupCommentModel {



    private String groupContent;
    private int userID;
    private int groupContentID;
    private int groupID;
    private String userFirstName;
    private int groupContentLikes;
    private String GroupContentPostTime;

    public String getGroupContentPostTime() {
        return GroupContentPostTime;
    }

    public void setGroupContentPostTime(String groupContentPostTime) {
        GroupContentPostTime = groupContentPostTime;
    }

    public int getGroupContentLikes() {
        return groupContentLikes;
    }

    public void setGroupContentLikes(int groupContentLikes) {
        this.groupContentLikes = groupContentLikes;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getGroupContent() {
        return groupContent;
    }

    public void setGroupContent(String groupContent) {
        this.groupContent = groupContent;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getGroupContentID() {
        return groupContentID;
    }

    public void setGroupContentID(int groupContentID) {
        this.groupContentID = groupContentID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public GroupCommentModel()
    {

    }
}
