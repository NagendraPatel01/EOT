package com.eot_app.nav_menu.addlead;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.eot_app.R;
import com.eot_app.custom_dropDown.CustomDPControllerLead;
import com.eot_app.custom_dropDown.ServiceInterface;
import com.eot_app.eoteditor.CustomEditor;
import com.eot_app.nav_menu.addlead.add_lead_presenter.Add_lead_pi;
import com.eot_app.nav_menu.addlead.model.AddLeadPost;
import com.eot_app.nav_menu.client.client_db.Client;
import com.eot_app.nav_menu.jobs.add_job.add_job_recr.daily_recr_pkg.CustomWeekSelector;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.MyFormInterFace;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.qus_model.AnswerModel;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.settings.setting_db.JobTitle;
import com.eot_app.utility.settings.setting_db.Suggestion;
import com.eot_app.utility.settings.setting_db.TagData;
import com.eot_app.utility.util_interfaces.NoDefaultSpinner;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by shivani on 12/4/22.
 */


public class FieldListAdapter extends RecyclerView.Adapter<FieldListAdapter.ViewHolder> {
    private final ArrayList<AfJsonResModel> typeList;
    final ArrayList<AfJsonResModel> duplicateCopy = new ArrayList<>();
    private final Context context;
    MyFormInterFace myFormInterFace;
    String time = "", date;
    ArrayList<JobTitle> datastr;
    List<Client> clientList;
    private String[] id_array;
    String date_str, time_str = "", date_en, time_en = "";
    Add_lead_pi addLead_pc;
    final Set<String> jtIdList = new HashSet<>();
    List<Suggestion> suggestions = new ArrayList<>();
    String[] suggestionsArray = new String[suggestions.size()];
    MyAttachmentLead myAttachment;
    String contrId = "";
    public AddLeadPost addLeadPost;
    Set<String> jobServiceNm = new HashSet<>();
    AddLeadFragment addLeadFragment;
    private final int MAIN_ITEM_VIEW = 0;
    int FORM_LIST_VIEW = 1;
    int SCHEDULE_VIEW = 2;

    /*****this for Fragment***/
    public FieldListAdapter(ArrayList<AfJsonResModel> typeList, Context context,
                            MyFormInterFace myFormInterFace,
                            Add_lead_pi addLead_pc,
                            ArrayList<JobTitle> datastr,
                            List<Client> clientList,
                            MyAttachmentLead myAttachment,
                            AddLeadPost addLeadPost, AddLeadFragment addLeadFragment) {
        this.context = context;
        this.typeList = typeList;
        this.datastr = datastr;
        this.clientList = clientList;
        this.addLeadPost = addLeadPost;
        this.myAttachment = myAttachment;
        if (this.typeList != null)
            duplicateCopy.addAll(typeList);
        this.myFormInterFace = myFormInterFace;
        this.addLead_pc = addLead_pc;
        this.addLeadFragment = addLeadFragment;
        String currentDateTime = AppUtility.getDateByFormat(AppUtility.dateTimeByAmPmFormate(
                "dd-MMM-yyyy hh:mm a", "dd-MMM-yyyy kk:mm"));
        String[] currentDateTimeArry = currentDateTime.split(" ");
        date = currentDateTimeArry[0];

        try {
            if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                    App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0"))
                time = currentDateTimeArry[1] + " " + currentDateTimeArry[2];
            else time = currentDateTimeArry[1] + "";

        } catch (Exception e) {
            e.printStackTrace();
        }
        setIndex();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDatastr(ArrayList<JobTitle> datastr) {
        this.datastr.clear();
        this.datastr.addAll(datastr);
        notifyDataSetChanged();
    }

    private void setIndex() {
        if (typeList != null && typeList.size() > 0) {
            int i = 0;
            for (AfJsonResModel custOmFormQuestionsRes : typeList) {
                if (!custOmFormQuestionsRes.getType().equals("9")) {
                    i++;
                    custOmFormQuestionsRes.setIndex(i);
                } else custOmFormQuestionsRes.setIndex(0);
            }
        }
    }

    public ArrayList<AfJsonResModel> getTypeList() {
        return typeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MAIN_ITEM_VIEW) {
            View view = inflater.inflate(R.layout.add_lead_feild_list, parent, false);
            return new ViewHolder(view, context);
        } else if (viewType == SCHEDULE_VIEW) {
            View view = inflater.inflate(R.layout.shedule_start_end_layout, parent, false);
            return new ViewHolder(view, context);
        } else {
            View view = inflater.inflate(R.layout.question_list_layout, parent, false);
            return new ViewHolder(view, context);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (typeList.get(position).getFormControlName().equalsIgnoreCase("schdl"))
            return SCHEDULE_VIEW;
        else if (typeList.get(position).getType().isEmpty())
            return MAIN_ITEM_VIEW;
        else return FORM_LIST_VIEW;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if ((typeList.get(position).getIndex() > 0)) {
            //count++;
            holder.que_no.setVisibility(View.VISIBLE);
            holder.que_no.setText(typeList.get(position).getIndex() + ".");
        } else {
            holder.que_no.setText("");
            holder.que_no.setVisibility(View.GONE);
        }

        holder.tvQuestion.setVisibility(View.VISIBLE);

        if (typeList.get(position).getMand()) {
            holder.tvQuestion.setText(typeList.get(position).getFieldName() + " *");
        } else {
            holder.tvQuestion.setText(typeList.get(position).getFieldName());
        }

        try {
            holder.tvQuestion.setTypeface(holder.tvQuestion.getTypeface(), Typeface.NORMAL);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (typeList.get(position).getType().isEmpty() &&
                typeList.get(position).getFormControlName().equalsIgnoreCase("schdl")) {
            // default question list
            /*Schedule start & end time***/
            addLeadFragment.getCurrentdateTime(holder);
            addLeadFragment.getTimeShiftList(holder);

            holder.date_time_clear_btn.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.clear));
            holder.date_time_clear_btn.setOnClickListener(v -> addLeadFragment.setDateTimeEmptyTime(holder));

            // recur
            holder.msg_pattern_view.setOnClickListener(v -> addLeadFragment.jobCustomRecurPattenrn());


            holder.add_recur_checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (date_str.equals("")) {
                        holder.add_recur_checkBox.setChecked(false);
                        addLeadFragment.showErrorDialog(LanguageController.getInstance().getMobileMsgByKey(AppConstant.sch_time_required));
                    } else
                        addLeadFragment.jobWeeklyRecur(holder);
                } else {
                    holder.normal_weekly_recur.setVisibility(View.GONE);
                    holder.recur_view.setVisibility(View.GONE);
                    holder.recur_pattern_view.setVisibility(View.GONE);
                    addLeadFragment.setJobWeeklyRecurView(holder);
                }
            });
            holder.radio_on.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    holder.radio_on.setChecked(true);
                    holder.radio_never_end.setChecked(false);
                    holder.end_date_for_weekly_recur.setBackgroundResource(R.drawable.edittext_shap_qus);
                    holder.end_date_for_weekly_recur.setClickable(true);
                    addLeadFragment.jobWeeklyRecur(holder);
                } else {
                    holder.radio_on.setChecked(false);
                }
            });
            holder.radio_never_end.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    holder.radio_never_end.setChecked(true);
                    holder.radio_on.setChecked(false);
                    holder.end_date_for_weekly_recur.setText("");
                    holder.end_date_for_weekly_recur.setBackgroundResource(R.drawable.layout_disable);
                    holder.end_date_for_weekly_recur.setText("");
                    holder.end_date_for_weekly_recur.setClickable(false);
                    addLeadFragment.endDate = "";
                    addLeadFragment.endRecurMode = "0";
                } else {
                    holder.radio_never_end.setChecked(false);
                }
            });
            holder.end_date_for_weekly_recur.setOnClickListener(v -> addLeadFragment.selectRecurtillDate(holder));

            holder.custom_recur_pattern.setOnClickListener(v -> {
                if (LanguageController.getInstance().getMobileMsgByKey(AppConstant.normal_recur).equals(holder.custom_recur_pattern.getText().toString())) {
                    holder.msg_pattern_view.setVisibility(View.GONE);
                    holder.custom_recur_pattern.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.custom_recur_pattern));
                    holder.normal_weekly_recur.setVisibility(View.VISIBLE);
                    addLeadFragment.jobWeeklyRecur(holder);
                } else {
                    addLeadFragment.jobCustomRecurPattenrn();
                }
            });

            holder.mon_week_day.setOnClickListener(v -> addLeadFragment.mon_week_day_click(holder));
            holder.tu_week_day.setOnClickListener(v -> addLeadFragment.tu_week_day_click(holder));
            holder.wed_week_day.setOnClickListener(v -> addLeadFragment.wed_week_day_click(holder));
            holder.thu_week_day.setOnClickListener(v -> addLeadFragment.thu_week_day_click(holder));
            holder.fri_week_day.setOnClickListener(v -> addLeadFragment.fri_week_day_click(holder));
            holder.sat_week_day.setOnClickListener(v -> addLeadFragment.sat_week_day_click(holder));
            holder.sun_week_day.setOnClickListener(v -> addLeadFragment.sun_week_day_click(holder));


            holder.add_recur_checkBox.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_rucr_for_job));
            holder.recur_not_work.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.dont_create_recur) + " ");
            holder.custom_recur_pattern.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.custom_recur_pattern));

            holder.radio_on.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.radio_end_by));
            holder.radio_never_end.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.never_end));

//ToDO
            /* permission for Add recur***/
//            if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsRecur().equals("0")) {
//                holder.recur_parent_view.setVisibility(View.VISIBLE);
//            } else {
//                holder.recur_parent_view.setVisibility(View.GONE);
//            }
            // recur end


            holder.switch_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (addLeadFragment.shiftList != null && addLeadFragment.shiftList.size() > 1) {
                        holder.shift_time_txt.setVisibility(View.VISIBLE);
                    } else {
                        holder.shift_time_txt.setVisibility(View.GONE);
                        if (addLeadFragment.shiftList.size() > 0) {
                            addLeadFragment.setDataInDateTimeField(addLeadFragment.shiftList.get(0), holder);
                        }
                    }
                } else {
                    holder.shift_time_txt.setVisibility(View.GONE);

                    addLead_pc.getCurrentdateTime(holder);
                }
            });
            holder.shift_time_txt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.all_day_leave));
            holder.shift_time_txt.setOnClickListener(v -> holder.time_shift_dp.performClick());
            holder.time_end.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.time_form));
            holder.time_start.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.time_form));
            holder.date_end.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.date_form));
            holder.date_start.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.date_form));
            holder.schel_end.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.shdl_end));
            holder.schel_start.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.shdl_start));

            holder.date_start.setOnClickListener(v -> addLeadFragment.selectStartDate(holder));
            holder.time_start.setOnClickListener(v -> addLeadFragment.selectStartTime(holder));
            holder.date_end.setOnClickListener(v -> addLeadFragment.selectEndDate(holder));
            holder.time_end.setOnClickListener(v -> addLeadFragment.selectEndTime(holder));


            holder.ll_start_end_date.setVisibility(View.VISIBLE);
            if (addLeadPost.getLeadStartDate() != null && !addLeadPost.getLeadStartDate().isEmpty()) {
                date_str = addLeadPost.getLeadStartDate().split(" ")[0];
                time_str = addLeadPost.getLeadStartDate().split(" ")[1];
                holder.date_start.setText(date_str);
                holder.time_start.setText(time_str);
            }
            if (addLeadPost.getLeadEndDate() != null && !addLeadPost.getLeadEndDate().isEmpty()) {
                date_en = addLeadPost.getLeadEndDate().split(" ")[0];
                time_en = addLeadPost.getLeadEndDate().split(" ")[1];
                holder.date_end.setText(date_en);
                holder.time_end.setText(time_en);
            }

        } else if (typeList.get(position).getType().isEmpty() &&
                !typeList.get(position).getFormControlName().equalsIgnoreCase("schdl")) {
            // default question list
            holder.rl_job_service.setVisibility(View.GONE);
//            holder.ll_start_end_date.setVisibility(View.GONE);
            holder.rl_refrences.setVisibility(View.GONE);
            holder.rl_des.setVisibility(View.GONE);
            holder.rl_client.setVisibility(View.GONE);
            holder.ll_client_details.setVisibility(View.GONE);

            switch (typeList.get(position).getFormControlName()) {
                /*tag service */
                case "tag":
                    addLeadFragment.getTagList(holder);
                    holder.add_tag_btn.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add));
                    holder.tag_lable.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.tag));
                    holder.et_tag.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_tag));

                    holder.add_tag_btn.setOnClickListener(v -> {
                        String tagName = holder.et_tag.getText().toString();
                        if (tagName.length() > 0) {
                            final TagData tagdata = new TagData();
                            tagdata.setTagId("");
                            tagdata.setTnm(tagName);
                            addLeadFragment.addChipsForTags(tagdata,holder);
                            holder.et_tag.setText("");
                        }
                    });
                    holder.et_tag.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            addLeadFragment.etTagTextWatcher(charSequence.toString(),holder);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    holder.tag_lable.setOnClickListener(v -> holder.et_tag.showDropDown());

                    break;
                case "assign_to":
                    addLead_pc.getWorkerList(holder);
                    holder.assign_to.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.assign_to));
                    holder.members.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_fieldworker));

                    holder.assignto_linear.setOnClickListener(v -> holder.members.showDropDown());
                    break;

                /*Job type service */
                case "jtId":

                    holder.jobservicetxtlableset.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.job_title) + " *");
                    id_array = new String[datastr.size()];
                    holder.jobservicelayout.setOnClickListener(v -> {
                        if (id_array.length > 0) {
                            holder.customDPController.showSpinnerDropDown(context, holder.jobservicelayout, datastr);
                        } else {
                            AppUtility.alertDialog(context, "", LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_empty_Title), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
                        }
                    });
                    if (!jtIdList.isEmpty()) {
                        if (jobServiceNm.size() >= 4)
                            holder.jobservicetxtlableset.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.items_selected) + " " + jobServiceNm.size());
                        else
                            holder.jobservicetxtlableset.setText(jobServiceNm.toString().replace("[", "").replace("]", ""));
                    }
                    holder.rl_job_service.setVisibility(View.VISIBLE);
                    holder.jobservicetxtlableset.setTag(position);
                    break;

                /*Reference***/
                case "refId":
                    // add refrences
                    addLeadFragment.setRefrences(holder);
                    holder.linearLayout_reference.setOnClickListener(v -> holder.referenceDp.performClick());

                    holder.tv_spinner_reference.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.reference));

                    holder.rl_refrences.setVisibility(View.VISIBLE);
                    holder.tv_spinner_reference.setTag(position);
                    if (!addLeadPost.getReferenceName().isEmpty()) {
                        holder.tv_spinner_reference.setText(addLeadPost.getReferenceName());
                    }
                    break;
                /*Client Name****/
                case "nm":


                    /*Hide lat & lng filed when permission deny****/
                    if (App_preference.getSharedprefInstance().getCompanySettingsDetails().getIsJobLatLngEnable().equals("0")) {
                        holder.lng_view.setVisibility(View.GONE);
                        holder.lat_view.setVisibility(View.GONE);
                        holder.job_lat_layout.setVisibility(View.GONE);
                        holder.job_lng_layout.setVisibility(View.GONE);
                        holder.lat_lng_view_lay.setVisibility(View.GONE);

                    }
                    holder.lat_lng_view_lay.setOnClickListener(v -> addLeadFragment.getLocation(holder));

                    /*hide/show landmark field***/
                    if (App_preference.getSharedprefInstance().getCompanySettingsDetails().getIsLandmarkEnable().equals("0")) {
                        holder.landmark_layout.setVisibility(View.GONE);
                        holder.landmark_view.setVisibility(View.GONE);
                    }
                    //get country list
                    addLeadFragment.getCountryList(holder);
                    // get client list
                    addLeadFragment.setClientDataList(holder);
                    // set default country data
//                addLeadFragment.setCompanySettingAdrs(holder);
                    Log.e("AddLeadFragment", "addLeadPost Adapter::" + new Gson().toJson(addLeadPost));

                    holder.rl_client.setVisibility(View.VISIBLE);
                    if (!addLeadPost.getCnm().isEmpty()) {
                        holder.auto_client.setText(addLeadPost.getCnm());
                        holder.ll_client_details.setVisibility(View.VISIBLE);

                    }

                    holder.auto_client.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.client_mand) + " *");
                    holder.cb_future_client.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.save_for_future_use));
                    holder.cb_future_client.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (!addLeadPost.getCltId().isEmpty() &&
                                !addLeadPost.getCltId().equals("0") && isChecked) {
                            addLeadPost.setClientForFuture(1);
                            addLeadPost.setContactForFuture(1);
                            addLeadPost.setSiteForFuture(1);
                        } else {
                            addLeadPost.setClientForFuture(0);
                            addLeadPost.setContactForFuture(0);
                            addLeadPost.setSiteForFuture(0);
                        }
                    });
                    holder.site_add_edt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_new_site));
                    holder.contact_add_edt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_contact_name));
                    holder.landmark_edt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.landmark_addjob));

                    holder.edt_lat.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.latitude));

                    holder.edt_lng.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.longitued));

                    holder.post_code.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.postal_code));

                    holder.mob_no.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.mob_no));

                    holder.at_mob.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.alt_mobile_number));

                    holder.email.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.client_email));

                    holder.adderes.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.address) + " *");

                    holder.city.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.city));

                    holder.auto_contact.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.contact_name));
                    holder.site_add_edt.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_new_site));
                    holder.cb_future_contact.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add));
                    holder.cb_future_sites.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_new_site));
                    holder.site_dp_img.setOnClickListener(v -> holder.auto_sites.showDropDown());
                    holder.input_layout_site.setOnClickListener(v -> holder.auto_sites.showDropDown());
                    holder.input_layout_contact.setOnClickListener(v -> holder.auto_contact.showDropDown());
                    holder.contact_dp_img.setOnClickListener(v -> holder.auto_contact.showDropDown());
                    holder.auto_country.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.country) + " *");
                    holder.auto_sites.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.site_name));
                    holder.auto_states.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.state) + " *");
                    holder.contarct_lable.setOnClickListener(v -> holder.contractSpinner.performClick());

                    holder.contract_dp_img.setOnClickListener(v -> holder.contractSpinner.performClick());

                    holder.contract_cross_img.setOnClickListener(v -> {
                        /*
                         * Clear Selected Contract Data
                         ****/
                        holder.contract_hint_lable.setVisibility(View.GONE);
                        holder.contarct_lable.setText("");
                        holder.contarct_lable.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.contract));
                        holder.contract_cross_img.setVisibility(View.GONE);
                        contrId = "";
                    });

                    break;
                /*Description****/
                case "desc":
                    holder.action_insert_image.setOnClickListener(v -> {
                        holder.mEditor.focusEditor();
                        if (myAttachment != null)
                            myAttachment.selectFileWithoutAttachment(position, true, holder.mEditor);
                    });
                    holder.mEditor.setPlaceholder(LanguageController.getInstance().getMobileMsgByKey(AppConstant.job_desc));
                    holder.mEditor.setTextColor(Color.parseColor("#8C9293"));
                    holder.mEditor.setOnTextChangeListener(text -> addLeadPost.setDes(text));

                    holder.mEditor.setBackgroundColor(Color.TRANSPARENT);
                    holder.mEditor.focusEditor();

                    holder.rl_des.setVisibility(View.VISIBLE);
                    if (addLeadPost.getDes() != null && !addLeadPost.getDes().isEmpty()) {
                        holder.mEditor.setHtml(addLeadPost.getDes());
                    }
                    break;
            }
        } else {

            // custom question list
            holder.type_text.setVisibility(View.GONE);
            holder.type_text_area.setVisibility(View.GONE);
            holder.linearCheck.setVisibility(View.GONE);
            holder.linearSpinner.setVisibility(View.GONE);
            holder.linearDate.setVisibility(View.GONE);
            holder.linearTime.setVisibility(View.GONE);
            holder.linearDateTime.setVisibility(View.GONE);
            holder.checkbox_single.setVisibility(View.GONE);

            final Calendar myCalendar = Calendar.getInstance();
            /* Get Date from picker call back***/
            @SuppressLint("SimpleDateFormat") final DatePickerDialog.OnDateSetListener datePicker = (view, year, monthOfYear, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                /*type 5 for Time Type Question**/
                if (view.getTag().equals("DateType5")) {

                    String month_String = String.valueOf((monthOfYear + 1));
                    String completeDate;
                    if (month_String.length() == 1) {
                        month_String = "0" + month_String;
                        completeDate = dayOfMonth + "-" + month_String + "-" + year;
                        // tvDate.setText(dayOfMonth + "-" + month_String + "-" + year + " ");
                    } else
                        completeDate = dayOfMonth + "-" + month_String + "-" + year;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date parse = sdf.parse(completeDate.trim());
                        assert parse != null;
                        holder.tvDate.setText(new SimpleDateFormat("dd-MMM-yyyy").format(parse));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                /*type 7 for DateTime type Question***/
                else if (view.getTag().equals("DateType7")) {
                    String monthString = String.valueOf((monthOfYear + 1));
                    if (monthString.length() == 1) {
                        monthString = "0" + monthString;
                    }
                    date = "";
                    date = (dayOfMonth + "-" + monthString + "-" + year + " ");
                    String newDateTime = date + " " + time;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(
                            AppUtility.dateTimeByAmPmFormate("dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm"));
                    try {
                        Date parseDate = sdf.parse(newDateTime);
                        assert parseDate != null;
                        holder.tvTimeDate.setText(new SimpleDateFormat(AppUtility.dateTimeByAmPmFormate(
                                "dd-MMM-yyyy hh:mm a",
                                "dd-MMM-yyyy kk:mm")).format(parseDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            };

            /*initialize Date picker***/
            final DatePickerDialog datePickerDialog = new DatePickerDialog(context, datePicker, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));


            switch (typeList.get(position).getType()) {
                /*type 1 for text type quest*/
                case "1":
                    holder.type_text.setVisibility(View.VISIBLE);
                    holder.type_text.setTag(position);
                    holder.type_text.setTag(position);
                    holder.type_text.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_your_ans));

                    holder.type_text.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (holder.type_text.getTag() != null) {
                                String pos = holder.type_text.getTag().toString();
                                int position = Integer.parseInt(pos);
                                List<AnswerModel> ans = typeList.get(position).getAns();
                                if (ans != null && ans.size() > 0) {
                                    ans.set(0, new AnswerModel(ans.get(0).getKey(), s.toString()));
                                    typeList.get(position).setAns(ans);
                                } else {
                                    List<AnswerModel> answerModels = new ArrayList<>();
                                    answerModels.add(new AnswerModel("0", s.toString()));
                                    typeList.get(position).setAns(answerModels);
                                }

                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    // set ans if filled
                    if (typeList.get(position).getAns().isEmpty())
                        holder.type_text.setText("");
                    else if (typeList.get(position).getAns().size() > 0)
                        holder.type_text.setText(typeList.get(position).getAns().get(0).getValue());

                    break;

                /*TextArea***/
                case "2":

                    holder.type_text_area.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_your_ans));
                    holder.type_text_area.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (holder.type_text_area.getTag() != null) {
                                String pos = holder.type_text_area.getTag().toString();
                                int position = Integer.parseInt(pos);

                                List<AnswerModel> ans = typeList.get(position).getAns();
                                if (ans != null && ans.size() > 0) {
                                    ans.set(0, new AnswerModel(ans.get(0).getKey(), s.toString()));
                                    typeList.get(position).setAns(ans);
                                } else {
                                    List<AnswerModel> answerModels = new ArrayList<>();
                                    answerModels.add(new AnswerModel("0", s.toString()));
                                    typeList.get(position).setAns(answerModels);
                                }

                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    holder.type_text_area.setVisibility(View.VISIBLE);
                    holder.type_text_area.setTag(position);
                    // set ans if filled
                    if (typeList.get(position).getAns().isEmpty())
                        holder.type_text_area.setText("");
                    else if (typeList.get(position).getAns().size() > 0)
                        holder.type_text_area.setText(typeList.get(position).getAns().get(0).getValue());

                    break;


                /*checkbox type***/
                case "3":
                    holder.linearCheck.setVisibility(View.VISIBLE);
                    addLeadFragment.setCheckBoxOption(holder, position, typeList);
                    break;
                /*dropdown type****/
                case "4":
                    holder.spinner_text.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.select_option));
                    holder.linearSpinner.setOnClickListener(view -> {
                        holder.spinner.performClick();
                        Log.e("TAG", "THAG");
                    });
                    holder.linearSpinner.setVisibility(View.VISIBLE);
                    addLeadFragment.setDropDownOptions(holder, position, typeList);
                    break;
                /*date type****/
                case "5":

                    holder.tvDate.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (holder.tvDate.getTag() != null) {
                                String pos = holder.tvDate.getTag().toString();
                                int position = Integer.parseInt(pos);
                                if (s != null && s.toString().length() > 0) {
                                    long startDate = 0;
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                                        Date date = sdf.parse(s.toString());
                                        assert date != null;
                                        startDate = date.getTime() / 1000;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    List<AnswerModel> answerModelList = new ArrayList<>();
                                    if (typeList.get(position).getAns() != null && typeList.get(position).getAns().size() > 0)
                                        answerModelList.add(new AnswerModel(typeList.get(position).getAns().get(0).getKey(),
                                                startDate + ""));
                                    else
                                        answerModelList.add(new AnswerModel("0", startDate + ""));
                                    typeList.get(position).setAns(answerModelList);
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    holder.linearDate.setVisibility(View.VISIBLE);
                    holder.tvDate.setTag(position);
                    holder.linearDate.setOnClickListener(view -> {
                        datePickerDialog.getDatePicker().setTag("DateType5");
                        datePickerDialog.show();
                    });
                    holder.tvDate.setTag(position);
                    if (typeList.get(position).getAns().isEmpty())
                        holder.tvDate.setText("");
                    else if (typeList.get(position).getAns().size() > 0) {
                        try {
                            if (!(typeList.get(position).getAns().get(0).getValue()).equals("")) {
                                String[] dateConvert = AppUtility.getFormatedTime(typeList.get(position).
                                        getAns().get(0).getValue());
                                assert dateConvert != null;
                                String s = dateConvert[0];
                                String[] date = s.split(",");
                                holder.tvDate.setText(date[1].trim().replace(" ", "-"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
                /*Time type****/
                case "6":

                    holder.tvTime.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (holder.tvTime.getTag() != null) {
                                String pos = holder.tvTime.getTag().toString();
                                int position = Integer.parseInt(pos);
                                if (s != null && s.toString().length() > 0) {
                                    long startDate = 10;
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat(
                                                AppUtility.dateTimeByAmPmFormate("hh:mm a", "kk:mm"), Locale.US);
                                        Date date = sdf.parse(s.toString());
                                        assert date != null;
                                        startDate = date.getTime() / 1000;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    List<AnswerModel> answerModelList = new ArrayList<>();
                                    if (typeList.get(position).getAns() != null && typeList.get(position).getAns().size() > 0)
                                        answerModelList.add(new AnswerModel(typeList.get(position).getAns().get(0).getKey(),
                                                startDate + ""));
                                    else
                                        answerModelList.add(new AnswerModel("0", startDate + ""));
                                    typeList.get(position).setAns(answerModelList);
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });


                    holder.linearTime.setOnClickListener(v -> addLeadFragment.getTimeFromPicker(myCalendar, "TimeType6", holder.tvTime));

                    holder.linearTime.setVisibility(View.VISIBLE);
                    holder.tvTime.setTag(position);

                    if (typeList.get(position).getAns().isEmpty())
                        holder.tvTime.setText("");
                    else if (typeList.get(position).getAns().size() > 0) {
                        try {
                            if (!(typeList.get(position).getAns().get(0).
                                    getValue().equals(""))) {
                                String time = AppUtility.getDateWithFormate2((Long.parseLong(typeList.get(position).
                                                getAns().get(0).
                                                getValue()) * 1000),
                                        AppUtility.dateTimeByAmPmFormate(
                                                "hh:mm a", "kk:mm"));
                                holder.tvTime.setText(time);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
                /*Date Time Type****/
                case "7":

                    holder.tvTimeDate.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (holder.tvTimeDate.getTag() != null) {
                                String pos = holder.tvTimeDate.getTag().toString();
                                int position = Integer.parseInt(pos);
                                if (s != null && s.toString().length() > 0) {
                                    long startDate = 0;
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat(
                                                AppUtility.dateTimeByAmPmFormate("dd-MMM-yyyy hh:mm a",
                                                        "dd-MMM-yyyy kk:mm"), Locale.US);
                                        Date date = sdf.parse(s.toString());
                                        assert date != null;
                                        startDate = date.getTime() / 1000;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    List<AnswerModel> answerModelList = new ArrayList<>();
                                    if (typeList.get(position).getAns() != null && typeList.get(position).getAns().size() > 0)
                                        answerModelList.add(new AnswerModel(typeList.get(position).getAns().get(0).getKey(),
                                                startDate + ""));
                                    else
                                        answerModelList.add(new AnswerModel("0", startDate + ""));
                                    typeList.get(position).setAns(answerModelList);
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    holder.linearDateTime.setVisibility(View.VISIBLE);
                    holder.tvTimeDate.setTag(position);
                    holder.dateImg.setOnClickListener(v -> {
                        datePickerDialog.getDatePicker().setTag("DateType7");
                        datePickerDialog.show();
                    });


                    holder.timeImg.setOnClickListener(v -> addLeadFragment.getTimeFromPicker(myCalendar, "TimeType7", holder.tvTimeDate));
                    if (typeList.get(position).getAns().isEmpty())
                        holder.tvTimeDate.setText("");
                    else if (typeList.get(position).getAns().size() > 0) {
                        try {
                            if (!typeList.get(position).getAns().get(0).getValue().equals("")) {
                                long dateLong = Long.parseLong(typeList.get(position).getAns().get(0).getValue());
                                String dateConvert = AppUtility.getDate(dateLong,
                                        AppUtility.dateTimeByAmPmFormate("dd-MMM-yyyy hh:mm a", "dd-MMM-yyyy kk:mm"));
                                holder.tvTimeDate.setText(dateConvert);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                /*CheckBox for Only single option (True/false) ****/
                case "8":
                    holder.checkbox_single.setVisibility(View.VISIBLE);
                    holder.tvQuestion.setVisibility(View.GONE);
                    if (typeList.get(position).getMand()) {
                        holder.checkbox_single.setText(typeList.get(position).getFieldName() + " *");
                    } else {
                        holder.checkbox_single.setText(typeList.get(position).getFieldName());
                    }

                    if (typeList.get(position).getAns() != null && typeList.get(position).getAns().size() > 0) {
                        holder.checkbox_single.setChecked(typeList.get(position).getAns().get(0).getValue().equals("1"));
                    }

                    holder.checkbox_single.setOnClickListener(v -> {
                        if (typeList.get(position).getAns() != null && typeList.get(position).getAns().size() > 0) {
                            if (typeList.get(position).getAns().get(0).getValue().equals("1"))
                                typeList.get(position).getAns().get(0).setValue("0");
                            else typeList.get(position).getAns().get(0).setValue("1");
                        } else {
                            typeList.get(position).getAns().add(new AnswerModel("0", "1"));
                        }
                    });
                    break;
                /*Show Only Lable****/
                case "9":
                    try {
                        holder.tvQuestion.setTypeface(holder.tvQuestion.getTypeface(), Typeface.BOLD);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    holder.tvQuestion.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener, View.OnClickListener, ServiceInterface, TextWatcher {
        // private final ClientChat_View mlistener;
        TextView tvQuestion, tvDate, que_no, spinner_text, tvTime, tvTimeDate;//, txt_lable;//, textView1;
        EditText type_text, type_text_area;
        LinearLayout linearCheck, linearDate, linearTime, linearDateTime, ll_start_end_date;
        RelativeLayout linearSpinner, rl_des, rl_refrences, rl_job_service, rl_client;
        NoDefaultSpinner spinner;
        ImageView timeImg, dateImg;
        CheckBox checkbox_single;
        LinearLayout jobservicelayout;
        TextView jobservicetxtlableset;
        //   private String img_url = "";
        TextView tv_spinner_reference;
        Spinner referenceDp;
        LinearLayout linearLayout_reference;
        ImageView action_insert_image;
        TextView schel_start, schel_end, date_start, time_start, date_end, time_end;
        CustomDPControllerLead customDPController;
        AutoCompleteTextView auto_client, auto_sites, auto_country, auto_states, auto_contact;
        LinearLayout ll_client_details;
        TextInputLayout input_layout_contact, input_layout_site, job_state_layout, job_mob1_layout, job_mob2_layout, job_email_layout, job_adr_layout, job_lat_layout, job_lng_layout, job_city_layout, job_postal_layout, job_country_layout, contact_add_layout, site_add_layout, landmark_layout;
        EditText mob_no, at_mob, email, adderes, city, post_code, edt_lng, edt_lat, landmark_edt, contact_add_edt, site_add_edt;
        RelativeLayout contract_parent_view;
        Spinner contractSpinner;
        TextView contarct_lable, contract_hint_lable, cb_future_sites, cb_future_contact;
        ImageView contract_cross_img, contract_dp_img, contact_dp_img, site_dp_img, imvCross;
        LinearLayout contact_dp_layout, contact_new_add_layout, site_new_add_layout, site_dp_layout;
        CustomEditor mEditor;
        CheckBox cb_future_client;
        View landmark_view, lng_view, lat_view, lat_lng_view_lay;
        TextView shift_time_txt;
        Switch switch_btn;
        Spinner time_shift_dp;
        // recur
        LinearLayout normal_weekly_recur, recur_view, recur_parent_view, recur_pattern_view;
        CheckBox add_recur_checkBox;
        TextView end_date_for_weekly_recur, recur_job_days_msg, cutom_txt;
        AppCompatTextView custom_recur_pattern, recur_not_work;
        RadioButton radio_on, radio_never_end;
        CustomWeekSelector mon_week_day, tu_week_day, wed_week_day, thu_week_day, fri_week_day, sat_week_day, sun_week_day;
        RelativeLayout msg_pattern_view;
        Button date_time_clear_btn;

        // tag
        TextView tag_lable;
        AutoCompleteTextView et_tag;
        Button add_tag_btn;
        LinearLayout linear_addTag;

        // assign to

        LinearLayout assignto_linear,linearMainView;
        TextView assign_to;
        AutoCompleteTextView members;


        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(View itemView, final Context context) {
            super(itemView);

            // tag
            tag_lable = itemView.findViewById(R.id.tag_lable);
            et_tag = itemView.findViewById(R.id.et_tag);
            add_tag_btn = itemView.findViewById(R.id.add_tag_btn);
            linear_addTag = itemView.findViewById(R.id.linear_addTag);

            //assign to
            assignto_linear = itemView.findViewById(R.id.assignto_linear);
            members = itemView.findViewById(R.id.members);
            linearMainView = itemView.findViewById(R.id.linearMainView);
            //client
            ll_client_details = itemView.findViewById(R.id.ll_client_details);
            rl_client = itemView.findViewById(R.id.rl_client);
            auto_client = itemView.findViewById(R.id.auto_client);
            cb_future_client = itemView.findViewById(R.id.cb_future_client);
            //views
            landmark_view = itemView.findViewById(R.id.landmark_view);
            lng_view = itemView.findViewById(R.id.lng_view);
            lat_view = itemView.findViewById(R.id.lat_view);
            lat_lng_view_lay = itemView.findViewById(R.id.lat_lng_view_lay);
            /*find Recur View's****/
            recur_parent_view = itemView.findViewById(R.id.recur_parent_view);
            recur_pattern_view = itemView.findViewById(R.id.recur_pattern_view);
            normal_weekly_recur = itemView.findViewById(R.id.normal_weekly_recur);
            add_recur_checkBox = itemView.findViewById(R.id.add_recur_checkBox);

            end_date_for_weekly_recur = itemView.findViewById(R.id.end_date_for_weekly_recur);

            custom_recur_pattern = itemView.findViewById(R.id.custom_recur_pattern);
            recur_not_work = itemView.findViewById(R.id.recur_not_work);

            mon_week_day = itemView.findViewById(R.id.mon_week_day);
            tu_week_day = itemView.findViewById(R.id.tu_week_day);
            wed_week_day = itemView.findViewById(R.id.wed_week_day);
            thu_week_day = itemView.findViewById(R.id.thu_week_day);
            fri_week_day = itemView.findViewById(R.id.fri_week_day);
            sat_week_day = itemView.findViewById(R.id.sat_week_day);
            sun_week_day = itemView.findViewById(R.id.sun_week_day);

            radio_on = itemView.findViewById(R.id.radio_on);
            radio_never_end = itemView.findViewById(R.id.radio_never_end);

            recur_view = itemView.findViewById(R.id.recur_view);

            recur_job_days_msg = itemView.findViewById(R.id.recur_job_days_msg);
            cutom_txt = itemView.findViewById(R.id.cutom_txt);

            msg_pattern_view = itemView.findViewById(R.id.msg_pattern_view);


            // client detail views
            job_mob1_layout = itemView.findViewById(R.id.input_layout_mobile);
            job_mob2_layout = itemView.findViewById(R.id.job_mob2_layout);
            job_email_layout = itemView.findViewById(R.id.job_email_layout);
            job_adr_layout = itemView.findViewById(R.id.job_adr_layout);
            job_city_layout = itemView.findViewById(R.id.job_city_layout);
            job_state_layout = itemView.findViewById(R.id.job_state_layout);
            job_country_layout = itemView.findViewById(R.id.job_country_layout);
            job_postal_layout = itemView.findViewById(R.id.job_postal_layout);
            job_lat_layout = itemView.findViewById(R.id.job_lat_layout);
            job_lng_layout = itemView.findViewById(R.id.job_lng_layout);
            landmark_layout = itemView.findViewById(R.id.landmark_layout);
            input_layout_contact = itemView.findViewById(R.id.input_layout_contact);
            input_layout_site = itemView.findViewById(R.id.input_layout_site);
            contact_add_layout = itemView.findViewById(R.id.contact_add_layout);
            site_add_layout = itemView.findViewById(R.id.site_add_layout);
            contact_dp_layout = itemView.findViewById(R.id.contact_dp_layout);
            site_new_add_layout = itemView.findViewById(R.id.site_new_add_layout);
            contact_new_add_layout = itemView.findViewById(R.id.contact_new_add_layout);
            site_dp_layout = itemView.findViewById(R.id.site_dp_layout);


            mob_no = itemView.findViewById(R.id.mob_no);
            at_mob = itemView.findViewById(R.id.at_mob);
            email = itemView.findViewById(R.id.email);
            adderes = itemView.findViewById(R.id.adderes);
            city = itemView.findViewById(R.id.city);
            post_code = itemView.findViewById(R.id.post_code);
            edt_lng = itemView.findViewById(R.id.edt_lng);
            edt_lat = itemView.findViewById(R.id.edt_lat);
            landmark_edt = itemView.findViewById(R.id.landmark_edt);
            contact_add_edt = itemView.findViewById(R.id.contact_add_edt);
            site_add_edt = itemView.findViewById(R.id.site_add_edt);

            // contract views
            contract_parent_view = itemView.findViewById(R.id.contract_parent_view);
            contractSpinner = itemView.findViewById(R.id.contractSpinner);
            contarct_lable = itemView.findViewById(R.id.contarct_lable);
            contract_hint_lable = itemView.findViewById(R.id.contract_hint_lable);
            contract_cross_img = itemView.findViewById(R.id.contract_cross_img);
            contract_dp_img = itemView.findViewById(R.id.contract_dp_img);
            site_dp_img = itemView.findViewById(R.id.site_dp_img);
            imvCross = itemView.findViewById(R.id.imvCross);


            // site view
            auto_sites = itemView.findViewById(R.id.auto_sites);
            auto_states = itemView.findViewById(R.id.auto_states);
            auto_country = itemView.findViewById(R.id.auto_country);
            contact_dp_img = itemView.findViewById(R.id.contact_dp_img);
            cb_future_sites = itemView.findViewById(R.id.cb_future_sites);
            cb_future_contact = itemView.findViewById(R.id.cb_future_contact);
            site_add_edt = itemView.findViewById(R.id.site_add_edt);
            auto_contact = itemView.findViewById(R.id.auto_contact);

            if (city != null)
                city.addTextChangedListener(this);
            if (adderes != null)
                adderes.addTextChangedListener(this);
            if (email != null)
                email.addTextChangedListener(this);
            if (at_mob != null)
                at_mob.addTextChangedListener(this);
            if (mob_no != null)
                mob_no.addTextChangedListener(this);
            if (post_code != null)
                post_code.addTextChangedListener(this);
            if (edt_lng != null)
                edt_lng.addTextChangedListener(this);
            if (edt_lat != null)
                edt_lat.addTextChangedListener(this);
            if (landmark_edt != null)
                landmark_edt.addTextChangedListener(this);

            // des view

            mEditor = itemView.findViewById(R.id.editor);
//
            action_insert_image = itemView.findViewById(R.id.action_insert_image);

            // service layout

            rl_job_service = itemView.findViewById(R.id.rl_job_service);
            jobservicetxtlableset = itemView.findViewById(R.id.jobservicetxtlableset);

            jobservicelayout = itemView.findViewById(R.id.jobservicelayout);
            customDPController = new CustomDPControllerLead(this);


            /*Reference View find***/
            rl_des = itemView.findViewById(R.id.rl_des);
            rl_refrences = itemView.findViewById(R.id.rl_refrences);
            ll_start_end_date = itemView.findViewById(R.id.ll_start_end_date);
            linearLayout_reference = itemView.findViewById(R.id.linearLayout_reference);
            referenceDp = itemView.findViewById(R.id.referenceDp);
            tv_spinner_reference = itemView.findViewById(R.id.tv_spinner_reference);

            switch_btn = itemView.findViewById(R.id.switch_btn);
            time_shift_dp = itemView.findViewById(R.id.time_shift_dp);

            // shedule start and end
            schel_start = itemView.findViewById(R.id.schel_start);
            date_time_clear_btn = itemView.findViewById(R.id.date_time_clear_btn);

            schel_end = itemView.findViewById(R.id.schel_end);

            date_start = itemView.findViewById(R.id.date_start);

            date_end = itemView.findViewById(R.id.date_end);

            time_start = itemView.findViewById(R.id.time_start);
            shift_time_txt = itemView.findViewById(R.id.shift_time_txt);

            time_end = itemView.findViewById(R.id.time_end);

            checkbox_single = itemView.findViewById(R.id.checkbox_single);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvDate = itemView.findViewById(R.id.tvDate);

            type_text = itemView.findViewById(R.id.type_text);

            type_text_area = itemView.findViewById(R.id.type_text_area);


            linearCheck = itemView.findViewById(R.id.linearCheck);
            linearDate = itemView.findViewById(R.id.linearDate);
            spinner = itemView.findViewById(R.id.dropdown_spinner);
            linearSpinner = itemView.findViewById(R.id.linearSpinner);
            que_no = itemView.findViewById(R.id.que_no);
            spinner_text = itemView.findViewById(R.id.spinner_text);
            //   txt_lable = itemView.findViewById(R.id.txt_lable);


//
//            type_text.setOnTouchListener(this);
//            type_text_area.setOnTouchListener(this);

            linearTime = itemView.findViewById(R.id.linearTime);
            tvTime = itemView.findViewById(R.id.tvTime);



            /*question type & for date time ***/
            linearDateTime = itemView.findViewById(R.id.linearDateTime);
            tvTimeDate = itemView.findViewById(R.id.tvTimeDate);


            timeImg = itemView.findViewById(R.id.timeImg);
            dateImg = itemView.findViewById(R.id.dateImg);


        }

        /**
         * Required for edittext scrolling
         */
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (view.getId() == R.id.type_text) {
                view.getParent().requestDisallowInterceptTouchEvent(false);

               /* view.getParent().requestDisallowInterceptTouchEvent(true);
                if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                }*/
            } else if (view.getId() == R.id.type_text_area) {
                view.getParent().requestDisallowInterceptTouchEvent(false);

            }
            return false;
        }

        @Override
        public void onClick(View v) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void setSelectedJobServices(ArrayList<JobTitle> selectedJobServices) {
            jtIdList.clear();
            if (suggestions != null && suggestions.size() > 0) {
                suggestions.clear();
            }
//            Set<String> jobServiceNm = new HashSet<>();
            for (JobTitle jobTitle : selectedJobServices) {
                if (jobTitle.isSelect()) {
                    jtIdList.add(jobTitle.getJtId());
                    jobServiceNm.add(jobTitle.getName());
                    try {
                        if (jobTitle.getSuggestionList() != null)
                            suggestions.addAll(jobTitle.getSuggestionList());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }

            addLeadPost.setJtId(jtIdList);

            if (jobServiceNm.size() >= 4)
                jobservicetxtlableset.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.items_selected) + " " + jobServiceNm.size());
            else
                jobservicetxtlableset.setText(jobServiceNm.toString().replace("[", "").replace("]", ""));

            setdataInSuggestion();
        }


        public void setdataInSuggestion() {
            suggestionsArray = new String[suggestions.size()];
            for (int i = 0; i < suggestions.size(); i++) {
                try {
                    suggestionsArray[i] = suggestions.get(i).getJtDesSugg();//String.valueOf(suggestions.get(i).getJtDesSugg());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable charSequence) {
            if (charSequence.hashCode() == mob_no.getText().hashCode())
                addLeadPost.setMob1(charSequence.toString());
            if (charSequence.hashCode() == at_mob.getText().hashCode())
                addLeadPost.setMob2(charSequence.toString());
            if (charSequence.hashCode() == email.getText().hashCode())
                addLeadPost.setEmail(charSequence.toString());
            if (charSequence.hashCode() == adderes.getText().hashCode())
                addLeadPost.setAdr(charSequence.toString());
            if (charSequence.hashCode() == edt_lat.getText().hashCode())
                addLeadPost.setLat(charSequence.toString());
            if (charSequence.hashCode() == edt_lng.getText().hashCode())
                addLeadPost.setLng(charSequence.toString());
            if (charSequence.hashCode() == city.getText().hashCode())
                addLeadPost.setCity(charSequence.toString());
            if (charSequence.hashCode() == post_code.getText().hashCode())
                addLeadPost.setZip(charSequence.toString());
            if (charSequence.hashCode() == landmark_edt.getText().hashCode())
                addLeadPost.setLandmark(charSequence.toString());
        }
    }


    public void onDocumentSelected(String path, boolean isImage, CustomEditor editor) {
        if (!TextUtils.isEmpty(path) && isImage && editor != null) {
            editor.getSettings().setAllowFileAccess(true);
            editor.insertImage(path, "logo", 320, 250);
        }
    }

}