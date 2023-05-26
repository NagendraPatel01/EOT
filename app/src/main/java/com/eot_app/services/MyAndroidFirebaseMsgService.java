package com.eot_app.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.eot_app.home_screens.MainActivity;
import com.eot_app.nav_menu.admin_fw_chat_pkg.sonam_user_user_chat_pkg.user_chat_controller.UserToUserChatController;
import com.eot_app.nav_menu.admin_fw_chat_pkg.sonam_user_user_chat_pkg.usertouser_model.NotModel;
import com.eot_app.nav_menu.client_chat_pkg.client_chat_model.ClientChatReqModel;
import com.eot_app.nav_menu.jobs.job_controller.ChatController;
import com.eot_app.nav_menu.jobs.job_detail.chat.fire_Base_Model.Chat_Send_Msg_Model;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.NotificationUtils;
import com.eot_app.utility.Noty_otherdata;
import com.eot_app.utility.db.AppDataBase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import java.util.Map;


public class MyAndroidFirebaseMsgService extends FirebaseMessagingService {
    String notificationMessage;
    NotificationUtils nNotificationUtils;
    String TAG = MyAndroidFirebaseMsgService.class.getSimpleName();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("Notificationdata", "" + remoteMessage.getData());
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this Appc may be: https://goo.gl/39bRNJLog.d(TAG, "From: " + remoteMessage.getFrom());
        //https://pushtry.com/
        Map data = remoteMessage.getData();
        notificationMessage = (String) data.get("message");
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            HyperLog.i(TAG, "MyAndroidFirebaseMsgService--" + new Gson().fromJson(data.get("otherdata").toString(), Noty_otherdata.class));


            Gson gson = new Gson();
            Noty_otherdata otherdata = gson.fromJson(data.get("otherdata").toString(), Noty_otherdata.class);

            //     Log.e("Toast", gson.toJson(otherdata));

            if (otherdata.getAction().equals("AppointmentRemoveFW")) {
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).appointmentModel().deleteAppointmentById(otherdata.getId());
                createNotification(data.get("title").toString(), data.get("body").toString(), "APPOINTMENT", null);
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
                // EotApp.getAppinstance().notifyObserver(otherdata.getAction(), otherdata.getId(), data.get("body").toString());
            } else if (otherdata.getAction().equals("removeFW")||otherdata.getAction().equals("archiveJob")||otherdata.getAction().equals("deleteJob")) {
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobById(otherdata.getId());
//                for remove the job listener from listener list.
                if (ChatController.getInstance() != null) {
                    ChatController.getInstance().removeListnerByJobID(otherdata.getId());
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("job_refresh"));
                createNotification(data.get("title").toString(), data.get("body").toString(), "JOB",
                        null);
                EotApp.getAppinstance().notifyObserver(otherdata.getAction(), otherdata.getId(), data.get("body").toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
            } /*else if (otherdata.getAction().equals("archiveJob")||otherdata.getAction().equals("deleteJob")) {
                String msg = "Archived Jobs";
                if (data.get("body").toString() != null)
                    msg = data.get("body").toString();
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().deleteJobById(otherdata.getId());
                if (ChatController.getInstance() != null) {
                    ChatController.getInstance().removeListnerByJobID(otherdata.getId());
                }
                createNotification(data.get("title").toString(), msg, "JOB",
                        null);
                EotApp.getAppinstance().notifyObserver(otherdata.getAction(), otherdata.getId(), msg);
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
            } else if (otherdata.getAction().equals("unarchiveJob")) {
                String msg = "UnArchived Jobs";
                if (data.get("body").toString() != null)
                    msg = data.get("body").toString();

                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("job_refresh"));
                createNotification(data.get("title").toString(), msg, "JOB", otherdata.getId());
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
            } */else if (otherdata.getAction().equals("updateJob")) {
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("job_refresh"));
                createNotification(data.get("title").toString(), data.get("body").toString(), "JOB", otherdata.getId());
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
            } else if (otherdata.getAction().equals("updateAppointment")) {
                if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAppointmentVisible()==0) {
                    createNotification(data.get("title").toString(), data.get("body").toString(), "APPOINTMENT", otherdata.getId());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
                }
            } else if (otherdata.getAction().equals("chat")) {
                ChatController.getInstance().createNotificationforChat(
                        new Gson().fromJson(data.get("body").toString(), Chat_Send_Msg_Model.class));
            } else if (otherdata.getAction().equals("newJob")||otherdata.getAction().equals("unarchiveJob")) {
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("job_refresh"));
                createNotification(data.get("title").toString(), data.get("body").toString(), "JOB", otherdata.getId());
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
            } else if (otherdata.getAction().equals("newAppointment")) {
                if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAppointmentVisible()==0) {
                    createNotification(data.get("title").toString(), data.get("body").toString(), "APPOINTMENT", otherdata.getId());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
                }
            } else if (otherdata.getAction().equals("newAudit")) {
                if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAuditVisible()==0) {
                    createNotification(data.get("title").toString(), data.get("body").toString(), "AUDIT", otherdata.getId());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
                }
            } else if (otherdata.getAction().equals("updateAudit")) {
                if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsAuditVisible()==0) {
                    createNotification(data.get("title").toString(), data.get("body").toString(), "AUDIT", otherdata.getId());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
                }
            } else if (otherdata.getAction().equals("one2one")) {
                NotModel mo = new Gson().fromJson(data.get("body").toString(), NotModel.class);
                UserToUserChatController.getInstance().
                        createofflineChatNotifications(mo);
            } else if (otherdata.getAction().equals("removeAuditFw")) {
                AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).auditDao().deletAuditById(otherdata.getId());
                createNotification(data.get("title").toString(), data.get("body").toString(), "AUDIT", null);
                // EotApp.getAppinstance().notifyObserver(otherdata.getAction(), otherdata.getId(), data.get("body").toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
            } else if (otherdata.getAction().equals("clientChat")) {
                ChatController.getInstance().createClientChatNotificationforChat(
                        new Gson().fromJson(data.get("body").toString(), ClientChatReqModel.class));
            } else if (otherdata.getAction().equals("updateLeave")) {
                createNotification(data.get("title").toString(), data.get("body").toString(), "updateLeave", otherdata.getId());
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("appointment_refresh"));
            }else if (otherdata.getAction().equals("someJobArchivedOrUnarchived")||otherdata.getAction().equals("someJobDeleted")) {
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("job_refresh"));
                if (otherdata.getAction().equals("someJobDeleted")){
                    createNotification("Deleted Jobs", data.get("body").toString(), "JOB",
                            null);
                }else {
                    createNotification("Archived Jobs", data.get("body").toString(), "JOB",
                            null);
                }
                EotApp.getAppinstance().notifyObserver(otherdata.getAction(), otherdata.getId(), data.get("body").toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("Archived_jobs"));
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /***
     * CREATE notification's for Job/Audit ****/
    private void createNotification(String title, String messageBody, String tag, String id) {
//        HyperLog.i(TAG, "title-->" + title);
//        HyperLog.i(TAG, "message-->" + messageBody);
//        HyperLog.i(TAG, "tag2-->" + tag);
//        HyperLog.i(TAG, "id-->" + id);

        int notIdEmergency = (int) System.currentTimeMillis();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("NOTIFICATIONTAG", tag);
        intent.putExtra("id", id);

        //   HyperLog.i(TAG, "notIdEmergency-->" + notIdEmergency);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(this, notIdEmergency,
                intent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);


        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (nNotificationUtils == null) {
            nNotificationUtils = new NotificationUtils(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder nb = nNotificationUtils.getAndroidChannelNotification(
                    0, title, messageBody, resultIntent)
                    .setShowWhen(true)
                    .setWhen(System.currentTimeMillis());
            nNotificationUtils.getManager().notify(notIdEmergency, nb.build());

            /*NotificationHelper notificationHelper = new NotificationHelper(mContext);
                notificationHelper.createNotification(title, message);*/
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification notification = nNotificationUtils.getNotificationManager(title, messageBody, resultIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notIdEmergency, notification);
        }
    }

}


