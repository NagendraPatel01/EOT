package com.eot_app.nav_menu.jobs.job_detail.history;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.eot_app.R;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.util_interfaces.MyListItemSelected;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    final MyListItemSelected<History> mListner;
    private List<History> hisData;

    public HistoryAdapter(List<History> hisData, MyListItemSelected<History> mListner) {
        this.hisData = hisData;
        this.mListner = mListner;
    }

    public void updateRecords(List<History> hisData) {
        this.hisData = hisData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_historyfragment, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {


        History history = hisData.get(holder.getBindingAdapterPosition());
        setViewByJobStatus(holder, Integer.parseInt(history.getStatus()));

        holder.txtStatusHis.setText(history.getStatusName());
        holder.txtStatusHis1.setText(history.getStatusName());

        // for setting image for custom status
        if(history.getIsDefaultStatus()!=null&&history.getIsDefaultStatus().equalsIgnoreCase("0") && history.getImageBitmap()!=null)
        {
            holder.imageViewStatus.setImageBitmap(AppUtility.StringToBitMap(history.getImageBitmap()));
        }

        if (position % 2 == 0) {
            holder.txtStatusName.setVisibility(View.VISIBLE);
            holder.txtdatetime.setVisibility(View.VISIBLE);
            holder.txtStatusName1.setVisibility(View.GONE);
            holder.txtdatetime1.setVisibility(View.GONE);
            holder.txtStatusHis.setVisibility(View.VISIBLE);
            holder.txtStatusHis1.setVisibility(View.GONE);
            String[] formated_date = AppUtility.getFormatedTime(history.getTime());
            String frmt_dt = formated_date[0];
            String[] frmtDt = frmt_dt.split(",");
            if (formated_date != null) {
                try {
                    if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                            App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0"))
                        holder.txtdatetime.setText(frmtDt[1] + "  " + formated_date[1] + " " + formated_date[2]);
                    else holder.txtdatetime.setText(frmtDt[1] + "  " + formated_date[1] + "");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (history.getReferencebyType().equals("1")) {
                holder.txtStatusName.setText(history.getReferencebyName());
            } else {
                holder.txtStatusName.setText(history.getReferencebyName() + " (A)");
            }
        } else {
            holder.txtStatusName1.setVisibility(View.VISIBLE);
            holder.txtdatetime1.setVisibility(View.VISIBLE);
            holder.txtStatusName.setVisibility(View.GONE);
            holder.txtdatetime.setVisibility(View.GONE);
            holder.txtStatusHis1.setVisibility(View.VISIBLE);
            holder.txtStatusHis.setVisibility(View.GONE);
            holder.txtStatusName1.setText(history.getName());
            String[] formated_date = AppUtility.getFormatedTime(history.getTime());
            assert formated_date != null;
            String frmt_dt = formated_date[0];
            String[] frmtDt = frmt_dt.split(",");
            if (formated_date != null) {
                try {
                    if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null && App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0"))
                        holder.txtdatetime1.setText(frmtDt[1] + "  " + formated_date[1] + " " + formated_date[2]);
                    else holder.txtdatetime1.setText(frmtDt[1] + "  " + formated_date[1] + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (history.getReferencebyType().equals("1")) {
                holder.txtStatusName1.setText(history.getReferencebyName());
            } else {
                holder.txtStatusName1.setText(history.getReferencebyName() + " (A)");
            }
        }
    }

    private History getItem(int position) {
        return hisData.get(position);
    }

    @Override
    public int getItemCount() {
        return hisData.size();
    }

    private void setViewByJobStatus(MyViewHolder holder, int status) {
        switch (status) {
            case 1:
                holder.imageViewStatus.setImageResource(R.drawable.ic_new_task);
                break;
            case 2:
                holder.imageViewStatus.setImageResource(R.drawable.ic_accepted_task);
                break;
            case 3:
                holder.imageViewStatus.setImageResource(R.drawable.ic_rejected);
                break;
            case 4:
                holder.imageViewStatus.setImageResource(R.drawable.ic_cancel);
                break;
            case 5:
                holder.imageViewStatus.setImageResource(R.drawable.ic_travelling_small_icon);
                break;
            case 6:
                holder.imageViewStatus.setImageResource(R.drawable.ic_break);
                break;
            case 7:
                holder.imageViewStatus.setImageResource(R.drawable.in_progress);
                break;
            case 8:
                holder.imageViewStatus.setImageResource(R.drawable.breake_job_his);
                break;
            case 9:
                holder.imageViewStatus.setImageResource(R.drawable.ic_complete_task);
                break;
            case 10:
                holder.imageViewStatus.setImageResource(R.drawable.ic_closed_small_icon);
                break;
            case 12:
                holder.imageViewStatus.setImageResource(R.drawable.ic_pendng_task);
                break;

        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtStatusName, txtdatetime, txtStatusName1, txtdatetime1, txtStatusHis, txtStatusHis1;
        ImageView imageViewStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtStatusName = itemView.findViewById(R.id.txtStatusName);
            txtdatetime = itemView.findViewById(R.id.txtdatetime);
            txtStatusName1 = itemView.findViewById(R.id.txtStatusName1);
            txtdatetime1 = itemView.findViewById(R.id.txtdatetime1);
            txtStatusHis = itemView.findViewById(R.id.txtStatusHis);
            txtStatusHis1 = itemView.findViewById(R.id.txtStatusHis1);
            imageViewStatus = itemView.findViewById(R.id.imageViewStatus);
        }
    }
}
