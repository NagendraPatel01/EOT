package com.eot_app.nav_menu.addlead.add_lead_presenter;

import android.text.TextUtils;
import android.util.Log;

import com.eot_app.nav_menu.addlead.AddLeadResModel;
import com.eot_app.nav_menu.addlead.AddLeadView;

import com.eot_app.nav_menu.addlead.FieldListAdapter;
import com.eot_app.nav_menu.addlead.model.AddLeadPost;
import com.eot_app.nav_menu.client.client_db.Client;
import com.eot_app.nav_menu.client.clientlist.client_detail.site.sitelist.editsite.editsitedb.SpinnerCountrySite;
import com.eot_app.services.ApiClient;
import com.eot_app.services.Service_apis;

import com.eot_app.time_shift_pkg.ShiftTimeReSModel;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.Country;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.Eot_Validation;
import com.eot_app.utility.States;
import com.eot_app.utility.db.AppDataBase;

import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.settings.contractdb.ContractRes;

import com.eot_app.utility.settings.setting_db.FieldWorker;
import com.eot_app.utility.settings.setting_db.JobTitle;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.io.File;

import java.lang.reflect.Type;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class AddLead_pc implements Add_lead_pi {
    private final AddLeadView addLeadView;
    private List<States> statesList;
    private List<Country> countryList;
    private String date_str, time_str;


    public AddLead_pc(AddLeadView addLeadView) {
        this.addLeadView = addLeadView;
    }

    @Override
    public void getTillDateForRecur(String startDate, FieldListAdapter.ViewHolder holder) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert date != null;
        startDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(date);
        startDate = AppUtility.getDate(startDate);

        try {
            calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(startDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.MONTH, 1);

        addLeadView.setTillDateForRecur(simpleDateFormat.format(calendar.getTime()), holder);
    }

    @Override
    public FieldWorker getDefaultFieldWorker() {
        return AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).fieldWorkerModel().getFieldWorkerByID(App_preference.getSharedprefInstance().getLoginRes().getUsrId());
    }

    @Override
    public boolean RequiredFields(String cltId, boolean contactSelf, boolean siteSelf, String conNm,
                                  String siteNm, String adr, String countryId, String stateId, String mob, String
                                          alterNateMob, String email) {
       /* if (jtId.isEmpty()) {
            addLeadView.showErrorMsgsForValidation(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_job_title));
            return false;
        } else*/
        if (cltId.equals("")) {
            addLeadView.showErrorMsgsForValidation(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_client_name));
            return false;
        } else if (!mob.isEmpty() && !mob.equalsIgnoreCase(App_preference.getSharedprefInstance().getLoginRes().getCtryCode()) && mob.length() < AppConstant.MOBILE_LIMIT) {
            addLeadView.showErrorMsgsForValidation(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_mob_lent));
            return false;
        } else if (!alterNateMob.isEmpty() && !mob.equalsIgnoreCase(App_preference.getSharedprefInstance().getLoginRes().getCtryCode()) && alterNateMob.length() < AppConstant.MOBILE_LIMIT) {
            addLeadView.showErrorMsgsForValidation(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_alter_mob_lent));
            return false;
        } else if (!email.isEmpty() && (!Eot_Validation.email_checker(email).equals(""))) {
            addLeadView.showErrorMsgsForValidation(Eot_Validation.email_checker(email));
            return false;
        } else if (adr.equals("")) {
            addLeadView.showErrorMsgsForValidation(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_addr));
            return false;
        } else if (!isValidCountry(countryId)) {
            addLeadView.showErrorMsgsForValidation(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_select_country_first));
            return false;
        } else if (!isValidState(stateId)) {
            addLeadView.showErrorMsgsForValidation(LanguageController.getInstance().getMobileMsgByKey(AppConstant.state_error));
            return false;
        }
        return true;
    }

    @Override
    public void getCurrentdateTime(@NonNull FieldListAdapter.ViewHolder holder) {
        String dateTime = AppUtility.getDateByFormat(AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm:ss a"
                , "dd-MM-yyyy kk:mm:ss"));
        String[] date_Time = dateTime.split(" ");
        String datestr = date_Time[0];

        String time1 = App_preference.getSharedprefInstance().getLoginRes().getJobSchedule();
        if (!TextUtils.isEmpty(time1)) {
            schdul_Start_Date_Time(AppUtility.getFormatedTime(time1), datestr,holder);
        }

        if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0")) {
            getEndTime(datestr, date_Time[1] + " " + date_Time[2],holder);
        } else {
            getEndTime(datestr, date_Time[1],holder);

        }
    }

    @Override
    public void getTimeShiftList(@NonNull FieldListAdapter.ViewHolder holder) {
        List<ShiftTimeReSModel> list = new ArrayList<>();
        list = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).shiftTimeDao().getShiftTimeList();
        addLeadView.setTimeShiftList(list, holder);
    }

    @Override
    public void getEndTime(String datestr, String dateTime, @NonNull FieldListAdapter.ViewHolder holder) {
        String sch_tm_dt = App_preference.getSharedprefInstance().getLoginRes().getJobCurrentTime();
        if (!TextUtils.isEmpty(sch_tm_dt)) {
            sch_time_cur(datestr, dateTime, sch_tm_dt,holder);
        }
    }

    private void sch_time_cur(String datestr, String date_Time, String sch_tm_dt,FieldListAdapter.ViewHolder holder) {
        String an_pm = "";
        try {
            String[] remv_sec = date_Time.split(":");
            try {
                if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                        App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0")) {
                    String[] am_pm = date_Time.split(" ");
                    an_pm = " " + am_pm[1];
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //  String[] am_pm = date_Time.split(" ");
            String cur_start = remv_sec[0] + ":" + remv_sec[1] + an_pm;
            String date_time = datestr + " " + cur_start;

            String[] time_dur = sch_tm_dt.split(":");
            long dur_milliseconds = TimeUnit.SECONDS.toMillis(TimeUnit.HOURS.toSeconds(Integer.parseInt(time_dur[0])) +
                    TimeUnit.MINUTES.toSeconds(Integer.parseInt(time_dur[1])));

            SimpleDateFormat simpleDate = new SimpleDateFormat(
                    AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm"), Locale.US);
            Date past = null;
            long milisce = 0;
            try {
                past = simpleDate.parse(date_time);
                assert past != null;
                milisce = past.getTime() + dur_milliseconds;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milisce);
            String std = simpleDate.format(calendar.getTime());

            addLeadView.set_str_DT_after_cur(std,holder);

            String[] time_duration = std.split(" ");
            date_str = time_duration[0];
            try {
                if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                        App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0"))
                    time_str = time_duration[1] + " " + time_duration[2];
                else time_str = time_duration[1] + "";

            } catch (Exception e) {
                e.printStackTrace();
            }
            end_Date_Time(holder);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    private void schdul_Start_Date_Time(String[] sch_time, String datestr,FieldListAdapter.ViewHolder holder) {
        date_str = datestr;
        try {
            if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                    App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0"))
                time_str = sch_time[1] + " " + sch_time[2];
            else time_str = sch_time[1] + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        addLeadView.set_Str_DTime(date_str, time_str,holder);
        end_Date_Time(holder);
    }

    private void end_Date_Time(FieldListAdapter.ViewHolder holder) {
        String date_time = date_str + " " + time_str;
        SimpleDateFormat simpleDate = new SimpleDateFormat(
                AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm"), Locale.US);
        Date past = null;
        long milisce = 0;
        try {
            past = simpleDate.parse(date_time);
            assert past != null;
            milisce = past.getTime() + duration_Time();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milisce);
        String std = simpleDate.format(calendar.getTime());
        addLeadView.set_End_Date_Time(std,holder);
    }

    //set schedula date time accoroding to duration
    private long duration_Time() {
        String duration = App_preference.getSharedprefInstance().getLoginRes().getDuration();
        String[] time_dur = duration.split(":");
        return TimeUnit.SECONDS.toMillis(TimeUnit.HOURS.toSeconds(Integer.parseInt(time_dur[0])) +
                TimeUnit.MINUTES.toSeconds(Integer.parseInt(time_dur[1])));
    }


    @Override
    public void getJobTitleList() {
        List<JobTitle> data = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobTitleModel().getJobTitlelist();
        addLeadView.SetJobTittle((ArrayList<JobTitle>) data);
    }

    @Override
    public void getClientList() {
        List<Client> data = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).clientModel().getActiveClientList();
        addLeadView.setClientlist(data);
    }

    @Override
    public void getLeadFormApiCall() {
//        ApiRequestresponce requestor = new ApiRequestresponce(this, LEADReqCode);
//        requestor.sendReqOnServerGetRes(Service_apis.getAddLeadFormList, new JsonObject());

        ApiClient.getservices().eotServiceCall(Service_apis.getAddLeadFormList, AppUtility.getApiHeaders(), new JsonObject())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull JsonObject jsonObject) {
                        if (jsonObject.get("success").getAsBoolean()) {
                            try {
                                String convert = new Gson().toJson(jsonObject.get("data").getAsJsonArray());
                                Type listType = new TypeToken<List<AddLeadResModel>>() {
                                }.getType();
                                List<AddLeadResModel> data = new Gson().fromJson(convert, listType);
//                    addRecordsToDB(data);
                                addLeadView.SetLead(data);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        EotApp.getAppinstance().sessionExpired();
                        AppUtility.progressBarDissMiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void getSiteList(String cltId, @NonNull FieldListAdapter.ViewHolder holder) {
        if (Integer.parseInt(cltId) > 0) {
            addLeadView.setSiteList(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).sitemodel().getSitesFromCltId(Integer.parseInt(cltId)), holder);
        }

    }

    @Override
    public void getCOntactList(String cltId, @NonNull FieldListAdapter.ViewHolder holder) {
        if (Integer.parseInt(cltId) > 0) {
            addLeadView.setContactList(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contactModel().getContactFromcltId(cltId), holder);
        }
    }


    @Override
    public void getCountryList(@NonNull FieldListAdapter.ViewHolder holder) {
        countryList = SpinnerCountrySite.clientCountryList();// clientCountryList("countries.json");
        addLeadView.setCountryList(countryList, holder);
    }

    @Override
    public void getStateList(String countyId, @NonNull FieldListAdapter.ViewHolder holder) {
        statesList = SpinnerCountrySite.clientStatesList(countyId);//clientStatesList("states.json", countyId);
        addLeadView.setStateList(statesList, holder);
    }

    @Override
    public void getWorkerList(FieldListAdapter.ViewHolder holder) {
        addLeadView.setWorkerList(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).fieldWorkerModel().getFieldWorkerlist(),holder);
    }


    private String getTimeStampFromFormatedDate(String schdlStart) {
        SimpleDateFormat gettingfmt = new SimpleDateFormat(
                //AppUtility.dateTimeByAmPmFormate
                ("dd-MM-yyyy hh:mm a"
                        //                , "dd-MM-yyyy kk:mm"
                )
                , Locale.US);
        try {
            Date formated = gettingfmt.parse(schdlStart);
            assert formated != null;
            return String.valueOf(formated.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
    public void getTagDataList(@NonNull FieldListAdapter.ViewHolder holder) {
        addLeadView.setSetTagData(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).tagmodel().getTagslist(), holder);
    }


    @Override
    public void getContractList(String cltId, @NonNull FieldListAdapter.ViewHolder holder) {
        List<ContractRes> contractResList = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).contractDao().getContractListById(cltId, (System.currentTimeMillis() / 1000) + "");
        Log.e("", "");
        addLeadView.setContractlist(contractResList, holder);
    }

    @Override
    public void addLeadWithImageDescription(AddLeadPost addJob_req, ArrayList<String> links, List<String> fileNames) {
        if (AppUtility.isInternetConnected()) {
            Log.v("Call From add job pc ", "");

            RequestBody userIdrb = RequestBody.create(App_preference.getSharedprefInstance().getLoginRes().getUsrId(), MultipartBody.FORM);

            RequestBody contractId = RequestBody.create(addJob_req.getConId(), MultipartBody.FORM);//contrId
            RequestBody type = RequestBody.create("" + addJob_req.getType(), MultipartBody.FORM);
            RequestBody jobDes = null;
            if (addJob_req.getDes() != null) {
                jobDes = RequestBody.create(addJob_req.getDes(), MultipartBody.FORM);
            }
            RequestBody cltIdReq = RequestBody.create(addJob_req.getCltId(), MultipartBody.FORM);
            RequestBody clientForFuturerb = RequestBody.create(addJob_req.getClientForFuture() + "", MultipartBody.FORM);
            RequestBody siteIfrb = RequestBody.create(addJob_req.getSiteId(), MultipartBody.FORM);
            RequestBody newsiterb = RequestBody.create(addJob_req.getSnm(), MultipartBody.FORM);
            RequestBody siteforfuturerb = RequestBody.create(addJob_req.getSiteForFuture() + "", MultipartBody.FORM);
            RequestBody conIdrb = RequestBody.create(addJob_req.getConId() + "", MultipartBody.FORM);//conId
            RequestBody new_con_nmrb = RequestBody.create(addJob_req.getCnm() + "", MultipartBody.FORM);//new_con_nm
            RequestBody nm = RequestBody.create(addJob_req.getNm() + "", MultipartBody.FORM);//new_con_nm
            RequestBody contactforfuturerb = RequestBody.create(addJob_req.getContactForFuture() + "", MultipartBody.FORM);
            RequestBody mobilerb = RequestBody.create(addJob_req.getMob1(), MultipartBody.FORM);
            RequestBody atmobrb = RequestBody.create(addJob_req.getMob2(), MultipartBody.FORM);
            RequestBody emailrb = RequestBody.create(addJob_req.getEmail(), MultipartBody.FORM);
            RequestBody adrrb = RequestBody.create(addJob_req.getAdr(), MultipartBody.FORM);
            RequestBody citrb = RequestBody.create(addJob_req.getCity(), MultipartBody.FORM);
            RequestBody ctryrb = RequestBody.create(addJob_req.getCtry(), MultipartBody.FORM);//ctry_id
            RequestBody staterb = RequestBody.create(addJob_req.getState(), MultipartBody.FORM);//state_id
            RequestBody postcoderb = RequestBody.create(addJob_req.getZip(), MultipartBody.FORM);
            RequestBody schdlStartrb = RequestBody.create(addJob_req.getLeadStartDate(), MultipartBody.FORM);
            RequestBody schdlFinishrb = RequestBody.create(addJob_req.getLeadEndDate(), MultipartBody.FORM);
            RequestBody afId = RequestBody.create("" + addJob_req.getAfId(), MultipartBody.FORM);

            RequestBody statusrb = RequestBody.create("1", MultipartBody.FORM);
            RequestBody dateTimerb = RequestBody.create(AppUtility.getDateByFormat(AppConstant.DATE_TIME_FORMAT), MultipartBody.FORM);
            RequestBody latrb = RequestBody.create(addJob_req.getLat(), MultipartBody.FORM);
            RequestBody lngrb = RequestBody.create(addJob_req.getLng(), MultipartBody.FORM);
            RequestBody landmarkrb = RequestBody.create(addJob_req.getLandmark(), MultipartBody.FORM);
            RequestBody sourceBody = RequestBody.create(""+addJob_req.getSource(), MultipartBody.FORM);

            String mimeType = "";
            MultipartBody.Part body = null;
            List<MultipartBody.Part> fileList = new ArrayList<>();
            for (int i = 0; i < links.size(); i++) {
                File file1 = new File(links.get(i));
                String s = fileNames.get(i);
                if (file1 != null) {
                    mimeType = URLConnection.guessContentTypeFromName(file1.getName());
                    if (mimeType == null) {
                        mimeType = s;
                    }
                    RequestBody requestFile = RequestBody.create(file1, MediaType.parse(mimeType));
                    // MultipartBody.Part is used to send also the actual file name
                    body = MultipartBody.Part.createFormData("ja[]", s, requestFile);
                    fileList.add(body);
                }
            }

            RequestBody answerRequestBody = null;
            if (addJob_req.getAnswerArrayList() != null) {
                Gson gson = new Gson();
                String answerJson = gson.toJson(addJob_req.getAnswerArrayList());
                answerRequestBody = RequestBody.create(answerJson, MultipartBody.FORM);
            }


            RequestBody jtIdRequestBody = null;
            if (addJob_req.getAnswerArrayList() != null) {
                Gson gson = new Gson();
                String answerJson = gson.toJson(addJob_req.getJtId());
                jtIdRequestBody = RequestBody.create(answerJson, MultipartBody.FORM);
            }
//            List<MultipartBody.Part> jatIds = new ArrayList<>();
//            if (jatIds != null)
//                for (String s : addJob_req.getJtId())
//                    jatIds.add(MultipartBody.Part.createFormData("jtId", s));


            ApiClient.getservices().addLeadWithDocuments(AppUtility.getApiHeaders(),
                    userIdrb,
                    afId,
//                    contractId,
                    cltIdReq,
                    siteIfrb,
                    conIdrb,
//                    type,
                    statusrb,
                    jobDes,
                    schdlStartrb,
                    schdlFinishrb,
                    new_con_nmrb,
                    nm,
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
//                    dateTimerb,
                    latrb,
                    lngrb,
                    landmarkrb,
                    answerRequestBody,
                    jtIdRequestBody,sourceBody
            )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onNext(@NonNull JsonObject jsonObject) {
                            AppUtility.progressBarDissMiss();
                            if (jsonObject.get("success").getAsBoolean()) {
                                //  EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getServerMsgByKey(jsonObject.get("messgae").toString()));
                                addLeadView.finishActivity();
                                EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.success));

                                //refresh recent job on appointment details and show the label of recent job with code
                                EotApp.getAppinstance().notifyApiObserver(Service_apis.add_lead);
                            } else if (jsonObject.get("statusCode") != null && jsonObject.get("statusCode").getAsString().equals(AppConstant.SESSION_EXPIRE)) {
                                EotApp.getAppinstance().sessionExpired();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
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


}
