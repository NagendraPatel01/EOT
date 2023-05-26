package com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.Fromdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CustomForm")
public class CustomForm {
    @PrimaryKey(autoGenerate = true)
    private int id=0;
    private String frmid;
    private String ans;
    private String jobid;
    private String opid;

    public CustomForm(String frmid, String ans,String jobid,String opid) {
        this.frmid = frmid;
        this.ans = ans;
        this.jobid=jobid;
        this.opid=opid;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrmid() {
        return frmid;
    }

    public void setFrmid(String frmid) {
        this.frmid = frmid;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}
