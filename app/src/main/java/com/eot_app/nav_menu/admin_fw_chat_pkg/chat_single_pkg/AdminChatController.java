package com.eot_app.nav_menu.admin_fw_chat_pkg.chat_single_pkg;

import android.util.Log;

import com.eot_app.nav_menu.admin_fw_chat_pkg.chatmember_list_pkg.chatmember_model.UserOflineOnlineModel;
import com.eot_app.utility.App_preference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Sonam-11 on 2020-03-09.
 */
public class AdminChatController {
    static private AdminChatController adminChatController;
    private int userChatBatchCount;
    ArrayList<UserOflineOnlineModel> userStatusModelList;

    static public AdminChatController getAdminChatInstance() {
        if (adminChatController == null) {
            adminChatController = new AdminChatController();
            //getUserOnlineOfflineList();
        }
        return adminChatController;

    }

    public String getAdminGrpChatPath(String userId) {
        String path = "";
        if (Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getUsrId()) < Integer.parseInt(userId)) {
            path = App_preference.getSharedprefInstance().getRegion()
                    + "/comp-"
                    + App_preference.getSharedprefInstance().getLoginRes().getCompId()
                    + "/chatuser/" + App_preference.getSharedprefInstance().getLoginRes().getUsrId()
                    + "-" + userId + "/messages/";
        } else {
            path = App_preference.getSharedprefInstance().getRegion()
                    + "/comp-"
                    + App_preference.getSharedprefInstance().getLoginRes().getCompId()
                    + "/chatuser/" + userId +
                    "-" + App_preference.getSharedprefInstance().getLoginRes().getUsrId() + "/messages/";
        }
        return path;
    }

    public int getUserChatBatchCount() {
        return userChatBatchCount;
    }

    public void setUserChatBatchCount(int userChatBatchCount) {
        this.userChatBatchCount = userChatBatchCount;
    }

    /**
     * chat path
     ****/
    public String getUserChatPath(String userId) {
        String userPath = "";
        if (Integer.parseInt(App_preference.getSharedprefInstance().getLoginRes().getUsrId()) < Integer.parseInt(userId)) {
            userPath = App_preference.getSharedprefInstance().getRegion()
                    + "/comp-"
                    + App_preference.getSharedprefInstance().getLoginRes().getCompId()
                    + "/chatuser/" + App_preference.getSharedprefInstance().getLoginRes().getUsrId()
                    + "-" + userId + "/msgcount/";
        } else {
            userPath = App_preference.getSharedprefInstance().getRegion()
                    + "/comp-"
                    + App_preference.getSharedprefInstance().getLoginRes().getCompId()
                    + "/chatuser/" + userId +
                    "-" + App_preference.getSharedprefInstance().getLoginRes().getUsrId() + "/msgcount/";
        }
        return userPath;
    }

    /*** get  online /ofline user list from Realtime Database***/
    public void setUserMgsCount(String rcvrId) {
        FirebaseFirestore.getInstance().collection(getUserChatPath(rcvrId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get a new write batch
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            if (!document.getId().equals("rvcr" + App_preference.getSharedprefInstance().getLoginRes().getUsrId())) {
                                SingleChatCountModel fwmodel = document.toObject(SingleChatCountModel.class);
                                fwmodel.setChatCount(fwmodel.getChatCount() + 1);
                                batch.set(document.getReference(), fwmodel);
                            }
                        }
                        // Commit the batch
                        batch.commit().addOnCompleteListener(task1 -> Log.d(TAG, "=>> committed"));
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public ArrayList<UserOflineOnlineModel> getUserStatusList() {
        return userStatusModelList;
    }
}
