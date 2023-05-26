package com.eot_app.nav_menu.appointment.list.model;

public class AppointmentListReq {
     int usrId;
     int limit;
     int index;
    private String search;
     String dateTime;

    public AppointmentListReq(int usrId, int limit, int index, String dateTime) {
        this.usrId = usrId;
        this.limit = limit;
        this.index = index;
        this.dateTime = dateTime;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getUsrId() {
        return usrId;
    }

    public int getLimit() {
        return limit;
    }

    public int getIndex() {
        return index;
    }

    public String getDateTime() {
        return dateTime;
    }
}
