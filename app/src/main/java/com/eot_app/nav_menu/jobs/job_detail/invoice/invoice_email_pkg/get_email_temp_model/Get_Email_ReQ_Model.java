package com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_email_pkg.get_email_temp_model;

public class Get_Email_ReQ_Model {

    String invId;
    String compId;
    String isProformaInv = "0";

    public void setIsProformaInv(String isProformaInv) {
        this.isProformaInv = isProformaInv;
    }

    public Get_Email_ReQ_Model(String invId, String compId) {
        this.invId = invId;
        this.compId = compId;
    }
}
