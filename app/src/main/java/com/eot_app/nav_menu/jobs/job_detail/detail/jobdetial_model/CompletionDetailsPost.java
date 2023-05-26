package com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompletionDetailsPost {
    public CompletionDetailsPost(int logType, String usrFor, List<CompletionDetail> completionDetail, String jobId) {
        this.logType = logType;
        this.usrFor = usrFor;
        this.completionDetail = completionDetail;
        this.jobId = jobId;
    }

    @SerializedName("logType")
    private int logType;
    @SerializedName("usrFor")
    private String usrFor;
    @SerializedName("completionDetail")
    private List<CompletionDetail> completionDetail;
    @SerializedName("jobId")
    private String jobId;

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    public String getUsrFor() {
        return usrFor;
    }

    public void setUsrFor(String usrFor) {
        this.usrFor = usrFor;
    }

    public List<CompletionDetail> getCompletionDetail() {
        return completionDetail;
    }

    public void setCompletionDetail(List<CompletionDetail> completionDetail) {
        this.completionDetail = completionDetail;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public static class CompletionDetail {
        @SerializedName("tra_iscalculate")
        private int tra_iscalculate;
        @SerializedName("job_iscalculate")
        private int job_iscalculate;
        @SerializedName("schdlFinish")
        private String schdlFinish;
        @SerializedName("schdlStart")
        private String schdlStart;
        @SerializedName("tlogEnd")
        private String tlogEnd;
        @SerializedName("tlogStart")
        private String tlogStart;
        @SerializedName("usrId")
        private String usrId;

        public CompletionDetail(int tra_iscalculate, int job_iscalculate, String schdlFinish, String schdlStart, String tlogEnd, String tlogStart, String usrId) {
            this.tra_iscalculate = tra_iscalculate;
            this.job_iscalculate = job_iscalculate;
            this.schdlFinish = schdlFinish;
            this.schdlStart = schdlStart;
            this.tlogEnd = tlogEnd;
            this.tlogStart = tlogStart;
            this.usrId = usrId;
        }

        public int getTra_iscalculate() {
            return tra_iscalculate;
        }

        public void setTra_iscalculate(int tra_iscalculate) {
            this.tra_iscalculate = tra_iscalculate;
        }

        public int getJob_iscalculate() {
            return job_iscalculate;
        }

        public void setJob_iscalculate(int job_iscalculate) {
            this.job_iscalculate = job_iscalculate;
        }

        public String getSchdlFinish() {
            return schdlFinish;
        }

        public void setSchdlFinish(String schdlFinish) {
            this.schdlFinish = schdlFinish;
        }

        public String getSchdlStart() {
            return schdlStart;
        }

        public void setSchdlStart(String schdlStart) {
            this.schdlStart = schdlStart;
        }

        public String getTlogEnd() {
            return tlogEnd;
        }

        public void setTlogEnd(String tlogEnd) {
            this.tlogEnd = tlogEnd;
        }

        public String getTlogStart() {
            return tlogStart;
        }

        public void setTlogStart(String tlogStart) {
            this.tlogStart = tlogStart;
        }

        public String getUsrId() {
            return usrId;
        }

        public void setUsrId(String usrId) {
            this.usrId = usrId;
        }
    }
}
