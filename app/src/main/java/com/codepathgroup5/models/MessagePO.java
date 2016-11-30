package com.codepathgroup5.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kristianss27
 */

@ParseClassName("MessagePO")
public class MessagePO extends ParseObject {

    public MessagePO() {
        super();
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description",description);
    }

    public String getContactEmail() {
        return getString("contact_email");
    }

    public void setContactEmail(String contactEmail) {
        put("contact_email",contactEmail);
    }

    public String getContactPhone() {
        return getString("contact_phone");
    }

    public void setContactPhone(String contactPhone) {
        put("contact_phone",contactPhone);
    }

    public boolean getPermitCalls() {
        return getBoolean("permit_calls");
    }

    public void setPermitCalls(boolean permitCalls) {put("permit_calls",permitCalls);}

    public Date getWhenDate() {
        return getDate("when_date");
    }

    public void setWhenDate(Date whenDate) {
        put("when_date",whenDate);
    }

    public void setListId(String listId) { put("list_id",listId);}

    public String getListId(){ return getString("list_id"); }

    public ParseUser getUser(){
        return getParseUser("owner");
    }

    public void setOwner(ParseUser user){
        put("owner",user);
    }

    public List<String> getBusinessesList() {
        List<String> list = getList("business_list");
        if(list!=null){
            Log.d(MessagePO.class.getName(),"getBusinessesList() - List filled with "+list.size()+" results");
            return list;
        }
        Log.d(MessagePO.class.getName(),"getBusinessesList() - List empty");
        return new ArrayList<>();
    }

    public void setBusinessesList(List<String> businessesList) {
        addAllUnique("business_list", businessesList);
    }

}

