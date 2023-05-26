package com.eot_app.nav_menu.appointment.list;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.eot_app.R;
import com.eot_app.databinding.AppoinmentFragmentLayoutBinding;
import com.eot_app.home_screens.MainActivity;
import com.eot_app.nav_menu.appointment.addupdate.AddAppointmentActivity;
import com.eot_app.nav_menu.appointment.calendar.data.Day;
import com.eot_app.nav_menu.appointment.calendar.widget.CollapsibleCalendar;
import com.eot_app.nav_menu.appointment.calendar.widget.UICalendar;
import com.eot_app.nav_menu.appointment.dbappointment.Appointment;
import com.eot_app.nav_menu.appointment.details.AppointmentDetailsActivity;
import com.eot_app.nav_menu.appointment.list.common.CommonAppointmentModel;
import com.eot_app.nav_menu.audit.addAudit.AddAuditActivity;
import com.eot_app.nav_menu.audit.audit_list.AuditDetails;
import com.eot_app.nav_menu.audit.audit_list.audit_mvp.model.AuditList_Res;
import com.eot_app.nav_menu.jobs.add_job.Add_job_activity;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_detail.JobDetailActivity;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AppointmentListFragment extends
        Fragment implements
        AdapterAppointments.OnAppointmentItemClicked,
        View.OnClickListener {
    private final int APPOINTMENT_INTENT_CODE = 987;
    AppointmentListViewModel alvm;
    AppoinmentFragmentLayoutBinding binding;
    AdapterAppointments adapterAppointments;
    MainActivity mainActivity;
    private boolean refreshCalendar;

    List<CommonAppointmentModel> liveTodayAppointments = new ArrayList<>();
    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "appointment_refresh" is broadcasted.
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d("appointment", "refreshed called");
            if (intent.getAction().equals("Archived_jobs")){
                alvm.loadFromServer();
            }else {
                if (alvm != null) alvm.refreshList();
            }
            refreshCalendar = true;
        }
    };

    private boolean isFBMenuOpened;

    public static AppointmentListFragment newInstance(String s) {
        AppointmentListFragment fragment = new AppointmentListFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", s);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)
            mainActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alvm = new ViewModelProvider(this).get(AppointmentListViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.appoinment_fragment_layout, container, false);
        binding.setLifecycleOwner(getActivity());
        binding.setAppinment(alvm);
        alvm.setSyncDone(mainActivity);
        setUILables();
        bindObervers();
        App_preference.getSharedprefInstance().setCalenderSelectedDate("");
        return binding.getRoot();
    }

    private void setUILables() {
        Objects.requireNonNull(getActivity()).setTitle("");
        binding.nolistTxt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.appointment_not_found));
        binding.tvFabAddAppoint.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_appointment));
        binding.tvFabJob.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_add_job));
        binding.tvFabAudit.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_add_audit));
    }

    private void bindObervers() {
        adapterAppointments = new AdapterAppointments(getActivity());
        adapterAppointments.setOnAppointmentItemClicked(this);
        binding.appointmentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.appointmentList.setAdapter(adapterAppointments);
        binding.swiperefresh.setOnRefreshListener(() -> {
            if (alvm != null)
                if (AppUtility.isInternetConnected())
                    alvm.refreshList();
                else {
                    binding.swiperefresh.setRefreshing(false);
                    AppUtility.alertDialog(getActivity(),
                            "",
                            LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network),
                            LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), null, null);
                }
        });
        alvm.getIsRefresh().observe(Objects.requireNonNull(getActivity()), aBoolean -> binding.swiperefresh.setRefreshing(aBoolean));
        alvm.getLiveTodayAppointments().observe(getActivity(), commonAppointmentModels -> {
            if (refreshCalendar) {
                searchEvents();
                refreshCalendar = false;
            }
            if (commonAppointmentModels != null && commonAppointmentModels.size() > 0) {
                binding.nolistLinear.setVisibility(View.GONE);
                adapterAppointments.setList(commonAppointmentModels);
                liveTodayAppointments.clear();
                liveTodayAppointments.addAll(commonAppointmentModels);
            } else {
                if (commonAppointmentModels == null) return;
                binding.nolistLinear.setVisibility(View.VISIBLE);
                adapterAppointments.setList(Collections.emptyList());
            }
        });
        binding.calendarView.expandIconView.setVisibility(View.GONE);
        Calendar todayDate = Calendar.getInstance();
        binding.calendarView.select(new Day(todayDate.get(Calendar.YEAR), todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DATE)));
        int todayMonth = todayDate.get(Calendar.MONTH) + 1;
        String s = todayDate.get(Calendar.DATE) + "-" + todayMonth + "-" + todayDate.get(Calendar.YEAR);
        alvm.searchRecordOnGivenDates(s);
        binding.calendarView.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                searchRecords();
            }

            @Override
            public void onItemClick(View v) {
                setTitle(binding.calendarView.getTitle());
            }

            @Override
            public void onDataUpdate() {
            }

            @Override
            public void onMonthChange() {
                setTitle(binding.calendarView.getTitle());
                searchEvents();
            }

            @Override
            public void onWeekChange(int position) {
                setTitle(binding.calendarView.getTitle());
            }
        });
        alvm.liveEvents.observe(getActivity(), eventList -> {
            if (eventList != null && eventList.size() > 0) {
                binding.calendarView.addEvents(eventList);
            }
        });
        alvm.getLiveNotificationRedirect().observe(getActivity(), aBoolean -> {
            if (aBoolean) {
                if (getActivity() instanceof MainActivity) {
                    String notificationDataId = ((MainActivity) getActivity()).getNotificationDataId();
                    ((MainActivity) getActivity()).setNotificationDataId(null);
                    if (!TextUtils.isEmpty(notificationDataId)) {
                        Appointment appointment = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().getAppointmentById(notificationDataId);
                        if (appointment != null) {
                            Intent intent = new Intent(getActivity(), AppointmentDetailsActivity.class);
                            intent.putExtra("data", appointment);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityForResult(intent, APPOINTMENT_INTENT_CODE);
                        }
                    }
                }
            }
        });
        binding.linearFabAudit.setOnClickListener(this);
        binding.linearFabJob.setOnClickListener(this);
        binding.linearFabAppointment.setOnClickListener(this);
        binding.backgroundView.setOnClickListener(this);
        searchEvents();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(mMessageReceiver,
                new IntentFilter("appointment_refresh"));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(mMessageReceiver,
                new IntentFilter("Archived_jobs"));
        refreshFullList();
    }

    private void searchEvents() {
        if (alvm != null && binding != null) {
            alvm.searchEvents(binding.calendarView.getMonth(), binding.calendarView.getYear());
        }
    }
    public void reverseList(){
        Collections.reverse(liveTodayAppointments);
        adapterAppointments.setList(liveTodayAppointments);
    }


    public void refreshFullList() {
        if (alvm != null)
            alvm.refreshList();
    }

    private void setTitle(String title) {
        if (mainActivity != null)
            mainActivity.setDateAsTitle(title);
    }

    public void refreshAppointmentList() {
        if (alvm != null) {
            alvm.localRefresh();
            searchEvents();
            alvm.refreshList();
        }
    }


    public void searchRecords() {
        Day selectedItem = binding.calendarView.getSelectedItem();
        int month = selectedItem.getMonth() + 1;
        alvm.searchRecordOnGivenDates(selectedItem.getDay() + "-" + month + "-" + selectedItem.getYear());
        App_preference.getSharedprefInstance().setCalenderSelectedDate(selectedItem.getDay() + "-" + month + "-" + selectedItem.getYear());
        Log.v("Selected Date :::", App_preference.getSharedprefInstance().getCalenderSelectedDate());
    }

    @Override
    public void onAppointItemClick(CommonAppointmentModel cpm) {
        if (cpm != null)
            if (cpm.getType() == 0) {
                Appointment appointment = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().getAppointmentById(cpm.getId());
                if (appointment != null) {
                    Intent intent = new Intent(getActivity(), AppointmentDetailsActivity.class);
                    intent.putExtra("data", appointment);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityForResult(intent, APPOINTMENT_INTENT_CODE);
                }
            } else if (cpm.getType() == 1) {
                Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(cpm.getId());
                if (job != null)
                    openJobDetails(job);
            } else if (cpm.getType() == 2) {
                AuditList_Res audit = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().getAuditsEquipmentList(cpm.getId());
                if (audit != null) {
                    Intent intent = new Intent(getActivity(), AuditDetails.class);
                    intent.putExtra("audit", audit);
                    String str = new Gson().toJson(audit);
                    intent.putExtra("auditstr", str);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
    }

    private void openJobDetails(Job job) {
        Intent intentJobDeatis = new Intent(getActivity(), JobDetailActivity.class);
        String strjob = new Gson().toJson(job);
        intentJobDeatis.putExtra("JOBS", strjob);
        intentJobDeatis.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intentJobDeatis,77);
    }


    public void changeCollpase() {
        try {
            binding.calendarView.expandIconView.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getImgRotationAngle() {
        if (binding != null && binding.calendarView != null) {
            if (binding.calendarView.getState() == UICalendar.STATE_EXPANDED)
                return 0;
            else if (binding.calendarView.getState() == UICalendar.STATE_COLLAPSED)
                return 180;
        }
        return 0;
    }

    public void showFloatingButtons() {
        if (isFBMenuOpened) closeFABMenu();
        else showFBButtons();
    }

    private void showFBButtons() {
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_color)));
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#90ffffff")));
        binding.backgroundView.setVisibility(View.VISIBLE);

        // for checking permission of audit module

        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAuditVisible() == 0)
            binding.linearFabAudit.setVisibility(View.VISIBLE);
        else binding.linearFabAudit.setVisibility(View.GONE);

        // for checking permission of job add or not

        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsJobAddOrNot() == 0)
            binding.linearFabJob.setVisibility(View.VISIBLE);
        else binding.linearFabJob.setVisibility(View.GONE);

        // for checking permission of appointment  module
        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAppointmentVisible() == 0)
            binding.linearFabAppointment.setVisibility(View.VISIBLE);
        else binding.linearFabAppointment.setVisibility(View.GONE);


        // for managing the position of the view after adding animation
        if (binding.linearFabAppointment.getVisibility() != View.VISIBLE) {
            // for managing the position of the view after adding animation
            if (binding.linearFabJob.getVisibility() == View.VISIBLE) {
                binding.linearFabJob.animate().translationY(getResources().getDimension(R.dimen.standard_55));
                binding.linearFabAudit.animate().translationY(getResources().getDimension(R.dimen.standard_100));
            } else {
                binding.linearFabAudit.animate().translationY(getResources().getDimension(R.dimen.standard_55));
            }
        } else {
            binding.linearFabAppointment.animate().translationY(getResources().getDimension(R.dimen.standard_55));
            // for managing the position of the view after adding animation
            if (binding.linearFabJob.getVisibility() == View.VISIBLE) {
                binding.linearFabJob.animate().translationY(getResources().getDimension(R.dimen.standard_100));
                binding.linearFabAudit.animate().translationY(getResources().getDimension(R.dimen.standard_145));
            } else {
                binding.linearFabAudit.animate().translationY(getResources().getDimension(R.dimen.standard_100));
            }
        }


        isFBMenuOpened = true;
    }


    private void closeFABMenu() {
        isFBMenuOpened = false;
        binding.linearFabAudit.animate().translationY(0);
        binding.linearFabJob.animate().translationY(0);
        binding.linearFabAppointment.animate().translationY(0);
        binding.linearFabAppointment.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFBMenuOpened) {
                    binding.backgroundView.setVisibility(View.GONE);
                    binding.linearFabAppointment.setVisibility(View.GONE);
                    binding.linearFabJob.setVisibility(View.GONE);
                    binding.linearFabAudit.setVisibility(View.GONE);
                    Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backgroundView:
                closeFABMenu();
                break;
            case R.id.linearFabAppointment:
                Intent open_add_appointment = new Intent(getActivity(), AddAppointmentActivity.class);
                open_add_appointment.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                open_add_appointment.putExtra("CalenderDate", App_preference.getSharedprefInstance().getCalenderSelectedDate());
                startActivityForResult(open_add_appointment, MainActivity.ADDAPPOINTMENT);
                closeFABMenu();
                break;
            case R.id.linearFabJob:
                Intent open_add_job = new Intent(getActivity(), Add_job_activity.class);
                open_add_job.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                open_add_job.putExtra("CalenderDate", App_preference.getSharedprefInstance().getCalenderSelectedDate());
                startActivity(open_add_job);
                closeFABMenu();
                break;
            case R.id.linearFabAudit:
                Intent open_add_aduit = new Intent(getActivity(), AddAuditActivity.class);
                open_add_aduit.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                open_add_aduit.putExtra("CalenderDate", App_preference.getSharedprefInstance().getCalenderSelectedDate());
                startActivity(open_add_aduit);
                closeFABMenu();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (alvm != null)
            alvm.refreshAppoiListFromServer();
        if (requestCode == 15 && data != null)
            if (alvm != null) alvm.localRefresh();
            else if (requestCode == APPOINTMENT_INTENT_CODE && data != null) {
                if (alvm != null) {
                    alvm.refreshAppoiListFromServer();
                    alvm.localRefresh();
                }
            }
    }


    @Override
    public void onDestroyView() {
        try {
            if (mMessageReceiver != null)
                LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.e("", "");
        super.onDestroy();
    }
}
