package com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.eot_app.utility.DropdownListBean;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created by shivani on 23 Aug 2022.
 */
@Entity(tableName = "JobStatusModelNew")
public class JobStatusModelNew implements DropdownListBean, Serializable {

    @PrimaryKey
    @NonNull
    String id = "0";
    String text = "Not_Started";
    private String url;
    private String urlBitmap;
    private String img;
    private String isDefault;
    private String isStatusShow;
    private String isFwSelect;

    public String getIsStatusShow() {
        return isStatusShow;
    }

    public void setIsStatusShow(String isStatusShow) {
        this.isStatusShow = isStatusShow;
    }

    public String getIsFwSelect() {
        return isFwSelect;
    }

    public void setIsFwSelect(String isFwSelect) {
        this.isFwSelect = isFwSelect;
    }

    public JobStatusModelNew(@NotNull String status, String statusName, String img) {
        this.img=img;
        this.id=status;
        this.text=statusName;

    }

    public JobStatusModelNew(String status, String statusName) {
        this.id=status;
        this.text=statusName;

    }

    public JobStatusModelNew() {

    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlBitmap() {
        return urlBitmap;
    }

    public void setUrlBitmap(String urlBitmap) {
        this.urlBitmap = urlBitmap;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus_no() {
        return id;
    }

    public void setStatus_no(String status_no) {
        this.id = status_no;
    }

    public String getStatus_name() {
        return text;
    }

    public void setStatus_name(String status_name) {
        this.text = status_name;
    }

    @Override
    public String getKey() {
        return getStatus_no();
    }

    @Override
    public String getName() {
        return getStatus_name();
    }


}
