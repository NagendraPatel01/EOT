package com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_mvp;

import android.util.Log;

import com.eot_app.nav_menu.jobs.job_detail.JobDetailActivity;
import com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_model.CustomFormListOffline;
import com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_model.CustomFormList_Res;
import com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_model.FormList_Model_Req;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by ubuntu on 13/9/18.
 */

public class Cstm_Form_Pc implements Custm_Form_Pi {
    JobDetailActivity jobDetailActivity;
    private int count;
    private int updateindex;
    private int updatelimit;
    private Cstm_Form_View cstmFormView;


    public Cstm_Form_Pc(JobDetailActivity jobDetailActivity) {
        this.jobDetailActivity = jobDetailActivity;
        this.updateindex = 0;
        this.updatelimit = AppConstant.LIMIT_MID;
    }

    public Cstm_Form_Pc(Cstm_Form_View cstmFormView) {
        this.cstmFormView = cstmFormView;
    }

    @Override
    public void getFormListBYSwipe(final String jobId, final ArrayList<String> jtId) {

        final FormList_Model_Req formList_model = new FormList_Model_Req(App_preference.getSharedprefInstance().getLoginRes().getCompId(),
                jobId, jtId, App_preference.getSharedprefInstance().getLoginRes().getUsrId());
        String jsonObjectdata = new Gson().toJson(formList_model);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonObjectdata).getAsJsonObject();

        ApiClient.getservices().eotServiceCall(Service_apis.getFormList, AppUtility.getApiHeaders(), jsonObject)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        Log.e("Responce", jsonObject.toString());
                        if (jsonObject.get("success").getAsBoolean() && jsonObject.get("data").getAsJsonArray().size() > 0) {
                            if (jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("status_code") != null && jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("status_code").getAsString().equals("1")) {
                                String jobId = jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("jobid").getAsString();
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobById(jobId);
//                          for alert msg to remove job
                                EotApp.getAppinstance().notifyObserver("removeFW", jobId, LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            } else if (jsonObject.get("count").getAsInt() > 0) {
                                count = jsonObject.get("count").getAsInt();
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<CustomFormList_Res>>() {
                                }.getType();
                                ArrayList<CustomFormList_Res> formList = new Gson().fromJson(convert, listType);
                                if (cstmFormView != null) {
                                    cstmFormView.setFormList(formList);
                                }
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
//                        if ((updateindex + updatelimit) <= count) {
//                            updateindex = updatelimit;
//                            getFormListBYSwipe(jobId, jtId);
//                        }

                    }
                });

    }

    //get custom form
    @Override
    public void callApiGetFormlist(final String jobId, final ArrayList<String> jtId) {
        if (AppUtility.isInternetConnected()) {
        final FormList_Model_Req formList_model = new FormList_Model_Req(updateindex, updatelimit, App_preference.getSharedprefInstance().getLoginRes().getCompId(),
                jobId, jtId, App_preference.getSharedprefInstance().getLoginRes().getUsrId());
        String jsonObjectdata = new Gson().toJson(formList_model);
        JsonParser parser = new JsonParser();
        final JsonObject jsonObject = parser.parse(jsonObjectdata).getAsJsonObject();

        ApiClient.getservices().eotServiceCall(Service_apis.getFormList, AppUtility.getApiHeaders(), jsonObject)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        Log.e("Responce", jsonObject.toString());
                        if (jsonObject.get("success").getAsBoolean() && jsonObject.get("data").getAsJsonArray().size() > 0) {
                            if (jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("status_code") != null && jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("status_code").getAsString().equals("1")) {
                                String jobId = jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("jobid").getAsString();
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobById(jobId);
//                          for alert msg to remove job
                                EotApp.getAppinstance().notifyObserver("removeFW", jobId, LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            } else if (jsonObject.get("count").getAsInt() > 0) {
                                count = jsonObject.get("count").getAsInt();
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<CustomFormList_Res>>() {
                                }.getType();
                                ArrayList<CustomFormList_Res> formList = new Gson().fromJson(convert, listType);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                              for (CustomFormList_Res customFormList_res:formList)
                                              {
                                                  String getfrombyid = AppDataBase.getInMemoryDatabase(jobDetailActivity.getApplicationContext()).customFormDao().getfrombyids(customFormList_res.getFrmId(),jobId);
                                                  if (getfrombyid!=null)
                                                  {
                                                      customFormList_res.setDraft(true);
                                                  }else customFormList_res.setDraft(false);
                                              }
                                        jobDetailActivity.setList(formList);
                                    }
                                }).start();
                            }
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                            jobDetailActivity.onSessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if ((updateindex + updatelimit) <= count) {
                            updateindex = updatelimit;
                            callApiGetFormlist(jobId, jtId);
                        }

                    }
                });
        }else{
            Executor executor= Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
            ArrayList<CustomFormList_Res> fromlistoffline=new ArrayList<>();
                    int getofflinedata=0;
            for (int i=0;i<jtId.size();i++)
            {
                List<CustomFormListOffline> formByJtid = AppDataBase.getInMemoryDatabase(jobDetailActivity.getApplicationContext()).customFormListOfflineDao().getFormByJtid(jtId.get(i));
                for (int j=0;j<formByJtid.size();j++)
                {
                    String formId="%frmId\":\""+formByJtid.get(j).getFrmId()+"%";
                    String jobIds="%jobId\":\""+jobId+"%";
                    getofflinedata = AppDataBase.getInMemoryDatabase(jobDetailActivity.getApplicationContext()).jobOfflineDao().getofflinedata(formId, jobIds);
                    CustomFormList_Res listRes=new CustomFormList_Res();
                    listRes.setFrmId(formByJtid.get(j).getFrmId());
                    listRes.setJtId(formByJtid.get(j).getJtId());
                    listRes.setFrmnm(formByJtid.get(j).getFrmnm());
                    listRes.setEvent(formByJtid.get(j).getEvent());
                    listRes.setTab(formByJtid.get(j).getTab());
                    listRes.setMandatory(formByJtid.get(j).getMandatory());
                    listRes.setTotalQues(formByJtid.get(j).getTotalQues());
                    if (getofflinedata==1){
                        listRes.setSubmit(true);
                    }else{
                        listRes.setSubmit(false);
                    }
                    fromlistoffline.add(listRes);
                }
            }

                    List<CustomFormListOffline> customFormListOfflines = AppDataBase.getInMemoryDatabase(jobDetailActivity.getApplicationContext()).customFormListOfflineDao().getcommonForm();
                     if (customFormListOfflines!=null&&!customFormListOfflines.isEmpty())
                     {
                         for (int i=0;i<customFormListOfflines.size();i++)
                         {
                             String formId="%frmId\":\""+customFormListOfflines.get(i).getFrmId()+"%";
                             String jobIds="%jobId\":\""+jobId+"%";
                             getofflinedata = AppDataBase.getInMemoryDatabase(jobDetailActivity.getApplicationContext()).jobOfflineDao().getofflinedata(formId, jobIds);
                             CustomFormList_Res listRes=new CustomFormList_Res();
                             listRes.setFrmId(customFormListOfflines.get(i).getFrmId());
                             listRes.setJtId(customFormListOfflines.get(i).getJtId());
                             listRes.setFrmnm(customFormListOfflines.get(i).getFrmnm());
                             listRes.setEvent(customFormListOfflines.get(i).getEvent());
                             listRes.setTab(customFormListOfflines.get(i).getTab());
                             listRes.setMandatory(customFormListOfflines.get(i).getMandatory());
                             listRes.setTotalQues(customFormListOfflines.get(i).getTotalQues());
                             if (getofflinedata==1){
                                 listRes.setSubmit(true);
                             }else{
                                 listRes.setSubmit(false);
                             }
                             fromlistoffline.add(listRes);
                         }
                     }
                    jobDetailActivity.setList(fromlistoffline);
                }
            });
        }
    }
}
