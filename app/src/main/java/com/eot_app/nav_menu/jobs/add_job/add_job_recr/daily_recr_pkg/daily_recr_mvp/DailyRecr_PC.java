package com.eot_app.nav_menu.jobs.add_job.add_job_recr.daily_recr_pkg.daily_recr_mvp;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.eot_app.nav_menu.jobs.add_job.add_job_recr.daily_recr_pkg.daily_recur_model.DailyMsgReqModel;
import com.eot_app.nav_menu.jobs.add_job.add_job_recr.daily_recr_pkg.daily_recur_model.DailyMsgResModel;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.language_support.LanguageController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.Callable;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
//import android.util.Log;


/**
 * Created by Sona-11 on 15/3/21.
 */
public class DailyRecr_PC implements DailyRecr_PI {
    private final DailyRecr_View dailyRecrView;

    public DailyRecr_PC(DailyRecr_View dailyRecrView) {
        this.dailyRecrView = dailyRecrView;
    }

    @Override
    public void getDailyRecurMsg(DailyMsgReqModel dailyMsgReqModel) {

        if (AppUtility.isInternetConnected()) {

            ApiClient.getservices().eotServiceCall(Service_apis.dailyJobRecurrenceResult, AppUtility.getApiHeaders(),
                    AppUtility.getJsonObject(new Gson().toJson(dailyMsgReqModel)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String transVarModel = new Gson().toJson(jsonObject.get("transVar").getAsJsonObject());
                                DailyMsgResModel dailyMsgResModel = new Gson().fromJson(transVarModel, DailyMsgResModel.class);
                                Log.e("", "");
                                dailyRecrView.showMsgOnView(dailyMsgResModel);
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                dailyRecrView.onSessionExpired(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            } else {

                            }
                        }


                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                        }
                    });


        } else {
            netWorkError();
        }
    }

    private void netWorkError() {
        AppUtility.alertDialog((((Fragment) dailyRecrView).getActivity()), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return null;
            }
        });
    }

}





