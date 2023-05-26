package com.eot_app.home_screens;

import static com.eot_app.nav_menu.report.ReportFragment.REPORT_TIMESHEET;
import static com.eot_app.nav_menu.usr_time_sheet_pkg.TimeSheetFragment.TIMESHEET;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.eot_app.R;
import com.eot_app.UploadDocumentActivity;
import com.eot_app.firebases.RealTimeDBController;
import com.eot_app.login_next.Login2Activity;
import com.eot_app.login_next.login_next_model.ResLoginData;
import com.eot_app.login_next.login_next_model.Right;
import com.eot_app.nav_menu.addlead.AddLeadFragment;
import com.eot_app.nav_menu.addleave.AddLeaveFragment;
import com.eot_app.nav_menu.admin_fw_chat_pkg.chatmember_list_pkg.ChatUsersListFragment;
import com.eot_app.nav_menu.admin_fw_chat_pkg.sonam_user_user_chat_pkg.user_chat_controller.UserToUserChatController;
import com.eot_app.nav_menu.admin_fw_chat_pkg.sonam_user_user_chat_pkg.usertouser_model.UserChatModel;
import com.eot_app.nav_menu.appointment.list.AppointmentListFragment;
import com.eot_app.nav_menu.appointment.list.AppointmentListViewModel;
import com.eot_app.nav_menu.audit.addAudit.AddAuditActivity;
import com.eot_app.nav_menu.audit.audit_list.FragmentAuditList;
import com.eot_app.nav_menu.audit.audit_list.audit_mvp.model.AuditList_Res;
import com.eot_app.nav_menu.audit.nav_scan.BarcodeScanActivity;
import com.eot_app.nav_menu.checkin_checkout_dialog.DialogCheckInUploadDocuments;
import com.eot_app.nav_menu.client.add_client.AddClient;
import com.eot_app.nav_menu.client.clientlist.ClientListNav;
import com.eot_app.nav_menu.expense.add_expense.AddExpenseActivity;
import com.eot_app.nav_menu.expense.expense_list.ExpenseListFragment;
import com.eot_app.nav_menu.jobs.add_job.Add_job_activity;
import com.eot_app.nav_menu.jobs.job_controller.ChatController;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_detail.job_status_pkg.JobStatus_Controller;
import com.eot_app.nav_menu.jobs.job_filter.JobFilter_Activity;
import com.eot_app.nav_menu.jobs.job_list.JobList;
import com.eot_app.nav_menu.quote.add_quotes_pkg.AddQuotes_Activity;
import com.eot_app.nav_menu.quote.filter_quotes_pkg.QuotesFilter_Activity;
import com.eot_app.nav_menu.quote.quotes_list_pkg.QuotesFilter;
import com.eot_app.nav_menu.quote.quotes_list_pkg.Quotes_List;
import com.eot_app.nav_menu.report.ReportFragment;
import com.eot_app.nav_menu.setting.ModuleCode;
import com.eot_app.nav_menu.setting.ModuleCodeModel;
import com.eot_app.nav_menu.setting.SettingActivity;
import com.eot_app.nav_menu.userleave_list_pkg.UserLeaveListFragment;
import com.eot_app.nav_menu.usr_time_sheet_pkg.TimeSheetFragment;
import com.eot_app.recievers.BatteryStatusReceiver;
import com.eot_app.services.CustomLogMessageFormat;
import com.eot_app.services.ForegroundService2;
import com.eot_app.services.GpsUtils;
import com.eot_app.services.Service_apis;
import com.eot_app.services.SyncDataJobS;
import com.eot_app.services.SyncDataJobService;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.ChatApp_Preference;
import com.eot_app.utility.DialogPermission;
import com.eot_app.utility.DropdownListBean;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.JobReciver;
import com.eot_app.utility.LocaleHelper;
import com.eot_app.utility.MyDigitalClock;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.db.OfflineDataController;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.language_support.Language_Model;
import com.eot_app.utility.language_support.Language_Preference;
import com.eot_app.utility.util_interfaces.ApiCallbackObserver;
import com.eot_app.utility.util_interfaces.Callback_AlertDialog;
import com.eot_app.utility.util_interfaces.OnFragmentInteractionListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.BuildConfig;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hypertrack.hyperlog.HyperLog;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends UploadDocumentActivity implements MainActivityView,
        JobReciver.InternetConectionListner_JOB,
        OnFragmentInteractionListener,
        ApiCallbackObserver,
        ResultCallback<LocationSettingsResult>,
        View.OnClickListener //{
        , MyDigitalClock.NotifyClock {

    ImageView sync_iv;
    DialogCheckInUploadDocuments dialogUpdateDocuments;
    public static final int AuditADD = 17;
    public static final int ADDUSERLEAVE = 18;
    public static final int ClientADD = 11;
    public static final int ADDEXPENSES = 14;
    public static final int ADDAPPOINTMENT = 15;
    public static final int FILTERJOBLIST = 12;
    public static final int FILTERQUOTESLIST = 13;
    public static final int REQUEST_CHECK_SETTINGS = 101;
    public static final int LOCATION_REQUEST = 1000;
    public static final int ADDJOB = 16;
    private static final String TAG = "MainActivity";
    private static final int BAR_CODE_REQUEST = 122;
    private static final int SETTING_CODE = 545;
    public static boolean USERLOGOUT = false;
    private final BroadcastReceiver br = new JobReciver();
    final int logExpireTime = 86400; //set log expire time  in second (86400 sec 24 hr)
    public boolean isRegistered;
    public boolean isSyncDone = false;
    public boolean isNetworkDisconnected = false;
    boolean auditNoti;
    //firebase ref path
    String path;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat serverFormat = new SimpleDateFormat("hh:mm:ss a");
    LinearLayout parent_client;
    private String notificationDataId;
    private BatteryStatusReceiver batteryStatusReceiver;
    public MainActivity_pi mainActivity_pi;
    private Menu menu;
    ImageView profile_img;
    TextView profile_name, profile_email, version_name;
    NavigationView navigationView;
    private ClientListNav clientList2Fragment;
    private JobList joblistfragment;
    private AddLeadFragment addLeadFragment;
    private FragmentAuditList fragmentAuditList;
    private ChatUsersListFragment chatmemberListFragment;
    private ExpenseListFragment expenseListFragment;
    private ReportFragment reportFragment;
    private UserLeaveListFragment userLeaveListFragment;
    private TimeSheetFragment timeSheetFragment;
    private AppointmentListFragment appointmentListFragment;
    private AppointmentListViewModel appointmentListViewModel;
    private Quotes_List quotesList;
    private boolean isConnetionFIrstTime = false;
    boolean isGPS = false;
    TextView title_jobs, title_client,title_addLead,
            title_logout, title_setting, title_check_in_out, title_qoutes,
            title_audit, title_scan, chat_textview, badge_count, title_expence, title_report, title_calender, title_timeSheet, title_addLeave,timezone;
    private ImageView loader;
    private boolean isQuotesSearchSeleted, isJobSearchSelected = true, isAuditSearch, isSingleChatSearch, isexpenseSearch;
    private FloatingActionButton fab;
    private Button versionUpdateBtn;
    private LinearLayout chat_layout;
    private AppCompatImageView img_month_arrow;
    private AppCompatTextView tv_custom_title;
    boolean isCalendarSelected;
    public ImageView img_sorting;
    TextView text_clock, check_in_time;
    BroadcastReceiver updateUIReceiver;
    BroadcastReceiver showAlertReceiver;
    BottomSheetDialog bottomSheetDialog;
    /**
     * internet off/on event to update user status
     */
    BroadcastReceiver networkSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            if (ni != null && ni.isConnected()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("isOnline", 1);
                int mapStatus = checkUserMapStatus();
                hashMap.put("gpsStatus", mapStatus);
                RealTimeDBController.setStatus(hashMap);
                hashMap.clear();
                RealTimeDBController.setOnDisconnectFirebase();
                Log.d("MainActivity", " setOnDisconnectFirebase");
                if (!isSyncDone && isNetworkDisconnected) {
                    isNetworkDisconnected = false;
                    retryCall();
                }
            } else {
                isNetworkDisconnected = true;
            }
        }
    };

    public String getNotificationDataId() {
        return notificationDataId;
    }

    public void setNotificationDataId(String notificationDataId) {
        this.notificationDataId = notificationDataId;
    }


    private void registerBatteryReceiver() {
        if (batteryStatusReceiver == null) {
            batteryStatusReceiver
                    = new BatteryStatusReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryStatusReceiver, intentFilter);
        }
    }

    public void setDateAsTitle(String date) {
        if (tv_custom_title != null && date != null)
            tv_custom_title.setText(date);
    }

    @Override
    public void setClockTime(String str) {
        try {
            String[] strSplite = str.split(":");
            String strHrMm = strSplite[0] + ":" + strSplite[1];

            SpannableStringBuilder builder2 = new SpannableStringBuilder();
            SpannableString ss1 = new SpannableString(strHrMm);
            ss1.setSpan(new RelativeSizeSpan(1f), 0, strHrMm.length(), 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, strHrMm.length(), 0);// set color
            builder2.append(ss1);

            SpannableString ss2 = new SpannableString(":" + strSplite[2]);
            ss2.setSpan(new RelativeSizeSpan(0.8f), 0, (":" + strSplite[2]).length(), 0); // set size
            builder2.append(ss2);

            text_clock.setText(builder2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
           after language change update status json according the language
         **/
        // first sync start

        sync_iv = findViewById(R.id.sync_iv);

        if (AppUtility.isInternetConnected()) {

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.progress_anim);
            sync_iv.startAnimation(animation);
            sync_iv.setVisibility(View.VISIBLE);

            if (!isSyncDone) {
                SyncDataJobS.scheduleJob(this);
                Log.v("Main Activity", "Sync completed " + " --" + isSyncDone);
            }

            IntentFilter filter = new IntentFilter();
            filter.addAction("updateUi");
            // update ui sync data
            updateUIReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //UI update here
                    Log.v("Main Activity", "Sync completed");
                    isSyncDone = true;
                    sync_iv.clearAnimation();
                    sync_iv.setVisibility(View.GONE);
                    stopService(new Intent(MainActivity.this, SyncDataJobService.class));
                }
            };
            registerReceiver(updateUIReceiver, filter);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("showAlert");
        // update ui sync data
        showAlertReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //UI update here
                Log.v("Main Activity", "Network error");
                showRetryDialog();
            }
        };
        registerReceiver(showAlertReceiver, filter);

        // first sync end

        JobStatus_Controller.getInstance().getStatusList();
        JobStatus_Controller.getInstance().setDynamicStatusList();
        //init the hyperlog when super admin enabled the permission for report bug
        if (App_preference.getSharedprefInstance().getLoginRes().getIsEmailLogEnable().equals("1")) {
            HyperLog.initialize(this, logExpireTime, new CustomLogMessageFormat(this));
            HyperLog.setLogLevel(Log.VERBOSE);
        }

        UserToUserChatController.getInstance().setMainActivity(this);
        craeteUserChatListner();
        initializelables();

        registerDeregisterReceiver(true);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Launch Current/root activity when app at background
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();

            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }


//      View Initialization
        initializeViews();

//        set user online offline in in app version
        ChatController.getInstance().setAppUserOnline(1);

//        Set Observer for handle callback of offline data about add job and add client
        EotApp.getAppinstance().setApiObserver(this);

//  generalized fab for all the screens to add the particular screen data
        fab.setOnClickListener(view -> {
            EotApp.getAppinstance().setApiObserver(MainActivity.this);
            if (toolbar.getTitle().equals(LanguageController.getInstance().getMobileMsgByKey(AppConstant.jobs))) {
                Intent intent = new Intent(MainActivity.this, Add_job_activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent, ADDJOB);
            } else if (toolbar.getTitle().equals(LanguageController.getInstance().getMobileMsgByKey(AppConstant.clients))) {
                Intent intent = new Intent(MainActivity.this, AddClient.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent, ClientADD);
            } else if (toolbar.getTitle().equals(LanguageController.getInstance().getMobileMsgByKey(AppConstant.quotes))) {
                Intent addQuoetIntent = new Intent(MainActivity.this, AddQuotes_Activity.class);
                addQuoetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(addQuoetIntent);
            } else if (toolbar.getTitle().equals(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_expence))) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent, ADDEXPENSES);
            } else if (toolbar.getTitle().equals(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_report))) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent, ADDEXPENSES);
            } else if (isCalendarSelected) {
                if (appointmentListFragment != null)
                    appointmentListFragment.showFloatingButtons();
            } else if (toolbar.getTitle().equals(LanguageController.getInstance().getMobileMsgByKey(AppConstant.audit_nav))) {
                Intent intent = new Intent(MainActivity.this, AddAuditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent, AuditADD);
            } else if (toolbar.getTitle().equals(LanguageController.getInstance().getMobileMsgByKey(AppConstant.user_leave))) {
                Intent intent = new Intent(MainActivity.this, AddLeaveFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent, ADDUSERLEAVE);
            }
        });
//        job_navigation drawer setup
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                setChatbatchCount();
                Log.e("", "");
// do what ever you want
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
// do what ever you want
                setChatbatchCount();
                Log.e("", "");
                AppUtility.hideSoftKeyboard(MainActivity.this);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        /* new dynamic menu add in main drawer   */
        setValues();
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.containsKey("NOTIFICATIONTAG")) {
                    if (bundle.containsKey("id")) {
                        notificationDataId = bundle.getString("id");
                    }
                    if (Objects.equals(bundle.get("NOTIFICATIONTAG"), "AUDIT")) {
                        if (fab != null)
                            fab.show();
                        auditNoti = true;
                        updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.audit_nav),
                                fragmentAuditList);
                    } else if (Objects.equals(bundle.get("NOTIFICATIONTAG"), "JOB")) {
                        updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.jobs), joblistfragment);
                    } else if (Objects.equals(bundle.get("NOTIFICATIONTAG"), "APPOINTMENT")) {
                        isCalendarSelected = true;
                        title_calender.performClick();
                    } else if (Objects.equals(bundle.get("NOTIFICATIONTAG"), "updateLeave")) {
                        updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.user_leave), userLeaveListFragment);
                    } else {
                        title_jobs.performClick();
                    }
                } else {
                    title_jobs.performClick();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            title_jobs.performClick();
        }
        Log.e("", "");
        try {
            MyDigitalClock.getInstance().setNotifyClock(this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void loadDefaultPageView() {
        /*check default page view permission and load fragment*/
        Right right = App_preference.getSharedprefInstance().getLoginRes().getRights().get(0);
        int defaultPageView = Language_Preference.getSharedprefInstance().getDefaultPageView();
        switch (defaultPageView) {
            case ModuleCode.JOB:
                title_jobs.performClick();
                break;
            case ModuleCodeModel.CALENDAR:
//                if (right.getIsAppointmentVisible() == 0) {
                if (right.getIsSchedular() == 0) {
                    isCalendarSelected = true;
                    title_calender.performClick();
                } else {
                    title_jobs.performClick();
                    Language_Preference.getSharedprefInstance().setDefaultPageView(ModuleCodeModel.JOB);
                }
                break;
            case ModuleCodeModel.AUDIT:
                if (right.getIsAuditVisible() == 0)
                    title_audit.performClick();
                else setDefaultPageSettingIsAllowed();
                break;

            case ModuleCodeModel.QUOTES:
                if (right.getIsQuoteVisible() == 0)
                    title_qoutes.performClick();
                else setDefaultPageSettingIsAllowed();
                break;
            case ModuleCodeModel.CLIENTS:
                if (right.getIsClientVisible() == 0)
                    parent_client.performClick();
                else setDefaultPageSettingIsAllowed();
                break;
            case ModuleCodeModel.CHATS:
                if (App_preference.getSharedprefInstance().getLoginRes().getIsOnetoOneChatEnable().equals("1"))
                    chat_textview.performClick();
                else setDefaultPageSettingIsAllowed();
                break;
            case ModuleCodeModel.EXPENSES:
                if (right.getIsExpenseVisible() == 0)
                    title_expence.performClick();
                else setDefaultPageSettingIsAllowed();
                break;
            case ModuleCodeModel.REPORT:
                if (right.getIsExpenseVisible() == 0)
                    title_report.performClick();
                else setDefaultPageSettingIsAllowed();
                break;
            default:
                setDefaultPageSettingIsAllowed();
        }

    }


    /**
     * 1. calendar priority if permission is allowed if not than
     * 2. Job will be load as default setting
     * check the default permission of appointment can be set or not
     */
    private void setDefaultPageSettingIsAllowed() {
        Right right = App_preference.getSharedprefInstance().getLoginRes().getRights().get(0);
//        if (right.getIsAppointmentVisible() == 0) {
        // changed calender permission
        if (right.getIsSchedular() == 0) {
            Language_Preference.getSharedprefInstance().setDefaultPageView(ModuleCodeModel.CALENDAR);
            isCalendarSelected = true;
            title_calender.performClick();
        } else {
            title_jobs.performClick();
            Language_Preference.getSharedprefInstance().setDefaultPageView(ModuleCodeModel.JOB);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.menu == null) {
            this.menu = menu;
        }
        if (getIntent().getExtras() == null)
            loadDefaultPageView();
        return super.onPrepareOptionsMenu(menu);
    }

    private void initializelables() {
        img_month_arrow = findViewById(R.id.img_month_arrow);
        img_month_arrow.setOnClickListener(this);

        tv_custom_title = findViewById(R.id.tv_custom_title);
        tv_custom_title.setOnClickListener(this);

        img_sorting = findViewById(R.id.img_sorting);
        img_sorting.setOnClickListener(this);

        title_addLead = findViewById(R.id.title_addLead);
        title_addLead.setOnClickListener(this);

        title_audit = findViewById(R.id.title_audit);
        title_audit.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.audit_nav));
        title_scan = findViewById(R.id.title_scan);
        title_scan.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.detail_scan_barcode));
        title_jobs = findViewById(R.id.title_jobs);
        title_jobs.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.jobs));
        title_client = findViewById(R.id.title_client);
        title_client.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.clients));

        title_logout = findViewById(R.id.title_logout);
        title_logout.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.logout));
        title_setting = findViewById(R.id.title_setting);
        title_setting.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.settings));
        title_check_in_out = findViewById(R.id.title_check_in_out);
        loader = findViewById(R.id.loader);
        title_qoutes = findViewById(R.id.title_qoutes);
        title_qoutes.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.quotes));

        title_expence = findViewById(R.id.title_expence);
        title_expence.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_expence));

        title_report = findViewById(R.id.title_report);
        title_report.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_report));
        title_timeSheet = findViewById(R.id.title_timeSheet);
        title_timeSheet.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_timeSheet));

        title_calender = findViewById(R.id.title_calender);
        title_calender.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_appointments));

        timezone=findViewById(R.id.timezone);

//        isLeaveAddEnable


        LinearLayout parent_add_leave = findViewById(R.id.parent_add_leave);
        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsLeaveAddEnable() != null && App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsLeaveAddEnable().equals("0")) {
            parent_add_leave.setVisibility(View.VISIBLE);
        } else {
            parent_add_leave.setVisibility(View.GONE);
        }


        title_addLeave = findViewById(R.id.title_addLeave);
        title_addLeave.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.user_leave));

        chat_layout = findViewById(R.id.chat_layout);
        chat_textview = findViewById(R.id.chat_textview);
        chat_textview.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.side_menu_title_chats));
        badge_count = findViewById(R.id.badge_count);
        versionUpdateBtn = findViewById(R.id.versionUpdateBtn);
        versionUpdateBtn.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.update));
        versionUpdateBtn.setOnClickListener(this);

        text_clock = findViewById(R.id.text_clock);
        check_in_time = findViewById(R.id.check_in_time);
        updateVersionForcFully();

    }

    private void downloadJsonFile(float app_version) {
        if (app_version > App_preference.getSharedprefInstance().getLanFileVer()) {
            for (Language_Model lan_list : App_preference.getSharedprefInstance().getLoginRes().getLanguageList()) {
                if (lan_list.getFileName().equals(Language_Preference.getSharedprefInstance().getlanguageFilename())) {
                    AppUtility.progressBarShow(this);
                    setLanguage2(lan_list);
                    break;
                }
            }
        }
    }

    private void setLanguage2(final Language_Model language_model) {
        String lan_url = language_model.getFilePath() + language_model.getFileName() + ".json";
        LanguageController.getInstance().downloadFile(lan_url, () -> {
            App_preference.getSharedprefInstance().setLanFileVer(Float.parseFloat(BuildConfig.VERSION_NAME));
            AppUtility.progressBarDissMiss();
            return null;
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onResume() {
        //        set user online offline in in app version
        ChatController.getInstance().setAppUserOnline(1);
        Log.e("MainActivity","OnResumeCalled");
        super.onResume();
    }

    public void registerDeregisterReceiver(boolean action) {
        if (action) {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            this.registerReceiver(br, filter);
        } else {
            unregisterReceiver(br);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {
        //NavigationView navigationView
        profile_img = findViewById(R.id.profile_img);
        profile_email = findViewById(R.id.profile_email);
        profile_name = findViewById(R.id.profile_name);
        version_name = findViewById(R.id.version_name);

        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (version != null)
                version_name.setText("V " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String full_name = App_preference.getSharedprefInstance().getLoginRes().getFnm() + " " + App_preference.getSharedprefInstance().getLoginRes().getLnm();
        String email = App_preference.getSharedprefInstance().getLoginRes().getEmail();

        String img_url = App_preference.getSharedprefInstance().getLoginRes().getImg();
        if (email != null) {
            profile_name.setText(full_name);
            profile_email.setText(email);
            if (!img_url.equals("")) {
                Picasso.with(this).load(App_preference.getSharedprefInstance().getBaseURL() + img_url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(profile_img);
            }
        }
    }

    private void initializeViews() {
//        fragment initialize


//init path for firebase realtime DB ref
        path =
                App_preference.getSharedprefInstance().getRegion()
                        + "/cmp" +
                        App_preference.getSharedprefInstance().getLoginRes().getCompId().toLowerCase() + "/users/usr"
                        + App_preference.getSharedprefInstance().getLoginRes().getUsrId();

        RealTimeDBController.initFirebase(path);

        registerNetworkReceiver();

        parent_client = findViewById(R.id.parent_client);


        if (App_preference.getSharedprefInstance().getLoginRes().getRights() != null)
            if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsClientVisible() != 0) {
                parent_client.setVisibility(View.GONE);
            }

//      hide/show quotes
        LinearLayout parent_quotes = findViewById(R.id.parent_quotes);
        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsQuoteVisible() != 0) {
            parent_quotes.setVisibility(View.GONE);
        }

        /*visible/gone audit according to permission**/
        LinearLayout audit_menu_layout = findViewById(R.id.audit_menu_layout);
        LinearLayout parent_scan = findViewById(R.id.parent_scan);
        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAuditVisible() != 0) {
            audit_menu_layout.setVisibility(View.GONE);
            parent_scan.setVisibility(View.GONE);
        } else {
            if (App_preference.getSharedprefInstance().getLoginRes().getIsEquipmentEnable().equals("1"))
                parent_scan.setVisibility(View.VISIBLE);
            else parent_scan.setVisibility(View.GONE);
        }


        /*visible/gone Expense according to permission**/
        LinearLayout parent_expence = findViewById(R.id.parent_expence);
        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsExpenseVisible() != 0) {
            parent_expence.setVisibility(View.GONE);
        }


        //visible/gone TimeZone text
        LinearLayout timezonelayout=findViewById(R.id.timezonelayout);
        if (App_preference.getSharedprefInstance().getLoginRes().getIsAutoTimeZone().equals("1")
                &&!App_preference.getSharedprefInstance().getLoginRes().getLoginUsrTz().equals(TimeZone.getDefault().getID()))
        {
            timezonelayout.setVisibility(View.VISIBLE);
            timezone.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.timezone_Message)+" "+App_preference.getSharedprefInstance().getLoginRes().getLoginUsrTz());
        }

        /*visible/gone appointment according to permission**/
        // this permission will work on the basis of issheduler
        LinearLayout parent_calender = findViewById(R.id.parent_calender);
        /*if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAppointmentVisible() != 0) {*/
        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsSchedular() != 0) {
            parent_calender.setVisibility(View.GONE);
        }

        /*one to one chat permission to hide and show*/
        if (App_preference.getSharedprefInstance().getLoginRes().getIsOnetoOneChatEnable().equals("1")) {
            chat_layout.setVisibility(View.VISIBLE);
        } else {
            chat_layout.setVisibility(View.GONE);
        }


        LinearLayout parent_timeSheet = findViewById(R.id.parent_timeSheet);
        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsTimeSheetEnableMobile() != null && App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsTimeSheetEnableMobile().equals("0")) {
            parent_timeSheet.setVisibility(View.VISIBLE);
        } else {
            parent_timeSheet.setVisibility(View.GONE);
        }
        LinearLayout parent_report = findViewById(R.id.parent_report);
        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsCheckInOutEnableMobile() != null && App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsCheckInOutEnableMobile().equals("0")) {
            parent_report.setVisibility(View.VISIBLE);
        } else {
            parent_report.setVisibility(View.GONE);
        }


        title_jobs.setOnClickListener(this);
        parent_client.setOnClickListener(this);
        //title_language.setOnClickListener(this);
        title_qoutes.setOnClickListener(this);
        title_audit.setOnClickListener(this);
        title_scan.setOnClickListener(this);
        chat_textview.setOnClickListener(this);
        title_expence.setOnClickListener(this);
        title_report.setOnClickListener(this);
        title_timeSheet.setOnClickListener(this);
        title_calender.setOnClickListener(this);
        title_addLeave.setOnClickListener(this);


        findViewById(R.id.logout_layout).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);

        mainActivity_pi = new MainActivity_pc(this);
        clientList2Fragment = ClientListNav.newInstance("1", "2");
        fragmentAuditList = FragmentAuditList.newInstance(true);
        addLeadFragment = AddLeadFragment.newInstance();
        joblistfragment = JobList.newInstance("1", "2");
        quotesList = Quotes_List.newInstance("1", "2");
        chatmemberListFragment = ChatUsersListFragment.newInstance("", "");
        expenseListFragment = ExpenseListFragment.newInstance(0);
        reportFragment = ReportFragment.newInstance("", "");
        userLeaveListFragment = UserLeaveListFragment.newInstance("", "");
        timeSheetFragment = TimeSheetFragment.newInstance("", "");
        appointmentListFragment = AppointmentListFragment.newInstance("");
        appointmentListViewModel=new AppointmentListViewModel(getApplication());

        registerBatteryReceiver();

        if (App_preference.getSharedprefInstance().getLoginRes().getIsFWgpsEnable().equals("1")) {
            HyperLog.i(TAG, "Admin Enabled tracking Permission");
            userPerMissionForLocation();
        } else {
            HyperLog.i(TAG, "Admin Disabled tracking Permission");
            //        stop service on logout.
            if (EotApp.getAppinstance().isMyServiceRunning(ForegroundService2.class)) {
                Intent stopIntent = new Intent(this, ForegroundService2.class);
                stopIntent.setAction(ForegroundService2.STOPFOREGROUND_ADMINDENIED_ACTION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(stopIntent);
                } else {
                    startService(new Intent(stopIntent));
                }
            }
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(ForegroundService2.F_STATUS_FIELD, 6);
            setOnlineOffline(hashMap);
        }


        title_check_in_out.setOnClickListener(this);

        // initialize the clock timer instance
        MyDigitalClock.getInstance().createTimerInstance();

        //check the time shift ,and last check in time for calculation and UI set
        testTimeShiftEbd();


        fab = findViewById(R.id.fab);


        initFirebase();
    }

    private void checkInViewSet() {

        HyperLog.e("TAG","Check in checkout checkInViewSet called");

        title_check_in_out.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.check_in));
        check_in_time.setVisibility(View.GONE);
        App_preference.getSharedprefInstance().setCheckInTime("");
        // App_preference.getSharedprefInstance().setLastAppKillTime("");
        MyDigitalClock.getInstance().stopTimerCounting();
        App_preference.getSharedprefInstance().setcheckId("");

        ResLoginData data = App_preference.getSharedprefInstance().getLoginRes();
        data.setCheckId("");
        App_preference.getSharedprefInstance().setLoginResponse(new Gson().toJson(data));
    }

    public void testTimeShiftEbd() {
        if (!App_preference.getSharedprefInstance().getcheckId().equals("")) {

            HyperLog.e("TAG","Check in checkout testTimeShiftEbd CheckIdNotEmpty");
            boolean tets = AppUtility.compareCheckInOutTime();
            if (tets) {
                HyperLog.e("TAG","Check in checkout testTimeShiftEbd tets true");
                title_check_in_out.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.check_out));
                if (App_preference.getSharedprefInstance().getCheckInTime() != null
                        && !App_preference.getSharedprefInstance().getCheckInTime().equals("")) {
                    MyDigitalClock.getInstance().startTimerCounting();
                    // changed by shivani 21 April 2022
//                    check_in_time.setText(App_preference.getSharedprefInstance().getCheckInTime());
                    check_in_time.setText(App_preference.getSharedprefInstance().getCheckInTime().split("-")[1]);
                    check_in_time.setVisibility(View.VISIBLE);
                }
                else {
                    check_in_time.setVisibility(View.GONE);
                }
            } else {
                HyperLog.e("TAG","Check in checkout testTimeShiftEbd CheckIdNotEmpty tets false");
                checkInViewSet();
            }
        } else {
            HyperLog.e("TAG","Check in checkout testTimeShiftEbd CheckIdEmpty");
            checkInViewSet();
        }
    }

    /***login user Inactive & set status offline ***/
    public void setusersChatListnerS() {
        AppUtility.alertDialog2(this,
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.account_deactivated),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.deactivated_msg),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok),
                "", new
                        Callback_AlertDialog() {
                            @Override
                            public void onPossitiveCall() {
                                Log.e("Ee", "onPossitiveCall");
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isOnline", 0);
                                hashMap.put(ForegroundService2.F_STATUS_FIELD, 9);
                                setOnlineOffline(hashMap);
                                MainActivity.this.finish();
                                Intent intent = new Intent(MainActivity.this, Login2Activity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onNegativeCall() {

                            }
                        });
    }

    /******set user online status when tab on notifaction ***/
    public void setOnlineOfflineToOnline() {
        HashMap<String, Object> hashMap = new HashMap<>();
        /* set online status when app launch****/
        hashMap.put("isOnline", 1);
        setOnlineOffline(hashMap);
        try {
            FirebaseDatabase.getInstance()
                    .getReference().child(App_preference.getSharedprefInstance().getRegion()
                    + "/cmp" +
                    App_preference.getSharedprefInstance().getLoginRes().getCompId().toLowerCase() + "/users/usr"
                    + App_preference.getSharedprefInstance().getLoginRes().getUsrId()).updateChildren(hashMap)
                    .addOnSuccessListener(aVoid -> Log.d("ForegroundService2", "init firebase success realtime")).addOnFailureListener(e -> Log.d("ForegroundService2", "init firebase fail realtime"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateVersionForcFully() {
        Log.e("", "");
        float version;
        PackageInfo pInfo = null;
        try {
            pInfo = EotApp.getAppinstance().getPackageManager().getPackageInfo(EotApp.getAppinstance().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert pInfo != null;
        float app_version = Float.parseFloat(pInfo.versionName);
        //   app_version2 = app_version;
        if (App_preference.getSharedprefInstance().getLoginRes().getVersion() != null) {
            version = Float.parseFloat(App_preference.getSharedprefInstance().getLoginRes().getVersion());
        } else {
            version = app_version;
        }

        /*Download Update Language File ***/
        downloadJsonFile(app_version);

        if (version > app_version) {
            versionUpdateBtn.setVisibility(View.VISIBLE);
            Animation anim = new AlphaAnimation(0.5f, 1.0f);
            anim.setDuration(800); //You can manage the blinking time with this parameter
            anim.setStartOffset(50);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            versionUpdateBtn.startAnimation(anim);
        } else {
            versionUpdateBtn.setVisibility(View.GONE);
        }
    }

    private void upateForcefully() {
        /*changes accroding to jit sir***/
        AppUtility.alertDialog2(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.update_btn)
                        + "!",
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.update_app_message),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.update_btn)
                , LanguageController.getInstance().getMobileMsgByKey(AppConstant.cancel), new Callback_AlertDialog() {
                    @Override
                    public void onPossitiveCall() {
                        Intent intent = new Intent(MainActivity.this, Login2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }

                    @Override
                    public void onNegativeCall() {

                    }
                });
    }


    private void dialogForLocationDisclosure() {

        DialogPermission dialogPermission = new DialogPermission();
        dialogPermission.setOnActionButton(isAllowed -> {
            if (isAllowed) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            LOCATION_REQUEST);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_REQUEST);
                }
            }
        });
        dialogPermission.show(getSupportFragmentManager(), "permission");

    }

    private void userPerMissionForLocation() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(ForegroundService2.F_STATUS_FIELD, 5);
            setOnlineOffline(hashMap);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialogForLocationDisclosure();
            }
            HyperLog.i(TAG, "USER Disabled Location Permission");
        } else {
            onFWTrackingStart();
        }

    }

    private void onFWTrackingStart() {
        AppUtility.showAlertForIgnoreBatteryOptimize(this);
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(ForegroundService2.F_STATUS_FIELD, 4);
                setOnlineOffline(hashMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new GpsUtils(this).turnGPSOn(isGPSEnable -> {
            // turn on GPS
            isGPS = isGPSEnable;
            if (isGPSEnable) {
                callBackgroundServices();
            } else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(ForegroundService2.F_STATUS_FIELD, 4);
                setOnlineOffline(hashMap);
                HyperLog.i(TAG, "GPS OFf");
            }
        });
    }

    private void callBackgroundServices() {
        checkTimerExpire();
        if (!EotApp.getAppinstance().isMyServiceRunning(ForegroundService2.class)) {
            Intent startIntent = new Intent(this, ForegroundService2.class);
            startIntent.setAction(ForegroundService2.STARTFOREGROUND_ACTION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //this.startForegroundService(startIntent);
                startForegroundService(startIntent);
            } else {
                startService(new Intent(startIntent));
            }
            HyperLog.i(TAG, "Location service started");
        }
    }

    void checkTimerExpire() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isOnline", 1);

        long remainingTime = calculateRemaingTime(serverFormat.format(Calendar.getInstance().getTime()));

        if (remainingTime == 0) {
            hashMap.put(ForegroundService2.F_STATUS_FIELD, 7);
        } else {
            hashMap.put(ForegroundService2.F_STATUS_FIELD, 1);
        }
        setOnlineOffline(hashMap);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_activity_container);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat
                    .START);
        }
        else if(bottomSheetDialog!=null){
            bottomSheetDialog.dismiss();
            sync_iv.clearAnimation();
            sync_iv.setVisibility(View.GONE);
        }
        else if(fragment instanceof AddLeadFragment){
            // do something with f
            title_jobs.performClick();
        }
        else {
            show_logout_Dialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menuItem) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menuItem;

        getMenuInflater().inflate(R.menu.main, menu);
        if (auditNoti) {
            if (menu != null) {
                menu.setGroupVisible(R.id.job_group, false);
                menu.setGroupVisible(R.id.client_group, false);
                menu.setGroupVisible(R.id.quote_group, false);
                menu.setGroupVisible(R.id.audit_group, true);
                menu.setGroupVisible(R.id.single_chat_group, false);
                menu.setGroupVisible(R.id.expense_group, false);
                menu.setGroupVisible(R.id.report_group, false);
                isCalendarSelected = false;
                img_month_arrow.setVisibility(View.GONE);
                img_sorting.setVisibility(View.GONE);
            }
        } else if (isCalendarSelected) {
            menu.setGroupVisible(R.id.job_group, false);
            menu.setGroupVisible(R.id.client_group, false);
            menu.setGroupVisible(R.id.quote_group, false);
            menu.setGroupVisible(R.id.audit_group, false);
            menu.setGroupVisible(R.id.single_chat_group, false);
            menu.setGroupVisible(R.id.expense_group, false);
            menu.setGroupVisible(R.id.report_group, false);
            isCalendarSelected = true;
            img_month_arrow.setVisibility(View.VISIBLE);
            img_sorting.setVisibility(View.VISIBLE);
        } else if (menu != null) {
            menu.setGroupVisible(R.id.job_group, false);
            menu.setGroupVisible(R.id.client_group, false);
            menu.setGroupVisible(R.id.quote_group, false);
            menu.setGroupVisible(R.id.audit_group, false);
            menu.setGroupVisible(R.id.single_chat_group, false);
            menu.setGroupVisible(R.id.expense_group, false);
            menu.setGroupVisible(R.id.report_group, false);
            isCalendarSelected = false;
            img_month_arrow.setVisibility(View.GONE);
            img_sorting.setVisibility(View.GONE);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Action on menu item of particular drawer menu
        if (id == R.id.job_filter) {
            Intent filterresult = new Intent(MainActivity.this, JobFilter_Activity.class);
            startActivityForResult(filterresult, FILTERJOBLIST);
            return true;
        } else if (item.getItemId() == R.id.quotes_filter) {
            Intent filterresult = new Intent(MainActivity.this, QuotesFilter_Activity.class);
            startActivityForResult(filterresult, FILTERQUOTESLIST);
            return true;
        } else if (id == R.id.quotes_filter_search) {
            /* Quotes search bar enable/disable****/
            quotesList.setSearchVisibility(!isQuotesSearchSeleted);
            isQuotesSearchSeleted = !isQuotesSearchSeleted;
        } else if (id == R.id.job_filter_search) {
            //search bar enable/disable
            joblistfragment.setSearchVisibility(isJobSearchSelected);
            isJobSearchSelected = !isJobSearchSelected;
        } else if (id == R.id.audit_filter_search) {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            if (fragmentList != null && fragmentList.size() > 0) {
                Fragment fragment = fragmentList.get(0);
                if (fragment instanceof FragmentAuditList) {
                    isAuditSearch = !isAuditSearch;
                    ((FragmentAuditList) fragment).setSearchVisibility(isAuditSearch);
                }
            }
        } else if (id == R.id.single_chat_search) {
            /* One to One chat search bar enable/disable****/
            chatmemberListFragment.setSearchVisibility(!isSingleChatSearch);
            isSingleChatSearch = !isSingleChatSearch;
        } else if (id == R.id.expense_search) {
            /* Expense search bar enable/disable****/
            expenseListFragment.setSearchVisibility(!isexpenseSearch);
            isexpenseSearch = !isexpenseSearch;
        } else if (id == R.id.img_month_arrow) {
            /* Appoinment calendar week and month view****/
            if (appointmentListFragment != null) {
                appointmentListFragment.changeCollpase();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void show_logout_Dialog() {
        AppUtility.alertDialog(this, "", LanguageController.getInstance().getMobileMsgByKey(AppConstant.user_logout_msg),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.yes), LanguageController.getInstance().getMobileMsgByKey(AppConstant.no), () -> {
                    mainActivity_pi.onLogoutServicecall();
                    return null;
                });
    }

    private void updateFragment(CharSequence title, Fragment joblistfragment) {
        if (LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_appointments).contentEquals(title))
            setTitle("");
        else {
            if (tv_custom_title != null) tv_custom_title.setText("");
            setTitle(title);
        }
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_container, joblistfragment).commit();
        } catch (IllegalStateException ille) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_container, joblistfragment).commitAllowingStateLoss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case SETTING_CODE:
                    if (data.hasExtra("isChange")) {
                        if (data.hasExtra("isChange")) {
                            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(refresh);
                            finish();
                        }
                    } else if (data.hasExtra("isShowSite")) {
                        if (joblistfragment != null)
                            joblistfragment.showSiteName(App_preference.getSharedprefInstance().getSiteNameShowInSetting());
                        if (fragmentAuditList != null)
                            fragmentAuditList.showSiteName(App_preference.getSharedprefInstance().getSiteNameShowInSetting());
                    }
                    break;
                case ADDJOB:
                    if (joblistfragment != null) {
                        joblistfragment.refreshJobList();
                    }
                    break;
                case BAR_CODE_REQUEST:
                    if (data.hasExtra("AUDITDATA")) {
                        String barcode = data.getStringExtra("code");
                        List<AuditList_Res> equipmentList = new ArrayList<>();
                        try {
                            String str = data.getStringExtra("AUDITDATA");
                            Type listType = new TypeToken<List<AuditList_Res>>() {
                            }.getType();
                            equipmentList = new Gson().fromJson(str, listType);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        /*
                         * @Use of commitAllowingStateLoss here
                         *  The fragment will be on its’ place if the user returns back after scan code
                         * Its’ state will be preserved in the Bundle and the Fragment will be recreated even if the process is killed.
                         * */
                        hidesearchMenu(true);
                        img_month_arrow.setVisibility(View.GONE);
                        img_sorting.setVisibility(View.GONE);
                        tv_custom_title.setText("");
                        isCalendarSelected = false;
                        fab.hide();
                        List<AuditList_Res> dbAudits = new ArrayList<>();
                        if (equipmentList != null)
                            for (AuditList_Res j : equipmentList) {
                                AuditList_Res audit = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().getAuditById(j.getAudId());
                                if (audit != null)
                                    dbAudits.add(audit);
                            }

                        setTitle(LanguageController.getInstance().getMobileMsgByKey(AppConstant.audit_nav));
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_activity_container, FragmentAuditList.newInstance(dbAudits, true, barcode))
                                .commitAllowingStateLoss();
                    } else if (data.hasExtra("JOBDATA")) {
                        List<String> jobListfilter = new ArrayList<>();
                        try {
                            String str = data.getStringExtra("JOBDATA");
                            Type listType = new TypeToken<List<String>>() {
                            }.getType();
                            jobListfilter = new Gson().fromJson(str, listType);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        if (fab.getVisibility() == View.GONE) {
                            fab.hide();
                        }
                        /* Hide floationg action button Hide when Admin permission not Authorized*/
                        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsJobAddOrNot() != 0) {
                            //0
                            fab.hide();
                        }
                        if (menu != null) {
                            menu.setGroupVisible(R.id.job_group, false);
                            menu.setGroupVisible(R.id.client_group, false);
                            menu.setGroupVisible(R.id.quote_group, false);
                            menu.setGroupVisible(R.id.audit_group, false);
                            menu.setGroupVisible(R.id.single_chat_group, false);
                            menu.setGroupVisible(R.id.expense_group, false);
                            menu.setGroupVisible(R.id.report_group, false);
                            img_month_arrow.setVisibility(View.GONE);
                            img_sorting.setVisibility(View.GONE);
                            tv_custom_title.setText("");
                            isCalendarSelected = false;
                            fab.hide();
                        }
                        String barcode = data.getStringExtra("code");

                        setTitle(LanguageController.getInstance().getMobileMsgByKey(AppConstant.jobs));
                        List<Job> dbJobs = new ArrayList<>();
                        if (jobListfilter != null) {
                            /*for (Job j : jobListfilter) {
                                Job jobsById = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(j.getJobId());
                                if (jobsById != null)
                                    dbJobs.add(jobsById);
                            }*/
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_activity_container, JobList.newInstance(true, jobListfilter, barcode))
                                    .commitAllowingStateLoss();
                        }
                    }
                    break;
                case FILTERQUOTESLIST:
                    try {
                        String str = data.getStringExtra("filterlist");
                        QuotesFilter quotesFilter = new Gson().fromJson(str, QuotesFilter.class);
                        quotesList.chipAdd(quotesFilter);//add dynamically chips
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ClientADD:
                    if (data.hasExtra("cltId"))//update new client in client list
                        clientList2Fragment.refreshList((data.getStringExtra("cltId")));
                    break;
                case AuditADD:
                    if (fragmentAuditList != null) {
                        fragmentAuditList.refreshList();
                    }
                    break;
                case FILTERJOBLIST:
                    ArrayList<DropdownListBean> listBeans;
                    listBeans = (ArrayList<DropdownListBean>) data.getSerializableExtra("filterlist");
                    joblistfragment.filterListByChip(listBeans, 3);//job list filter by Advance filter
                    assert listBeans != null;
                    joblistfragment.chipAdd(listBeans);//Add dynamically chips on job list for selected filter

                    //used for - search button take extra click for visible search box when filter comes from advanced filter
                    if (!isJobSearchSelected) {
                        isJobSearchSelected = true;
                    }
                    break;
                case GpsUtils.REQUEST_CHECK_SETTINGS_LOCATION:
                    if (resultCode == RESULT_OK) {
                        callBackgroundServices();
                    }
                    break;
                case JobList.UPDATE:
                    if (resultCode == RESULT_OK) {
                        if (joblistfragment != null) {
                            joblistfragment.refreshchangesFromLocalDB();//update job list
                     //       appointmentListViewModel.refreshList();
                        }
                    }
                    break;
                case JobList.UPDATEAPPOINTMENT:
                        if (resultCode == RESULT_OK) {
                            if (joblistfragment != null) {
                                joblistfragment.refreshchangesFromLocalDB();//update job list
                                      appointmentListViewModel.refreshList();
                            }
                        }
                        break;
                case ADDEXPENSES:
                    if (expenseListFragment != null) {
                        expenseListFragment.refereshExpenseList();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected && !isConnetionFIrstTime) {
            isConnetionFIrstTime = true;
            OfflineDataController.getInstance().fromOutSideCall();
        } else {
            isConnetionFIrstTime = isConnected;
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("Ee", "MainActivity");
        /*for chat***/
        registerDeregisterReceiver(false);
        EotApp.getAppinstance().setApiObserver(null);
        ChatController.getInstance().setAppUserOnline(0);
        /*remove 1-1 chat user listner***/
        UserToUserChatController.getInstance().removeChatListner();
        UserToUserChatController.getInstance().removeAllChatUserListner();

        ChatController.getInstance().removeAllListner();

        /*set char prefernce & it's never clear on logout's**/
        if (!ChatApp_Preference.getSharedprefInstance().getChatData()) {
            ChatApp_Preference.getSharedprefInstance().setChatData(true);
        }

        try {
            if (networkSwitchStateReceiver != null) {
                unregisterReceiver(networkSwitchStateReceiver);
                isRegistered = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (batteryStatusReceiver != null)
                unregisterReceiver(batteryStatusReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MyDigitalClock.getInstance().setMyTimerTime();
        if (updateUIReceiver != null) {
            unregisterReceiver(updateUIReceiver);
        }
        unregisterReceiver(showAlertReceiver);

        super.onDestroy();
    }

    @Override
    public void onLogout(String msg) {
//        stop service on logout.
        Log.e("Ee", "onLogout");
        USERLOGOUT = true;

        if (EotApp.getAppinstance().isMyServiceRunning(ForegroundService2.class)) {
            Intent stopIntent = new Intent(this, ForegroundService2.class);
            stopIntent.setAction(ForegroundService2.STOPFOREGROUND_ACTION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(stopIntent);
            } else {
                startService(new Intent(stopIntent));
            }
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isOnline", 0);
        hashMap.put(ForegroundService2.F_STATUS_FIELD, 9);
        setOnlineOffline(hashMap);

        //cancel onDisconnect operations
        /*
         * this line of cancel operation is must here otherwise after socket timeout user
         * automatically set to app killed status
         * and we have to set user as logout status because user has logout out from app manually
         * */
        cancelOnDisconnectOperations();


//        stop service on logout.
        Intent intent = new Intent(this, Login2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

//        sign out from firebase
        ChatController.getInstance().signOutUserFromFirebase();

        MyDigitalClock.getInstance().setMyTimerTime();
//        if (title_check_in_out.getText().toString().equals(LanguageController.getInstance().getMobileMsgByKey(AppConstant.check_out)))
//            App_preference.getSharedprefInstance().setLastAppKillTime(AppUtility.getCurrentDateByFormat("hh:mm a"));


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Log.e("", "");
        super.onStop();
    }

    @Override
    public void onLogoutErrorOccur(String msg) {
        AppUtility.alertDialog(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_error_title), msg, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> {
            EotApp.getAppinstance().sessionExpired();
            return null;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST: {
                //location for FW
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    HyperLog.i(TAG, "Location Permission granted");
////                    onFWTrackingStart();
                    callBackgroundServices();
                } else {
                    HyperLog.i(TAG, "Location Permission denied from user");
                }
                break;
            }
            case REPORT_TIMESHEET: {
                reportFragment.setPERMMISSIONALLOW(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
            }
            case TIMESHEET: {
                timeSheetFragment.setSHEETPERMMISSIONALLOW(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
            }
        }
    }

    @Override
    public void onObserveCallBack(String api_name) {
        switch (api_name) {
            case Service_apis.changeJobStatus:
                if (joblistfragment != null) {
                    joblistfragment.getUpdatedJob();
                }
                break;
            case Service_apis.addJob:
                if (joblistfragment != null) {
                    joblistfragment.updateFromApiObserver();
                }
                if (isCalendarSelected) {
                    if (appointmentListFragment != null) {
                        appointmentListFragment.refreshAppointmentList();
                        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
                    }
                }
                break;
            case Service_apis.addClient:
                if (clientList2Fragment != null) {
                    clientList2Fragment.updateFromApiObserver();
                }
                break;
            case Service_apis.addAudit:
                if (isCalendarSelected) {
                    if (appointmentListFragment != null)
                        appointmentListFragment.refreshAppointmentList();
                } else if (fragmentAuditList != null) {
                    fragmentAuditList.updateFromApiObserver();
                }
                break;
            case Service_apis.updateAppointment:
            case Service_apis.addAppointment:
                if (appointmentListFragment != null)
                    appointmentListFragment.refreshAppointmentList();
                break;
            case Service_apis.addLeave:
                if (userLeaveListFragment != null)
                    userLeaveListFragment.refreshAppointmentList();
                break;
        }
    }

    @Override
    protected void onPause() {
        Log.e("", "");
        super.onPause();
    }

    @Override
    public void onClearCache() {
        deleteCache();
    }

    @Override
    public void seletedLanguage(final Language_Model language_model) {

        AppUtility.alertDialog2(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.confirmation), LanguageController.getInstance().getMobileMsgByKey(AppConstant.changeLanguage)
                , LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), LanguageController.getInstance().getMobileMsgByKey(AppConstant.cancel), new Callback_AlertDialog() {
                    @Override
                    public void onPossitiveCall() {
                        if (AppUtility.isInternetConnected()) {
                            AppUtility.progressBarShow(MainActivity.this);
                            setLanguage(language_model);
                        } else {
                            AppUtility.alertDialog(MainActivity.this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
                        }
                    }

                    @Override
                    public void onNegativeCall() {

                    }
                });
    }

    @Override
    public void checkIdUpdateUI(String checkId, String msg) {

        HyperLog.e("TAG","Check in checkout checkIdUpdateUI "+checkId);
        title_check_in_out.setEnabled(true);
        if (checkId.isEmpty()) {
            title_check_in_out.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.check_in));
            MyDigitalClock.getInstance().stopTimerCounting();
            text_clock.setVisibility(View.GONE);
            check_in_time.setVisibility(View.GONE);
            App_preference.getSharedprefInstance().setCheckInTime("");
        } else {
            title_check_in_out.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.check_out));
            text_clock.setVisibility(View.VISIBLE);
            // changed by shivani 21 April 2022
//            App_preference.getSharedprefInstance().setCheckInTime(AppUtility.getCurrentDateByFormat("hh:mm a"));
            App_preference.getSharedprefInstance().setCheckInTime(AppUtility.getCurrentDateByFormat("dd/MM/yyyy-hh:mm a"));
            if (!App_preference.getSharedprefInstance().getCheckInTime().equals("")) {

                HyperLog.e("TAG","Check in checkout checkIdUpdateUI CheckInTimeNotEmpty");
                check_in_time.setVisibility(View.VISIBLE);
                // changed by shivani 21 April 2022
//                check_in_time.setText(App_preference.getSharedprefInstance().getCheckInTime());
                check_in_time.setText(App_preference.getSharedprefInstance().getCheckInTime().split("-")[1]);
            } else {
                HyperLog.e("TAG","Check in checkout checkIdUpdateUI CheckInTimeEmpty");
                check_in_time.setVisibility(View.GONE);
                App_preference.getSharedprefInstance().setCheckInTime("");
            }
            MyDigitalClock.getInstance().startTimerCounting();
        }
        loader.clearAnimation();
        loader.setVisibility(View.GONE);
    }




    private void setLanguage(final Language_Model language_model) {
        String lan_url = language_model.getFilePath() + language_model.getFileName() + ".json";
        LanguageController.getInstance().downloadFile(lan_url, () -> {
            App_preference.getSharedprefInstance().setLanFileVer(Float.parseFloat(BuildConfig.VERSION_NAME));
            AppUtility.progressBarDissMiss();
            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            startActivity(refresh);
            finish();
            return null;
        });
    }

    private void deleteCache() {
        try {
            File dir = this.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.e("TAG: ", "Success");
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  GPS turned off, Show the user a dialog
                if (AppConstant.location_checker_enable) {
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(this, GpsUtils.REQUEST_CHECK_SETTINGS_LOCATION);

                    } catch (IntentSender.SendIntentException e) {

                        //failed to show dialog
                    } finally {
                        AppConstant.location_checker_enable = false;
                    }
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    public void setChatbatchCount() {
        int totalCount = UserToUserChatController.getInstance().getTotalCount();
        if (totalCount > 0) {
            badge_count.setVisibility(View.VISIBLE);
            badge_count.setText(totalCount + "");
        } else {
            badge_count.setVisibility(View.GONE);
        }
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        switch (v.getId()) {
            case R.id.title_jobs:
                if (fab.getVisibility() == View.GONE) {
                    fab.show();
                }
                /* Hide floationg action button Hide when Admin permission not Authorized*/
                if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsJobAddOrNot() != 0) {//0
                    fab.hide();
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.jobs), joblistfragment);
                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, true);
                    menu.setGroupVisible(R.id.client_group, false);
                    menu.setGroupVisible(R.id.quote_group, false);
                    menu.setGroupVisible(R.id.audit_group, false);
                    menu.setGroupVisible(R.id.single_chat_group, false);
                    menu.setGroupVisible(R.id.expense_group, false);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.addleave_group, false);


                    img_month_arrow.setVisibility(View.GONE);
                    img_sorting.setVisibility(View.GONE);
                    isCalendarSelected = false;
                    //  menu.setGroupVisible(R.id.appoinment_group, false);
                }
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.title_addLead:
                fab.hide();
                updateFragment(AppConstant.lead, addLeadFragment);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.parent_client:
                if (fab.getVisibility() == View.GONE) {
                    fab.show();
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.clients), clientList2Fragment);
                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, false);
                    menu.setGroupVisible(R.id.client_group, true);
                    menu.setGroupVisible(R.id.quote_group, false);
                    menu.setGroupVisible(R.id.audit_group, false);
                    menu.setGroupVisible(R.id.single_chat_group, false);
                    menu.setGroupVisible(R.id.expense_group, false);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.addleave_group, false);
                    //  menu.setGroupVisible(R.id.appoinment_group, false);
                    isCalendarSelected = false;
                    img_month_arrow.setVisibility(View.GONE);
                    img_sorting.setVisibility(View.GONE);
                }
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.logout_layout:
                show_logout_Dialog();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.setting:
                Intent inten = new Intent(MainActivity.this, SettingActivity.class);
                drawer.closeDrawer(GravityCompat.START);
                startActivityForResult(inten, SETTING_CODE);
                break;
            case R.id.title_check_in_out:
                if (AppUtility.isInternetConnected()){
                    title_check_in_out.setEnabled(false);
                    try {
                        Right rights = App_preference.getSharedprefInstance().getLoginRes().getRights().get(0);
                        boolean isDesPermission = false;
                        boolean isAttachmentPermission = false;
                        if (rights != null &&rights.getIsCheckInOutDescAdd()==0){
                            isDesPermission= true;
                        }
                        if (rights != null &&rights.getIsCheckInOutAttAdd()==0){
                            isAttachmentPermission= true;
                        }
                        String shiftDifference;
                        boolean isShowDate=false;
                        if(App_preference.getSharedprefInstance().getLoginRes().getShiftStartTime() != null&&
                                !App_preference.getSharedprefInstance().getLoginRes().getShiftStartTime().equals("")){

                            // check if the shift is of single day or multiple days

                            String time1 = App_preference.getSharedprefInstance().getLoginRes().getShiftEndTime()+":00";
                            String time2 = App_preference.getSharedprefInstance().getLoginRes().getShiftStartTime()+":00";
                            if(AppUtility.conditionCheck(time2,time1,"HH:mm:ss")){
                                // if the shift is of multiple days
                                time2 = AppUtility.getCurrentDateByFormat("dd-MM-yyyy")+" "+time2;
                                time1 = AppUtility.getTomorrowDateByFormat("dd-MM-yyyy")+" "+time1;
                                shiftDifference = AppUtility.calculateTimeDiff(time2,time1,"dd-MM-yyyy HH:mm:ss");
                            }
                            else {
                                // if the shift is of single day
                                shiftDifference = AppUtility.calculateTimeDiff(time1,time2,"HH:mm:ss");
                            }
                            Log.e("CheckIn Checkout",shiftDifference);
                            isShowDate = AppUtility.conditionCheck(text_clock.getText().toString(),shiftDifference,"HH:mm:ss");
                        }

                        if (App_preference.getSharedprefInstance().getLoginRes().getCompPermission().get(0).getIsAutocheckOutEnabled()!=null&&
                                App_preference.getSharedprefInstance().getLoginRes().getCompPermission().get(0).getIsAutocheckOutEnabled().equals("1")
                                &&isShowDate
                                && title_check_in_out.getText().toString().equalsIgnoreCase(LanguageController.getInstance().getMobileMsgByKey(AppConstant.check_out))) {

                            title_check_in_out.setEnabled(true);
                            drawer.closeDrawer(GravityCompat.START);
                            dialogUpdateDocuments = new DialogCheckInUploadDocuments();
                            dialogUpdateDocuments.setContext(this);
                            dialogUpdateDocuments.setPermissions(isDesPermission,isAttachmentPermission,true);
                            dialogUpdateDocuments.setButtonText(title_check_in_out.getText().toString());
                            dialogUpdateDocuments.show(getSupportFragmentManager(), "dialog");
                        }
                        else if (isDesPermission||isAttachmentPermission) {
                            title_check_in_out.setEnabled(true);
                            drawer.closeDrawer(GravityCompat.START);
                            dialogUpdateDocuments = new DialogCheckInUploadDocuments();
                            dialogUpdateDocuments.setContext(this);
                            dialogUpdateDocuments.setPermissions(isDesPermission,isAttachmentPermission,false);
                            dialogUpdateDocuments.setButtonText(title_check_in_out.getText().toString());
                            dialogUpdateDocuments.show(getSupportFragmentManager(), "dialog");
                        } else {
                            mainActivity_pi.addCheckInOutIime(null,null,null,null);
//                        for loader animation
                            loader.setVisibility(View.VISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(this, R.anim.progress_anim);
                            loader.startAnimation(animation);
                        }
                    } catch (Exception e) {
                        title_check_in_out.setEnabled(true);
                        e.printStackTrace();
                    }
                }
                else {
                    title_check_in_out.setEnabled(true);

                    EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network));
                }

                break;
            case R.id.title_qoutes:
                if (fab.getVisibility() == View.GONE) {
                    fab.show();
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.quotes), quotesList);
                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, false);
                    menu.setGroupVisible(R.id.client_group, false);
                    menu.setGroupVisible(R.id.quote_group, true);
                    menu.setGroupVisible(R.id.audit_group, false);
                    menu.setGroupVisible(R.id.single_chat_group, false);
                    menu.setGroupVisible(R.id.expense_group, false);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.addleave_group, false);

                    // menu.setGroupVisible(R.id.appoinment_group, false);
                    isCalendarSelected = false;
                    img_month_arrow.setVisibility(View.GONE);
                    img_sorting.setVisibility(View.GONE);
                }
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.title_audit:
                if (fab.getVisibility() == View.VISIBLE) {
                    fab.show();
                } else if ((fab.getVisibility() != View.VISIBLE))
                    fab.show();

                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, false);
                    menu.setGroupVisible(R.id.client_group, false);
                    menu.setGroupVisible(R.id.quote_group, false);
                    menu.setGroupVisible(R.id.audit_group, true);
                    menu.setGroupVisible(R.id.single_chat_group, false);
                    menu.setGroupVisible(R.id.expense_group, false);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.addleave_group, false);

                    // menu.setGroupVisible(R.id.appoinment_group, false);
                    isCalendarSelected = false;
                    img_month_arrow.setVisibility(View.GONE);
                    img_sorting.setVisibility(View.GONE);
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.audit_nav), fragmentAuditList);

                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.title_scan:
                Intent intent = new Intent(MainActivity.this, BarcodeScanActivity.class);
                startActivityForResult(intent, BAR_CODE_REQUEST);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.versionUpdateBtn:
                upateForcefully();
                break;
            case R.id.chat_textview:
                if (fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                }
                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, false);
                    menu.setGroupVisible(R.id.client_group, false);
                    menu.setGroupVisible(R.id.quote_group, false);
                    menu.setGroupVisible(R.id.audit_group, false);
                    menu.setGroupVisible(R.id.single_chat_group, true);
                    menu.setGroupVisible(R.id.expense_group, false);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.addleave_group, false);

                    // menu.setGroupVisible(R.id.appoinment_group, false);
                    isCalendarSelected = false;
                    img_month_arrow.setVisibility(View.GONE);
                    img_sorting.setVisibility(View.GONE);
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.side_menu_title_chats),
                        chatmemberListFragment);

                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.title_expence:
                if (fab.getVisibility() == View.GONE) {
                    fab.show();
                }
                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, false);
                    menu.setGroupVisible(R.id.client_group, false);
                    menu.setGroupVisible(R.id.quote_group, false);
                    menu.setGroupVisible(R.id.audit_group, false);
                    menu.setGroupVisible(R.id.single_chat_group, false);
                    menu.setGroupVisible(R.id.expense_group, true);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.addleave_group, false);
                    // menu.setGroupVisible(R.id.appoinment_group, false);
                    isCalendarSelected = false;
                    img_month_arrow.setVisibility(View.GONE);
                    img_sorting.setVisibility(View.GONE);
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_expence), expenseListFragment);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.title_timeSheet:
                if (fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                }
                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, false);
                    menu.setGroupVisible(R.id.client_group, false);
                    menu.setGroupVisible(R.id.quote_group, false);
                    menu.setGroupVisible(R.id.audit_group, false);
                    menu.setGroupVisible(R.id.single_chat_group, false);
                    menu.setGroupVisible(R.id.expense_group, false);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.title_timeSheet, false);
                    menu.setGroupVisible(R.id.addleave_group, false);
                    isCalendarSelected = false;
                    img_month_arrow.setVisibility(View.GONE);
                    img_sorting.setVisibility(View.GONE);
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_timeSheet), timeSheetFragment);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.title_report:
                if (fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                }
                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, false);
                    menu.setGroupVisible(R.id.client_group, false);
                    menu.setGroupVisible(R.id.quote_group, false);
                    menu.setGroupVisible(R.id.audit_group, false);
                    menu.setGroupVisible(R.id.single_chat_group, false);
                    menu.setGroupVisible(R.id.expense_group, false);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.title_timeSheet, false);
                    menu.setGroupVisible(R.id.addleave_group, false);
                    isCalendarSelected = false;
                    img_month_arrow.setVisibility(View.GONE);
                    img_sorting.setVisibility(View.GONE);
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_report), reportFragment);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.title_addLeave:
                if (fab.getVisibility() == View.GONE) {
                    fab.show();
                }
                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, false);
                    menu.setGroupVisible(R.id.client_group, false);
                    menu.setGroupVisible(R.id.quote_group, false);
                    menu.setGroupVisible(R.id.audit_group, false);
                    menu.setGroupVisible(R.id.single_chat_group, false);
                    menu.setGroupVisible(R.id.expense_group, false);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.title_timeSheet, false);
                    menu.setGroupVisible(R.id.addleave_group, false);
                    isCalendarSelected = false;
                    img_month_arrow.setVisibility(View.GONE);
                    img_sorting.setVisibility(View.GONE);
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.user_leave), userLeaveListFragment);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.title_calender:

                if(App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAuditVisible()!=0
                        &&App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsJobAddOrNot()!= 0
                        &&App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAppointmentVisible()!= 0)
                {
                    fab.hide();
                }
                else {
                    if (fab.getVisibility() == View.GONE) {
                        fab.show();
                    }
                }

                if (menu != null) {
                    menu.setGroupVisible(R.id.job_group, false);
                    menu.setGroupVisible(R.id.client_group, false);
                    menu.setGroupVisible(R.id.quote_group, false);
                    menu.setGroupVisible(R.id.audit_group, false);
                    menu.setGroupVisible(R.id.single_chat_group, false);
                    menu.setGroupVisible(R.id.expense_group, false);
                    menu.setGroupVisible(R.id.report_group, false);
                    menu.setGroupVisible(R.id.addleave_group, false);
                    // menu.setGroupVisible(R.id.appoinment_group, true);
                    isCalendarSelected = true;
                    img_month_arrow.setVisibility(View.VISIBLE);
                    img_sorting.setVisibility(View.VISIBLE);
                }
                updateFragment(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_appointments),
                        appointmentListFragment);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.tv_custom_title:
            case R.id.img_month_arrow:
                if (appointmentListFragment != null) {
                    int imgRotationAngle = appointmentListFragment.getImgRotationAngle();
                    img_month_arrow.setRotation(imgRotationAngle);
                    appointmentListFragment.changeCollpase();
                }
                break;
            case R.id.img_sorting:
                if (appointmentListFragment != null) {
                    appointmentListFragment.reverseList();
                }
                break;
        }
    }

    @Override
    public void check_In_Out_Fail() {
        title_check_in_out.setEnabled(true);
        AppUtility.alertDialog(this, "",
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.check_in_out_fail),
                AppConstant.ok,
                ""
                , () -> null);
    }

    @Override
    public void setEnableButton() {
        title_check_in_out.setEnabled(true);
    }

    public void hidesearchMenu(boolean b) {
        if (menu != null) {
            menu.setGroupVisible(R.id.job_group, false);
            menu.setGroupVisible(R.id.client_group, false);
            menu.setGroupVisible(R.id.quote_group, false);
            menu.setGroupVisible(R.id.audit_group, b);
        }
    }

    /**
     * set User online/offline
     ******/
    private void initFirebase() {
        try {
            ForegroundService2.isAppInBackground = false;
            HashMap<String, Object> hashMap = new HashMap<>();
            /* set online status when app launch ****/
            hashMap.put("isOnline", 1);
            setOnlineOffline(hashMap);

            /*set offline status when use disconnect from internet ****/
            hashMap.clear();
            hashMap.put("isOnline", 0);
            hashMap.put(ForegroundService2.F_ISBACKGROUND_FIELD, 0);
            hashMap.put(ForegroundService2.F_STATUS_FIELD, 8);
            // Adding on disconnect hook

            FirebaseDatabase.getInstance().getReference(path)
                    .onDisconnect()// Set up the disconnect hook
                    .updateChildren(hashMap).addOnSuccessListener(aVoid -> Log.d("ForegroundService", "Disconnect hook added"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cancelOnDisconnectOperations() {
        try {
            FirebaseDatabase.getInstance().getReference(path).onDisconnect().cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOnlineOffline(HashMap<String, Object> hashMap) {
        try {
            ForegroundService2.isAppInBackground = false;
            if (!hashMap.containsKey(ForegroundService2.F_ISBACKGROUND_FIELD))
                hashMap.put(ForegroundService2.F_ISBACKGROUND_FIELD, 0);
            FirebaseDatabase.getInstance()
                    .getReference().child(path).updateChildren(hashMap)
                    .addOnSuccessListener(aVoid -> Log.d("ForegroundService2", "init firebase success realtime")).addOnFailureListener(e -> Log.d("ForegroundService2", "init firebase fail realtime"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculating the tracking duration of the FW
     * from given START and END time parameter from admin
     */
    public long calculateRemaingTime(String currenttime) {
        long expirationDuration = 0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat serverFormat = new SimpleDateFormat("hh:mm:ss a");

        try {

            String startTime = App_preference.getSharedprefInstance().getLoginRes().getTrkStartingHour();
            String endTime = App_preference.getSharedprefInstance().getLoginRes().getTrkEndingHour();

            Date startDate = serverFormat.parse(startTime);

            assert startDate != null;
            startDate.setTime(startDate.getTime() - 1000);

            Date endDate = serverFormat.parse(endTime);
            Date currentDate = serverFormat.parse(currenttime);

            if (startDate.before(currentDate)) {
                assert endDate != null;
                if (endDate.after(currentDate)) {
                    long secondEnd = endDate.getTime();
                    assert currentDate != null;
                    long secondCurrent = currentDate.getTime();

                    long l = (secondEnd - secondCurrent);
                    if (l < 0)
                        l = (secondCurrent - secondEnd);
                    expirationDuration = l;
                    // Log.d(LOG_TAG, "Remaining Time: " + l);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return expirationDuration;
    }

    private void registerNetworkReceiver() {
        if (!isRegistered) {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
            registerReceiver(networkSwitchStateReceiver, filter);
            isRegistered = true;
        }
    }

    private int checkUserMapStatus() {
        int status = 1;
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!App_preference.getSharedprefInstance().getLoginRes().getIsFWgpsEnable().equals("1")) {
                status = 6;
                return status;
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                status = 5;
                return status;
            } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                status = 4;
                return status;
            } else if (calculateRemaingTime(serverFormat.format(Calendar.getInstance().getTime())) == 0) {
                status = 7;
                return status;
            } else return status;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /*****Replacement of AsyncTask******/
    private void craeteUserChatListner() {
        try {
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> {
                /* craete listner for User notification's *****/
                UserToUserChatController.getInstance().addUserStatusListner(App_preference.getSharedprefInstance().getLoginRes().getUsrId());
                List<UserChatModel> mo = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).userChatModel().getChatUserlist();
                for (UserChatModel model : mo) {
                    if (model.getIsTeam() != null && !model.getIsTeam().equals("1"))
                        UserToUserChatController.getInstance().addUserStatusListner(model.getUsrId());
                }
                UserToUserChatController.getInstance().registerChatUserListner();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showRetryDialog() {
        try {
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(R.layout.bottom_alert_dialog);
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            TextView txt_lable1 = bottomSheetDialog.findViewById(R.id.lable1);
            TextView txt_lable2 = bottomSheetDialog.findViewById(R.id.lable2);
            TextView alert_msg = bottomSheetDialog.findViewById(R.id.alert_msg);
            assert txt_lable1 != null;
            txt_lable1.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.back_to_login));
            assert txt_lable2 != null;
            txt_lable2.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.continue_));
            assert alert_msg != null;
            alert_msg.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.error_retry));

            txt_lable1.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                sync_iv.clearAnimation();
                sync_iv.setVisibility(View.GONE);
                startActivity(new Intent(MainActivity.this, Login2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            });
            txt_lable2.setOnClickListener(view -> {
                retryCall();
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void retryCall() {
        if (AppUtility.isInternetConnected()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.progress_anim);
            sync_iv.startAnimation(animation);
            sync_iv.setVisibility(View.VISIBLE);
            stopService(new Intent(MainActivity.this, SyncDataJobService.class));
            SyncDataJobS.scheduleJob(this);

            IntentFilter filter = new IntentFilter();
            filter.addAction("updateUi");
            // update ui sync data
            updateUIReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //UI update here
                    Log.v("Main Activity", "Sync completed");
                    isSyncDone = true;
                    sync_iv.clearAnimation();
                    sync_iv.setVisibility(View.GONE);
                    stopService(new Intent(MainActivity.this, SyncDataJobService.class));
                }
            };
            registerReceiver(updateUIReceiver, filter);

        } else {
            sync_iv.clearAnimation();
            sync_iv.setVisibility(View.GONE);
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network));
        }
    }

    @Override
    public void onDocumentSelected(String path, String name, boolean isImage) {
        super.onDocumentSelected(path, name, isImage);
        Log.v("Image File Name", ":::::" + name);
        dialogUpdateDocuments.setImgPath(path, name);
        dialogUpdateDocuments.setIsFileImage(isImage);
        dialogUpdateDocuments.setData();
    }

}
