package com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompletionDetails {


    @SerializedName("attachments")
    private List<Attachments> attachments;
    @SerializedName("lastStatus")
    private String lastStatus;
    @SerializedName("lastStatusTime")
    private String lastStatusTime;
    @SerializedName("lastBrkTime")
    private String lastBrkTime;
    @SerializedName("firstBrkTime")
    private String firstBrkTime;
    @SerializedName("lastPorgressTime")
    private String lastPorgressTime;
    @SerializedName("lastPorgressStatus")
    private String lastPorgressStatus;
    @SerializedName("actualTime")
    private int actualTime;
    @SerializedName("pauseTime")
    private int pauseTime;
    @SerializedName("progressStatus")
    private String progressStatus;
    @SerializedName("logoutTime")
    private String logoutTime;
    @SerializedName("loginTime")
    private String loginTime;
    @SerializedName("lastTrvlBrkTime")
    private String lastTrvlBrkTime;
    @SerializedName("firstTrvlBrkTime")
    private String firstTrvlBrkTime;
    @SerializedName("travel_lastPorgressTime")
    private String travel_lastPorgressTime;
    @SerializedName("travel_lastPorgressStatus")
    private String travel_lastPorgressStatus;
    @SerializedName("travelTime")
    private int travelTime;
    @SerializedName("travel_pauseTime")
    private int travel_pauseTime;
    @SerializedName("travel_progressStatus")
    private String travel_progressStatus;
    @SerializedName("tarvel_logoutTime")
    private String tarvel_logoutTime;
    @SerializedName("travel_loginTime")
    private String travel_loginTime;
    @SerializedName("complNoteType")
    private String complNoteType;
    @SerializedName("commentDate")
    private String commentDate;
    @SerializedName("complNote")
    private String complNote;
    @SerializedName("nm")
    private String nm;
    @SerializedName("jobId")
    private String jobId;
    @SerializedName("usrId")
    private String usrId;

    public List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachments> attachments) {
        this.attachments = attachments;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public String getLastStatusTime() {
        return lastStatusTime;
    }

    public void setLastStatusTime(String lastStatusTime) {
        this.lastStatusTime = lastStatusTime;
    }

    public String getLastBrkTime() {
        return lastBrkTime;
    }

    public void setLastBrkTime(String lastBrkTime) {
        this.lastBrkTime = lastBrkTime;
    }

    public String getFirstBrkTime() {
        return firstBrkTime;
    }

    public void setFirstBrkTime(String firstBrkTime) {
        this.firstBrkTime = firstBrkTime;
    }

    public String getLastPorgressTime() {
        return lastPorgressTime;
    }

    public void setLastPorgressTime(String lastPorgressTime) {
        this.lastPorgressTime = lastPorgressTime;
    }

    public String getLastPorgressStatus() {
        return lastPorgressStatus;
    }

    public void setLastPorgressStatus(String lastPorgressStatus) {
        this.lastPorgressStatus = lastPorgressStatus;
    }

    public int getActualTime() {
        return actualTime;
    }

    public void setActualTime(int actualTime) {
        this.actualTime = actualTime;
    }

    public int getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(int pauseTime) {
        this.pauseTime = pauseTime;
    }

    public String getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(String progressStatus) {
        this.progressStatus = progressStatus;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLastTrvlBrkTime() {
        return lastTrvlBrkTime;
    }

    public void setLastTrvlBrkTime(String lastTrvlBrkTime) {
        this.lastTrvlBrkTime = lastTrvlBrkTime;
    }

    public String getFirstTrvlBrkTime() {
        return firstTrvlBrkTime;
    }

    public void setFirstTrvlBrkTime(String firstTrvlBrkTime) {
        this.firstTrvlBrkTime = firstTrvlBrkTime;
    }

    public String getTravel_lastPorgressTime() {
        return travel_lastPorgressTime;
    }

    public void setTravel_lastPorgressTime(String travel_lastPorgressTime) {
        this.travel_lastPorgressTime = travel_lastPorgressTime;
    }

    public String getTravel_lastPorgressStatus() {
        return travel_lastPorgressStatus;
    }

    public void setTravel_lastPorgressStatus(String travel_lastPorgressStatus) {
        this.travel_lastPorgressStatus = travel_lastPorgressStatus;
    }

    public int getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(int travelTime) {
        this.travelTime = travelTime;
    }

    public int getTravel_pauseTime() {
        return travel_pauseTime;
    }

    public void setTravel_pauseTime(int travel_pauseTime) {
        this.travel_pauseTime = travel_pauseTime;
    }

    public String getTravel_progressStatus() {
        return travel_progressStatus;
    }

    public void setTravel_progressStatus(String travel_progressStatus) {
        this.travel_progressStatus = travel_progressStatus;
    }

    public String getTarvel_logoutTime() {
        return tarvel_logoutTime;
    }

    public void setTarvel_logoutTime(String tarvel_logoutTime) {
        this.tarvel_logoutTime = tarvel_logoutTime;
    }

    public String getTravel_loginTime() {
        return travel_loginTime;
    }

    public void setTravel_loginTime(String travel_loginTime) {
        this.travel_loginTime = travel_loginTime;
    }

    public String getComplNoteType() {
        return complNoteType;
    }

    public void setComplNoteType(String complNoteType) {
        this.complNoteType = complNoteType;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getComplNote() {
        return complNote;
    }

    public void setComplNote(String complNote) {
        this.complNote = complNote;
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public static class Attachments {
        @SerializedName("isLinked")
        private String isLinked;
        @SerializedName("attchParentId")
        private String attchParentId;
        @SerializedName("size")
        private int size;
        @SerializedName("attchOriginId")
        private String attchOriginId;
        @SerializedName("des")
        private String des;
        @SerializedName("att_docName")
        private String att_docName;
        @SerializedName("name")
        private String name;
        @SerializedName("attFolderNm")
        private String attFolderNm;
        @SerializedName("createdate")
        private String createdate;
        @SerializedName("type")
        private String type;
        @SerializedName("attachFileActualName")
        private String attachFileActualName;
        @SerializedName("attachThumnailFileName")
        private String attachThumnailFileName;
        @SerializedName("attachFileName")
        private String attachFileName;
        @SerializedName("userId")
        private String userId;
        @SerializedName("image_name")
        private String image_name;
        @SerializedName("deleteTable")
        private String deleteTable;
        @SerializedName("attachmentId")
        private String attachmentId;

        public String getIsLinked() {
            return isLinked;
        }

        public void setIsLinked(String isLinked) {
            this.isLinked = isLinked;
        }

        public String getAttchParentId() {
            return attchParentId;
        }

        public void setAttchParentId(String attchParentId) {
            this.attchParentId = attchParentId;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getAttchOriginId() {
            return attchOriginId;
        }

        public void setAttchOriginId(String attchOriginId) {
            this.attchOriginId = attchOriginId;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getAtt_docName() {
            return att_docName;
        }

        public void setAtt_docName(String att_docName) {
            this.att_docName = att_docName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAttFolderNm() {
            return attFolderNm;
        }

        public void setAttFolderNm(String attFolderNm) {
            this.attFolderNm = attFolderNm;
        }

        public String getCreatedate() {
            return createdate;
        }

        public void setCreatedate(String createdate) {
            this.createdate = createdate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAttachFileActualName() {
            return attachFileActualName;
        }

        public void setAttachFileActualName(String attachFileActualName) {
            this.attachFileActualName = attachFileActualName;
        }

        public String getAttachThumnailFileName() {
            return attachThumnailFileName;
        }

        public void setAttachThumnailFileName(String attachThumnailFileName) {
            this.attachThumnailFileName = attachThumnailFileName;
        }

        public String getAttachFileName() {
            return attachFileName;
        }

        public void setAttachFileName(String attachFileName) {
            this.attachFileName = attachFileName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getImage_name() {
            return image_name;
        }

        public void setImage_name(String image_name) {
            this.image_name = image_name;
        }

        public String getDeleteTable() {
            return deleteTable;
        }

        public void setDeleteTable(String deleteTable) {
            this.deleteTable = deleteTable;
        }

        public String getAttachmentId() {
            return attachmentId;
        }

        public void setAttachmentId(String attachmentId) {
            this.attachmentId = attachmentId;
        }
    }
}
