package com.eot_app.nav_menu.jobs.job_detail.detail.job_detail_presenter;

import android.util.Log;
import androidx.fragment.app.Fragment;
import com.eot_app.activitylog.ActivityLogController;
import com.eot_app.activitylog.LogModel;
import com.eot_app.nav_menu.custom_fileds.custom_model.CustOmFiledReqModel;
import com.eot_app.nav_menu.custom_fileds.custom_model.CustOmFiledResModel;
import com.eot_app.nav_menu.custom_fileds.custom_model.CustOmFormQuestionsReq;
import com.eot_app.nav_menu.custom_fileds.custom_model.CustOmFormQuestionsRes;
import com.eot_app.nav_menu.jobs.add_job.add_job_recr.DeleteReCur;
import com.eot_app.nav_menu.jobs.job_controller.ChatController;
import com.eot_app.nav_menu.jobs.job_db.EquArrayModel;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_detail.JobDetailActivity;
import com.eot_app.nav_menu.jobs.job_detail.addinvoiveitem2pkg.model.InvoiceItemDataModel;
import com.eot_app.nav_menu.jobs.job_detail.detail.job_detail_view.JobDetail_view;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.CompletionDetails;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.CompletionDetailsPost;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.JobStatusModelNew;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.Jobdetail_status_res;
import com.eot_app.nav_menu.jobs.job_detail.documents.doc_model.GetFileList_Res;
import com.eot_app.nav_menu.jobs.job_detail.documents.doc_model.GetFileList_req_Model;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_db.model_pkg.ItembyJobModel;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.model.EquipmentStatusReq;
import com.eot_app.nav_menu.jobs.job_detail.job_status_pkg.JobStatus_Controller;
import com.eot_app.nav_menu.jobs.joboffline_db.JobOfflineDataModel;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.db.OfflineDataController;
import com.eot_app.utility.language_support.LanguageController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hypertrack.hyperlog.HyperLog;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by aplite_pc302 on 6/25/18.
 */

public class JobDetail_pc implements JobDetail_pi {
    private final int updatelimit;
    JobDetail_view view;
    private int count;
    private int updateindex;
    private int updateindexAtttachment;
    JobStatusModelNew jobstatus;
    String jobId;
    private String img = "";


    public JobDetail_pc(JobDetail_view view) {
        this.view = view;
        LogModel logModel = ActivityLogController
                .getObj(ActivityLogController.JOB_MODULE, ActivityLogController.JOB_DETAILS, ActivityLogController.JOB_MODULE);
        ActivityLogController.saveOfflineTable(logModel);
        this.updateindex = 0;
        this.updateindexAtttachment = 0;
        this.updatelimit = AppConstant.LIMIT_MID;
    }


    @Override
    public void getAttachFileList(final String jobId, final String usrId, final String type) {
        try {
            GetFileList_req_Model getFileList_model = new GetFileList_req_Model(updateindexAtttachment, updatelimit, jobId, usrId, type);
            JsonObject jsonObject = AppUtility.getJsonObject(new Gson().toJson(getFileList_model));

            if (AppUtility.isInternetConnected()) {

                ActivityLogController.saveActivity(ActivityLogController.JOB_MODULE, ActivityLogController.JOB_DOCUMENT_LIST, ActivityLogController.JOB_MODULE);
                ApiClient.getservices().eotServiceCall(Service_apis.getJobAttachments, AppUtility.getApiHeaders(), jsonObject)

                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<JsonObject>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NotNull JsonObject jsonObject) {
                                Log.e("FileList", "" + jsonObject.toString());
                                if (jsonObject.get("success").getAsBoolean()) {
                                    if (jsonObject.get("data").getAsJsonArray().size() > 0) {
                                        try {
                                            count = jsonObject.get("count").getAsInt();
                                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                            Type listType = new TypeToken<List<GetFileList_Res>>() {
                                            }.getType();
                                            ArrayList<GetFileList_Res> getFileList_res = new Gson().fromJson(convert, listType);
                                            view.setList(getFileList_res, "");
                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                    } else {
                                        view.setList(new ArrayList<>(), "");
                                    }
                                } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                    //  view.onSessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                                }
                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                Log.e("", e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                Log.e("onComplete", "onComplete");
                                Log.e("onComplete", "updateindexAtttachment:"+updateindexAtttachment);
                                Log.e("onComplete", "count:"+count);
                                Log.e("onComplete", "updatelimit:"+updatelimit);
                                if ((updateindexAtttachment + updatelimit) <= count) {
                                    Log.e("onComplete", "second time call");
                                    updateindexAtttachment +=updatelimit;
                                    getAttachFileList(jobId, usrId, type);
                                }
                            }
                        });
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void getItemFromServer(String jobId) {
        if (AppUtility.isInternetConnected()) {
            ItembyJobModel model = new ItembyJobModel(jobId);//, App_preference.getSharedprefInstance().getJobSyncTime()
            String data = new Gson().toJson(model);
            ApiClient.getservices().eotServiceCall(Service_apis.getItemFromJob, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new com.google.common.reflect.TypeToken<List<InvoiceItemDataModel>>() {
                                }.getType();
                                List<InvoiceItemDataModel> data = new Gson().fromJson(convert, listType);
                                if (data.size() > 0) {
                                    addgetItemFromJobToDB(data, jobId);
                                }
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            getItemListByJobFromDB(jobId);
                        }
                    });
        }
    }
    private void addgetItemFromJobToDB(List<InvoiceItemDataModel> data, String jobId) {
        if (data != null && data.size() > 0) {
            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobitems(jobId, data);
        }
    }
    @Override
    public void getItemListByJobFromDB(String jobId) {
        Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobId);
        try {
            if (job.getItemData() != null && job.getItemData() != null && job.getItemData().size() > 0) {
                view.setItemListByJob(job.getItemData());
            } else {
                view.setItemListByJob(new ArrayList<>());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stopRecurpattern(String jobId) {

        ApiClient.getservices().eotServiceCall(Service_apis.deleteRecur, AppUtility.getApiHeaders(),
                AppUtility.getJsonObject(new Gson().toJson(new DeleteReCur(jobId))))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                    }
                    @Override
                    public void onNext(@NotNull JsonObject jsonObject) {
                        Log.e("", "");
                        if (jsonObject.get("success").getAsBoolean()) {
                            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getServerMsgByKey("recur_deleted"));
                            view.StopRecurPatternHide();
                            Log.e("", "");
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                            view.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                        } else {
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("TAG", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG", " e.getMessage()");
                    }
                });

    }

    private void showErrorDialog(String msg) {
        AppUtility.error_Alert_Dialog(((Fragment) view).getActivity(), msg, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok)
                , () -> null);
    }

    @Override
    synchronized public void changeJobStatusAlertInvisible(String jobid, String type, JobStatusModelNew jobStatus, String lat, String lng
            , String isMailSentToClt
    ) {
        HyperLog.i("JobDetail_pc", "changeJobStatusAlertInvisible(M) start");

        this.jobstatus = jobStatus;
        this.jobId = jobid;


        // to remove double calling of api for same status in case of double tap
        try{
            // to check the current status of job
            String currentJobStatus =AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobStatusByJobId(jobid);
            Log.e("CurrentStatus::",currentJobStatus);
            HyperLog.e("TAG","CurrentStatusJob:: jobId :: "+jobId +"Status :: "+currentJobStatus);

            if(jobstatus!=null
                    &&jobstatus.getKey()!=null
                    &&jobstatus.getKey().equalsIgnoreCase(currentJobStatus)) {
                return;
            }
            //TODO
            // 7 is for in progress and 2 is for accepted
            // for resolving the issue , job  getting accepted after in progress
            if(jobstatus!=null
                    &&jobstatus.getKey()!=null &&currentJobStatus!=null
                    &&currentJobStatus.equalsIgnoreCase("7")
                    &&jobstatus.getKey().equalsIgnoreCase("2")) {
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        String dateTime = AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT);
        Gson gson = new Gson();
        Jobdetail_status_res request = new Jobdetail_status_res(jobId,
                App_preference.getSharedprefInstance().getLoginRes().getUsrId(), type, jobstatus.getStatus_no(),
                dateTime, lat, lng, isMailSentToClt);
        String data = gson.toJson(request);
        Job jobData = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobid);
        /* **JOB status change Before JOB sync**/
        if (jobData != null && jobData.getJobId() != null && jobData.getTempId() != null && jobData.getJobId().equals(jobData.getTempId())) {
            JobOfflineDataModel jobOfflineDataModel = new JobOfflineDataModel(Service_apis.changeJobStatus, data, dateTime, jobData.getTempId());
            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobOfflineDao().insertJobOfflineData(jobOfflineDataModel);
            // HyperLog.i("", "Job Not Sync -----" + "Add in Job table");
            HyperLog.i("JobDetail_pc", "Job status saved IN LOCAL TABLE with temp id");
            HyperLog.i("JobDetail_pc", "ob status saved In Local Table");
        } else {
            OfflineDataController.getInstance().addInOfflineDB(Service_apis.changeJobStatus, data, dateTime);
            HyperLog.i("JobDetail_pc", "Job status saved on with job id");
            HyperLog.i("JobDetail_pc", "Job status saved In Offline Table");
        }


        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobByStatus(jobId, jobstatus.getStatus_no(),
                AppUtility.getDateByMiliseconds());
        HyperLog.i("JobDetail_pc", "Local DB updated with selected job status");


        if (jobStatus.getStatus_no().equals(AppConstant.In_Progress)) {
            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().setOtherToPending(jobId, AppConstant.In_Progress, AppConstant.New_On_Hold);
            HyperLog.i("JobDetail_pc", "Local DB refresh for other In progress job");
        }
        view.setResultForChangeInJob("Update", jobid);
        HyperLog.i("JobDetail_pc", "callback completion of local DB update the view on list");

        //data and UI reflect after offline save
        if (jobStatus.getStatus_no().equals(AppConstant.Cancel) || jobStatus.getStatus_no().equals(AppConstant.Reject)||jobstatus.getStatus_no().equals("10")) {
            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobById(jobId);
            ChatController.getInstance().removeListnerByJobID(jobid);
            ((JobDetailActivity) ((Fragment) view).getActivity()).finishActivityWithSetResult();
            HyperLog.i("JobDetail_pc", "delete job with status cancel or reject");

        } else {
            HyperLog.i("JobDetail_pc", "Local DB updated with selected job status");
            view.setButtonsUI(jobstatus);
        }

        LogModel logModel = ActivityLogController
                .getObj(ActivityLogController.JOB_MODULE, ActivityLogController.JOB_STATUS, ActivityLogController.JOB_MODULE);
        ActivityLogController.saveOfflineTable(logModel);

        HyperLog.i("JobDetail_pc", "changeJobStatusAlertInvisible(M) Completed");

    }


    @Override
    public String getStatusName(String status) {
        JobStatusModelNew jobStatusModel = JobStatus_Controller.getInstance().getStatusObjectById(status);
        if (jobStatusModel != null) {
            if (jobStatusModel.getImg() != null)
                img = jobStatusModel.getImg();
            if (jobStatusModel.getStatus_name() != null && jobStatusModel.getStatus_name().equals("New")) {
                return "Not Started";
            } else {
                return jobStatusModel.getStatus_name();
            }
        }
        return "";
    }

    @Override
    public String getImg() {
        return img;
    }

    @Override
    public void getEquipmentList(String jobId) {
        Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobId);
        if (job != null && job.getEquArray() != null) {
            view.setEuqipmentList(job.getEquArray());
        }
    }

    @Override
    public void refreshList(String auditID, final String jobId) {
        if (AppUtility.isInternetConnected()) {
            HashMap<String, String> auditListRequestModel = new HashMap<>();
            auditListRequestModel.put("limit", ""+updatelimit);
            auditListRequestModel.put("index", ""+updateindex);
            auditListRequestModel.put("audId", ""+auditID);
            auditListRequestModel.put("isJob", "1");
            auditListRequestModel.put("isParent", "1");

//            AuditEquipmentRequestModel auditListRequestModel = new AuditEquipmentRequestModel(auditID,
//                    updatelimit, updateindex, "");
//            auditListRequestModel.setIsJob(1);

            String data = new Gson().toJson(auditListRequestModel);
            ApiClient.getservices().eotServiceCall(Service_apis.getEquipmentList, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {
                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            Log.e("", "");
                            try {
                                Log.d("mahi", jsonObject.toString());
                                if (jsonObject.get("success").getAsBoolean()) {
                                    count = jsonObject.get("count").getAsInt();
                                    String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                    Type listType = new com.google.common.reflect.TypeToken<List<EquArrayModel>>() {
                                    }.getType();
                                    List<EquArrayModel> data = new Gson().fromJson(convert, listType);

                                    updateEuipmentInDB(data, jobId);

                                    view.setEuqipmentList(data);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("", "");
                        }

                        @Override
                        public void onComplete() {
                            if ((updateindex + updatelimit) <= count) {
                                updateindex += updatelimit;
                            } else {
                                App_preference.getSharedprefInstance().setEquipmentSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                updateindex = 0;
                                count = 0;
                            }
                        }
                    });
        } else {
            getEquipmentList(jobId);

        }
    }


    @Override
    public void getEquipmentStatus() {
        if (AppUtility.isInternetConnected()) {
            EquipmentStatusReq equipmentListReq = new EquipmentStatusReq();
            String data = new Gson().toJson(equipmentListReq);
            ApiClient.getservices().eotServiceCall(Service_apis.getEquipmentStatus, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                App_preference.getSharedprefInstance().setEquipmentStatusList("");
                                // store locally
                                String convertString = jsonObject.get("data").getAsJsonArray().toString();
                                App_preference.getSharedprefInstance().setEquipmentStatusList(convertString);
                                EotApp.getAppinstance().getNotifyForEquipmentStatusList();
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private void updateEuipmentInDB(List<EquArrayModel> data, String jobId) {
        try {
            Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobId);
            /* *****Notify JOB overView for Equipmetn Added first time ****/
            if (job.getEquArray() != null && job.getEquArray().size() == 0) {
                job.setEquArray(data);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobequipArray(jobId, data);
                EotApp.getAppinstance().getJobFlagOverView();
            } else {
                /* **Refresh job Table in Exiting Equ. lisy***/
                job.setEquArray(data);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobequipArray(jobId, data);
                if (job.getEquArray() != null && job.getEquArray().size() == 0)
                    EotApp.getAppinstance().getJobFlagOverView();
            }
            getEquipmentList(jobId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public JobStatusModelNew getJobStatusObject(String statusId) {
        JobStatusModelNew jobStatusModel = JobStatus_Controller.getInstance().getStatusObjectById(statusId);
        if (jobStatusModel != null)
            return jobStatusModel;
        else return new JobStatusModelNew();
    }

    @Override
    public boolean isOldStaus(String status_no, String jobId) {
        if (jobId != null && !jobId.equals("")) {
            Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobId);
            if (job != null && job.getStatus() != null && status_no != null && !status_no.equals("")) {
                return AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobId).getStatus().equals(status_no);
            }
        }
        return true;
    }

    @Override
    public void setJobCurrentStatus(String jobid) {
        Job item_job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobid);
        view.resetstatus(item_job.getStatus());
    }

    @Override
    public boolean checkContactHideOrNot() {
        try {
            if (App_preference.getSharedprefInstance().getLoginRes().getIsHideContact().equals("1")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void getCustomFieldQues(final String jobId) {
        if (AppUtility.isInternetConnected()) {
            ApiClient.getservices().eotServiceCall(Service_apis.getFormDetail, AppUtility.getApiHeaders(),
                    AppUtility.getJsonObject(new Gson().toJson(new CustOmFiledReqModel("1"))))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = jsonObject.get("data").getAsJsonObject().toString();
                                CustOmFiledResModel resModel = new Gson().fromJson(convert, CustOmFiledResModel.class);
                                getQuestByParntId(resModel.getFrmId(), jobId);
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                view.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }


                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("TAG", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            // joblist_view.setRefereshPullOff();
        }
    }

    @Override
    public void getQuestByParntId(String formId, String jobId) {
        CustOmFormQuestionsReq model = new CustOmFormQuestionsReq(formId, jobId);
        if (AppUtility.isInternetConnected()) {
            ApiClient.getservices().eotServiceCall(Service_apis.getQuestionsByParentId, AppUtility.getApiHeaders(),
                    AppUtility.getJsonObject(new Gson().toJson(model)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = jsonObject.get("data").getAsJsonArray().toString();
                                Type listType = new TypeToken<List<CustOmFormQuestionsRes>>() {
                                }.getType();
                                ArrayList<CustOmFormQuestionsRes> data = new Gson().fromJson(convert, listType);
                                view.setCustomFiledList(data);
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                view.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }


                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("TAG", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
    }

    @Override
    public void getJobCompletionDetails(String jobId) {
        if (AppUtility.isInternetConnected()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("jobId", jobId);
            hashMap.put("usrId", App_preference.getSharedprefInstance().getLoginRes().getUsrId());
            hashMap.put("type", "1");
            ApiClient.getservices().eotServiceCall(Service_apis.getJobCompletionNOte, AppUtility.getApiHeaders(),
                    AppUtility.getJsonObject(new Gson().toJson(hashMap)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                try {

                                    String convert = jsonObject.get("data").getAsJsonArray().toString();
                                    Type listType = new TypeToken<List<CompletionDetails>>() {
                                    }.getType();
                                    ArrayList<CompletionDetails> data = new Gson().fromJson(convert, listType);
                                    view.setCompletionDetails(data);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                view.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }


                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("TAG", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            AppUtility.progressBarDissMiss();
                        }
                    });
        }
    }


    @Override
    public void addJobCompletionDetails(String jobId,CompletionDetailsPost.CompletionDetail obj,int logType) {
        if (AppUtility.isInternetConnected()) {

            List< CompletionDetailsPost.CompletionDetail> list = new ArrayList<>();
            list.add(obj);

            CompletionDetailsPost completionDetailsPost = new CompletionDetailsPost(logType,
                    App_preference.getSharedprefInstance().getLoginRes().getUsrId(),list,jobId);

             AppUtility.progressBarShow((((Fragment) view).getActivity()));

            ApiClient.getservices().addCompletionDetails( AppUtility.getApiHeaders(),
                    completionDetailsPost)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                try {
//                                    String convert = jsonObject.get("data").getAsJsonArray().toString();
                                    getJobCompletionDetails(jobId);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                view.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }


                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("TAG", e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
        else
            networkDialog();
    }

    private void networkDialog() {
        AppUtility.alertDialog((((Fragment) view).getActivity()), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
    }
}
