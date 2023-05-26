package com.eot_app.nav_menu.jobs.job_detail.customform;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eot_app.R;
import com.eot_app.activitylog.ActivityLogController;
import com.eot_app.nav_menu.jobs.job_db.Job;
import com.eot_app.nav_menu.jobs.job_db.JtId;
import com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_model.CustomFormList_Res;
import com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_mvp.Cstm_Form_Pc;
import com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_mvp.Cstm_Form_View;
import com.eot_app.nav_menu.jobs.job_detail.customform.cstm_form_mvp.Custm_Form_Pi;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.FormQueAns_Activity;
import com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.ans_model.Answer;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.Language;
import com.eot_app.utility.db.AppDataBase;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.util_interfaces.Callback_AlertDialog;
import com.eot_app.utility.util_interfaces.MyListItemSelected;
import com.eot_app.utility.util_interfaces.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CustomFormListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomFormListFragment extends Fragment implements MyListItemSelected<CustomFormList_Res>, Cstm_Form_View {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SwipeRefreshLayout swiperefresh;
    RecyclerView customform_list;
    private AdapterCustomFormList adapter;
    // TODO: Rename and change types of parameters
    private List<CustomFormList_Res> mParam1;
    private String mParam2;
    View em_layout;
    TextView nolist_txt;
    private int Requestcode=147;
    private OnFragmentInteractionListener mListener;
    private Custm_Form_Pi custmFormPi;
    private BroadcastReceiver fromUpdate=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(mParam2);
            ArrayList<String> jTitleId = null;
            try {
                jTitleId = new ArrayList<>();
                for (JtId jobTitleId : job.getJtId()) {
                    jTitleId.add(jobTitleId.getJtId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            custmFormPi.getFormListBYSwipe(mParam2, jTitleId);
        }
    };

    public CustomFormListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomFormListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomFormListFragment newInstance(List<CustomFormList_Res> param1, String param2) {
        CustomFormListFragment fragment = new CustomFormListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, (ArrayList<? extends Parcelable>) param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getParcelableArrayList(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void setFormList(ArrayList<CustomFormList_Res> customFormLists) {
        try {
            if (customFormLists != null) {
                if (swiperefresh != null) {
                    if (swiperefresh.isRefreshing())
                        swiperefresh.setRefreshing(false);
                }
                ArrayList<CustomFormList_Res> tempList = new ArrayList<>();
                for (CustomFormList_Res item : customFormLists) {
                    if (!item.getTotalQues().equals("0") && !item.getTab().equals("0")) {
                        tempList.add(item);
                    }
                }
                if (adapter != null) {
                    adapter.updateFormList(tempList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ActivityLogController.saveActivity(
                ActivityLogController.JOB_MODULE, ActivityLogController.JOB_CUSTOM_FORM_LIST,
                ActivityLogController.JOB_MODULE
        );
        View view = inflater.inflate(R.layout.fragment_custom_form_list, container, false);

        customform_list = view.findViewById(R.id.customform_list);
        em_layout = view.findViewById(R.id.em_layout);
        nolist_txt = view.findViewById(R.id.nolist_txt);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        customform_list.setLayoutManager(layoutManager);
        if (mParam1 != null && mParam1.size() > 0) {
            adapter = new AdapterCustomFormList(mParam1, this);
            customform_list.setAdapter(adapter);
        } else {
            customform_list.setVisibility(View.GONE);
            em_layout.setVisibility(View.VISIBLE);
            nolist_txt.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.no_form_added_for_this_job));
        }

        swiperefresh = view.findViewById(R.id.swiperefresh);
        custmFormPi = new Cstm_Form_Pc(this);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swiperefresh.setRefreshing(true);
                Job job = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).jobModel().getJobsById(mParam2);
                ArrayList<String> jTitleId = null;
                try {
                    jTitleId = new ArrayList<>();
                    for (JtId jobTitleId : job.getJtId()) {
                        jTitleId.add(jobTitleId.getJtId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
               // custmFormPi.getFormListBYSwipe(mParam2, jTitleId);
            }
        });
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(fromUpdate,
                new IntentFilter("FormSubmittedToServer"));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMyListitemSeleted(CustomFormList_Res customFormList_res) {
        if (AppUtility.isInternetConnected()) {
            Intent intent = new Intent(getActivity(), FormQueAns_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("formId", customFormList_res);
            intent.putExtra("jobId", mParam2);

            startActivityForResult(intent, Requestcode);
        }else{
            String formSubmitedorNot = AppDataBase.getInMemoryDatabase(EotApp.getAppinstance()).customFormSubmitedDao().getFormSubmitedorNot(customFormList_res.getFrmId(), mParam2, App_preference.getSharedprefInstance().getLoginRes().getUsrId());
            if (formSubmitedorNot!=null&&formSubmitedorNot.equals("1"))
            {

                AppUtility.alertDialog2(getActivity(),
                        "Alert Message",
                        "This form is already submitted If you resubmit the form in offline mode then it will be overwritten your old(saved) data.",
                        LanguageController.getInstance().getMobileMsgByKey(AppConstant.yes),
                        LanguageController.getInstance().getMobileMsgByKey(AppConstant.no), new Callback_AlertDialog() {
                            @Override
                            public void onPossitiveCall() {
                                Intent intent = new Intent(getActivity(), FormQueAns_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                intent.putExtra("formId", customFormList_res);
                                intent.putExtra("jobId", mParam2);
                                startActivityForResult(intent,Requestcode);
                            }

                            @Override
                            public void onNegativeCall() {

                            }
                        });
            }else{
                Intent intent = new Intent(getActivity(), FormQueAns_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("formId", customFormList_res);
                intent.putExtra("jobId", mParam2);
                startActivityForResult(intent,Requestcode);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode==Requestcode){
            if (data!=null&&data.hasExtra("isDelete"))
            {
                adapter.refresh(data.getStringExtra("fmid"),data.getBooleanExtra("isDelete",false));
            }else if (data!=null&&data.hasExtra("isSumbitted")){
                adapter.refreshForSubmit(data.getStringExtra("fmid"),data.getBooleanExtra("isSumbitted",false));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
