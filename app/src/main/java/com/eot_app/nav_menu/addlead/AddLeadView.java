package com.eot_app.nav_menu.addlead;

import com.eot_app.nav_menu.client.client_db.Client;
import com.eot_app.nav_menu.client.clientlist.client_detail.contact.client_dao.ContactData;
import com.eot_app.nav_menu.client.clientlist.client_detail.site.sitelist.Site_model;
import com.eot_app.time_shift_pkg.ShiftTimeReSModel;
import com.eot_app.utility.Country;
import com.eot_app.utility.States;
import com.eot_app.utility.settings.contractdb.ContractRes;
import com.eot_app.utility.settings.setting_db.FieldWorker;
import com.eot_app.utility.settings.setting_db.JobTitle;
import com.eot_app.utility.settings.setting_db.TagData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by aplite_pc302 on 6/27/18.
 */

public interface AddLeadView {
    void setTimeShiftList(List<ShiftTimeReSModel> list,@NonNull final FieldListAdapter.ViewHolder holder);

    void SetJobTittle(ArrayList<JobTitle> datastr);
    void SetLead(List<AddLeadResModel> datastr);

    void setClientlist(List<Client> data);

    void setContractlist(List<ContractRes> contractlist,@NonNull final FieldListAdapter.ViewHolder holder);

    void setSiteList(List<Site_model> data,@NonNull final FieldListAdapter.ViewHolder holder);

    void setContactList(List<ContactData> data,@NonNull final FieldListAdapter.ViewHolder holder);

    void setCountryList(List<Country> countryList,@NonNull final FieldListAdapter.ViewHolder holder);

    void setStateList(List<States> statesList,@NonNull final FieldListAdapter.ViewHolder holder);
    void setTillDateForRecur(String tillDate,@NonNull final FieldListAdapter.ViewHolder holder);
    void setWorkerList(List<FieldWorker> fieldWorkerlist,@NonNull final FieldListAdapter.ViewHolder holder);

    void finishActivity();

    void set_Str_DTime(String str_dt_tm, String time_str,@NonNull final FieldListAdapter.ViewHolder holder);

    void set_End_Date_Time(String std,@NonNull final FieldListAdapter.ViewHolder holder);

    void set_str_DT_after_cur(String std,@NonNull final FieldListAdapter.ViewHolder holder);

    void setSetTagData(List<TagData> tagslist,@NonNull final FieldListAdapter.ViewHolder holder);

    void showErrorMsgsForValidation(String msg);
}
