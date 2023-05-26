package com.eot_app.utility;

import android.os.Build;

import com.eot_app.BuildConfig;
import com.microsoft.appcenter.analytics.Analytics;
import java.util.HashMap;

public class AppCenterLogs {

    public static void  addLogToAppCenterOnAPIFail(String tag,String apiname,String errorMsg, String screen,String responsecode) {

        try {
            HashMap<String,String> properties =new  HashMap<String, String>();

            properties.put("userName", App_preference.getSharedprefInstance().getLoginRes().getUsername());
            properties.put("servername", AppUtility.getBaserUrlforlog());
            properties.put("responsecode", responsecode);
            properties.put("apiname",apiname);
            properties.put("errorMessage", errorMsg);
            properties.put("screen", screen);
            properties.put("device", Build.MODEL + ", " + Build.VERSION.RELEASE
                    + " " + Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName());
            properties.put("appVersion", BuildConfig.VERSION_NAME);
            Analytics.trackEvent(tag+": User:-> "+App_preference.getSharedprefInstance().getLoginRes().getUsername()+", Compid:-> "+App_preference.getSharedprefInstance().
                    getLoginRes().getCompId(), properties);
        } catch  (Exception e) {
            e.printStackTrace();
        }
    }
}
