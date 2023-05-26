package com.eot_app.utility.db;

import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.settings.setting_db.ErrorLog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by aplite_pc302 on 9/4/18.
 */

public class ErrorLogController {
    private final ErrorApiCallBack callBack = data -> {
        if (data != null) {
            int isdelete = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).errorLogmodel().deleteById(data.getId());
            if (isdelete == 1) {
                sendErrorReports();
            }
        }
    };

    public void sendErrorReports() {
        if (AppUtility.isInternetConnected()) {
            ErrorLog record = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).errorLogmodel().getSingleRecord();
            if (record != null) {
                if (record.getApiUrl().equals(Service_apis.addFWlatlong)) {
                    AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).errorLogmodel().deleteById(record.getId());
                } else
                    callErrorRequest(record);
            }
        }
    }

    private void callErrorRequest(final ErrorLog record) {
        ApiClient.getservices().eotServiceCall(Service_apis.errorLogMail, AppUtility.getApiHeaders(), AppUtility.getJsonObject(new Gson().toJson(record)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull JsonObject jsonObject) {
                        if (jsonObject.get("success").getAsBoolean()) {
                            callBack.getResponse(record);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface ErrorApiCallBack {
        void getResponse(ErrorLog data);
    }
}
