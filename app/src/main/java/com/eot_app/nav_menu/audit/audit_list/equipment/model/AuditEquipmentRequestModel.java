package com.eot_app.nav_menu.audit.audit_list.equipment.model;

/**
 * Created by Mahendra Dabi on 11/11/19.
 */
public class AuditEquipmentRequestModel {
    private final String audId;
    private int limit;
    private int index;
    String dateTime;
    private final int isMobile;
    /**
     * 1 for job equipment
     ***/
    int isJob;

    /***Audit equipment**/
    public AuditEquipmentRequestModel(String audId, int limit, int index, String dateTime) {
        this.audId = audId;
        this.limit = limit;
        this.index = index;
        this.dateTime = dateTime;
        isMobile = 1;
    }

    public AuditEquipmentRequestModel(String jobId) {
        this.audId = jobId;
        isMobile = 1;
    }


    public int getLimit() {
        return limit;
    }

    public int getIndex() {
        return index;
    }

    public String getAudId() {
        return audId;
    }

    public void setIsJob(int isJob) {
        this.isJob = isJob;
    }
}
