package com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CustomFormListOffline")
public class CustomFormListOffline {

    @PrimaryKey(autoGenerate = true)
    private int id=0;
    private String frmId;
    private String jtId;
    private String frmnm;
    private String event;
    private String tab;
    private String mandatory;
    private String totalQues;

    public CustomFormListOffline(String frmId, String jtId, String frmnm, String event, String tab, String mandatory, String totalQues) {
        this.frmId = frmId;
        this.jtId = jtId;
        this.frmnm = frmnm;
        this.event = event;
        this.tab = tab;
        this.mandatory = mandatory;
        this.totalQues = totalQues;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrmId() {
        return frmId;
    }

    public void setFrmId(String frmId) {
        this.frmId = frmId;
    }

    public String getJtId() {
        return jtId;
    }

    public void setJtId(String jtId) {
        this.jtId = jtId;
    }

    public String getFrmnm() {
        return frmnm;
    }

    public void setFrmnm(String frmnm) {
        this.frmnm = frmnm;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getTotalQues() {
        return totalQues;
    }

    public void setTotalQues(String totalQues) {
        this.totalQues = totalQues;
    }
}
