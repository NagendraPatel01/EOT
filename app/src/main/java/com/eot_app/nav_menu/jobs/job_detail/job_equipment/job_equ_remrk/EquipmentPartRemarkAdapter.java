package com.eot_app.nav_menu.jobs.job_detail.job_equipment.job_equ_remrk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class EquipmentPartRemarkAdapter extends RecyclerView.Adapter<EquipmentPartRemarkAdapter.MyViewHolder> {
    final ArrayList<EquArrayModel> suggestions;
    Context mContext;
    List<EquArrayModel> list;
    List<EquipmentStatus> equipmentStatus;
    OnEquipmentSelectionForDetails selectionForDetails;
    OnEquipmentSelection onEquipmentSelection;
    boolean llDetailHide=false;
    List<EquipmentStatus> equipmentCurrentStatus;

    @SuppressLint("NotifyDataSetChanged")
    public void setEquipmentCurrentStatus(List<EquipmentStatus> equipmentCurrentStatus) {
        this.equipmentCurrentStatus = equipmentCurrentStatus;
        notifyDataSetChanged();
    }
    public EquipmentPartRemarkAdapter(Context mContext, List<EquArrayModel> list) {
        this.mContext = mContext;
        this.llDetailHide = true;
        this.list = list;
        equipmentStatus = App_preference.getSharedprefInstance().getLoginRes().getEquipmentStatus();
        this.suggestions = new ArrayList<>();
    }

    public EquipmentPartRemarkAdapter(Context mContext, List<EquArrayModel> list, OnEquipmentSelectionForDetails selectionForDetails) {
        this.mContext = mContext;
        this.list = list;
        equipmentStatus = App_preference.getSharedprefInstance().getLoginRes().getEquipmentStatus();
        this.suggestions = new ArrayList<>();
        this.selectionForDetails = selectionForDetails;
    }
    public void setOnEquipmentSelection(OnEquipmentSelection onEquipmentSelection) {
        this.onEquipmentSelection = onEquipmentSelection;
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
    public interface OnEquipmentSelectionForDetails {
        void onEquipmentSelectedForDetails(EquArrayModel equipmentRes);
    }

    @NonNull
    @Override
    public EquipmentPartRemarkAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.equipment_remark_part, viewGroup, false);
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
    public void onBindViewHolder(@NonNull EquipmentPartRemarkAdapter.MyViewHolder holder, final int position) {
        EquArrayModel equArrayModel = list.get(position);
        Log.e("List", "" + new Gson().toJson(list));
        holder.tv_item_name.setText(equArrayModel.getEqunm());




        if(llDetailHide)
        {
            holder.ll_views.setVisibility(View.GONE);
        }
        else {
            holder.ll_views.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(equArrayModel.getStatus()) || equArrayModel.getAttachments() != null && equArrayModel.getAttachments().size() > 0) {
            holder.add_remark.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.view_edit_remark));
        } else {
            holder.add_remark.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_remark));
        }

        holder.view_details.setOnClickListener(v -> selectionForDetails.onEquipmentSelectedForDetails(list.get(position)));
        holder.add_remark.setOnClickListener(v -> setRemarkActivity(holder.getBindingAdapterPosition()));


        if(equArrayModel.getExpiryDate()!=null&&!equArrayModel.getExpiryDate().isEmpty()){
            try {
                String expiryDate = AppUtility.getDate(Long.parseLong(equArrayModel.getExpiryDate()),"dd/MM/yyyy");
                Log.e("Equipment",expiryDate);
                Log.e("Equipment",AppUtility.getCurrentDateByFormat("dd/MM/yyyy"));
                if(!AppUtility.compareTwoDatesWarranty(AppUtility.getCurrentDateByFormat("dd/MM/yyyy"),expiryDate,"dd/MM/yyyy")){
                    holder.tvWarranty.setTextColor(ContextCompat.getColor(mContext,R.color.red_color));
                    holder.ivAlert.setVisibility(View.VISIBLE);
                }
                else{
                    holder.tvWarranty.setTextColor(ContextCompat.getColor(mContext,R.color.txt_sub_color));
                    holder.ivAlert.setVisibility(View.GONE);
                }
                holder.tvWarranty.setVisibility(View.VISIBLE);
                holder.tvWarranty.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.warranty_expiry_date) + ": "+expiryDate);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else {
            holder.tvWarranty.setVisibility(View.GONE);
        }
        holder.tv_model_label.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.equipment_model) + ": ");
        holder.tv_serial_label.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.equipment_serial) + ": ");

        holder.tv_model.setText("" + equArrayModel.getMno());
        holder.tv_serial.setText("" + equArrayModel.getSno());

        if (!TextUtils.isEmpty(equArrayModel.getImage())) {
            Glide.with(mContext).load(App_preference.getSharedprefInstance().getBaseURL() + equArrayModel.getImage())
                    .thumbnail(Glide.with(mContext).load(R.raw.loader_eot))
                    .placeholder(R.drawable.app_logo2).into(holder.img_equipment);
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

    @Override
    public int getItemCount() {
        return this.list == null ? 0 : this.list.size();
    }

    public interface OnEquipmentSelection {
        void onEquipmentSelected(int position, EquArrayModel equipmentRes);
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



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView client_item_constraint;
        TextView tv_item_name, condition;
        AppCompatImageView img_equipment;
        TextView equ_img_view;
        AppCompatTextView tv_model, tv_serial, tv_date,tv_model_label, tv_serial_label,tv_status;//, tv_des;// tv_status, , tv_details ,, tv_remark
        LinearLayout ll_main;
        LinearLayout ll_views;
        TextView tvWarranty;
        ImageView ivAlert;
        TextView view_details, add_remark;
        LinearLayout ll_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            client_item_constraint = itemView.findViewById(R.id.client_item_constraint);

            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            img_equipment = itemView.findViewById(R.id.img_equipment);
            ll_main = itemView.findViewById(R.id.ll_main);
            ll_views = itemView.findViewById(R.id.ll_views);
            tv_model = itemView.findViewById(R.id.tv_model);
            tv_serial = itemView.findViewById(R.id.tv_serial);
            tv_model_label = itemView.findViewById(R.id.tv_model_label);
            tv_serial_label = itemView.findViewById(R.id.tv_serial_label);

            equ_img_view = itemView.findViewById(R.id.equ_img_view);
            tvWarranty = itemView.findViewById(R.id.tvWarranty);
            ivAlert = itemView.findViewById(R.id.ivAlert);

            view_details = itemView.findViewById(R.id.view_details);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_date = itemView.findViewById(R.id.tv_date);
            ll_status = itemView.findViewById(R.id.ll_status);
            view_details.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.view_details));
            add_remark = itemView.findViewById(R.id.add_remark);
        }


    }


    void setRemarkActivity(int position) {
        if (onEquipmentSelection != null) {
            list.get(position).setIsPart("1");
            onEquipmentSelection.onEquipmentSelected(position, list.get(position));
        } else {
            String strEqu = new Gson().toJson(list.get(position));
            mContext.startActivity(new Intent(mContext, JobEquRemarkRemarkActivity.class).putExtra("equipment", strEqu));
        }
    }


}
