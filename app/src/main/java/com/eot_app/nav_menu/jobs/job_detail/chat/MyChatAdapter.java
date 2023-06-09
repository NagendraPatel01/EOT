package com.eot_app.nav_menu.jobs.job_detail.chat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.eot_app.R;
import com.eot_app.nav_menu.jobs.job_detail.chat.fb_mvp.Chat_View;
import com.eot_app.nav_menu.jobs.job_detail.chat.fire_Base_Model.Chat_Send_Msg_Model;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.annotations.Nullable;

public class MyChatAdapter extends FirestoreRecyclerAdapter<Chat_Send_Msg_Model,
        MyChatAdapter.MyViewHolder> {

    final String TYPE_IMG = "image";
    String same_date = "";
    ChatFragment context;

    public MyChatAdapter(FirestoreRecyclerOptions<Chat_Send_Msg_Model> options, ChatFragment context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(final MyViewHolder holder, int position, final Chat_Send_Msg_Model model) {
        String timeFormate = AppUtility.dateTimeByAmPmFormate("dd/MMM/yyyy hh:mm a", "dd/MMM/yyyy kk:mm");

        String today_date = AppUtility.getDateByFormat(timeFormate);
        String[] today_Date = today_date.split(" ");

        String str = "", usrIDs = "", stringDate = "";

        String timeStamp = AppUtility.getDate(Long.parseLong(model.getTime()));
        String[] new_Date = timeStamp.split(" ");
        if (position != 0) {
            Chat_Send_Msg_Model chatModel = getItem(position - 1);
            String timeStamp1 = AppUtility.getDate(Long.parseLong(chatModel.getTime()));
            String[] old_Date = timeStamp1.split(" ");
            same_date = old_Date[0];
            if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                    App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0")) {
                str = old_Date[1] + " " + old_Date[2];
            } else {
                str = old_Date[1] + " ";
            }
            usrIDs = chatModel.getUsrid();
        }


        if (position == 0) {
            holder.chat_date.setVisibility(View.VISIBLE);
            if (today_Date[0].equals(new_Date[0])) {
                holder.chat_date.setText("Today");
            } else {
                holder.chat_date.setText(new_Date[0]);
            }
        } else if (!same_date.equals(new_Date[0])) {
            holder.chat_date.setVisibility(View.VISIBLE);
            if (today_Date[0].equals(new_Date[0])) {
                holder.chat_date.setText("Today");
            } else {
                holder.chat_date.setText(new_Date[0]);
            }
        } else {
            holder.chat_date.setVisibility(View.GONE);
            holder.chat_date.setText("");
        }


        /****This for current login user chat messages *****/
        if (model.getUsrid().equals(App_preference.getSharedprefInstance().getLoginRes().getUsrId())) {
            holder.sndr_layout2.setVisibility(View.VISIBLE);
            holder.rcv_layout1.setVisibility(View.GONE);
            try {

                if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                        App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0")) {
                    stringDate = new_Date[1] + " " + new_Date[2];
                } else {
                    stringDate = new_Date[1] + "";
                }
                holder.date2.setText(stringDate);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (position != 0)
                showHideTimeMsgView(usrIDs, model.getUsrid(), str, stringDate, holder.date2);


            if (model.getType().equals("1")) {
                holder.sendr_msgs.setVisibility(View.VISIBLE);
                holder.send_img.setVisibility(View.GONE);
                holder.send_all_file_layout.setVisibility(View.GONE);
                holder.sendr_msgs.setText(model.getMsg());
                Linkify.addLinks(holder.sendr_msgs, Linkify.WEB_URLS);
                holder.sendr_msgs.setLinkTextColor(Color.BLUE);
                holder.send_usrNm.setText(model.getUsrnm());

            } else if (model.getType().startsWith(TYPE_IMG) && !model.getType().contains("gif")) {
                holder.setImg_url(model.getFile());
                holder.sendr_msgs.setVisibility(View.GONE);
                holder.send_all_file_layout.setVisibility(View.GONE);
                holder.send_img.setVisibility(View.VISIBLE);
                holder.send_img.setClickable(false);
                holder.send_usrNm.setText(model.getUsrnm());
                Glide.with(context).load(model.getFile()).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.send_img.setClickable(true);
                        return false;
                    }
                }).placeholder(R.drawable.ic_gallery)
                        .centerCrop()
                        .override(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        .into(holder.send_img);
            } else {
                holder.send_all_file_layout.setVisibility(View.VISIBLE);
                holder.send_img.setVisibility(View.GONE);
                holder.sendr_msgs.setVisibility(View.GONE);
                holder.all_types_file_send.setText("Open File");//model.getFile()
                holder.send_usrNm.setText(model.getUsrnm());

                holder.send_all_file_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.openUriOnBroWser(model.getFile(), model.getType());
                    }
                });

            }

        } else {
            holder.sndr_layout2.setVisibility(View.GONE);
            holder.rcv_layout1.setVisibility(View.VISIBLE);


            try {
                if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                        App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0")) {
                    stringDate = new_Date[1] + " " + new_Date[2];
                } else {
                    stringDate = new_Date[1] + "";
                }
                holder.date1.setText(stringDate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (position != 0)
                showHideTimeMsgView(usrIDs, model.getUsrid(), str, stringDate, holder.date1);


            if (model.getType().equals("1")) {
                holder.recv_msgs.setVisibility(View.VISIBLE);
                holder.rcv_img.setVisibility(View.GONE);
                holder.rcv_all_file_layout.setVisibility(View.GONE);
                holder.recv_msgs.setText(model.getMsg());
                Linkify.addLinks(holder.recv_msgs, Linkify.WEB_URLS);
                holder.recv_msgs.setLinkTextColor(Color.BLUE);
                holder.rev_usrNm.setText(model.getUsrnm());
            } else if (model.getType().startsWith(TYPE_IMG) && !model.getType().contains("gif")) {
                holder.setImg_url(model.getFile());
                holder.recv_msgs.setVisibility(View.GONE);
                holder.rcv_all_file_layout.setVisibility(View.GONE);
                holder.rcv_img.setVisibility(View.VISIBLE);
                holder.rcv_img.setClickable(false);
                holder.rev_usrNm.setText(model.getUsrnm());
                Glide.with(context).load(model.getFile()).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.rcv_img.setClickable(true);
                        return false;
                    }
                })
                        .placeholder(R.drawable.ic_gallery)
                        .centerCrop()
                        .override(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)//220
                        .into(holder.rcv_img);


            } else {
                holder.rcv_all_file_layout.setVisibility(View.VISIBLE);
                holder.rcv_img.setVisibility(View.GONE);
                holder.recv_msgs.setVisibility(View.GONE);
                holder.all_types_file_rcv.setText("Open File");
                holder.rev_usrNm.setText(model.getUsrnm());

                holder.rcv_all_file_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.openUriOnBroWser(model.getFile(), model.getType());
                    }
                });
            }
        }
    }

    private void showHideTimeMsgView(String usrIDs, String usrid, String str, String stringDate, TextView date1) {
        try {
            if (usrIDs.equals(usrid)) {
                if (!str.equals(stringDate)) {
                    date1.setVisibility(View.VISIBLE);
                } else {
                    date1.setVisibility(View.GONE);
                }
            } else {
                date1.setVisibility(View.VISIBLE);

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chat_adpter_layout, viewGroup, false);
        return new MyViewHolder(view, context);
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView recv_msgs;
        private final TextView sendr_msgs;
        private final TextView chat_date;
        private final TextView date2;
        private final TextView date1;
        private final TextView rev_usrNm;
        private final TextView send_usrNm;
        private final LinearLayout rcv_layout1;
        private final LinearLayout sndr_layout2;
        private final ImageView send_img;
        private final ImageView rcv_img;
        private final Chat_View mlistener;
        private final TextView all_types_file_send;
        private final TextView all_types_file_rcv;
        private final LinearLayout send_all_file_layout;
        private final LinearLayout rcv_all_file_layout;
        private String img_url = "";

        public MyViewHolder(@NonNull View itemView, Chat_View mlistener) {
            super(itemView);
            this.mlistener = mlistener;
            recv_msgs = itemView.findViewById(R.id.recv_msgs);
            sendr_msgs = itemView.findViewById(R.id.sendr_msgs);

            rcv_layout1 = itemView.findViewById(R.id.rcv_layout1);
            sndr_layout2 = itemView.findViewById(R.id.sndr_layout2);

            chat_date = itemView.findViewById(R.id.chat_date);

            date2 = itemView.findViewById(R.id.time2);
            date1 = itemView.findViewById(R.id.time1);

            send_img = itemView.findViewById(R.id.send_img);
            rcv_img = itemView.findViewById(R.id.rcv_img);

            send_img.setOnClickListener(this);
            rcv_img.setOnClickListener(this);

            rev_usrNm = itemView.findViewById(R.id.rev_usrNm);
            send_usrNm = itemView.findViewById(R.id.send_usrNm);

            all_types_file_rcv = itemView.findViewById(R.id.all_types_file_rcv);
            all_types_file_send = itemView.findViewById(R.id.all_types_file_send);

            send_all_file_layout = itemView.findViewById(R.id.send_all_file_layout);
            rcv_all_file_layout = itemView.findViewById(R.id.rcv_all_file_layout);


        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.send_img:
//                    mlistener.openImage(v, ((ImageView) v).getDrawable(), getImg_url());
//                    break;
                case R.id.rcv_img:
                    mlistener.openImage(v, ((ImageView) v).getDrawable(), getImg_url());
                    break;
                case R.id.all_types_file_send:
                    // context.openUriOnBroWser();
                    break;
                case R.id.all_types_file_rcv:
                    break;
            }


        }


    }
}
