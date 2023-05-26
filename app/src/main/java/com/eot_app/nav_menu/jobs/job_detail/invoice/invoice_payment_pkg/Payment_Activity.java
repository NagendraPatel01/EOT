package com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_payment_pkg;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.eot_app.R;
import com.eot_app.UploadDocumentActivity;
import com.eot_app.databinding.ActivityPaymentBinding;
import com.eot_app.nav_menu.client.clientlist.client_detail.site.sitelist.editsite.editsitedb.SpinnerCountrySite;
import com.eot_app.nav_menu.jobs.job_detail.documents.DocumentListAdapter;
import com.eot_app.nav_menu.jobs.job_detail.documents.doc_model.GetFileList_Res;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_detail_pkg.inv_detail_model.Inv_Res_Model;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_payment_pkg.payment_mvp.PayMent_View;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_payment_pkg.payment_mvp.Payment_pc;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_payment_pkg.payment_mvp.Payment_pi;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.util_interfaces.Callback_AlertDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Payment_Activity extends UploadDocumentActivity implements  DocumentListAdapter.FileDesc_Item_Selected,
        View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, PayMent_View {
    final Calendar myCalendar = Calendar.getInstance();
    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            txt_date_value.setText(AppUtility.getDateWithFormate2(myCalendar.getTimeInMillis(), "dd-MMM-yyyy"));
        }
    };
    Inv_Res_Model invoiceDetails;
    Spinner payment_type_spinner;
  //  String[] paymentType = {"Cash", "Cheque", "Credit Card", "Debit Card", "Wire Transfer", "Paypal", "Direct Deposit", "Stripe"};
    int type = 1;
    List<String> paymentType;
    String payType = "", invId = "", invNm = "";
    TextInputLayout input_layout_amount, input_layout_payment_notes;
    String jobId;
    TextView payment_date, txtTotalAmount, txtPaidAmount, txtDueAmount, tv_payment_type, txt_date_value, totalInvoiceAmo, paidAmo, dueAmo, txtPayType;
    private Button btnPayment;
    private EditText edtAmountReceived, notesInvoice;
    private Payment_pi payment_pi;
    private LinearLayout linearPaymentType, linearPaymentDate;
    private Boolean isEmailSendOrNot = false;
    ActivityPaymentBinding binding ;
    CheckBox checkbox_payment;
    LinearLayout upload_lable_layout;
    AppCompatTextView upload_lable, click_here_txt, tv_label_attachment;
    private DocumentListAdapter adapter;
    RecyclerView recyclerView_attachment;
    String imagePath;
    double totalDueAmount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_payment);
        setTitle(LanguageController.getInstance().getMobileMsgByKey(AppConstant.title_payment));

        Bundle bundle = getIntent().getExtras();
        if (getIntent().hasExtra("JobId")) {
            jobId = bundle.getString("JobId");
        }

        paymentType=new ArrayList<>();
        paymentType.add(LanguageController.getInstance().getMobileMsgByKey(AppConstant.cash));
        paymentType.add(LanguageController.getInstance().getMobileMsgByKey(AppConstant.cheque));
        paymentType.add(LanguageController.getInstance().getMobileMsgByKey(AppConstant.credit_card));
        paymentType.add(LanguageController.getInstance().getMobileMsgByKey(AppConstant.Debit_card));
        paymentType.add(LanguageController.getInstance().getMobileMsgByKey(AppConstant.wire_transfer));
        paymentType.add(LanguageController.getInstance().getMobileMsgByKey(AppConstant.paypal));
        paymentType.add("");
        paymentType.add(LanguageController.getInstance().getMobileMsgByKey(AppConstant.stripe));


        initializelables();
        findViews();
        setTextData();

        payment_pi = new Payment_pc(this);
        payment_pi.getInvoiceDetails(jobId);

        AppUtility.spinnerPopUpWindow(payment_type_spinner);
        PaymentSpinnerAdapter adapter = new PaymentSpinnerAdapter(this, paymentType);
        payment_type_spinner.setAdapter(adapter);
        payment_type_spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onPaymentSuccess(String message) {
        EotApp.getAppinstance().showToastmsg(message);
        finish();
        AppUtility.hideSoftKeyboard(Payment_Activity.this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linearPaymentDate:
                new DatePickerDialog(this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.upload_lable_layout:
                type = 1;
                selectFile(true);
                break;
            case R.id.invoice_save_btn:
                final String amount = edtAmountReceived.getText().toString().trim();
                final String notes = notesInvoice.getText().toString().trim();
                if (payment_pi.isInputFieldDataValid(amount, payType, invoiceDetails)) {

                    if (App_preference.getSharedprefInstance().getLoginRes().getConfirmationTrigger() != null) {
                        //for (String trigger : App_preference.getSharedprefInstance().getLoginRes().getConfirmationTrigger()) {
                        if (App_preference.getSharedprefInstance().getLoginRes().getConfirmationTrigger().contains("11")) {
                            AppUtility.alertDialog2(this,
                                    "",
                                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.send_client_mail),
                                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.yes),
                                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.no), new Callback_AlertDialog() {
                                        @Override
                                        public void onPossitiveCall() {
                                            payment_pi.payment_invoice_apicall(amount, notes,
                                                    invId, invNm,
                                                    payType, myCalendar.getTimeInMillis(),
                                                    isEmailSendOrNot, jobId, "1",imagePath);
                                        }

                                        @Override
                                        public void onNegativeCall() {
                                            payment_pi.payment_invoice_apicall(amount, notes,
                                                    invId, invNm,
                                                    payType, myCalendar.getTimeInMillis(),
                                                    isEmailSendOrNot, jobId, "0",imagePath);
                                        }
                                    });
                            // break;
                            //}
                        } else {
                            notMailStatusFound(amount, notes);
                        }
                    } else {
                        notMailStatusFound(amount, notes);
                    }

                }
                break;
            case R.id.paymentType:
                payment_type_spinner.performClick();
                break;
        }
    }

    private void notMailStatusFound(String amount, String notes) {
        payment_pi.payment_invoice_apicall(amount, notes,
                invId, invNm,
                payType, myCalendar.getTimeInMillis(),
                isEmailSendOrNot, jobId, "1",imagePath);
    }

    @SuppressLint("SetTextI18n")
    private void initializelables() {
        totalInvoiceAmo = findViewById(R.id.totalInvoiceAmo);
        totalInvoiceAmo.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.total_inv_amt));
        paidAmo = findViewById(R.id.paidAmo);
        paidAmo.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.paid_amt));
        dueAmo = findViewById(R.id.dueAmo);
        dueAmo.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.due_amt));
        payment_date = findViewById(R.id.txt_payment_date);
        payment_date.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.pay_date));
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtPaidAmount = findViewById(R.id.txtPaidAmount);
        txtDueAmount = findViewById(R.id.txtDueAmount);
        btnPayment = findViewById(R.id.invoice_save_btn);
        btnPayment.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.save_btn));
        edtAmountReceived = findViewById(R.id.edt_amount_received);
        edtAmountReceived.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.amt));
        notesInvoice = findViewById(R.id.edt_payment_notes);
        notesInvoice.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.notes));
        payment_type_spinner = findViewById(R.id.payment_type_spinner);
        linearPaymentType = findViewById(R.id.paymentType);
        tv_payment_type = findViewById(R.id.txt_payment_type);
        tv_payment_type.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.lable_pay_type));
        input_layout_amount = findViewById(R.id.input_layout_amount);
        input_layout_payment_notes = findViewById(R.id.input_layout_payment_notes);
        txt_date_value = findViewById(R.id.txt_date_value);
        linearPaymentDate = findViewById(R.id.linearPaymentDate);
        txtPayType = findViewById(R.id.txtPayType);
        txtPayType.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.lable_pay_type));

        upload_lable_layout = findViewById(R.id.upload_lable_layout);
        upload_lable_layout.setOnClickListener(this);
        upload_lable = findViewById(R.id.upload_lable);
        upload_lable.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.expense_upload));
        click_here_txt = findViewById(R.id.click_here_txt);
        click_here_txt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.doc_here));
        tv_label_attachment = findViewById(R.id.tv_label_attachment);
        tv_label_attachment.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.attachment));

        checkbox_payment = findViewById(R.id.checkbox_payment);
        checkbox_payment.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.send_payment_reciept));
        checkbox_payment.setOnCheckedChangeListener((buttonView, isChecked) -> isEmailSendOrNot = isChecked);

        /* ***set  Default Curreny*****/
        txtTotalAmount.setText(SpinnerCountrySite.getCourrencyNamebyId(App_preference.getSharedprefInstance().getCompanySettingsDetails().getCur()) + AppUtility.getRoundoff_amount(String.valueOf(0)));
        txtPaidAmount.setText(SpinnerCountrySite.getCourrencyNamebyId(App_preference.getSharedprefInstance().getCompanySettingsDetails().getCur()) + AppUtility.getRoundoff_amount(String.valueOf(0)));
        txtDueAmount.setText(SpinnerCountrySite.getCourrencyNamebyId(App_preference.getSharedprefInstance().getCompanySettingsDetails().getCur()) + AppUtility.getRoundoff_amount(String.valueOf(0)));

    }

    private void findViews() {
        Objects.requireNonNull(input_layout_amount.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(input_layout_payment_notes.getEditText()).addTextChangedListener(this);

        linearPaymentType.setOnClickListener(this);
        btnPayment.setOnClickListener(this);
        linearPaymentDate.setOnClickListener(this);
    }

    private void setTextData() {
        tv_payment_type.setText(paymentType.get(0));
        payType = "1";
        txt_date_value.setText(AppUtility.getDateWithFormate2(myCalendar.getTimeInMillis(), "dd-MMM-yyyy"));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        tv_payment_type.setText(paymentType.get(i));
        payType = String.valueOf(adapterView.getSelectedItemPosition() + 1);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String roundoff_amount = AppUtility.getRoundoff_amount(edtAmountReceived.getText().toString());
        String totaldueamount = AppUtility.getRoundoff_amount(totalDueAmount + "");
        if (charSequence.length() >= 1) {
            if (charSequence.hashCode() == edtAmountReceived.getText().hashCode()) {
                input_layout_amount.setHintEnabled(true);
                if (!edtAmountReceived.getText().toString().isEmpty()&&Double.parseDouble(roundoff_amount)>Double.parseDouble(totaldueamount)){
                    showError(LanguageController.getInstance().getMobileMsgByKey(AppConstant.amount_error));
                    edtAmountReceived.setText("0");
                }
            }
            if (charSequence.hashCode() == notesInvoice.getText().hashCode()) {
                input_layout_payment_notes.setHintEnabled(true);
            }
        } else if (charSequence.length() <= 0) {
            if (charSequence.hashCode() == edtAmountReceived.getText().hashCode()) {
                input_layout_amount.setHintEnabled(false);
            }
            if (charSequence.hashCode() == notesInvoice.getText().hashCode()) {
                input_layout_payment_notes.setHintEnabled(false);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setData(Inv_Res_Model invoice_Details) {
        this.invoiceDetails = invoice_Details;
        if (invoiceDetails != null && invoiceDetails.getInvId() != null) {
            invId = invoiceDetails.getInvId();
            invNm = invoiceDetails.getNm();
        }
        //currencyList("currency.json");
        double remainingAmount = 0, totalAmount = 0, paidAmount = 0;
        if (invoice_Details != null) {
            if (invoice_Details.getTotal() != null && !invoice_Details.getTotal().equals("")) {

                try {
                    totalAmount = Double.parseDouble(AppUtility.getRoundoff_amount(invoice_Details.getTotal()));
                    txtTotalAmount.setText(invoice_Details.getCurSym() + AppUtility.getRoundoff_amount(String.valueOf(totalAmount)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            if (invoice_Details.getPaid() != null && !invoice_Details.getPaid().equals("")) {
                try {
                    paidAmount = Double.parseDouble(AppUtility.getRoundoff_amount(invoice_Details.getPaid()));
                    txtPaidAmount.setText(invoice_Details.getCurSym() + AppUtility.getRoundoff_amount(String.valueOf(paidAmount)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            remainingAmount = totalAmount - paidAmount;

            totalDueAmount=remainingAmount;
            /* *
             * live fix 2.32 null currency showing
             **/
            if (invoice_Details != null && !TextUtils.isEmpty(invoice_Details.getCurSym())){
                txtDueAmount.setText(invoice_Details.getCurSym() + AppUtility.getRoundoff_amount(String.valueOf(remainingAmount)));
            }

        }
        edtAmountReceived.setText(AppUtility.getRoundoff_amount(String.valueOf(totalDueAmount)));
    }

    @Override
    public void onInvoiceSuccessFalse(String errorMessage) {
        AppUtility.alertDialog(this, "", errorMessage, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                finish();
                return null;
            }
        });
    }


    @Override
    public void showErrorAlertDialog(String errorMessage) {
        AppUtility.alertDialog(this, "", errorMessage, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return null;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            AppUtility.hideSoftKeyboard(Payment_Activity.this);
            /**
             * live fix 2.32 null currency showing
             **/
            onBackPressed();
        }
        /**
         * live fix 2.32 null currency showing
         **/
        return true;
    }

    private void setAttachments(ArrayList<GetFileList_Res> attachments) {
        if (attachments != null) {
            if (attachments.size() > 0) {
                tv_label_attachment.setVisibility(View.VISIBLE);
            } else {
                tv_label_attachment.setVisibility(View.GONE);
            }
            if (adapter == null) {
                recyclerView_attachment.setLayoutManager(new GridLayoutManager(this, 2));
                adapter = new DocumentListAdapter(this, attachments, jobId);
                recyclerView_attachment.setAdapter(adapter);
            } else {
                adapter.updateFileList(attachments);
            }
        }

    }

    public static final int SINGLEATTCHMENT = 365;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SINGLEATTCHMENT) {
            if (data.hasExtra("code")) {
                String barcode = data.getStringExtra("code");
                ArrayList<GetFileList_Res> attachmentList = new ArrayList<>();
                try {
                    Type listType = new TypeToken<List<GetFileList_Res>>() {
                    }.getType();
                    attachmentList = new Gson().fromJson(barcode, listType);
                        /*if (equipment != null && equipment.getAttachments() != null && equipment.getAttachments().size() > 0) {
                            equipmentList.addAll(equipment.getAttachments());
                        }*/
                        /*if (equipmentList.size() > 0) {
                            setAttachments(equipmentList);
                        }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void OnItemClick_Document(GetFileList_Res getFileList_res) {
        if (getFileList_res != null)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(App_preference.getSharedprefInstance().getBaseURL() + "" + getFileList_res.getAttachFileName())));
    }

    @Override
    public void onDocumentSelected(String path, String name, boolean isImage) {
        super.onDocumentSelected(path, name, isImage);
        imagePath = path;
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        binding.ivAttachment.setImageBitmap(myBitmap);
        binding.llAttachment.setVisibility(View.VISIBLE);

    }

    private void showError(String msg) {
        AppUtility.alertDialog(this, "", msg, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok),
                "", () -> true);
    }
}
