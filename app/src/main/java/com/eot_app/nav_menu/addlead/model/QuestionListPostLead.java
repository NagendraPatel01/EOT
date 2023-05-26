package com.eot_app.nav_menu.addlead.model;

import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.qus_model.AnswerModel;

import java.util.List;

public class QuestionListPostLead {

    private String queId;

    public QuestionListPostLead(String queId, String frmId, String type, List<AnswerModel> ans) {
        this.queId = queId;
        this.frmId = frmId;
        this.type = type;
        this.ans = ans;
    }

    private String frmId;
    private String type;

    private List<AnswerModel> ans = null;
    private int index;

    public String getQueId() {
        return queId;
    }

    public void setQueId(String queId) {
        this.queId = queId;
    }

    public String getFrmId() {
        return frmId;
    }

    public void setFrmId(String frmId) {
        this.frmId = frmId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AnswerModel> getAns() {
        return ans;
    }

    public void setAns(List<AnswerModel> ans) {
        this.ans = ans;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
