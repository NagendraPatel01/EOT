package com.eot_app.nav_menu.client.clientlist.client_detail.work_history.model;

public class AduitAppointmentHistoryReq {
     int index;
     int limit;
     String search = "";
     String searchType = "0";
     String srtType = "asc";
     String dtf = "";
     String dtt = "";
     String cltId;

    public AduitAppointmentHistoryReq(String cltId, int index, int limit) {
        this.cltId = cltId;
        this.index = index;
        this.limit = limit;
    }
}
