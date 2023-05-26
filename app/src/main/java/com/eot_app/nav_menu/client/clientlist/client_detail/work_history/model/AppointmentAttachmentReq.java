package com.eot_app.nav_menu.client.clientlist.client_detail.work_history.model;

public class AppointmentAttachmentReq {
    Integer limit=10;
    Integer index=0;
    String usrId="";
    String appId;

    public AppointmentAttachmentReq(String appId){
        this.appId=appId;
    }
}
