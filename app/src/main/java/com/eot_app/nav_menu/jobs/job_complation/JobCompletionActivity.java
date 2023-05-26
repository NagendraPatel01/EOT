package com.eot_app.nav_menu.jobs.job_complation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.eot_app.R;
import com.eot_app.eoteditor.Utils;
import com.eot_app.nav_menu.jobs.job_complation.complat_mvp.Compl_PC;
import com.eot_app.nav_menu.jobs.job_complation.complat_mvp.Compl_PI;
import com.eot_app.nav_menu.jobs.job_complation.complat_mvp.Compla_View;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_db.JtId;
import com.eot_app.nav_menu.jobs.job_detail.documents.ActivityDocumentSaveUpload;
import com.eot_app.nav_menu.jobs.job_detail.documents.EditImageDialog;
import com.eot_app.nav_menu.jobs.job_detail.documents.PathUtils;
import com.eot_app.nav_menu.jobs.job_detail.documents.doc_model.GetFileList_Res;
import com.eot_app.nav_menu.jobs.job_detail.documents.fileattach_mvp.Doc_Attch_Pc;
import com.eot_app.nav_menu.jobs.job_detail.documents.fileattach_mvp.Doc_Attch_Pi;
import com.eot_app.nav_menu.jobs.job_detail.documents.fileattach_mvp.Doc_Attch_View;
import com.eot_app.utility.AppCenterLogs;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.settings.setting_db.JobTitle;
import com.eot_app.utility.settings.setting_db.Suggestion;
import com.eot_app.utility.util_interfaces.Callback_AlertDialog;
import com.eot_app.utility.util_interfaces.JoBServSuggAdpter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.microsoft.appcenter.AppCenter;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class JobCompletionActivity extends AppCompatActivity implements View.OnClickListener
        , TextWatcher, Compla_View, Doc_Attch_View, JobCompletionAdpter.FileDesc_Item_Selected {
    private final static int CAPTURE_IMAGE_GALLARY = 222;
    private static final int DOUCMENT_UPLOAD_CODE = 156;
    final String JOBCOMPLATIONTYPE = "";
    private final int CAMERA_CODE = 100;
    private final int ATTACHFILE_CODE = 101;
    private final StringBuffer desc = new StringBuffer();
    private final List<Suggestion> suggestionList = new ArrayList<>();
    Job jobData;
    JoBServSuggAdpter suggestionAdapter;
    String captureImagePath;
    boolean isFileImage = false;
    RecyclerView.LayoutManager layoutManager;
    Doc_Attch_Pi doc_attch_pi;
    String[] suggestionsArray = new String[suggestionList.size()];
    TextView cancel_txt, complHeader, save_txt, tv_label_des;
    ImageView suggestion_img;
    TextView tvCancel, tvDone, tvFetchDes,tvtimestemp;
    private EditText compedt;
    private Compl_PI complPi;
    private RecyclerView recyclerView;
    private JobCompletionAdpter jobCompletionAdpter;
    private String jobId;
    private EditImageDialog currentDialog = null;
    private ArrayList<GetFileList_Res> fileList_res = new ArrayList<>();
    private ProgressBar progressBar;
    private Spinner job_suggestion_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_completion);
        getSupportActionBar().hide();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (getIntent().hasExtra("Complation")) {
                String jobDataStr = bundle.getString("Complation");
                jobData = new Gson().fromJson(jobDataStr, Job.class);
                jobId = jobData.getJobId();
            }
        }
        initializeMyViews();
    }

    private void initializeAdapter() {
        ArrayList<GetFileList_Res> getFileList_res = new ArrayList<>();
        jobCompletionAdpter = new JobCompletionAdpter(this, getFileList_res, this, jobId
                , jaId -> {
            Log.e("", "");
            if (complPi != null) {
                showDialogForRemoveAttch(jaId);
            }
        });
        recyclerView.setAdapter(jobCompletionAdpter);
    }

    private void showDialogForRemoveAttch(final String jaId) {
        AppUtility.alertDialog2(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.are_you_sure),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.doc_remove),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.cancel), new Callback_AlertDialog() {
                    @Override
                    public void onPossitiveCall() {
                        try {
                            complPi.removeUploadAttchment(jaId);
                        } catch (Exception ex) {
                            AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","showDialogForRemoveAttch()"+ex.getMessage(),"JobCompletionActivity","");
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onNegativeCall() {

                    }
                });
    }

    @Override
    public void uploadDocDelete(String msg) {
        doc_attch_pi.getAttachFileList(jobId, App_preference.getSharedprefInstance().getLoginRes().getUsrId(), "6");
    }


    @Override
    public void sessionexpire(String msg) {
        AppUtility.alertDialog(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_error_title),
                msg, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> {
                    EotApp.getAppinstance().sessionExpired();
                    return null;
                });
    }

    private void initializeMyViews() {
        tv_label_des = findViewById(R.id.tv_label_des);
        tv_label_des.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.completion_note));
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 2);
//        CustomLinearLayoutManager customLayoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL
//                , false);

        recyclerView.setLayoutManager(layoutManager);
        initializeAdapter();
        doc_attch_pi = new Doc_Attch_Pc(this);
        doc_attch_pi.getAttachFileList(jobId, App_preference.getSharedprefInstance().getLoginRes().getUsrId(), "6");

        cancel_txt = findViewById(R.id.cancel_txt);
        complHeader = findViewById(R.id.complHeader);
        save_txt = findViewById(R.id.save_txt);

        cancel_txt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.cancel));
        complHeader.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.completion_note));

        //    compleLayout = findViewById(R.id.compleLayout);
        compedt = findViewById(R.id.compedt);
        compedt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.completion_note));

        tvCancel = findViewById(R.id.tvCancel);
        tvCancel.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.cancel));

        tvDone = findViewById(R.id.tvDone);
        tvDone.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.done));

        tvFetchDes = findViewById(R.id.tvFetchDes);
        tvFetchDes.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.fetch_des));

        tvtimestemp=findViewById(R.id.tvtimestemp);
        tvtimestemp.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.timestemp));

        tvCancel.setOnClickListener(this);
        tvDone.setOnClickListener(this);
        tvFetchDes.setOnClickListener(this);
        tvtimestemp.setOnClickListener(this);
        //    compleLayout.getEditText().addTextChangedListener(this);

        suggestion_img = findViewById(R.id.suggestion_img);
        job_suggestion_spinner = findViewById(R.id.job_suggestion_spinner);
        suggestion_img.setOnClickListener(v -> {
            if (suggestionList != null && suggestionList.size() > 0)
                job_suggestion_spinner.performClick();
            else {
                AppUtility.alertDialog(JobCompletionActivity.this,
                        "", LanguageController.getInstance()
                                .getMobileMsgByKey(AppConstant.no_suggesstion)
                        , LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok),"", () -> null);
            }

        });
        viewClickListner();

        setComplationView();

        complPi = new Compl_PC(this);

        filterJobServices();

        if (AppUtility.isInternetConnected()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void filterJobServices() {
        try {
            if (jobData != null && jobData.getJtId() != null && jobData.getJtId().size() > 0) {
                if (suggestionList != null && suggestionList.size() > 0) {
                    suggestionList.clear();
                }

                for (JtId jtId : jobData.getJtId()) {
                    JobTitle jobTitle1 = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobTitleModel().getJobTitleByid(jtId.getJtId());
                    if (jobTitle1 != null)
                        suggestionList.addAll(jobTitle1.getSuggestionList());
                }

                List<String> tempList = new ArrayList<>();
//                suggestionsArray = new String[suggestionList.size()];
                for (int i = 0; i < Objects.requireNonNull(suggestionList).size(); i++) {
                    try {
                        if (suggestionList.get(i).getComplNoteSugg() != null &&
                                suggestionList.get(i).getComplNoteSugg().length() > 0) {
                            tempList.add(suggestionList.get(i).getComplNoteSugg());
                            //     suggestionsArray[i] = suggestionList.get(i).getComplNoteSugg();
                        }
                    } catch (Exception e) {
                        AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","filterJobServices()"+e.getMessage(),"JobCompletionActivity","");
                        e.printStackTrace();
                    }
                }
                try {
                    suggestionsArray = new String[tempList.size()];
                    for (int i = 0; i < tempList.size(); i++) {
                        suggestionsArray[i] = tempList.get(i);
                    }
                } catch (Exception e) {
                    AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","filterJobServices()"+e.getMessage(),"JobCompletionActivity","");
                    e.printStackTrace();
                }
                try {
                    if (suggestionAdapter == null) {
                        AppUtility.spinnerPopUpWindow(job_suggestion_spinner);
                        suggestionAdapter = new JoBServSuggAdpter(JobCompletionActivity.this, suggestionsArray
                                , nm -> setSelectedSuggeston(nm));
                        job_suggestion_spinner.setAdapter(suggestionAdapter);
                    }
                } catch (Exception e) {
                    AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","filterJobServices()"+e.getMessage(),"JobCompletionActivity","");
                    e.printStackTrace();
                }
            }
        } catch (Exception exception) {
            AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","filterJobServices()"+exception.getMessage(),"JobCompletionActivity","");
            exception.printStackTrace();
        }
    }


    private void setSelectedSuggeston(String nm) {
        try {
            String str = "";
            int cursorpostion=compedt.getText().toString().length();
            if (compedt.getText().toString().trim().length() > 0) {
                str = compedt.getText().toString() + "\n" + nm;
            } else {
                str = nm;
            }
            compedt.setText(str);
            str.replace("<br>","");
            str.replace("null","");
            compedt.setText(str);
            compedt.setSelection(cursorpostion+nm.length()+1);
        } catch (Exception e) {
            AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","setSelectedSuggeston()"+e.getMessage(),"JobCompletionActivity","");
            e.printStackTrace();
        }
    }

    private void setComplationView() {
        if (TextUtils.isEmpty(jobData.getComplNote())) {
            save_txt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.save_btn));
        } else {
            save_txt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.update));
//            compedt.setText(jobData.getComplNote().replace("null", ""));
  //          compedt.setText(AppUtility.html2text(jobData.getComplNote().replace("null", "")));// Log.d("notes",jobData.getComplNote());
              String notes=jobData.getComplNote().replace("null","");
              notes.replace("<br>","");
            compedt.setText(notes);
        }
    }

    private void viewClickListner() {
        cancel_txt.setOnClickListener(this);
        save_txt.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_txt:
                AppUtility.hideSoftKeyboard(this);
                this.finish();
                break;
            case R.id.save_txt:
                AppUtility.hideSoftKeyboard(this);
                complPi.addEditJobComplation(jobData.getJobId(), compedt.getText().toString().trim());
                break;
            case R.id.tvDone:
                setUpdatedDesc(1);
                break;
            case R.id.tvCancel:
                setUpdatedDesc(2);
                break;
            case R.id.tvFetchDes:
                setUpdatedDesc(3);
                break;
            case R.id.tvtimestemp:
                setUpdatedDesc(4);
                break;
        }
    }


    @Override
    public void selectFile() {
        if (!Utils.isOnline(this)) {
            AppUtility.alertDialog(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_error_title), LanguageController.getInstance().getMobileMsgByKey(AppConstant.feature_not_available), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
        } else {
            final BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(R.layout.bottom_image_chooser);
            TextView camera = dialog.findViewById(R.id.camera);
            assert camera != null;
            camera.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.camera));
            TextView gallery = dialog.findViewById(R.id.gallery);
            assert gallery != null;
            gallery.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.gallery));
            TextView drive_document = dialog.findViewById(R.id.drive_document);
            assert drive_document != null;
            drive_document.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant              .document));
            camera.setOnClickListener(view -> {
                if (AppUtility.askCameraTakePicture(JobCompletionActivity.this)) {
                    takePictureFromCamera();
                } else {
                    askTedPermission(0, AppConstant.cameraPermissions);
                }
                dialog.dismiss();
            });

            gallery.setOnClickListener(view -> {
                if (AppUtility.askGalaryTakeImagePermiSsion(JobCompletionActivity.this)) {
                    getImageFromGallray();
                } else {
                    askTedPermission(1, AppConstant.galleryPermissions);
                }
                dialog.dismiss();
            });

            drive_document.setOnClickListener(view -> {
                if (AppUtility.askGalaryTakeImagePermiSsion(JobCompletionActivity.this)) {
                    takeimageFromGalary();//only for drive documents
                } else {
                    askTedPermission(2, AppConstant.galleryPermissions);
                }
                dialog.dismiss();
            });
            dialog.show();
        }
    }

    private void askTedPermission(int type, String[] permissions) {
        TedPermission.with(EotApp.getAppinstance())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (type == 0)
                            takePictureFromCamera();
                        else if (type == 1)
                            getImageFromGallray();
                        else if (type == 2)
                            takeimageFromGalary();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [SettingActivity] > [Permission]")
                .setPermissions(permissions)
                .check();
    }

    @Override
    public void OnItemClick_Document(GetFileList_Res getFileList_res) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(App_preference.getSharedprefInstance().getBaseURL() + "" + getFileList_res.getAttachFileName())));
    }

    @Override
    public void openAttachmentDialog() {
        selectFile();
    }

    @Override
    public void setList(ArrayList<GetFileList_Res> getFileList_res, String isAttachCommpletionNotes) {
        progressBar.setVisibility(View.GONE);
        if (currentDialog != null) {
            currentDialog.dismiss();
            currentDialog = null;
        }
        if (getFileList_res.size() == 1)
            this.fileList_res.addAll(getFileList_res);
        else
            this.fileList_res = getFileList_res;
        if (jobCompletionAdpter != null)
            (jobCompletionAdpter).updateFileList(fileList_res);
    }

    @Override
    public void addNewItemToAttachmentList(ArrayList<GetFileList_Res> getFileList_res, String            isAttachCompletionNotes) {
        if (isFileImage) {
            String bitmapString = "";
            // remove the temporary added item for showing loader
            if (fileList_res != null && !fileList_res.isEmpty()) {
                int position = -1;
                for (int i = 0; i < fileList_res.size(); i++) {
                    if (fileList_res.get(i).getAttachmentId().equalsIgnoreCase("0")) {
                        position = i;
                        break;
                    }
                }
                if (position != -1) {
                    bitmapString = fileList_res.get(position).getBitmap();
                    fileList_res.remove(fileList_res.get(position));
                }
            }
            // to add the new entry into existing list
            if (getFileList_res != null) {
                assert fileList_res != null;
                getFileList_res.addAll(fileList_res);
                getFileList_res.get(0).setBitmap(bitmapString);
                setList(getFileList_res, isAttachCompletionNotes);
            }
        }
    }

    @Override
    public void addView() {
        // recyclerView.setVisibility(View.VISIBLE);
        //  nolist_linear.setVisibility(View.GONE);
    }

    @Override
    public void onSessionExpire(String msg) {
        AppUtility.alertDialog(this, LanguageController.getInstance().getMobileMsgByKey              (AppConstant.dialog_error_title), msg, LanguageController.getInstance().getMobileMsgByKey            (AppConstant.ok), "",
                () -> {
                    EotApp.getAppinstance().sessionExpired();
                    return null;
                });
    }

    @Override
    public void fileExtensionNotSupport(String msg) {
    }

    @Override
    public void onDocumentUpdate(String msg, boolean isSuccess) {
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
    /**
     * get image from camera & edit & croping functinallity
     */
    private void takePictureFromCamera() {
        if (!AppUtility.askCameraTakePicture(JobCompletionActivity.this)) {
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Eot Directory");

        if (!path.exists()) {
            path.mkdir();
        }

        File imageFile = null;
        try {
            imageFile = createImageFile();
        } catch (IOException e) {
            AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","takePictureFromCamera()"+e.getMessage(),"JobCompletionActivity","");
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", imageFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivityForResult(takePictureIntent, CAMERA_CODE); // IMAGE_CAPTURE = 0
    }

    private void getImageFromGallray() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, CAPTURE_IMAGE_GALLARY);
    }

    private void takeimageFromGalary() {
        //allow upload file extension
        String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png",
                "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",//.doc & .docx
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",//.xls & .xlsx
                "application/pdf",//pdf
                "text/csv", "text/plain"//csv
        };

        /* *only for document uploading */
        Intent documentIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        documentIntent.addCategory(Intent.CATEGORY_OPENABLE);
        documentIntent.setType("*/*");
        documentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(documentIntent, ATTACHFILE_CODE);
    }

    private File createImageFile() throws IOException {
        Calendar calendar = Calendar.getInstance();
        long imageFileName = calendar.getTime().getTime();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment            .DIRECTORY_PICTURES), "Eot Directory");

        File directoryPath = new File(storageDir.getPath());
        File image = File.createTempFile(
                String.valueOf(imageFileName),  /* prefix */
                ".jpg",         /* suffix */
                directoryPath   /* directory */
        );
        captureImagePath = image.getAbsolutePath();
        return new File(image.getPath());
    }


    @Override
    public void updateDetailJob(String note) {
        Intent intent = new Intent();
        if (jobData != null) jobData.setComplNote(note);
        intent.putExtra("note", note);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DOUCMENT_UPLOAD_CODE:
                if (resultCode == RESULT_OK)
                    if (doc_attch_pi != null){
                        String fileNameExt = AppUtility.getFileNameWithExtension(data.getStringExtra("imgPath"));
                        String bitmapString = "";
                        if (data.getBooleanExtra("isFileImage", false)) {
                            Bitmap bitmap = AppUtility.getBitmapFromPath(data.getStringExtra("imgPath"));
                            bitmapString = AppUtility.BitMapToString(bitmap);
                        }
                        GetFileList_Res obj = new GetFileList_Res("0", fileNameExt, fileNameExt, bitmapString);
                        ArrayList<GetFileList_Res> updateList = new ArrayList<>();
                        updateList.add(obj);
                        if (fileList_res != null) {
                            updateList.addAll(fileList_res);
                        }
                        String isAttach = data.getStringExtra("isAttach");
                        isFileImage = data.getBooleanExtra("isFileImage", false);
                        if (isAttach.equals("1") && isFileImage) {
                            desc.append(compedt.getText().toString().trim());
                            compedt.getText().clear();
                            if (!TextUtils.isEmpty(desc))
                                desc.append(System.lineSeparator());

                            if (updateList.get(0).getDes() != null)
                                desc.append(updateList.get(0).getDes());
                            compedt.setText(desc);
                            String tempnotes=desc.toString().replace("null","");
                            compedt.setText(tempnotes);
                            compedt.setSelection(compedt.getText().toString().length());
                            desc.setLength(0);
                            setList(updateList, "");
                        }
                        if (data.getStringExtra("fileName") != null) {
                            try {
                                doc_attch_pi.uploadDocuments(jobId, data.getStringExtra("imgPath"),
                                        data.getStringExtra("fileName"),
                                        data.getStringExtra("desc"),
                                        data.getStringExtra("type"),
                                        data.getStringExtra("isAttach"));
                            } catch (Exception e) {
                                AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","onActivityResult()"+e.getMessage(),"JobCompletionActivity","");
                                if (updateList.size() == 1) {
                                    fileList_res.remove(updateList.get(0));
                                    setList(fileList_res, "");
                                }
                                e.printStackTrace();
                            }
                        }
                    }
                break;
            case CAMERA_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        File file = AppUtility.scaleToActualAspectRatio(captureImagePath, 1024f, 1024f);
                        if (file != null) {
                            imageEditing(Uri.fromFile(file), true);
                        }
                    } catch (Exception e) {
                        AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","onActivityResult()-->CAMERA_CODE"+e.getMessage(),"JobCompletionActivity","");
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
                break;
            case CAPTURE_IMAGE_GALLARY:
            case ATTACHFILE_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri galreyImguriUri = data.getData();
                        String gallery_image_Path = PathUtils.getRealPath(this, galreyImguriUri);
                        String img_extension = gallery_image_Path.substring(gallery_image_Path.lastIndexOf("."));
                        //('jpg','png','jpeg','pdf','doc','docx','xlsx','csv','xls'); supporting extensions
                        if (img_extension.equals(".jpg") || img_extension.equals(".png") || img_extension.equals(".jpeg")) {
                            imageEditing(data.getData(), true);
                        } else {
                            uploadFileDialog(gallery_image_Path);
                        }
                    } catch (Exception e) {
                        AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","onActivityResult()-->ATTACHFILE_CODE"+e.getMessage(),"JobCompletionActivity","");
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //upload image edit highlighting feature for image
    private void imageEditing(Uri uri, boolean isImage) {
        try {
            Intent intent = new Intent(this, ActivityDocumentSaveUpload.class);
            intent.putExtra("uri", uri);
            intent.putExtra("isImage", true);
            intent.putExtra("jobid", jobId);
            intent.putExtra("SAVEASCOMPLETION", true);
            startActivityForResult(intent, DOUCMENT_UPLOAD_CODE);
        } catch (Exception e) {
            AppCenterLogs.addLogToAppCenterOnAPIFail("JobCompletion","","imageEditing()"+e.getMessage(),"JobCompletionActivity","");
            e.printStackTrace();
        }
    }

    //upload file dialog
    private void uploadFileDialog(final String selectedFilePath) {
        Intent intent = new Intent(this, ActivityDocumentSaveUpload.class);
        intent.putExtra("uri", selectedFilePath);
        intent.putExtra("isImage", false);
        intent.putExtra("jobid", jobId);
        startActivityForResult(intent, DOUCMENT_UPLOAD_CODE);
    }

    public void setUpdatedDesc(String desc) {
        StringBuffer data = new StringBuffer(compedt.getText().toString().trim());
        if (!TextUtils.isEmpty(desc))
            data.append(System.lineSeparator());
        data.append(desc);
//        compedt.setText(data);
        compedt.setText(AppUtility.html2text(data.toString()));
        data.setLength(0);
    }

    public void setUpdatedDesc(int type) {
        String s;
        int cursorpostion=compedt.getSelectionEnd();
        CharSequence enteredText = compedt.getText().toString();
        CharSequence cursorToStart=  enteredText.subSequence(0,cursorpostion);
        CharSequence cursorToEnd = enteredText.subSequence(cursorpostion, enteredText.length());
        StringBuffer stringBuffer=new StringBuffer(cursorToStart);
        if (type == 1)
            stringBuffer.append(" "+"\u2705");
        else if(type==2)
            stringBuffer.append(" "+"\u274c");
        else if (type == 3
             &&  jobData.getDes() != null
             && !jobData.getDes().isEmpty()){
             s=AppUtility.html2text(jobData.getDes());
             stringBuffer.append(" " + s);
        } else if (type==4)
            stringBuffer.append(" "+AppUtility.getCurrentDateByFormats("dd-MM-yyyy hh:mm a"));

            StringBuffer finalstring = stringBuffer.append(cursorToEnd);
            compedt.setText(finalstring);

        //setting cursor possition
        if (type==1||type==2) {
            compedt.setSelection(cursorpostion+2);
        } else if(type==3){
             s=AppUtility.html2text(jobData.getDes());
             if(s.isEmpty())
                  compedt.setSelection(cursorpostion+s.length());
             else
             compedt.setSelection(cursorpostion+s.length()+1);
        }else if (type==4)
        {
            String time=AppUtility.getCurrentDateByFormats("dd/MM/yyyy-hh:mm a");
            compedt.setSelection(cursorpostion+time.length()+1);
        }
        stringBuffer.setLength(0);
    }
}
