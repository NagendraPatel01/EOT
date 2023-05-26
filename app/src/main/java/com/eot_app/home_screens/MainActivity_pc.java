package com.eot_app.home_screens;

import android.content.Context;
import android.util.Log;
import com.eot_app.activitylog.ActivityLogController;
import com.eot_app.lat_lng_sync_pck.LatLngSycn_Controller;
import com.eot_app.login_next.login_next_model.ResLoginData;
import com.eot_app.nav_menu.jobs.job_controller.ChatController;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.language_support.Language_Preference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hypertrack.hyperlog.HyperLog;
import java.io.File;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
/**
 * Created by aplite_pc302 on 8/9/18.
 */

public class MainActivity_pc implements MainActivity_pi {
    private final MainActivityView mainActivityView;

    public MainActivity_pc(MainActivityView mainActivityView) {
        this.mainActivityView = mainActivityView;
    }


    private void addRecordsToDB(List<Job> data) {
        try {
            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().inserJob(data);
//        for add/remove listener.
            for (Job item : data) {
                if (item.getIsdelete().equals("0")
                        || item.getStatus().equals(AppConstant.Cancel)
                        || item.getStatus().equals(AppConstant.Closed)
                        || item.getStatus().equals(AppConstant.Reject)) {
                    ChatController.getInstance().removeListnerByJobID(item.getJobId());
                } else {
                    ChatController.getInstance().registerChatListner(item);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onLogoutServicecall() {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow((Context) mainActivityView);
            HashMap<String, String> hm = new HashMap<>();
            hm.put("udId", App_preference.getSharedprefInstance().getLoginRes().getUdId());
            Gson gson = new Gson();
            String data = gson.toJson(hm);

            if (AppUtility.isInternetConnected())
                ApiClient.getservices().eotServiceCall(Service_apis.logout, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<JsonObject>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                            }

                            @Override
                            public void onNext(@NonNull JsonObject jsonObject) {
                                if (jsonObject.get("success").getAsBoolean()) {
                                    mainActivityView.onClearCache();
//                                    add logout call
                                    ResLoginData data = App_preference.getSharedprefInstance().getLoginRes();
                                    data.setToken("");
                                    data.setIsAutoTimeZone("");
                                    data.setLoginUsrTz("");
                                    Language_Preference.getSharedprefInstance().setStaticMsgsModel("");
                                    App_preference.getSharedprefInstance().setJobStartSyncTime("");
                                    // for syncing of contact and site
                                    App_preference.getSharedprefInstance().setContactSiteSynced(false);
                                    App_preference.getSharedprefInstance().setLoginResponse(new Gson().toJson(data));
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.e("", Objects.requireNonNull(e.getMessage()));
                                AppUtility.progressBarDissMiss();
                            }

                            @Override
                            public void onComplete() {
                                mainActivityView.onLogout("");
                                AppUtility.progressBarDissMiss();
                                ChatController.getInstance().setAppUserOnline(0);
                            }
                        });
        } else {
            networkDialog();
        }
    }

    @Override
    public void addCheckInOutIime(String des,String file,String finalFname,String timeCheckout) {
        String usrId = App_preference.getSharedprefInstance().getLoginRes().getUsrId();
        String time="";
        if(timeCheckout!=null&&!timeCheckout.isEmpty()){
            time = timeCheckout;
        }
        else {
            time = AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT_new);
        }

        String checkId = App_preference.getSharedprefInstance().getcheckId();
        String CHECK_IN_TYPE = "1";
        String CHECK_OUT_TYPE = "2";
        String checkType = App_preference.getSharedprefInstance().getcheckId().isEmpty() ? CHECK_IN_TYPE : CHECK_OUT_TYPE;
        if (AppUtility.isInternetConnected()) {

            RequestBody usrIdBody = RequestBody.create(usrId,MultipartBody.FORM);
            RequestBody timeBody = RequestBody.create(time,MultipartBody.FORM);
            RequestBody checkIdBody = RequestBody.create(checkId,MultipartBody.FORM);
            RequestBody checkTypeBody = RequestBody.create(checkType,MultipartBody.FORM);

            RequestBody latBody;
            RequestBody lngBody;

            if (AppUtility.isGPSEnabled()
                    &&LatLngSycn_Controller.getInstance().getLat()!=null&&
                    !LatLngSycn_Controller.getInstance().getLat().isEmpty()
                    &&LatLngSycn_Controller.getInstance().getLng()!=null&&
                    !LatLngSycn_Controller.getInstance().getLng().isEmpty()) {
                latBody = RequestBody.create(LatLngSycn_Controller.getInstance().getLat(),MultipartBody.FORM);
                lngBody = RequestBody.create(LatLngSycn_Controller.getInstance().getLng(),MultipartBody.FORM);
            } else {
                latBody = RequestBody.create("0.0",MultipartBody.FORM);
                lngBody = RequestBody.create("0.0",MultipartBody.FORM);
            }

            ActivityLogController.saveActivity(ActivityLogController.JOB_MODULE, ActivityLogController.JOB_UPLOAD_DOC, ActivityLogController.JOB_MODULE);
            String mimeType = "";
            MultipartBody.Part body = null;
            if (file != null) {
                File file1 = new File(file);
                mimeType = URLConnection.guessContentTypeFromName(file1.getName());
                if (mimeType == null) {
                    mimeType = finalFname;
                }
                RequestBody requestFile = RequestBody.create(file1, MediaType.parse(mimeType));
                // MultipartBody.Part is used to send also the actual file name
                body = MultipartBody.Part.createFormData("attachment", finalFname + file.substring(file.lastIndexOf(".")), requestFile);
            }
            RequestBody descBody=null;
            if(des!=null && !des.isEmpty()){
                descBody = RequestBody.create(des,MultipartBody.FORM);
            }

            if (AppUtility.isInternetConnected())
                ApiClient.getservices().postCheckInCheckOut(
                        AppUtility.getApiHeaders(),
                        usrIdBody,
                        timeBody,
                        checkIdBody,
                        checkTypeBody,
                        latBody,
                        lngBody,
                        descBody,
                        body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<JsonObject>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull JsonObject jsonObject) {
                                AppUtility.progressBarDissMiss();
                                if (jsonObject.get("success").getAsBoolean()) {
                                    HyperLog.i("Check In check out success","Success");
                                    Log.e("TAG: ", "TAG");
                                    JsonObject dataobj = jsonObject.getAsJsonObject("data");
                                    if (dataobj != null) {

                                        String checkId = dataobj.get("checkId").getAsString();

                                        HyperLog.i("TAG","Check In check out checkId "+ checkId);
                                        App_preference.getSharedprefInstance().setcheckId(checkId);

                                        // For resolving the check in check out issue
                                        try
                                        {
                                            if(dataobj.get("lastCheckIn").getAsString()!=null && !dataobj.get("lastCheckIn").getAsString().isEmpty()){
                                                String lastCheckIn = dataobj.get("lastCheckIn").getAsString();
                                                HyperLog.i("TAG","Check In check out lastCheckIn "+lastCheckIn);
                                                ResLoginData data = App_preference.getSharedprefInstance().getLoginRes();
                                                data.setLastCheckIn(lastCheckIn);
                                                App_preference.getSharedprefInstance().setLoginResponse(new Gson().toJson(data));
                                                HyperLog.i("TAG","Check In check out lastCheckIn "+App_preference.getSharedprefInstance().getLoginRes().getLastCheckIn());
                                                HyperLog.i("TAG","Check In check out lastCheckIn date time "+AppUtility.getDateWithFormate(Long.parseLong(App_preference.getSharedprefInstance().getLoginRes().getLastCheckIn()), "dd-MM-yyyy kk:mm"));
                                            }
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }


                                        mainActivityView.checkIdUpdateUI(checkId, jsonObject.get("message").getAsString());

                                    } else {
                                        App_preference.getSharedprefInstance().setcheckId("");
                                        HyperLog.i("Check In check out ","Check In check out dataobjNull");
                                        mainActivityView.checkIdUpdateUI("", jsonObject.get("message").getAsString());
                                    }
                                } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                    HyperLog.i("Check In check out exception","Else if condition");
                                    EotApp.getAppinstance().sessionExpired();
                                } else {
                                    HyperLog.i("Check In check out exception","Else condition");
                                    Log.e("TAG: ", "TAG");
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                AppUtility.progressBarDissMiss();
                                HyperLog.i("TAG","Check In check out exception"+e.getMessage());
                                Log.e("", Objects.requireNonNull(e.getMessage()));
                                mainActivityView.check_In_Out_Fail();
                            }

                            @Override
                            public void onComplete() {
                                AppUtility.progressBarDissMiss();
                            }
                        });
        } else {
            AppUtility.progressBarDissMiss();
            mainActivityView.setEnableButton();
            networkDialog();
        }
    }
    public void networkDialog() {
        AppUtility.alertDialog(((Context) mainActivityView), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
    }
}
