package com.eot_app.nav_menu.addleave;

import java.io.Serializable;

public class LeaveUserModel implements Comparable, Serializable {

    private String ltId;
    private String leaveType;

    public String getLtId() {
        return ltId;
    }

    public void setLtId(String ltId) {
        this.ltId = ltId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
