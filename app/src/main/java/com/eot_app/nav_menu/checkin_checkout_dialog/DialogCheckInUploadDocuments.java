package com.eot_app.nav_menu.checkin_checkout_dialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.eot_app.R;
import com.eot_app.databinding.CheckinCheckoutDialogBinding;
import com.eot_app.home_screens.MainActivity;
import com.eot_app.nav_menu.jobs.job_detail.documents.doc_model.GetFileList_Res;
import com.eot_app.nav_menu.jobs.job_detail.documents.fileattach_mvp.Doc_Attch_Pc;
import com.eot_app.nav_menu.jobs.job_detail.documents.fileattach_mvp.Doc_Attch_Pi;
import com.eot_app.nav_menu.jobs.job_detail.documents.fileattach_mvp.Doc_Attch_View;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

public class DialogCheckInUploadDocuments extends DialogFragment implements View.OnClickListener, Doc_Attch_View {

    Doc_Attch_Pi doc_attch_pi;
    OnDocumentUpdate onDocumentUpdate;
    private String imgPath;
    boolean isFileImage;
    String fileName;
    String buttonText;
    private MainActivity activity;
    private boolean isDesPermission = false;
    private boolean isShowDate = false;
    boolean isAttachmentPermission = false;
    private CheckinCheckoutDialogBinding binding;
    private int year, month, day, mHour, mMinute;
    Date lastCheckInDate = null;
    String lastCheckInTime = "";

    public void setIsFileImage(boolean b) {
        this.isFileImage = b;
    }

    public void setButtonText(String text) {
        this.buttonText = text;
    }

    public void setContext(MainActivity activity) {
        this.activity = activity;
    }

    public void setPermissions(boolean isDesPermission, boolean isAttachmentPermission, boolean isShowDate) {
        this.isDesPermission = isDesPermission;
        this.isAttachmentPermission = isAttachmentPermission;
        this.isShowDate = isShowDate;
    }


    public void setOnDocumentUpdate(OnDocumentUpdate onDocumentUpdate) {
        this.onDocumentUpdate = onDocumentUpdate;
    }

    public void setDocumentImage(OnDocumentUpdate onDocumentUpdate) {
        this.onDocumentUpdate = onDocumentUpdate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
    }


    public void setImgPath(String imgPath, String fileName) {
        this.imgPath = imgPath;
        this.fileName = fileName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.checkin_checkout_dialog, container, false);
        View view = binding.getRoot();
        initViews();
        return view;
    }

    private void initViews() {

        // permission checks for showing description and attachment options
        if (isDesPermission) {
            binding.llDes.setVisibility(View.VISIBLE);
        } else {
            binding.llDes.setVisibility(View.GONE);
        }

        if (isAttachmentPermission) {
            binding.rlAttachmentHeader.setVisibility(View.VISIBLE);
        } else {
            binding.rlAttachmentHeader.setVisibility(View.GONE);
        }

        if (isShowDate) {
            binding.llDate.setVisibility(View.VISIBLE);
            setLastCheckInDate();
        } else {
            binding.llDate.setVisibility(View.GONE);
        }

        binding.buttonSubmit.setText(buttonText);
        binding.buttonSubmit.setOnClickListener(this);
        binding.ivEdit.setOnClickListener(this);
        binding.ivDelete.setOnClickListener(this);
        binding.ivAttachment.setOnClickListener(this);
        binding.tvDocName.setOnClickListener(this);
        binding.docImg.setOnClickListener(this);
        binding.tvLabelDate.setOnClickListener(this);
        binding.tvTime.setOnClickListener(this);

//        binding.buttonSubmit.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.update_btn));
        binding.tvLabelDes.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.notes));
        binding.tvLabelAttachment.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_attachment));

    }

    public void setLastCheckInDate() {
        String format=AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm");

        lastCheckInTime = AppUtility.getDateWithFormate(Long.parseLong(App_preference.getSharedprefInstance().getLoginRes().getLastCheckIn()), format);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat(format);//, Locale.US
        try {
            lastCheckInDate = simpleDateFormat.parse(lastCheckInTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str = new SpannableString(LanguageController.getInstance().getMobileMsgByKey(AppConstant.the_last_checkin));
        builder.append(str);

        SpannableString str1 = new SpannableString(lastCheckInTime);
        str1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(str1);

        SpannableString str2 = new SpannableString(LanguageController.getInstance().getMobileMsgByKey(AppConstant.do_you_want_to));
        builder.append(str2);

        binding.tvDateTimeLabel.setText(builder);
        binding.tvLabelDate.setText(AppUtility.getCurrentDateByFormat("dd-MM-yyyy"));
        binding.tvTime.setText(AppUtility.getCurrentDateByFormat(AppUtility.dateTimeByAmPmFormate("hh:mm a", "kk:mm")));
    }


    public void setData() {

        if (fileName != null) {
            binding.frameDoc.setVisibility(View.VISIBLE);
            binding.ivDelete.setVisibility(View.VISIBLE);
            binding.ivEdit.setVisibility(View.VISIBLE);
            binding.ivAttachment.setVisibility(View.GONE);
            binding.tvDocName.setText(fileName);
        } else {
            binding.frameDoc.setVisibility(View.GONE);
            binding.ivDelete.setVisibility(View.GONE);
            binding.ivEdit.setVisibility(View.GONE);
            binding.ivAttachment.setVisibility(View.VISIBLE);
        }

        if (isFileImage && imgPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            binding.docImg.setImageBitmap(bitmap);
        } else {
            setImageIcon();
        }
        doc_attch_pi = new Doc_Attch_Pc(this);

    }


    private void setImageIcon() {
        if (imgPath != null) {
            try {
                int fileIcons = getFileIcons(imgPath);
                if (fileIcons > 0) {
                    binding.docImg.setImageResource(fileIcons);
                    binding.docImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_submit:
                if(isShowDate){
                    String date = binding.tvLabelDate.getText().toString() + " " + binding.tvTime.getText().toString();
                    // to check the current user time format
                    String format=AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm");
                    if (!AppUtility.conditionCheck(date, lastCheckInTime, format)
                    ) {
                        EotApp.getAppinstance().
                                showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.checkout_time_less_checkin_time));
                        return;
                    }
                    if (AppUtility.conditionCheck(date, AppUtility.getCurrentDateByFormat(format), format)
                    ) {
                        EotApp.getAppinstance().
                                showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.checkout_time_greater_current_time));
                        return;
                    }
                    uploadDocuments(AppUtility.changeDateFormat(date,format,AppConstant.DATE_TIME_FORMAT_new));
                }
                else {
                    uploadDocuments("");
                }
                break;
            case R.id.iv_attachment:
            case R.id.iv_edit:
                activity.selectFile(true);
                break;
            case R.id.iv_delete:
                setImgPath(null, null);
                setData();
                break;

            case R.id.tv_label_date:
                selectDate();
                break;
            case R.id.tv_time:
                selectStartTime();
                break;

        }
    }

    //get start date
    private void selectDate() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        showDialogPicker();
    }

    private void showDialogPicker() {
        DatePickerDialog datePickerDialogSelectDate = new DatePickerDialog(activity, AppUtility.InputDateSet(activity, dateTime -> binding.tvLabelDate.setText(dateTime), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_date)),
                year, month, day);
        Calendar minDate = Calendar.getInstance();
        minDate.setTime(lastCheckInDate);
        datePickerDialogSelectDate.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialogSelectDate.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialogSelectDate.show();
    }

    //schedule start time
    private void selectStartTime() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        showDialogTimePicker();
    }

    private void showDialogTimePicker() {
//        Calendar minDate = Calendar.getInstance();
//        minDate.setTime(lastCheckInDate);
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, AppUtility.InputTimeSet(activity, dateTime -> {
            DecimalFormat formatter = new DecimalFormat("00");
            String[] aa = dateTime.split(":");
            System.out.println("strings::" + new Gson().toJson(aa));
            binding.tvTime.setText((formatter.format(Integer.parseInt(aa[0]))) + ":" + aa[1]);
        }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_time))
                , mHour, mMinute, false);

        timePickerDialog.show();
    }


    private void uploadDocuments(String checkoutTime) {
        AppUtility.hideSoftKeyboard(getActivity());
        String updateDesc = "";

        if (!TextUtils.isEmpty(binding.etDocDesc.getText().toString()))
            updateDesc = binding.etDocDesc.getText().toString();
        AppUtility.progressBarShow(getActivity());

        activity.mainActivity_pi.addCheckInOutIime(updateDesc, imgPath, fileName, checkoutTime);
        dismiss();
    }

    private int getFileIcons(String serverFilePath) {
        int resId = 0;
        String ext = serverFilePath.substring((serverFilePath.lastIndexOf(".")) + 1);

        if (!ext.isEmpty()) {
            switch (ext) {
                case "doc":
                case "docx":
                    resId = R.drawable.word;
                    break;
                case "pdf":
                    resId = R.drawable.pdf;
                    break;
                case "xlsx":
                case "xls":
                    resId = R.drawable.excel;

                    break;
                case "csv":
                    resId = R.drawable.csv;
                    break;
                default:
                    resId = R.drawable.doc;
                    break;
            }
        }
        return resId;
    }

    @Override
    public void onDocumentUpdate(String msg, boolean isSuccess) {
        if (isSuccess && onDocumentUpdate != null) {
            onDocumentUpdate.onUpdateDes(binding.etDocDesc.getText().toString());
            dismiss();
        } else if (msg != null)
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void selectFile() {

    }

    @Override
    public void setList(ArrayList<GetFileList_Res> getFileList_res, String isAttch) {

    }

    @Override
    public void addNewItemToAttachmentList(ArrayList<GetFileList_Res> getFileList_res, String isAttachCompletionNotes) {

    }

    @Override
    public void addView() {

    }

    @Override
    public void onSessionExpire(String msg) {
        AppUtility.alertDialog(getActivity(), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_error_title), msg, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "",
                () -> {
                    EotApp.getAppinstance().sessionExpired();
                    return null;
                });
    }

    @Override
    public void fileExtensionNotSupport(String msg) {
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog) {
        try {
            if (binding.etDocDesc.isFocused()) {
                InputMethodManager imm = (InputMethodManager) binding.etDocDesc.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDismiss(dialog);
    }

    public interface OnDocumentUpdate {
        void onUpdateDes(String url);
    }
}
