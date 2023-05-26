package com.eot_app.utility.settings.firstSync;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.eot_app.BuildConfig;
import com.eot_app.login_next.login_next_model.MobileDefaultSettings;
import com.eot_app.login_next.login_next_model.ResLoginData;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.JobStatusModelNew;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppCenterLogs;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.db.OfflineDataController;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.language_support.Language_Preference;
import com.eot_app.utility.settings.SettingUrls;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by aplite_pc302 on 7/23/18.
 */

public class FirstSyncPC implements FirstSyncPi {
    private final FirstSyncView firstSyncView;
    final int updateLimit;
    float new_version = 0.0f;
    private int count;
    private int updateIndex;

    private JsonObject getJsonObject(String params) {
        JsonParser parser = new JsonParser();
        return parser.parse(params).getAsJsonObject();
    }


    public FirstSyncPC(FirstSyncView firstSync) {
        this.firstSyncView = firstSync;
        this.updateLimit = AppConstant.LIMIT_HIGH;
        /* ***set current Activity/Fragment Context***/
    }

    @Override
    public void startSync() {
        getMobileDefaultSettings();
    }


    public void getJobStatusList() {
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
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getJobStatus","","FirstSyncPc",String.valueOf(jsonObject.get("success").getAsBoolean()));
                        Log.d("firstsync", jsonObject.toString());
                        if (jsonObject.get("success").getAsBoolean()) {
                            count = jsonObject.get("count").getAsInt();
                            String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                            Type listType = new TypeToken<List<JobStatusModelNew>>() {
                            }.getType();
                            List<JobStatusModelNew> statusList = new Gson().fromJson(convert, listType);

                            Log.d("firstsync", String.valueOf(statusList.size())+"apiresponse");
                            if (statusList != null){
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().insertJobstatusList(statusList);

                                for (JobStatusModelNew jobStatus: statusList) {
                                    if(jobStatus.getIsDefault().equalsIgnoreCase("0"))
                                    {
                                        //  new SyncDataJobService.LoadImage().execute(App_preference.getSharedprefInstance().getBaseURL()+jobStatus.getUrl(),jobStatus.getStatus_no());
                                    }
                                }
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().insertJobstatusList(statusList);
                                Log.d("firstsync",new Gson().toJson(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().getAllStatusList()));
                            }
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("Network Error :", e.toString());
                        AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getJobStatus","error block in getjostatus api","FirstSyncPc","");
                        OfflineDbSync();
                        /* *****/
                        //    errorMsg(e.toString());

                    }

                    @Override
                    public void onComplete() {
                        if ((updateIndex + updateLimit) <= count) {
                            updateIndex += updateLimit;
                            getJobStatusList();
                        } else {
                            updateIndex = 0;
                            count = 0;
                            OfflineDbSync();
                            updateUserAppVersion();
                            Log.v("MainSync","Sync completed "+" --" +"job time Sync Done");
                        }
                    }
                });
    }



    private void updateUserAppVersion()
    {
        if (AppUtility.isInternetConnected()){
            Map<String, String> hm = new HashMap<>();
            hm.put("usrId", App_preference.getSharedprefInstance().getLoginRes().getUsrId());
            hm.put("appVersion",BuildConfig.VERSION_NAME);
            String data = new Gson().toJson(hm);
            ApiClient.getservices().eotServiceCall(Service_apis.updateUserAppVersion, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onNext(@NonNull JsonObject jsonObject) {
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","updateUserAppVersion","Completed First Sync servcie","FirstSyncPc","");
                        }
                    });
        }else{
            AppUtility.alertDialog(((Context) firstSyncView),
                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert),
                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.no_internet_area),
                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.continue_), "", () -> {
                        firstSyncView.goHomePage();
                        return null;
                    });
        }
    }



    private void getMobileDefaultSettings() {//get default company setting
        if (AppUtility.isInternetConnected()) {
            Map<String, String> hm = new HashMap<>();
            hm.put("usrId", App_preference.getSharedprefInstance().getLoginRes().getUsrId());
            hm.put("devType", "1");//1 is a device type for android
            String data = new Gson().toJson(hm);
            //AppUtility.getApiHeaders(),
            ApiClient.getservices().userLogin(Service_apis.getMobileDefaultSettings, getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@androidx.annotation.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@androidx.annotation.NonNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getMobileDefaultSettings","","FirstSyncPc",String.valueOf(jsonObject.get("success").getAsBoolean()));
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(jsonObject.get("data").getAsJsonObject());
                                Log.e("TAG :", "HII  " + jsonString);
                                MobileDefaultSettings mobileDefaultSettings = gson.fromJson(jsonString, MobileDefaultSettings.class);

                                ResLoginData resLoginData = App_preference.getSharedprefInstance().getLoginRes();

                                if (resLoginData != null) {
                                    resLoginData.setMobileDefaultSettings(mobileDefaultSettings);
                                }
                                String saveLoginData = gson.toJson(resLoginData);
                                App_preference.getSharedprefInstance().setLoginResponse(saveLoginData);



                                App_preference.getSharedprefInstance().setcheckId(
                                        App_preference.getSharedprefInstance().getLoginRes().getCheckId());

                                Log.e("TAG:", "TAG");


                                /* **** check for language control ******/
                                checkForLanguageSettings();

                                if (App_preference.getSharedprefInstance().getLoginRes().getExpireStatus().equals("0")) {
                                    subscriptionExpire();
                                } else {
                                    /* **  check for App version update ***/
                                    checkForVersionUpdate();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("Api","getMobileDefaultSettings","Error Block in getMobileDefaultSetting","FirstSyncPc","");
                            getJobStatusList();
                        }

                        @Override
                        public void onComplete() {
                            Log.e("", "");
                        }
                    });
        }
        else {
            AppUtility.alertDialog(((Context) firstSyncView),
                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert),
                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.no_internet_area),
                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.continue_), "", () -> {
                        firstSyncView.goHomePage();
                        return null;
                    });
        }
    }

    private void subscriptionExpire() {
        Map<String, String> hm = new HashMap<>();
        String data = new Gson().toJson(hm);
        if (AppUtility.isInternetConnected())
            ApiClient.getservices().eotServiceCall(Service_apis.getSubscriptionData, AppUtility.getApiHeaders(),
                    AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onNext(@NonNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                firstSyncView.setSubscriptionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                                ResLoginData data = App_preference.getSharedprefInstance().getLoginRes();
                                data.setToken("");
                                App_preference.getSharedprefInstance().setLoginResponse(new Gson().toJson(data));
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            firstSyncView.errorMsg(e.toString());
                            Log.e("", Objects.requireNonNull(e.getMessage()));

                        }

                        @Override
                        public void onComplete() {
                            Log.e("", "");
                        }
                    });

    }




    private void checkForVersionUpdate() {
        try {
            float force_version = Float.valueOf(App_preference.getSharedprefInstance().getLoginRes().getForceupdate_version());
            float version = Float.valueOf(App_preference.getSharedprefInstance().getLoginRes().getVersion());

            PackageInfo pInfo = EotApp.getAppinstance().getPackageManager().getPackageInfo(EotApp.getAppinstance().getPackageName(), 0);
            float app_version = Float.valueOf(pInfo.versionName);
            if (force_version > app_version) {
                firstSyncView.upateForcefully();
            } else if (version > app_version && check24HourComplete()) {
//                check 24 hours complete ?
                firstSyncView.updateNotForcefully();
            } else {
                getJobStatusList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            getJobStatusList();
        }

    }

    private boolean check24HourComplete() {
        String datevalue = App_preference.getSharedprefInstance().getIS_24HOURS();
        if (datevalue.equals("")) {
            App_preference.getSharedprefInstance().setIS_24HOURS(AppUtility.getDateByMiliseconds());
            return true;
        }
        Date last_date = new Date(Long.valueOf(datevalue)); // last save date on update check.
        Date current_date = new Date(Long.valueOf(AppUtility.getDateByMiliseconds())); // current date.
        long duration = current_date.getTime() - last_date.getTime();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
        if (diffInDays >= 1) {
            App_preference.getSharedprefInstance().setIS_24HOURS(AppUtility.getDateByMiliseconds());
            return true;
        }
        return false;
    }

    private void checkForLanguageSettings() {

        try {

            float existing_version = Float.parseFloat(Language_Preference.getSharedprefInstance().getlanguageVersion());
            String existing_name = Language_Preference.getSharedprefInstance().getlanguageFilename();
            if (App_preference.getSharedprefInstance().getLoginRes().getLanguage() != null) {
                final String new_filename = App_preference.getSharedprefInstance().getLoginRes().getLanguage().getFileName();
                try {
                    try {

                        if ((App_preference.getSharedprefInstance().getLoginRes().getLanguage().getVersion()) != null) {
                            new_version = Float.parseFloat(App_preference.getSharedprefInstance().getLoginRes().getLanguage().
                                    getVersion());
                        } else {
                            new_version = 0.0f;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        new_version = 0.0f;
                    }

                    boolean isLanChangebyUser = Language_Preference.getSharedprefInstance().isUserChangeLang();
                    String islock = App_preference.getSharedprefInstance().getLoginRes().getLanguage().getIsLock();//"1";
                    //the language file only download if user not change by it self or admin change the language
                    if (!islock.equals("0") || !isLanChangebyUser || (!existing_name.equals(new_filename) && existing_version != new_version)) {
                        String file_path = App_preference.getSharedprefInstance().getLoginRes().getLanguage().getFilePath();
                        String download_url = file_path + new_filename + ".json";
                        LanguageController.getInstance().downloadFile(download_url, new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                App_preference.getSharedprefInstance().setLanFileVer(Float.parseFloat(BuildConfig.VERSION_NAME));
                                Language_Preference.getSharedprefInstance().setisUserChangeLang(false);
                                Language_Preference.getSharedprefInstance().setLanguageFilename(new_filename);
                                Language_Preference.getSharedprefInstance().setLanguageVersion(String.valueOf(new_version));
                                return null;
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OfflineDbSync() {
        OfflineDataController.getInstance().fromFirstTimeSyncCall((success_no, msg) -> {
            if (success_no == 1) {
//             TODO       firstSyncView.progressStatus(0);
                firstSyncView.progressStatus(3);
                firstSyncView.setUI(0);
            }
            getSettingsData();
        });
    }

    private void getSettingsData() {
        String compid = String.valueOf(App_preference.getSharedprefInstance().getLoginRes().getCompId());
        if (compid != null) {
            SettingUrls settingUrls = new SettingUrls(Integer.parseInt(compid), new CallBackFirstSync() {
                @Override
                public void getCallBackOfComplete(int success_no, String msg) {
                    if (success_no == 1) {
                        firstSyncView.progressStatus(12);
                        firstSyncView.setUI(0);
                        firstSyncView.goHomePage();
                        App_preference.getSharedprefInstance().setFirstSyncState(0);
                    } else if (success_no == 2) {
                        EotApp.getAppinstance().sessionExpired();
                        firstSyncView.sessionExpiredFinishActivity();
                    } else {
                        firstSyncView.errorMsg("");
                    }
                }
            });
            settingUrls.getJobTitleList();
        }
    }

    public interface CallBackFirstSync {
        void getCallBackOfComplete(int success_no, String msg); // if -1 server call fail , 0 means
    }
}
