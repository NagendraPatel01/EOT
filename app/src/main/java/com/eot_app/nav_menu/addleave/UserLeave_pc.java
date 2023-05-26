package com.eot_app.nav_menu.addleave;

import android.util.Log;

import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserLeave_pc implements UserLeave_pi {

    LeaveList_view leaveList_view;
    private int limit;
    private int index;
    private List<LeaveUserModel> leaveUserModels;
    private int count;

    public UserLeave_pc(LeaveList_view leaveList_view)
    {
        this.leaveList_view=leaveList_view;
        this.index=0;
        this.limit= AppConstant.LIMIT_HIGH;
        this.leaveUserModels=new ArrayList<>();
    }

    @Override
    public void getuserlist() {
        if (AppUtility.isInternetConnected())
        {
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("limit",limit);
            jsonObject.addProperty("index",index);

            ApiClient.getservices().eotServiceCall(Service_apis.getLeaveTypeList, AppUtility.getApiHeaders(),jsonObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {
                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                if (jsonObject.get("count")!=null) {
                                    count = jsonObject.get("count").getAsInt();
                                }
                                String jsonString = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type typelist=new TypeToken<List<LeaveUserModel>>() {
                                }.getType();
                                List<LeaveUserModel> user_list = new Gson().fromJson(jsonString, typelist);
                                leaveUserModels.addAll(user_list);
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            if ((index + limit) <= count) {
                                index += limit;
                                getuserlist();
                            } else {
                                index = 0;
                                count = 0;
                                List<LeaveUserModel> templis = new ArrayList<>();
                                templis.addAll(leaveUserModels);
                                leaveUserModels.clear();

                                leaveList_view.setuserlist(templis);
                            }
                        }
                    });
        }

    }
}
