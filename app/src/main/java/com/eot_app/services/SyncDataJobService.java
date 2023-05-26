/*
 * Created by Shivani Vani
 */
package com.eot_app.services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import com.eot_app.activitylog.ActivityLogController;
import com.eot_app.nav_menu.admin_fw_chat_pkg.sonam_user_user_chat_pkg.usertouser_model.UserChatListModelReq;
import com.eot_app.nav_menu.admin_fw_chat_pkg.sonam_user_user_chat_pkg.usertouser_model.UserChatModel;
import com.eot_app.nav_menu.appointment.dbappointment.Appointment;
import com.eot_app.nav_menu.appointment.list.model.AppointmentListReq;
import com.eot_app.nav_menu.audit.audit_list.audit_mvp.model.AuditListRequestModel;
import com.eot_app.nav_menu.audit.audit_list.audit_mvp.model.AuditList_Res;
import com.eot_app.nav_menu.client.client_db.Client;
import com.eot_app.nav_menu.client.client_db.Client_Request_model;
import com.eot_app.nav_menu.client.clientlist.client_detail.contact.client_dao.ContactData;
import com.eot_app.nav_menu.client.clientlist.client_detail.site.sitelist.Site_model;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_db.JobListRequestModel;
import com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_model.CustomFormListOffline;
import com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_model.CustomFormList_Res;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.JobStatusModelNew;
import com.eot_app.nav_menu.jobs.job_detail.invoice.inventry_pkg.Inventry_ReQ_Model;
import com.eot_app.nav_menu.jobs.job_detail.invoice.inventry_pkg.Inventry_ReS_Model;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_db.location_tax_dao.TaxReqModel;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_db.location_tax_dao.TaxesLocation;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_detail_pkg.inv_detail_model.Tax;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_model_pkg.TaxList_ReQ_Model;
import com.eot_app.nav_menu.jobs.job_detail.job_status_pkg.JobStatus_Controller;
import com.eot_app.time_shift_pkg.ShiftTimeReSModel;
import com.eot_app.time_shift_pkg.ShiftTimeReqModel;
import com.eot_app.utility.AppCenterLogs;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.db.OfflineDataController;
import com.eot_app.utility.settings.contractdb.ContractReq;
import com.eot_app.utility.settings.contractdb.ContractRes;
import com.eot_app.utility.settings.equipmentdb.Equipment;
import com.eot_app.utility.settings.equipmentdb.EquipmentListReq;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hypertrack.hyperlog.HyperLog;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.RequiresApi;
import org.jetbrains.annotations.NotNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * * created by shivani vani in FEB 2022**
 * */

public class SyncDataJobService extends JobService {

    private final int updateLimit = AppConstant.LIMIT_HIGH;
    private int count;
    private int updateIndex;
    private String startJobSyncTime="";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(getClass().getName(), "onStartJob: ");
        Log.v("MainSync","Sync completed "+" --" +"onStartJob" );

        doBackgroundWork(params);
        return true;
    }

    @SuppressLint("MissingPermission")
    private void doBackgroundWork(final JobParameters params) {
        new Thread(() -> {
            OfflineDbSync();
            jobFinished(params, false);
        }).start();
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(getClass().getName(), "onStopJob: ");
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startSyncFromStatus() {
        int status_no = App_preference.getSharedprefInstance().getFirstSyncState();
        switch (status_no) {
            case 0:
                getJobStatusList();// get job status list
                break;
            case 1:
                startJobSyncTime=AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT);
                App_preference.getSharedprefInstance().setJobStartSyncTime(startJobSyncTime);
                Log.v("MainSync","startJobSyncTime"+" --" +startJobSyncTime);
                getJobSyncService();//get job list
                break;
            case 2:
                getClientSyncService();//sync client list
                break;
            case 3:
                getContactSyncService();//sync contact list
                break;
            case 4:
                getSiteSyncService();//get Site list
                break;
            case 5:
                getChatgrpUserSyncService();//get chat user list
                break;
            case 6:
                getAppointmentSyncService();//get appointment  list
                break;
            case 7:
                getInvoiceItemList();//get inventory item's
                break;
            case 8:
                getInvoiceTaxesList();//get taxes for invoice item's
                break;
            case 9:
                getAuditList();
                break;
            case 10:
                getContractList();
                break;
            case 11:
                getEquipmentList();
                break;
            case 12:
                getTaxLocations();
                break;
            case 13:
                getJobTimeShiftList();
                break;
            case 14:
                getCustomForm();
                break;
            case 15:
                goHomePage();
                App_preference.getSharedprefInstance().setFirstSyncState(0);
                stopSelf();
                break;
        }
    }

    private void goHomePage() {
        Intent local = new Intent();
        local.setAction("updateUi");
        this.sendBroadcast(local);
        Log.v("MainSync","Sync completed "+" --" +"All Sync Done");
    }

    private JsonObject getJsonObject(String params) {
        JsonParser parser = new JsonParser();
        return parser.parse(params).getAsJsonObject();
    }

    private void getJobSyncService() {
        if (AppUtility.isInternetConnected()) {
            ActivityLogController.saveActivity(
                    ActivityLogController.LOGIN_MODULE,
                    ActivityLogController.LOGIN_JOB_SYNC,
                    ActivityLogController.LOGIN_MODULE
            );
            JobListRequestModel jobListRequestModel = new JobListRequestModel(Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getUsrId()),
                    updateLimit, updateIndex, App_preference.getSharedprefInstance().getJobSyncTime());
            String data = new Gson().toJson(jobListRequestModel);
            ApiClient.getservices().eotServiceCall(Service_apis.getUserJobList, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {
                        }
                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getUserJobList","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                            Log.e("responce:", jsonObject.toString());
                            if (jsonObject.get("success").getAsBoolean()) {
                                count = jsonObject.get("count").getAsInt();
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<Job>>() {
                                }.getType();
                                List<Job> data = new Gson().fromJson(convert, listType);
                                addJobInToDB(data);
                            }
                        }
                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("Network Error :", e.toString());
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getUserJobList",e.getMessage(),"SyncDataJobService","");
                            errorMsg(e.toString());
                        }
                        @Override
                        public void onComplete() {
                            if ((updateIndex + updateLimit) <= count) {
                                updateIndex += updateLimit;
                                getJobSyncService();
                            } else {
                                if (count != 0) {
                                    Log.v("MainSync","startJobSyncTimeCI"+" --" +startJobSyncTime);
                                    if(!startJobSyncTime.isEmpty()){
                                        App_preference.getSharedprefInstance().setJobSyncTime(startJobSyncTime);
                                    }
                                    else{
                                        App_preference.getSharedprefInstance().setJobSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                    }
                                    App_preference.getSharedprefInstance().setJobStartSyncTime("");
                                    Log.v("MainSync","startJobSyncTimeC"+" --" +App_preference.getSharedprefInstance().getJobSyncTime());
                                }
                                updateIndex = 0;
                                count = 0;
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobByIsDelete();

//                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobStatusNot(
//                                        "1","2","3","4","5","6","7","8","9","10","11","12"
//                                );
                                App_preference.getSharedprefInstance().setFirstSyncState(2);
                                startSyncFromStatus();
                                Log.v("MainSync","startJobSyncTimeCR"+" --" +App_preference.getSharedprefInstance().getJobSyncTime());
                                Log.v("MainSync","Sync completed "+" --" +"JobSync Done");
                            }
                        }
                    });
        }
    }

    private void addJobInToDB(List<Job> data) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().inserJob(data);
    }

    private void getAppointmentSyncService() {
        int userId = Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getUsrId());

        AppointmentListReq model = new AppointmentListReq(
                userId,
                updateLimit, updateIndex
                , App_preference.getSharedprefInstance().getAppointmentSyncTime());
        String data = new Gson().toJson(model);
        Log.d("Data error", "error" + data);
        HyperLog.i("TAG", "Data error" + data);

        ApiClient.getservices().eotServiceCall(Service_apis.getAppointmentUserList, AppUtility.getApiHeaders(), getJsonObject(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull JsonObject jsonObject) {
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getAppointmentUserList","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        if (jsonObject.get("success").getAsBoolean()) {
                            count = jsonObject.get("count").getAsInt();
                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                            Type listType = new TypeToken<List<Appointment>>() {
                            }.getType();
                            List<Appointment> data = new Gson().fromJson(convert, listType);
                            addAppointmentToDB(data);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.d("error", e.getMessage());
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getAppointmentUserList",e.getMessage(),"SyncDataJobService","");
                        HyperLog.i("TAG", "Data error" + e.getMessage());
                        Log.e("Network Error :", e.toString());
                        Log.e("Network Error :", e.getLocalizedMessage());
                        errorMsg(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        if ((updateIndex + updateLimit) <= count) {
                            updateIndex += updateLimit;
                            getAppointmentSyncService();
                        } else {
                            if (count != 0) {
                                App_preference.getSharedprefInstance().setAppointmentSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                            }
                            updateIndex = 0;
                            count = 0;
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().deleteAppointmentByIsDelete();
                            App_preference.getSharedprefInstance().setFirstSyncState(7);
                            startSyncFromStatus();
                            Log.v("MainSync","Sync completed "+" --" +"appointment Sync Done");
                        }
                    }
                });
    }

    private void addAppointmentToDB(List<Appointment> data) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().insertAppointments(data);
    }

    /***get invoice Invetrt Item list****/
    private void getInvoiceItemList() {
        Log.e("data--->>>", "data--->>>");
        if (AppUtility.isInternetConnected()) {
            Inventry_ReQ_Model inventry_model = new Inventry_ReQ_Model(Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getCompId()),
                    "",

                    updateLimit, updateIndex, App_preference.getSharedprefInstance().getInventryItemSyncTime());//

            String data = new Gson().toJson(inventry_model);

            ApiClient.getservices().eotServiceCall(Service_apis.getItemList, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getItemList","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                            if (jsonObject.get("success").getAsBoolean()) {
                                count = jsonObject.get("count").getAsInt();
                                Log.e("Count", "" + count);
                                if (count < 2000) {
                                    String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                    Type listType = new TypeToken<List<Inventry_ReS_Model>>() {
                                    }.getType();
                                    List<Inventry_ReS_Model> inventryitemlist = new Gson().fromJson(convert, listType);
                                    addInvoiceItemInDB(inventryitemlist);
                                    Log.e("Count", "" + count);
                                }
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("Network Error :", e.toString());
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getItemList",e.getMessage(),"SyncDataJobService","");
                            Log.e("TAG : error----", e.getMessage());
                            errorMsg(e.toString());
                        }

                        @Override
                        public void onComplete() {
                            Log.e("TAG onComplete------", "onComplete");
//                            if (count < 2000) {
                            if (count < 2000 && (updateIndex + updateLimit) <= count) {
                                updateIndex += updateLimit;
                                getInvoiceItemList();
                            } else {
                                if (count != 0) {
                                    App_preference.getSharedprefInstance()
                                            .setInventryItemSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                }
                                updateIndex = 0;
                                count = 0;
                                App_preference.getSharedprefInstance().setFirstSyncState(8);
                                startSyncFromStatus();
                                Log.v("MainSync","Sync completed "+" --" +"Invoice Sync Done");
                            }
                        }
                    });
        }
    }


    private void addInvoiceTaxexListInDB(List<Tax> temptaxList) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).invoiceTaxDao().insertInvoiceTaxes(temptaxList);
    }

    private void addInvoiceItemInDB(List<Inventry_ReS_Model> inventryitemlist) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).invoiceItemDao().insertInvebtryItems(inventryitemlist);
    }

    /***get admin one-to-one chat group chat user list***/
    private void getChatgrpUserSyncService() {
        if (AppUtility.isInternetConnected()) {
            ActivityLogController.saveActivity(
                    ActivityLogController.LOGIN_MODULE,
                    ActivityLogController.LOGIN_SYNC_USER_LIST_CHAT,
                    ActivityLogController.LOGIN_MODULE
            );
            UserChatListModelReq model = new UserChatListModelReq(Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getCompId()),
                    updateLimit, updateIndex
                    , App_preference.getSharedprefInstance().getUsersSyncTime());
            String data = new Gson().toJson(model);
            ApiClient.getservices().eotServiceCall(Service_apis.groupUserListForChat,
                    AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","groupUserListForChat","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                            Log.e("", "");
                            if (jsonObject.get("success").getAsBoolean()) {
                                count = jsonObject.get("count").getAsInt();
                                String jsonString = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type typeList = new TypeToken<List<UserChatModel>>() {
                                }.getType();
                                List<UserChatModel> chatList = new Gson().fromJson(jsonString, typeList);
                                addChatUserListDataToDB(chatList);
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("Network Error :", e.toString());
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","groupUserListForChat",e.getMessage(),"SyncDataJobService","");
                            errorMsg(e.toString());
                        }

                        @Override
                        public void onComplete() {
                            if ((updateIndex + updateLimit) <= count) {
                                updateIndex += updateLimit;
                                getChatgrpUserSyncService();
                            } else {
                                if (count != 0) {
                                    App_preference.getSharedprefInstance().setUsersSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                }
                                updateIndex = 0;
                                count = 0;
                                App_preference.getSharedprefInstance().setFirstSyncState(6);
                                startSyncFromStatus();
                                Log.v("MainSync","Sync completed "+" --" +"Chat Grup Sync Done");

                            }
                        }
                    });
        }
    }

    private void getSiteSyncService() {
        if (AppUtility.isInternetConnected()) {
            ActivityLogController.saveActivity(
                    ActivityLogController.LOGIN_MODULE,
                    ActivityLogController.LOGIN_CLIENT_SITE_SYN,
                    ActivityLogController.LOGIN_MODULE
            );
            Client_Request_model client_request_model = new Client_Request_model(Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getCompId()),
                    updateLimit, updateIndex, App_preference.getSharedprefInstance().getSiteSyncTime());
//            Client_Request_model client_request_model = new Client_Request_model(Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getCompId()),
//                    5000, 0, App_preference.getSharedprefInstance().getSiteSyncTime());

            String data = new Gson().toJson(client_request_model);

            ApiClient.getservices().eotServiceCall(Service_apis.getClientSiteSink, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getClientSiteSink","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                            Log.e("SiteDataList", jsonObject.toString());
                            if (jsonObject.get("success").getAsBoolean()) {
                                count = jsonObject.get("count").getAsInt();
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<Site_model>>() {
                                }.getType();
                                List<Site_model> data = new Gson().fromJson(convert, listType);
                                addSiteDataToDB(data);
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("Network Error :", e.toString());
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getClientSiteSink",e.getMessage(),"SyncDataJobService","");
                            errorMsg(e.toString());
                        }

                        @Override
                        public void onComplete() {
                            if ((updateIndex + updateLimit) <= count) {
                                updateIndex += updateLimit;
                                getSiteSyncService();
                            } else {
                                if (count != 0) {
                                    App_preference.getSharedprefInstance().setSiteSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                }
                                updateIndex = 0;
                                count = 0;
                                /* ***very very important sonam**/
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel().deleteSiteByIsDelete();


                                App_preference.getSharedprefInstance().setFirstSyncState(5);
                                startSyncFromStatus();
                                Log.v("MainSync","Sync completed "+" --" +"site Sync Done");

                            }
                        }
                    });
        }
    }

    private void addSiteDataToDB(List<Site_model> data) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel().insertSiteList(data);
    }

    private void addChatUserListDataToDB(List<UserChatModel> data) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).userChatModel().insertChatUserList(data);
    }

    private void getContactSyncService() {
        if (AppUtility.isInternetConnected()) {
            ActivityLogController.saveActivity(
                    ActivityLogController.LOGIN_MODULE,
                    ActivityLogController.LOGIN_CLIENT_CONTACT_SYN,
                    ActivityLogController.LOGIN_MODULE
            );
            Client_Request_model client_request_model = new Client_Request_model(Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getCompId()),
                    updateLimit, updateIndex, App_preference.getSharedprefInstance().getContactSyncTime());

            String data = new Gson().toJson(client_request_model);

            ApiClient.getservices().eotServiceCall(Service_apis.getClientContactSink, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getClientContactSink","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                            if (jsonObject.get("success").getAsBoolean()) {
                                count = jsonObject.get("count").getAsInt();
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<ContactData>>() {
                                }.getType();
                                List<ContactData> data = new Gson().fromJson(convert, listType);
                                addContactDataToDB(data);
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("Network Error :", e.toString());
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getClientContactSink",e.getMessage(),"SyncDataJobService","");
                            errorMsg(e.toString());
                        }

                        @Override
                        public void onComplete() {
                            if ((updateIndex + updateLimit) <= count) {
                                updateIndex += updateLimit;
                                getContactSyncService();
                            } else {
                                if (count != 0) {
                                    App_preference.getSharedprefInstance().setContactSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                }
                                updateIndex = 0;
                                count = 0;
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contactModel().deleteContactByIsDelete();
                                App_preference.getSharedprefInstance().setFirstSyncState(4);
                                startSyncFromStatus();
                                Log.v("MainSync","Sync completed "+" --" +"Contact Sync Done");

                            }
                        }
                    });
        }
    }

    private void addContactDataToDB(List<ContactData> data) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contactModel().insertContactList(data);
    }

    private void getClientSyncService() {
        if (AppUtility.isInternetConnected()) {
            ActivityLogController.saveActivity(
                    ActivityLogController.LOGIN_MODULE,
                    ActivityLogController.LOGIN_CLIENT_SYNC,
                    ActivityLogController.LOGIN_MODULE
            );
            Client_Request_model client_request_model = new Client_Request_model(
                    Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getCompId()),
                    updateLimit, updateIndex, App_preference.getSharedprefInstance().getClientSyncTime());

            String data = new Gson().toJson(client_request_model);

            ApiClient.getservices().eotServiceCall(Service_apis.getClientSink, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getClientSink","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                            if (jsonObject.get("success").getAsBoolean()) {
                                count = jsonObject.get("count").getAsInt();
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<Client>>() {
                                }.getType();
                                List<Client> data = new Gson().fromJson(convert, listType);
                                addClientDataToDB(data);
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e("Network Error :", e.toString());
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getClientSink",e.getMessage(),"SyncDataJobService","");
                            errorMsg(e.toString());
                        }

                        @Override
                        public void onComplete() {
                            if ((updateIndex + updateLimit) <= count) {
                                updateIndex += updateLimit;
                                getClientSyncService();
                            } else {
                                if (count != 0) {
                                    App_preference.getSharedprefInstance().setClientSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                }
                                updateIndex = 0;
                                count = 0;
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).clientModel().deleteClientByIsDelete();
                                App_preference.getSharedprefInstance().setFirstSyncState(3);
                                startSyncFromStatus();
                                Log.v("MainSync","Sync completed "+" --" +"Client Sync Done");
                            }
                        }
                    });
        }
    }

    private void addClientDataToDB(List<Client> data) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).clientModel().insertUser(data);
    }

    private void getInvoiceTaxesList() {
        TaxList_ReQ_Model model = new TaxList_ReQ_Model(App_preference.getSharedprefInstance().getLoginRes().getCompId()
                , updateLimit, updateIndex, App_preference.getSharedprefInstance().getInventryTaxesSyncTime());
        String data = new Gson().toJson(model);
        ApiClient.getservices().eotServiceCall(Service_apis.getTaxList, AppUtility.getApiHeaders(), getJsonObject(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NotNull JsonObject jsonObject) {
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getTaxList","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        if (jsonObject.get("success").getAsBoolean()) {
                            count = jsonObject.get("count").getAsInt();
                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                            Type listType = new TypeToken<List<Tax>>() {
                            }.getType();
                            List<Tax> temptaxList = new Gson().fromJson(convert, listType);
                            addInvoiceTaxexListInDB(temptaxList);
                            Log.e("data----", "");
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("Network Error :", e.toString());
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getTaxList",e.getMessage(),"SyncDataJobService","");
                        Log.e("ERROR", e.getMessage());
                        errorMsg(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e("", "");
                        if ((updateIndex + updateLimit) <= count) {
                            updateIndex += updateLimit;
                            getInvoiceTaxesList();
                        } else {
                            if (count != 0) {
                                App_preference.getSharedprefInstance().setInventryTaxesSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                            }
                            updateIndex = 0;
                            count = 0;
                            App_preference.getSharedprefInstance().setFirstSyncState(9);
                            startSyncFromStatus();
                        }
                    }
                });
    }

    private void getCustomForm()
    {
         JsonObject jsonObject=new JsonObject();
         jsonObject.addProperty("updateIndex",updateIndex);
         jsonObject.addProperty("updateLimit",updateLimit);

        String data = new Gson().toJson(jsonObject);
       ApiClient.getservices().eotServiceCall(Service_apis.getFormList,AppUtility.getApiHeaders(),AppUtility.getJsonObject(data))
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Observer<JsonObject>() {
                   @Override
                   public void onSubscribe(@NotNull Disposable d) {
                   }

                   @Override
                   public void onNext(@NotNull JsonObject jsonObject) {
                       if (jsonObject.get("success").getAsBoolean() && jsonObject.get("data").getAsJsonArray().size() > 0) {
                           if (jsonObject.get("count") != null && jsonObject.get("count").getAsInt() > 0) {
                               count = jsonObject.get("count").getAsInt();
                               String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                               Type listType = new TypeToken<List<CustomFormListOffline>>() {
                               }.getType();
                               ArrayList<CustomFormListOffline> formList = new Gson().fromJson(convert, listType);
                               Executor executors=Executors.newSingleThreadExecutor();
                               executors.execute(new Runnable() {
                                   @Override
                                   public void run() {
                                       AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).customFormListOfflineDao().delete();
                                       for (int i=0;i<formList.size();i++)
                                       {
                                           String jtId = formList.get(i).getJtId();
                                           if(jtId!=null){
                                               String[] split = jtId.split(",");
                                               if (split.length>1){
                                                   for(int j=0;j<split.length;j++) {
                                                       CustomFormListOffline customForms = formList.get(i);
                                                       CustomFormListOffline customFormListOffline = new CustomFormListOffline(customForms.getFrmId(),split[j].trim(),
                                                               customForms.getFrmnm(), customForms.getEvent(), customForms.getTab(), customForms.getMandatory(), customForms.getTotalQues());

                                                       AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).customFormListOfflineDao().insert(customFormListOffline);
                                                   }
                                               }else
                                               AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).customFormListOfflineDao().insert(formList.get(i));

                                           }
                                       }
                                   }
                               });
                           }
                       }
                   }

                   @Override
                   public void onError(@NotNull Throwable e) {
                       Log.e("Network Error :", e.toString());
                       errorMsg(e.toString());
                   }

                   @Override
                   public void onComplete() {
                       if ((updateIndex + updateLimit) <= count) {
                           updateIndex += updateLimit;
                           getCustomForm();
                       } else {
                           if (count != 0) {
                               App_preference.getSharedprefInstance().setShiftTimeSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                           }
                           updateIndex = 0;
                           count = 0;
                           App_preference.getSharedprefInstance().setFirstSyncState(15);
                           startSyncFromStatus();
                           Log.v("MainSync","Sync completed "+" --" +"job time Sync Done");
                       }
                   }
               });
    }

    private void getJobTimeShiftList() {
        ShiftTimeReqModel equipmentListReq = new ShiftTimeReqModel(
                App_preference.getSharedprefInstance().getShiftTimeSyncTime(), updateLimit, updateIndex);
        String data = new Gson().toJson(equipmentListReq);
        ApiClient.getservices().eotServiceCall(Service_apis.getShiftList, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull JsonObject jsonObject) {
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getShiftList","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        Log.e("TaxLOcationList:", jsonObject.toString());
                        if (jsonObject.get("success").getAsBoolean()) {
                            count = jsonObject.get("count").getAsInt();
                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                            Type listType = new TypeToken<List<ShiftTimeReSModel>>() {
                            }.getType();
                            List<ShiftTimeReSModel> equipmentList = new Gson().fromJson(convert, listType);
                            if (equipmentList != null)
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).shiftTimeDao().insertAllShiftTimeList(equipmentList);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("Network Error :", e.toString());
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getShiftList",e.getMessage(),"SyncDataJobService","");
                        /* *****/
                        errorMsg(e.toString());

                    }

                    @Override
                    public void onComplete() {
                        if ((updateIndex + updateLimit) <= count) {
                            updateIndex += updateLimit;
                            getJobTimeShiftList();
                        } else {
                            if (count != 0) {
                                App_preference.getSharedprefInstance().setShiftTimeSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                            }
                            updateIndex = 0;
                            count = 0;
                            App_preference.getSharedprefInstance().setFirstSyncState(14);
                            startSyncFromStatus();
                            Log.v("MainSync","Sync completed "+" --" +"job time Sync Done");

                        }
                    }
                });


    }

    // for fetching the job status json
    private void getJobStatusList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("index",updateIndex);
        jsonObject.addProperty("limit",updateLimit);
        jsonObject.addProperty("search","");
        String data = new Gson().toJson(jsonObject);
        ApiClient.getservices().eotServiceCall(Service_apis.getJobStatus,
                AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull JsonObject jsonObject) {
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getJobStatus","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        Log.e("TaxLOcationList:", jsonObject.toString());
                        if (jsonObject.get("success").getAsBoolean()) {
                            count = jsonObject.get("count").getAsInt();
                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                            Type listType = new TypeToken<List<JobStatusModelNew>>() {
                            }.getType();
                            List<JobStatusModelNew> statusList = new Gson().fromJson(convert, listType);

                            if (statusList != null){
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().insertJobstatusList(statusList);

                                for (JobStatusModelNew jobStatus: statusList) {
                                    if(jobStatus.getIsDefault().equalsIgnoreCase("0"))
                                    {
                                        new LoadImage().execute(App_preference.getSharedprefInstance().getBaseURL()+jobStatus.getUrl(),jobStatus.getStatus_no());
                                    }
                                }
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().insertJobstatusList(statusList);
                                Log.e("JobStatusListFirst::",new Gson().toJson(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().getAllStatusList()));
                            }
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("Network Error :", e.toString());
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getJobStatus",e.getMessage(),"SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        /* *****/
                        errorMsg(e.toString());

                    }

                    @Override
                    public void onComplete() {
                        if ((updateIndex + updateLimit) <= count) {
                            updateIndex += updateLimit;
                            getJobStatusList();
                        } else {
                            JobStatus_Controller.getInstance().setDynamicStatusList();
                            updateIndex = 0;
                            count = 0;
                            App_preference.getSharedprefInstance().setFirstSyncState(1);
                            startSyncFromStatus();
                            Log.v("MainSync","Sync completed "+" --" +"job time Sync Done");
                        }
                    }
                });


    }

    private void getTaxLocations() {
        TaxReqModel equipmentListReq = new TaxReqModel(
                App_preference.getSharedprefInstance().getTaxLocationSyncTime());
        String data = new Gson().toJson(equipmentListReq);
        ApiClient.getservices().eotServiceCall(Service_apis.getLocationList, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull JsonObject jsonObject) {
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getLocationList","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        Log.e("TaxLOcationList:", jsonObject.toString());
                        if (jsonObject.get("success").getAsBoolean()) {
                            count = jsonObject.get("count").getAsInt();
                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                            Type listType = new TypeToken<List<TaxesLocation>>() {
                            }.getType();
                            List<TaxesLocation> equipmentList = new Gson().fromJson(convert, listType);
                            if (equipmentList != null)
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).taxesLocationDao().insertAllLocationList(equipmentList);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("Network Error :", e.toString());
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getLocationList",e.getMessage(),"SyncDataJobService","");
                        /* *****/
                        errorMsg(e.toString());

                    }

                    @Override
                    public void onComplete() {
                        if ((updateIndex + updateLimit) <= count) {
                            updateIndex += updateLimit;
                            getTaxLocations();
                        } else {
                            if (count != 0) {
                                App_preference.getSharedprefInstance().setTaxLocationSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                            }
                            updateIndex = 0;
                            count = 0;
                            App_preference.getSharedprefInstance().setFirstSyncState(13);
                            startSyncFromStatus();
                            Log.v("MainSync","Sync completed "+" --" +"tax location Sync Done");

                        }
                    }
                });
    }

    private void getEquipmentList() {
        EquipmentListReq equipmentListReq = new EquipmentListReq(
                updateLimit, updateIndex, "", App_preference.getSharedprefInstance().getAllEquipmentSyncTime());
        String data = new Gson().toJson(equipmentListReq);
        ApiClient.getservices().eotServiceCall(Service_apis.getAllEquipments, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull JsonObject jsonObject) {
                        Log.e("getAllEquipments", "Sync");
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getAllEquipments","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        Log.d("equipmentlist", jsonObject.toString());
                        if (jsonObject.get("success").getAsBoolean()) {
                            count = jsonObject.get("count").getAsInt();
                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                            Type listType = new TypeToken<List<Equipment>>() {
                            }.getType();
                            List<Equipment> equipmentList = new Gson().fromJson(convert, listType);
                            if (equipmentList != null)
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).equipmentDao().insertEquipmentList(equipmentList);

                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("Network Error :", e.toString());
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getAllEquipments",e.getMessage(),"SyncDataJobService","");
                        /* *****/
                        errorMsg(e.toString());

                    }

                    @Override
                    public void onComplete() {
                        if ((updateIndex + updateLimit) <= count) {
                            updateIndex += updateLimit;
                            getEquipmentList();
                        } else {
                            if (count != 0) {
                                App_preference.getSharedprefInstance().setAllEquipmentSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                            }
                            updateIndex = 0;
                            count = 0;
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).equipmentDao().deleteEquipmentByIsDelete();
                            App_preference.getSharedprefInstance().setFirstSyncState(12);
                            startSyncFromStatus();
                            Log.v("MainSync","Sync completed "+" --" +"quipment Sync Done");

                        }
                    }
                });
    }

    /***get Contract List****/
    private void getContractList() {
        ContractReq contractReq = new ContractReq(
                updateLimit, updateIndex, "", App_preference.getSharedprefInstance().getContractSyncTime());
        String data = new Gson().toJson(contractReq);
        ApiClient.getservices().eotServiceCall(Service_apis.getContractList, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull JsonObject jsonObject) {
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getContractList","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        Log.d("contractList", jsonObject.toString());
                        if (jsonObject.get("success").getAsBoolean()) {
                            count = jsonObject.get("count").getAsInt();
                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                            Type listType = new TypeToken<List<ContractRes>>() {
                            }.getType();
                            List<ContractRes> contractList = new Gson().fromJson(convert, listType);
                            if (contractList != null)
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contractDao().insertContractList(contractList);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("Network Error :", e.toString());
                        /* *****/
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getContractList",e.getMessage(),"SyncDataJobService","");
                        errorMsg(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        if ((updateIndex + updateLimit) <= count) {
                            updateIndex += updateLimit;
                            getContractList();
                        } else {
                            if (count != 0) {
                                App_preference.getSharedprefInstance().setContractSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                            }
                            updateIndex = 0;
                            count = 0;
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contractDao().deleteContractByIsDelete();
                            App_preference.getSharedprefInstance().setFirstSyncState(11);
                            startSyncFromStatus();
                            Log.v("MainSync","Sync completed "+" --" +"contract Sync Done");

                        }
                    }
                });
    }

    private void getAuditList() {
        AuditListRequestModel auditListRequestModel = new AuditListRequestModel(Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getUsrId()),
                updateLimit, updateIndex, App_preference.getSharedprefInstance().getAuditSyncTime());
        String data = new Gson().toJson(auditListRequestModel);
        ApiClient.getservices().eotServiceCall(Service_apis.getAuditList, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull JsonObject jsonObject) {
                        Log.d("mahi", jsonObject.toString());
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getAuditList","","SyncDataJobService",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        if (jsonObject.get("success").getAsBoolean()) {
                            count = jsonObject.get("count").getAsInt();
                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                            Type listType = new TypeToken<List<AuditList_Res>>() {
                            }.getType();
                            List<AuditList_Res> auditList = new Gson().fromJson(convert, listType);
                            Log.e("data----", "");
                            addAuditListInDB(auditList);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("Network Error :", e.toString());
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getAuditList",e.getMessage(),"SyncDataJobService","");
                        errorMsg(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e("", "");
                        if ((updateIndex + updateLimit) <= count) {
                            updateIndex += updateLimit;
                            getAuditList();
                        } else {
                            if (count != 0) {
                                App_preference.getSharedprefInstance().setAuditSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                            }
                            updateIndex = 0;
                            count = 0;
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().deleteJobByIsDelete();
                            //     AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().deleteAuditStatusNot();
                            App_preference.getSharedprefInstance().setFirstSyncState(10);
                            startSyncFromStatus();
                            Log.v("MainSync","Sync completed "+" --" +"Audit List Sync Done");
                        }
                    }
                });
    }

    private void addAuditListInDB(List<AuditList_Res> auditList) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().inserAuditList(auditList);
    }

    public void errorMsg(String errorMessage) {
            Intent local = new Intent();
            local.setAction("showAlert");
            this.sendBroadcast(local);
    }


   /* public void retryCall() {
        int status_no = App_preference.getSharedprefInstance().getFirstSyncState();
        if (status_no == 0) {
            OfflineDbSync();
        } else {
            startSyncFromStatus();
        }
    }*/

    public void OfflineDbSync() {
        OfflineDataController.getInstance().fromFirstTimeSyncCall((success_no, msg) -> startSyncFromStatus());
    }

    private static class LoadImage extends AsyncTask<String, Void, Bitmap> {

        String statusNo="0";
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                statusNo=strings[1];
                InputStream inputStream = new URL(strings[0]).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            try
            {
                Log.e("StatusNo:: ",statusNo);
                Log.e("BitmapImage:: ",AppUtility.BitMapToString(bitmap));
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().upadteBitmapUrl(statusNo,AppUtility.BitMapToString(bitmap));
                Log.e("JobStatusListSecond::",new Gson().toJson(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().getAllStatusList()));
                // to set the updated list in the job status controller
                JobStatus_Controller.getInstance().setDynamicStatusList();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

//            imageView.setImageBitmap(bitmap);
        }
    }

}