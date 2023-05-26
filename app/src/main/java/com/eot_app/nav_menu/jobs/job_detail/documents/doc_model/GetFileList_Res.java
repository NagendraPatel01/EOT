package com.eot_app.nav_menu.jobs.job_detail.documents.doc_model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;


/**
 * Created by ubuntu on 9/10/18.
 */

public class GetFileList_Res implements Parcelable {
    private String attachmentId;
    private String deleteTable;
    private String userId;
    private String image_name;
    private String attachFileName;
    private String des;
    private String attachThumnailFileName;
    private String attachFileActualName;
    private String type;
    private String createdate;
    private String complNote;
    String  bitmap;

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public GetFileList_Res(String attachmentId, String deleteTable, String userId, String attachFileName, String attachFileActualName, String type, String createdate, String complNote) {
        this.attachmentId = attachmentId;
        this.deleteTable = deleteTable;
        this.userId = userId;
        this.attachFileName = attachFileName;
        this.attachFileActualName = attachFileActualName;
        this.type = type;
        this.createdate = createdate;
        this.complNote = complNote;
    }

    public GetFileList_Res(String attachmentId, String image_name, String attachFileActualName, String bitmap) {
        this.attachmentId = attachmentId;
        this.image_name = image_name;
        this.attachFileActualName = attachFileActualName;
        this.bitmap = bitmap;
    }
    @SuppressLint("NewApi")
    protected GetFileList_Res(Parcel in) {
        attachmentId = in.readString();
        deleteTable = in.readString();
        userId = in.readString();
        image_name = in.readString();
        attachFileName = in.readString();
        des = in.readString();
        attachThumnailFileName = in.readString();
        attachFileActualName = in.readString();
        type = in.readString();
        createdate = in.readString();
        complNote = in.readString();
        bitmap = in.readString();
    }

    public static final Creator<GetFileList_Res> CREATOR = new Creator<GetFileList_Res>() {
        @Override
        public GetFileList_Res createFromParcel(Parcel in) {
            return new GetFileList_Res(in);
        }

        @Override
        public GetFileList_Res[] newArray(int size) {
            return new GetFileList_Res[size];
        }
    };

    public String getAttachThumnailFileName() {
        return attachThumnailFileName;
    }

    public void setAttachThumnailFileName(String attachThumnailFileName) {
        this.attachThumnailFileName = attachThumnailFileName;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public GetFileList_Res(String attachFileActualName) {
        this.attachFileActualName = attachFileActualName;
    }


    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getDeleteTable() {
        return deleteTable;
    }

    public void setDeleteTable(String deleteTable) {
        this.deleteTable = deleteTable;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAttachFileName() {
        return attachFileName;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setAttachFileName(String attachFileName) {
        this.attachFileName = attachFileName;
    }

    public String getAttachFileActualName() {
        return attachFileActualName;
    }

    public void setAttachFileActualName(String attachFileActualName) {
        this.attachFileActualName = attachFileActualName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getComplNote() {
        return complNote;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressLint("NewApi")
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(attachmentId);
        parcel.writeString(deleteTable);
        parcel.writeString(userId);
        parcel.writeString(image_name);
        parcel.writeString(attachFileName);
        parcel.writeString(des);
        parcel.writeString(attachThumnailFileName);
        parcel.writeString(attachFileActualName);
        parcel.writeString(type);
        parcel.writeString(createdate);
        parcel.writeString(complNote);
        parcel.writeString(bitmap);
    }
}
