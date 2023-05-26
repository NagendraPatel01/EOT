package com.eot_app.utility.db;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.eot_app.BuildConfig;
import com.eot_app.activitylog.LogModelActivity;
import com.eot_app.nav_menu.appointment.addupdate.model.AppointmentAddReq;
import com.eot_app.nav_menu.appointment.addupdate.model.AppointmentUpdateReq;
import com.eot_app.nav_menu.appointment.dbappointment.Appointment;
import com.eot_app.nav_menu.audit.addAudit.add_aduit_model_pkg.AddAudit_Req;
import com.eot_app.nav_menu.audit.audit_list.audit_mvp.model.AuditList_Res;
import com.eot_app.nav_menu.audit.audit_list.report.mode.ReportRequest;
import com.eot_app.nav_menu.client.client_db.Client;
import com.eot_app.nav_menu.client.clientlist.client_detail.contact.client_dao.ContactData;
import com.eot_app.nav_menu.client.clientlist.client_detail.site.sitelist.Site_model;
import com.eot_app.nav_menu.item.QtyReqModel;
import com.eot_app.nav_menu.jobs.add_job.addjobmodel.AddJob_Req;
import com.eot_app.nav_menu.jobs.job_complation.compla_model.JobComplation;
import com.eot_app.nav_menu.jobs.job_controller.ChatController;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_detail.addinvoiveitem2pkg.model.AddInvoiceItemReqModel;
import com.eot_app.nav_menu.jobs.job_detail.addinvoiveitem2pkg.model.InvoiceItemDataModel;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.Jobdetail_status_res;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.Fromdb.CustomFormSubmited;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.ans_model.AnsModel_Offline;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.ans_model.Ans_Req;
import com.eot_app.nav_menu.jobs.joboffline_db.JobOfflineDataModel;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppCenterLogs;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.settings.firstSync.FirstSyncPC;
import com.eot_app.utility.settings.setting_db.ErrorLog;
import com.eot_app.utility.settings.setting_db.OfflineLogTable;
import com.eot_app.utility.settings.setting_db.Offlinetable;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.hypertrack.hyperlog.DeviceLogModel;
import com.hypertrack.hyperlog.HyperLog;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.lang.reflect.Type;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by aplite_pc302 on 7/5/18.
 */

public class OfflineDataController {
    private static final OfflineDataController ourInstance = new OfflineDataController();
    private final String TAG = "OfflineDataController";
    private final Gson gson = new Gson();
    FirstSyncPC.CallBackFirstSync callBackFirstSync;
    int jobId = 0;

    private boolean isSync = false;
    OfflineSericeCallBack callBack = (data, obj) -> {
        Log.e("TAG : STATUS :", data.getParams());
//            before deleting the row some thing (*Add) have to confirm.
        switch (data.getService_name()) {
            case Service_apis.updateClientSite:
                Site_model siteModel = gson.fromJson(obj.get("data").getAsJsonObject().toString(), Site_model.class);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel().update(siteModel);
                EotApp.getAppinstance().notifyCon_SiteObserver(data.getService_name());
                break;
            case Service_apis.updateAppointment:
                AppointmentUpdateReq updateReq = gson.fromJson(data.getParams(), AppointmentUpdateReq.class);
                if (updateReq.getMemIds() == null || updateReq.getMemIds().size() == 0) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().deleteAppointmentById(updateReq.getAppId());
                } else if (!(updateReq.getMemIds()).contains(App_preference.getSharedprefInstance().getLoginRes().getUsrId())) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().deleteAppointmentById(updateReq.getAppId());
                }
                EotApp.getAppinstance().notifyApiObserver(data.getService_name());
                break;
            case Service_apis.addAppointment:
                Appointment appointmentItem = gson.fromJson(obj.get("data").getAsJsonObject().toString(), Appointment.class);
                AppointmentAddReq addAppointment_req = gson.fromJson(data.getParams(), AppointmentAddReq.class);
                if (addAppointment_req.getMemIds() == null || addAppointment_req.getMemIds().size() == 0) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().deleteAppointmentByTempId(addAppointment_req.getTempId());
                } else if (!(addAppointment_req.getMemIds()).contains(App_preference.getSharedprefInstance().getLoginRes().getUsrId())) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().deleteAppointmentByTempId(addAppointment_req.getTempId());

                } else {
                    Appointment oldAppointment = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().getAppointmentById(addAppointment_req.getTempId());
                    oldAppointment.setAppId(appointmentItem.getAppId());
                    oldAppointment.setLabel(appointmentItem.getLabel());
                    List<Appointment> appointmentList = new ArrayList<>();
                    appointmentList.add(oldAppointment);
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().insertAppointments(appointmentList);
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().deleteAppointmentById(addAppointment_req.getTempId());
                }
                EotApp.getAppinstance().notifyApiObserver(data.getService_name());
                break;

            case Service_apis.addAudit:
                AuditList_Res auditList_res = gson.fromJson(obj.get("data").getAsJsonObject().toString(), AuditList_Res.class);
                AddAudit_Req addAudit_req = gson.fromJson(data.getParams(), AddAudit_Req.class);
                if (addAudit_req.getMemIds() == null || addAudit_req.getMemIds().size() == 0) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().deleteAuditByTempId(addAudit_req.getTempId());
                } else if (!(addAudit_req.getMemIds()).contains(App_preference.getSharedprefInstance().getLoginRes().getUsrId())) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().deleteAuditByTempId(addAudit_req.getTempId());
                } else {
                    AuditList_Res oldAuditList_res = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().getAuditById(addAudit_req.getTempId());
                    oldAuditList_res.setAudId(auditList_res.getAudId());
                    oldAuditList_res.setLabel(auditList_res.getLabel());
                    List<AuditList_Res> auditList_res1 = new ArrayList<>();
                    auditList_res1.add(oldAuditList_res);
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().inserAuditList(auditList_res1);
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().deletAuditById(addAudit_req.getTempId());
                }
                EotApp.getAppinstance().notifyApiObserver(data.getService_name());
                break;

            case Service_apis.addJob:
                Job jobitem = gson.fromJson(obj.get("data").getAsJsonObject().toString(), Job.class);

                AddJob_Req addJob_req = gson.fromJson(data.getParams(), AddJob_Req.class);
                if (addJob_req.getMemIds() == null) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobByTempId(jobitem.getTempId());
                } else if (!(addJob_req.getMemIds()).contains(App_preference.getSharedprefInstance().getLoginRes().getUsrId())) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobByTempId(jobitem.getTempId());
                    /*initialize all fieldworkers unread count "0" with admin and super admin*/
                    if (App_preference.getSharedprefInstance().getLoginRes().getAdminIds() != null) {
                        List<String> userIds = App_preference.getSharedprefInstance().getLoginRes().getAdminIds();
                        userIds.addAll(addJob_req.getMemIds());
                        ChatController.getInstance().initializeUnreadFromJobID(jobitem.getLabel(), jobitem.getJobId(), userIds);
                    }
                } else {
                    /*android.database.sqlite.SQLiteConstraintException: Unique constrains issue  ************  **/
                    Job oldJod = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobitem.getTempId());
                    oldJod.setJobId(jobitem.getJobId());
                    oldJod.setLabel(jobitem.getLabel());
                    List<Job> jobList = new ArrayList<>();
                    jobList.add(oldJod);
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().inserJob(jobList);

//                    add job into database
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobById(jobitem.getTempId());
                    // to update the job id in the detail page
                    EotApp.getAppinstance().getAddJobObserver(jobitem.getTempId(),jobitem.getJobId());
                }
                jobStatusAndItemSync(jobitem);
                EotApp.getAppinstance().notifyApiObserver(data.getService_name());

                try {
                    if (jobitem.getJobId() != null && jobitem.getLabel() != null) {
                        String msg = "A new Job has been created with Job code " + jobitem.getLabel() + ".";// in the system
                        String s1 = (Html.fromHtml("<b>" + jobitem.getLabel()).toString() + "</b>");

                        String tempMsg = "A new Job has been " + (Html.fromHtml("<b>created</b>"))
                                + " with Job code " + s1 + ".";

                        ChatController.getInstance().notifyWeBforNew("JOB", "AddJob", jobitem.getJobId(), tempMsg, "");
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                HyperLog.i(TAG, "Add job pending operation completed");

                break;
            case Service_apis.addClientContact:
                ContactData contactitem = gson.fromJson(obj.get("data").getAsJsonObject().toString(), ContactData.class);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contactModel().udpateContactByTempIdtoOriganalId(contactitem.getConId(), contactitem.getTempId());
                if (contactitem.getDef().equals("1")) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contactModel().updateDefaultContact(contactitem.getConId(), contactitem.getCltId());
                }
                EotApp.getAppinstance().notifyCon_SiteObserver(data.getService_name());
                break;
            case Service_apis.addClientSite:
                Site_model siteitem = gson.fromJson(obj.get("data").getAsJsonObject().toString(), Site_model.class);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel()
                        .updateSiteByTempIdtoOriganalId(siteitem.getSiteId(), siteitem.getTempId());
                if (siteitem.getDef().equals("1")) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel().updateDefault(siteitem.getCltId(), siteitem.getSiteId());
                } else {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel().update(siteitem);
                }
                EotApp.getAppinstance().notifyCon_SiteObserver(data.getService_name());

                break;
            case Service_apis.addClient:
//                  add client into data base
                Client clientitem = gson.fromJson(obj.get("data").getAsJsonObject().toString(), Client.class);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).clientModel().udpateClientByTempIdtoOriganalId(clientitem.getCltId(), clientitem.getTempId());
                EotApp.getAppinstance().notifyApiObserver(data.getService_name());
                try {
                    if (clientitem.getCltId() != null && clientitem.getNm() != null) {
                        String msg = "A new Client has been created with Client Name " + clientitem.getNm() + ".";// in the system

                        ChatController.getInstance().notifyWeBforNew("CLIENT", "AddClient", clientitem.getCltId(), msg, "");
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            case Service_apis.changeJobStatus:
                HyperLog.i("ChangeJobStatus", "ChangeJobStatus - Success Api" );

                // delete offline job record
                if (jobId != 0) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobOfflineDao().deleteFromId(jobId);
                    jobId = 0;
                }
                if (obj.get("data").isJsonArray()
                        && obj.get("data").getAsJsonArray().size() > 0
                        && obj.get("data").getAsJsonArray().get(0).getAsJsonObject().get("status_code").
                        getAsString().equals("1")) {
                    String jobId = obj.get("data").getAsJsonArray().get(0).getAsJsonObject().get("jobid").getAsString();
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobById(jobId);
//  for alert msg to remove job
                    EotApp.getAppinstance().notifyObserver("removeFW", jobId, LanguageController.getInstance().getServerMsgByKey(obj.get("message").getAsString()));
// delete all record related to job which are deleted by server
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().deleteFromSearchJobID("%" + jobId + "%");
                }

                EotApp.getAppinstance().notifyApiObserver(data.getService_name());

                try {
                    Job j = gson.fromJson(data.getParams(), Job.class);
                    if (j != null && j.getJobId() != null) {
                        Job tempJob = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(j.getJobId());
                        if (tempJob != null && tempJob.getJobId() != null && tempJob.getNm() != null) {

                            String s1 = (Html.fromHtml("<b>" + tempJob.getLabel()).toString() + "</b>");

                            String tempMsg = "A Job " + (Html.fromHtml("<b>status</b>"))
                                    + " of " + s1 + " has been updated by fieldworker " + App_preference.getSharedprefInstance().getLoginRes().getUsername() + ".";

                            ChatController.getInstance().notifyWeBforNew("JOB", "JobStatus", tempJob.getJobId(), tempMsg, tempJob.getStatus());
                        }
                    }
                } catch (Exception exception) {
                    HyperLog.i("ChangeJobStatus", "ChangeJobStatus - " + exception.getMessage());
                    exception.printStackTrace();
                }
                break;
            case Service_apis.setCompletionNotes:
                // for resolving the issue of completion notes not updating on client side for some clients
                try
                {
                    EotApp.getAppinstance().notifyApiObserver(Service_apis.getUserJobList);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case Service_apis.addItemOnJob:
            case Service_apis.deleteItemFromJob:
            case Service_apis.updateItemInJobMobile: {
                updateJobItems(data, obj);
                break;
            }
            case Service_apis.updateItemQuantity:
                notifyJobItemData(obj, data);
                break;
        }
        int check = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().deleteFromId(data.getId());
        if (check == 1) {
            getTotalRequest();
        }
    };

    private OfflineDataController() {
    }

    public static OfflineDataController getInstance() {
        return ourInstance;
    }

    /****Notify Job Item List after QTy update***/
    private void notifyJobItemData(JsonObject obj, Offlinetable data) {
        HyperLog.i(TAG, "notifyJobItemData(M) start");
        try {
            if (obj != null && obj.has("data") && obj.get("data").isJsonArray()
                    && obj.get("data").getAsJsonArray().size() > 0) {
                Type listType = new TypeToken<List<InvoiceItemDataModel>>() {
                }.getType();
                List<InvoiceItemDataModel> dataList = new Gson().fromJson(new Gson().toJson(obj.get("data").getAsJsonArray()), listType);

                QtyReqModel qtyReqModel = gson.fromJson(data.getParams(), QtyReqModel.class);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobitems(qtyReqModel.getJobId(), dataList);

                EotApp.getAppinstance().notifyApiItemAddEdit_Observer(data.getService_name(), qtyReqModel.getJobId());

            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            HyperLog.i(TAG, "JsonSyntaxException " + e.toString());
        }
        HyperLog.i(TAG, "notifyJobItemData(M) Completed");
    }

    public void updateSyncdata() {
        int i = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().getList().size();
        if (i > 0 && !isSync) {
            HyperLog.i(TAG, "updateSyncdata(M) Hard Refresh calling....");
            isSync = false;
            getTotalRequest();
        }
    }


    /***Sync Job State's & Item's from Local Table to OfflineTable ****/
    private void jobStatusAndItemSync(Job jobitem) {
        try {
            List<JobOfflineDataModel> offlineJobstatusdata = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobOfflineDao().getJobofflineDataById(jobitem.getTempId(),
                    Service_apis.changeJobStatus);
            try {
                if (offlineJobstatusdata != null)
                    HyperLog.i(TAG, "job status Temp to offlineTable start:" + offlineJobstatusdata.size());
                if (offlineJobstatusdata != null && offlineJobstatusdata.size() > 0) {
                    for (JobOfflineDataModel model : offlineJobstatusdata) {
                        Jobdetail_status_res jobdetail_status_res = new Gson().fromJson(model.getParams(), Jobdetail_status_res.class);
                        jobdetail_status_res.setJobId(jobitem.getJobId());
                        String param = new Gson().toJson(jobdetail_status_res);
                        model.setParams(param);
                        jobId = model.getId();
                        addInOfflineDB(model.getService_name(), model.getParams(), model.getTimestamp());
                        // TODO removed by shivani for managing syncing
//                        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobOfflineDao().deleteFromId(model.getId());
                    }
                }
                HyperLog.i(TAG, "job status Temp to offlineTable completed success:");

            } catch (Exception ex) {
                ex.printStackTrace();
                HyperLog.i(TAG, "job status Temp to offlineTable exception:" + ex.toString());

            }

            try {
                JobOfflineDataModel offlineJobItemdata = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance())
                        .jobOfflineDao().getJobofflineDataForInvoice(Service_apis.addItemOnJob, jobitem.getTempId());
                if (offlineJobItemdata != null) {
                    HyperLog.i(TAG, "Job item temp to offline start:" + offlineJobItemdata.toString());
                    AddInvoiceItemReqModel addInvoiceItemReqModel = new Gson().fromJson(offlineJobItemdata.getParams(), AddInvoiceItemReqModel.class);
                    addInvoiceItemReqModel.setJobId(jobitem.getJobId());
                    String param = new Gson().toJson(addInvoiceItemReqModel);
                    offlineJobItemdata.setParams(param);
                    addInOfflineDB(offlineJobItemdata.getService_name(), offlineJobItemdata.getParams(), offlineJobItemdata.getTimestamp());

                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobOfflineDao().deleteFromId(offlineJobItemdata.getId());
                    HyperLog.i(TAG, "Job item temp to offline completed success:" + offlineJobItemdata.toString());

                }
            } catch (Exception ex) {
                ex.getStackTrace();
                HyperLog.i(TAG, "Job item temp to offline exception:" + ex.toString());
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            HyperLog.i(TAG, "jobStatusAndItemSync(M) exception:" + ex.toString());
        }
    }

    /**
     * Notify Add/Update?Remove Item's
     ****/
    private void updateJobItems(Offlinetable data, JsonObject obj) {
        try {
            HyperLog.i(TAG, "updateJobItems(M) start");


            AddInvoiceItemReqModel addInvoiceItemReqModel = gson.fromJson(data.getParams(), AddInvoiceItemReqModel.class);

            if (data.getService_name().equals(Service_apis.deleteItemFromJob)) {
                addInvoiceItemReqModel.setAddItemOnInvoice(true);
            }
            if (obj != null && obj.has("data") && obj.get("data").isJsonArray()
                    && obj.get("data").getAsJsonArray().size() > 0) {
//        if (obj.get("data").isJsonArray() && obj.get("data").getAsJsonArray().size() > 0) {
                Type listType = new TypeToken<List<InvoiceItemDataModel>>() {
                }.getType();
                List<InvoiceItemDataModel> dataList = new Gson().fromJson(new Gson().toJson(obj.get("data").getAsJsonArray()), listType);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobitems(addInvoiceItemReqModel.
                        getJobId(), dataList);
                if (!addInvoiceItemReqModel.isAddItemOnInvoice()) {
                    EotApp.getAppinstance().notifyApiItemAddEdit_Observer(data.getService_name(), addInvoiceItemReqModel.getJobId());
                    EotApp.getAppinstance().getNotifyForItemCount();
                } else {
                    EotApp.getAppinstance().notifyInvoiceItemObserver(obj.get("totalAmount").toString());
                }
            }

            HyperLog.i(TAG, "updateJobItems(M) completed");

        } catch (Exception ex) {
            ex.printStackTrace();
            HyperLog.i(TAG, "updateJobItems(M) exception:" + ex.toString());

        }

    }

    public void addLogListInOfflineDB(String url, String params, String timestamp) {
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlineLogmodel().insertLogOffline(new OfflineLogTable(url, params, timestamp));
        if (!isSync) {
            HyperLog.i(TAG, "Syncing start pending request:" +
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlineLogmodel().getCountOfRowLog() + "");
            // insert the stack in offline table when the count reaches 25
            insertInOfflineTable(url, timestamp);
        } else
            HyperLog.i(TAG, "syncing already in progress pending:" + AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlineLogmodel().getCountOfRowLog());
    }

    public void insertInOfflineTable(String url, String timestamp) {
        List<OfflineLogTable> record1 = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlineLogmodel().getOfflinetablesByIdLog(Service_apis.insertUserActivityBulk);
        Log.e("InsertLogRecords", new Gson().toJson(record1));
        if (record1.size() >= 25) {
            List<String> record25 = new ArrayList<>();
            // insert the first 25 records and send the request
            for (int i = 0; i < 25; i++) {
                record25.add(record1.get(i).getParams());
            }
            String param = new Gson().toJson(record25);
            // for adding the list of 25 log records as a single record in the main offline table
            addInOfflineDB(url, param, timestamp);
            // to delete the offline log report every time
            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlineLogmodel().deleteLog();
        }
    }


    public void addInOfflineDB(String url, String params, String timestamp) {

        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().insertOffline(new Offlinetable(url, params, timestamp));

        if (!isSync) {
            HyperLog.i(TAG, "Syncing start pending request:" +
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().getCountOfRow() + "");
            getTotalRequest();
        } else
            HyperLog.i(TAG, "syncing already in progress pending:" + AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().getCountOfRow());
    }



    private synchronized  void addAnswerWithAttachment(final Offlinetable table)
    {
        List<MultipartBody.Part> docAns = new ArrayList<>();
        List<MultipartBody.Part> signAns = new ArrayList<>();
        String params = table.getParams();
        AnsModel_Offline ansModel_offline = new Gson().fromJson(params, AnsModel_Offline.class);
        Ans_Req ans_req = ansModel_offline.getAns_req();
        List<String> dosanspath = ansModel_offline.getDosanspath();
        ArrayList<String> docQueIdArrays = ansModel_offline.getDocQueIdArray();
        List<String> signanspath = ansModel_offline.getSignanspath();
        ArrayList<String> signQueIdArrays = ansModel_offline.getSignQueIdArray();

        for (int i=0;i<dosanspath.size();i++) {

            String mimeType = "";
            MultipartBody.Part body = null;
            File file = new File(dosanspath.get(i));

            if (file != null && file.exists()) {
                mimeType = URLConnection.guessContentTypeFromName(file.getName());
                if (mimeType == null) {
                    mimeType = file.getName();
                }
                RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
                // MultipartBody.Part is used to send also the actual file name
                body = MultipartBody.Part.createFormData("docAns[]", file.getName()
                        , requestFile);//ans.substring(ans.lastIndexOf(".")
                docAns.add(body);
            }
        }

            for (int i=0;i<signanspath.size();i++) {

                String mimeType = "";
                MultipartBody.Part body = null;
                File file = new File(signanspath.get(i));

                if (file != null && file.exists()) {
                    mimeType = URLConnection.guessContentTypeFromName(file.getName());
                    if (mimeType == null) {
                        mimeType = file.getName();
                    }
                    RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
                    // MultipartBody.Part is used to send also the actual file name
                    body = MultipartBody.Part.createFormData("signAns[]", file.getName()
                            , requestFile);//ans.substring(ans.lastIndexOf(".")
                    signAns.add(body);
                }
            }


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
                            if (jsonObject.get("success").getAsBoolean()) {
                                callBack.getResponse(table, jsonObject);
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
//                           onSessionExpire(jsonObject.get("message").getAsString());
//                            when session expires
                                EotApp.getAppinstance().sessionExpired();
                                isSync = false;
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.ALREADY_SYNC)) {
//                            when record already exist.
//                            Log.e(TAG, jsonObject.get("message").getAsString());
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().deleteFromId(table.getId());
                            } else {
                                sendForErrorLog(table, LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                                if (callBackFirstSync != null) { // for first time call from sync
                                    callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                                    callBackFirstSync = null;
                                }
                                isSync = false;
                            }

                            // if from sumbited to server then 1 is insert to data base
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).customFormSubmitedDao().insert(new CustomFormSubmited(ans_req.getFrmId(),ans_req.getJobId(),"1",App_preference.getSharedprefInstance().getLoginRes().getUsrId()));

                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).customFormDao().deleterecode( ans_req.getFrmId(), ans_req.getJobId());

                            LocalBroadcastManager.getInstance(EotApp.getAppinstance()).sendBroadcast(new Intent("FormSubmittedToServer"));
                            LocalBroadcastManager.getInstance(EotApp.getAppinstance()).sendBroadcast(new Intent("loadfromserver"));
                        }

                        @Override
                        public void onError(Throwable e) {
                            sendForErrorLog(table, e.getMessage());
                            isSync = false;
                            if (callBackFirstSync != null) { // for first time call from sync
                                callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                                callBackFirstSync = null;
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private synchronized void callPendingLogRequest(final Offlinetable table) {
        Log.e("TAG's", "" + new Gson().toJson(table));
        HyperLog.i(TAG, "API NAME: " + table.getService_name());
        HyperLog.i(TAG, "PARAM:" + new Gson().toJson(table));
        HyperLog.i(TAG, "OFFLINE TABLE Data Synced:");

        // for getting the list of records from the param string

        List<JsonObject> logList = new ArrayList<>();
        JsonArray jsonElements = getJsonArray(table.getParams());
        for (JsonElement logs : jsonElements) {
            JsonObject object = getJsonObject(logs.getAsString());
            logList.add(object);
        }
        LogModelActivity obj = new LogModelActivity(logList);
        ApiClient.getservices().insertBulkLog(AppUtility.getApiHeaders(), obj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JsonObject jsonObject) {
                        Log.e("TAG's", "" + jsonObject.toString());
                        HyperLog.i(TAG, "Response:" + jsonObject.toString());

                        if (jsonObject.get("success").getAsBoolean()) {
                            callBack.getResponse(table, jsonObject);

                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                            EotApp.getAppinstance().sessionExpired();
                            isSync = false;
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.ALREADY_SYNC)) {
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().deleteFromId(table.getId());
                            isSync = false;
                        } else {
                            /*very important ***/
                            sendForErrorLog(table, LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            if (callBackFirstSync != null) { // for first time call from sync
                                callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                                callBackFirstSync = null;
                            }
                            isSync = false;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG's", "" + e.getMessage());
                        HyperLog.i(TAG, "onError:" + e.toString());
                        /*very important ***/
                        sendForErrorLog(table, e.getMessage());
                        isSync = false;
                        if (callBackFirstSync != null) { // for first time call from sync
                            callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                            callBackFirstSync = null;
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.e("", "");
                        Log.e("", "");
                        // isSync = false;
                    }
                });
    }

    public void getTotalRequest() {

        if (AppUtility.isInternetConnected()) {
            Offlinetable record = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().getSingleRecord();
            if (record != null) {
                isSync = true;
                callPendingRequest(record);
            } else {
                HyperLog.i(TAG, "Sync Completed pending request: " + AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().getCountOfRow());
                isSync = false;
                if (callBackFirstSync != null) { // for first time call from sync
                    callBackFirstSync.getCallBackOfComplete(1, "no pending records");
                    callBackFirstSync = null;
                }
                int rows = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).errorLogmodel().getNoOfRows();
                if (rows > 0) {
                    ErrorLogController ctr = new ErrorLogController();
                    ctr.sendErrorReports();
                    // for sending the log report everytime we send the error log mail
                    sendDeviceLog();
                }
//                remove all jobs, clients, sites, from main tables.
//                removeAllTempJobClientContactSite();
            }
        }
    }


    private synchronized void callPendingRequest(final Offlinetable table) {
        Log.e("TAG's", "" + table.getParams());
        HyperLog.i(TAG, "API NAME: " + table.getService_name());
        HyperLog.i(TAG, "PARAM:" + table.getParams());
        HyperLog.i(TAG, "OFFLINE TABLE Data Synced:");
        if (table.getService_name().equals(Service_apis.addAppointment) || table.getService_name().equals(Service_apis.updateAppointment)) {
            callPendingAppointmentRequest(table);
            return;
        } else if (table.getService_name().equals(Service_apis.addAuditReport)) {
            equipmentReport(table);
            return;
        } else if (table.getService_name().equalsIgnoreCase(Service_apis.insertUserActivityBulk)) {
            callPendingLogRequest(table);
            return;
        } else if (table.getService_name().equals("addAnswerWithAttachment"))
        {
            addAnswerWithAttachment(table);
            return;
        }
        // Done by shivani vani
      /* In case of offline mode if we create a job and set its completion notes in
       offline mode then job temp id will get saved as job id in completion notes
       api param so api will respond false and notes will not get saved so changing the job temp id with job id  */


        if (table.getService_name().equalsIgnoreCase(Service_apis.setCompletionNotes)) {
            JsonObject jsonObject = getJsonObject(table.getParams());

            if(jsonObject.get("jobId").getAsString().contains("Job-")){
                HyperLog.i("TAG","Completion Notes Data in case of offline"+new Gson().toJson(jsonObject));
                HyperLog.i("TAG","Completion Notes Param Data in case of offline"+new Gson().toJson(table.getParams()));
                String jobId=AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobIdbyTempId(jsonObject.get("jobId").getAsString());
                JobComplation request = new JobComplation
                        (jobId, jsonObject.get("complNote").getAsString());
                Gson gson = new Gson();
                String data = gson.toJson(request);
                table.setParams(data);
                HyperLog.i("TAG","Completion Notes Param Data after change in case of offline"+new Gson().toJson(table.getParams()));
            }
        }

        ApiClient.getservices().eotServiceCall(table.getService_name(), AppUtility.getApiHeaders(), getJsonObject(table.getParams()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull JsonObject jsonObject) {
                        Log.e("TAG's", "" + jsonObject.toString());

                        if(table.getService_name().equalsIgnoreCase(Service_apis.changeJobStatus))
                        HyperLog.i("ChangeJobStatus", "ChangeJobStatus - On next Api Call "+jsonObject.toString() );
                        HyperLog.i(TAG, "Response:" + jsonObject.toString());

                        if (jsonObject.get("success").getAsBoolean()) {
                            if(table.getService_name().equalsIgnoreCase(Service_apis.changeJobStatus))
                            HyperLog.i("ChangeJobStatus", "ChangeJobStatus - success called" );

                            callBack.getResponse(table, jsonObject);
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
//                           onSessionExpire(jsonObject.get("message").getAsString());
//                            when session expires
                            EotApp.getAppinstance().sessionExpired();
                            if(table.getService_name().equalsIgnoreCase(Service_apis.changeJobStatus))
                            HyperLog.i("ChangeJobStatus", "ChangeJobStatus - statusCode" + jsonObject.get("statusCode").getAsString());

                            isSync = false;
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.ALREADY_SYNC)) {
//                            when record already exist.
//                            Log.e(TAG, jsonObject.get("message").getAsString());
                            if(table.getService_name().equalsIgnoreCase(Service_apis.changeJobStatus))
                            HyperLog.i("ChangeJobStatus", "ChangeJobStatus - statusCode" + jsonObject.get("statusCode").getAsString());

                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().deleteFromId(table.getId());
                            isSync = false;
                        } else {
                            if (table.getService_name().equals(Service_apis.addFWlatlong)) {
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).errorLogmodel().deleteById(table.getId());
                            } else {
                                if(table.getService_name().equalsIgnoreCase(Service_apis.changeJobStatus))
                                HyperLog.i("ChangeJobStatus", "ChangeJobStatus - Send Error ForLog");

                                /*very important ***/
                                sendForErrorLog(table, LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }

                            if (callBackFirstSync != null) { // for first time call from sync
                                callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                                callBackFirstSync = null;
                            }
                            isSync = false;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG's", "" + e.getMessage());

                        HyperLog.i(TAG, "onError:" + e.toString());
                        if (table.getService_name().equals(Service_apis.addFWlatlong)) {
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).errorLogmodel().deleteById(table.getId());
                        } else {
                            if(table.getService_name().equalsIgnoreCase(Service_apis.changeJobStatus))
                                HyperLog.i("ChangeJobStatus", "ChangeJobStatus - Send Error ForLog with message "+e.getMessage());

                            /*very important ***/
                            sendForErrorLog(table, e.getMessage());
                        }

                        isSync = false;
                        if (callBackFirstSync != null) { // for first time call from sync
                            callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                            callBackFirstSync = null;
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.e("", "");
                        Log.e("", "");
                        // isSync = false;
                    }
                });
    }

    private synchronized void callPendingAppointmentRequest(final Offlinetable table) {
        if (table.getService_name().equals(Service_apis.updateAppointment)) {
            callPendingUpdateAppointment(table);
            return;
        }
        final AppointmentAddReq appointmentModel = new Gson().fromJson(table.getParams(), AppointmentAddReq.class);
        RequestBody conId = null;
        RequestBody siteId = null;
        RequestBody cnm = null;
        RequestBody snm = null;


        if (TextUtils.isEmpty(appointmentModel.getConId()))
            conId = RequestBody.create("", MultipartBody.FORM);
        else
            conId = RequestBody.create(appointmentModel.getConId(), MultipartBody.FORM);

        if (TextUtils.isEmpty(appointmentModel.getSiteId()))
            siteId = RequestBody.create("", MultipartBody.FORM);
        else
            siteId = RequestBody.create(appointmentModel.getSiteId(), MultipartBody.FORM);

        if (TextUtils.isEmpty(appointmentModel.getCnm()))
            cnm = RequestBody.create("", MultipartBody.FORM);
        else cnm = RequestBody.create(appointmentModel.getCnm(), MultipartBody.FORM);

        if (TextUtils.isEmpty(appointmentModel.getSnm()))
            snm = RequestBody.create("", MultipartBody.FORM);
        else snm = RequestBody.create(appointmentModel.getSnm(), MultipartBody.FORM);

        RequestBody cltId = RequestBody.create(appointmentModel.getCltId(), MultipartBody.FORM);
        RequestBody leadId = RequestBody.create(appointmentModel.getLeadId(), MultipartBody.FORM);
        RequestBody des = RequestBody.create(appointmentModel.getDes(), MultipartBody.FORM);
        RequestBody schdlStart = RequestBody.create(appointmentModel.getSchdlStart(), MultipartBody.FORM);
        RequestBody schdlFinish = RequestBody.create(appointmentModel.getSchdlFinish(), MultipartBody.FORM);
        RequestBody nm = RequestBody.create(appointmentModel.getNm(), MultipartBody.FORM);
        RequestBody email = RequestBody.create(appointmentModel.getEmail(), MultipartBody.FORM);
        RequestBody mob1 = RequestBody.create(appointmentModel.getMob1(), MultipartBody.FORM);
        RequestBody mob2 = RequestBody.create(appointmentModel.getMob2(), MultipartBody.FORM);
        RequestBody adr = RequestBody.create(appointmentModel.getAdr(), MultipartBody.FORM);
        RequestBody city = RequestBody.create(appointmentModel.getCity(), MultipartBody.FORM);
        RequestBody state = RequestBody.create(appointmentModel.getState(), MultipartBody.FORM);
        RequestBody ctry = RequestBody.create(appointmentModel.getCtry(), MultipartBody.FORM);
        RequestBody zip = RequestBody.create(appointmentModel.getZip(), MultipartBody.FORM);
        RequestBody fclient = RequestBody.create(appointmentModel.getClientForFuture(), MultipartBody.FORM);
        RequestBody fcontact = RequestBody.create(appointmentModel.getContactForFuture(), MultipartBody.FORM);
        RequestBody fsite = RequestBody.create(appointmentModel.getSiteForFuture(), MultipartBody.FORM);

        String meidString = "";
        if (appointmentModel.getMemIds() != null)
            meidString = new Gson().toJson(appointmentModel.getMemIds());

        RequestBody rmids = RequestBody.create(meidString, MultipartBody.FORM);

        String mimeType = "";
        MultipartBody.Part body = null;
        List<MultipartBody.Part> files = new ArrayList<>();
        for (int i = 0; i < appointmentModel.getAppDoc().size(); i++) {
            File file1 = new File(appointmentModel.getAppDoc().get(i));
            String s = appointmentModel.getFileNames().get(i);
            if (file1 != null) {
                mimeType = URLConnection.guessContentTypeFromName(file1.getName());
                if (mimeType == null) {
                    mimeType = s;
                }
                RequestBody requestFile = RequestBody.create(file1, MediaType.parse(mimeType));
                // MultipartBody.Part is used to send also the actual file name
                body = MultipartBody.Part.createFormData("appDoc[]", s, requestFile);
                files.add(body);
            }
        }

        ApiClient.getservices().addAppointment(AppUtility.getApiHeaders(),
                cltId, siteId, conId, leadId, des, schdlStart, schdlFinish, nm
                , cnm, snm, email, mob1, mob2, adr, city, state, ctry, zip, fclient, fsite, fcontact, rmids, files)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JsonObject jsonObject) {
                        if (jsonObject.get("success").getAsBoolean()) {
                            callBack.getResponse(table, jsonObject);
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
//                           onSessionExpire(jsonObject.get("message").getAsString());
//                            when session expires
                            EotApp.getAppinstance().sessionExpired();
                            isSync = false;
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.ALREADY_SYNC)) {
//                            when record already exist.
//                            Log.e(TAG, jsonObject.get("message").getAsString());
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().deleteFromId(table.getId());
                        } else {
                            sendForErrorLog(table, LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            if (callBackFirstSync != null) { // for first time call from sync
                                callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                                callBackFirstSync = null;
                            }
                            isSync = false;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        sendForErrorLog(table, e.getMessage());
                        isSync = false;
                        if (callBackFirstSync != null) { // for first time call from sync
                            callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                            callBackFirstSync = null;
                        }


                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public synchronized void equipmentReport(final Offlinetable table) {
        ReportRequest request = new Gson().fromJson(table.getParams(), ReportRequest.class);
        String mimeType = "image/png";
        MultipartBody.Part custbody = null;
        MultipartBody.Part auditbbody = null;

        try {
            File file=new File(request.getFilecustPath());
            RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
            // MultipartBody.Part is used to send also the actual file name
            custbody = MultipartBody.Part.createFormData("custSign", file.getName(), requestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File file1=new File(request.getFilesignPath());
            RequestBody requestFile1 = RequestBody.create(file1, MediaType.parse(mimeType));
            // MultipartBody.Part is used to send also the actual file name
            auditbbody = MultipartBody.Part.createFormData("audSign", file1.getName(), requestFile1);
        } catch (Exception e) {
            e.printStackTrace();
        }


        RequestBody description = RequestBody.create(request.getDes(), MultipartBody.FORM);
        RequestBody audId = RequestBody.create(String.valueOf(request.getAudId()), MultipartBody.FORM);
        RequestBody userId = RequestBody.create(App_preference.getSharedprefInstance().getLoginRes().getUsrId(), MultipartBody.FORM);


        ApiClient.getservices().addAuditFeedback(AppUtility.getApiHeaders(),
                userId, audId, description, custbody, auditbbody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JsonObject jsonObject) {

                        if (jsonObject.get("success").getAsBoolean()) {
                            callBack.getResponse(table, jsonObject);
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
//                           onSessionExpire(jsonObject.get("message").getAsString());
//                            when session expires
                            EotApp.getAppinstance().sessionExpired();
                            isSync = false;
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.ALREADY_SYNC)) {
//                            when record already exist.
//                            Log.e(TAG, jsonObject.get("message").getAsString());
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().deleteFromId(table.getId());
                        } else {
                            sendForErrorLog(table, LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            if (callBackFirstSync != null) { // for first time call from sync
                                callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                                callBackFirstSync = null;
                            }
                            isSync = false;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        sendForErrorLog(table, e.getMessage());
                        isSync = false;
                        if (callBackFirstSync != null) { // for first time call from sync
                            callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                            callBackFirstSync = null;
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private synchronized void callPendingUpdateAppointment(final Offlinetable table) {
        final AppointmentUpdateReq liveAppointmentModel = new Gson().fromJson(table.getParams(), AppointmentUpdateReq.class);
        RequestBody conId = null;
        RequestBody siteId = null;
        RequestBody nm = null;
        RequestBody des = null;
        RequestBody email = null;
        RequestBody mob1 = null;
        RequestBody adr = null;
        RequestBody city = null;
        RequestBody zip = null;
        RequestBody status = null;

        if (TextUtils.isEmpty(liveAppointmentModel.getConId()))
            conId = RequestBody.create("", MultipartBody.FORM);
        else
            conId = RequestBody.create(liveAppointmentModel.getConId(), MultipartBody.FORM);

        if (TextUtils.isEmpty(liveAppointmentModel.getSiteId()))
            siteId = RequestBody.create("", MultipartBody.FORM);
        else
            siteId = RequestBody.create(liveAppointmentModel.getSiteId(), MultipartBody.FORM);

        RequestBody cnm = RequestBody.create("", MultipartBody.FORM);
        RequestBody cltId = RequestBody.create(liveAppointmentModel.getCltId(), MultipartBody.FORM);


        if (TextUtils.isEmpty(liveAppointmentModel.getStatus()))
            status = RequestBody.create("", MultipartBody.FORM);
        else status = RequestBody.create(liveAppointmentModel.getStatus(), MultipartBody.FORM);

        if (TextUtils.isEmpty(liveAppointmentModel.getDes()))
            des = RequestBody.create("", MultipartBody.FORM);
        else des = RequestBody.create(liveAppointmentModel.getDes(), MultipartBody.FORM);

        if (TextUtils.isEmpty(liveAppointmentModel.getNm()))
            nm = RequestBody.create("", MultipartBody.FORM);
        else
            nm = RequestBody.create(liveAppointmentModel.getNm(), MultipartBody.FORM);

        if (TextUtils.isEmpty(liveAppointmentModel.getEmail()))
            email = RequestBody.create("", MultipartBody.FORM);
        else
            email = RequestBody.create(liveAppointmentModel.getEmail(), MultipartBody.FORM);

        if (TextUtils.isEmpty(liveAppointmentModel.getMob1()))
            mob1 = RequestBody.create("", MultipartBody.FORM);
        else
            mob1 = RequestBody.create(liveAppointmentModel.getMob1(), MultipartBody.FORM);

        if (TextUtils.isEmpty(liveAppointmentModel.getAdr()))
            adr = RequestBody.create("", MultipartBody.FORM);
        else
            adr = RequestBody.create(liveAppointmentModel.getAdr(), MultipartBody.FORM);
        if (TextUtils.isEmpty(liveAppointmentModel.getCity()))
            city = RequestBody.create("", MultipartBody.FORM);
        else
            city = RequestBody.create(liveAppointmentModel.getCity(), MultipartBody.FORM);

        RequestBody state = RequestBody.create(liveAppointmentModel.getState(), MultipartBody.FORM);
        RequestBody ctry = RequestBody.create(liveAppointmentModel.getCtry(), MultipartBody.FORM);
        if (TextUtils.isEmpty(liveAppointmentModel.getZip()))
            zip = RequestBody.create("", MultipartBody.FORM);
        else
            zip = RequestBody.create(liveAppointmentModel.getZip(), MultipartBody.FORM);

        //converting datetime format
        String startTime = "";
        String endTime = "";
        if (!TextUtils.isEmpty(liveAppointmentModel.getSchdlStart()))
            startTime = liveAppointmentModel.getSchdlStart();
        if (!TextUtils.isEmpty(liveAppointmentModel.getSchdlFinish()))
            endTime = liveAppointmentModel.getSchdlFinish();


        RequestBody schdlStart = RequestBody.create(startTime, MultipartBody.FORM);
        RequestBody schdlFinish = RequestBody.create(endTime, MultipartBody.FORM);


        String meidString = new Gson().toJson(liveAppointmentModel.getMemIds());

        RequestBody rmids = RequestBody.create(meidString, MultipartBody.FORM);
        RequestBody rbAppId = RequestBody.create(liveAppointmentModel.getAppId(), MultipartBody.FORM);


        String mimeType = "";
        MultipartBody.Part body = null;
        List<MultipartBody.Part> files = new ArrayList<>();
        for (int i = 0; i < liveAppointmentModel.getAppDoc().size(); i++) {
            File file1 = new File(liveAppointmentModel.getAppDoc().get(i));
            String s = liveAppointmentModel.getFileNames().get(i);
            if (file1 != null) {
                mimeType = URLConnection.guessContentTypeFromName(file1.getName());
                if (mimeType == null) {
                    mimeType = s;
                }
                RequestBody requestFile = RequestBody.create(file1, MediaType.parse(mimeType));
                // MultipartBody.Part is used to send also the actual file name
                body = MultipartBody.Part.createFormData("appDoc[]", s, requestFile);
                files.add(body);
            }
        }


        ApiClient.getservices().updateAppointment(AppUtility.getApiHeaders(),
                rbAppId,
                cltId,
                siteId,
                conId,
                status,
                des,
                schdlStart,
                schdlFinish,
                nm,
                cnm,
                email,
                mob1,
                adr,
                city,
                state,
                ctry,
                zip,
                rmids,
                files)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JsonObject jsonObject) {
                        if (jsonObject.get("success").getAsBoolean()) {
                            callBack.getResponse(table, jsonObject);
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
//                           onSessionExpire(jsonObject.get("message").getAsString());
//                            when session expires
                            EotApp.getAppinstance().sessionExpired();
                            isSync = false;
                        } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.ALREADY_SYNC)) {
//                            when record already exist.
//                            Log.e(TAG, jsonObject.get("message").getAsString());
                            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().deleteFromId(table.getId());
                        } else {
                            sendForErrorLog(table, LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            if (callBackFirstSync != null) { // for first time call from sync
                                callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                                callBackFirstSync = null;
                            }
                            isSync = false;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        sendForErrorLog(table, e.getMessage());
                        isSync = false;
                        if (callBackFirstSync != null) { // for first time call from sync
                            callBackFirstSync.getCallBackOfComplete(0, "Error Occur");
                            callBackFirstSync = null;
                        }
                    }
                    @Override
                    public void onComplete() {

                    }
                });

    }


    public MultipartBody.Part prepareFilePart(String partName, File file) {
        // create RequestBody instance from file
        if (file == null) return null;
        RequestBody requestFile =
                RequestBody.create(
                        file,
                        MediaType.parse("multipart/form-data")
                );
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void sendForErrorLog(Offlinetable table, String message) {
        // Todo as per discussion with ayush sir the api will get called 5 times in error case
        if (table.getCount() > 4) {

            if(table.getService_name().equalsIgnoreCase(Service_apis.changeJobStatus))
                HyperLog.i("ChangeJobStatus", "ChangeJobStatus - table count increases by 2 with message -"+message);

            ErrorLog errorLog = new ErrorLog();
            errorLog.setApiUrl(App_preference.getSharedprefInstance().getBaseURL() + table.getService_name());
            errorLog.setRequestParam(table.getParams());
            errorLog.setResponse(message);
            errorLog.setVersion(Build.MODEL + ", " + Build.VERSION.RELEASE
                    + " " + Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName() + ", " +
                    BuildConfig.VERSION_NAME
                    +" UserId : "+App_preference.getSharedprefInstance().getLoginRes().getUsrId());
            errorLog.setTime(table.getTimestamp());
            int check = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().deleteFromId(table.getId());

            if (check == 1) {
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).errorLogmodel().insertError(errorLog);
            }
            if(table.getService_name().equalsIgnoreCase(Service_apis.changeJobStatus))
                HyperLog.i("ChangeJobStatus", "ChangeJobStatus - Error Log Inserted");

        } else {
            new Handler().postDelayed(() -> {
                int update_count = table.getCount() + 1;
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).offlinemodel().updateCountById(table.getId(), update_count);
                if(table.getService_name().equalsIgnoreCase(Service_apis.changeJobStatus))
                    HyperLog.i("ChangeJobStatus", "ChangeJobStatus - table count "+update_count+" with message -"+message);

                Log.e(TAG, "ApiInError sendForErrorLog Called for  " + update_count);
                HyperLog.e(TAG, "ApiInError sendForErrorLog Called for  " + update_count);
            }, 1000);
        }
    }

    private JsonObject getJsonObject(String params) {
        JsonParser parser = new JsonParser();
        return parser.parse(params).getAsJsonObject();
    }

    private JsonArray getJsonArray(String params) {
        JsonParser parser = new JsonParser();
        return parser.parse(params).getAsJsonArray();
    }

    public void fromOutSideCall() {
        if (!isSync) {
            getTotalRequest();
        }
    }

    /**
     * call from First sync Controller
     **/
    public void fromFirstTimeSyncCall(FirstSyncPC.CallBackFirstSync callBackFirstSync) {
        this.callBackFirstSync = callBackFirstSync;
        if (!isSync) {
            getTotalRequest();
        }
    }

    public interface OfflineSericeCallBack {
        void getResponse(Offlinetable data, JsonObject obj);
    }
    public void sendDeviceLog() {

        if (AppUtility.isInternetConnected()) {

            StringBuilder builder = new StringBuilder();
            builder.append(getDeviceConfigurations() + "<br><br>");
            List<DeviceLogModel> deviceLogs = HyperLog.getDeviceLogs(false);
            if (deviceLogs != null) {
                for (DeviceLogModel s : deviceLogs) {
                    builder.append("<br>" + s.getDeviceLog());
                }

                ErrorLog errorLog = new ErrorLog();
                errorLog.setApiUrl("Send from setting");
                errorLog.setRequestParam("[]");
                errorLog.setResponse(builder.toString());
                errorLog.setVersion(Build.MODEL + ", " + Build.VERSION.RELEASE
                        + " " + Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName() + ", " + BuildConfig.VERSION_NAME);
                errorLog.setTime(System.currentTimeMillis() + "");

                ApiClient.getservices().eotServiceCall(Service_apis.errorLogMail, AppUtility.getApiHeaders(), AppUtility.getJsonObject(new Gson().toJson(errorLog)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<JsonObject>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {
                            }

                            @Override
                            public void onNext(@NotNull JsonObject jsonObject) {
                                if (jsonObject.get("success").getAsBoolean()) {
                                    Log.d("loggg", jsonObject.toString());
                                    // HyperLog.deleteLogs();
                                    Toast.makeText(EotApp.getAppinstance(), "Log sent", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                Log.d("loggg", e.toString());

                            }

                            @Override
                            public void onComplete() {
                                AppUtility.progressBarDissMiss();
                            }
                        });
            }
        } else {
            AppUtility.alertDialog(EotApp.getAppinstance(), LanguageController.getInstance().
                            getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey
                            (AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok),
                    "", () -> null);
        }
    }
    /**
     * get logged in device configuration for
     * better log understanding and bug fix
     */
    private String getDeviceConfigurations() {
        StringBuilder builder = new StringBuilder();
        builder.append(App_preference.getSharedprefInstance().getLoginRes().getUsername() + " ID:"
                + App_preference.getSharedprefInstance().getLoginRes().getUsrId());
        builder.append("\nCompany Id:" +
                App_preference.getSharedprefInstance().getLoginRes().getCompId());
        builder.append("\nDevice Name:" + Build.MANUFACTURER + " " + Build.BRAND + " " + Build.MODEL);
        builder.append("\nDevice Version:" + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName()
                + " " + Build.ID);
        builder.append("\nApp Version:" + BuildConfig.VERSION_NAME);

        builder.append("\nToken:" +
                App_preference.getSharedprefInstance().getLoginRes().getToken());

        builder.append("\nBASE url:" +
                App_preference.getSharedprefInstance().getBaseURL());

        return builder.toString();

    }
}
