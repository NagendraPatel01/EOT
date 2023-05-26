package com.eot_app.nav_menu.addlead;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eot_app.R;



import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class LeadHeaderAdapter extends RecyclerView.Adapter<LeadHeaderAdapter.MyViewHolder> {
    private List<AddLeadResModel> list;
    Context context;
    onLeadClick onLeadClick;

    public void setList(List<AddLeadResModel> list) {
        if (list != null) {
            this.list = list;
            notifyDataSetChanged();
        }
    }

    public LeadHeaderAdapter(Context context,onLeadClick onLeadClick) {
        this.context = context;
        this.onLeadClick = onLeadClick;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lead_form_header, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.tvHeader.setText(list.get(position).getAfName());

        if(list.get(position).isSelected()){
            holder.tvHeader.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));

            holder.tvHeader.setBackground(ContextCompat.getDrawable(context,R.drawable.lead_header));
        }
        else {
            holder.tvHeader.setTextColor(ContextCompat.getColor(context,R.color.txt_color));

            holder.tvHeader.setBackground(ContextCompat.getDrawable(context,R.drawable.white_bg));
        }

        holder.tvHeader.setOnClickListener(v -> {
            if(!list.get(position).isSelected()){
                list.get(position).setSelected(true);
                for (int i = 0; i <list.size() ; i++) {
                   if(i!=position)
                       list.get(i).setSelected(false);
                }
                onLeadClick.onLeadClickEvent(list.get(position).getAfId());
            }
            notifyDataSetChanged();

        });



    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);

        }
    }
    public  interface onLeadClick{
         void onLeadClickEvent(String afId);
    }



}
