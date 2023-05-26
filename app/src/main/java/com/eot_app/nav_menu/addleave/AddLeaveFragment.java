package com.eot_app.nav_menu.addleave;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.eot_app.R;
import com.eot_app.databinding.FragmentAddLeaveBinding;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.util_interfaces.MySpinnerAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class AddLeaveFragment extends AppCompatActivity implements View.OnClickListener, TextWatcher,LeaveList_view{

    FragmentAddLeaveBinding binding;
    private UserLeave_pi userLeave_pi;
    List<LeaveUserModel> listuser;

    private String STARTSELCTEDATE = "", STARTSELCTETIME = " 12:00 am", ENDSELCTETIME = " 11:59 pm", ENDSELCTEDATE = "";
    private final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            try {
                if (view.getTag().equals("time_from")) {
                    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);//hh:mm:ss a
                    Date startDate = formatter.parse(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);
                    STARTSELCTEDATE = "";
                    assert startDate != null;
                    STARTSELCTEDATE = " " + formatter.format(startDate);
                    binding.timeFrom.setText("");
                    binding.timeFrom.setText(STARTSELCTEDATE.concat(STARTSELCTETIME));
                    selectStartTime("START");


                } else if (view.getTag().equals("time_to")) {
                    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);//hh:mm:ss a
                    Date startDate = formatter.parse(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);
                    ENDSELCTEDATE = "";
                    assert startDate != null;
                    ENDSELCTEDATE = " " + formatter.format(startDate);
                    binding.timeTo.setText("");
                    binding.timeTo.setText(ENDSELCTEDATE.concat(ENDSELCTETIME));
                    selectStartTime("END");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
    };
    private AddLeaveViewModel addLeaveViewModel;
    int year, month, day;

    public AddLeaveFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_leave));

        addLeaveViewModel = new ViewModelProvider(this).get(AddLeaveViewModel.class);
        addLeaveViewModel.getFinishActivity().observe(this, aBoolean -> {
            if (aBoolean) {
                try {
                    AppUtility.progressBarDissMiss();
                    setResult(RESULT_OK, new Intent());
                    finish();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                initilaleDateTimeset();
                binding.notesEdt.setText("");
                binding.reasonEdt.setText("");
            }
        });


        addLeaveViewModel.getShowDialogs().observe(this, s -> {
            AppUtility.progressBarDissMiss();
            showMyDialog(s);
        });

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_add_leave);
        binding.userName.setOnClickListener(this);
        userLeave_pi=new UserLeave_pc(this);
        userLeave_pi.getuserlist();
        setUiLables();
    }


    public void setuserlist()
    {
        int index = 0;
        String[] list=new String[listuser.size()];
        AppUtility.spinnerPopUpWindow(binding.languageSpinner);
        for (LeaveUserModel ulist : listuser) {
            list[index] = ulist.getLeaveType();
            index++;
        }
        binding.languageSpinner.setAdapter(new MySpinnerAdapter(this,list));
        binding.languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LeaveUserModel leaveUserModel=listuser.get(i);
                binding.userName.setText(leaveUserModel.getLeaveType());
                Log.d("userid",leaveUserModel.getLtId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void showMyDialog(String msg) {
        AppUtility.alertDialog(this, "",
                msg,
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
    }

    private void selectStartTime(final String tag) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            String stime = updateTimeS(selectedHour, selectedMinute);
            if (tag.equals("START")) {
                STARTSELCTETIME = "";
                STARTSELCTETIME = " " + stime;
                binding.timeFrom.setText(STARTSELCTEDATE.concat(STARTSELCTETIME));
            } else if (tag.equals("END")) {
                ENDSELCTETIME = "";
                ENDSELCTETIME = " " + stime;
                binding.timeTo.setText(ENDSELCTEDATE.concat(ENDSELCTETIME));
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    public String updateTimeS(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        return String.valueOf(hours) + ':' +
                minutes + " " + timeSet;


    }


    private void setUiLables() {

        initilaleDateTimeset();
        AppUtility.setupUI(binding.parentLayout, this);

        binding.notesEdt.setText("");
        binding.reasonEdt.setText("");

        binding.notesEdt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.notes));
        binding.reasonEdt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_reason_leave));

        binding.submitButton.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_leave));
        createListners();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    private void initilaleDateTimeset() {
        binding.timeFrom.setText(AppUtility.getDateByFormat("dd-MM-yyyy").concat(STARTSELCTETIME));
        binding.timeTo.setText(AppUtility.getDateByFormat("dd-MM-yyyy").concat(ENDSELCTETIME));
        listuser=new ArrayList<>();
        emptyfields();
    }

    private void createListners() {
        binding.timeFrom.setOnClickListener(this);
        binding.timeTo.setOnClickListener(this);

        Objects.requireNonNull(binding.addNotesLayout.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(binding.addReasonLayout.getEditText()).addTextChangedListener(this);
        binding.submitButton.setOnClickListener(this);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_from:
                selectStartDate("time_from");
                break;
            case R.id.time_to:
                selectStartDate("time_to");
                break;
            case R.id.user_name:
                binding.languageSpinner.performClick();
                break;
            case R.id.submit_button:
                Date startDate, endDate;
                String s = "", e = "", datetiemform;
                datetiemform = "dd-MM-yyyy hh:mm a";
                try {
                    startDate = new SimpleDateFormat(datetiemform, Locale.getDefault()).parse(binding.timeFrom.getText().toString().trim());
                    endDate = new SimpleDateFormat(datetiemform, Locale.getDefault()).parse(binding.timeTo.getText().toString().trim());
                    assert startDate != null;
                    s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault()).format(startDate);
                    assert endDate != null;
                    e = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault()).format(endDate);
                    e = AppUtility.getDate(e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (!AppUtility.compareTwoDatesForTimeSheet2(s, e, "dd-MM-yyyy hh:mm:ss")) {
                    showMyDialog(LanguageController.getInstance().getMobileMsgByKey(AppConstant.time_sheet_date_error));
                } else {
                    AppUtility.progressBarShow(this);
                    int id=0;
                    for (int i=0;i<listuser.size();i++)
                    {
                        if (listuser.get(i).getLeaveType().equals(binding.userName.getText().toString()))
                        {
                            id= Integer.parseInt(listuser.get(i).getLtId());
                        }
                    }
                    addLeaveViewModel.getLeaveApiCall(binding.reasonEdt.getText().toString().trim(), binding.notesEdt.getText().toString().trim()
                            , s, e,id);
                }
                break;
        }

    }

    private void selectStartDate(String tag) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, datePickerListener, year, month, day);
        datePickerDialog.getDatePicker().setTag(tag);
        datePickerDialog.show();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (charSequence.length() >= 1) {
            if (charSequence.hashCode() == binding.notesEdt.getText().hashCode())
                binding.addNotesLayout.setHintEnabled(true);
            if (charSequence.hashCode() == binding.reasonEdt.getText().hashCode())
                binding.addReasonLayout.setHintEnabled(true);
        } else if (charSequence.length() <= 0) {
            if (charSequence.hashCode() == binding.notesEdt.getText().hashCode())
                binding.addNotesLayout.setHintEnabled(false);
            if (charSequence.hashCode() == binding.reasonEdt.getText().hashCode())
                binding.addReasonLayout.setHintEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void emptyfields() {
        binding.notesEdt.setText("");
        binding.reasonEdt.setText("");
        binding.notesEdt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.notes));
        binding.reasonEdt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_reason_leave));
        binding.userName.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_leave_type));
        binding.addNotesLayout.setHintEnabled(false);
        binding.addReasonLayout.setHintEnabled(false);
    }

    @Override
    public void setuserlist(List<LeaveUserModel> list) {
        this.listuser=list;
        setuserlist();
    }
}