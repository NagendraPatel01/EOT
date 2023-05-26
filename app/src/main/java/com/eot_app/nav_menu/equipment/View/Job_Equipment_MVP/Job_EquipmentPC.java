package com.eot_app.nav_menu.equipment.View.Job_Equipment_MVP;

import android.content.Context;
import android.util.Log;
import com.eot_app.activitylog.ActivityLogController;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_detail.addinvoiveitem2pkg.model.InvoiceItemDataModel;
import com.eot_app.nav_menu.jobs.job_detail.detail.job_detail_presenter.JobDetail_pc;
import com.eot_app.nav_menu.jobs.job_detail.documents.doc_model.GetFileList_Res;
import com.eot_app.nav_menu.jobs.job_detail.documents.doc_model.GetFileList_req_Model;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.language_support.LanguageController;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Job_EquipmentPC extends JobDetail_pc implements Job_Equipment_PI {
    Job_Equipment_View equipment_view;
    Job job;
    private int count;
    private int updateindex= 0;
    int  updatelimit = AppConstant.LIMIT_MID;


    public Job_EquipmentPC(Job_Equipment_View view) {
        super(view);
        this.equipment_view = view;
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
                                    String completionNote = jsonObject.getAsJsonArray("data").get(0).getAsJsonObject().get("complNote").getAsString();
                                    equipment_view.setCompletionNote(completionNote);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                equipment_view.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
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
    public void getJobItemList(String jobId) {
        if (AppUtility.isInternetConnected()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("jobId", jobId);
            String data = new Gson().toJson(hashMap);
            ApiClient.getservices().eotServiceCall(Service_apis.getItemFromJob, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<InvoiceItemDataModel>>() {
                                }.getType();
                                List<InvoiceItemDataModel> data = new Gson().fromJson(convert, listType);
                                if (data != null && data.size() > 0) {
                                    equipment_view.setJobItemList(data);
                                }
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                equipment_view.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
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

    @Override
    public void getJobAttachment(String jobId) {
        GetFileList_req_Model getFileList_model = new GetFileList_req_Model(updateindex, updatelimit, jobId, "", "");
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
                                    count = jsonObject.get("count").getAsInt();
                                    String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                    Type listType = new com.google.gson.reflect.TypeToken<List<GetFileList_Res>>() {
                                    }.getType();
                                    ArrayList<GetFileList_Res> getFileList_res = new Gson().fromJson(convert, listType);
                                    equipment_view.setList(getFileList_res, "");
                                } else {
                                    equipment_view.noAttachment();
                                    equipment_view.setList(new ArrayList<>(), "");
                                }
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                equipment_view.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {

                            Log.e("", e.getMessage());
                            // AppUtility.progressBarDissMiss();

                        }

                        @Override
                        public void onComplete() {
                            Log.e("onComplete", "second time call");
                            Log.e("onComplete", "onComplete");
                            //  AppUtility.progressBarDissMiss();
                            if ((updateindex + updatelimit) <= count) {
                                updateindex += updatelimit;
                                getAttachFileList(jobId, "", "");
                            }
                        }
                    });
        } else {
            networkDialog();
        }

    }


    private void networkDialog() {
        AppUtility.alertDialog(((Context) equipment_view), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
    }
}
