package com.mcafeweb.Models;

import android.graphics.Bitmap;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class BlogModel {

    private String title = null;
    private String blogBrief = null;
    private String blogUrl = null;
    private String blogCanonicalLink = null;


    private Bitmap blogImage = null;
    private int blogID;
    private boolean isImageSet = false;
    int blogCategory;
    private String blogSharedDate;
    private int blogSharedByUser;

    private int blogLikes;
    private int blogShares;
    private int blogViews;

    private boolean isTitleSet = false;
    String sharerName;

    public String getSharerName() {
        return sharerName;
    }

    public void setSharerName(String sharerName) {
        this.sharerName = sharerName;
    }

    public String getBlogCanonicalLink() {
        return blogCanonicalLink;
    }

    public void setBlogCanonicalLink(String blogCanonicalLink) {
        this.blogCanonicalLink = blogCanonicalLink;
    }


    public boolean isImageSet() {
        return isImageSet;
    }

    public void setImageSet(boolean imageSet) {
        isImageSet = imageSet;
    }


    private String canonicalURL = "";

    public String getCanonicalURL() {
        if(canonicalURL.length() > 35)
            return canonicalURL.substring(0,34) + "...";
        return canonicalURL;
    }

    public void setCanonicalURL(String canonicalURL) {
        this.canonicalURL = canonicalURL;
    }

    public int getBlogLikes() {
        return blogLikes;
    }

    public void setBlogLikes(int blogLikes) {
        this.blogLikes = blogLikes;
    }

    public int getBlogShares() {
        return blogShares;
    }

    public void setBlogShares(int blogShares) {
        this.blogShares = blogShares;
    }

    public int getBlogViews() {
        return blogViews;
    }

    public void setBlogViews(int blogViews) {
        this.blogViews = blogViews;
    }

    public String getBlogSharedDate() {
        return blogSharedDate;
    }

    public void setBlogSharedDate(String blogSharedDate) {
        this.blogSharedDate = blogSharedDate;
    }

    public int getBlogSharedByUser() {
        return blogSharedByUser;
    }

    public void setBlogSharedByUser(int blogSharedByUser) {
        this.blogSharedByUser = blogSharedByUser;
    }

    public int getBlogID() {
        return blogID;
    }

    public void setBlogID(int blogID) {
        this.blogID = blogID;
    }

    public BlogModel() {

    }

    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBlogBrief() {
        if (blogBrief == null) {
            return "";
        }
        if (blogBrief.length() > 125) {
            blogBrief = blogBrief.substring(0, 124) + "...";
        }
        return blogBrief;
    }

    public void setBlogBrief(String blogBrief) {
        this.blogBrief = blogBrief;
    }

    public Bitmap getBlogImage() {
        return blogImage;
    }

    public void setBlogImage(Bitmap blogImage) {
        if (blogImage != null)
            this.blogImage = blogImage;
    }


    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public boolean isTitleSet() {
        return isTitleSet;
    }

    public void setTitleSet(boolean titleSet) {
        isTitleSet = titleSet;
    }

    public int getBlogCategory() {
        return blogCategory;
    }

    public void setBlogCategory(int blogCategory) {
        this.blogCategory = blogCategory;
    }
}
