package com.eot_app.nav_menu.jobs.job_detail.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.eot_app.R;
import com.eot_app.databinding.DialogJobCardBinding;
import com.eot_app.nav_menu.jobs.job_detail.JobDetailActivity;
import com.eot_app.nav_menu.jobs.job_detail.generate_invoice.Generate_Invoice_Activity;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_email_pkg.Invoice_Email_Activity;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_email_pkg.get_email_temp_model.InvoiceEmaliTemplate;
import com.eot_app.nav_menu.jobs.job_detail.invoice2list.itemlist_model.InvoiceItemDetailsModel;
import com.eot_app.nav_menu.jobs.job_detail.invoice2list.itemlist_mvp.ItemList_PI;
import com.eot_app.nav_menu.jobs.job_detail.job_detail_activity_presenter.Job_Detail_Activity_pi;
import com.eot_app.nav_menu.quote.quote_invoice_pkg.quote_mvp_pkg.Quo_Invo_Pi;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.settings.setting_db.FieldWorker;
import com.eot_app.utility.util_interfaces.MySpinnerAdapter;
import com.google.gson.Gson;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

public class DialogJobCardDocuments extends DialogFragment
        implements View.OnClickListener, Spinner.OnItemSelectedListener {

    Context activity;
    DialogJobCardBinding binding;
    Job_Detail_Activity_pi detail_activity_pi;
    private ItemList_PI itemListPi;
    Quo_Invo_Pi quo_invo_pi;
    String jobId = "";
    String tempId = "";
    String fwId = "";
    String[] fwList;
    ArrayList<InvoiceEmaliTemplate> templateList;
    String[] kprList;
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    String quotId;
    private InvoiceItemDetailsModel invoice_Details;

    public void setdetail_activity_pi(Job_Detail_Activity_pi detail_activity_pi) {
        this.detail_activity_pi = detail_activity_pi;
    }
    public void setitemListPi(ItemList_PI itemListPi) {
        this.itemListPi = itemListPi;
    }
    public void setinvoice_Details(InvoiceItemDetailsModel invoice_Details) {
        this.invoice_Details = invoice_Details;
    }
    public void setFwList(String[] fwList2) {
        this.fwList = fwList2;
        Log.e("fwList set",new Gson().toJson(fwList));
    }
    public void setquo_invo_pi(Quo_Invo_Pi quo_invo_pi) {
        this.quo_invo_pi=quo_invo_pi;
    }
    public void setInvoiceTmpList(ArrayList<InvoiceEmaliTemplate> templateList) {
        this.templateList = templateList;
        Log.e("TemplateData::", "setList:"+new Gson().toJson(this.templateList));
        AppUtility.progressBarDissMiss();
    }
    public void setQuoteId(String quoteId) {
        this.quotId = quoteId;

    }

    public void setContext(Context activity) {
        this.activity = activity;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_job_card, container, false);
        View view = binding.getRoot();
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUtility.progressBarDissMiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppUtility.progressBarDissMiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUtility.progressBarDissMiss();
    }

    void initViews() {


       // Log.e("fwList",new Gson().toJson(fwList));

        if (fwList != null && fwList.length > 0) {
            kprList = new String[fwList.length];
            AppUtility.spinnerPopUpWindow(binding.signatureDp);
            int pos = 0;
            for (String id : fwList) {
                FieldWorker fieldWorker = AppDataBase.getInMemoryDatabase(getActivity()).fieldWorkerModel().getFieldWorkerByID(id);
                if(fieldWorker!=null)
                {
                    kprList[pos]=fieldWorker.getName()+" "+fieldWorker.getLnm();
                    if (pos == 0) {
                        setFwStatus(pos);
                    }
                }
                pos++;
            }
            binding.signatureDp.setAdapter(new MySpinnerAdapter(activity, kprList));
        }

        if (templateList != null && templateList.size() > 0) {
            binding.templateView.setVisibility(View.VISIBLE);
            AppUtility.spinnerPopUpWindow(binding.templatDp);
            String[] statusList = new String[templateList.size()];
            int pos = 0;
            for (InvoiceEmaliTemplate status : templateList) {
                statusList[pos] = status.getInputValue();
                pos++;
            }
            binding.templatDp.setAdapter(new MySpinnerAdapter(activity, statusList));
            for (int i = 0; i < templateList.size(); i++) {
                if (templateList.get(i).getDefaultTemp().equalsIgnoreCase("1"))
                    setEquStatus(i);
            }
        }
        else {
            binding.templateView.setVisibility(View.GONE);
        }


        binding.buttonSendEmail.setOnClickListener(this);
        binding.buttonView.setOnClickListener(this);
        binding.linearLayoutSignature.setOnClickListener(this);
        binding.linearLayoutTemplat.setOnClickListener(this);
        binding.templatDp.setOnItemSelectedListener(this);
        binding.signatureDp.setOnItemSelectedListener(this);

        binding.hintTemplateTxt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_template));
        binding.hintSignatureTxt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.tech_sign));

        binding.templatTxt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_template));
        binding.signatureTxt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.tech_sign));

        if (jobId != null&&!jobId.isEmpty()) {
            binding.buttonSendEmail.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.email_job_card));
            binding.buttonView.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.print_job_card));
            binding.buttonSendEmail.setVisibility(View.VISIBLE);
        }
        else if(quotId!=null&&!quotId.isEmpty()){
            binding.buttonView.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.print_quote));
            binding.buttonSendEmail.setVisibility(View.GONE);
        }
        else {
            binding.buttonView.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.print_invoice));
            binding.buttonSendEmail.setVisibility(View.GONE);
        }

    }


    public void setData() {

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_send_email:
                if (jobId != null&&!jobId.isEmpty()) {
                    Intent intent = new Intent(activity, Invoice_Email_Activity.class);
                    intent.putExtra("jobId", jobId);
                    intent.putExtra("tempId", tempId);
                    intent.putExtra("fwid",fwId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    dismiss();
                }
                break;
            case R.id.button_view:
                dismiss();
                if (detail_activity_pi != null && jobId != null)
                    detail_activity_pi.printJobCard(jobId, tempId,fwId);
                else if (quo_invo_pi != null && quotId != null)
                    quo_invo_pi.generateQuotPDF(quotId,tempId);
                else if (itemListPi!=null&&invoice_Details != null) {
                    String isProformaInv = "0";
                    if (invoice_Details.getIsShowInList() != null && invoice_Details.getIsShowInList().equals("0"))
                        isProformaInv = "1";
                    else isProformaInv = "0";
                    itemListPi.getGenerateInvoicePdf(invoice_Details.getInvId(), isProformaInv,tempId);
                }
                break;
            case R.id.linearLayout_templat:
                binding.templatDp.performClick();
                break;
            case R.id.linearLayout_signature:
                binding.signatureDp.performClick();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.templat_Dp) {
            setEquStatus(position);
        }
        if (parent.getId() == R.id.signature_Dp) {
            setFwStatus(position);
        }
    }

    private void setFwStatus(int pos) {
        binding.hintSignatureTxt.setVisibility(View.VISIBLE);
        fwId = fwList[pos];
        String selectedValue = kprList[pos];
        binding.signatureTxt.setText(selectedValue);
    }

    private void setEquStatus(int pos) {
        binding.hintTemplateTxt.setVisibility(View.VISIBLE);
        tempId = templateList.get(pos).getInvTempId();
        String selectedValue = templateList.get(pos).getInputValue();
        binding.templatTxt.setText(selectedValue);
        if(templateList.get(pos).isTechSignature()&&jobId!=null){
            binding.signatureView.setVisibility(View.VISIBLE);
        }
        else {
            fwId="";
            binding.signatureView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
