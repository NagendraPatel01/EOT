package com.eot_app.nav_menu.addleave;

import com.eot_app.utility.App_preference;

import java.util.ArrayList;

/**
 * Created by Sona-11 on 28/10/21.
 */
public class LeaveReqModel {
    final String reason;
    final String note;
    final String startDateTime;
    final String finishDateTime;
    final int ltId;
    final ArrayList<String> usrId = new ArrayList<>();

    public LeaveReqModel(String reason, String note, String startDateTime, String finishDateTime,int ltId) {
        this.usrId.add(App_preference.getSharedprefInstance().getLoginRes().getUsrId());
        this.reason = reason;
        this.note = note;
        this.ltId=ltId;
        this.startDateTime = startDateTime;
        this.finishDateTime = finishDateTime;
    }
}
