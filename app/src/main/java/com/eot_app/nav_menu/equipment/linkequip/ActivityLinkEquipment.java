package com.eot_app.nav_menu.equipment.linkequip;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eot_app.R;
import com.eot_app.nav_menu.audit.audit_list.equipment.model.EquipmentStatus;
import com.eot_app.nav_menu.equipment.link_own_client_equ_barc.JobEquipmentScanActivity;
import com.eot_app.nav_menu.equipment.linkequip.linkMVP.LinkEquipmentPC;
import com.eot_app.nav_menu.equipment.linkequip.linkMVP.LinkEquipmentPI;
import com.eot_app.nav_menu.equipment.linkequip.linkMVP.LinkEquipmentView;
import com.eot_app.nav_menu.equipment.linkequip.linkMVP.model.ContractEquipmentReq;
import com.eot_app.nav_menu.jobs.job_db.EquArrayModel;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.util_interfaces.MySpinnerAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityLinkEquipment extends AppCompatActivity implements View.OnClickListener,
        LinkEquipmentView, EquipmentLinkAdapter.OnEquipmentSelection {
    static public boolean SCANBY = false;
    LinkEquipmentPI linkEquipmentPI;
    RecyclerView recyclerView;//, found_equipment_list;
    ContentLoadingProgressBar content_loading_progress;
    LinearLayout nolist_linear;
    TextView nolist_txt;
    AppCompatTextView tv_equipment_size;
    Spinner spinner_filter;
    AppCompatTextView tv_filter_name, tv_contract_msg;//, found_equip;
    EquipmentLinkAdapter adapter;
    List<EquArrayModel> myEquList = new ArrayList<>();
    List<EquipmentStatus> statusList = new ArrayList<>();
    private String type;
    private String cltId = "";
    private String siteId="";
    private String id; //jobId or auditId
    private String contrId = "";
    private String tempContrId = "";
    private boolean contractMsg = true;
    Button save_btn;
    List<String> linkedEquipments=new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_equipment);

        getSupportActionBar().setTitle(LanguageController.getInstance().getMobileMsgByKey(AppConstant.link_equipment));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        type = getIntent().getStringExtra("type");
        cltId = getIntent().getStringExtra("cltId");
        id = getIntent().getStringExtra("id");
        contrId = getIntent().getStringExtra("contrId");
        siteId=getIntent().getStringExtra("siteid");



        tempContrId = contrId;


        tv_contract_msg = findViewById(R.id.tv_contract_msg);
        tv_contract_msg.setMovementMethod(LinkMovementMethod.getInstance());

        setContractMsg(contractMsg);

        tv_filter_name = findViewById(R.id.tv_filter_name);
        tv_filter_name.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.all));

        tv_equipment_size = findViewById(R.id.tv_equipment_size);
        spinner_filter = findViewById(R.id.spinner_filter);


        save_btn = findViewById(R.id.btn_save);
        save_btn.setOnClickListener(this);
        save_btn.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.link_unlink_equi));

        nolist_linear = findViewById(R.id.nolist_linear);
        nolist_txt = findViewById(R.id.nolist_txt);
        nolist_txt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.equipment_not_found));

        recyclerView = findViewById(R.id.audit_equipment_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        content_loading_progress = findViewById(R.id.content_loading_progress);

        adapter = new EquipmentLinkAdapter(this);
        adapter.setList(Collections.<EquArrayModel>emptyList());
        adapter.setOnEquipmentSelection(this);
        recyclerView.setAdapter(adapter);


        final String[] filterOption = {
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.all),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.linked),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.unlinked)};


        linkEquipmentPI = new LinkEquipmentPC(this);

        /*****Android***/
        //  setContractMsg(contractMsg);

        AppUtility.spinnerPopUpWindow(spinner_filter);
        spinner_filter.setAdapter(new MySpinnerAdapter(this, filterOption));
        spinner_filter.setSelection(0);

        spinner_filter.post(new Runnable() {
            @Override
            public void run() {
                spinner_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (adapter != null) {
                            adapter.filterListByStatus(position);
                        }
                        tv_filter_name.setText(filterOption[position]);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        });
        tv_filter_name.setOnClickListener(v -> spinner_filter.performClick());
    }

    private void setContractMsg(boolean isContractMsg) {
        String msg = "";
        if (isContractMsg)
            msg = LanguageController.getInstance().getMobileMsgByKey(AppConstant.contract_link_msg);
        else
            msg = LanguageController.getInstance().getMobileMsgByKey(AppConstant.client_link_msg);

        String yes_no = LanguageController.getInstance().getMobileMsgByKey(AppConstant.yes);
        String s = msg + "  " + yes_no + "  ";
        Spannable spannable = new SpannableString(s);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.WHITE);
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(getResources().getColor(R.color.colorPrimary));

        spannable.setSpan(backgroundColorSpan, msg.length(), s.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //   FROMSCANBARCODE = false;
                if (contractMsg) {
                    contractMsg = false;
                    contrId = "";
                } else {
                    contractMsg = true;
                    contrId = tempContrId;
                }
                setContractMsg(contractMsg);
                refreshEquipmentList();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
                ds.setUnderlineText(false);
            }
        };
        spannable.setSpan(foregroundColorSpan, msg.length(), s.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannable.setSpan(clickableSpan, msg.length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_contract_msg.setText(spannable);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.ic_qr_code) {
            Intent intent = new Intent(ActivityLinkEquipment.this, JobEquipmentScanActivity.class);
            intent.putExtra("JOBID", id);
            intent.putExtra("type", type);
            intent.putExtra("cltId", cltId);
            intent.putExtra("contrId", contrId);
            Gson gson = new Gson();
            String str = gson.toJson(myEquList);
            intent.putExtra("myEquList", str);
            String strstatusstr = gson.toJson(statusList);
            intent.putExtra("strstatus", strstatusstr);
            startActivityForResult(intent, 330);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("", "");
        if (data != null) {
            if (requestCode == 330) {
                try {
                    if (data.hasExtra("str")) {
                        List<EquArrayModel> list = new ArrayList<>();
                        String str = data.getStringExtra("str");
                        //    foundequ_layout.setVisibility(View.VISIBLE);
                        EquArrayModel model = new Gson().fromJson(str, EquArrayModel.class);
                        list.add(model);
                        myEquList.remove(model);
                        SCANBY = true;
                        myEquList.add(0, model);
                        setEquipmentList(myEquList);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println();
            showDialogTax();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDialogTax() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.equ_not_lay);
        TextView eqi_not_foun_txt = dialog.findViewById(R.id.eqi_not_foun_txt);
        eqi_not_foun_txt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.eqi_not_foun_txt));
        //  eqi_not_foun_txt.setText("Equipment not avalable in our database");
        TextView not_msg = dialog.findViewById(R.id.not_msg);
        TextView ok_btn = dialog.findViewById(R.id.ok_btn);
        not_msg.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.not_msg));
        //not_msg.setText("No Record Found");
        ok_btn.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok));

         /* *add all taxes into the views */

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.equipment_barcode_menu, menu);
        return true;
    }

    @Override
    public void updateLinkUnlinkEqu() {


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setEquipmentList(List<EquArrayModel> list) {
        if (list != null) {
            if (list.size() == 0) {
                nolist_linear.setVisibility(View.VISIBLE);
                tv_equipment_size.setText(LanguageController.getInstance()
                        .getMobileMsgByKey(AppConstant.equipment) + " (0)"
                );
            } else {
                myEquList = list;
                tv_equipment_size.setText(LanguageController.getInstance()
                        .getMobileMsgByKey(AppConstant.equipment) + " (" + list.size() + ")"
                );
                nolist_linear.setVisibility(View.GONE);

                adapter.setList(list);
                if (adapter != null) {
                    adapter.filterListByStatus(spinner_filter.getSelectedItemPosition());
                }
            }

        }
    }


    @Override
    public void showHideProgressBar(boolean isShowProgress) {
        if (isShowProgress) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            content_loading_progress.setVisibility(View.VISIBLE);
        } else {
            content_loading_progress.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void refreshEquipmentList() {
        if (TextUtils.isEmpty(contrId))
            linkEquipmentPI.getEquipmentList(type, cltId, id);
        else linkEquipmentPI.getContractList(new ContractEquipmentReq(type, id, contrId));

        onBackPressed();
    }

    @Override
    public void onSessionExpired(String message) {
        AppUtility.alertDialog(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_error_title), message, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> {
            EotApp.getAppinstance().sessionExpired();
            return null;
        });
    }

    @Override
    public void setEquStatusList(List<EquipmentStatus> list) {
        statusList = list;
        if (adapter != null)
            adapter.setEquipmentStatus(list);
        if (!TextUtils.isEmpty(type)) {
            if (TextUtils.isEmpty(contrId)) {
                tv_contract_msg.setVisibility(View.GONE);
                linkEquipmentPI.getEquipmentList(type, cltId, id);
            } else {
                tv_contract_msg.setVisibility(View.VISIBLE);
                linkEquipmentPI.getContractList(new ContractEquipmentReq(type, id, contrId));
            }
        }

    }

    @Override
    public void onEquipmentSelected(List<String> linkedEquipments) {
        setResult(RESULT_OK);
        save_btn.setVisibility(View.VISIBLE);
        this.linkedEquipments.clear();
        this.linkedEquipments=linkedEquipments;

//        if (linkedEquipments != null && linkEquipmentPI != null) {
//            linkEquipmentPI.addAuditEquipment(linkedEquipments, id, "");
//        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onListSizeChange(int size) {
        if (size == 0) {
            nolist_linear.setVisibility(View.VISIBLE);
            tv_equipment_size.setText(LanguageController.getInstance()
                    .getMobileMsgByKey(AppConstant.equipment) + " (0)"
            );
        } else {
            tv_equipment_size.setText(LanguageController.getInstance()
                    .getMobileMsgByKey(AppConstant.equipment) + " (" + size + ")"
            );
            nolist_linear.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.btn_save)
       {
           if (linkedEquipments != null && linkEquipmentPI != null) {
               linkEquipmentPI.addAuditEquipment(linkedEquipments, id, "");
           }
       }
    }
}
