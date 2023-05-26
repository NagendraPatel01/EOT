package com.eot_app.services;


import com.eot_app.activitylog.LogModelActivity;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.CompletionDetailsPost;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by geet-pc on 21/5/18.
 */

public interface Service_apis {

    String getApiUrl = "AuthenticationController/getApiUrl";
    String mobileLogin = "AuthenticationController/mobileLogin";
    String updateUserAppVersion="UserController/updateUserAppVersion";
    String getMobileDefaultSettings = "AuthenticationController/getMobileDefaultSettings";
    String logout = "UserController/logout";
    String updateClient = "CompanyController/updateClient";
    String getJobTitleList = "JobController/getJobTitleList";
    String addClientContact = "CompanyController/addClientContact";
    String updateClientContact = "CompanyController/updateClientContact";
    String addClientSite = "CompanyController/addClientSite";
    String updateClientSite = "CompanyController/updateClientSite";
    String getCompanyAccountTypeList = "CompanyController/getCompanyAccountTypeList";
    String getReferenceList = "CompanyController/getReferenceList";
    String changeJobStatus = "JobController/changeJobStatus";
    String addJob = "JobController/addJob";
    String addClient = "CompanyController/addClient";
    String getTermsCondition = "CompanyController/getQuatSetting";
    String forgotPasswordKey = "AuthenticationController/forgotPasswordKey";
    String forgotPassword = "AuthenticationController/forgotPassword";
    String forgotPasswordReset = "AuthenticationController/forgotPasswordReset";
    String getJobStatusHistoryMobile = "JobController/getJobStatusHistoryMobile";
    String get_po_list = "PurchaseOrderController/getPurchaseOrderList";
    String getFieldWorkerList = "UserController/getFieldWorkerList";
     //Old api
     //    String getUserJobList = "JobController/getUserJobList";
    // new api for eq components
    String getUserJobList = "JobController/getUserJobListNew";
    String getTagList = "JobController/getTagList";
    String getLoginReport = "UserController/generateCheckInOutPDF";
    String getClientSink = "CompanyController/getClientSink";
    String getClientContactSink = "CompanyController/getClientContactSink";
    String getClientSiteSink = "CompanyController/getClientSiteSink";
    String getAllEquipments = "AssetsController/getEquipmentList";
    String getLocationList = "CompanyController/getLocationList";
    String errorLogMail = "AuthenticationController/errorLogMail";
    String getFormList = "JobController/getFormList";
    String getQuestionsByParentId = "JobController/getQuestionsByParentId";
    String addAnswer = "JobController/addAnswer";
    String updateJObDocument = "JobController/updateJobDocument";
    String getJobAttachments = "JobController/getJobAttachments";
    String addFWlatlong = "UserController/addFWlatlong2";
    String sendNotificationToUser = "UserController/sendNotificationToUser";
    String getItemList = "InvoiceController/getItemList";
    String getInvoiceDetail = "InvoiceController/getInvoiceDetail";
    String updateInvoice = "InvoiceController/updateInvoice";
    String getTaxList = "CompanyController/getTaxList";
    String addInvoice = "InvoiceController/addInvoice";
    String postPaymentInvoice = "InvoiceController/invoicePaymentRecieve";
    String getInvoiceEmailTemplate = "InvoiceController/getInvoiceEmailTemplate";
    String sendInvoiceEmailTemplate = "InvoiceController/sendInvoiceEmailTemplate";
    String generateInvoice = "InvoiceController/generateInvoice";
    String getCompanySettingDetails = "CompanyController/getCompanySettingDetails";
    String generateInvoicePDF = "InvoiceController/generateInvoicePDF";
    String addCheckInOutIime = "UserController/addCheckInOutIime";
    String setCompletionNotes = "jobController/setCompletionNotes";
    String addCompany = "AuthenticationController/addCompany";
    String generateJobCardPDF = "JobController/generateJobCardPDF";
    /**
     * Autation APi's
     */
    String getAdminQuoteList = "QuotationController/getAdminQuoteList";
    String getQuotationInvoiceDetail = "QuotationController/getQuoteDetailForMobile";
    String addQuotationForMobile = "QuotationController/addUpdateQuotationForMobile";
    String addQuotItemForMobile = "QuotationController/addQuotItemForMobile";
    String deleteQuotItemForMobile = "QuotationController/deleteQuotItemForMobile";
    String updateQuotItemForMobile = "QuotationController/updateQuotItemForMobile";
    String getQuotationEmailTemplate = "QuotationController/getQuotationEmailTemplate";
    String sendQuotationEmailTemplate = "QuotationController/sendQuotationEmailTemplate";
    String generateQuotPDF = "QuotationController/generateQuotPDF";
    String convertQuotationToJob = "QuotationController/convertQuotationToJob";
    /***Api's for Audit Module**/
    String getAuditList = "AssetsController/getAuditUserList";
    String getEquipmentList = "AssetsController/getAuditEquipmentList";
    String addNewRemark = "AssetsController/updateAuditEquipment";
    String updateAuditStatus = "AssetsController/changeAuditStatus";
    String getEquipmentInfoByBarcode = "AssetsController/getEquipmentInfoByBarcode";
    //String getAuditAttachments = "AssetsController/getAuditAttachments";
    String getAuditAttachments = "AssetsController/getAuditAttachmentApi";
    String getContractList = "ContractController/contractSynch";
    String addAudit = "AssetsController/addAudit";
    String getClientSiteList = "CompanyController/getClientSiteList";
    String addAuditEquipment = "AssetsController/addAuditEquipment";
    /**
     * one to one chat
     **/
    String groupUserListForChat = "UserController/groupUserListForChat";
    String insertUserActivity = "UserController/insertActivityLog";
    String insertUserActivityBulk = "UserController/insertActivityLogInBulk";

    /***Expense Module**/
    String getExpenseList = "ExpenseController/getExpenseList";
    String getCategoryList = "ExpenseController/getCategoryList";
    String getExpenseDetail = "ExpenseController/getExpenseDetail";
    String getExpenseTagList = "ExpenseController/getExpenseTagList";
    String getExpenseStatus = "ExpenseController/getExpenseStatus";
    String deleteExpenseReceipt = "ExpenseController/deleteExpenseReceipt";

    /***Appointment Module**/
    String getAppointmentUserList = "LeadController/getAppointmentUserList";
    String addAppointment = "LeadController/addAppointment";
    String updateAppointment = "LeadController/updateAppointment";
    String getAppointmentDetail = "LeadController/getAppointmentDetail";
    String generateJobDocumentPDF = "JobController/generateJobDocumentPDF";
    String getJobDocEmailTemplate = "JobController/getJobDocEmailTemplate";
    String sendJobDocEmailTemplate = "JobController/sendJobDocEmailTemplate";

    String getJobCardEmailTemplate = "JobController/getJobCardEmailTemplate";
    String sendJobCardEmailTemplate = "JobController/sendJobCardEmailTemplate";

    /***Client industry**/
    String getIndustryList = "CompanyController/getIndustryList";

    String getItemFromJob = "JobController/getItemFromJob";
    //equipment details
    String getEquipmentComponents = "AssetsController/getEquComponentList";
    String getItemForEquipment = "JobController/getAllEquipmentItems";

    String addItemOnJob = "JobController/addItemOnJob";
    String deleteItemFromJob = "JobController/deleteItemFromJob";

    /**
     * give Single Item But in Array
     ******/
    String updateItemInJobMobile = "JobController/updateItemInJobMobile";
    String updateItemQuantity = "JobController/updateItemQuantity";


    String getInvoiceDetailMobile = "InvoiceController/getInvoiceDetailMobile";
    String getSubscriptionData = "companyController/getSubscriptionData";
    String addInvoiceForMobile = "InvoiceController/addInvoiceForMobile";

    String rescheduleJob = "jobController/updateJobSchedule";

    String addAuditReport = "AssetsController/addAuditReport";
    String getFormDetail = "JobController/getFormDetail";
    String getEquGroupList = "AssetsController/getEquGroupList";
    String getEquCategoryList = "AssetsController/getEquCategoryList";
    String getBrandList = "AssetsController/getEquBrandList";

    String setCompanyDefaultSetting = "CompanyController/setCompanyDefaultSetting";

    String login = "AuthenticationController/login";

    String resendVerificationCode = "AuthenticationController/resendVerificationCode";
    String verifyEmail = "AuthenticationController/verifyEmail";

    String verifyCompanyCode = "AuthenticationController/verifyCompanyCode";

    String getEquipmentStatus = "AssetsController/getEquipmentStatus";
    String generateBarcodeWithGiveCode = "AssetsController/generateBarcodeUsingGivenCode";


    /***Equipment Module**/
//    String getEquipmentDetail = "AssetsController/getEquipmentDetail";
    String getEquAuditSchedule = "AssetsController/getEquAuditSchedule";
    String getAuditDetail = "AssetsController/getAuditDetail";
    String getJobDetail = "JobController/getJobDetail";
    String getJobCompletionNOte = "JobController/getJobCompletionDetail";
    String getContractEquipmentList = "ContractController/getContractEquipmentList";
    String addClientForLinkEquipment = "JobController/addClientForLinkEquipment";

    /***client WorkHistory Module**/
    String getAdminJobList = "JobController/getAdminJobList";
    String getAppointmentAdminList = "LeadController/getAppointmentAdminList";
    String getAuditAdminList = "AssetsController/getAuditAdminList";
    String getAppointmentAttachment = "LeadController/getAppointmentAttachment";
    String dailyJobRecurrenceResult = "RecurController/DailyJobRecurrenceResult";
    String weeklyJobRecurrenceResult = "RecurController/weeklyJobRecurrenceResult";
    String monthlyjobRecurrenceResult = "RecurController/MonthlyjobRecurrenceResult";
    String deleteRecur = "RecurController/deleteRecur";

    String getClientDetail = "CompanyController/getClientDetail";
    String deleteDocument = "JobController/deleteDocument";
    String sendNotificationToCpClient = "UserController/sendNotificationToCpClient";
    String generateUserTimesheetPDF = "UserController/generateUserTimesheetPDF";
    String addLeave = "UserController/addLeave";
    String getShiftList = "CompanyController/getShiftList";
    String getUserLeaveList = "UserController/getUserLeaveList";
    String getLeaveTypeList="UserController/getLeaveTypeList";
    String getInvoiceTemplates = "InvoiceController/getInvoiceTemplates";
    String getQuotaTemplates = "QuotationController/getQuotaTemplates";
    String getJobCardTemplates = "JobController/getJobCardTemplates";
    String getAddLeadFormList = "CompanyController/getAddFormsList";
    String add_lead = "LeadController/addLead";
    String add_completion_detail = "JobController/addCompletionDetail";
    // manage JobStatus with api
    String getJobStatus = "JobController/getJobStatusList";
    String get_supplier_list = "SupplierController/getSupplierList";


    //no token required & header
    @POST
    Observable<JsonObject> service_Call_Without_Token(@Url String url,
                                                      @Header("User-Time-Zone") String timezone,
                                                      @Body JsonObject request);

    @Multipart
    @POST("JobController/addJobFeedback")
    Observable<ResponseBody>
    uploadFileWithPartMap(@HeaderMap Map<String, String> map,
                          @Part("usrId") RequestBody userId,
                          @Part("jobId") RequestBody jobId,
                          @Part("des") RequestBody des,
                          @Part("rating") RequestBody rating,
                          @Part MultipartBody.Part file);

    @Multipart
    @POST("AssetsController/addAuditReport")
    Observable<JsonObject>
    addAuditFeedback(@HeaderMap Map<String, String> map,
                     @Part("usrId") RequestBody userId,
                     @Part("audId") RequestBody jobId,
                     @Part("des") RequestBody des,
                     @Part MultipartBody.Part custSign,
                     @Part MultipartBody.Part auditSign
    );

    //new insertLogService
    @POST(insertUserActivityBulk)
    @Headers({"Content-Type: application/json"})
    Observable<JsonObject> insertBulkLog(@HeaderMap Map<String, String> map,
                                         @Body LogModelActivity obj);

    //new common services
    @POST
    @Headers({"Content-Type: application/json"})
    Observable<JsonObject> eotServiceCall(@Url String url,
                                          @HeaderMap Map<String, String> map,
                                          @Body JsonObject request);

    @POST
    @Headers({"Content-Type: application/json"})
    Observable<JsonObject> eotServiceCall2(@Url String url,
                                           @HeaderMap Map<String, String> map);

    @Multipart
    @POST(addCheckInOutIime)
    Observable<JsonObject>
    postCheckInCheckOut(@HeaderMap Map<String, String> map,
//                        @Body JsonObject request,
                        @Part("usrId") RequestBody usrId,
                        @Part("time") RequestBody time,
                        @Part("checkId") RequestBody checkId,
                        @Part("checkType") RequestBody checkType,
                        @Part("lat") RequestBody lat,
                        @Part("lng") RequestBody lng,
                        @Part("desc") RequestBody desc,
                        @Part MultipartBody.Part attachment);

    @Multipart
    @POST("JobController/uploadDocument")
    Observable<JsonObject>
    uploadDocements(@HeaderMap Map<String, String> map,
                    @Part("jobId") RequestBody jobId,
                    @Part("usrId") RequestBody userId,
                    @Part("des") RequestBody des,
                    @Part("type") RequestBody type,
                    @Part("docNm") RequestBody docNm,
                    @Part("isAddAttachAsCompletionNote") RequestBody isAddAttachAsCompletionNote,
                    @Part MultipartBody.Part file);

    @Multipart
    @POST("jobController/uploadJobCardSign")
    Observable<JsonObject>
    uploadCustomerSignature(@HeaderMap Map<String, String> map,
                            @Part("jobId") RequestBody jobId,
                            @Part MultipartBody.Part file);

    /*appointment attachment*/
    @Multipart
    @POST("LeadController/uploadAppmtDocument")
    Observable<JsonObject>
    uploadAppointmentDocements(@HeaderMap Map<String, String> map,
                               @Part("appId") RequestBody appId,
                               @Part("usrId") RequestBody userId,
                               @Part("des") RequestBody des,
                               @Part("type") RequestBody type,
                               @Part MultipartBody.Part file);

    @Multipart
    @POST("AssetsController/uploadAuditDocument")
    Observable<JsonObject>
    uploadAuditDocements(@HeaderMap Map<String, String> map,
                         @Part("audId") RequestBody jobId,
                         @Part("usrId") RequestBody userId,
                         @Part("docNm") RequestBody docNm,
                         @Part("deviceType") RequestBody type,
                         @Part MultipartBody.Part file);


    /**
     * submit/update Equipment remark
     ****/
    @Multipart
    @POST(addNewRemark)
    Observable<JsonObject>
    uploadAuditRemarkWithDocument(@HeaderMap Map<String, String> map,
                                  @Part("audId") RequestBody audId,
                                  @Part("equId") RequestBody equId,
                                  @Part("usrId") RequestBody userId,
                                  @Part("remark") RequestBody remark,
                                  @Part("status") RequestBody status,
                                  @Part("lat") RequestBody lat,
                                  @Part("lng") RequestBody lng,
                                  @Part("isJob") RequestBody isJob,
                                  @Part List<MultipartBody.Part> file,
                                  @Part List<MultipartBody.Part> docAns,
                                  @Part("docQueIdArray") RequestBody docQueIdArray,
                                  @Part("answerArray") RequestBody answerArray,
                                  @Part List<MultipartBody.Part> signAns,
                                  @Part("signQueIdArray") RequestBody signQueIdArray,
                                  @Part("equStatus") RequestBody equStatus
    );


    @Multipart
    @POST("AssetsController/uploadAuditEquipmentAttachments")
    Observable<JsonObject>
    uploadSingleAttchmentForJobAudit(@HeaderMap Map<String, String> map,
                                     @Part("audId") RequestBody audId,
                                     @Part("equId") RequestBody equId,
                                     @Part("usrId") RequestBody usrId,
                                     @Part("isJob") RequestBody isJob,
                                     @Part MultipartBody.Part ja);


    @Multipart
    @POST("UserController/uploadChatDocument")
    Observable<JsonObject> uploadChatDocements(@HeaderMap Map<String, String> map,
                                               @Part MultipartBody.Part cd);


    /*upload equipment details Attchment*/
    @Multipart
    @POST("AssetsController/uploadEquUsrManualDoc")
    Observable<JsonObject> uploadEquDetailAttchment(@HeaderMap Map<String, String> map,
                                                    @Part("equId") RequestBody equId,
                                                    @Part MultipartBody.Part usrManualDoc);

    @Multipart
    @POST("ExpenseController/addExpense")
    Observable<JsonObject> addExpense(@HeaderMap Map<String, String> map,
                                      @Part MultipartBody.Part[] receipt,
                                      @Part("jobId") RequestBody jobId,
                                      @Part("cltId") RequestBody cltId,
                                      @Part("usrId") RequestBody usrId,
                                      @Part("name") RequestBody name,
                                      @Part("amt") RequestBody amt,
                                      @Part("dateTime") RequestBody dateTime,
                                      @Part("category") RequestBody category,
                                      @Part("tag") RequestBody tag,
                                      @Part("status") RequestBody status,
                                      @Part("des") RequestBody des,
                                      @Part("comment") RequestBody comment);


    /****Job Custom Form***/
    @Multipart
    @POST("JobController/addAnswerWithAttachment")
    Observable<JsonObject> submitCustomFormAns(@HeaderMap Map<String, String> map,
                                               @Part List<MultipartBody.Part> signAns,
                                               @Part List<MultipartBody.Part> docAns,
                                               @Part("signQueIdArray") RequestBody signQueIdArray,
                                               @Part("docQueIdArray") RequestBody docQueIdArray,
                                               @Part("answer") RequestBody answer,
                                               @Part("usrId") RequestBody usrId,
                                               @Part("frmId") RequestBody frmId,
                                               @Part("jobId") RequestBody jobId,
                                               @Part("isdelete") RequestBody isdelete,
                                               @Part("type") RequestBody type);


    @Multipart
    @POST("ExpenseController/updateExpense")
    Observable<JsonObject> updateExpense2(@HeaderMap Map<String, String> map,
                                          @Part("jobId") RequestBody jobId,
                                          @Part("cltId") RequestBody cltId,
                                          @Part("usrId") RequestBody usrId,
                                          @Part("name") RequestBody name,
                                          @Part("amt") RequestBody amt,
                                          @Part("dateTime") RequestBody dateTime,
                                          @Part("category") RequestBody category,
                                          @Part("tag") RequestBody tag,
                                          @Part("status") RequestBody status,
                                          @Part("des") RequestBody des,
                                          @Part("comment") RequestBody comment,
                                          @Part("expId") RequestBody expId,
                                          @Part MultipartBody.Part... receipt);

    @POST()
    Observable<JsonObject> getRegionURL(@Url String url,
                                        @Body HashMap<String, String> emailMap);


    @POST()
    Observable<JsonObject> userLogin(@Url String url,
                                     @Body JsonObject request_mOdel);

    @POST(add_completion_detail)
    Observable<JsonObject> addCompletionDetails(@HeaderMap Map<String, String> map,
                                                @Body CompletionDetailsPost request_mOdel);


    @GET
    Observable<Response<ResponseBody>> downloadFile(@Url String fileUrl);

    @Multipart
    @POST(addJob)
    Observable<JsonObject>
    addJobWithDocuments(@HeaderMap Map<String, String> map,
                        @Part("pono") RequestBody pono,
                        @Part("poId") RequestBody poId,
                        @Part("parentId") RequestBody parentId,
                        @Part("compId") RequestBody compId,
                        @Part("contrId") RequestBody contrId,
                        @Part("appId") RequestBody appId,
                        @Part("cltId") RequestBody cltId,
                        @Part("siteId") RequestBody siteId,
                        @Part("conId") RequestBody conId,
                        @Part("quotId") RequestBody quotId,
                        @Part("type") RequestBody type,
                        @Part("prty") RequestBody prty,
                        @Part("status") RequestBody status,
                        @Part("athr") RequestBody athr,
                        @Part("kpr") RequestBody kpr,
                        @Part("des") RequestBody des,
                        @Part("inst") RequestBody inst,
                        @Part("schdlStart") RequestBody schdlStart,
                        @Part("schdlFinish") RequestBody schdlFinish,
                        @Part("nm") RequestBody nm,
                        @Part("cnm") RequestBody cnm,
                        @Part("snm") RequestBody snm,
                        @Part("email") RequestBody email,
                        @Part("mob1") RequestBody mob1,
                        @Part("mob2") RequestBody mob2,
                        @Part("adr") RequestBody adr,
                        @Part("city") RequestBody city,
                        @Part("state") RequestBody state,
                        @Part("ctry") RequestBody ctry,
                        @Part("zip") RequestBody zip,
                        @Part("clientForFuture") RequestBody clientForFuture,
                        @Part("siteForFuture") RequestBody siteForFuture,
                        @Part("contactForFuture") RequestBody contactForFuture,
                        @Part("dateTime") RequestBody dateTime,
                        @Part("lat") RequestBody lat,
                        @Part("lng") RequestBody lng,
                        @Part("landmark") RequestBody landmark,
                        @Part("tagData") RequestBody tagdata,
                        @Part("answerArray") RequestBody answerArray,
                        @Part List<MultipartBody.Part> jatIds,
                        @Part List<MultipartBody.Part> memIds,
                        @Part List<MultipartBody.Part> files,
                        @Part List<MultipartBody.Part> equId,
                        @Part("scheduleDisplayText") RequestBody scheduleDisplayText
    );

    @Multipart
    @POST(add_lead)
    Observable<JsonObject>
    addLeadWithDocuments(@HeaderMap Map<String, String> map,
                         @Part("createdby") RequestBody createdby,
                         @Part("afId") RequestBody afId,
//                        @Part("contrId") RequestBody contrId,
                         @Part("cltId") RequestBody cltId,
                         @Part("siteId") RequestBody siteId,
                         @Part("conId") RequestBody conId,
//                        @Part("type") RequestBody type,
                         @Part("status") RequestBody status,
                         @Part("des") RequestBody des,
                         @Part("leadStartDate") RequestBody schdlStart,
                         @Part("leadEndDate") RequestBody schdlFinish,
                         @Part("cnm") RequestBody cnm,
                         @Part("nm") RequestBody nm,
                         @Part("snm") RequestBody snm,
                         @Part("email") RequestBody email,
                         @Part("mob1") RequestBody mob1,
                         @Part("mob2") RequestBody mob2,
                         @Part("adr") RequestBody adr,
                         @Part("city") RequestBody city,
                         @Part("state") RequestBody state,
                         @Part("ctry") RequestBody ctry,
                         @Part("zip") RequestBody zip,
                         @Part("clientForFuture") RequestBody clientForFuture,
                         @Part("siteForFuture") RequestBody siteForFuture,
                         @Part("contactForFuture") RequestBody contactForFuture,
                         @Part("lat") RequestBody lat,
                         @Part("lng") RequestBody lng,
                         @Part("landmark") RequestBody landmark,
                         @Part("answerArray") RequestBody answerArray,
                         @Part("jtId") RequestBody jtId,
                         @Part("source") RequestBody source
    );

    @Multipart
    @POST(addAudit)
    Observable<JsonObject>
    addAuditWithDocuments(@HeaderMap Map<String, String> map,
                          @Part("type") RequestBody type,
                          @Part("cltId") RequestBody cltId,
                          @Part("nm") RequestBody nm,
                          @Part("siteId") RequestBody siteId,
                          @Part("conId") RequestBody conId,
                          @Part("contrId") RequestBody contrId,
                          @Part("parentId") RequestBody parentId,
                          @Part("des") RequestBody des,
                          @Part("status") RequestBody status,
                          @Part("athr") RequestBody athr,
                          @Part("kpr") RequestBody kpr,
                          @Part("schdlStart") RequestBody schdlStart,
                          @Part("schdlFinish") RequestBody schdlFinish,
                          @Part("inst") RequestBody inst,
                          @Part("cnm") RequestBody cnm,
                          @Part("snm") RequestBody snm,
                          @Part("email") RequestBody email,
                          @Part("mob1") RequestBody mob1,
                          @Part("mob2") RequestBody mob2,
                          @Part("adr") RequestBody adr,
                          @Part("city") RequestBody city,
                          @Part("state") RequestBody state,
                          @Part("ctry") RequestBody ctry,
                          @Part("zip") RequestBody zip,
                          @Part List<MultipartBody.Part> memIds,
                          @Part("tagData") RequestBody tagdata,
                          @Part("dateTime") RequestBody dateTime,
                          @Part("lat") RequestBody lat,
                          @Part("lng") RequestBody lng,
                          @Part("landmark") RequestBody landmark,
                          @Part("auditType") RequestBody auditType,
                          @Part("clientForFuture") RequestBody clientForFuture,
                          @Part("siteForFuture") RequestBody siteForFuture,
                          @Part("answerArray") RequestBody answerArray,
                          @Part("contactForFuture") RequestBody contactForFuture,
                          //    @Part("tempId") RequestBody tempId,
                          @Part List<MultipartBody.Part> files);

    @Multipart
    @POST(addQuotationForMobile)
    Observable<JsonObject>
    addQuoteWithDocuments(@HeaderMap Map<String, String> map,
                          @Part("leadId") RequestBody leadId,
                          @Part("appId") RequestBody appId,
                          @Part("cltId") RequestBody cltId,
                          @Part("siteId") RequestBody siteId,
                          @Part("conId") RequestBody conId,
                          @Part("status") RequestBody status,
                          @Part("invDate") RequestBody invDate,
                          @Part("dueDate") RequestBody dueDate,
                          @Part("nm") RequestBody nm,
                          @Part("cnm") RequestBody cnm,
                          @Part("snm") RequestBody snm,
                          @Part("email") RequestBody email,
                          @Part("mob1") RequestBody mob1,
                          @Part("mob2") RequestBody mob2,
                          @Part("adr") RequestBody adr,
                          @Part("city") RequestBody city,
                          @Part("state") RequestBody state,
                          @Part("ctry") RequestBody ctry,
                          @Part("zip") RequestBody zip,
                          @Part("clientForFuture") RequestBody clientForFuture,
                          @Part("siteForFuture") RequestBody siteForFuture,
                          @Part("contactForFuture") RequestBody contactForFuture,
                          @Part List<MultipartBody.Part> jatIds,
                          @Part("des") RequestBody des,
                          @Part("inst") RequestBody inst,
                          @Part("athr") RequestBody athr,
                          @Part("note") RequestBody note,
                          @Part("assignByUser") RequestBody assignByUser,
                          @Part("quotId") RequestBody quotId,
                          @Part("invId") RequestBody invId,
                          @Part("term") RequestBody term,
                          @Part("lat") RequestBody lat,
                          @Part("lng") RequestBody lng,
                          @Part List<MultipartBody.Part> files,
                          @Part("statusComment") RequestBody commets
    );


    @Multipart
    @POST(addAppointment)
    Observable<JsonObject>
    addAppointment(@HeaderMap Map<String, String> map,
                   @Part("cltId") RequestBody cltId,
                   @Part("siteId") RequestBody siteId,
                   @Part("conId") RequestBody conId,
                   @Part("leadId") RequestBody leadId,
                   @Part("des") RequestBody des,
                   @Part("schdlStart") RequestBody schdlStart,
                   @Part("schdlFinish") RequestBody schdlFinish,
                   @Part("nm") RequestBody nm,
                   @Part("cnm") RequestBody cnm,
                   @Part("snm") RequestBody snm,
                   @Part("email") RequestBody email,
                   @Part("mob1") RequestBody mob1,
                   @Part("mob2") RequestBody mob2,
                   @Part("adr") RequestBody adr,
                   @Part("city") RequestBody city,
                   @Part("state") RequestBody state,
                   @Part("ctry") RequestBody ctry,
                   @Part("zip") RequestBody zip,
                   @Part("clientForFuture") RequestBody clientForFuture,
                   @Part("siteForFuture") RequestBody siteForFuture,
                   @Part("contactForFuture") RequestBody contactForFuture,
                   @Part("memIds") RequestBody memIds,
                   @Part List<MultipartBody.Part> files);


    @Multipart
    @POST(addAppointment)
    Observable<JsonObject> addAppoinmentForAttchment(Map<String, String> apiHeaders
            , RequestBody cltId, RequestBody leadId, RequestBody nm, RequestBody schdlStart,
                                                     RequestBody schdlFinish, RequestBody email,
                                                     RequestBody cnm, RequestBody mob1, RequestBody mob2,
                                                     RequestBody adr, RequestBody city, RequestBody ctry,
                                                     RequestBody state, RequestBody zip, RequestBody clientForFuture,
                                                     RequestBody siteForFuture, RequestBody contactForFuture, RequestBody siteId,
                                                     RequestBody conId, RequestBody des, RequestBody snm,
                                                     List<MultipartBody.Part> memIds, List<MultipartBody.Part> appDoc,
                                                     List<MultipartBody.Part> fileList);


    @Multipart
    @POST(updateAppointment)
    Observable<JsonObject>
    updateAppointment(@HeaderMap Map<String, String> map,
                      @Part("appId") RequestBody appId,
                      @Part("cltId") RequestBody cltId,
                      @Part("siteId") RequestBody siteId,
                      @Part("conId") RequestBody conId,
                      @Part("status") RequestBody status,
                      @Part("des") RequestBody des,
                      @Part("schdlStart") RequestBody schdlStart,
                      @Part("schdlFinish") RequestBody schdlFinish,
                      @Part("nm") RequestBody nm,
                      @Part("cnm") RequestBody cnm,
                      @Part("email") RequestBody email,
                      @Part("mob1") RequestBody mob1,
                      @Part("adr") RequestBody adr,
                      @Part("city") RequestBody city,
                      @Part("state") RequestBody state,
                      @Part("ctry") RequestBody ctry,
                      @Part("zip") RequestBody zip,
                      @Part("memIds") RequestBody memIds,
                      @Part List<MultipartBody.Part> files);


    @Multipart
    @POST("AssetsController/addLinkEquFromMob")
    Observable<JsonObject> addEquipment(@HeaderMap Map<String, String> map,
                                        @Part List<MultipartBody.Part> image,
                                        @Part("equnm") RequestBody equnm,
                                        @Part("brand") RequestBody brand,
                                        @Part("mno") RequestBody mno,
                                        @Part("sno") RequestBody sno,
                                        @Part("expiryDate") RequestBody expiryDate,
                                        @Part("manufactureDate") RequestBody manufactureDate,
                                        @Part("purchaseDate") RequestBody purchaseDate,
                                        @Part("status") RequestBody status,
                                        @Part("notes") RequestBody notes,
                                        @Part("isBarcodeGenerate") RequestBody isBarcodeGenerate,
                                        @Part("state") RequestBody state,
                                        @Part("ctry") RequestBody ctry,
                                        @Part("adr") RequestBody adr,
                                        @Part("city") RequestBody city,
                                        @Part("zip") RequestBody zip,
                                        @Part("ecId") RequestBody ecId,
                                        @Part("type") RequestBody type,
                                        @Part("egId") RequestBody egId,
                                        @Part("jobId") RequestBody jobId,
                                        @Part("cltId") RequestBody cltId,
                                        @Part("contrId") RequestBody contrId,
                                        @Part("isPart") RequestBody isPart,
                                        @Part("siteId") RequestBody siteId,
                                        @Part("extraField1") RequestBody extraField1,
                                        @Part("extraField2") RequestBody extraField2,
                                        @Part("barCode") RequestBody barcode,
                                        @Part("installedDate") RequestBody installedDate,
                                        @Part("parentId") RequestBody parentId,
                                        @Part("servIntvalType") RequestBody servIntvalType,
                                        @Part("servIntvalValue") RequestBody servIntvalValue,
                                        @Part("supId") RequestBody supId,
                                        @Part("supplier") RequestBody supplier
    );

    @Multipart
    @POST("AssetsController/addEquipmentUsingItem")
    Observable<JsonObject> convertItemToEquipment(@HeaderMap Map<String, String> map,
                                                  @Part List<MultipartBody.Part> image,
                                                  @Part("equnm") RequestBody equnm,
                                                  @Part("brand") RequestBody brand,
                                                  @Part("mno") RequestBody mno,
                                                  @Part("sno") RequestBody sno,
                                                  @Part("expiryDate") RequestBody expiryDate,
                                                  @Part("manufactureDate") RequestBody manufactureDate,
                                                  @Part("purchaseDate") RequestBody purchaseDate,
                                                  @Part("status") RequestBody status,
                                                  @Part("notes") RequestBody notes,
                                                  @Part("isBarcodeGenerate") RequestBody isBarcodeGenerate,
                                                  @Part("state") RequestBody state,
                                                  @Part("ctry") RequestBody ctry,
                                                  @Part("adr") RequestBody adr,
                                                  @Part("city") RequestBody city,
                                                  @Part("zip") RequestBody zip,
                                                  @Part("ecId") RequestBody ecId,
                                                  @Part("type") RequestBody type,
                                                  @Part("egId") RequestBody egId,
                                                  @Part("jobId") RequestBody jobId,
                                                  @Part("cltId") RequestBody cltId,
                                                  @Part("contrId") RequestBody contrId,
                                                  @Part("itemId") RequestBody itemId,
                                                  @Part("supplier") RequestBody supplier,
                                                  @Part("rate") RequestBody rate,
                                                  @Part("isPart") RequestBody isPart,
                                                  @Part("siteId") RequestBody siteId,
                                                  @Part("invId") RequestBody invId,
                                                  @Part("extraField1") RequestBody extraField1,
                                                  @Part("extraField2") RequestBody extraField2,
                                                  @Part("barCode") RequestBody barcode,
                                                  @Part("parentId") RequestBody parentId,
                                                  @Part("servIntvalType") RequestBody servIntvalType,
                                                  @Part("servIntvalValue") RequestBody servIntvalValue,
                                                  @Part("rplacedEquId") RequestBody rplacedEquId,
                                                  @Part("isEquReplaced") RequestBody isEquReplaced,
                                                  @Part("isCnvtItemParts") RequestBody isCnvtItemParts,
                                                  @Part("installedDate") RequestBody installedDate,
                                                  @Part("supId") RequestBody supId
    );

    @Multipart
    @POST(postPaymentInvoice)
    Observable<JsonObject>
    addPaymentForJob(@HeaderMap Map<String, String> map,
                     @Part("amt") RequestBody amt,
                     @Part("invId") RequestBody invId,
                     @Part("ref") RequestBody ref,
                     @Part("paytype") RequestBody paytype,
                     @Part("paydate") RequestBody paydate,
                     @Part("note") RequestBody note,
                     @Part("isEmailSendOrNot") RequestBody isEmailSendOrNot,
                     @Part("jobId") RequestBody jobId,
                     @Part("isMailSentToClt") RequestBody isMailSentToClt,
                     @Part MultipartBody.Part paymentImg
    );

    @FormUrlEncoded
    @POST
    Observable<JsonObject> registration(@Url String url,
                                        @Field("name") String name,
                                        @Field("email") String email,
                                        @Field("pass") String pass,
                                        @Field("planId") String planId
    );

    @POST
    Observable<JsonObject> verifyCompanyCode(@Url String url,
                                             @Body Map<String, String> stringMap
    );

    @POST
    Observable<JsonObject> verifyemail(@Url String url,
                                       @Body Map<String, String> stringMap
    );


    @POST
    Observable<JsonObject> resendVerificationCode(@Url String url,
                                                  @Body Map<String, String> stringMap

    );


    @POST
    Observable<JsonObject> setAlmostDone(@HeaderMap Map<String, String> map, @Url String url,
                                         @Body Map<String, String> stringMap);

    @POST
    Observable<JsonObject> login(@Url String url,
                                 @Body Map<String, String> stringMap);
}

