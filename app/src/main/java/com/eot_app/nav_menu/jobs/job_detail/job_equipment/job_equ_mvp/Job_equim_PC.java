package com.eot_app.nav_menu.jobs.job_detail.job_equipment.job_equ_mvp;

import android.content.Context;
import android.util.Log;

import com.eot_app.nav_menu.audit.audit_list.equipment.model.AuditEquipmentRequestModel;
import com.eot_app.nav_menu.audit.audit_list.equipment.model.EquipmentStatus;
import com.eot_app.nav_menu.jobs.job_db.EquArrayModel;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.model.EquipmentStatusReq;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Sonam-11 on 22/9/20.
 */
public class Job_equim_PC implements Job_equim_PI {
    private final Job_equim_View jobEquimView;
    private final int updatelimit;
    private int updateindex;
    private int count;


    public Job_equim_PC(Job_equim_View jobEquimView) {
        this.jobEquimView = jobEquimView;
        this.updatelimit = AppConstant.LIMIT_HIGH;
        this.updateindex = 0;
    }

    @Override
    public void getEquipmentBySiteName(String jobId, String siteNAme) {
        Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobId);
        ArrayList<EquArrayModel> equList = new ArrayList<>();
        if (job != null && job.getEquArray() != null) {
            for (EquArrayModel equArrayModel : job.getEquArray()) {
                if (equArrayModel.getSnm() != null && equArrayModel.getSnm().equalsIgnoreCase(siteNAme))
                    equList.add(equArrayModel);
            }
            jobEquimView.setEuqipmentList(equList);
        }
    }

    @Override
    public void getEquipmentList(String jobId) {
        AppUtility.progressBarDissMiss();
        Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobId);
        if (job != null && job.getEquArray() != null) {
            Log.e("job Eq ::", new Gson().toJson(job.getEquArray()));
            jobEquimView.setEuqipmentList(job.getEquArray());
        }
    }

    @Override
    public void getEquipmentJobList(String auditID, final String jobId) {
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
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            Log.e("", "");
                            try {
//                                Log.d("mahi", jsonObject.toString());

                                if (jsonObject.get("success").getAsBoolean()) {
                                    count = jsonObject.get("count").getAsInt();
                                    String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                    Type listType = new TypeToken<List<EquArrayModel>>() {
                                    }.getType();
                                    List<EquArrayModel> data = new Gson().fromJson(convert, listType);
                                    Log.e("EquipmentListJobPC::",new Gson().toJson(data));

                                    updateEuipmentInDB(data, jobId);

                                } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                    jobEquimView.onSessionExpired(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                                } else {
                                    jobEquimView.showErrorAlertDialog(LanguageController.getInstance().
                                            getServerMsgByKey(jsonObject.get("message").getAsString()));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            AppUtility.progressBarDissMiss();
                            Log.e("", "");
                            jobEquimView.swipeRefresh();
                        }

                        @Override
                        public void onComplete() {
                            jobEquimView.swipeRefresh();
                            Log.v("okhttp refresh","List Refreshed updateindex"+updateindex+" updatelimit "+updatelimit);
                            if ((updateindex + updatelimit) <= count) {
                                Log.v("okhttp refresh","List Refreshed updateindex"+updateindex+" updatelimit "+updatelimit);
                                updateindex += updatelimit;
                                getEquipmentJobList(auditID,jobId);
                            } else {
                                AppUtility.progressBarDissMiss();
                                getEquipmentList(jobId);
                                App_preference.getSharedprefInstance().setEquipmentSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                updateindex = 0;
                                count = 0;
                            }
                        }
                    });
        } else {
            AppUtility.progressBarDissMiss();
            getEquipmentList(jobId);
            jobEquimView.swipeRefresh();
            networkError();
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
            EotApp.getAppinstance().getNotifyForEquipmentCount();
            EotApp.getAppinstance().getNotifyForEquipmentCountRemark();
            EotApp.getAppinstance().getNotifyForEquipmentCountList();
            Log.e("EquipmentListLocalJPC::",new Gson().toJson(job.getEquArray()));
            getEquipmentList(jobId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void addRecordsToDB(List<Job> data) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().inserJob(data);
    }

    private void networkError() {
        AppUtility.alertDialog(((Context) jobEquimView), LanguageController.getInstance().
                        getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey
                        (AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok),
                "", new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return null;
                    }
                });

    }

    @Override
    public void getEquipmentStatus() {
        if (AppUtility.isInternetConnected()) {
            EquipmentStatusReq equipmentListReq = new EquipmentStatusReq();
            String data = new Gson().toJson(equipmentListReq);
            AppUtility.progressBarShow((Context)jobEquimView);
            ApiClient.getservices().eotServiceCall(Service_apis.getEquipmentStatus, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new com.google.gson.reflect.TypeToken<List<EquipmentStatus>>() {
                                }.getType();

                                App_preference.getSharedprefInstance().setEquipmentStatusList("");
                                // store locally
                                String convertString = jsonObject.get("data").getAsJsonArray().toString();
                                App_preference.getSharedprefInstance().setEquipmentStatusList(convertString);
                                EotApp.getAppinstance().getNotifyForEquipmentStatusList();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }


}
