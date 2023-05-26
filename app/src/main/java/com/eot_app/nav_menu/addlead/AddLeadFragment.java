package com.eot_app.nav_menu.addlead;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.eot_app.R;
import com.eot_app.eoteditor.CustomEditor;
import com.eot_app.eoteditor.Utils;
import com.eot_app.home_screens.MainActivity;
import com.eot_app.nav_menu.addlead.add_lead_presenter.AddLead_pc;
import com.eot_app.nav_menu.addlead.add_lead_presenter.Add_lead_pi;
import com.eot_app.nav_menu.addlead.model.AddLeadPost;
import com.eot_app.nav_menu.addlead.model.QuestionListPostLead;
import com.eot_app.nav_menu.client.add_client.clientadpter.ClientRefrenceAdpter;
import com.eot_app.nav_menu.client.client_db.Client;
import com.eot_app.nav_menu.client.clientlist.client_detail.contact.client_dao.ContactData;
import com.eot_app.nav_menu.client.clientlist.client_detail.site.sitelist.Site_model;
import com.eot_app.nav_menu.client.clientlist.client_detail.site.sitelist.editsite.editsitedb.SpinnerCountrySite;
import com.eot_app.nav_menu.jobs.add_job.adapters.DynamicClassAdapter;
import com.eot_app.nav_menu.jobs.add_job.adapters.FilterAdapter;
import com.eot_app.nav_menu.jobs.add_job.adapters.FilterAdapterContact;
import com.eot_app.nav_menu.jobs.add_job.adapters.FilterAdapterSites;
import com.eot_app.nav_menu.jobs.add_job.adapters.FilterCountry;
import com.eot_app.nav_menu.jobs.add_job.adapters.FilterStates;
import com.eot_app.nav_menu.jobs.add_job.adapters.TimeSHiftAdpter;
import com.eot_app.nav_menu.jobs.add_job.add_job_recr.AddJobRecrHomeActivity;
import com.eot_app.nav_menu.jobs.add_job.add_job_recr.daily_recr_pkg.daily_recur_model.JobRecurModel;
import com.eot_app.nav_menu.jobs.job_detail.chat.img_crop_pkg.ImageCropFragment;
import com.eot_app.nav_menu.jobs.job_detail.documents.PathUtils;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.MyFormInterFace;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.qus_model.AnswerModel;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.qus_model.OptionModel;
import com.eot_app.time_shift_pkg.ShiftTimeReSModel;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.Country;
import com.eot_app.utility.CurrLatLngCntrlr;
import com.eot_app.utility.DropdownListBean;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.States;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.settings.client_refrence_db.ClientRefrenceModel;
import com.eot_app.utility.settings.contractdb.ContractRes;
import com.eot_app.utility.settings.setting_db.FieldWorker;
import com.eot_app.utility.settings.setting_db.JobTitle;
import com.eot_app.utility.settings.setting_db.TagData;
import com.eot_app.utility.util_interfaces.Callback_AlertDialog;
import com.eot_app.utility.util_interfaces.MyAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.hypertrack.hyperlog.HyperLog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import static android.app.Activity.RESULT_OK;
import static android.location.LocationManager.GPS_PROVIDER;
import static com.eot_app.utility.AppUtility.updateTime;
import static java.lang.Double.parseDouble;

public class AddLeadFragment extends androidx.fragment.app.Fragment
        implements
        AdapterView.OnItemSelectedListener, AddLeadView, TextWatcher,
        View.OnClickListener, MyFormInterFace, LeadHeaderAdapter.onLeadClick, MyAttachmentLead {

    public String endDate = "";
    RecyclerView recyclerView, rv_form_title;
    ArrayList<AddLeadResModel> addLeadResModels = new ArrayList<>();
    ArrayList<AfJsonResModel> afJsonResModels = new ArrayList<>();
    // TODO: Rename and change types of parameters
    Button saveBtn;
    private boolean isMandatoryNotFill;
    private static final String TAG = "AddLeadFragment";
    FieldListAdapter fieldListAdapter;
    LeadHeaderAdapter leadHeaderAdapter;
    public static final int ADDCUSTOMRECUR = 501;
    private final ArrayList<TagData> tagArray = new ArrayList<>();
    OnFragmentInteractionListener mListener;
    ArrayList<JobTitle> datastr = new ArrayList<>();
    String[] id_array;
    private Add_lead_pi addLead_pc;
    CustomEditor editor;
    String captureImagePath;
    int position = 0;
    public static final int LOCATION_REQUEST = 1000;
    int RECURTYPENORMAL_CUSTOM = 1;
    public String endRecurMode = "";
    private final static int CAPTURE_IMAGE_GALLARY = 222;
    private final int CAMERA_CODE = 100;
    private final int ATTACHFILE_CODE = 101;
    List<Client> clientList = new ArrayList<>();
    AddLeadPost addLeadPost;
    String contrId = "";
    List<ContactData> contact_data;
    FilterAdapterContact filterContact;
    ContactData selectedContactData;
    List<Site_model> site_data;
    FilterAdapterSites filterSites;
    Site_model selectedSiteData;
    int clientForFuture = 0, siteForFuture = 0, contactForFuture = 0;
    String new_site_nm = "";
    String siteId = "";
    String conId = "";
    String new_con_nm = "";
    String ctry_id = "";
    String state_id = "";
    int reference = 0;
    MainActivity activity;
    String referenceName = "";
    String cltId = "";
    String new_cnm = "";
    int afId = 0;
    String time = "", date = "";
    int year, month, day, mHour, mMinute;
    String date_str, time_str = "", date_en, time_en = "", schdlStart = "", schdlEnd = "";
    final boolean newContact = false;
    final boolean newSite = false;
    List<ShiftTimeReSModel> shiftList = new ArrayList<>();
    private LocationManager locationManager;
    final ArrayList<String> weekDays = new ArrayList<>();
    List<LinkedHashMap<String, Boolean>> selectedDays = new ArrayList<>();
    LinkedHashMap<String, Boolean> selectedDaysMap = new LinkedHashMap<>();
    ArrayList<JobRecurModel> recudata = new ArrayList<>();
    String recurType = "", recurMsg = "";
    String isRecur = "";
    int iTag = 1;
    TimeSHiftAdpter timeSHiftAdpter;
    List<TagData> tagslist;
    final Set<String> listwork = new HashSet<>();

    public static AddLeadFragment newInstance() {
        return new AddLeadFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_lead_form, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        rv_form_title = view.findViewById(R.id.recycleView_form_type);
        leadHeaderAdapter = new LeadHeaderAdapter(getActivity(), this);
        rv_form_title.setAdapter(leadHeaderAdapter);
        locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);

        AppUtility.progressBarShow(getActivity());

        addLeadPost = new AddLeadPost();

        addLead_pc = new AddLead_pc(this);
        addLead_pc.getJobTitleList();
        addLead_pc.getClientList();
        addLead_pc.getLeadFormApiCall();

        //      for add user as default fieldworker in member list initialize.
        //todo
//        FieldWorker yourId = addLead_pc.getDefaultFieldWorker();
//        if (yourId != null) {
//            linearMainView.setVisibility(View.VISIBLE);
//            addChips(yourId);
//        }

        saveBtn = view.findViewById(R.id.saveBtn);
        saveBtn.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.save_btn));
        saveBtn.setOnClickListener(this);


        return view;
    }

    public void getCountryList(FieldListAdapter.ViewHolder holder) {
        addLead_pc.getCountryList(holder);
    }

    public void getTagList(FieldListAdapter.ViewHolder holder) {
        addLead_pc.getTagDataList(holder);
    }

    public void getTimeShiftList(FieldListAdapter.ViewHolder holder) {
        addLead_pc.getTimeShiftList(holder);
    }

    private void setData() {
        afJsonResModels.clear();

        for (int i = 0; i < addLeadResModels.size(); i++) {
            if (addLeadResModels.get(i).isSelected()) {
                afId = Integer.parseInt(addLeadResModels.get(i).getAfId());
                try {
                    String afJson = addLeadResModels.get(i).getAfJson();
                    String jsonFormattedString = afJson.replaceAll("\\\\", "");

                    JSONArray jsonArrayMain = new JSONArray(jsonFormattedString);
                    for (int a = 0; a < jsonArrayMain.length(); a++) {
                        JSONObject obj = jsonArrayMain.getJSONObject(a);

                        AfJsonResModel afJsonResModel = new AfJsonResModel();
                        afJsonResModel.setFieldName(obj.getString("fieldName"));
                        afJsonResModel.setId(obj.getString("id"));
                        afJsonResModel.setType(obj.getString("type"));
                        afJsonResModel.setIsCustom(obj.getBoolean("isCustom"));
                        afJsonResModel.setFormControlName(obj.getString("formControlName"));

                        if (obj.getBoolean("isCustom")) {
                            afJsonResModel.setFrmId(obj.getString("frmId"));
                            afJsonResModel.setMand(obj.getBoolean("mand"));
                        }


                        afJsonResModel.setQueWidth(obj.getString("queWidth"));
                        List<OptionModel> optionModelList = new ArrayList<>();

                        if (obj.getString("type").equalsIgnoreCase("3") ||
                                obj.getString("type").equalsIgnoreCase("4")) {
                            obj.getJSONArray("options");
                            JSONArray jsonArray = obj.getJSONArray("options");
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(j);

                                OptionModel optionModel = new OptionModel();
                                optionModel.setKey(jsonObject.getString("key"));
                                optionModel.setOptHaveChild(jsonObject.getString("optHaveChild"));
                                optionModel.setValue(jsonObject.getString("value"));

                                optionModelList.add(optionModel);
                            }
                            afJsonResModel.setOptions(optionModelList);
                        }
                        afJsonResModels.add(afJsonResModel);

//                        if (obj.getBoolean("isCustom")) {
//                            afJsonResModels.add(afJsonResModel);
//                        }
                    }
                    fieldListAdapter = new FieldListAdapter(afJsonResModels, getActivity(), this, addLead_pc, datastr, clientList, this, addLeadPost, AddLeadFragment.this);
                    recyclerView.setAdapter(fieldListAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveBtn) {
            if (isMandatoryNotFill) {
                isMandatoryNotFill = false;
                AppUtility.alertDialog(getActivity(), "",
                        LanguageController.getInstance().getMobileMsgByKey(AppConstant.fill_all_mandatory_questions), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
                return;
            }

            callAddLead();
        }
    }

    private void callAddLead() {
        /*
         * get the answer submitted by the user
         *
         * */
        this.addLeadPost = fieldListAdapter.addLeadPost;

        String clientReq = "";

        if (!addLeadPost.getSiteId().isEmpty()) {
            addLeadPost.setSnm("");
        }


        if (!addLeadPost.getConId().isEmpty()) {
            addLeadPost.setCnm("");
        }

        if (!addLeadPost.getCltId().isEmpty()) {
            addLeadPost.setNm("");
            clientReq = addLeadPost.getCltId();
        } else if (!addLeadPost.getNm().isEmpty()) {
            clientReq = addLeadPost.getNm();
            addLeadPost.setCnm("Self");
            addLeadPost.setSnm("Self");
        }
        // validations
        if (!addLead_pc.RequiredFields(clientReq,
                newContact,
                newSite,
                new_con_nm,
                new_site_nm,
                addLeadPost.getAdr(),
                addLeadPost.getCtry(),
                addLeadPost.getState(),
                addLeadPost.getMob1(),
                addLeadPost.getMob2(),
                addLeadPost.getEmail()
        )) {
            return;
        }

        if (afJsonResModels != null) {
            ArrayList<QuestionListPostLead> customFieldAnswerList = getCustomFieldAnswerList(afJsonResModels);
            addLeadPost.setAnswerArrayList(customFieldAnswerList);
        }

        ArrayList links = new ArrayList();
        final List<String> fileNames = new ArrayList<>();
        String jobDescription = addLeadPost.getDes();
        if (!TextUtils.isEmpty(jobDescription)) {
            Elements srcs = Jsoup.parse(jobDescription).select("[src]"); //get All tags containing "src"
            for (int i = 0; i < srcs.size(); i++) {
                if (srcs.get(i).attributes() != null) {
                    String link = null;
                    link = srcs.get(i).attributes().get("src");// get links of selected tags
                    links.add(link);
                    String filename = link.substring(link.lastIndexOf("/") + 1);
                    fileNames.add(filename);
                    jobDescription = jobDescription.replace(link, "_jobAttSeq_" + i + "_");
                }
            }
            addLeadPost.setDes("<p>" + jobDescription + "</p>");
        }

        //set afId
        addLeadPost.setAfId(afId);
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(getActivity());
            addLead_pc.addLeadWithImageDescription(addLeadPost, links, fileNames);
        }

    }

    public ArrayList<QuestionListPostLead> getCustomFieldAnswerList(ArrayList<AfJsonResModel> questionList) {

        ArrayList<QuestionListPostLead> answerArrayList = new ArrayList<>();

        for (int i = 0; i < questionList.size(); i++) {
            String key = "";
            String ans = "";
            ArrayList<AnswerModel> ansArrayList = new ArrayList<>();
            QuestionListPostLead answer;
            switch (questionList.get(i).getType()) {
                case "8":
                    if (questionList.get(i).getAns() != null && questionList.get(i).getAns().size() > 0) {
                        AnswerModel answerModel = new AnswerModel(questionList.get(i).getAns().get(0).getKey(), questionList.get(i).getAns().get(0).getValue());
                        ansArrayList.add(answerModel);
                        answer = new QuestionListPostLead(questionList.get(i).getId(), questionList.get(i).getFrmId(), questionList.get(i).getType(), ansArrayList);
                        answerArrayList.add(answer);
                    }

                    break;
                case "2":
                case "5":
                case "6":
                case "7":
                case "1":
                    if (questionList.get(i).getAns() != null && questionList.get(i).getAns().size() > 0) {
                        switch (questionList.get(i).getType()) {
                            case "5":
                                if (!TextUtils.isEmpty(questionList.get(i).getAns().get(0).getValue())) {
                                    long l = Long.parseLong(questionList.get(i).getAns().get(0).getValue());
                                    ans = AppUtility.getDate(l, "dd-MMM-yyyy");
                                }
                                break;
                            case "6":
                                if (!TextUtils.isEmpty(questionList.get(i).getAns().get(0).getValue())) {
                                    long l = Long.parseLong(questionList.get(i).getAns().get(0).getValue());
                                    ans = AppUtility.getDate(l, "hh:mm a");
                                }
                                break;
                            case "7":
                                if (!TextUtils.isEmpty(questionList.get(i).getAns().get(0).getValue())) {
                                    long l = Long.parseLong(questionList.get(i).getAns().get(0).getValue());
                                    ans = AppUtility.getDate(l, "dd-MMM-yyyy hh:mm a");
                                }
                                break;
                            default:
                                ans = questionList.get(i).getAns().get(0).getValue();
                                break;
                        }
                        AnswerModel answerModel = new AnswerModel(key, ans);
                        ansArrayList.add(answerModel);

                        answer = new QuestionListPostLead(questionList.get(i).getId(), questionList.get(i).getFrmId(), questionList.get(i).getType(), ansArrayList);

                        answerArrayList.add(answer);

                    }
                    break;
                case "4":
                case "3":
                    if (questionList.get(i).getAns() != null && questionList.get(i).getAns().size() > 0) {
                        List<AnswerModel> ans1 = questionList.get(i).getAns();
                        if (ans1 != null)
                            for (AnswerModel am : ans1) {
                                key = am.getKey();
                                ans = am.getValue();
                                AnswerModel answerModel = new AnswerModel(key, ans);
                                ansArrayList.add(answerModel);
                            }
                    }
                    if (ansArrayList.size() > 0) {
                        answer = new QuestionListPostLead(questionList.get(i).getId(), questionList.get(i).getFrmId(), questionList.get(i).getType(), ansArrayList);
                        answerArrayList.add(answer);
                    } else {

                        answer = new QuestionListPostLead(questionList.get(i).getId(), questionList.get(i).getFrmId(), questionList.get(i).getType(), ansArrayList);

                        answerArrayList.add(answer);
                    }
                    break;
            }

        }


        return answerArrayList;

    }


    @Override
    public void onAttach(Context context) {
        activity = new MainActivity();
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void getAnsId(String ansId) {
//
    }

    @Override
    public void onLeadClickEvent(String afId) {
        setData();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void SetJobTittle(ArrayList<JobTitle> datastr) {
        this.datastr = datastr;
        id_array = new String[datastr.size()];
        if (fieldListAdapter != null)
            fieldListAdapter.setDatastr(datastr);
    }

    @Override
    public void SetLead(List<AddLeadResModel> addLeadResModel) {
        if (addLeadResModel != null && !addLeadResModel.isEmpty()) {
            try {
                AppUtility.progressBarDissMiss();
                addLeadResModels.clear();
                addLeadResModels.addAll(addLeadResModel);
                addLeadResModels.get(0).setSelected(true);
                leadHeaderAdapter.setList(addLeadResModels);
                setData();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void setClientlist(final List<Client> data) {
        clientList = data;
    }

    @Override
    public void setContractlist(List<ContractRes> contractlist, @NonNull FieldListAdapter.ViewHolder holder) {

        AppUtility.spinnerPopUpWindow(holder.contractSpinner);
        if (contractlist.size() > 0) {
            setContractViews(contractlist.get(0), holder);
            holder.contract_parent_view.setVisibility(View.VISIBLE);
            MyAdapter<ContractRes> contractAdpter = new MyAdapter(getContext(), R.layout.custom_adapter_item_layout,
                    contractlist);
            holder.contractSpinner.setAdapter(contractAdpter);

            holder.contractSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                    ContractRes model = (ContractRes) adapterView.getItemAtPosition(pos);
                    setContractViews(model, holder);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            contractlist.size();
            holder.contract_parent_view.setVisibility(View.GONE);
        }
    }

    /****set contractor value's in View****/
    private void setContractViews(ContractRes contractRes, @NonNull final FieldListAdapter.ViewHolder holder) {
        holder.contarct_lable.setText(contractRes.getLabel());
        holder.contract_hint_lable.setVisibility(View.VISIBLE);
        holder.contract_cross_img.setVisibility(View.VISIBLE);
        contrId = contractRes.getContrId();
        addLeadPost.setConId(contrId);
    }


    @Override
    public void setSiteList(List<Site_model> data, @NonNull FieldListAdapter.ViewHolder holder) {
//        fieldListAdapter.setSiteList(data, holder);
        if (data.size() > 0) {
            this.site_data = data;
            AppUtility.autocompletetextviewPopUpWindow(holder.auto_sites);
            for (Site_model siteData : data) {
                if (siteData.getDef().equals("1")) {
                    holder.auto_sites.setText(siteData.getSnm());
                    setSitetDefaultData(siteData, holder);
                    holder.input_layout_site.setHintEnabled(true);
                    selectedSiteData = siteData;
                    break;
                }
            }
            filterSites = new FilterAdapterSites(getContext(), R.layout.custom_adapter_item_layout, (ArrayList<Site_model>) data);
            holder.auto_sites.setAdapter(filterSites);
            holder.auto_sites.setThreshold(0);
            holder.auto_sites.setOnItemClickListener((adapterView, view, i, l) -> {
                Site_model site_model = (Site_model) (adapterView.getAdapter().getItem(i));
                selectedSiteData = site_model;
                setSitetDefaultData(site_model, holder);
                holder.site_add_edt.setText("");
            });
            holder.auto_sites.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    new_site_nm = charSequence.toString();
                    if (charSequence.length() >= 1) {
                        holder.input_layout_site.setHintEnabled(true);
                    } else if (charSequence.length() <= 0) {
                        holder.input_layout_site.setHintEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        } else {
            data.size();
            holder.cb_future_sites.setVisibility(View.GONE);
            holder.auto_sites.setFocusableInTouchMode(false);
            holder.auto_sites.setFocusable(false);
        }
    }

    private void setSitetDefaultData(Site_model sitetData, FieldListAdapter.ViewHolder holder) {
        if (sitetData != null) {
            holder.cb_future_sites.setVisibility(View.GONE);
            siteForFuture = 0;
            addLeadPost.setSiteForFuture(0);
            new_site_nm = "";
            siteId = sitetData.getSiteId();
            Log.e(TAG, "sitetData::" + new Gson().toJson(sitetData));

            holder.site_add_edt.setText("");

            holder.auto_sites.setFocusableInTouchMode(true);
            holder.auto_sites.setFocusable(true);
            holder.site_dp_img.setClickable(true);

            holder.edt_lat.setText(sitetData.getLat());
            holder.edt_lng.setText(sitetData.getLng());
            holder.adderes.setText(sitetData.getAdr());
            holder.auto_country.setText(SpinnerCountrySite.getCountryNameById(sitetData.getCtry()));
            holder.auto_states.setText(SpinnerCountrySite.getStatenameById((sitetData.getCtry()), sitetData.getState()));
            //get state list
            addLead_pc.getStateList(sitetData.getCtry(), holder);

            holder.job_state_layout.setHintEnabled(true);
            holder.post_code.setText(sitetData.getZip());
            holder.city.setText((sitetData.getCity()));


            addLeadPost.setZip(sitetData.getZip());
            addLeadPost.setCity(sitetData.getCity());
            addLeadPost.setLat(sitetData.getLat());
            addLeadPost.setLng(sitetData.getLng());
            addLeadPost.setAdr(sitetData.getAdr());
            addLeadPost.setCtry(sitetData.getCtry());
            addLeadPost.setState(sitetData.getState());
            addLeadPost.setSiteId(sitetData.getSiteId());
            addLeadPost.setSnm(sitetData.getSnm());
            Log.e(TAG, "addLeadPost Set::" + new Gson().toJson(addLeadPost));
            Log.v(TAG, "addLeadPost Set 1:" + new Gson().toJson(fieldListAdapter.addLeadPost));

        }
    }


    @Override
    public void setContactList(List<ContactData> data, @NonNull FieldListAdapter.ViewHolder holder) {
        if (data.size() > 0) {
            this.contact_data = data;
            AppUtility.autocompletetextviewPopUpWindow(holder.auto_contact);
            for (ContactData contactData : data) {
                if (contactData.getDef().equals("1")) {
                    holder.auto_contact.setText(contactData.getCnm());
                    setContactDefaultData(contactData, holder);
                    holder.input_layout_contact.setHintEnabled(true);
                    selectedContactData = contactData;
                    break;
                }
            }

            filterContact = new FilterAdapterContact(getContext(), R.layout.custom_adapter_item_layout, (ArrayList<ContactData>) contact_data);
            holder.auto_contact.setAdapter(filterContact);
            holder.auto_contact.setThreshold(0);

            holder.auto_contact.setOnItemClickListener((adapterView, view, i, l) -> {
                ContactData contactData = (ContactData) ((ListView) adapterView).getAdapter().getItem(i);
                selectedContactData = contactData;
                setContactDefaultData(contactData, holder);
                holder.contact_add_edt.setText("");
            });
            holder.auto_contact.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    new_con_nm = charSequence.toString();
                    addLeadPost.setCnm(new_con_nm);
                    if (charSequence.length() >= 1) {
                        holder.input_layout_contact.setHintEnabled(true);
                    } else if (charSequence.length() <= 0) {
                        holder.input_layout_contact.setHintEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.e("", "");
                }
            });
        }
        if (data.size() == 0) {
            holder.cb_future_contact.setVisibility(View.GONE);
            holder.auto_contact.setFocusableInTouchMode(false);
            holder.auto_contact.setFocusable(false);
        }
    }

    void setContactDefaultData(ContactData contactData, FieldListAdapter.ViewHolder holder) {
        if (contactData != null) {

            Log.e(TAG, "contactData::" + new Gson().toJson(contactData));
            Log.v(TAG, "addLeadPost1::" + new Gson().toJson(addLeadPost));
            holder.cb_future_contact.setVisibility(View.GONE);
            holder.contact_dp_layout.setVisibility(View.VISIBLE);
            holder.contact_new_add_layout.setVisibility(View.GONE);
            holder.contact_add_edt.setText("");

            holder.auto_contact.setFocusableInTouchMode(true);
            holder.auto_contact.setFocusable(true);
            holder.contact_dp_img.setClickable(true);

            contactForFuture = 0;
            conId = contactData.getConId();
            new_con_nm = "";
            addLeadPost.setCnm("");
            addLeadPost.setConId(contactData.getConId());


            try {
                if (contactData.getMob1() != null && contactData.getMob1().length() == 0
                        && App_preference.getSharedprefInstance().getLoginRes().getCtryCode() != null) {
                    holder.mob_no.setText(App_preference.getSharedprefInstance().getLoginRes().getCtryCode());
                    Log.e(TAG, "addLeadPost Set Mob 1 not get::" + new Gson().toJson(addLeadPost));

                } else {
                    holder.mob_no.setText(contactData.getMob1());
                    addLeadPost.setMob1(contactData.getMob1());
                    Log.e(TAG, "addLeadPost Set Mob 1::" + new Gson().toJson(addLeadPost));

                }
                if (contactData.getMob2() != null && contactData.getMob2().length() == 0
                        && App_preference.getSharedprefInstance().getLoginRes().getCtryCode() != null) {
                    holder.at_mob.setText(App_preference.getSharedprefInstance().getLoginRes().getCtryCode());
                    Log.e(TAG, "addLeadPost Set Mob 2 not get::" + new Gson().toJson(addLeadPost));

                } else {
                    holder.at_mob.setText(contactData.getMob2());
                    addLeadPost.setMob2(contactData.getMob2());
                    Log.e(TAG, "addLeadPost Set Mob 2::" + new Gson().toJson(addLeadPost));

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                holder.mob_no.setText(contactData.getMob1());
                holder.at_mob.setText(contactData.getMob2());
                Log.e(TAG, "addLeadPost On Error" + new Gson().toJson(addLeadPost));

            }

            holder.email.setText(contactData.getEmail());
            addLeadPost.setEmail(contactData.getEmail());


            Log.e(TAG, "addLeadPost2::" + new Gson().toJson(addLeadPost));
            Log.v(TAG, "addLeadPost3:" + new Gson().toJson(fieldListAdapter.addLeadPost));

        }
    }

    @Override
    public void setTimeShiftList(final List<ShiftTimeReSModel> list, @NonNull FieldListAdapter.ViewHolder holder) {
        this.shiftList = list;
        try {
            if (timeSHiftAdpter == null) {
                AppUtility.spinnerPopUpWindow(holder.time_shift_dp);
                timeSHiftAdpter = new TimeSHiftAdpter(getActivity(), list);
                holder.time_shift_dp.setAdapter(timeSHiftAdpter);
            } else {
                timeSHiftAdpter.updtaeList(list);
            }

            holder.time_shift_dp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setDataInDateTimeField(shiftList.get(position), holder);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWorkerList(List<FieldWorker> fieldWorkerlist, @NonNull FieldListAdapter.ViewHolder holder) {
        AppUtility.autocompletetextviewPopUpWindow(holder.members);
        holder.members.setThreshold(1);
        DynamicClassAdapter myClassAdapter = new DynamicClassAdapter<>(getActivity(), R.layout.custom_adapter_item_layout, R.id.item_title_name, fieldWorkerlist);
        holder.members.setAdapter(myClassAdapter);
        holder.members.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!listwork.contains((((FieldWorker) adapterView.getItemAtPosition(i)).getUsrId()))) {
                addChips((FieldWorker) adapterView.getItemAtPosition(i), holder);
            }
            holder.linearMainView.setVisibility(View.VISIBLE);
            holder.members.setText("");
        });
    }

    private void addChips(final FieldWorker itemAtPosition, FieldListAdapter.ViewHolder holder) {//add chip for fieldworker to assign job
//        add id in list
        listwork.add(itemAtPosition.getUsrId());
        try {
            LayoutInflater vi = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.member_item_view, null);
//            memIds.add(itemAtPosition.getUsrId());
// fill in any details dynamically here

            TextView textView = v.findViewById(R.id.memberName);
            textView.setText(itemAtPosition.getFnm());
            ImageView deleteMember = v.findViewById(R.id.deleteMember);
            deleteMember.setOnClickListener(view -> {
                holder.linearMainView.removeView((LinearLayout) view.getParent());
                if (holder.linearMainView.getChildCount() <= 0) {
                    holder.linearMainView.setVisibility(View.GONE);
                }
                listwork.remove(itemAtPosition.getUsrId());
            });

// insert into main view

            holder.linearMainView.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void finishActivity() {
        Objects.requireNonNull(getActivity()).onBackPressed();
    }


    @Override
    public void set_Str_DTime(String str_dt_tm, String time_str, @NonNull FieldListAdapter.ViewHolder holder) {
        date_str = str_dt_tm;
        this.time_str = time_str;
        holder.date_start.setText(date_str);
        holder.time_start.setText(this.time_str);

        /*this for  add recur**/
        schdlStart = date_str + " " + time_str;

        addLeadPost.setLeadStartDate(schdlStart);
    }

    @Override
    public void set_str_DT_after_cur(String std, @NonNull FieldListAdapter.ViewHolder holder) {
        String[] time_duration = std.split(" ");
        date_str = time_duration[0];
        try {
            if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                    App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0"))
                time_str = time_duration[1] + " " + time_duration[2];
            else time_str = time_duration[1] + "";

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.date_start.setText(date_str);
        holder.time_start.setText(time_str);

        /*this for Add recur**/
        schdlStart = date_str + " " + time_str;
        addLeadPost.setLeadStartDate(schdlStart);

    }

    @Override
    public void set_End_Date_Time(String std, @NonNull FieldListAdapter.ViewHolder holder) {
        String[] time_duration = std.split(" ");
        date_en = time_duration[0];
        try {
            if (App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable() != null &&
                    App_preference.getSharedprefInstance().getLoginRes().getIs24hrFormatEnable().equals("0"))
                time_en = time_duration[1] + " " + time_duration[2];
            else time_en = time_duration[1] + " ";
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.date_end.setText(date_en);
        holder.time_end.setText(time_en);
        schdlEnd = date_en + " " + time_en;
        addLeadPost.setLeadEndDate(schdlEnd);

    }


    @Override
    public void setSetTagData(List<TagData> tagslist, @NonNull FieldListAdapter.ViewHolder holder) {
        this.tagslist = tagslist;
        if (tagslist.size() > 0) {
            AppUtility.autocompletetextviewPopUpWindow(holder.et_tag);
            holder.et_tag.setThreshold(1);
            DynamicClassAdapter myClassAdapter = new DynamicClassAdapter<>(getActivity(), R.layout.custom_adapter_item_layout, R.id.item_title_name, tagslist);
            holder.et_tag.setAdapter(myClassAdapter);
            holder.et_tag.setOnItemClickListener((adapterView, view, i, l) -> {
                addChipsForTags((TagData) adapterView.getItemAtPosition(i), holder);
                holder.et_tag.setText("");
            });
        }

    }

    public void addChipsForTags(final TagData tagData, FieldListAdapter.ViewHolder holder) {
        String tagName = "";
        for (int tag = 0; tag <= tagArray.size(); tag++) {
            if (!tagArray.contains(tagData)) {
                tagName = tagData.getName();
                tagArray.add(tagData);
                break;
            }
        }
        iTag++;
        try {
            LayoutInflater vi = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = vi.inflate(R.layout.member_item_view, null);

// fill in any details dynamically here
            TextView textView = v.findViewById(R.id.memberName);
            if ((tagName.length() > 0))
                textView.setText(tagName);

            ImageView deleteMember = v.findViewById(R.id.deleteMember);
            deleteMember.setOnClickListener(view -> {
                holder.linear_addTag.removeView((LinearLayout) view.getParent());
                for (int j = 0; j < tagArray.size(); j++) {
                    tagArray.remove(tagData);
                }
            });
// insert into main view

            if ((tagName.length() > 0)) {
                holder.linear_addTag.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void showErrorMsgsForValidation(String msg) {
        showErrorDialog(msg);
    }

    public void showErrorDialog(String msg) {
        AppUtility.error_Alert_Dialog(getActivity(), msg, LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok)
                , () -> null);
    }

    @Override
    public void setCountryList(List<Country> countryList, @NonNull final FieldListAdapter.ViewHolder holder) {
//        fieldListAdapter.setCountryList(countryList, holder);
        AppUtility.autocompletetextviewPopUpWindow(holder.auto_country);
        final FilterCountry countryAdapter = new FilterCountry(getContext(), R.layout.custom_adapter_item_layout, (ArrayList<Country>) countryList);
        holder.auto_country.setAdapter(countryAdapter);
        holder.auto_country.setThreshold(1);
        holder.auto_country.setOnItemClickListener((adapterView, view, i, l) -> {
            ctry_id = ((Country) adapterView.getItemAtPosition(i)).getId();
            addLead_pc.getStateList(ctry_id, holder);
            addLeadPost.setCtry(ctry_id);
            holder.job_country_layout.setHintEnabled(true);
        });

        holder.auto_country.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    holder.job_country_layout.setHintEnabled(true);
                } else if (charSequence.length() <= 0) {
                    holder.job_country_layout.setHintEnabled(false);
                }

                addLeadPost.setCtry(ctry_id);
                holder.auto_states.setText("");
                ctry_id = "";
                state_id = "";
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void setStateList(List<States> statesList, @NonNull final FieldListAdapter.ViewHolder holder) {
//        fieldListAdapter.setStateList(statesList, holder);
        FilterStates stateAdapter = new FilterStates(getContext(), R.layout.custom_adapter_item_layout, (ArrayList<States>) statesList);
        holder.auto_states.setAdapter(stateAdapter);
        holder.auto_country.setThreshold(0);
        holder.auto_states.setOnItemClickListener((adapterView, view, i, l) -> {
            state_id = ((States) adapterView.getItemAtPosition(i)).getId();
            holder.job_state_layout.setHintEnabled(true);
            addLeadPost.setState(state_id);
        });


        holder.auto_states.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    holder.job_state_layout.setHintEnabled(true);
                } else if (charSequence.length() <= 0) {
                    holder.job_state_layout.setHintEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }


    @Override
    public void afterTextChanged(Editable s) {
    }


    private void getImageFromGallray() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, CAPTURE_IMAGE_GALLARY);
    }

    private void takeimageFromGalary() {
        //allow upload file extension
        String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png",
                "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",//.doc & .docx
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",//.xls & .xlsx
                "application/pdf",//pdf
                "text/csv", "text/plain"//csv
        };

        /*only for document uploading */
        Intent documentIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        documentIntent.addCategory(Intent.CATEGORY_OPENABLE);
        documentIntent.setType("*/*");
        documentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(documentIntent, ATTACHFILE_CODE);
    }

    /**
     * get image from camera & edit & croping functinallity
     */
    private void takePictureFromCamera() {
        if (!AppUtility.askCameraTakePicture(getActivity())) {
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Eot Directory");

        if (!path.exists()) {
            path.mkdir();
        }

        File imageFile = null;
        try {
            imageFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert imageFile != null;
        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()), getActivity().getApplicationContext().getPackageName() + ".provider", imageFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivityForResult(takePictureIntent, CAMERA_CODE); // IMAGE_CAPTURE = 0
    }

    private File createImageFile() throws IOException {


        Calendar calendar = Calendar.getInstance();
        long imageFileName = calendar.getTime().getTime();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Eot Directory");

        File directoryPath = new File(storageDir.getPath());
        File image = File.createTempFile(
                String.valueOf(imageFileName),  /* prefix */
                ".jpg",         /* suffix */
                directoryPath   /* directory */
        );
        captureImagePath = image.getAbsolutePath();
        return new File(image.getPath());
    }

    @Override
    public void selectFileWithoutAttachment(int position, boolean isImage, CustomEditor editor) {
        this.editor = editor;
        this.position = position;

        if (!Utils.isOnline(Objects.requireNonNull(getActivity()))) {

            AppUtility.alertDialog(getActivity(), LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_error_title), LanguageController.getInstance().getMobileMsgByKey(AppConstant.feature_not_available), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", () -> null);
        } else {
            final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
            dialog.setContentView(R.layout.bottom_image_chooser);
            TextView camera = dialog.findViewById(R.id.camera);
            assert camera != null;
            camera.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.camera));
            TextView gallery = dialog.findViewById(R.id.gallery);
            Objects.requireNonNull(gallery).setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.gallery));

            LinearLayout ll_doc = dialog.findViewById(R.id.driveLayout);
            Objects.requireNonNull(ll_doc).setVisibility(View.GONE);

            TextView drive_document = dialog.findViewById(R.id.drive_document);
            Objects.requireNonNull(drive_document).setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.document));
            camera.setOnClickListener(view -> {
                if (AppUtility.askCameraTakePicture(getActivity())) {
                    takePictureFromCamera();
                }
                dialog.dismiss();
            });

            gallery.setOnClickListener(view -> {
                if (AppUtility.askGalaryTakeImagePermiSsion(getActivity())) {
                    getImageFromGallray();
                }
                dialog.dismiss();
            });

            drive_document.setOnClickListener(view -> {
                if (AppUtility.askGalaryTakeImagePermiSsion(getActivity())) {
                    takeimageFromGalary();//only for drive documents
                }
                dialog.dismiss();
            });
            dialog.show();
        }
    }

    @Override
    public void setTillDateForRecur(String tillDate, @NonNull FieldListAdapter.ViewHolder holder) {
        holder.radio_on.setChecked(true);
        holder.end_date_for_weekly_recur.setText(tillDate);
    }

    public void mon_week_day_click(FieldListAdapter.ViewHolder holder) {
        holder.mon_week_day.setSeleted(!holder.mon_week_day.isSeleted());
        if (holder.mon_week_day.isSeleted()) {
            weekDays.add("1");
            selectedDaysMap.put("monday", true);
        } else {
            selectedDaysMap.remove("monday");
            weekDays.remove("1");
        }
    }

    public void tu_week_day_click(FieldListAdapter.ViewHolder holder) {
        holder.tu_week_day.setSeleted(!holder.tu_week_day.isSeleted());
        if (holder.tu_week_day.isSeleted()) {
            selectedDaysMap.put("tuesday", true);
            weekDays.add("2");
        } else {
            selectedDaysMap.remove("tuesday");
            weekDays.remove("2");
        }
    }

    public void wed_week_day_click(FieldListAdapter.ViewHolder holder) {
        holder.wed_week_day.setSeleted(!holder.wed_week_day.isSeleted());
        if (holder.wed_week_day.isSeleted()) {
            weekDays.add("3");
            selectedDaysMap.put("wednesday", true);
        } else {
            selectedDaysMap.remove("wednesday");
            weekDays.remove("3");
        }
    }

    public void thu_week_day_click(FieldListAdapter.ViewHolder holder) {
        holder.thu_week_day.setSeleted(!holder.thu_week_day.isSeleted());
        if (holder.thu_week_day.isSeleted()) {
            weekDays.add("4");
            selectedDaysMap.put("thursday", true);
        } else {
            selectedDaysMap.remove("thursday");
            weekDays.remove("4");
        }
    }

    public void fri_week_day_click(FieldListAdapter.ViewHolder holder) {
        holder.fri_week_day.setSeleted(!holder.fri_week_day.isSeleted());
        if (holder.fri_week_day.isSeleted()) {
            weekDays.add("5");
            selectedDaysMap.put("friday", true);
        } else {
            selectedDaysMap.remove("friday");
            weekDays.remove("5");
        }
    }

    public void sat_week_day_click(FieldListAdapter.ViewHolder holder) {
        holder.sat_week_day.setSeleted(!holder.sat_week_day.isSeleted());
        if (holder.sat_week_day.isSeleted()) {
            weekDays.add("6");
            selectedDaysMap.put("saturday", true);
        } else {
            weekDays.remove("6");
            selectedDaysMap.remove("saturday");
        }
    }

    public void sun_week_day_click(FieldListAdapter.ViewHolder holder) {
        holder.sun_week_day.setSeleted(!holder.sun_week_day.isSeleted());
        if (holder.sun_week_day.isSeleted()) {
            weekDays.add("7");
            selectedDaysMap.put("sunday", true);
        } else {
            weekDays.remove("7");
            selectedDaysMap.remove("sunday");
        }
    }

    public void etTagTextWatcher(String toString, FieldListAdapter.ViewHolder holder) {
        holder.add_tag_btn.setVisibility(View.VISIBLE);
        if (toString.length() > 0) {
            if (tagslist != null) {
                for (TagData item : tagslist) {
                    if (item.getTnm().equals(toString)) {
                        holder.add_tag_btn.setVisibility(View.GONE);
                    }
                }
            }
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public interface DateTimeCallback {
        void setDateTime(String dateTime);
    }


    /***
     * call from image crop fragment **/
    public void callServiceForImage(Uri newUri) {
        String path;
        //  path = PathUtils.getPath(getActivity(), newUri);
        path = PathUtils.getRealPath(getActivity(), newUri);
        if (!path.isEmpty()) {
            fieldListAdapter.onDocumentSelected(path, true, editor);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        imageCroping(Uri.fromFile(new File(captureImagePath)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
                break;
            case CAPTURE_IMAGE_GALLARY:
                if (data == null) {
                    return;
                } else {
                    Uri uri = data.getData();
                    assert uri != null;
                    imageCroping(uri);
                }

                break;
            case ATTACHFILE_CODE:
                if (resultCode == RESULT_OK) {
                    Uri fileUri = data.getData();
                    // String filePath = PathUtils.getPath(getActivity(), fileUri);
                    String filePath = PathUtils.getRealPath(getActivity(), fileUri);
                    try {
                        String extension = filePath.substring(filePath.lastIndexOf("."));
                        //('jpg','png','jpeg','pdf','doc','docx','xlsx','csv','xls'); supporting extensions
                        if (extension.equals(".jpg") || extension.equals(".png") || extension.equals(".jpeg")) {
                            assert fileUri != null;
                            imageCroping(fileUri);
                        } else {
                            fieldListAdapter.onDocumentSelected(filePath, true, editor);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void imageCroping(final Uri uri) {
        ImageCropFragment myfragment = ImageCropFragment.newInstance("Uri", uri.toString());
        assert getParentFragmentManager() != null;
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        myfragment.setTargetFragment(this, 101);
        // myfragment.show(ft, "MyTag");
        myfragment.show(ft, "AddLeadFragment");
    }

    public void setCheckBoxOption(FieldListAdapter.ViewHolder holder, final int position, ArrayList<AfJsonResModel> typeList) {
        if (holder.linearCheck.getChildCount() > 0)
            holder.linearCheck.removeAllViews();

        for (final OptionModel optionModel : typeList.get(position).getOptions()) {
            final CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText("");
            checkBox.setText(optionModel.getValue());
            checkBox.setTag(optionModel);


            checkBox.setTextAppearance(getContext(), R.style.header_text_style);
            checkBox.setTypeface(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.arimo_regular));
            checkBox.setButtonTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.txt_sub_color)));

            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            holder.linearCheck.addView(checkBox);

            if (typeList.get(position).getAns() != null && !typeList.get(position).getAns().isEmpty()) {
                for (final AnswerModel answerModel : typeList.get(position).getAns()) {
                    if (optionModel.getKey().equals(answerModel.getKey())) {
                        checkBox.setChecked(true);
                        break;
                    }
                }
            } else checkBox.setChecked(false);

            checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                OptionModel selectedOptionModel = ((OptionModel) compoundButton.getTag());

                if (compoundButton.isChecked()) {
                    if (typeList.get(position).getAns() != null && typeList.get(position).getAns().size() > 0) {
                        List<AnswerModel> ans = typeList.get(position).getAns();
                        for (AnswerModel ansmodel : ans)
                            if (ansmodel.getKey().equals(selectedOptionModel.getKey()))
                                return;
                        ans.add(new AnswerModel(selectedOptionModel.getKey(), selectedOptionModel.getValue()));
                        typeList.get(position).setAns(ans);
                    } else {
                        List<AnswerModel> answerModelList = new ArrayList<>();
                        answerModelList.add(new AnswerModel(selectedOptionModel.getKey(), selectedOptionModel.getValue()));
                        typeList.get(position).setAns(answerModelList);
                    }
                } else {
                    if (typeList.get(position).getAns() != null && typeList.get(position).getAns().size() > 0) {
                        List<AnswerModel> ans = typeList.get(position).getAns();
                        for (AnswerModel ansmodel : ans)
                            if (ansmodel.getKey().equals(selectedOptionModel.getKey())) {
                                ans.remove(ansmodel);
                                typeList.get(position).setAns(ans);
                                return;
                            }
                    }
                }

            });
        }


    }

    public void setDropDownOptions(final FieldListAdapter.ViewHolder holder, final int position, ArrayList<AfJsonResModel> typeList) {
        if (typeList.get(position).getAns() != null && typeList.get(position).getAns().isEmpty())
            holder.spinner_text.setText("");
        else if (typeList.get(position).getAns().size() > 0)
            holder.spinner_text.setText(typeList.get(position).getAns().get(0).getValue());


        final MyAdapter<OptionModel> spinnerAdapter = new MyAdapter<>(getActivity(), R.layout.custom_adapter_item_layout, typeList.get(position).getOptions());
        holder.spinner.setAdapter(spinnerAdapter);


        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                List<AnswerModel> ans = new ArrayList<>();

                ans.add(new AnswerModel(((DropdownListBean) parent.getItemAtPosition(pos)).getKey(),
                        ((DropdownListBean) parent.getItemAtPosition(pos)).getName()));

                typeList.get(position).setAns(ans);

                String text = ((DropdownListBean) parent.getItemAtPosition(pos)).getName();
                holder.spinner_text.setText(text);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void setClientDataList(@NonNull final FieldListAdapter.ViewHolder holder) {
        AppUtility.autocompletetextviewPopUpWindow(holder.auto_client);
        FilterAdapter filter = new FilterAdapter(getContext(), R.layout.custom_adapter_item_layout, (ArrayList<Client>) clientList);
        holder.auto_client.setAdapter(filter);
        holder.auto_client.setThreshold(2);
        holder.auto_client.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (filterContact != null) {
                    filterContact.clearListData();
                    filterContact.notifyDataSetInvalidated();
                }

                new_cnm = charSequence.toString();
                if (charSequence.toString().isEmpty()) {
                    holder.ll_client_details.setVisibility(View.GONE);

                } else {
                    holder.ll_client_details.setVisibility(View.VISIBLE);

                    if (charSequence.length() >= 3) {
                        /*Default check On when Admin Allow permission for Save for future use***/
                        if (App_preference.getSharedprefInstance().getLoginRes().getIsClientForFutureEnable() != null) {
                            clientForFuture = contactForFuture = siteForFuture = 1;
                        } else {
                            clientForFuture = contactForFuture = siteForFuture = 0;
                        }
                    }
                }
                addLeadPost.setNm(charSequence.toString());
                if (addLeadPost.getCltId().isEmpty() && addLeadPost.getCltId().equals("0"))
                    addNewClient(holder);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("", "");
            }
        });

        holder.auto_client.setOnItemClickListener((adapterView, view, i, l) -> {
            addLeadPost.setNm((((Client) adapterView.getItemAtPosition(i))).getNm());
            holder.auto_client.setText((((Client) adapterView.getItemAtPosition(i))).getNm());
            setClientDataForSelection((((Client) adapterView.getItemAtPosition(i))), holder);
        });
    }

    private void addNewClient(FieldListAdapter.ViewHolder holder) {
        HideContractView(holder);
        if (new_cnm.length() > 0) {
            holder.auto_sites.setFocusableInTouchMode(false);
            holder.auto_sites.setFocusable(false);
            cltId = "";
            new_site_nm = "self";
            new_con_nm = "self";
            addLeadPost.setNm("");
            addLeadPost.setSnm("");
            addLeadPost.setCnm("");

            holder.site_dp_img.setClickable(false);
            holder.contact_dp_img.setClickable(false);

            // auto_sites.setText(new_site_nm);

            holder.cb_future_sites.setVisibility(View.GONE);
            holder.cb_future_contact.setVisibility(View.GONE);

            holder.edt_lat.setText("");
            holder.edt_lng.setText("");

            newContactForNewClient(holder);
            newSiteForNewClient(holder);

            holder.input_layout_site.setHintEnabled(true);
            holder.input_layout_contact.setHintEnabled(true);


            holder.auto_contact.setFocusableInTouchMode(false);
            holder.auto_contact.setFocusable(false);
            //   auto_contact.setText(new_con_nm);


            holder.contact_add_edt.setText("");
            holder.site_add_edt.setText("");

            //change for new client with add contact and site name
            holder.site_add_edt.setText(new_site_nm);
            holder.site_new_add_layout.setVisibility(View.VISIBLE);
            holder.site_dp_layout.setVisibility(View.GONE);


            holder.contact_add_edt.setText(new_con_nm);
            addLeadPost.setCnm(new_con_nm);
            holder.contact_new_add_layout.setVisibility(View.VISIBLE);
            holder.contact_dp_layout.setVisibility(View.GONE);
            holder.imvCross.setVisibility(View.GONE);


            holder.cb_future_client.setVisibility(View.VISIBLE);


        } else {
            holder.cb_future_client.setVisibility(View.GONE);
            holder.auto_sites.setText("");
            holder.auto_contact.setText("");
            holder.cb_future_client.setChecked(false);
            holder.auto_sites.setFocusableInTouchMode(true);
            clientForFuture = contactForFuture = siteForFuture = 0;
            addLeadPost.setClientForFuture(0);
            addLeadPost.setContactForFuture(0);
            addLeadPost.setSiteForFuture(0);
            holder.site_add_edt.setText("");
            holder.contact_add_edt.setText("");
        }
    }


    void newContactForNewClient(FieldListAdapter.ViewHolder holder) {
        holder.mob_no.setText("");
        holder.at_mob.setText("");
        holder.email.setText("");
        conId = "";
        addLeadPost.setMob2("");
        addLeadPost.setMob1("");
        addLeadPost.setEmail("");
        addLeadPost.setConId("");
    }

    private void newSiteForNewClient(FieldListAdapter.ViewHolder holder) {
        holder.auto_country.setText("");
        holder.auto_states.setText("");
        holder.post_code.setText("");
        holder.city.setText("");
        holder.adderes.setText("");
        holder.edt_lng.setText("");
        holder.edt_lat.setText("");
        siteId = "";

        addLeadPost.setZip("");
        addLeadPost.setCity("");
        addLeadPost.setLat("");
        addLeadPost.setLng("");
        addLeadPost.setAdr("");
        addLeadPost.setCtry("");
        addLeadPost.setState("");
        addLeadPost.setSiteId("");
    }

    /**
     * Hide View when New Client add
     ****/
    private void HideContractView(FieldListAdapter.ViewHolder holder) {
        clearContractData(holder);
        holder.contract_parent_view.setVisibility(View.GONE);
    }

    /**
     * Clear Selected Contract Data
     ****/
    private void clearContractData(FieldListAdapter.ViewHolder holder) {
        holder.contract_hint_lable.setVisibility(View.GONE);
        holder.contarct_lable.setText("");
        holder.contarct_lable.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.contract));
        holder.contract_cross_img.setVisibility(View.GONE);
        contrId = "";
        addLeadPost.setConId("");
    }


    private void setClientDataForSelection(Client clientModel, @NonNull final FieldListAdapter.ViewHolder holder) {
        holder.imvCross.setVisibility(View.GONE);
        holder.site_new_add_layout.setVisibility(View.GONE);
        holder.site_dp_layout.setVisibility(View.VISIBLE);
        cltId = clientModel.getCltId();

        addLeadPost.setCltId(cltId);

//        new_cnm = "";
        clientForFuture = 0;
        cltId = clientModel.getCltId();
        addLead_pc.getContractList(clientModel.getCltId(), holder);
        addLead_pc.getCOntactList(clientModel.getCltId(), holder);
        addLead_pc.getSiteList(clientModel.getCltId(), holder);


    }

    public void setRefrences(@NonNull final FieldListAdapter.ViewHolder holder) {
        AppUtility.spinnerPopUpWindow(holder.referenceDp);
        try {
            final List<ClientRefrenceModel> list = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).clientRefrenceDao().getRefrenceList();

            holder.referenceDp.setAdapter(new ClientRefrenceAdpter(getContext(), list));

            holder.referenceDp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    holder.tv_spinner_reference.setText(list.get(position).getRefName());
                    reference = Integer.parseInt(list.get(position).getRefId());
                    referenceName = list.get(position).getRefName();
                    addLeadPost.setSource(reference);
                    addLeadPost.setReferenceName(referenceName);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setCompanySettingAdrs(FieldListAdapter.ViewHolder holder) {
        holder.auto_country.setText(SpinnerCountrySite.getCountryNameById(App_preference.getSharedprefInstance().getCompanySettingsDetails().getCtry()));
        addLead_pc.getStateList((App_preference.getSharedprefInstance().getCompanySettingsDetails().getCtry()), holder);
        holder.job_state_layout.setHintEnabled(true);
        holder.auto_states.setText(SpinnerCountrySite.getStatenameById(App_preference.getSharedprefInstance().getCompanySettingsDetails().getCtry(), App_preference.getSharedprefInstance().getCompanySettingsDetails().getState()));
        state_id = (App_preference.getSharedprefInstance().getCompanySettingsDetails().getState());
        ctry_id = (App_preference.getSharedprefInstance().getCompanySettingsDetails().getCtry());
    }

    /**
     * Take time from picker for Question Type 6 & 7
     ***/
    public void getTimeFromPicker(Calendar myCalendar, final String queType, final TextView textView) {
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> {
                    String timeData = updateTime(hourOfDay, minute);

                    DecimalFormat formatter = new DecimalFormat("00");
                    String[] timeary = timeData.split(":");
                    timeData = ((formatter.format(Integer.parseInt(timeary[0]))) + ":" + timeary[1]);
                    if (queType.equals("TimeType6")) {
                        textView.setText(timeData);
                    } else if (queType.equals("TimeType7")) {
                        time = "";
                        time = timeData;
                        String newdateTime = date + " " + timeData;
                        try {
                            @SuppressLint("SimpleDateFormat") String s = new SimpleDateFormat(
                                    AppUtility.dateTimeByAmPmFormate("dd-MMM-yyyy hh:mm a",
                                            "dd-MMM-yyyy kk:mm")).format(
                                    Objects.requireNonNull(new SimpleDateFormat(
                                            AppUtility.dateTimeByAmPmFormate(
                                                    "dd-MM-yyyy hh:mm a", "dd-MM-yyyy kk:mm")).parse(newdateTime)));
                            textView.setText(s);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true
        );
        timePickerDialog.show();
    }


    public void selectStartDate(FieldListAdapter.ViewHolder holder) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialogSelectDate = new DatePickerDialog(Objects.requireNonNull(getActivity()), AppUtility.InputDateSet(getActivity(), dateTime -> {
            date_str = dateTime;
            if (time_str != null && time_str.equals("")) {
                Date date = new Date(System.currentTimeMillis());
                String formate = AppUtility.dateTimeByAmPmFormate("hh:mm aa", "kk:mm");
                SimpleDateFormat dateFormat = new SimpleDateFormat(formate,
                        Locale.getDefault());
                time_str = dateFormat.format(date);
            }
            schdlStart = date_str + " " + time_str;
            if (AppUtility.conditionCheckStartEnd(schdlStart, schdlEnd)) {
                EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_time));
            } else {
                holder.date_start.setText(dateTime);
                holder.date_end.setText(dateTime);
                date_en = date_str = dateTime;

                holder.time_start.setText(time_str);
                if (time_en != null && time_en.equals("")) {
                    addLead_pc.getEndTime(date_str, time_str, holder);
                }

                addLeadPost.setLeadStartDate(schdlStart);

            }
            /*set start date for Recurance only when add recurance permission Allow***/
            if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsRecur().equals("0")
                    && !holder.date_start.getText().toString().equals("") && holder.add_recur_checkBox.isChecked()) {
                if (holder.recur_pattern_view.getVisibility() == View.GONE)
                    holder.recur_pattern_view.setVisibility(View.VISIBLE);
                jobWeeklyRecur(holder);
            }

        }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_date)), year, month, day);
        datePickerDialogSelectDate.show();
    }

    public void selectStartTime(FieldListAdapter.ViewHolder holder) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AppUtility.InputTimeSet(getActivity(), dateTime -> {
            time_str = dateTime;
            DecimalFormat formatter = new DecimalFormat("00");
            String[] aa = dateTime.split(":");
            schdlStart = date_str + " " + time_str;

            if (AppUtility.conditionCheckStartEnd(schdlStart, schdlEnd)) {
                EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_time));
            } else {
                holder.time_start.setText((formatter.format(Integer.parseInt(aa[0]))) + ":" + aa[1]);
                addLeadPost.setLeadEndDate(schdlStart);
            }
        }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_time)), mHour, mMinute, true);

        timePickerDialog.show();
    }

    public void selectEndDate(FieldListAdapter.ViewHolder holder) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), AppUtility.CompareInputOutputDate(getActivity(), dateTime -> {
            holder.date_end.setText(dateTime);

            date_en = dateTime;

            if (time_en != null && time_en.equals("")) {
                Date date = new Date(System.currentTimeMillis());
                String formate = AppUtility.dateTimeByAmPmFormate("hh:mm aa", "kk:mm");
                SimpleDateFormat dateFormat = new SimpleDateFormat(formate,
                        Locale.getDefault());
                time_en = dateFormat.format(date);

            }
            schdlEnd = date_en + " " + time_en;

            if (AppUtility.conditionCheckStartEnd(schdlStart, schdlEnd)) {
                EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_time));
            } else {
                holder.time_end.setText(time_en);
                addLeadPost.setLeadEndDate(schdlEnd);
            }

        }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_due_start_date)), year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void selectEndTime(FieldListAdapter.ViewHolder holder) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog1 = new TimePickerDialog(getActivity(), AppUtility.OutPutTime(getActivity(), dateTime -> {
            time_en = dateTime;
            DecimalFormat formatter = new DecimalFormat("00");
            String[] aa = dateTime.split(":");
            schdlEnd = date_en + " " + time_en;

            if (AppUtility.conditionCheckStartEnd(schdlStart, schdlEnd)) {
                EotApp.getAppinstance().showToastmsg(LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_time));
            } else {
                holder.time_end.setText((formatter.format(Integer.parseInt(aa[0]))) + ":" + aa[1]);
                addLeadPost.setLeadEndDate(schdlEnd);
            }
        }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_due_start_time)), mHour, mMinute, true);
        timePickerDialog1.show();
    }

    public void getLocation(FieldListAdapter.ViewHolder holder) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                    getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST);
            } else {
                Location locationGPS = null;
                try {
                    if (Build.VERSION.SDK_INT >= 30) {
                        locationGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        HyperLog.i("Add_job_activity", "getLocation-" + "above version 11");
                        //provider = LocationManager.NETWORK_PROVIDER;
                    } else {
                        locationGPS = locationManager.getLastKnownLocation(GPS_PROVIDER);
                        HyperLog.i("Add_job_activity", "getLocation-" + "below version 10");
                        //  provider = GPS_PROVIDER;
                    }
                } catch (Exception exception) {
                    getLatLngCntr(holder);
                    exception.printStackTrace();
                    HyperLog.i("Add_job_activity", "getLocation-" + exception.getMessage());

                }


                if (locationGPS != null) {
                    double lat = locationGPS.getLatitude();
                    double longi = locationGPS.getLongitude();
                    try {
                        DecimalFormat dFormat = new DecimalFormat("#.######");
                        lat = parseDouble(dFormat.format(lat));
                        longi = parseDouble(dFormat.format(longi));
                    } catch (Exception e) {
                        e.printStackTrace();
                        lat = locationGPS.getLatitude();
                        longi = locationGPS.getLongitude();
                    }

                    String latitude = String.valueOf(lat);
                    String longitude = String.valueOf(longi);
                    holder.edt_lat.setText(latitude);
                    holder.edt_lng.setText(longitude);
                } else {
                    //Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
                    HyperLog.i("Add_job_activity", "getLocation-" + "Unable to find location.");
                    getLatLngCntr(holder);
                }
            }

        } catch (Exception exception) {
            HyperLog.i("Add_job_activity", exception.getMessage());
            getLatLngCntr(holder);
            exception.printStackTrace();
        }
    }

    private void getLatLngCntr(FieldListAdapter.ViewHolder holder) {
        CurrLatLngCntrlr.getInstance().getCurrLatLng((lat, lng) -> {
            holder.edt_lat.setText(lat);
            holder.edt_lng.setText(lng);
        });
    }

    public void getCurrentdateTime(FieldListAdapter.ViewHolder holder) {
        addLead_pc.getCurrentdateTime(holder);
    }

    public void setDataInDateTimeField(ShiftTimeReSModel shiftTimeReSModel, FieldListAdapter.ViewHolder holder) {
        try {
            date_str = AppUtility.getDateByFormat("dd-MM-yyyy");
            holder.date_start.setText(date_str);

            String[] startTime = shiftTimeReSModel.getShiftStartTime().split(":");


            String temptime_str = AppUtility.updateTime(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));

            DecimalFormat formatterStr = new DecimalFormat("00");
            String[] aa = temptime_str.split(":");
            time_str = ((formatterStr.format(Integer.parseInt(aa[0]))) + ":" + aa[1]);

            holder.time_start.setText(time_str);


            date_en = AppUtility.getDateByFormat("dd-MM-yyyy");
            String[] endTime = shiftTimeReSModel.getShiftEndTime().split(":");
            String temptime_en = AppUtility.updateTime(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1]));


            DecimalFormat formatterend = new DecimalFormat("00");
            String[] aaen = temptime_en.split(":");
            time_en = ((formatterend.format(Integer.parseInt(aaen[0]))) + ":" + aaen[1]);


            holder.date_end.setText(date_en);
            holder.time_end.setText(time_en);

            /*this for Add recur**/
            schdlStart = date_str + " " + time_str;
            schdlEnd = date_en + " " + time_en;
            addLeadPost.setLeadStartDate(schdlStart);
            addLeadPost.setLeadEndDate(schdlEnd);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void jobCustomRecurPattenrn() {
        if (AppUtility.isInternetConnected()) {
            showCustomRecurDialog();
        } else {
            showErrorDialog(LanguageController.getInstance().getMobileMsgByKey(AppConstant.offline_feature_alert));
        }
    }

    private void showCustomRecurDialog() {
        AppUtility.alertDialog2(getActivity(),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.custom_recur),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.add_custom_recur),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.yes),
                LanguageController.getInstance().getMobileMsgByKey(AppConstant.no)
                , new Callback_AlertDialog() {
                    @Override
                    public void onPossitiveCall() {
                        RECURTYPENORMAL_CUSTOM = 2;
                        Intent addJobrecrIntent = new Intent(getActivity(), AddJobRecrHomeActivity.class);
                        addJobrecrIntent.putExtra("AddJobScdlStartTime", date_str);
                        startActivityForResult(addJobrecrIntent, ADDCUSTOMRECUR);
                    }

                    @Override
                    public void onNegativeCall() {

                    }
                });
    }


    /***Recur Date Picker*******/
    public void selectRecurtillDate(FieldListAdapter.ViewHolder holder) {
        showDialogPicker(R.id.end_date_for_weekly_recur, holder);
    }

    @SuppressLint("NonConstantResourceId")
    public void showDialogPicker(int id, FieldListAdapter.ViewHolder holder) {
        switch (id) {
            case R.id.end_date_for_weekly_recur:

                DatePickerDialog endDateForjobRecur = new DatePickerDialog(Objects.requireNonNull(getActivity()), AppUtility.recurendDate(dateTime -> holder.end_date_for_weekly_recur.setText(dateTime)), year, month, day);

                endDateForjobRecur.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                endDateForjobRecur.show();
                break;

            case R.id.date_start:
                DatePickerDialog datePickerDialogSelectDate = new DatePickerDialog(Objects.requireNonNull(getActivity()), AppUtility.InputDateSet(getActivity(), dateTime -> {
                    holder.date_start.setText(dateTime);
                    holder.date_end.setText(dateTime);
                    date_en = date_str = dateTime;
                    if (time_str != null && time_str.equals("")) {
                        Date date = new Date(System.currentTimeMillis());
                        String formate = AppUtility.dateTimeByAmPmFormate("hh:mm aa", "kk:mm");
                        SimpleDateFormat dateFormat = new SimpleDateFormat(formate,
                                Locale.getDefault());
                        time_str = dateFormat.format(date);
                        holder.time_start.setText(time_str);
                    }

                    if (time_en != null && time_en.equals("")) {
                        addLead_pc.getEndTime(date_str, time_str, holder);
                    }

                    schdlStart = date_str + " " + time_str;


                    /*set start date for Recurance only when add recurance permission Allow***/
                    if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsRecur().equals("0")
                            && !holder.date_start.getText().toString().equals("") && holder.add_recur_checkBox.isChecked()) {
                        if (holder.recur_pattern_view.getVisibility() == View.GONE)
                            holder.recur_pattern_view.setVisibility(View.VISIBLE);
                        jobWeeklyRecur(holder);
                    }


                }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_date)), year, month, day);
                datePickerDialogSelectDate.show();
                break;

            case R.id.date_end:
                final DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), AppUtility.CompareInputOutputDate(getActivity(), dateTime -> {
                    holder.date_end.setText(dateTime);

                    date_en = dateTime;

                    if (time_en != null && time_en.equals("")) {
                        Date date = new Date(System.currentTimeMillis());
                        String formate = AppUtility.dateTimeByAmPmFormate("hh:mm aa", "kk:mm");
                        SimpleDateFormat dateFormat = new SimpleDateFormat(formate,
                                Locale.getDefault());
                        time_en = dateFormat.format(date);
                        holder.time_end.setText(time_en);
                    }
                }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_due_start_date)), year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;

            case R.id.time_start:
                @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AppUtility.InputTimeSet(getActivity(), dateTime -> {
                    time_str = dateTime;
                    DecimalFormat formatter = new DecimalFormat("00");
                    String[] aa = dateTime.split(":");
                    holder.time_start.setText((formatter.format(Integer.parseInt(aa[0]))) + ":" + aa[1]);
                    schdlStart = date_str + " " + time_str;
                }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_start_end_time)), mHour, mMinute, true);

                timePickerDialog.show();
                break;

            case R.id.time_end:
                @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog1 = new TimePickerDialog(getActivity(), AppUtility.OutPutTime(getActivity(), dateTime -> {
                    time_en = dateTime;
                    DecimalFormat formatter = new DecimalFormat("00");
                    String[] aa = dateTime.split(":");
                    holder.time_end.setText((formatter.format(Integer.parseInt(aa[0]))) + ":" + aa[1]);
                }, LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_due_start_time)), mHour, mMinute, true);
                timePickerDialog1.show();
                break;
        }
    }

    /***Enable All recur view for End Date*****/
    public void jobWeeklyRecur(FieldListAdapter.ViewHolder holder) {
        holder.recur_pattern_view.setVisibility(View.VISIBLE);
        holder.normal_weekly_recur.setVisibility(View.VISIBLE);
        holder.recur_view.setVisibility(View.VISIBLE);
        if (!holder.date_start.getText().toString().equals(""))
            addLead_pc.getTillDateForRecur(holder.date_start.getText().toString(), holder);

        holder.mon_week_day.setSeleted(false);
        holder.tu_week_day.setSeleted(false);
        holder.wed_week_day.setSeleted(false);
        holder.thu_week_day.setSeleted(false);
        holder.fri_week_day.setSeleted(false);
        holder.sat_week_day.setSeleted(false);
        holder.sun_week_day.setSeleted(false);
        endRecurMode = "2";

    }

    /****Hide Recur View When no need for Recur*******/
    public void setJobWeeklyRecurView(FieldListAdapter.ViewHolder holder) {
        holder.mon_week_day.setSeleted(false);
        holder.tu_week_day.setSeleted(false);
        holder.wed_week_day.setSeleted(false);
        holder.thu_week_day.setSeleted(false);
        holder.fri_week_day.setSeleted(false);
        holder.sat_week_day.setSeleted(false);
        holder.sun_week_day.setSeleted(false);

        weekDays.clear();
        selectedDays.clear();
        selectedDaysMap.clear();

        recudata.clear();
        recurType = "";
        recurMsg = "";
        isRecur = "";

    }

    public void setDateTimeEmptyTime(FieldListAdapter.ViewHolder holder) {
        holder.date_start.setText("");
        holder.time_start.setText("");
        holder.date_end.setText("");
        holder.time_end.setText("");
        holder.date_start.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.date_form));
        holder.time_start.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.time_form));
        holder.date_end.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.date_form));
        holder.time_end.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.time_form));
        schdlStart = date_str = time_str = date_en = time_en = "";

        if (App_preference.getSharedprefInstance().getLoginRes().getRights().get(0).getIsRecur().equals("0")) {
            holder.recur_pattern_view.setVisibility(View.GONE);
            setJobWeeklyRecurView(holder);
        }
        if (holder.add_recur_checkBox.isChecked())
            holder.add_recur_checkBox.performClick();
    }
}