package com.eot_app.api_call_controller;


import com.google.gson.JsonObject;

public interface ApiCallCallBack {
    void getSuccessResponse(JsonObject jsonObject);

    void getApiErrorResponse(Throwable error);

    void getApiCallComplete();

    //void disableSwipeRefresh();
}
