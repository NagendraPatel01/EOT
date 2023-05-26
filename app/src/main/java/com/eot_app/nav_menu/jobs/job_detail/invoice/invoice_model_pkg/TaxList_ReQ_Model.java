package com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_model_pkg;

public class TaxList_ReQ_Model {
     String compId;
     String isactive;
     String show_Invoice;
     int limit;
     int index;
     String dateTime;


    public TaxList_ReQ_Model(String compId, int limit, int index, String dateTime) {
        this.compId = compId;
        this.limit = limit;
        this.index = index;
        this.show_Invoice = "1";
        this.dateTime = dateTime;
    }
}
