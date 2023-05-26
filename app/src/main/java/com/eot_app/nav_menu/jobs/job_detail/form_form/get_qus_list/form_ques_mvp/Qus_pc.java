package com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.form_ques_mvp;

import android.content.Context;
import android.util.Log;

import com.eot_app.activitylog.ActivityLogController;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.Fromdb.CustomFormQue;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.Fromdb.CustomFormSubmited;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.ans_model.AnsModel_Offline;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.ans_model.Ans_Req;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.qus_model.QuesGetModel;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.qus_model.QuesRspncModel;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppCenterLogs;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.db.OfflineDataController;
import com.eot_app.utility.language_support.LanguageController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hypertrack.hyperlog.HyperLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ubuntu on 18/9/18.
 */

public class Qus_pc implements Que_pi {
    Que_View queAns_view;
    String ansId;

    public Qus_pc(Que_View queAns_view) {
        this.queAns_view = queAns_view;
    }

    /***get Question's**/
    @Override
    public void getQuestions(QuesGetModel quesGetModel) {
        if (AppUtility.isInternetConnected()) {
            ActivityLogController.saveActivity(
                    ActivityLogController.JOB_MODULE,
                    ActivityLogController.JOB_GET_QUESTON,
                    ActivityLogController.JOB_MODULE
            );
            ansId = quesGetModel.getAnsId();
            AppUtility.progressBarShow((Context) queAns_view);
            ApiClient.getservices().eotServiceCall(Service_apis.getQuestionsByParentId, AppUtility.getApiHeaders(), AppUtility.jsonToStingConvrt(quesGetModel))

                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getQuestionsByParentId","","Que_pc",String.valueOf(jsonObject.get("success").getAsBoolean()));
                            HyperLog.i("getQuestionsByParentId: ", "jsonobject ::"+jsonObject.toString());
                            Log.e("Responce", jsonObject.toString());
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<QuesRspncModel>>() {
                                }.getType();
                                Executor executors= Executors.newSingleThreadExecutor();
                                executors.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        String getfrombyids = AppDataBase.getInMemoryDatabase(((Context) queAns_view).getApplicationContext()).customFormQueDao().getfrombyids(quesGetModel.getFrmId(), quesGetModel.getAnsId());
                                        if (getfrombyids==null||getfrombyids.isEmpty())
                                        {
                                            AppDataBase.getInMemoryDatabase(((Context) queAns_view).getApplicationContext()).customFormQueDao().insert(new CustomFormQue(quesGetModel.getFrmId(),convert,quesGetModel.getAnsId()));
                                        }else{
                                            AppDataBase.getInMemoryDatabase(((Context) queAns_view).getApplicationContext()).customFormQueDao().updatefrom(convert,quesGetModel.getFrmId(),quesGetModel.getAnsId());
                                        }
                                    }
                                });
                               /* String getfrombyids = AppDataBase.getInMemoryDatabase(((Context) queAns_view).getApplicationContext()).customFormQueDao().getfrombyids(quesGetModel.getFrmId(), quesGetModel.getAnsId());
                                if (getfrombyids==null||getfrombyids.isEmpty())
                                {
                                    AppDataBase.getInMemoryDatabase(((Context) queAns_view).getApplicationContext()).customFormQueDao().insert(new CustomFormQue(quesGetModel.getFrmId(),convert,quesGetModel.getAnsId()));
                                }else{
                                    AppDataBase.getInMemoryDatabase(((Context) queAns_view).getApplicationContext()).customFormQueDao().updatefrom(convert,quesGetModel.getFrmId(),quesGetModel.getAnsId());
                                }*/
                                List<QuesRspncModel> respnc = new Gson().fromJson(convert, listType);
                                if (ansId.equals("-1")) {
                                    queAns_view.questionlist(respnc);// update list by initial encounter id
                                } else {
                                    queAns_view.addfragmentDynamically(respnc,ansId);// update list by previous encounter id
                                }
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                queAns_view.onSessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Error", e.getMessage());
                            HyperLog.i("getQuestionsByParentId: ", "Error ::"+e.getMessage());
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getQuestionsByParentId",e.getMessage(),"Que_pc","");
                            AppUtility.progressBarDissMiss();
                        }

                        @Override
                        public void onComplete() {
                            AppUtility.progressBarDissMiss();
                        }
                    });
            } else {
              String getfrombyids = AppDataBase.getInMemoryDatabase(((Context) queAns_view).getApplicationContext()).customFormQueDao().getfrombyids(quesGetModel.getFrmId(), quesGetModel.getAnsId());
             if (getfrombyids!=null&&!getfrombyids.isEmpty())
             {
                Type listType = new TypeToken<List<QuesRspncModel>>() {
                }.getType();
                List<QuesRspncModel> respnc = new Gson().fromJson(getfrombyids, listType);
                for (int i=0;i<respnc.size();i++)
                {
                    if (respnc.get(i).getAns()!=null)
                    {
                        respnc.get(i).setAns(new ArrayList<>());
                    }
                }
                if (quesGetModel.getAnsId().equals("-1")) {
                    queAns_view.questionlist(respnc);// update list by initial encounter id
                } else {
                    queAns_view.addfragmentDynamically(respnc,quesGetModel.getAnsId());// update list by previous encounter id
                }
               }else {
                if (quesGetModel.getAnsId().equals("-1")) {
                    queAns_view.showOfflineAlert(LanguageController.getInstance().getMobileMsgByKey(AppConstant.offline_feature_alert));
                }else{
                    queAns_view.showofflineAlertchild();
                }
            }
        }
    }


    @Override
    public void addAnswerWithAttachments(Ans_Req ans_req, List<MultipartBody.Part> signAns, List<MultipartBody.Part> docAns
            , ArrayList<String> signQueIdArrays, ArrayList<String> docQueIdArrays,List<String> dosanspath,List<String> signanspath) {
        String str1 = new Gson().toJson(ans_req.getAnswer());

        RequestBody usrId = RequestBody.create( ans_req.getUsrId(),MultipartBody.FORM);
        RequestBody answer = RequestBody.create( str1,MultipartBody.FORM);
        RequestBody frmId = RequestBody.create( ans_req.getFrmId(),MultipartBody.FORM);
        RequestBody jobId = RequestBody.create( ans_req.getJobId(),MultipartBody.FORM);
        RequestBody isdelete = RequestBody.create( ans_req.getIsdelete(),MultipartBody.FORM);
        RequestBody type = RequestBody.create( ans_req.getType(),MultipartBody.FORM);
        String signIdArrayStr = new Gson().toJson(signQueIdArrays);
        RequestBody signQueIdArray = RequestBody.create( signIdArrayStr,MultipartBody.FORM);
        String docIdArrayStr = new Gson().toJson(docQueIdArrays);
        RequestBody docQueIdArray = RequestBody.create( docIdArrayStr,MultipartBody.FORM);

        //   RequestBody signQueIdArrayStr = RequestBody.create( new Gson().toJson(signQueIdArray));
        // RequestBody docQueIdArrayStr = RequestBody.create( new Gson().toJson(docQueIdArray));

        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(((Context) queAns_view));
            ApiClient.getservices().submitCustomFormAns(AppUtility.getApiHeaders(),
                    signAns, docAns, signQueIdArray, docQueIdArray
                    , answer, usrId, frmId, jobId, isdelete, type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","addAnswerWithAttachment","","Que_pc",String.valueOf(jsonObject.get("success").getAsBoolean()));
                            HyperLog.i("getQuestionsByParentId: ", "jsonobject ::"+jsonObject.toString());
                            Log.e("Responce", jsonObject.toString());
                            if (jsonObject.get("success").getAsBoolean()) {
                                queAns_view.onSubmitSuccess(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                queAns_view.onSessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            } else {
                                queAns_view.onSubmitSuccess(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                            // if from sumbited to server then 1 is insert to data base
                            AppDataBase.getInMemoryDatabase(((Context) queAns_view).getApplicationContext()).customFormSubmitedDao().insert(new CustomFormSubmited(ans_req.getFrmId(),ans_req.getJobId(),"1", App_preference.getSharedprefInstance().getLoginRes().getUsrId()));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG : error----", e.getMessage());
                            HyperLog.i("getQuestionsByParentId: ", "Error ::"+e.getMessage());
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","addAnswerWithAttachment",e.getMessage(),"Que_pc","");
                            AppUtility.progressBarDissMiss();
                            queAns_view.finishMuAvtivity();
                        }

                        @Override
                        public void onComplete() {
                            AppUtility.progressBarDissMiss();
                            Log.e("TAG onComplete------", "onComplete");
                        }
                    });
        } else {
            AnsModel_Offline ansModel_offline=new AnsModel_Offline(ans_req,dosanspath,signanspath,signQueIdArrays,docQueIdArrays);
            String request = new Gson().toJson(ansModel_offline);
            OfflineDataController.getInstance().addInOfflineDB("addAnswerWithAttachment",request,AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
            queAns_view.onSubmitSuccessOffline();
        }
    }

}
