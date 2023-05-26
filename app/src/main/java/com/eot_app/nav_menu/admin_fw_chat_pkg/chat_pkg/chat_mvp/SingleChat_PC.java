package com.eot_app.nav_menu.admin_fw_chat_pkg.chat_pkg.chat_mvp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.eot_app.nav_menu.admin_fw_chat_pkg.chat_pkg.model_pkg.SingleChatModel;
import com.eot_app.nav_menu.admin_fw_chat_pkg.chat_single_pkg.AdminChatController;
import com.eot_app.nav_menu.jobs.job_detail.chat.ChatFragment;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import java.util.Objects;

/**
 * Created by Sonam-11 on 2020-03-07.
 */
public class SingleChat_PC implements SingleChat_PI {
    SingleChat_View singleChatView;
    String PATH;
    private DatabaseReference databaseReference;
    String rcvrId;

    public SingleChat_PC(SingleChat_View singleChatView, String PATH, String rcvrId) {
        this.singleChatView = singleChatView;
        this.PATH = PATH;
        this.rcvrId = rcvrId;
    }

    @Override
    public void sendChatMessages(SingleChatModel chatModel) {

        FirebaseFirestore.getInstance().collection(PATH)
                .add(chatModel)
                .addOnSuccessListener(documentReference -> {
                    Log.e("Message Send", documentReference.getId());
                    AdminChatController.getAdminChatInstance().setUserMgsCount(rcvrId);
                })
                .addOnFailureListener(e -> Log.e("Msg Not Send", Objects.requireNonNull(e.getMessage())));

    }

    @Override
    public void uploadActualImageOnFireStore(Uri uri) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(((Context) singleChatView));
            try {
                final String im_url = ChatFragment.PATH + System.currentTimeMillis();
                FirebaseStorage.getInstance().getReference().child(im_url).putFile(uri).addOnCompleteListener(task -> {
                    Log.e("TAG", "image upload");
                    getmeUrl(im_url, Objects.requireNonNull(task.getResult().getMetadata()).getContentType());
                }).addOnFailureListener(e -> {
                    Log.e("TAG", "fail");
                    AppUtility.progressBarDissMiss();
                }).addOnSuccessListener(taskSnapshot -> {
                    Log.e("TAG", "Success");
                    AppUtility.progressBarDissMiss();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network));
        }
    }


    //after upload image get image uri from firebase storage & than send msg
    public void getmeUrl(String image_url, final String type) {
        FirebaseStorage.getInstance().getReference().child(image_url).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    SingleChatModel fileModel = new SingleChatModel
                            ("", uri.toString(), AppUtility.getDateByMiliseconds(), type);
                    String string = PATH.replace(".", "-");
                    databaseReference = FirebaseDatabase.getInstance().getReference(string);
                    databaseReference.child(Objects.requireNonNull(databaseReference.push().getKey())).push().setValue(fileModel);
                    sendChatMessages(fileModel);
                }).addOnFailureListener(exception -> Log.e("", ""));

    }
}
