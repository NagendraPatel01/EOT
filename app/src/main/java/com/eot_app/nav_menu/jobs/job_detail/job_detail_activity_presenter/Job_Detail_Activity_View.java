package com.eot_app.nav_menu.jobs.job_detail.job_detail_activity_presenter;

import com.eot_app.nav_menu.jobs.job_detail.addinvoiveitem2pkg.model.InvoiceItemDataModel;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_email_pkg.get_email_temp_model.InvoiceEmaliTemplate;

import java.util.ArrayList;
import java.util.List;

public interface Job_Detail_Activity_View {

    void finishActivityWithSetResult();

    void onSessionExpire(String message);

    void setInvoiceDetails();

    void moreInvoiceOption(List<InvoiceItemDataModel> data);

    void onGetPdfPath(String pdfPath);

    void onSignatureUpload(String signaturePath, String msg);
    void setInvoiceTmpList(ArrayList<InvoiceEmaliTemplate> templateList);
    // void setBooleanForGenerateInvoice(boolean CheckInvoice);
}
