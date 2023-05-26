package com.eot_app.nav_menu.jobs.add_job.addjobmodel;

import java.util.ArrayList;

/**
 * Created by Mahendra Dabi on 29/3/21.
 */
public class JOBRecurmodel {
     final String mode;
     final String startDate;
     final String numOfWeeks;
     final String endDate;
     final String occurences;
     final String weekDays;
     final String endRecurMode;
     String stopRecur;

    public JOBRecurmodel( String mode, String startDate, String numOfWeeks, String endDate, String occurences, String weekDays, String endRecurMode) {
        this.mode = mode;
        this.startDate = startDate;
        this.numOfWeeks = numOfWeeks;
        this.endDate = endDate;
        this.occurences = occurences;
        this.weekDays = weekDays;
        this.endRecurMode = endRecurMode;
        this.stopRecur = "0";

    }
}
