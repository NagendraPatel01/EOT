package com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.clientEqu;

public class ClientEquReq {
    int limit;
    int index;
    String search;
    String cltId;

    public ClientEquReq(String cltId){
        this.limit=100;
        this.index=0;
        this.search="";
        this.cltId=cltId;
    }


}
