package com.mcafeweb.Models;

import java.util.Date;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class EventModel {

    private String title;
    private int Event_Image;
    private String Event_Description;
    private PrivacyType Event_Privacy;
    private Date eventDate;

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }


    static enum PrivacyType {PRIVATE, PUBLIC;}

    ;

    public PrivacyType getEvent_Privacy() {
        return Event_Privacy;
    }

    public void setEvent_Privacy(PrivacyType event_Privacy) {
        Event_Privacy = event_Privacy;
    }


    public String getEvent_Description() {
        return Event_Description;
    }

    public void setEvent_Description(String event_Description) {
        Event_Description = event_Description;
    }


    public EventModel() {

    }

    public EventModel(String title, int Image, int event_Members, String description) {
        setTitle(title);
        setEvent_Image(Image);
        setEvent_Description(description);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEvent_Image() {
        return Event_Image;
    }

    public void setEvent_Image(int Event_Image) {
        this.Event_Image = Event_Image;
    }


}
