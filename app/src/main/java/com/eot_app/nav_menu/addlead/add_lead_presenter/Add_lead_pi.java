package com.eot_app.nav_menu.addlead.add_lead_presenter;

import com.eot_app.nav_menu.addlead.FieldListAdapter;
import com.eot_app.nav_menu.addlead.model.AddLeadPost;
import com.eot_app.utility.settings.setting_db.FieldWorker;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public interface Add_lead_pi {


    void getJobTitleList();

    void getClientList();
    void getLeadFormApiCall();

    void getTillDateForRecur(String startDate,FieldListAdapter.ViewHolder holder);

    void getSiteList(String cltId, @NonNull final FieldListAdapter.ViewHolder holder);

    void getCOntactList(String cltId, @NonNull final FieldListAdapter.ViewHolder holder);

    void getCountryList(@NonNull final FieldListAdapter.ViewHolder holder);

    void getStateList(String countyName, @NonNull final FieldListAdapter.ViewHolder holder);

    void getWorkerList(FieldListAdapter.ViewHolder holder);


    void addLeadWithImageDescription(AddLeadPost addJob_req, ArrayList<String> links, List<String> fileNames);

    boolean RequiredFields(String cltId,
                           boolean contactSelf,
                           boolean siteSelf,
                           String conNm,
                           String siteNm,
                           String adr,
                           String countryname,
                           String statename,
                           String mob,
                           String alterNateMob,
                           String email);


    void getCurrentdateTime(@NonNull final FieldListAdapter.ViewHolder holder);

    boolean isValidCountry(String countryaaAaA);

    boolean isValidState(String state);

    String cntryId(String cntId);

    String statId(String state, String statename);

    void getTagDataList(@NonNull final FieldListAdapter.ViewHolder holder);
    void getTimeShiftList(@NonNull final FieldListAdapter.ViewHolder holder);

    void getContractList(String cltId, @NonNull final FieldListAdapter.ViewHolder holder);
    FieldWorker getDefaultFieldWorker();

    void getEndTime(String datestr, String s, @NonNull final FieldListAdapter.ViewHolder holder);


}
