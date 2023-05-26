package com.eot_app.nav_menu.jobs.job_detail.job_status_pkg;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.JobStatusModel;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.JobStatusModelNew;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.google.gson.Gson;
import com.hypertrack.hyperlog.HyperLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class JobStatus_Controller {

    private static JobStatus_Controller ourInstance;
    List<JobStatusModel> jobStatusStaticList = new ArrayList<>();// = new ArrayList<>();
    List<JobStatusModelNew> jobStatusDynamicList = new ArrayList<>();// = new ArrayList<>();
    private JSONObject jsonObject;

    private JobStatus_Controller() {
        getStatusList();
        getJsonFileById();//get json for button Ids & job Status change by button
        setDynamicStatusList();
    }

    public static JobStatus_Controller getInstance() {
        if (ourInstance == null) {
            ourInstance = new JobStatus_Controller();
        }
        return ourInstance;
    }


    public void getStatusList() {
        jobStatusStaticList = new ArrayList<>();
        try {
            InputStream is = null;
            is = EotApp.getAppinstance().getAssets().open("jobstatus_keys.json");
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("jobstatus");
            is.close();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String statname = "";
                    statname = LanguageController.getInstance().getMobileMsgByKey(jsonArray.getJSONObject(i).getString("name"));
                    jobStatusStaticList.add(new JobStatusModel((String) jsonArray.getJSONObject(i).get("id"), statname, (String) jsonArray.getJSONObject(i).get("img")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException je) {
            Log.e("Error", je.getMessage());
            je.printStackTrace();
        }
    }

    public void setDynamicStatusList() {
        jobStatusDynamicList = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().getAllStatusList();
        // for setting static images for default status
        for (JobStatusModel jobStatusStatic : jobStatusStaticList) {
            for (JobStatusModelNew jobstatus : jobStatusDynamicList) {
                if (jobstatus.getKey().equalsIgnoreCase(jobStatusStatic.getKey())) {
                    jobstatus.setImg(jobStatusStatic.getImg());
                }
            }
        }
        AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().insertJobstatusList(jobStatusDynamicList);
        Log.e("JobStatusListLocal::", new Gson().toJson(AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobStatusModelNew().getAllStatusList()));
    }


    public void setIconForStatus(JobStatusModelNew jobStatus, ImageView imageView) {
        // for setting static images
        if (jobStatus.getIsDefault().equalsIgnoreCase("1")) {
            int id = EotApp.getAppinstance().getResources().getIdentifier(jobStatus.getImg(), "drawable", EotApp.getAppinstance().getPackageName());
            imageView.setImageResource(id);
        } else if (jobStatus.getUrlBitmap() != null && !jobStatus.getUrlBitmap().isEmpty()) {
            Bitmap bitmap1 = AppUtility.StringToBitMap(jobStatus.getUrlBitmap());
            imageView.setImageBitmap(bitmap1);
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


    public JobStatusModelNew getStatusByButtonAction(String ids, int type) {
        HyperLog.i("", "getStatusByButtonAction(M) Start");
        type--; //  see the josn file first and this btn id is the indexing of array in json format.
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(ids);
            String statusName = "";
            statusName = LanguageController.getInstance().getMobileMsgByKey(jsonArray.getJSONObject(type).getString("status_name"));
            return new JobStatusModelNew(jsonArray.getJSONObject(type).getString("status_no"), statusName, jsonArray.getJSONObject(type).getString("img"));
        } catch (JSONException e) {
            HyperLog.i("", e.toString());
            e.printStackTrace();
        }
        HyperLog.i("", "getStatusByButtonAction(M) Stop");
        return null;
    }


    public JobStatusModelNew getStatusObjectById(String statusId) {
        if (jobStatusDynamicList != null) {
            for (JobStatusModelNew jobStatusModel : jobStatusDynamicList) {
                if (jobStatusModel != null && jobStatusModel.getStatus_no().equals(statusId)) {
                    return jobStatusModel;
                }
            }
        }
        return null;
    }


}