package com.eot_app.nav_menu.equipment.model.aduit_job_history;
/**
 moduleType for aduitHistory=1
 moduleType for jobHistory=0
 **/

public class Aduit_Job_History_Req {

    String equId;
    int limit = 50;
    int index = 0;
    String moduleType;

    public Aduit_Job_History_Req(String equId, String moduleType){
        this.equId=equId;
        this.moduleType=moduleType;
    }


}
