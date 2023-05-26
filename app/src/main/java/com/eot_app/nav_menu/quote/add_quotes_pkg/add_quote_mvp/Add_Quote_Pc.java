package com.eot_app.nav_menu.quote.add_quotes_pkg.add_quote_mvp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.eot_app.activitylog.ActivityLogController;
import com.eot_app.nav_menu.client.client_db.Client;
import com.eot_app.nav_menu.client.clientlist.client_detail.site.sitelist.editsite.editsitedb.SpinnerCountrySite;
import com.eot_app.nav_menu.quote.add_quotes_pkg.model_pkg.Add_Quote_ReQ;
import com.eot_app.nav_menu.quote.quotes_list_pkg.qoute_model_pkg.Quote_ReQ;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.Country;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.Eot_Validation;
import com.eot_app.utility.States;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.settings.setting_db.FieldWorker;
import com.eot_app.utility.settings.setting_db.JobTitle;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hypertrack.hyperlog.HyperLog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Add_Quote_Pc implements Add_Quote_Pi {
    private final Add_Quote_View add_quote_view;
    private List<States> statesList = new ArrayList<>();
    private List<Country> countryList = new ArrayList<>();

    public Add_Quote_Pc(Add_Quote_View add_quote_view) {
        this.add_quote_view = add_quote_view;
    }

    @Override
    public void getActiveUserList() {
        List<FieldWorker> fwDataList = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).fieldWorkerModel().getFieldWorkerlist();
        //   add_quote_view.setActiveUserList(fwDataList);//only set active fieldworkers
        add_quote_view.setfwListForQuotes(fwDataList);
    }


    @Override
    public boolean requiredFileds(Set<String> jobType, String cltId, String adr, String countryId, String stateId, String mob, String alterNateMob, String email) {

            if (cltId.equals("")) {
            add_quote_view.setClientNameError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_client_name));
            return false;
        } else if (adr.equals("")) {
            add_quote_view.setAddr_Error(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_addr));
            return false;
        } else if (!isValidCountry(countryId)) {
            add_quote_view.setCountryError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_select_country_first));
            return false;
        } else if (!isValidState(stateId)) {
                add_quote_view.setStateError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.state_error));
                return false;
            } else if (!mob.isEmpty() && !mob.equalsIgnoreCase(App_preference.getSharedprefInstance().getLoginRes().getCtryCode()) && mob.length() < AppConstant.MOBILE_LIMIT) {
                add_quote_view.setMobError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_mob_lent));
                return false;
            } else if (!alterNateMob.isEmpty() && !mob.equalsIgnoreCase(App_preference.getSharedprefInstance().getLoginRes().getCtryCode()) && alterNateMob.length() < AppConstant.MOBILE_LIMIT) {
                add_quote_view.setMobError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_alter_mob_lent));
                return false;
            } else if (!email.isEmpty() && (!Eot_Validation.email_checker(email).equals(""))) {
                add_quote_view.setEmailError(Eot_Validation.email_checker(email));
                return false;
            }
        return true;
    }

    @Override
    public boolean isValidCountry(String countryId) {//country
        for (Country ctry : countryList) {
            if (ctry.getId().equalsIgnoreCase(countryId))
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
    public void getJobServices() {
        List<JobTitle> data = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobTitleModel().getJobTitlelist();
        add_quote_view.setJobServiceslist(data);
    }

    @Override
    public void getClientList() {
        List<Client> data = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).clientModel().getActiveClientList();
        add_quote_view.setClientList(data);
    }


    @Override
    public void getCountryList() {
        countryList = SpinnerCountrySite.clientCountryList();// readCountryJsonFile("countries.json");
        add_quote_view.setCountryList(countryList);
    }


    @Override
    public void getStateList(String countyId) {
        statesList = SpinnerCountrySite.clientStatesList(countyId);//readStateJsonFile("states.json", countyId);
        add_quote_view.setStateList(statesList);
    }


    @Override
    public void getContactList(String cltId) {


        if (Integer.parseInt(cltId) > 0) {
            //TODO to be checked only active contact should be dispalyed
//            add_quote_view.setContactList(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contactModel().
//                    getContactFromcltId(cltId));
            add_quote_view.setContactList(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contactModel().
                    getActiveContactFromcltId(cltId));        }
    }

    @Override
    public void getSilteList(String cltId) {
        if (Integer.parseInt(cltId) > 0) {
            //TODO to be checked only active site should be dispalyed

//            add_quote_view.setSiteList(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel().getSitesFromCltId(Integer.parseInt(cltId)));
            add_quote_view.setSiteList(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel().getActiveSitesFromCltId(Integer.parseInt(cltId)));
        }
    }

    @Override
    public void addQuotes(Add_Quote_ReQ requestModel) {
        if (AppUtility.isInternetConnected()) {
            HyperLog.i("", "addQuotes PC(M) start");
            ActivityLogController.saveActivity(
                    ActivityLogController.QUOTE_MODULE,
                    ActivityLogController.QUOTE_ADD,
                    ActivityLogController.QUOTE_MODULE
            );
            AppUtility.progressBarShow((Context) add_quote_view);
            String data = new Gson().toJson(requestModel);

            ApiClient.getservices().eotServiceCall(Service_apis.addQuotationForMobile, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            HyperLog.i("", "Quotation:" + jsonObject.toString());
                            if (jsonObject.get("success").getAsBoolean()) {
                                EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                                try {
                                    if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
                                        JsonObject data = jsonObject.getAsJsonObject("data");
                                        String quotId = data.get("quotId").getAsString();
                                        String label = data.get("label").getAsString();
                                        add_quote_view.onAddNewQuotes(quotId, label);
                                    } else add_quote_view.finishActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
//                                add_quote_view.onSessionExpire(LanguageController.getInstance().
//                                        getServerMsgByKey(jsonObject.getAsJsonObject().get("message").toString()));
                                EotApp.getAppinstance().sessionExpired();
                            } else {
                                add_quote_view.errorMsg(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }

                        }


                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppUtility.progressBarDissMiss();
                            add_quote_view.finishActivity();
                            HyperLog.i("", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            AppUtility.progressBarDissMiss();
                            Log.e("TAG", "");
                        }
                    });
        } else {
            networkError();
        }

    }

    @Override
    public void getTermsConditions() {
        if (AppUtility.isInternetConnected()) {
            ActivityLogController.saveActivity(
                    ActivityLogController.QUOTE_MODULE,
                    ActivityLogController.QUOTE_TERM,
                    ActivityLogController.QUOTE_MODULE
            );
            AppUtility.progressBarShow((Context) add_quote_view);
            String data = new Gson().toJson(new Quote_ReQ(1, 1));


            ApiClient.getservices().eotServiceCall(Service_apis.getTermsCondition, AppUtility.getApiHeaders(), AppUtility.getJsonObject(data))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            if (jsonObject.get("success").getAsBoolean()) {
                                if (jsonObject.has("data")) {
                                    String termsConditions = jsonObject.getAsJsonObject("data").get("quotTerms").getAsString();
                                    add_quote_view.setTermsConditions(termsConditions);
                                    //  EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                                }
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
//                                add_quote_view.onSessionExpire(LanguageController.getInstance().
//                                        getServerMsgByKey(jsonObject.getAsJsonObject().get("message").toString()));
                                EotApp.getAppinstance().sessionExpired();
                            } else {
                                //  add_quote_view.errorMsg(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("message").getAsString()));
                            }

                        }


                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppUtility.progressBarDissMiss();
                            Log.e("TAG", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            AppUtility.progressBarDissMiss();
                            Log.e("TAG", "");
                        }
                    });
        } else {
            networkError();
        }

    }

    @Override
    public void addQuoteWithDocuments(Add_Quote_ReQ add_quote_reQ, ArrayList links, List<String> fileNames) {
        if (AppUtility.isInternetConnected()) {

            RequestBody leadId = RequestBody.create( add_quote_reQ.getLeadId() == null ? "" : add_quote_reQ.getLeadId(),MultipartBody.FORM);
            RequestBody appId = RequestBody.create( add_quote_reQ.getAppId() == null ? "" : add_quote_reQ.getAppId(),MultipartBody.FORM);//parent id
            RequestBody cltId = RequestBody.create( add_quote_reQ.getCltId(),MultipartBody.FORM);//contrId
            RequestBody siteId = RequestBody.create( add_quote_reQ.getSiteId(),MultipartBody.FORM);
            RequestBody conId = RequestBody.create( add_quote_reQ.getConId(),MultipartBody.FORM);
            RequestBody status = RequestBody.create( add_quote_reQ.getStatus(),MultipartBody.FORM);
            RequestBody invDate = RequestBody.create( add_quote_reQ.getInvDate(),MultipartBody.FORM);
            RequestBody dueDate = RequestBody.create( add_quote_reQ.getDueDate(),MultipartBody.FORM);
            RequestBody newcnrb = RequestBody.create( add_quote_reQ.getNm(),MultipartBody.FORM);
            RequestBody new_con_nmrb = RequestBody.create( add_quote_reQ.getCnm() + "",MultipartBody.FORM);//new_con_nm
            RequestBody newsiterb = RequestBody.create( add_quote_reQ.getSnm(),MultipartBody.FORM);
            RequestBody emailrb = RequestBody.create( add_quote_reQ.getEmail(),MultipartBody.FORM);
            RequestBody mobilerb = RequestBody.create( add_quote_reQ.getMob1(),MultipartBody.FORM);
            RequestBody atmobrb = RequestBody.create( add_quote_reQ.getMob2(),MultipartBody.FORM);
            RequestBody adrrb = RequestBody.create( add_quote_reQ.getAdr(),MultipartBody.FORM);
            RequestBody citrb = RequestBody.create( add_quote_reQ.getCity(),MultipartBody.FORM);
            RequestBody ctryrb = RequestBody.create( add_quote_reQ.getCtry(),MultipartBody.FORM);//ctry_id
            RequestBody staterb = RequestBody.create( add_quote_reQ.getState(),MultipartBody.FORM);//state_id
            RequestBody postcoderb = RequestBody.create( add_quote_reQ.getZip(),MultipartBody.FORM);
            RequestBody clientForFuturerb = RequestBody.create( add_quote_reQ.getClientForFuture() + "",MultipartBody.FORM);
            RequestBody siteforfuturerb = RequestBody.create( add_quote_reQ.getSiteForFuture() + "",MultipartBody.FORM);
            RequestBody contactforfuturerb = RequestBody.create( add_quote_reQ.getContactForFuture() + "",MultipartBody.FORM);
            RequestBody commentsBody = RequestBody.create( add_quote_reQ.getCommets() + "",MultipartBody.FORM);

            List<MultipartBody.Part> jatIds = new ArrayList<>();
            for (String s : add_quote_reQ.getJtId())
                jatIds.add(MultipartBody.Part.createFormData("jtId[]", s));


            // RequestBody jtId = RequestBody.create( String.valueOf(add_quote_reQ.getJtId()));
            RequestBody des = RequestBody.create( add_quote_reQ.getDes(),MultipartBody.FORM);
            RequestBody inst = RequestBody.create( add_quote_reQ.getInst(),MultipartBody.FORM);
            RequestBody athr = RequestBody.create( add_quote_reQ.getAthr(),MultipartBody.FORM);
            RequestBody note = RequestBody.create( add_quote_reQ.getNote(),MultipartBody.FORM);
            RequestBody assignByUser = RequestBody.create( add_quote_reQ.getAssignByUser(),MultipartBody.FORM);
            RequestBody quotId = RequestBody.create( add_quote_reQ.getQuotId(),MultipartBody.FORM);
            RequestBody invId = RequestBody.create( add_quote_reQ.getInvId(),MultipartBody.FORM);
            RequestBody term = RequestBody.create( add_quote_reQ.getTerm(),MultipartBody.FORM);
            RequestBody latrb = RequestBody.create( add_quote_reQ.getLat(),MultipartBody.FORM);
            RequestBody lngrb = RequestBody.create( add_quote_reQ.getLng(),MultipartBody.FORM);

            String mimeType = "";
            MultipartBody.Part body;
            List<MultipartBody.Part> fileList = new ArrayList<>();
            for (int i = 0; i < links.size(); i++) {
                File file1 = new File((String) links.get(i));
                String s = fileNames.get(i);
                if (file1 != null) {
                    mimeType = URLConnection.guessContentTypeFromName(file1.getName());
                    if (mimeType == null) {
                        mimeType = s;
                    }
                    RequestBody requestFile = RequestBody.create(file1,MediaType.parse(mimeType));
                    // MultipartBody.Part is used to send also the actual file name
                    body = MultipartBody.Part.createFormData("qa[]", s, requestFile);
                    fileList.add(body);
                }
            }


            AppUtility.progressBarShow((Activity) add_quote_view);
            ApiClient.getservices().addQuoteWithDocuments(AppUtility.getApiHeaders(),
                    leadId,
                    appId,
                    cltId,
                    siteId,
                    conId,
                    status,
                    invDate,
                    dueDate,
                    newcnrb,
                    new_con_nmrb,
                    newsiterb,
                    emailrb,
                    mobilerb,
                    atmobrb,
                    adrrb,
                    citrb,
                    staterb,
                    ctryrb,
                    postcoderb,
                    clientForFuturerb,
                    siteforfuturerb,
                    contactforfuturerb,
                    jatIds,
                    des,
                    inst,
                    athr,
                    note,
                    assignByUser,
                    quotId,
                    invId,
                    term,
                    latrb,
                    lngrb,
                    fileList,commentsBody
            )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {
                        }

                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            AppUtility.progressBarDissMiss();
                            if (jsonObject.get("success").getAsBoolean()) {
                                add_quote_view.finishActivity();
                                //refresh recent job on appointment details and show the label of recent job with code
//                                EotApp.getAppinstance().notifyApiObserver(Service_apis.addJob);
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                EotApp.getAppinstance().sessionExpired();
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppUtility.progressBarDissMiss();
                            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.something_wrong));
                        }

                        @Override
                        public void onComplete() {
                            AppUtility.progressBarDissMiss();
                        }
                    });
        }
    }

    private void networkError() {
        AppUtility.alertDialog(((Context) add_quote_view), LanguageController.getInstance().
                        getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey
                        (AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok),
                "", () -> null);
    }

}
