package com.eot_app.nav_menu.appointment.details;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.eot_app.R;
import com.eot_app.nav_menu.jobs.job_detail.documents.DialogUpdateDocuments;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AttachementAdapter extends RecyclerView.Adapter<AttachementAdapter.MyViewHolder> {
    Context context;
    OnItemSelection onItemSelection;
    private List<AppointmentAttachment> list;
    private boolean isFromClientHistory=false;

    public void setFromClientHistory(boolean fromClientHistory) {
        isFromClientHistory = fromClientHistory;
    }

    public AttachementAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemSelection(OnItemSelection onItemSelection) {
        this.onItemSelection = onItemSelection;
    }

    public void setSingleAttachment(List<AppointmentAttachment> list) {
        try {
            if (this.list == null) this.list = new ArrayList<>();
            this.list.addAll(list);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<AppointmentAttachment> getList() {
        return list;
    }

    public void setList(List<AppointmentAttachment> list) {
        this.list = list;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_appointment_attachement, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String ext = "";

        final AppointmentAttachment fileList = list.get(position);
        final File file = convertToFile(fileList.getAttachFileActualName());
        if (fileList.getType() == 1) {
            if (file != null)
                holder.file_name.setText(file.getName());
        } else
            holder.file_name.setText(fileList.getAttachFileActualName());
        try {
            ext = fileList.getAttachFileActualName().substring((fileList.getAttachFileActualName().lastIndexOf(".")) + 1).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!ext.isEmpty()) {
            switch (ext) {
                case "jpg":
                case "jpeg":
                case "png":
                    if (fileList.getType() == 1) {
                        if (file != null && file.exists()) {
                            Glide.with(context).load(file.getPath())
                                    .thumbnail(Glide.with(context).load(R.raw.loader_eot))
                                    .placeholder(R.drawable.picture).into(holder.image_thumb_nail);
                        }
                    } else {

                        if (fileList.getAttachmentId().equalsIgnoreCase("0") && fileList.getBitmap() != null) {
                            try {
                                Bitmap bitmap1 = AppUtility.StringToBitMap(fileList.getBitmap());
                                holder.image_thumb_nail.setImageBitmap(bitmap1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Glide.with(context).load(App_preference.getSharedprefInstance().getBaseURL() + fileList.getAttachThumnailFileName())
                                    .format(DecodeFormat.PREFER_ARGB_8888).thumbnail(Glide.with(context).load(R.raw.loader_eot)).placeholder(R.drawable.picture).into(holder.image_thumb_nail);
                        }

                    }
                    holder.image_thumb_nail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case "doc":
                case "docx":
                    holder.image_thumb_nail.setImageResource(R.drawable.word);
                    holder.image_thumb_nail.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    break;
                case "pdf":
                    holder.image_thumb_nail.setImageResource(R.drawable.pdf);
                    holder.image_thumb_nail.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    break;
                case "xlsx":
                case "xls":
                    holder.image_thumb_nail.setImageResource(R.drawable.excel);
                    holder.image_thumb_nail.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    break;
                case "csv":
                    holder.image_thumb_nail.setImageResource(R.drawable.csv);
                    holder.image_thumb_nail.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
                default:
                    holder.image_thumb_nail.setImageResource(R.drawable.doc);
                    holder.image_thumb_nail.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
            }
            if(fileList.getAttachmentId().equalsIgnoreCase("0"))
            {
                holder.image_loader.setVisibility(View.VISIBLE);
                Glide.with(context).load("")
                        .thumbnail(Glide.with(context).load(R.raw.loader_eot)).into(holder.image_loader);
            }
            else {
                holder.image_loader.setVisibility(View.GONE);
            }
        }

        holder.layout_doc.setOnClickListener(view -> {

            DialogUpdateDocuments
                    dialogUpdateDocuments = new DialogUpdateDocuments();
            dialogUpdateDocuments.setFromClientHistory(isFromClientHistory);
            try {
                String ext1 = list.get(position).getAttachFileActualName().substring((list.get(position).getAttachFileActualName().lastIndexOf(".")) + 1);
                if (!TextUtils.isEmpty(ext1) && ext1.equals("jpg") || ext1.equals("jpeg") || ext1.equals("png"))
                    dialogUpdateDocuments.setIsFileImage(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialogUpdateDocuments.setImgPath(
                    list.get(position).getAttachmentId(),
                    list.get(position).getAttachFileName(),
                    list.get(position).getAttachFileActualName(),
                    list.get(position).getDes(),"","");

            dialogUpdateDocuments.setOnDocumentUpdate(desc -> {
                if (desc != null)
                    list.get(position).setDes(desc);
            });
            dialogUpdateDocuments.show(((AppCompatActivity) context).getSupportFragmentManager(), "dialog");
        });

        if(isFromClientHistory)
            holder.checkbox.setVisibility(View.GONE);
        else
            holder.checkbox.setVisibility(View.VISIBLE);

        holder.checkbox.setChecked(fileList.isSelected());

        holder.checkbox.setOnClickListener(v -> {
            if (isFileImage(list.get(position).getAttachFileActualName())) {
                list.get(position).setSelected(!list.get(position).isSelected());
                holder.checkbox.setChecked(list.get(position).isSelected());
                if (onItemSelection != null) {
                    onItemSelection.showExportOption(isSelected());
                }
            } else {
                holder.checkbox.setChecked(false);
                if (onItemSelection != null) onItemSelection.showDocFormatMSG();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private File convertToFile(String path) {
        File file = null;
        if (path != null) {
            try {
                file = new File(path);
                return file;
            } catch (Exception ex) {
                 ex.printStackTrace();
            }
        }
        return file;
    }

    private boolean isSelected() {
        if (getList() == null || getList().size() == 0) return false;

        boolean isAllSelected = true;
        boolean isSelected = false;

        for (AppointmentAttachment at : getList())
            if (isFileImage(at.getAttachFileActualName()))
                if (at.isSelected()) {
                    isSelected = true;
                } else isAllSelected = false;

        if (onItemSelection != null) onItemSelection.selectAllOption(isAllSelected);

        return isSelected;

    }

    public void selectAllFiles(boolean b) {
        if (getList() != null && getList().size() > 0)
            for (AppointmentAttachment at : getList()) {
                if (isFileImage(at.getAttachFileActualName()))
                    at.setSelected(b);
            }
        notifyDataSetChanged();
    }

    private boolean isFileImage(String fileName) {
        String ext = null;
        boolean fileFormatImage = false;
        try {
            ext = fileName.substring((fileName.lastIndexOf(".")) + 1);
            if (!ext.isEmpty())
                if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("png")) {
                    fileFormatImage = true;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileFormatImage;
    }

    public interface OnItemSelection {
        void showExportOption(boolean b);

        void selectAllOption(boolean b);

        void showDocFormatMSG();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView file_name;
        LinearLayout layout_doc;
        ImageView image_thumb_nail;
        ImageView image_loader;
        AppCompatCheckBox checkbox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.tv_file_name);
            layout_doc = itemView.findViewById(R.id.layout_doc);
            image_thumb_nail = itemView.findViewById(R.id.image_thumb_nail);
            image_loader = itemView.findViewById(R.id.image_loader);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}
