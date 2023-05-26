package com.eot_app.utility;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.eot_app.login_next.Login2Activity;
import com.eot_app.nav_menu.jobs.job_controller.ChatController;
import com.eot_app.nav_menu.jobs.job_detail.JobDetailActivity;
import com.eot_app.nav_menu.jobs.job_detail.detail.NotifyForAddJob;
import com.eot_app.nav_menu.jobs.job_detail.detail.NotifyForAttchCount;
import com.eot_app.nav_menu.jobs.job_detail.detail.NotifyForEquipmentCount;
import com.eot_app.nav_menu.jobs.job_detail.detail.NotifyForEquipmentCountList;
import com.eot_app.nav_menu.jobs.job_detail.detail.NotifyForEquipmentCountRemark;
import com.eot_app.nav_menu.jobs.job_detail.detail.NotifyForItemCount;
import com.eot_app.nav_menu.jobs.job_detail.detail.NotifyForItemCountRemark;
import com.eot_app.nav_menu.jobs.job_detail.generate_invoice.InvoiceItemObserver;
import com.eot_app.nav_menu.jobs.joboffline_db.JobItem_Observer;
import com.eot_app.nav_menu.jobs.joboffline_db.JobOverViewNotify;
import com.eot_app.services.ForegroundService2;
import com.eot_app.utility.util_interfaces.ApiCallbackObserver;
import com.eot_app.utility.util_interfaces.ApiContactSiteObserver;
import com.eot_app.utility.util_interfaces.NotificationObserver;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by geet-pc on 18/5/18.
 */

public class EotApp extends Application implements Application.ActivityLifecycleCallbacks {
    private static EotApp INSTANCE;
    List<Activity> activityList = new ArrayList<>();
    private NotificationObserver notiobserver;
    private ApiCallbackObserver apiobserver;
    private ApiContactSiteObserver con_site_observer;
    private JobItem_Observer jobItemObserver;
    private InvoiceItemObserver invoiceItemObserver;
    private JobOverViewNotify jobOverViewNotify;
    private NotifyForAddJob notifyForAddJob;
    private NotifyForAttchCount notifyForAttchCount;
    private NotifyForItemCount notifyForItemCount;
    private NotifyForItemCountRemark notifyForItemCountRemark;
    private NotifyForEquipmentCount notifyForEquipmentCount;
    private NotifyForEquipmentCountRemark notifyForEquipmentCountRemark;
    private NotifyForEquipmentCountList notifyForEquipmentCountList;
    private NotifyForEquipmentStatusList notifyForEquipmentStatusList;
    private NotifyForInvoiceGenr notifyForInvoiceGenr;

    public static synchronized EotApp getAppinstance() {
        return INSTANCE;
    }

    public void showToastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void sessionExpired() {
        if (EotApp.getAppinstance().isMyServiceRunning(ForegroundService2.class)) {
//        stop service on logout.
            Intent stopIntent = new Intent(this, ForegroundService2.class);
            stopIntent.setAction(ForegroundService2.STOPFOREGROUND_ACTION);
//            startService(stopIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(stopIntent);
            } else {
                startService(new Intent(stopIntent));
            }
        }

        /* clear token on session expire */
        App_preference.getSharedprefInstance().setBlankTokenOnSessionExpire();

        Intent intent = new Intent(this, Login2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

//        sign out from firebse
        ChatController.getInstance().signOutUserFromFirebase();

    }

    public void notifyObserver(String action, String key, String msg) {
//        notify observer
        if (this.notiobserver instanceof JobDetailActivity && action.equals("removeFW")) {
            notiobserver.onNotifyListner(key, msg);
        }
    }


    public void setObserver(NotificationObserver observer) {
        this.notiobserver = observer;
    }

    public void notifyApiObserver(String api_name) {
        if (this.apiobserver != null) {
            apiobserver.onObserveCallBack(api_name);
        }
    }
    public void clearCache() {
        try {
            File dir = this.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void setApiObserver(ApiCallbackObserver observer) {
        this.apiobserver = observer;
    }

    public void setJobFlagObserver(JobOverViewNotify jobOverViewNotify) {
        this.jobOverViewNotify = jobOverViewNotify;
    }
    public void setAddJobObserver(NotifyForAddJob notifyForAddJob) {
        this.notifyForAddJob = notifyForAddJob;
    }


    public void getAddJobObserver(String tempId,String jobId) {
        if (this.notifyForAddJob != null) {
            notifyForAddJob.updateJobId(tempId,jobId);
        }
    }

    public void getNotifyForAttchCount() {
        if (notifyForAttchCount != null) {
            notifyForAttchCount.updateCOuntAttchment();
        }
    }

    public void getNotifyForItemCount() {
        if (notifyForItemCount != null) {
            notifyForItemCount.updateCountItem();
        }
    }public void getNotifyForItemCountRemark() {
        if (notifyForItemCountRemark != null) {
            notifyForItemCountRemark.updateCountItem();
        }
    }
    public void getNotifyForEquipmentCount() {
        if (notifyForEquipmentCount != null) {
            notifyForEquipmentCount.updateCountEquipment();
        }
    }
    public void getNotifyForEquipmentStatusList() {
        if (notifyForEquipmentStatusList != null) {
            notifyForEquipmentStatusList.updateStatusEquipment();
        }
    }
    public void getNotifyForEquipmentCountRemark() {
        if (notifyForEquipmentCountRemark != null) {
            notifyForEquipmentCountRemark.updateCountEquipment();
        }
    }
    public void getNotifyForEquipmentCountList() {
        if (notifyForEquipmentCountList != null) {
            notifyForEquipmentCountList.updateCountEquipment();
        }
    }

    public void setNotifyForAttchCount(NotifyForAttchCount notifyForAttchCount) {
        this.notifyForAttchCount = notifyForAttchCount;
    }
    public void setNotifyForItemCount(NotifyForItemCount notifyForItemCount) {
        this.notifyForItemCount = notifyForItemCount;
    }
    public void setNotifyForItemCountRemark(NotifyForItemCountRemark notifyForItemCount) {
        this.notifyForItemCountRemark = notifyForItemCount;
    }
    public void setNotifyForEquipmentCount(NotifyForEquipmentCount notifyForEquipmentCount) {
        this.notifyForEquipmentCount = notifyForEquipmentCount;
    }
    public void setNotifyForEquipmentStatusList(NotifyForEquipmentStatusList notifyForEquipmentStatus) {
        this.notifyForEquipmentStatusList = notifyForEquipmentStatus;
    }
    public void setNotifyForEquipmentCountRemark(NotifyForEquipmentCountRemark notifyForEquipmentCount) {
        this.notifyForEquipmentCountRemark = notifyForEquipmentCount;
    }
    public void setNotifyForEquipmentCountList(NotifyForEquipmentCountList notifyForEquipmentCount) {
        this.notifyForEquipmentCountList = notifyForEquipmentCount;
    }

    public void getNotifyForInvoiceGenr() {
        if (notifyForInvoiceGenr!=null)
         notifyForInvoiceGenr.notiFyGeneratInvoice();
    }

    public void setNotifyForInvoiceGenr(NotifyForInvoiceGenr notifyForInvoiceGenr) {
        this.notifyForInvoiceGenr = notifyForInvoiceGenr;
    }



    public void getJobFlagOverView() {
        if (this.jobOverViewNotify != null) {
            jobOverViewNotify.updateJobOverViewFlag();
        }
    }

    public void notifyCon_SiteObserver(String api_name) {
        if (this.con_site_observer != null) {
            con_site_observer.onObserveCallBack(api_name);
        }
    }

    public void setApiCon_SiteObserver(ApiContactSiteObserver observer) {
        this.con_site_observer = observer;
    }

    public void setApiItemAddEdit_Observer(JobItem_Observer itemAddEditObserver) {
        this.jobItemObserver = itemAddEditObserver;
    }

    public void notifyApiItemAddEdit_Observer(String api_name, String jobId) {
        if (this.jobItemObserver != null) {
            jobItemObserver.onObserveCallBack(api_name, jobId);
        }
    }

    public void setInvoiceItemObserver(InvoiceItemObserver invoiceItemObserver) {
        this.invoiceItemObserver = invoiceItemObserver;
    }

    public void notifyInvoiceItemObserver(String api_name) {
        if (this.invoiceItemObserver != null) {
            invoiceItemObserver.onObserveCallBack(api_name);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("TAG", "terminate app");
    }


    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public void setLocalLanguage(String local_language) {
        LocaleHelper.setLocale(EotApp.getAppinstance(), local_language);
    }

    public String getString(String stringName) {
        int id = getResources().getIdentifier(stringName, "string", getPackageName());
        return getString(id);
    }

    public String[] getStringArray(String arrayName) {
        String packageName = INSTANCE.getPackageName();
        int id = getResources().getIdentifier(arrayName, "array", packageName);
        return getResources().getStringArray(id);
    }

    @Override
    public void onCreate() {
        INSTANCE = this;
        super.onCreate();
//        new Instabug.Builder(this, "f3e2c36ebab9b82237b75ae26620d5e9")
//                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT)
//                .build();
        registerActivityLifecycleCallbacks(this);

        // this is for AppCenter Logs
        AppCenter.start(this, "a32d534d-2040-441c-9f66-80bf3203e9a7",
                Analytics.class, Crashes.class);


    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle bundle) {
        try {
            activityList.add(activity);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.e("", "");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.e("", "");

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        try {
            if (activityList != null) {
                activityList.remove(activity);
                if (activityList.isEmpty()) {
                    if (!TextUtils.isEmpty(App_preference.getSharedprefInstance().getLoginRes().getToken())) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isOnline", 0);
                        if (EotApp.getAppinstance().isMyServiceRunning(ForegroundService2.class)) {
                            ForegroundService2.isAppInBackground = true;
                            hashMap.put(ForegroundService2.F_ISBACKGROUND_FIELD, 1);
                        } else {
                            hashMap.put(ForegroundService2.F_ISBACKGROUND_FIELD, 0);
                            hashMap.put(ForegroundService2.F_STATUS_FIELD, 9);
                        }
                        // RealTimeDBController.setStatus(hashMap);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface NotifyForEquipmentStatusList {
        void updateStatusEquipment();
    }
}
