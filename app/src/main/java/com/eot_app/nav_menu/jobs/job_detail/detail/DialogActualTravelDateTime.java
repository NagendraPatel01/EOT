package com.eot_app.nav_menu.jobs.job_detail.detail;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import com.eot_app.R;
import com.eot_app.databinding.DialogActualTravelDateTimeBinding;
import com.eot_app.nav_menu.jobs.add_job.Add_job_activity;
import com.eot_app.nav_menu.jobs.job_detail.detail.job_detail_presenter.JobDetail_pi;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.CompletionDetails;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.CompletionDetailsPost;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class DialogActualTravelDateTime extends DialogFragment implements View.OnClickListener {

    private DialogActualTravelDateTimeBinding binding;
    int year, month, day, mHour, mMinute;
    private JobDetail_pi jobDetail_pi;
    int logType;
    String jobId;
    String lastStatus, lastStatusTime;
    String date_start_ac, time_str_ac = "", actualStart = "", actualFinish = "", time_en_ac = "", date_end_ac;
    String date_start_tr, time_str_tr = "", travelStart = "", travelFinish = "", time_en_tr = "", date_end_tr;
    CompletionDetails completionDetails;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
    }

    public void setDataRequired(JobDetail_pi jobDetail_pi, int logType, String jobId, String lastStatus, String lastStatusTime
    ,CompletionDetails completionDetails){

        this.jobDetail_pi=jobDetail_pi;
        this.logType=logType;
        this.jobId=jobId;
        this.lastStatus=lastStatus;
        this.lastStatusTime=lastStatusTime;
        this.completionDetails=completionDetails;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_actual_travel_date_time, container, false);
        initViews();
        return binding.getRoot();
    }

    private void initViews() {
        binding.buttonSubmit.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.save_btn));
        binding.buttonSubmit.setOnClickListener(this);
        binding.dateAcEnd.setOnClickListener(this);
        binding.dateAcStart.setOnClickListener(this);
        binding.dateTrEnd.setOnClickListener(this);
        binding.dateTrStart.setOnClickListener(this);



        binding.dateAcStart.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.actual_start_date_time));
        binding.dateTrStart.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.travel_start_date_time));
        binding.dateAcEnd.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.actual_end_date_time));
        binding.dateTrEnd.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.travel_end_date_time));

        binding.startDateLabel.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_start_date));
        binding.endDateLabel.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_end_date_new));
        binding.startDateLabel1.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_start_date));
        binding.endDateLabel1.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_end_date_new));

        binding.buttonCancel.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.cancel));
        binding.buttonCancel.setOnClickListener(this);

        if(logType==1){
            binding.llActualDateTime.setVisibility(View.VISIBLE);
            binding.llTravelDateTime.setVisibility(View.GONE);
            binding.header.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.modifdy));
        }
        else {
            binding.llActualDateTime.setVisibility(View.GONE);
            binding.llTravelDateTime.setVisibility(View.VISIBLE);
            binding.header.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.modifdy_reavel));
        }
        String timeFormate1 = AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm");

        if (completionDetails!=null&&completionDetails.getLoginTime() != null && !completionDetails.getLoginTime().isEmpty()&& !completionDetails.getLoginTime().equals("0")) {
            if (LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_start_date).equals("Başlangıç \u200B\u200BTarihini Seç")){
                binding.dateAcStart.setCompoundDrawablePadding(-15);
            }
            binding.dateAcStart.setText(AppUtility.getDate(Long.parseLong(completionDetails.getLoginTime()), timeFormate1));
            actualStart = AppUtility.getDate(Long.parseLong(completionDetails.getLoginTime()), timeFormate1);
        }
        if (completionDetails!=null&&completionDetails.getLogoutTime() != null && !completionDetails.getLogoutTime().isEmpty() && !completionDetails.getLogoutTime().equals("0")) {
            binding.dateAcEnd.setText(AppUtility.getDate(Long.parseLong(completionDetails.getLogoutTime()), timeFormate1));
            actualFinish = AppUtility.getDate(Long.parseLong(completionDetails.getLogoutTime()), timeFormate1);
        }
        if (completionDetails!=null&&completionDetails.getTravel_loginTime() != null && !completionDetails.getTravel_loginTime().isEmpty()) {
            if (LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_start_date).equals("Başlangıç \u200B\u200BTarihini Seç")){
                binding.dateTrStart.setCompoundDrawablePadding(-15);
            }
            binding.dateTrStart.setText(AppUtility.getDate(Long.parseLong(completionDetails.getTravel_loginTime()), timeFormate1));
            travelStart = AppUtility.getDate(Long.parseLong(completionDetails.getTravel_loginTime()), timeFormate1);
        }
        if (completionDetails!=null&&completionDetails.getTarvel_logoutTime() != null && !completionDetails.getTarvel_logoutTime().isEmpty()) {
            binding.dateTrEnd.setText(AppUtility.getDate(Long.parseLong(completionDetails.getTarvel_logoutTime()), timeFormate1));
            travelFinish = AppUtility.getDate(Long.parseLong(completionDetails.getTarvel_logoutTime()), timeFormate1);
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_submit:
                callApiForCompletionDetails(logType);
                break;
            case R.id.date_ac_start:
                showDialogPicker(R.id.date_ac_start);
                break;
            case R.id.date_ac_end:
                showDialogPicker(R.id.date_ac_end);
                break;
            case R.id.date_tr_start:
                showDialogPicker(R.id.date_tr_start);
                break;
            case R.id.date_tr_end:
                showDialogPicker(R.id.date_tr_end);
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
    }


    private void callApiForCompletionDetails(int logType) {
        // for setting time fomats
        String timeFormate = "yyyy-MM-dd kk:mm";
//        String timeFormateInput = "dd-MM-yyyy kk:mm";

        String timeFormateInput = AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm");

        // set values
        String schdlFinish = "", schdlStart = "", tlogEnd = "", tlogStart = "";
        if (actualFinish != null && !actualFinish.isEmpty())
            schdlFinish = AppUtility.changeDateFormat(actualFinish, timeFormateInput, timeFormate) + ":00";
        if (actualStart != null && !actualStart.isEmpty())
            schdlStart = AppUtility.changeDateFormat(actualStart, timeFormateInput, timeFormate) + ":00";
        if (travelFinish != null && !travelFinish.isEmpty())
            tlogEnd = AppUtility.changeDateFormat(travelFinish, timeFormateInput, timeFormate) + ":00";
        if (travelStart != null && !travelStart.isEmpty())
            tlogStart = AppUtility.changeDateFormat(travelStart, timeFormateInput, timeFormate) + ":00";


        if (logType == 1 && schdlFinish.isEmpty() && schdlStart.isEmpty()) {
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_select_start_end_date));
            return;
        }
        if (logType == 2 && tlogEnd.isEmpty() && tlogStart.isEmpty()) {
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_select_start_end_travel_date));
            return;
        }
        if (logType == 1 && !schdlFinish.isEmpty() && schdlStart.isEmpty()) {
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_select_start_date));
            return;
        }
        if (logType == 1 && !schdlStart.isEmpty() && schdlFinish.isEmpty()) {
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_select_end_date));
            return;
        }
        if (logType == 2 &&!tlogEnd.isEmpty() && tlogStart.isEmpty()) {
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_select_travel_start_date));
            return;
        }
        if (logType == 2 &&!tlogStart.isEmpty() && tlogEnd.isEmpty()) {
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_select_travel_end_date));
            return;
        }
        if (logType == 1 && actualStart!=null&&actualFinish!=null&&!actualStart.isEmpty()&&!actualFinish.isEmpty()&&!conditionCheck(actualStart, actualFinish)) {
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.error_actual_start_end));
            return;
        }
        if (logType == 2 &&travelStart!=null&&travelFinish!=null&&!travelStart.isEmpty()&&!travelFinish.isEmpty()&&!conditionCheck(travelStart, travelFinish)) {
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.error_travel_start_end));
            return;
        }

        CompletionDetailsPost.CompletionDetail obj = new CompletionDetailsPost.CompletionDetail(1, 1,
                schdlFinish,
                schdlStart,
                tlogEnd, tlogStart,
                App_preference.getSharedprefInstance().getLoginRes().getUsrId());
        //to update the completion details
        if (jobDetail_pi != null)
            jobDetail_pi.addJobCompletionDetails(jobId, obj, logType);
        dismiss();

    }

    @SuppressLint("NonConstantResourceId")
    public void showDialogPicker(int id) {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        switch (id) {
            case R.id.date_ac_start:
                DatePickerDialog datePickerDialogSelectDate = new DatePickerDialog(getActivity(), AppUtility.InputDateSet(getActivity(), new Add_job_activity.DateTimeCallback() {
                    @Override
                    public void setDateTime(String dateTime) {
                        date_start_ac = dateTime;
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AppUtility.InputTimeSet(getActivity(), new Add_job_activity.DateTimeCallback() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void setDateTime(String dateTime) {
                                time_str_ac = dateTime;
                                if (actualFinish != null && !actualFinish.isEmpty() && !conditionCheck(date_start_ac + " " + time_str_ac, actualFinish)) {
                                    EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.error_actual_start_end));
                                } else {
                                    actualStart = date_start_ac + " " + time_str_ac;
                                    binding.dateAcStart.setText(actualStart);
                                }
                            }
                        }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_time)), mHour, mMinute, true);
                        timePickerDialog.show();
                    }
                }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_date)), year, month, day);
                datePickerDialogSelectDate.show();
                break;

            case R.id.date_ac_end:
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AppUtility.CompareInputOutputDate(getActivity(), new Add_job_activity.DateTimeCallback() {
                    @Override
                    public void setDateTime(String dateTime) {
                        date_end_ac = dateTime;

                        TimePickerDialog timePickerDialog1 = new TimePickerDialog(getActivity(), AppUtility.OutPutTime(getActivity(), new Add_job_activity.DateTimeCallback() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void setDateTime(String dateTime) {
                                time_en_ac = dateTime;
                                if (actualStart != null && !actualStart.isEmpty() && !conditionCheck(actualStart, date_end_ac + " " + time_en_ac)) {
                                    EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.error_actual_start_end));
                                } else {
                                    actualFinish = date_end_ac + " " + time_en_ac;
                                    binding.dateAcEnd.setText(actualFinish);
                                }
                            }
                        }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_due_start_time)), mHour, mMinute, true);
                        timePickerDialog1.show();
                    }
                }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_due_start_date)), year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;

            case R.id.date_tr_start:
                DatePickerDialog datePickerDialogSelectDate1 = new DatePickerDialog(getActivity(), AppUtility.InputDateSet(getActivity(), new Add_job_activity.DateTimeCallback() {
                    @Override
                    public void setDateTime(String dateTime) {
                        date_start_tr = dateTime;
                        TimePickerDialog timePickerDialog2 = new TimePickerDialog(getActivity(), AppUtility.InputTimeSet(getActivity(), new Add_job_activity.DateTimeCallback() {
                            @Override
                            public void setDateTime(String dateTime) {
                                time_str_tr = dateTime;
                                if (!conditionCheckTravel(date_start_tr + " " + time_str_tr, travelFinish, 0)) {
                                    timeOutOfScopeAlert();
                                } else if (travelFinish != null && !travelFinish.isEmpty() && !conditionCheck(date_start_tr + " " + time_str_tr, travelFinish)) {
                                    EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.error_travel_start_end));
                                } else {
                                    travelStart = date_start_tr + " " + time_str_tr;
                                    binding.dateTrStart.setText(travelStart);
                                }
                            }
                        }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_time)), mHour, mMinute, true);
                        timePickerDialog2.show();

                    }
                }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_date)), year, month, day);
                datePickerDialogSelectDate1.show();
                break;

            case R.id.date_tr_end:
                final DatePickerDialog datePickerDialog1 = new DatePickerDialog(getActivity(), AppUtility.CompareInputOutputDate(getActivity(), new Add_job_activity.DateTimeCallback() {
                    @Override
                    public void setDateTime(String dateTime) {
                        date_end_tr = dateTime;
                        TimePickerDialog timePickerDialog3 = new TimePickerDialog(getActivity(), AppUtility.OutPutTime(getActivity(), new Add_job_activity.DateTimeCallback() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void setDateTime(String dateTime) {
                                time_en_tr = dateTime;
                                if (!conditionCheckTravel(travelStart, date_end_tr + " " + time_en_tr, 1)) {
                                    timeOutOfScopeAlert();
                                } else if (travelStart != null && !travelStart.isEmpty() && !conditionCheck(travelStart, date_end_tr + " " + time_en_tr)) {
                                    EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.error_travel_start_end));
                                } else {
                                    travelFinish = date_end_tr + " " + time_en_tr;
                                    binding.dateTrEnd.setText(travelFinish);
                                }
                            }
                        }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_due_start_time)), mHour, mMinute, true);
                        timePickerDialog3.show();
                    }
                }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_due_start_date)), year, month, day);
                datePickerDialog1.show();
                break;

        }
    }

    private boolean conditionCheckTravel(String travelStart, String travelFinish, int type) {
        Locale.getDefault().getDisplayLanguage();
        try {
            SimpleDateFormat gettingfmt = new SimpleDateFormat(
                    //"dd-MM-yyyy hh:mm a"
                    AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm")
                    , Locale.US);

            Date dateJobStart=null;
            Date dateJobEnd=null;
            Date dateJlastStatusTime=null;
            if(actualStart!=null&&!actualStart.isEmpty()) {
                dateJobStart = gettingfmt.parse(actualStart);
                if (dateJobStart != null)
                    dateJobStart.getTime();
            }
            if(actualFinish!=null&&!actualFinish.isEmpty()){
                dateJobEnd = gettingfmt.parse(actualFinish);
                dateJobEnd.getTime();
            }
            if(lastStatusTime!=null&&!lastStatusTime.isEmpty()){
                dateJlastStatusTime = gettingfmt.parse(lastStatusTime);
                dateJlastStatusTime.getTime();
            }


            if (type == 0) {
                Date date = gettingfmt.parse(travelStart);
                date.getTime();

                if (actualStart!=null&&!actualStart.isEmpty()&&date.getTime() > dateJobStart.getTime()) {
                    return false;
                } else if (actualFinish!=null&&!actualFinish.isEmpty()&&date.getTime() > dateJobEnd.getTime()) {
                    return false;
                } else if(lastStatus!=null&&!lastStatus.isEmpty()&&lastStatusTime!=null&&!lastStatusTime.isEmpty()){
                    if (Integer.parseInt(lastStatus) == 7 || Integer.parseInt(lastStatus) == 9) {
                        if(dateJlastStatusTime!=null&&date.getTime()  > dateJlastStatusTime.getTime()){
                            return false;
                        }
                    } else if (Integer.parseInt(lastStatus) == 1 || Integer.parseInt(lastStatus) == 2) {
                        if(date.getTime() < dateJlastStatusTime.getTime()){
                            return false;
                        }
                    }
                }

                return true;
            } else if (type == 1) {

                Date date1 = gettingfmt.parse(travelFinish);
                date1.getTime();
                if (actualStart!=null&&!actualStart.isEmpty()&&date1.getTime() > dateJobStart.getTime()) {
                    return false;
                } else if (actualFinish!=null&&!actualFinish.isEmpty()&&date1.getTime() > dateJobEnd.getTime()) {
                    return false;
                } else if(lastStatus!=null&&!lastStatus.isEmpty()&&lastStatusTime!=null&&!lastStatusTime.isEmpty()){
                    if (Integer.parseInt(lastStatus) == 7 || Integer.parseInt(lastStatus) == 9) {
                        if(date1.getTime() > dateJlastStatusTime.getTime()){
                            return false;
                        }
                    } else if (Integer.parseInt(lastStatus) == 1 || Integer.parseInt(lastStatus) == 2) {
                        if(date1.getTime() <  dateJlastStatusTime.getTime()){
                            return false;
                        }
                    }
                }
                return true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //start date,time must be grater than to end date time
    private boolean conditionCheck(String schdlStart, String schdlFinish) {
        Locale.getDefault().getDisplayLanguage();
        try {
            SimpleDateFormat gettingfmt = new SimpleDateFormat(
                    //"dd-MM-yyyy hh:mm a"
                    AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm")
                    , Locale.US);
            Date date = gettingfmt.parse(schdlStart);
            date.getTime();

            Date date1 = gettingfmt.parse(schdlFinish);
            date1.getTime();
            if (date1.getTime() > date.getTime()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void timeOutOfScopeAlert() {
        AppUtility.error_Alert_Dialog(getActivity(), LanguageController.getInstance().getMobileMsgByKey(AppConstant.time_out_of_scope), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok)
                , () -> null);
    }

}
