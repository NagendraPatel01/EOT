package com.eot_app.lat_lng_sync_pck;

import android.location.Location;
import android.os.BatteryManager;

import com.eot_app.utility.AppUtility;
import com.eot_app.utility.EotApp;

import static android.content.Context.BATTERY_SERVICE;

public class GPSModel {
    Location location;
    int btryStatus;
    String dateTime;


    public GPSModel(Location location) {
        BatteryManager bm = (BatteryManager) (EotApp.getAppinstance()).getSystemService(BATTERY_SERVICE);
        this.location = location;
        this.btryStatus = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        this.dateTime = AppUtility.getDateByFormat("yyyy-MM-dd hh:mm:ss a");
    }

    public Location getLocation() {
        return location;
    }

    public int getBtryStatus() {
        return btryStatus;
    }

    public String getDateTime() {
        return dateTime;
    }
}
