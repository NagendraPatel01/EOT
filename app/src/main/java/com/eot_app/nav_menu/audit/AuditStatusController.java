package com.eot_app.nav_menu.audit;

import android.util.Log;

import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.JobStatusModel;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.hypertrack.hyperlog.HyperLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AuditStatusController {

    private static AuditStatusController ourInstance;
    private List<JobStatusModel> auditStatusList = new ArrayList<>();// = new ArrayList<>();
    private JSONObject jsonObject;

    private AuditStatusController() {
        getStatusList();
    }

    public static AuditStatusController getInstance() {
        if (ourInstance == null) {
            ourInstance = new AuditStatusController();
        }
        return ourInstance;
    }


    public void getStatusList() {
        auditStatusList = new ArrayList<>();
        try {
            InputStream is = null;
            is = EotApp.getAppinstance().getAssets().open("jobstatus_keys.json");

            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            getJsonFileById();//get json for buttob Ids & job Status change by button
            String json = new String(data);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("jobstatus");
            is.close();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String statname = "";
                    statname = LanguageController.getInstance().getMobileMsgByKey(jsonArray.getJSONObject(i).getString("name"));
                    auditStatusList.add(new JobStatusModel((String) jsonArray.getJSONObject(i).get("id"), statname, (String) jsonArray.getJSONObject(i).get("img")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException je) {
            Log.e("Error", je.getMessage());
            je.printStackTrace();
        }
    }

    private void getJsonFileById() {
        try {
            InputStream is = EotApp.getAppinstance().getAssets().open("job_status_btn.json");
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data);
            jsonObject = new JSONObject(json);
            Log.e("", "" + jsonObject.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public JobStatusModel getStatusByButtonAction(String ids, int type) {
        HyperLog.i("", "getStatusByButtonAction(M) Start");
        type--; //  see the josn file first and this btn id is the indexing of array in json format.
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(ids);
            String statusName = "";
            statusName = LanguageController.getInstance().getMobileMsgByKey(jsonArray.getJSONObject(type).getString("status_name"));
            return new JobStatusModel(jsonArray.getJSONObject(type).getString("status_no"), statusName, jsonArray.getJSONObject(type).getString("img"));
        } catch (JSONException e) {
            HyperLog.i("", e.toString());
            e.printStackTrace();
        }
        HyperLog.i("", "getStatusByButtonAction(M) Stop");
        return null;
    }


    public JobStatusModel getStatusObjectById(String statusId) {
        if (auditStatusList != null) {
            for (JobStatusModel jobStatusModel : auditStatusList) {
                if (jobStatusModel != null && jobStatusModel.getStatus_no().equals(statusId)) {
                    return jobStatusModel;
                }
            }
        }
        return null;
    }

}
