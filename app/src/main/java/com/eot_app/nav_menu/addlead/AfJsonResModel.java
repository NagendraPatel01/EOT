package com.eot_app.nav_menu.addlead;

import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.qus_model.AnswerModel;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.qus_model.OptionModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AfJsonResModel implements Serializable {
    @SerializedName("options")
    private List<OptionModel> options;
    @SerializedName("frmId")
    private String frmId;
    @SerializedName("queWidth")
    private String queWidth;
    @SerializedName("isCustom")
    private boolean isCustom;
    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private String id;
    @SerializedName("mand")
    private boolean mand;
    @SerializedName("formControlName")
    private String formControlName;
    @SerializedName("fieldName")
    private String fieldName;

    public List<AnswerModel> getAns() {
        return ans;
    }

    public void setAns(List<AnswerModel> ans) {
        this.ans = ans;
    }

    private List<AnswerModel> ans = new ArrayList<>();

    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<OptionModel> getOptions() {
        return options;
    }

    public void setOptions(List<OptionModel> options) {
        this.options = options;
    }

    public String getFrmId() {
        return frmId;
    }

    public void setFrmId(String frmId) {
        this.frmId = frmId;
    }

    public String getQueWidth() {
        return queWidth;
    }

    public void setQueWidth(String queWidth) {
        this.queWidth = queWidth;
    }

    public boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getMand() {
        return mand;
    }

    public void setMand(boolean mand) {
        this.mand = mand;
    }

    public String getFormControlName() {
        return formControlName;
    }

    public void setFormControlName(String formControlName) {
        this.formControlName = formControlName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

}
