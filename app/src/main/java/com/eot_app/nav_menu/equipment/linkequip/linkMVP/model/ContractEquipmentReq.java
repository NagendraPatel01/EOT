package com.eot_app.nav_menu.equipment.linkequip.linkMVP.model;

public class ContractEquipmentReq {
    private int index;
    int activeRecord=1;
    private int limit;
    String search="";
    String expiryDtf="";
    String expiryDtt="";
    String cltId;
    String type;
    String jobId;
    String contrId;

    public ContractEquipmentReq( String type, String jobId, String contrId) {
        this.type = type;
        this.jobId = jobId;
        this.contrId = contrId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getCltId() {
        return cltId;
    }

    public String getType() {
        return type;
    }

    public String getJobId() {
        return jobId;
    }

    public String getContrId() {
        return contrId;
    }
}
