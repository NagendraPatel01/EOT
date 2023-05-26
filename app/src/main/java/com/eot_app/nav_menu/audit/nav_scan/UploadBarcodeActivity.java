package com.eot_app.nav_menu.audit.nav_scan;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.eot_app.R;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.google.zxing.BarcodeFormat;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Mahendra Dabi on 8/11/19.
 */
public class UploadBarcodeActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    EditText edit_barcode;
    private String codeText;
    private String equipmentId;
    private UploadBarcodeViewModel uploadBarcodeViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_barcode);
        uploadBarcodeViewModel = new ViewModelProvider(this).get(UploadBarcodeViewModel.class);
        hideActionBar(true);

        if (getIntent() != null && getIntent().hasExtra("equipmentId")) {
            equipmentId = getIntent().getStringExtra("equipmentId");
        }

        CodeScannerView scannerView = findViewById(R.id.scanner_view);

        edit_barcode = findViewById(R.id.edit_barcode);

        mCodeScanner = new CodeScanner(this, scannerView);
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.CODE_128);
        list.add(BarcodeFormat.UPC_A);
        list.add(BarcodeFormat.UPC_E);
        list.add(BarcodeFormat.EAN_13);
        list.add(BarcodeFormat.CODE_39);
        list.add(BarcodeFormat.CODABAR);
        mCodeScanner.setFormats(list);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            codeText = result.getText();
            showDialog(codeText);
            // searchEquipment(result.getText());

        }));


        scannerView.setOnClickListener(view -> startScanner());

        uploadBarcodeViewModel.getIsUploadingBarcode().observe(this, aBoolean -> {
            if (aBoolean)
                AppUtility.progressBarShow(UploadBarcodeActivity.this);
            else AppUtility.progressBarDissMiss();
        });

        uploadBarcodeViewModel.getUploadMessage().observe(this, s -> {
            if (!TextUtils.isEmpty(s)) {
                Toast.makeText(UploadBarcodeActivity.this, LanguageController.getInstance().getServerMsgByKey(s), Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        uploadBarcodeViewModel.getUploadErrorMessage().observe(this, s -> {
            if (!TextUtils.isEmpty(s)) {
                AppUtility.error_Alert_Dialog(UploadBarcodeActivity.this,
                        LanguageController.getInstance().getServerMsgByKey(s),
                        LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), null);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        startScanner();

    }


    private void showDialog(final String msg) {
        AppUtility.alertDialog(this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.equipment_btn), msg, LanguageController.getInstance().getMobileMsgByKey(AppConstant.expense_upload), AppConstant.cancel, new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (uploadBarcodeViewModel != null)
                    if (!TextUtils.isEmpty(equipmentId) && !TextUtils.isEmpty(codeText)) {
                        if (AppUtility.isInternetConnected())
                            uploadBarcodeViewModel.uploadBarcode(equipmentId, codeText);
                        else {
                            AppUtility.error_Alert_Dialog(UploadBarcodeActivity.this,
                                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network),
                                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), null);
                        }
                    }
                return null;
            }
        });
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void startScanner() {
        if (AppUtility.askCameraTakePicture(this)) {
            mCodeScanner.startPreview();
        }
        else {
            askTedPermission(0,AppConstant.cameraPermissions);
        }
    }
    private void askTedPermission(int type,String[] permissions) {
        TedPermission.with(EotApp.getAppinstance())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (type == 0)
                            mCodeScanner.startPreview();

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [SettingActivity] > [Permission]")
                .setPermissions(permissions)
                .check();
    }
    /*hide show actionbar*/
    private void hideActionBar(boolean hide) {
        Window window = getWindow();
        if (hide) {
            getSupportActionBar().hide();
            window.setStatusBarColor(Color.BLACK);
        } else {
            getSupportActionBar().show();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }


}
