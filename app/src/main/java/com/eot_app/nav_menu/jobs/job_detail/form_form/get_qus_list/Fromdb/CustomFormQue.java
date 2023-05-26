package com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.Fromdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "CustomFormQue")
public class CustomFormQue {

    @PrimaryKey(autoGenerate = true)
    private int id=0;
    private String Formid;
    private String quelist;
    private String opid;

    public CustomFormQue(String Formid, String quelist, String opid) {
        this.Formid = Formid;
        this.quelist = quelist;
        this.opid = opid;
    }

    public String getQuelist() {
        return quelist;
    }

    public void setQuelist(String quelist) {
        this.quelist = quelist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFormid() {
        return Formid;
    }

    public void setFormid(String formid) {
        Formid = formid;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }
}
