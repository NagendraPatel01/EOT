package com.eot_app.lat_lng_sync_pck;

import android.os.BatteryManager;

import com.eot_app.utility.AppUtility;
import com.eot_app.utility.EotApp;

import static android.content.Context.BATTERY_SERVICE;

public class AddFWLatLng_Model {
    String usrId;
    String lat;
    String lng;
    int btryStatus;
    String dateTime;


    public AddFWLatLng_Model(String usrId, String lat, String lng) {
        BatteryManager bm = (BatteryManager) (EotApp.getAppinstance()).getSystemService(BATTERY_SERVICE);
        this.usrId = usrId;
        this.lat = lat;
        this.lng = lng;
        this.btryStatus = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
       this.dateTime = AppUtility.getDateByFormat("yyyy-MM-dd hh:mm:ss a");

    }
}
