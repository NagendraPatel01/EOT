package com.eot_app.nav_menu.jobs.job_detail.job_equipment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.eot_app.R;
import com.eot_app.nav_menu.audit.audit_list.equipment.model.EquipmentStatus;
import com.eot_app.nav_menu.audit.nav_scan.EquipmentDetailsActivity;
import com.eot_app.nav_menu.equipment.linkequip.ActivityLinkEquipment;
import com.eot_app.nav_menu.equipment.popupSaveClient.AlertDialogClass;
import com.eot_app.nav_menu.equipment.popupSaveClient.equipmentClinetsave.Equipment_Client_view;
import com.eot_app.nav_menu.jobs.job_db.EquArrayModel;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_detail.detail.NotifyForEquipmentCountList;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.AddJobEquipMentActivity;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.job_equ_mvp.Job_equim_PC;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.job_equ_mvp.Job_equim_PI;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.job_equ_mvp.Job_equim_View;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.job_equ_remrk.JobEquPartRemarkRemarkActivity;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.job_equ_remrk.JobEquRemarkRemarkActivity;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.model.EquipmentSaveClientRes;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.CustomFilterButton;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class JobEquipmentActivity extends AppCompatActivity
        implements Job_equim_View, NotifyForEquipmentCountList,
        JobEquipmentAdapter.OnEquipmentSelection, EotApp.NotifyForEquipmentStatusList,
        View.OnClickListener, Equipment_Client_view {


    public static final int ADDEQUIPMENT = 1000;
    final int EQUIPMENT_REMARK_CODE = 142;
    private final int EQUIPMENT_UPDATE_CODE = 141,
            DETAILSUPDATEFORUSERMANUAL = 142;
    LinearLayout linearFabAdd;
    LinearLayout linearFabclient;
    LinearLayout linearFabown;
    AlertDialogClass alertDialogClass;
    boolean isListLoaded = false, ISOWN = false, ISCLIENT = false;
    List<EquArrayModel> myList = new ArrayList<>();
    //  boolean isOwn = false;
    Job job;
    EditText edtSearch;
    ImageView imvCross;
    String query;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    CustomFilterButton fl_add_Remark,fl_no_remark;
    private LinearLayout nolist_linear;
    TextView nolist_txt, tv_fab_add, tv_fab_client, tv_fab_own;
    FloatingActionButton fab;
    private boolean isFBMenuOpened;
    private JobEquipmentAdapter adapter;
    private Job_equim_PI jobEquimPi;
    private String jobId = "", auditid = "", contrId = "",siteid="";
    private String cltId;
    private String comeFrom="";
    CoordinatorLayout relative_layout;
    private View backgroundView;
    private String appId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_equipment);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(LanguageController.getInstance().getMobileMsgByKey(AppConstant.detail_equipment));
        getintentData();
        initializeView();

    }



    private void getintentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            jobId = getIntent().getStringExtra("JobId");
            auditid = getIntent().getStringExtra("auditid");
            cltId = getIntent().getStringExtra("cltId");
            contrId = getIntent().getStringExtra("contrId");
            appId = getIntent().getStringExtra("appId");
            comeFrom = getIntent().getStringExtra("comeFrom");
            siteid= getIntent().getStringExtra("siteid");
            if (TextUtils.isEmpty(contrId))
                contrId = "";
            else if (contrId.equals("0"))
                contrId = "";
        }
        EotApp.getAppinstance().setNotifyForEquipmentCountList(this);
    }

    private void initializeView() {
        if (jobId != null) {
            job = AppDataBase.getInMemoryDatabase(this).jobModel().getJobsById(jobId);
            cltId = job.getCltId();
        }
        if (!TextUtils.isEmpty(appId))
            alertDialogClass = new AlertDialogClass(this, this, jobId, "", appId);
        else
            alertDialogClass = new AlertDialogClass(this, this, jobId, "", "");
        backgroundView = findViewById(R.id.backgroundView);


        relative_layout = findViewById(R.id.relative_layout);
        AppUtility.setupUI(relative_layout, JobEquipmentActivity.this);

        fab = findViewById(R.id.fab);

        linearFabAdd = findViewById(R.id.linearFabAdd);


        linearFabown = findViewById(R.id.linearFabown);
        linearFabclient = findViewById(R.id.linearFabclient);

        fl_add_Remark=findViewById(R.id.fl_add_Remark);
        fl_add_Remark.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.remarked));

        fl_no_remark=findViewById(R.id.fl_no_remark);
        fl_no_remark.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.unremarked));

        tv_fab_add = findViewById(R.id.tv_fab_add);
        tv_fab_client = findViewById(R.id.tv_fab_client);
        tv_fab_own = findViewById(R.id.tv_fab_own);

        tv_fab_add.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_add_equipment));
        tv_fab_client.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.link_client_equipment));
        tv_fab_own.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.link_own_equipment));

        recyclerView = findViewById(R.id.audit_equipment_list);
        nolist_txt = findViewById(R.id.nolist_txt);
        nolist_linear = findViewById(R.id.nolist_linear);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setRefreshing(false);

        nolist_txt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.equipment_not_found));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(this);
        backgroundView.setOnClickListener(this);
        linearFabAdd.setOnClickListener(this);
        linearFabclient.setOnClickListener(this);
        linearFabown.setOnClickListener(this);
        fl_add_Remark.setOnClickListener(this);
        fl_no_remark.setOnClickListener(this);


        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.search_by_site));
        imvCross = findViewById(R.id.imvCross);

        if (adapter == null) {
            adapter = new JobEquipmentAdapter(this, equipmentRes -> {
                Intent intent = new Intent(JobEquipmentActivity.this, EquipmentDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("job_equip", equipmentRes);
                String str = new Gson().toJson(equipmentRes);
                intent.putExtra("job_equip_str", str);
                intent.putExtra("equipment", true);
                intent.putExtra("jobId", jobId);
                startActivityForResult(intent, DETAILSUPDATEFORUSERMANUAL);
            }, (attchpos, equPos) -> {
                try {
                    if (attchpos == 0 || attchpos == 1) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(App_preference.getSharedprefInstance().getBaseURL()
                                + "" + (myList.get(equPos).getAttachments().get(attchpos).getAttachFileName()))));

                    } else {
                        try {
                            if (myList.size() > 0) {
                                String str = new Gson().toJson(myList.get(equPos).getAttachments());
                                Intent intent = new Intent(JobEquipmentActivity.this, EquipmentAttchmentList.class);
                                intent.putExtra("list", str);
                                startActivity(intent);
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            },jobId);
            adapter.setOnEquipmentSelection(this);
            recyclerView.setAdapter(adapter);
        }
        EotApp.getAppinstance().setNotifyForEquipmentStatusList(this);
        jobEquimPi = new Job_equim_PC(this);
        jobEquimPi.getEquipmentStatus();
        if (job.getEquArray() != null && !job.getEquArray().isEmpty()) {
            //For fetching local list for the first time
            setEuqipmentList(job.getEquArray());
//            jobEquimPi.refreshList(auditid, jobId);
        } else if(comeFrom!=null&&!comeFrom.isEmpty()&&comeFrom.equalsIgnoreCase("Replace")) {
            jobEquimPi.getEquipmentList(jobId);
        } else  {
            jobEquimPi.getEquipmentList(jobId);
        }


        swipeRefreshLayout.setOnRefreshListener(() -> {
            fl_no_remark.setSeleted(false);
            fl_add_Remark.setSeleted(false);
            if (jobEquimPi != null && !auditid.equals("0")) {
                recyclerView.setNestedScrollingEnabled(false);
                AppUtility.progressBarShow(this);
                jobEquimPi.getEquipmentJobList(auditid, jobId);
            }
            else if (jobEquimPi != null && auditid.equals("0")&&!jobId.equals("0")) {
                recyclerView.setNestedScrollingEnabled(false);
                AppUtility.progressBarShow(this);
                jobEquimPi.getEquipmentJobList(jobId, jobId);
            }else {
                swipeRefresh();
            }
        });


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSearch.getText().toString().equals("")) {
                    jobEquimPi.getEquipmentList(auditid);
                }
                query = edtSearch.getText().toString();
                if (query.length() > 0) {
                    if (query.length() >= 1) {
                        imvCross.setVisibility(View.VISIBLE);
                    } else {
                        imvCross.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                query = edtSearch.getText().toString();
                if (query.toString().length()>2)
                {
                filter(query);
                }
                // adapter.getNameFilter().filter(searchText);
                //jobEquimPi.getEquipmentBySiteName(jobId, searchText);
            }
        });
        imvCross.setOnClickListener(v -> {
            edtSearch.setText("");
            jobEquimPi.getEquipmentList(jobId);
        });


    }


    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<EquArrayModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (EquArrayModel s : myList) {
            //if the existing elements contains the search input
            if (s.getEqunm() != null && s.getEqunm().contains(text)&&s.getEqunm().equalsIgnoreCase(text.toLowerCase())||
                     s.getSnm().toLowerCase().contains(text.toLowerCase())
                    || (s.getSno() != null && s.getSno().equalsIgnoreCase(text.toLowerCase())) ||
                    (s.getMno() != null && s.getMno().equalsIgnoreCase(text.toLowerCase()))
                    || (s.getBarcode() != null && s.getBarcode().equalsIgnoreCase(text.toLowerCase())
            )) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }

    private void filterbyremark()
    {
        ArrayList<EquArrayModel> filterbyremark = new ArrayList<>();

        for (EquArrayModel s:myList){
            if (!TextUtils.isEmpty(s.getStatus()) || s.getAttachments() != null && s.getAttachments().size() > 0) {
                filterbyremark.add(s);
            }
        }
        adapter.filterbyaddremark(filterbyremark);
    }

    private void filterbynoremark()
    {
        ArrayList<EquArrayModel> filterbynoremark=new ArrayList<>();
        for (EquArrayModel s:myList)
        {
            if (!(s.getAttachments().size()>0)&&TextUtils.isEmpty(s.getStatus())) {
                filterbynoremark.add(s);
            }
        }
        adapter.filterbynoremark(filterbynoremark);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(jobEquimPi!=null)
        jobEquimPi.getEquipmentList(jobId);

    }

    @Override
    public void setEuqipmentList(List<EquArrayModel> list) {
        AppUtility.progressBarDissMiss();

        this.myList = list;
        isListLoaded = true;
        swipeRefresh();
        Collections.sort(list, (o1, o2) -> o1.getEqunm().compareTo(o2.getEqunm()));

        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
        if (list != null) {
            if (list.size() == 0)
                nolist_linear.setVisibility(View.VISIBLE);
            else nolist_linear.setVisibility(View.GONE);

            adapter.setList(list);
            // to set the list of status
            if(App_preference.getSharedprefInstance().getEquipmentStatusList()!=null&&!App_preference.getSharedprefInstance().getEquipmentStatusList().isEmpty())
                adapter.setEquipmentCurrentStatus(App_preference.getSharedprefInstance().getEquipmentStatusList());

            checkLinkedEquipmentType();
            recyclerView.setNestedScrollingEnabled(true);
        }
    }



    @Override
    public void showErrorAlertDialog(String message) {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
        showDialog(message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }

    @Override
    public void onSessionExpired(String msg) {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
        showDialog(msg);
    }

    @Override
    public void swipeRefresh() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    private void showDialog(String message) {
        AppUtility.alertDialog(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_error_title), message, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
            @Override
            public Boolean call() {
                EotApp.getAppinstance().sessionExpired();
                return null;
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fl_add_Remark.setSeleted(false);
        fl_no_remark.setSeleted(false);
        if (requestCode == EQUIPMENT_REMARK_CODE || requestCode == ADDEQUIPMENT || DETAILSUPDATEFORUSERMANUAL == requestCode && resultCode == Activity.RESULT_OK) {
            if (adapter != null && jobEquimPi != null) {
                jobEquimPi.getEquipmentList(jobId);
            }
        }
        if (requestCode == EQUIPMENT_UPDATE_CODE) {
            if (adapter != null && jobEquimPi != null) {
                jobEquimPi.getEquipmentJobList(jobId,jobId);
            }
        }
    }

    @Override
    public void onEquipmentSelected(int positions, EquArrayModel equipmentRes) {
        if (!job.getTempId().equals(job.getJobId())) {
            Log.e("getAllEquipments", "JobEqRemark JobEquipmentActivity");

            if(equipmentRes.getIsPart().equalsIgnoreCase("1")){
                Intent intent = new Intent(this, JobEquPartRemarkRemarkActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                String strEqu = new Gson().toJson(equipmentRes);
                intent.putExtra("equipment", strEqu);
                intent.putExtra("jobId", jobId);
                intent.putExtra("cltId", cltId);
                intent.putExtra("positions", positions);
                intent.putExtra("isGetData", "");
                startActivityForResult(intent,EQUIPMENT_UPDATE_CODE);
            }
            else {
                Intent intent = new Intent(this, JobEquRemarkRemarkActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                String strEqu = new Gson().toJson(equipmentRes);
                intent.putExtra("equipment", strEqu);
                intent.putExtra("jobId", jobId);
                intent.putExtra("cltId", cltId);
                intent.putExtra("positions", positions);
                intent.putExtra("isGetData", "");
                startActivityForResult(intent, EQUIPMENT_UPDATE_CODE);
            }

        }
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        finish();
    }


    public void showFloatingButtons() {
        if (isFBMenuOpened) closeFABMenu();
        else showFBButtons();
    }


    private void showFBButtons() {
        // #Eye09831
        if (isListLoaded) {
//            if (!isOwn && !TextUtils.isEmpty(contrId)) {
//                linearFabown.setAlpha(0.5f);
//                linearFabclient.setAlpha(1f);
//                linearFabclient.setClickable(true);
//                linearFabown.setClickable(false);
//            } else
            if (adapter != null && adapter.getList().size() == 0) {
                linearFabown.setAlpha(1f);
                linearFabclient.setAlpha(1f);
                linearFabclient.setClickable(true);
                linearFabown.setClickable(true);
            } else if (ISOWN) {
                linearFabown.setAlpha(1f);
                linearFabclient.setAlpha(0.5f);
                linearFabclient.setClickable(false);
                linearFabown.setClickable(true);
            } else if (ISCLIENT) {
                linearFabown.setAlpha(0.5f);
                linearFabclient.setAlpha(1f);
                linearFabclient.setClickable(true);
                linearFabown.setClickable(false);
            }
        } else {
            linearFabown.setAlpha(0.5f);
            linearFabclient.setAlpha(0.5f);
            linearFabclient.setClickable(false);
            linearFabown.setClickable(false);
        }
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_color)));
        getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#90ffffff")));
        backgroundView.setVisibility(View.VISIBLE);
        if (App_preference.getSharedprefInstance().getLoginRes().getCompPermission().get(0).getIsEquipmentAddEnable().equals("0")) {
            linearFabAdd.setVisibility(View.VISIBLE);
        } else linearFabAdd.setVisibility(View.GONE);
        linearFabclient.setVisibility(View.VISIBLE);
        linearFabown.setVisibility(View.VISIBLE);
        linearFabAdd.animate().translationY(getResources().getDimension(R.dimen.standard_145));
        linearFabclient.animate().translationY(getResources().getDimension(R.dimen.standard_100));
        linearFabown.animate().translationY(getResources().getDimension(R.dimen.standard_55));
        isFBMenuOpened = true;
    }

    private void closeFABMenu() {
        isFBMenuOpened = false;
        linearFabAdd.animate().translationY(0);
        linearFabclient.animate().translationY(0);
        linearFabown.animate().translationY(0);

        linearFabown.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFBMenuOpened) {
                    backgroundView.setVisibility(View.GONE);
                    linearFabown.setVisibility(View.GONE);
                    linearFabclient.setVisibility(View.GONE);
                    linearFabAdd.setVisibility(View.GONE);
                    Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

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
            case R.id.fab:
                if (cltId.equals("0")) {
                    alertDialogClass.alertDialog();
                } else {
                    showFloatingButtons();
                }
                break;

            case R.id.linearFabAdd:
                if (AppUtility.isInternetConnected()) {

                    Intent intent = new Intent(JobEquipmentActivity.this, AddJobEquipMentActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("JobId", jobId);
                    intent.putExtra("cltId", cltId);
                    startActivityForResult(intent, ADDEQUIPMENT);

                } else {
                    AppUtility.alertDialog(JobEquipmentActivity.this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            return null;
                        }
                    });
                }
                closeFABMenu();
                break;
            case R.id.fl_add_Remark:
                if (fl_add_Remark.isSeleted()){
                    adapter.setList(myList);
                    fl_add_Remark.setSeleted(false);
                }else {
                    fl_add_Remark.setSeleted(!fl_add_Remark.isSeleted());
                    fl_no_remark.setSeleted(false);
                    filterbyremark();
                }
                break;
            case R.id.fl_no_remark:
                if(fl_no_remark.isSeleted())
                {
                    adapter.setList(myList);
                    fl_no_remark.setSeleted(false);
                }else {
                    fl_no_remark.setSeleted(!fl_no_remark.isSeleted());
                    fl_add_Remark.setSeleted(false);
                    filterbynoremark();
                }
                break;
            case R.id.linearFabclient:
                Intent intent = new Intent(JobEquipmentActivity.this, ActivityLinkEquipment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("type", "2");
                intent.putExtra("cltId", cltId);
                intent.putExtra("id", jobId);
                intent.putExtra("contrId", contrId);
                startActivityForResult(intent, EQUIPMENT_UPDATE_CODE);
                closeFABMenu();
                break;

            case R.id.linearFabown:
                Intent intent1 = new Intent(JobEquipmentActivity.this, ActivityLinkEquipment.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent1.putExtra("type", "1");
                intent1.putExtra("cltId", "");
                intent1.putExtra("id", jobId);
                intent1.putExtra("siteid",siteid);
                startActivityForResult(intent1, EQUIPMENT_UPDATE_CODE);
                closeFABMenu();
                break;

            case R.id.backgroundView:
                closeFABMenu();
                break;

        }
    }


    private void checkLinkedEquipmentType() {
        if (adapter != null && adapter.getList() != null &&
                adapter.getList().size() > 0) {
            // isOwn = adapter.getList().get(0).getType().equals("1");
            ISOWN = adapter.getList().get(0).getType().equals("1");
            ISCLIENT = adapter.getList().get(0).getType().equals("2");
            // if (!TextUtils.isEmpty(contrId)) isOwn = false;
        }
    }


    @Override
    public void setClientForSaveUse(EquipmentSaveClientRes equipmentSaveClientRes) {

        cltId = equipmentSaveClientRes.getCltId();
    }


    @Override
    public void updateCountEquipment() {
        Log.e("OnCreate", "Item Equipment Called JobEquipment");
        if(jobEquimPi!=null)
            jobEquimPi.getEquipmentList(jobId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App_preference.getSharedprefInstance().setcatglist(null);
        App_preference.getSharedprefInstance().setBrandLists(null);
        App_preference.getSharedprefInstance().setGrpList(null);
        App_preference.getSharedprefInstance().seteqstatuslist(null);
    }

    @Override
    public void updateStatusEquipment() {
        setEuqipmentList(job.getEquArray());
    }
}