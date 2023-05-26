package com.eot_app.nav_menu.jobs.job_detail.job_detail_activity_presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.eot_app.activitylog.ActivityLogController;
import com.eot_app.nav_menu.jobs.job_detail.addinvoiveitem2pkg.model.InvoiceItemDataModel;
import com.eot_app.nav_menu.jobs.job_detail.invoice.inventry_pkg.Inventry_ReQ_Model;
import com.eot_app.nav_menu.jobs.job_detail.invoice.inventry_pkg.Inventry_ReS_Model;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_db.model_pkg.ItembyJobModel;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_email_pkg.get_email_temp_model.InvoiceEmaliTemplate;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hypertrack.hyperlog.HyperLog;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Job_Detail_Activity_pc implements Job_Detail_Activity_pi {
    private final Job_Detail_Activity_View activity_view;
    private final int updatelimit;
    private int updateindex;
    private int count;

    public Job_Detail_Activity_pc(Job_Detail_Activity_View activity_view) {
        this.activity_view = activity_view;
        this.updatelimit = AppConstant.LIMIT_HIGH;
        /****set current Activity/Fragment Context***/
    }


    /***get invoice Invetrt Item list****/
    @Override
    public void getInvoiceItemList() {
        Log.e("data--->>>", "data--->>>");
        if (AppUtility.isInternetConnected()) {
            HyperLog.i("", "Job_Detail_Activity_pc: " + "getInvoiceItemList(M) start");
            Inventry_ReQ_Model inventry_model = new
                    Inventry_ReQ_Model(Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getCompId()),
                    "",
                    updatelimit,
                    updateindex, App_preference.getSharedprefInstance().getInventryItemSyncTime());//


            String data = new Gson().toJson(inventry_model);

            ApiClient.getservices().eotServiceCall(Service_apis.getItemList, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                try {
                                    count = jsonObject.get("count").getAsInt();
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                    count = 0;
                                }
                                try {
                                    if (count < 2000) {
                                        String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                        Type listType = new com.google.gson.reflect.TypeToken<List<Inventry_ReS_Model>>() {
                                        }.getType();
                                        List<Inventry_ReS_Model> inventryitemlist = new Gson().fromJson(convert, listType);
                                        if (inventryitemlist.size() > 0) {
                                            addInvoiceItemInDB(inventryitemlist);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                activity_view.onSessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG", e.getMessage());
                            HyperLog.i("", "Job_Detail_Activity_pc: " + "getInvoiceItemList(M) " + e.getMessage());
                            EotApp.getAppinstance().showToastmsg(e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            if ((updateindex + updatelimit) <= count) {
                                updateindex += updatelimit;
                                getInvoiceItemList();
                            }
                        }
                    });

            HyperLog.i("", "Job_Detail_Activity_pc: " + "getInvoiceItemList(M) completed");
        }
    }

    @Override
    public void printJobCard(String jobId, String tempId, String techId) {
        final Map<String, String> jsonMap = new HashMap<>();

        jsonMap.put("jobId", jobId);

        if (tempId != null && !tempId.isEmpty()) {
            jsonMap.put("tempId", tempId);
        }

        if (techId != null && !techId.isEmpty()) {
            jsonMap.put("techId", techId);
        } else {
            jsonMap.put("techId", App_preference.getSharedprefInstance().getLoginRes().getUsrId());
        }
        JsonObject jsonObject = AppUtility.getJsonObject(new Gson().toJson(jsonMap));

        if (AppUtility.isInternetConnected()) {

            AppUtility.progressBarShow((Context) activity_view);

            ApiClient.getservices().eotServiceCall(Service_apis.generateJobCardPDF, AppUtility.getApiHeaders(), jsonObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            AppUtility.progressBarDissMiss();
                            if (jsonObject.get("success").getAsBoolean()) {
                                activity_view.onGetPdfPath(jsonObject.getAsJsonObject("data").get("path").getAsString());
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                activity_view.onSessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG", e.getMessage());
                            AppUtility.progressBarDissMiss();
                            EotApp.getAppinstance().showToastmsg(e.getMessage());
                        }

                        @Override
                        public void onComplete() {
//                            AppUtility.progressBarDissMiss();
                        }
                    });
        } else {
            networkError();
        }
    }


    /***Update Inventry Item's in Local Db**/
    private void addInvoiceItemInDB(List<Inventry_ReS_Model> inventryitemlist) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).invoiceItemDao().insertInvebtryItems(inventryitemlist);
    }

    /****Featch update Job/Invoice Item From Server***/
    @Override
    public void getItemFromServer(final String jobId) {
        if (AppUtility.isInternetConnected()) {
            HyperLog.i("", "Job_Detail_Activity_pc: " + "getItemFromServer(M) start");
            ItembyJobModel model = new ItembyJobModel(jobId);
            String data = new Gson().toJson(model);


            ApiClient.getservices().eotServiceCall(Service_apis.getItemFromJob, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
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
                                HyperLog.i("", "Job_Detail_Activity_pc: " + convert);
                                Type listType = new TypeToken<List<InvoiceItemDataModel>>() {
                                }.getType();
                                List<InvoiceItemDataModel> data = new Gson().fromJson(convert, listType);
                                if (data.size() > 0) {
                                    addgetItemFromJobToDB(data, jobId);
                                }
                                activity_view.moreInvoiceOption(data);
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                activity_view.onSessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG", e.getMessage());
                            HyperLog.i("", "Job_Detail_Activity_pc: " + e.toString());
                            EotApp.getAppinstance().showToastmsg(e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            //      apiCallCallBack.getApiCallComplete();
                        }
                    });


            HyperLog.i("", "Job_Detail_Activity_pc: " + "getItemFromServer(M) Complete");
        } else {
            networkError();
        }
    }

    private void addgetItemFromJobToDB(List<InvoiceItemDataModel> data, String jobId) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobitems(jobId, data);
    }

    private void networkError() {
        AppUtility.alertDialog(((Context) activity_view), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return null;
            }
        });
    }

    @Override
    public void uploadCustomerSign(final String jobId, File file1) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow((Context) activity_view);
            ActivityLogController.saveActivity(ActivityLogController.JOB_MODULE, ActivityLogController.JOB_UPLOAD_DOC, ActivityLogController.JOB_MODULE);
            String mimeType = "";
            MultipartBody.Part body = null;
            if (file1 != null) {
                mimeType = URLConnection.guessContentTypeFromName(file1.getName());
                if (mimeType == null) {
                    mimeType = file1.getAbsolutePath();
                }
                RequestBody requestFile = RequestBody.create(file1, MediaType.parse(mimeType));
                body = MultipartBody.Part.createFormData("signImg", file1.getAbsolutePath(), requestFile);
            }
            RequestBody requestBody_jobId = RequestBody.create(jobId, MultipartBody.FORM);

            ApiClient.getservices().uploadCustomerSignature(AppUtility.getApiHeaders(),
                    requestBody_jobId, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String signaturePath = jsonObject.get("data").getAsString();
                                if (!TextUtils.isEmpty(signaturePath)) {
                                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance())
                                            .jobModel().updateSignaturePath(signaturePath, jobId);
                                    activity_view.onSignatureUpload(signaturePath, LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));

                                }

                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                activity_view.onSessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            } else {
                                AppUtility.alertDialog(((Context) activity_view), "", LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
                                    @Override
                                    public Boolean call() throws Exception {
                                        return null;
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            HyperLog.i("", e.getMessage());
                            EotApp.getAppinstance().showToastmsg(e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            AppUtility.progressBarDissMiss();
                        }
                    });
        } else {
            AppUtility.progressBarDissMiss();
            networkError();
        }
    }

    @Override
    public void getJobCardetemplateList() {
        if (AppUtility.isInternetConnected()) {
            ActivityLogController.saveActivity(
                    ActivityLogController.JOB_MODULE,
                    ActivityLogController.JOB_GET_INVOICE_TEMP,
                    ActivityLogController.JOB_MODULE
            );
            AppUtility.progressBarShow((Context) activity_view);

            ApiClient.getservices().eotServiceCall2(Service_apis.getJobCardTemplates, AppUtility.getApiHeaders())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            try {
                                if (jsonObject.get("success").getAsBoolean()) {
                                    ArrayList<InvoiceEmaliTemplate> templateList = new ArrayList<>();
                                    InvoiceEmaliTemplate invoiceEmaliTemplate = new InvoiceEmaliTemplate();
                                    for (int i = 0; i < jsonObject.get("data").getAsJsonArray().size(); i++) {

                                        // condition added for custom temp
                                        if (jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("isCustomTemp") != null
                                                && jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("isCustomTemp").getAsString().equals("1")
                                                &&
                                                jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("cltTempNm") != null
                                                && !jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("cltTempNm").getAsString().equals("")
                                        ) {

                                            invoiceEmaliTemplate = new InvoiceEmaliTemplate(jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("jcTempId").getAsString(),
                                                    jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("cltTempNm").getAsString()
                                                    , jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("defaultTemp").getAsString(), false);
                                            Log.e("TemplateData::", "If::" + new Gson().toJson(invoiceEmaliTemplate));
                                        } else if (jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("cltTempNm") != null
                                                && !jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("cltTempNm").getAsString().equals("")
                                        ) {

                                            invoiceEmaliTemplate = new InvoiceEmaliTemplate(jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("jcTempId").getAsString(),
                                                    jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("cltTempNm").getAsString()
                                                    , jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("defaultTemp").getAsString(), false);
                                            Log.e("TemplateData::", "If::" + new Gson().toJson(invoiceEmaliTemplate));
                                        } else {
                                            boolean isTechSign = false;
                                            try {
                                                JsonArray jsonArray = jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tempJson").getAsJsonObject().get("signDetail").getAsJsonArray();
                                                for (int m = 0; m < jsonArray.size(); m++) {
                                                    if (jsonArray.get(m).getAsJsonObject().get("inputValue").getAsString().equalsIgnoreCase("Technician Signature")
                                                            && jsonArray.get(m).getAsJsonObject().get("isActive").getAsBoolean()) {
                                                        isTechSign = true;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            invoiceEmaliTemplate = new InvoiceEmaliTemplate(jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("jcTempId").getAsString(),
                                                    jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tempJson").getAsJsonObject().get("clientDetails").getAsJsonArray().get(0).getAsJsonObject().get("inputValue").getAsString()
                                                    , jsonObject.get("data").getAsJsonArray().get(i).getAsJsonObject().get("defaultTemp").getAsString(), isTechSign);
                                            Log.e("TemplateData::", "Else::" + new Gson().toJson(invoiceEmaliTemplate));

                                        }

                                        templateList.add(invoiceEmaliTemplate);

                                        Log.e("TemplateData::", "templateList:B" + new Gson().toJson(templateList));

                                    }
                                    Log.e("TemplateData::", "templateListCheck:" + new Gson().toJson(templateList));

                                    if (!templateList.isEmpty()) {
                                        Log.e("TemplateData::", "templateList:" + new Gson().toJson(templateList));
                                        activity_view.setInvoiceTmpList(templateList);
                                    }

                                } else {
                                    if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                        activity_view.onSessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                                    } else {
                                        AppUtility.alertDialog(((Context) activity_view), "", LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
                                            @Override
                                            public Boolean call() throws Exception {
                                                return null;
                                            }
                                        });
                                    }
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                            AppUtility.progressBarDissMiss();
                            Log.e("", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
//                            AppUtility.progressBarDissMiss();
                        }
                    });
        }
    }


}
