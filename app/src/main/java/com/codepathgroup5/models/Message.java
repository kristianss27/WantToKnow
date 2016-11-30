package com.codepathgroup5.models;


import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class Message {
    String description;
    String contactEmail;
    String contactPhone;
    boolean permitCalls;
    Date whenDate;

    public Message() {
    }

    @Override
    public String toString() {
        return "Message{" +
                "description='" + description + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", permitCalls=" + permitCalls +
                ", whenDate=" + whenDate +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public boolean isPermitCalls() {
        return permitCalls;
    }

    public void setPermitCalls(boolean permitCalls) {
        this.permitCalls = permitCalls;
    }

    public Date getWhenDate() {
        return whenDate;
    }

    public void setWhenDate(Date whenDate) {
        this.whenDate = whenDate;
    }
}
