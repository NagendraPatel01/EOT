package com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.mvp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.eot_app.nav_menu.audit.audit_list.equipment.model.EquipmentStatus;
import com.eot_app.nav_menu.client.clientlist.client_detail.site.sitelist.editsite.editsitedb.SpinnerCountrySite;
import com.eot_app.nav_menu.jobs.job_db.EquArrayModel;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_detail.addinvoiveitem2pkg.model.InvoiceItemDataModel;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_db.model_pkg.ItembyJobModel;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.clientEqu.ClientEquReq;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.clientEqu.ClientEquRes;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.model_pkg.AddEquReq;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.model_pkg.BrandData;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.model_pkg.GetCatgData;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.model_pkg.GetListModel;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.model_pkg.GetSupplierData;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.model_pkg.GetgrpData;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.model.EquipmentStatusReq;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.Country;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.States;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import static com.eot_app.utility.AppUtility.getJsonObject;

/**
 * Created by Sonam-11 on 30/9/20.
 */
public class AddJobEqu_Pc implements AddJobEqu_Pi {
    private final AddJobEqu_View addJobEquView;
    private final int updatelimit;
    List<ClientEquRes> clientEquRes;
    private List<States> statesList;
    private List<Country> countryList;
    private int count;
    private int updateindex;

    public AddJobEqu_Pc(AddJobEqu_View addJobEquView) {
        this.addJobEquView = addJobEquView;
        clientEquRes = new ArrayList<>();
        this.updatelimit = AppConstant.LIMIT_HIGH;
        this.updateindex = 0;
    }


    @Override
    public boolean RequiredFields(String countryId, String stateId, String equNm) {
        if (equNm.equals("")) {
            addJobEquView.setEquReqError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.equp_nm_req));
            return false;
        } else if (equNm.length() < 3) {
            addJobEquView.setEquReqError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.equp_nm_minimun));
            return false;
        }
        if (!isValidCountry(countryId)) {
            addJobEquView.setCountryError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_select_country_first));
            return false;
        } else if (!isValidState(stateId)) {
            addJobEquView.setStateError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.state_error));
            return false;
        }

        return true;
    }

    @Override
    public void getClientSiteList(String clientId) {
        addJobEquView.setClientSiteList(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel().getSitesByCltId(clientId));
    }


    /****After Convert Item to Equipment Item list Update in Job Dao*/
    public void getClientSiteListServer(String clientId) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow((Context) addJobEquView);
            ClientEquReq model = new ClientEquReq(clientId);
            String data = new Gson().toJson(model);
            ApiClient.getservices().eotServiceCall(Service_apis.getClientSiteList,
                    AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<ClientEquRes>>() {
                                }.getType();
                                List<ClientEquRes> list = new Gson().fromJson(convert, listType);
                                clientEquRes.addAll(list);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            AppUtility.progressBarDissMiss();
                        }

                        @Override
                        public void onComplete() {
                            addJobEquView.setClientSiteListServer(clientEquRes);
                            AppUtility.progressBarDissMiss();
                        }
                    });
        }

    }


    @Override
    public String cntryId(String country) {
        String cId = SpinnerCountrySite.getCountryId(country);
        statesList = SpinnerCountrySite.clientStatesList(cId);
        return cId;
    }

    @Override
    public String statId(String state, String statename) {
        return SpinnerCountrySite.getStateId(state, statename);
    }


    @Override
    public boolean isValidCountry(String country) {
        for (Country ctry : countryList) {
            if (ctry.getId().equalsIgnoreCase(country))
                return true;
        }
        return false;
    }

    @Override
    public boolean isValidState(String state) {
        for (States item : statesList) {
            if (item.getId().equalsIgnoreCase(state))
                return true;
        }

        return false;
    }


    @Override
    public void convertItemToequip(final AddEquReq addEquReq, String path, String barcode, String equipmentId) {
        if (AppUtility.isInternetConnected()) {
            RequestBody equnm = null;
            RequestBody brand = null;
            RequestBody mno = null;
            RequestBody sno = null;
            RequestBody expiryDate = null;
            RequestBody manufactureDate = null;
            RequestBody purchaseDate = null;
            RequestBody status = null;
            RequestBody notes = null;
            RequestBody isBarcodeGenerate = null;
            RequestBody state = null;
            RequestBody ctry = null;
            RequestBody adr = null;
            RequestBody city = null;
            RequestBody zip = null;
            RequestBody ecId = null;
            RequestBody type = null;
            RequestBody egId = null;
            RequestBody jobId = null;
            RequestBody cltId = null;
            RequestBody contrId = null;
            RequestBody itemId = null;
            RequestBody supplier = null;
            RequestBody rate = null;
            RequestBody isPart = null;
            RequestBody siteId = null;
            RequestBody invId = null;
            RequestBody extraField1 = null;
            RequestBody extraField2 = null;
            RequestBody barcodeBody = null;
            RequestBody equipmentIdBody = null;

            RequestBody servIntvalTypeBody = null;
            RequestBody servIntvalValueBody = null;
            RequestBody isEquReplaced = null;
            RequestBody rplacedEquId = null;
            RequestBody installedDateBody = null;
            RequestBody isCnvtItemPartsBody = null;
            RequestBody supplierIdBody = null;
            try {
                equnm = RequestBody.create(addEquReq.getEqunm(), MultipartBody.FORM);
                brand = RequestBody.create(addEquReq.getBrand(), MultipartBody.FORM);
                mno = RequestBody.create(addEquReq.getMno(), MultipartBody.FORM);
                sno = RequestBody.create(addEquReq.getSno(), MultipartBody.FORM);
                supplier = RequestBody.create(addEquReq.getSupplier(), MultipartBody.FORM);
                expiryDate = RequestBody.create(addEquReq.getExpiryDate(), MultipartBody.FORM);
                manufactureDate = RequestBody.create(addEquReq.getManufactureDate(), MultipartBody.FORM);
                purchaseDate = RequestBody.create(addEquReq.getPurchaseDate(), MultipartBody.FORM);
                status = RequestBody.create(addEquReq.getStatus(), MultipartBody.FORM);
                notes = RequestBody.create(addEquReq.getNotes(), MultipartBody.FORM);
                isBarcodeGenerate = RequestBody.create(addEquReq.getIsBarcodeGenerate(), MultipartBody.FORM);
                state = RequestBody.create(addEquReq.getState(), MultipartBody.FORM);
                ctry = RequestBody.create(addEquReq.getCtry(), MultipartBody.FORM);
                adr = RequestBody.create(addEquReq.getAdr(), MultipartBody.FORM);
                city = RequestBody.create(addEquReq.getCity(), MultipartBody.FORM);
                zip = RequestBody.create(addEquReq.getZip(), MultipartBody.FORM);
                ecId = RequestBody.create(addEquReq.getEcId(), MultipartBody.FORM);
                type = RequestBody.create(addEquReq.getType(), MultipartBody.FORM);
                egId = RequestBody.create(addEquReq.getEgId(), MultipartBody.FORM);
                jobId = RequestBody.create(addEquReq.getJobId(), MultipartBody.FORM);
                cltId = RequestBody.create(addEquReq.getCltId(), MultipartBody.FORM);
                contrId = RequestBody.create(addEquReq.getContrId(), MultipartBody.FORM);
                itemId = RequestBody.create(addEquReq.getItemId(), MultipartBody.FORM);
                supplier = RequestBody.create(addEquReq.getSupplier(), MultipartBody.FORM);
                rate = RequestBody.create(addEquReq.getRate(), MultipartBody.FORM);
                isPart = RequestBody.create(addEquReq.getIsPart(), MultipartBody.FORM);
                siteId = RequestBody.create(addEquReq.getSiteId(), MultipartBody.FORM);
                invId = RequestBody.create(addEquReq.getInvId(), MultipartBody.FORM);
                extraField1 = RequestBody.create(addEquReq.getExtraField1(), MultipartBody.FORM);
                extraField2 = RequestBody.create(addEquReq.getExtraField2(), MultipartBody.FORM);
                barcodeBody = RequestBody.create(barcode, MultipartBody.FORM);
                equipmentIdBody = RequestBody.create(equipmentId, MultipartBody.FORM);
                servIntvalTypeBody = RequestBody.create(addEquReq.getServIntvalType(), MultipartBody.FORM);
                servIntvalValueBody = RequestBody.create(addEquReq.getServIntvalValue(), MultipartBody.FORM);
                isEquReplaced = RequestBody.create(addEquReq.getIsEquReplaced(), MultipartBody.FORM);
                rplacedEquId = RequestBody.create(addEquReq.getRplacedEquId(), MultipartBody.FORM);
                installedDateBody = RequestBody.create(addEquReq.getInstalledDate(), MultipartBody.FORM);
                isCnvtItemPartsBody = RequestBody.create(addEquReq.getIsCnvtItemParts(), MultipartBody.FORM);
                supplierIdBody = RequestBody.create(addEquReq.getSupId(), MultipartBody.FORM);

            } catch (Exception e) {
                e.printStackTrace();
            }


            String mimeType = "";
            MultipartBody.Part body = null;
            List<MultipartBody.Part> filesList = new ArrayList<>();
            if (!TextUtils.isEmpty(path)) {
                File file1 = new File(path);
                if (file1 != null) {
                    mimeType = URLConnection.guessContentTypeFromName(file1.getName());
                    if (mimeType == null) {
                        mimeType = file1.getName();
                    }
                    RequestBody requestFile = RequestBody.create(file1, MediaType.parse(mimeType));
                    // MultipartBody.Part is used to send also the actual file name
                    body = MultipartBody.Part.createFormData("image[]", file1.getName() + path.substring(path.lastIndexOf(".")), requestFile);
                    filesList.add(body);
                }
            }


            AppUtility.progressBarShow(((Context) addJobEquView));
            final RequestBody finalJobId = jobId;
            ApiClient.getservices().convertItemToEquipment(AppUtility.getApiHeaders(),
                    filesList, equnm, brand, mno, sno,
                    expiryDate, manufactureDate, purchaseDate,
                    status, notes, isBarcodeGenerate, state
                    , ctry, adr, city, zip, ecId, type,
                    egId, jobId, cltId, contrId,
                    itemId, supplier, rate, isPart, siteId, invId, extraField1,
                    extraField2, barcodeBody, equipmentIdBody,servIntvalTypeBody,
                    servIntvalValueBody,rplacedEquId,isEquReplaced,isCnvtItemPartsBody,installedDateBody,supplierIdBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                updateEquipmentCount(addEquReq.getJobId());
                                updateJobItemData(addEquReq.getJobId(), LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));

                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                addJobEquView.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            } else {
                                addJobEquView.setEquReqError(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                                //addJobEquView.finishActivity();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG : error----", e.getMessage());
                            AppUtility.progressBarDissMiss();
                            addJobEquView.finishActivity();
                        }

                        @Override
                        public void onComplete() {
                            AppUtility.progressBarDissMiss();
                            Log.e("TAG onComplete------", "onComplete");
                        }
                    });
        } else {
            netWork_erroR();
        }
    }

    private void updateEquipmentCount(String jobId) {
        refreshList(jobId);
    }


    /******First time convert Item to equipment Flag refreshing*****/
    public void refreshList(final String jobId) {

        if (AppUtility.isInternetConnected()) {

            HashMap<String, String> auditListRequestModel = new HashMap<>();
            auditListRequestModel.put("limit", ""+updatelimit);
            auditListRequestModel.put("index", ""+updateindex);
            auditListRequestModel.put("audId", ""+jobId);
            auditListRequestModel.put("isJob", "1");
            auditListRequestModel.put("isParent", "1");

//            AuditEquipmentRequestModel auditListRequestModel = new AuditEquipmentRequestModel(auditID,
//                    updatelimit, updateindex, "");
//            auditListRequestModel.setIsJob(1);
            AppUtility.progressBarShow((Context) addJobEquView);

            String data = new Gson().toJson(auditListRequestModel);
            ApiClient.getservices().eotServiceCall(Service_apis.getEquipmentList,
                    AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            Log.e("", "");
                            try {
//                                Log.d("mahi", jsonObject.toString());
                                AppUtility.progressBarDissMiss();
                                if (jsonObject.get("success").getAsBoolean()) {
                                    count = jsonObject.get("count").getAsInt();
                                    String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                    Type listType = new com.google.common.reflect.TypeToken<List<EquArrayModel>>() {
                                    }.getType();
                                    List<EquArrayModel> data = new Gson().fromJson(convert, listType);
                                    if (data != null && data.size() > 0) {
                                        Log.e("EquipmentList::",new Gson().toJson(data));
                                        updateEquipmentDataInDb(jobId, data);
                                    }

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("", "");
                            Log.e("TAG", e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                            if ((updateindex + updatelimit) <= count) {
                                updateindex += updatelimit;
                                refreshList(jobId);
                            } else {
                                if (count != 0) {
                                    Log.v("MainSync", "startJobSyncTime AddJob" + " --" + App_preference.getSharedprefInstance().getJobSyncTime());
                                    App_preference.getSharedprefInstance().setJobSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                }
                                updateindex = 0;
                                count = 0;
                                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobByIsDelete();
                            }
                        }
                    });
        }
    }

    private void updateEquipmentDataInDb(String jobId, List<EquArrayModel> data) {
        try {
            Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobId);
            /* *****Notify JOB overView for Equipmetn Added first time ****/
            if (job.getEquArray() != null && job.getEquArray().size() == 0) {
                job.setEquArray(data);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobequipArray(jobId, data);
                EotApp.getAppinstance().getJobFlagOverView();
            } else {
                /* **Refresh job Table in Exiting Equ. lisy***/
                job.setEquArray(data);
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobequipArray(jobId, data);
                if (job.getEquArray() != null && job.getEquArray().size() == 0)
                    EotApp.getAppinstance().getJobFlagOverView();
            }
            EotApp.getAppinstance().getNotifyForEquipmentCount();
            EotApp.getAppinstance().getNotifyForEquipmentCountRemark();
            EotApp.getAppinstance().getNotifyForEquipmentCountList();
            Log.e("EquipmentListLocal::",new Gson().toJson(job.getEquArray()));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /****After Convert Item to Equipment Item list Update in Job Dao*/
    private void updateJobItemData(final String jobId, final String msg) {
        if (AppUtility.isInternetConnected()) {
            ItembyJobModel model = new ItembyJobModel(jobId);//, App_preference.getSharedprefInstance().getJobSyncTime()
            String data = new Gson().toJson(model);
            ApiClient.getservices().eotServiceCall(Service_apis.getItemFromJob,
                    AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new com.google.common.reflect.TypeToken<List<InvoiceItemDataModel>>() {
                                }.getType();
                                List<InvoiceItemDataModel> data = new Gson().fromJson(convert, listType);
                                if (data.size() > 0) {
                                    addgetItemFromJobToDB(data, jobId);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            addJobEquView.addExpenseSuccesFully(msg);
                        }
                    });
        }
    }

    private void addgetItemFromJobToDB(List<InvoiceItemDataModel> data, String jobId) {
        if (data != null && data.size() > 0) {
            AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().updateJobitems(jobId, data);
            EotApp.getAppinstance().getJobFlagOverView();
            EotApp.getAppinstance().getNotifyForItemCount();
        }
    }

    @Override
    public void addNewEquipment(AddEquReq addEquReq, String path, String barcode, String installedDate, String equipmentId) {
        if (AppUtility.isInternetConnected()) {
            RequestBody equnm = null;
            RequestBody brand = null;
            RequestBody mno = null;
            RequestBody sno = null;
            RequestBody expiryDate = null;
            RequestBody manufactureDate = null;
            RequestBody purchaseDate = null;
            RequestBody status = null;
            RequestBody notes = null;
            RequestBody isBarcodeGenerate = null;
            RequestBody state = null;
            RequestBody ctry = null;
            RequestBody adr = null;
            RequestBody city = null;
            RequestBody zip = null;
            RequestBody ecId = null;
            RequestBody type = null;
            RequestBody egId = null;
            RequestBody jobId = null;
            RequestBody cltId = null;
            RequestBody contrId = null;
            RequestBody isPart = null;
            RequestBody siteId = null;
            RequestBody extraField1 = null;
            RequestBody extraField2 = null;
            RequestBody barcodeBody = null;
            RequestBody installedDateBody = null;
            RequestBody equipmentIdBody = null;
            RequestBody servIntvalTypeBody = null;
            RequestBody servIntvalValueBody = null;
            RequestBody supplier = null;
            RequestBody supplierId = null;
            //  RequestBody invId = null;
            try {
                equnm = RequestBody.create(addEquReq.getEqunm(), MultipartBody.FORM);
                brand = RequestBody.create(addEquReq.getBrand(), MultipartBody.FORM);
                mno = RequestBody.create(addEquReq.getMno(), MultipartBody.FORM);
                sno = RequestBody.create(addEquReq.getSno(), MultipartBody.FORM);
                supplier = RequestBody.create(addEquReq.getSupplier(), MultipartBody.FORM);
                supplierId = RequestBody.create(addEquReq.getSupId(), MultipartBody.FORM);
                expiryDate = RequestBody.create(addEquReq.getExpiryDate(), MultipartBody.FORM);
                manufactureDate = RequestBody.create(addEquReq.getManufactureDate(), MultipartBody.FORM);
                purchaseDate = RequestBody.create(addEquReq.getPurchaseDate(), MultipartBody.FORM);
                status = RequestBody.create(addEquReq.getStatus(), MultipartBody.FORM);
                notes = RequestBody.create(addEquReq.getNotes(), MultipartBody.FORM);
                isBarcodeGenerate = RequestBody.create(addEquReq.getIsBarcodeGenerate(), MultipartBody.FORM);
                state = RequestBody.create(addEquReq.getState(), MultipartBody.FORM);
                ctry = RequestBody.create(addEquReq.getCtry(), MultipartBody.FORM);
                adr = RequestBody.create(addEquReq.getAdr(), MultipartBody.FORM);
                city = RequestBody.create(addEquReq.getCity(), MultipartBody.FORM);
                zip = RequestBody.create(addEquReq.getZip(), MultipartBody.FORM);
                ecId = RequestBody.create(addEquReq.getEcId(), MultipartBody.FORM);
                type = RequestBody.create(addEquReq.getType(), MultipartBody.FORM);
                egId = RequestBody.create(addEquReq.getEgId(), MultipartBody.FORM);
                jobId = RequestBody.create(addEquReq.getJobId(), MultipartBody.FORM);
                cltId = RequestBody.create(addEquReq.getCltId(), MultipartBody.FORM);
                contrId = RequestBody.create(addEquReq.getContrId(), MultipartBody.FORM);
                isPart = RequestBody.create(addEquReq.getIsPart(), MultipartBody.FORM);
                siteId = RequestBody.create(addEquReq.getSiteId(), MultipartBody.FORM);
                extraField1 = RequestBody.create(addEquReq.getExtraField1(), MultipartBody.FORM);
                extraField2 = RequestBody.create(addEquReq.getExtraField2(), MultipartBody.FORM);
                barcodeBody = RequestBody.create(barcode, MultipartBody.FORM);
                installedDateBody = RequestBody.create(installedDate, MultipartBody.FORM);
                equipmentIdBody = RequestBody.create(equipmentId, MultipartBody.FORM);
                servIntvalTypeBody = RequestBody.create(addEquReq.getServIntvalType(), MultipartBody.FORM);
                servIntvalValueBody = RequestBody.create(addEquReq.getServIntvalValue(), MultipartBody.FORM);

                // invId = RequestBody.create( addEquReq.getInvId());
            } catch (Exception e) {
                e.printStackTrace();
            }


            String mimeType = "";
            MultipartBody.Part body = null;
            List<MultipartBody.Part> filesList = new ArrayList<>();
            if (!TextUtils.isEmpty(path)) {
                File file1 = new File(path);
                if (file1 != null) {
                    mimeType = URLConnection.guessContentTypeFromName(file1.getName());
                    if (mimeType == null) {
                        mimeType = file1.getName();
                    }
                    RequestBody requestFile = RequestBody.create(file1, MediaType.parse(mimeType));
                    // MultipartBody.Part is used to send also the actual file name
                    body = MultipartBody.Part.createFormData("image[]", file1.getName() + path.substring(path.lastIndexOf(".")), requestFile);
                    filesList.add(body);
                }
            }


            AppUtility.progressBarShow(((Context) addJobEquView));
            ApiClient.getservices().addEquipment(AppUtility.getApiHeaders(),
                    filesList, equnm, brand, mno, sno,
                    expiryDate, manufactureDate, purchaseDate,
                    status, notes, isBarcodeGenerate, state
                    , ctry, adr, city, zip, ecId, type,
                    egId, jobId, cltId, contrId, isPart, siteId,
                    extraField1, extraField2, barcodeBody, installedDateBody,
                    equipmentIdBody,servIntvalTypeBody,servIntvalValueBody,supplierId,supplier)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {

                                updateEquipmentCount(addEquReq.getJobId());
                                addJobEquView.addExpenseSuccesFully(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));


                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                addJobEquView.sessionExpire(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            } else {
                                addJobEquView.setEquReqError(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG : error----", e.getMessage());
                            AppUtility.progressBarDissMiss();
                            addJobEquView.setEquReqError(e.getMessage());
                            addJobEquView.finishActivity();
                        }

                        @Override
                        public void onComplete() {
                            AppUtility.progressBarDissMiss();
                            Log.e("TAG onComplete------", "onComplete");
                        }
                    });
        } else {
            netWork_erroR();
        }
    }


    @Override
    public void getCountryList() {
        countryList = SpinnerCountrySite.clientCountryList();
        addJobEquView.setCountryList(countryList);
    }

    @Override
    public void getEquipmentList(String jobId) {

        if (AppUtility.isInternetConnected()) {

            HashMap<String, String> auditListRequestModel = new HashMap<>();
            auditListRequestModel.put("search", "");
            auditListRequestModel.put("isActive", "");
            auditListRequestModel.put("limit", ""+updatelimit);
            auditListRequestModel.put("index", ""+updateindex);
            auditListRequestModel.put("type", "");


            String data = new Gson().toJson(auditListRequestModel);
            ApiClient.getservices().eotServiceCall(Service_apis.getAllEquipments, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            Log.e("getAllEquipments", "AddJobPc");
                            try {
                                Log.d("mahi", jsonObject.toString());
                                if (jsonObject.get("success").getAsBoolean()) {
                                    count = jsonObject.get("count").getAsInt();
                                    String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                    Type listType = new com.google.common.reflect.TypeToken<List<EquArrayModel>>() {
                                    }.getType();
                                    List<EquArrayModel> data = new Gson().fromJson(convert, listType);
                                    addJobEquView.setEuqipmentList(data);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("", "");
                        }

                        @Override
                        public void onComplete() {
                            if ((updateindex + updatelimit) <= count) {
                                updateindex += updatelimit;
                                getEquipmentList("");
                            } else {
                                App_preference.getSharedprefInstance().setEquipmentSyncTime(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT));
                                updateindex = 0;
                                count = 0;
                            }
                        }
                    });
        } else {
            Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(jobId);
            if (job != null && job.getEquArray() != null) {
                addJobEquView.setEuqipmentList(job.getEquArray());
            }
        }
    }

    private void netWork_erroR() {
        AppUtility.alertDialog(((Context) addJobEquView), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return null;
            }
        });
    }

    @Override
    public void getStateList(String countyId) {
        statesList = SpinnerCountrySite.clientStatesList(countyId);
        addJobEquView.setStateList(statesList);
    }

    @Override
    public void getSupplierList(String search) {
        if (AppUtility.isInternetConnected()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("search",search);
            String data = new Gson().toJson(jsonObject);
            ApiClient.getservices().eotServiceCall(Service_apis.get_supplier_list, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<GetSupplierData>>() {
                                }.getType();
                                List<GetSupplierData> supplierList = new Gson().fromJson(convert, listType);
                                addJobEquView.setsupplierList(supplierList);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("", "");
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            AppUtility.alertDialog(((Context) addJobEquView), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return null;
                }
            });
        }
    }

    @Override
    public void getCageryList() {
        if (AppUtility.isInternetConnected()) {
            GetListModel model = new GetListModel();
            String data = new Gson().toJson(model);
            ApiClient.getservices().eotServiceCall(Service_apis.getEquCategoryList, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<GetCatgData>>() {
                                }.getType();
                                List<GetCatgData> cateList = new Gson().fromJson(convert, listType);
                                addJobEquView.setCategList(cateList);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("", "");
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            AppUtility.alertDialog(((Context) addJobEquView), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return null;
                }
            });
        }
    }

    @Override
    public void getGrpList() {
        if (AppUtility.isInternetConnected()) {
            GetListModel model = new GetListModel();
            String data = new Gson().toJson(model);
            ApiClient.getservices().eotServiceCall(Service_apis.getEquGroupList, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<GetgrpData>>() {
                                }.getType();
                                List<GetgrpData> cateList = new Gson().fromJson(convert, listType);
                                addJobEquView.setGrpList(cateList);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            AppUtility.alertDialog(((Context) addJobEquView), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return null;
                }
            });
        }
    }

    @Override
    public void getBrandList() {
        if (AppUtility.isInternetConnected()) {
            GetListModel model = new GetListModel();
            String data = new Gson().toJson(model);
            ApiClient.getservices().eotServiceCall(Service_apis.getBrandList, AppUtility.getApiHeaders(), getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<BrandData>>() {
                                }.getType();
                                List<BrandData> brandList = new Gson().fromJson(convert, listType);
                                addJobEquView.setBrandList(brandList);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            AppUtility.alertDialog(((Context) addJobEquView), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return null;
                }
            });
        }
    }


    @Override
    public void getEquStatusList() {
        if (AppUtility.isInternetConnected()) {
            EquipmentStatusReq equipmentListReq = new EquipmentStatusReq();
            String data = new Gson().toJson(equipmentListReq);
            ApiClient.getservices().eotServiceCall(Service_apis.getEquipmentStatus, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            Log.d("equipmentlist", jsonObject.toString());
                            if (jsonObject.get("success").getAsBoolean()) {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<EquipmentStatus>>() {
                                }.getType();
                                List<EquipmentStatus> equipmentStatusList = new Gson().fromJson(convert, listType);
                                addJobEquView.setEquStatusList(equipmentStatusList);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }

    }


}
