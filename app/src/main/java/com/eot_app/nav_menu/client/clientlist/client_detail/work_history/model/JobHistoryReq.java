package com.eot_app.nav_menu.client.clientlist.client_detail.work_history.model;

public class JobHistoryReq {

     int limit;
     int index;
     String search = "";
     String searchType = "0";
     String srtType = "asc";
     String dtf = "";
     String dtt = "";
     String jtId = "";
     String cltId;
     String isFilterBy = "1";
     String apiCallFrom = "1";

    public JobHistoryReq(String cltId, int index, int limit) {
        this.index = index;
        this.limit = limit;
        this.cltId = cltId;
    }


}
