package com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.Fromdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CustomFormSubmited {

    @PrimaryKey(autoGenerate = true)
    private int id=0;
    private String formId;
    private String jobId;
    private String isSubmited;
    private String UserId;

    public CustomFormSubmited(String formId, String jobId, String isSubmited,String UserId) {
        this.formId = formId;
        this.jobId = jobId;
        this.isSubmited = isSubmited;
        this.UserId=UserId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String isSubmited() {
        return isSubmited;
    }

    public void setSubmited(String submited) {
        isSubmited = submited;
    }
}
