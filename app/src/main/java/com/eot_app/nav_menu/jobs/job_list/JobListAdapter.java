package com.eot_app.nav_menu.jobs.job_list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.eot_app.R;
import com.eot_app.nav_menu.jobs.job_controller.ChatController;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_db.JtId;
import com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model.JobStatusModelNew;
import com.eot_app.nav_menu.jobs.job_detail.job_status_pkg.JobStatus_Controller;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.util_interfaces.MyListItemSelected;
import java.util.Iterator;
import java.util.List;

/**
 * Created by geet-pc on 16/4/18.
 */

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.MyViewHolder> {
    private final MyListItemSelected<Job> mListner;
    final Context context;
    private final List<Job> jobdata;
    OnListScrolling onListScrolling;
    int selected_item;
    private int unScheduleHeaderPos = -1;
    boolean isShowSiteName = false;

    public JobListAdapter(Context context, MyListItemSelected<Job> mListner, List<Job> arrayList) {
        this.mListner = mListner;
        this.jobdata = arrayList;
        this.context = context;

    }

    public void setFromShowSiteName(boolean isShowSiteName) {
        this.isShowSiteName = isShowSiteName;
    }

    public void setUnScheduleHeaderPos(int unScheduleHeaderPos) {
        this.unScheduleHeaderPos = unScheduleHeaderPos;
    }

    public void setOnListScrolling(OnListScrolling onListScrolling) {
        this.onListScrolling = onListScrolling;
    }

    public void updateRecords(List<Job> jobdataa) {
        jobdata.clear();
        jobdata.addAll(jobdataa);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (App_preference.getSharedprefInstance().getLoginRes().getCompPermission().get(0).getIsCompAdrShowFirstMobile()!=null
                &&App_preference.getSharedprefInstance().getLoginRes().getCompPermission().get(0).getIsCompAdrShowFirstMobile().equals("1")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item_view_second, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item_view, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (onListScrolling != null) onListScrolling.currentVisiblePos(position);
        if (position == getItemCount() - 1) holder.blank_view.setVisibility(View.VISIBLE);
        else holder.blank_view.setVisibility(View.GONE);
        String today_date = AppUtility.getDateByFormat(AppUtility.dateTimeByAmPmFormate("EEE, dd MMM yyyy ,hh:mm",
                "EEE, dd MMM yyyy ,kk:mm"));// ,a
        String[] today_dt = today_date.split(",");
        String to_day = today_dt[1];


        String temp_date = "", cr_date;
        holder.item_main_layout.setOnClickListener(v -> {
            mListner.onMyListitemSeleted(getItem(position));
            selected_item = position;
        });

        /* ** Job notification Batch count ***/

        int batchCount = ChatController.getInstance().getbatchCount(jobdata.get(position).getJobId())
                + ChatController.getInstance().getClientChatBatchCount(jobdata.get(position).getJobId());
        if (batchCount > 0) {
            holder.badge_count.setVisibility(View.VISIBLE);
            holder.badge_count.setText(String.valueOf(batchCount));
        } else {
            holder.badge_count.setVisibility(View.GONE);
        }

        switchDefaultColor(holder, EotApp.getAppinstance().getResources().getColor(R.color.body_font_color));
        if (jobdata.get(position).getSchdlStart() != null && !jobdata.get(position).getSchdlStart().equals("")) {
            String[] date = AppUtility.getFormatedTime(jobdata.get(position).getSchdlStart());
            cr_date = date[0];
            String[] date_separated = cr_date.split(",");
            Spannable txtFrst;

            if (to_day.contains(date_separated[1])) {
                txtFrst = new SpannableString("Today" + ",");
            } else {
                txtFrst = new SpannableString(date_separated[0] + ",");
            }

            txtFrst.setSpan(new ForegroundColorSpan(Color.parseColor("#00848d")), 0, txtFrst.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.job_time.setText(date[1]);
            try {
                if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null
                        && App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0"))
                    holder.job_am_pm.setText(date[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (position < jobdata.size())
                if (position == 0 || (jobdata.get(position - 1).getSchdlStart() != null && jobdata.get(position - 1).getSchdlStart().equals(""))) {
                    holder.txt_date.setText(txtFrst);
                    holder.txt_date.append(date_separated[1]);
                    holder.date_layout.setVisibility(View.VISIBLE);

                } else if (jobdata.get(position - 1).getSchdlStart() != null && !jobdata.get(position - 1).getSchdlStart().equals("")) {
                    String[] date2 = AppUtility.getFormatedTime(jobdata.get(position - 1).getSchdlStart());
                    temp_date = date2[0];
                    if (cr_date.equals(temp_date)) {
                        holder.date_layout.setVisibility(View.GONE);
                    } else {
                        holder.txt_date.setText(txtFrst);
                        holder.txt_date.append(date_separated[1]);
                        holder.date_layout.setVisibility(View.VISIBLE);
                    }
                }
        } else {
            holder.date_layout.setVisibility(View.GONE);
            holder.txt_date.setText("");
            holder.job_time.setText("");
            holder.job_am_pm.setText("");
        }

        if (unScheduleHeaderPos == position) {
            holder.date_layout.setVisibility(View.VISIBLE);
            holder.txt_date.setText(
                    LanguageController.getInstance().getMobileMsgByKey(AppConstant.unschedule_job)
            );
        }
        String label = jobdata.get(position).getLabel();
        String Nm = jobdata.get(position).getNm();
        if (label != null && Nm != null) {
            holder.label.setText(label + " - " + Nm);
        } else {
            holder.label.setText("Job" + " - " + "client");
        }

        String StrJt = "";
        List<JtId> jtaray = jobdata.get(position).getJtId();
        if (jtaray != null) {
            Iterator<JtId> it = jtaray.iterator();
            if (it.hasNext()) {
                StrJt += it.next().getTitle();
            }
            while (it.hasNext()) {
                StrJt = StrJt + ", " + it.next().getTitle();
            }
            holder.des.setText(StrJt);
            holder.des.setTextColor(EotApp.getAppinstance().getResources().getColor(R.color.body_font_color));
        }
        if (jobdata.get(position).getJobId().equals(jobdata.get(position).getTempId())) {
            holder.des.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.job_not_sync));
            holder.des.setTextColor(EotApp.getAppinstance().getResources().getColor(android.R.color.holo_red_light));
        }

        String full_address = jobdata.get(position).getAdr() + ", " + jobdata.get(position).getCity();
        holder.address.setText(full_address);
        holder.prty.setImageResource(getImageRes(jobdata.get(position).getPrty()));

        try {
            setViewByJobStatus(holder, Integer.parseInt(jobdata.get(position).getStatus()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        if (App_preference.getSharedprefInstance().getSiteNameShowInSetting()) {
            holder.site_name.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(jobdata.get(position).getSnm()))
                holder.site_name.setText(jobdata.get(position).getSnm());
            else holder.site_name.setText("");
        } else holder.site_name.setVisibility(View.GONE);


        /* ****visible item & Equipment Flag****/
        if (jobdata.get(position).getItemData() != null && jobdata.get(position).getItemData().size() > 0) {
            holder.item_flag.setVisibility(View.VISIBLE);
        } else {
            holder.item_flag.setVisibility(View.GONE);
        }

        if (jobdata.get(position).getEquArray() != null && jobdata.get(position).getEquArray().size() > 0) {
            holder.equi_flag.setVisibility(View.VISIBLE);
        } else {
            holder.equi_flag.setVisibility(View.GONE);
        }

        if (jobdata.get(position).getAttachCount() != null && Integer.parseInt(jobdata.get(position).getAttachCount()) != 0) {
            holder.attachmemt_flag.setVisibility(View.VISIBLE);
        } else {
            holder.attachmemt_flag.setVisibility(View.GONE);
        }
    }


    private void switchDefaultColor(MyViewHolder holder, int color) {
        holder.status.setTextColor(color);
        holder.job_time.setTextColor(color);
        holder.job_am_pm.setTextColor(color);
        holder.status.setTextColor(color);
    }

    private void setViewByJobStatus(MyViewHolder holder, int status) {
        try {
            JobStatusModelNew jobStatusObject = JobStatus_Controller.getInstance().getStatusObjectById(String.valueOf(status));
            if (jobStatusObject != null) {
                holder.status.setText(jobStatusObject.getStatus_name());
                holder.status_constraints.setBackgroundResource(R.color.white);

//                int id = context.getResources().getIdentifier(jobStatusObject.getImg(), "drawable", context.getPackageName());
//                holder.status_img.setImageResource(id);
//
                if(jobStatusObject.getIsDefault().equalsIgnoreCase("1")){
                    int id = EotApp.getAppinstance().getResources().getIdentifier(jobStatusObject.getImg(), "drawable", EotApp.getAppinstance().getPackageName());
                    holder.status_img.setImageResource(id);
                }
                else {
                    if(jobStatusObject.getUrlBitmap()!=null&&!jobStatusObject.getUrlBitmap().isEmpty()){
                        Bitmap bitmap1= AppUtility.StringToBitMap(jobStatusObject.getUrlBitmap());
                        holder.status_img.setImageBitmap(bitmap1);
                    }
                    else {
                        holder.status_img.setImageBitmap(null);
                    }
                }

                if (jobStatusObject.getStatus_no().equals("7")) {
                    holder.status_constraints.setBackgroundResource(R.color.in_progress);
                    switchDefaultColor(holder, EotApp.getAppinstance().getResources().getColor(R.color.white));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getImageRes(String prty) {
        if (prty != null) {
            switch (prty) {
                case "1":
                    return R.drawable.prty_low;
                case "2":
                    return R.drawable.prty_medium;
                case "3":
                    return R.drawable.prty_high;
            }
        }
        return R.drawable.prty_low;
    }

    private Job getItem(int position) {
        return jobdata.get(position);
    }

    @Override
    public int getItemCount() {
//        return jobdata.size();
        return jobdata.size();
    }

    public interface OnListScrolling {
        void currentVisiblePos(int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView label, status, des, address, job_time, job_am_pm, txt_date, badge_count;//  job_date;
        LinearLayout item_main_layout, date_layout;
        ImageView prty, status_img;
        ConstraintLayout status_constraints;
        View blank_view;
        ImageView item_flag, equi_flag, attachmemt_flag;
        TextView site_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_main_layout = itemView.findViewById(R.id.item_main_layout);
            blank_view = itemView.findViewById(R.id.blank_view);
            label = itemView.findViewById(R.id.label);
            des = itemView.findViewById(R.id.des);
            badge_count = itemView.findViewById(R.id.badge_count);
            address = itemView.findViewById(R.id.address);
            prty = itemView.findViewById(R.id.prty);
            status = itemView.findViewById(R.id.status);
            job_time = itemView.findViewById(R.id.job_time);
            job_am_pm = itemView.findViewById(R.id.job_am_pm);
            status_img = itemView.findViewById(R.id.status_img);
            status_constraints = itemView.findViewById(R.id.status_constraints);
            txt_date = itemView.findViewById(R.id.txt_date);
            date_layout = itemView.findViewById(R.id.date_layout);
            equi_flag = itemView.findViewById(R.id.equi_flag);
            item_flag = itemView.findViewById(R.id.item_flag);
            attachmemt_flag = itemView.findViewById(R.id.attachmemt_flag);
            site_name = itemView.findViewById(R.id.site_name);

        }
    }
}
