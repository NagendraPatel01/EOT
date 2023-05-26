package com.eot_app.utility.settings.firstSync;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.eot_app.R;
import com.eot_app.home_screens.MainActivity;
import com.eot_app.login_next.Login2Activity;
import com.eot_app.login_next.login_next_model.ResLoginData;
import com.eot_app.nav_menu.jobs.job_controller.ChatController;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.language_support.Language_Preference;
import com.eot_app.utility.util_interfaces.Callback_AlertDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.concurrent.Callable;


public class FirstSyncActivity extends AppCompatActivity implements FirstSyncView {
    FirstSyncPi syncpi;
    ImageView sync_iv;
    ProgressBar syn_progress;
    TextView tv_sync_msg;


    BroadcastReceiver networkSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            if (ni != null && ni.isConnected()) {
                Log.d("MainActivity", " setOnDisconnectFirebase");
            } else {
                showRetryDialog("NetworkError");
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkSwitchStateReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_sync);

        tv_sync_msg = findViewById(R.id.textView);
        tv_sync_msg.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.sync_msg));

        syncpi = new FirstSyncPC(this);
        sync_iv = findViewById(R.id.sync_iv);
        syn_progress = findViewById(R.id.syn_progress);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.progress_anim);
        sync_iv.startAnimation(animation);
        syn_progress.setMax(13);

        syncpi.startSync();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        registerReceiver(networkSwitchStateReceiver, filter);

        //added by shivani
        //In contact and site added a new param is active to manage the enable and disable contact functionality ,
        // can be removed after two or three release , current version is 2.73
        // to update is active param as 1 by default in contact site when update occurs

        // very Important

        // first time it will be false for every user
        if(!App_preference.getSharedprefInstance().getContactSiteSynced()){
            // set this param as true so that contact and site will not get resynced for the same user again

            App_preference.getSharedprefInstance().setContactSiteSynced(true);

            App_preference.getSharedprefInstance().setContactSyncTime("");
            App_preference.getSharedprefInstance().setSiteSyncTime("");
        }

    }


    @Override
    public void goHomePage() {
        if (!App_preference.getSharedprefInstance().getLoginRes().getExpireStatus().equals("0")) {
            startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        } else {
            Intent intent = new Intent(FirstSyncActivity.this, Login2Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            App_preference.getSharedprefInstance().setBlankTokenOnSessionExpire();
            finish();
        }
    }

    @Override
    public void errorMsg(String msg) {
        showRetryDialog(msg);
    }

    @Override
    public void progressStatus(int status_no) {
        syn_progress.setProgress(status_no + 1);
    }

    @Override
    public void showRetryDialog(String msg) {
        try {
            final BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(R.layout.bottom_alert_dialog);
            TextView txt_lable1 = dialog.findViewById(R.id.lable1);
            TextView txt_lable2 = dialog.findViewById(R.id.lable2);
            TextView alert_msg = dialog.findViewById(R.id.alert_msg);
            assert txt_lable1 != null;
            txt_lable1.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.back_to_login));
            assert txt_lable2 != null;
            txt_lable2.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.continue_));

            assert alert_msg != null;
            if(msg!=null&&!msg.isEmpty()&&msg.equalsIgnoreCase("NetworkError")){
                alert_msg.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network));
            }
            else {
                alert_msg.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.error_retry));
            }

            txt_lable1.setOnClickListener(view -> {
                dialog.dismiss();
                startActivity(new Intent(FirstSyncActivity.this, Login2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            });

            txt_lable2.setOnClickListener(view -> {
                dialog.dismiss();
                retryCall();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryCall() {
        syncpi.startSync();
    }

    @Override
    public void setUI(int i) {

    }

    @Override
    public void upateForcefully() {

        AppUtility.alertDialog(this, "Update!", AppConstant.updateAppMsg, AppConstant.update, "", () -> {
            // to clear app cache when user updates app

            EotApp.getAppinstance().clearCache();
            ResLoginData data = App_preference.getSharedprefInstance().getLoginRes();
            data.setToken("");
            Language_Preference.getSharedprefInstance().setStaticMsgsModel("");

            App_preference.getSharedprefInstance().setJobStartSyncTime("");
            App_preference.getSharedprefInstance().setInventryItemSyncTime("");
            //added by shivani
            //In contact and site added a new param is active to manage the enable and disable contact functionality ,
            // can be removed after two or three release , current version is 2.73
            // to update is active param as 1 by default in contact site when update occurs

            App_preference.getSharedprefInstance().setContactSyncTime("");
            App_preference.getSharedprefInstance().setSiteSyncTime("");


            App_preference.getSharedprefInstance().setLoginResponse(new Gson().toJson(data));
            ChatController.getInstance().setAppUserOnline(0);
//                open login screen
            Intent intent = new Intent(FirstSyncActivity.this, Login2Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();

            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            return null;
        });
    }

    @Override
    public void updateNotForcefully() {
        AppUtility.alertDialog2(this, "Update!", AppConstant.updateAppMsg, AppConstant.update, AppConstant.cancel, new Callback_AlertDialog() {
            @Override
            public void onPossitiveCall() {


                EotApp.getAppinstance().clearCache();
                //added by shivani
                //In contact and site added a new param is active to manage the enable and disable contact functionality ,
                // can be removed after two or three release , current version is 2.72
               // to update is active param as 1 by default in contact site when update occurs
                App_preference.getSharedprefInstance().setContactSyncTime("");
                App_preference.getSharedprefInstance().setSiteSyncTime("");


                Intent intent = new Intent(FirstSyncActivity.this, Login2Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finishAffinity();

                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }

            @Override
            public void onNegativeCall() {
                syncpi.getJobStatusList();
            }
        });
    }


    @Override
    public void setSubscriptionExpire(String msg) {
        try {

            TextView dailog_title, dialog_msg;
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            LayoutInflater inflater = (this).getLayoutInflater();
            final View customLayout = inflater.inflate(R.layout.subscription_layout, null);
            alertDialog.setView(customLayout);
            alertDialog.setCancelable(false);

            dailog_title = customLayout.findViewById(R.id.dai_title);
            dialog_msg = customLayout.findViewById(R.id.dia_msg);
            dailog_title.setText(msg);
            dialog_msg.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.please_contact_admin));

            alertDialog.setPositiveButton(LanguageController.getInstance().getMobileMsgByKey(AppConstant.close),
                    (dialog, which) -> {
                        Intent intent = new Intent(FirstSyncActivity.this, Login2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        App_preference.getSharedprefInstance().setBlankTokenOnSessionExpire();
                        finish();
                    });

            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sessionExpiredFinishActivity() {
        finish();
    }

}
