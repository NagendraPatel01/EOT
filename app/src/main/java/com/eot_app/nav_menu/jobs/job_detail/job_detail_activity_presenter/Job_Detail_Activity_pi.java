package com.eot_app.nav_menu.jobs.job_detail.job_detail_activity_presenter;

import java.io.File;

public interface Job_Detail_Activity_pi {

    void getItemFromServer(final String jobId);

    void getInvoiceItemList();

    void printJobCard(String jobId,String tempId,String techId);

    void uploadCustomerSign(String jobId, File file);
    void getJobCardetemplateList();

}
