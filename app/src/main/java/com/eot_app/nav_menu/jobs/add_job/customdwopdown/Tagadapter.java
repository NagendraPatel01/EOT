package com.eot_app.nav_menu.jobs.add_job.customdwopdown;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.eot_app.R;
import com.eot_app.custom_dropDown.ItemListener;
import com.eot_app.utility.settings.setting_db.JobTitle;
import com.eot_app.utility.settings.setting_db.TagData;

import java.util.List;

public class Tagadapter extends RecyclerView.Adapter<Tagadapter.MyViewHolder> {

    private final ItemListener itemListener;
    private List<TagData> jobServiceList;

    public Tagadapter(List<TagData> jobServiceList, ItemListener itemListener) {
        this.jobServiceList = jobServiceList;
        this.itemListener = itemListener;
    }


    @NonNull
    @Override
    public Tagadapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_spinner_items, parent, false);

        return new Tagadapter.MyViewHolder(itemView);
    }

    public void filterList(List<TagData> jobServiceList) {
        this.jobServiceList = jobServiceList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final Tagadapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final TagData jobTitle = jobServiceList.get(position);

        holder.checkbox.setChecked(false);

        holder.checkbox.setOnClickListener(v -> {
            jobTitle.setSelect(!jobTitle.isSelect());
            itemListener.onItemClick();
        });

        if (jobTitle.isSelect()) {
            holder.checkbox.setChecked(true);
            holder.checkbox.setTextColor(ContextCompat.getColor(holder.checkbox.getContext(), R.color.colorPrimary));
        } else {
            holder.checkbox.setChecked(false);
            holder.checkbox.setTextColor(ContextCompat.getColor(holder.checkbox.getContext(), R.color.txt_color));
        }
        holder.checkbox.setText(jobTitle.getTnm());
    }

    @Override
    public int getItemCount() {
        return jobServiceList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkbox;
        public LinearLayout dplayout;

        public MyViewHolder(View view) {
            super(view);
            dplayout = view.findViewById(R.id.dplayout);
            checkbox = view.findViewById(R.id.checkbox);
        }
    }


}
