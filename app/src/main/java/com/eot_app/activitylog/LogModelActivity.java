package com.eot_app.activitylog;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LogModelActivity {
    @SerializedName("LogBulkData")
    List<JsonObject> LogBulkData;
    public LogModelActivity(List<JsonObject> logBulkData) {
        LogBulkData = logBulkData;
    }
}
