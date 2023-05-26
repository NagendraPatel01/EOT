package com.eot_app.nav_menu.jobs.job_detail.job_equipment;

import android.annotation.SuppressLint;
import android.content.Context;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eot_app.R;
import com.eot_app.nav_menu.audit.audit_list.equipment.model.EquipmentStatus;
import com.eot_app.nav_menu.jobs.job_db.EquArrayModel;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.language_support.LanguageController;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Sonam-11 on 22/9/20.
 */
public class JobDetailEquipmentAdapter extends RecyclerView.Adapter<JobDetailEquipmentAdapter.MyViewHolder> {
    final ArrayList<EquArrayModel> suggestions;
    Context mContext;
    List<EquArrayModel> list;

    OnEquipmentClicked onEquipmentClicked;
    List<EquipmentStatus> equipmentStatus;
    List<EquipmentStatus> equipmentCurrentStatus;


    public JobDetailEquipmentAdapter(Context mContext, OnEquipmentClicked onEquipmentClicked) {
        this.onEquipmentClicked = onEquipmentClicked;
        this.mContext = mContext;
        equipmentStatus = App_preference.getSharedprefInstance().getLoginRes().getEquipmentStatus();
        this.suggestions = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEquipmentCurrentStatus(List<EquipmentStatus> equipmentCurrentStatus) {
        this.equipmentCurrentStatus = equipmentCurrentStatus;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<EquArrayModel> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }

    public List<EquArrayModel> getList() {
        return list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<EquArrayModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public JobDetailEquipmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_audit_equipment_detail, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull JobDetailEquipmentAdapter.MyViewHolder holder, final int position) {
        EquArrayModel equArrayModel = list.get(position);
        Log.e("List", "" + new Gson().toJson(list));
        holder.tv_item_name.setText(equArrayModel.getEqunm());


        holder.ll_main.setOnClickListener(v -> onEquipmentClicked.OnEquipmentClicked());

        /* 0 Mean equipment 1 means Part(Sub equipment) ****/
        if (!TextUtils.isEmpty(equArrayModel.getIsPart()) && !equArrayModel.getIsPart().equals("0"))
            holder.condition.setVisibility(View.VISIBLE);
        else holder.condition.setVisibility(View.GONE);

        holder.tv_model_label.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.equipment_model) + ": ");
        holder.tv_serial_label.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.equipment_serial) + ": ");

        holder.tv_model.setText("" + equArrayModel.getMno());
        holder.tv_serial.setText("" + equArrayModel.getSno());

        if (!TextUtils.isEmpty(equArrayModel.getImage())) {
            Glide.with(mContext).load(App_preference.getSharedprefInstance().getBaseURL() + equArrayModel.getImage())
                    .thumbnail(Glide.with(mContext).load(R.raw.loader_eot)).placeholder(R.drawable.app_logo2).into(holder.img_equipment);
        }

        try {
            if (equArrayModel.getEquStatus() != null) {
                if (equArrayModel.getEquStatus().equals("")) {
                    holder.ll_status.setVisibility(View.GONE);
                } else if (equArrayModel.getEquStatus().equals("0")) {
                    holder.ll_status.setVisibility(View.GONE);
                } else {
                    holder.ll_status.setVisibility(View.VISIBLE);
                    holder.tv_status.setText(getCurrentStatusNameById(equArrayModel.getEquStatus()));

                    if(equArrayModel.getStatusUpdateDate() != null&&!equArrayModel.getStatusUpdateDate().isEmpty()){
                        holder.tv_date.setText(AppUtility.getDate(Long.parseLong(equArrayModel.getStatusUpdateDate()), "dd MMM yyyy"));
                        holder.tv_date.setVisibility(View.VISIBLE);
                    }
                    else
                        holder.tv_date.setVisibility(View.GONE);

                    if (getCurrentStatusNameById(equArrayModel.getEquStatus()).trim().equalsIgnoreCase("Discarded")) {
                        holder.ll_status.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_red));
                    } else {
                        holder.ll_status.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_yellow));
                    }
                }
            } else {
                holder.ll_status.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            holder.ll_status.setVisibility(View.GONE);
        }
    }

    private String getStatusNameById(String statusId) {
        String statusName = "";
        if (equipmentStatus != null) {
            for (EquipmentStatus equipmentStatus : equipmentStatus)
                if (equipmentStatus.getEsId().equals(statusId)) {
                    statusName = equipmentStatus.getStatusText();
                    break;
                }
        }
        return statusName;
    }

    private String getCurrentStatusNameById(String statusId) {
        String statusName = "";
        if (equipmentCurrentStatus != null) {
            for (EquipmentStatus equipmentStatus : equipmentCurrentStatus)
                if (equipmentStatus.getEsId().equals(statusId)) {
                    statusName = equipmentStatus.getStatusText();
                    break;
                }
        }
        return statusName;
    }

    @Override
    public int getItemCount() {
        return this.list == null ? 0 : this.list.size();
    }

    public interface OnEquipmentSelection {
        void onEquipmentSelected(int position, EquArrayModel equipmentRes);
    }

    public interface OnEquipmentClicked {
        void OnEquipmentClicked();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView client_item_constraint;
        TextView tv_item_name, condition;
        AppCompatImageView img_equipment;
        TextView equ_img_view;
        AppCompatTextView tv_model, tv_serial,tv_date, tv_model_label, tv_serial_label,tv_status;//, tv_des;// tv_status, , tv_details ,, tv_remark
        LinearLayout ll_main;
        LinearLayout ll_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            client_item_constraint = itemView.findViewById(R.id.client_item_constraint);

            condition = itemView.findViewById(R.id.condition);
            condition.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.parts));
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            img_equipment = itemView.findViewById(R.id.img_equipment);
            ll_main = itemView.findViewById(R.id.ll_main);
            tv_model = itemView.findViewById(R.id.tv_model);
            tv_serial = itemView.findViewById(R.id.tv_serial);
            tv_model_label = itemView.findViewById(R.id.tv_model_label);
            tv_serial_label = itemView.findViewById(R.id.tv_serial_label);
            ll_status = itemView.findViewById(R.id.ll_status);
            equ_img_view = itemView.findViewById(R.id.equ_img_view);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_date = itemView.findViewById(R.id.tv_date);


        }


    }


}
