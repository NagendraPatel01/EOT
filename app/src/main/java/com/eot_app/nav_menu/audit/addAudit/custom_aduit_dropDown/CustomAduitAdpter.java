package com.eot_app.nav_menu.audit.addAudit.custom_aduit_dropDown;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.eot_app.R;
import com.eot_app.custom_dropDown.ItemListener;
import com.eot_app.utility.settings.setting_db.FieldWorker;
import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * Created by Sonam-11 on 13/8/20.
 */
class CustomAduitAdpter extends RecyclerView.Adapter<CustomAduitAdpter.MyViewHolder> {
    ItemListener itemListener;
    private List<FieldWorker> jobServiceList;


    public CustomAduitAdpter(List<FieldWorker> jobServiceList, ItemListener itemListener) {
        this.jobServiceList = jobServiceList;
        this.itemListener = itemListener;
    }


    @NotNull
    @Override
    public CustomAduitAdpter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_spinner_items, parent, false);

        return new MyViewHolder(itemView);
    }

    public void filterList(List<FieldWorker> jobServiceList) {
        this.jobServiceList = jobServiceList;
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CustomAduitAdpter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final FieldWorker jobTitle = jobServiceList.get(position);


        holder.checkbox.setChecked(false);

        holder.checkbox.setOnClickListener(v -> {

            jobTitle.setSelect(!jobTitle.isSelect());

            notifyDataSetChanged();
            itemListener.onItemClick();
        });

        if (jobTitle.isSelect()) {
            holder.checkbox.setChecked(true);
            holder.checkbox.setTextColor(ContextCompat.getColor(holder.checkbox.getContext(), R.color.colorPrimary));
        } else {
            holder.checkbox.setChecked(false);
            holder.checkbox.setTextColor(ContextCompat.getColor(holder.checkbox.getContext(), R.color.txt_color));
        }


        holder.checkbox.setText(jobTitle.getFnm() +" "+jobTitle.getLnm());
    }

    @Override
    public int getItemCount() {
        return jobServiceList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkbox;
        public LinearLayout dplayout;
//        public View views;

        public MyViewHolder(View view) {
            super(view);
            dplayout = view.findViewById(R.id.dplayout);
            checkbox = view.findViewById(R.id.checkbox);
            //  views = view.findViewById(R.id.views);
        }
    }

}