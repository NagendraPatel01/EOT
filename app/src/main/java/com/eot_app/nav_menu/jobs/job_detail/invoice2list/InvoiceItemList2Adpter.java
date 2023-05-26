package com.eot_app.nav_menu.jobs.job_detail.invoice2list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.eot_app.R;
import com.eot_app.login_next.login_next_model.CompPermission;
import com.eot_app.nav_menu.jobs.job_detail.addinvoiveitem2pkg.model.InvoiceItemDataModel;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.EotApp;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.util_interfaces.MyListItemSelected;
import com.eot_app.utility.util_interfaces.MyListItemSelectedLisT;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sonam-11 on 11/6/20.
 */
public class InvoiceItemList2Adpter extends RecyclerView.Adapter<InvoiceItemList2Adpter.MyViewHolder> {

    private final Set<String> nm_list;
    private final Set<String> ids_list;
    private MyListItemSelectedLisT<InvoiceItemDataModel> invoce_rm_item;
    private final ArrayList<InvoiceItemDataModel> removeInvoiceItemArrayList = new ArrayList<>();
    private final Context context;
    Set<Boolean> chk_pos;
    MyListItemSelected<InvoiceItemDataModel> myListItemSelected;
    private List<InvoiceItemDataModel> invoiceItemList;
    private boolean[] is_pos_checked;
    boolean is_hide_checkBox = false;
    boolean is_click_disable = false;
    private String taxCalculationType = "0";
    EditText qtyedit;
    String jobId;
    private String getDisCalculationType;

    /****This for FORM - 1 **/
    public InvoiceItemList2Adpter(Context context, List<InvoiceItemDataModel> invoiceItemList,String getDisCalculationType) {
        this.invoiceItemList = invoiceItemList;
        this.is_pos_checked = new boolean[invoiceItemList.size()];
        nm_list = new HashSet<>();
        ids_list = new HashSet<>();
        chk_pos = new HashSet<>();
        this.invoce_rm_item = ((MyListItemSelectedLisT)context);
        this.myListItemSelected = ((MyListItemSelected<InvoiceItemDataModel>) context);
        this.taxCalculationType = App_preference.getSharedprefInstance().getLoginRes().getTaxCalculationType();
        this.context = context;
        this.getDisCalculationType=getDisCalculationType;
    }

    /****This for FORM - 1 **/
    public InvoiceItemList2Adpter(Context context, List<InvoiceItemDataModel> invoiceItemList, boolean is_hide_checkBox, String jobId,String getDisCalculationType) {
        this.invoiceItemList = invoiceItemList;
        this.context = context;
        this.jobId = jobId;
        nm_list = new HashSet<>();
        ids_list = new HashSet<>();
        chk_pos = new HashSet<>();
        this.is_hide_checkBox = is_hide_checkBox;
        this.getDisCalculationType=getDisCalculationType;
    }

    /****This for eq item **/
    public InvoiceItemList2Adpter(Context context, List<InvoiceItemDataModel> invoiceItemList, boolean is_hide_checkBox, boolean is_click_disable,String getDisCalculationType) {
        this.invoiceItemList = invoiceItemList;
        this.is_pos_checked = new boolean[invoiceItemList.size()];
        this.context = context;
        nm_list = new HashSet<>();
        ids_list = new HashSet<>();
        chk_pos = new HashSet<>();
        this.is_hide_checkBox = is_hide_checkBox;
        this.is_click_disable = is_click_disable;
        this.getDisCalculationType=getDisCalculationType;
    }

    /****This for FORM - 1 **/
    public void updateitemlist(List<InvoiceItemDataModel> invoiceItemList) {
        this.is_pos_checked = new boolean[invoiceItemList.size()];
        this.invoiceItemList = invoiceItemList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public InvoiceItemList2Adpter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /* ***This for FORM - 1 **/
        View view;
        if (!App_preference.getSharedprefInstance().getLoginRes().getIsJobItemQuantityFormEnable().equals("1")) {
            if (is_hide_checkBox) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_adpter, viewGroup, false);
            } else{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_list_item_adpter, viewGroup, false);
            }
        } else {
            /* ***This for FORM - 2 Update QTY Form **/
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_item_list_adpter, viewGroup, false);
        }

        return new InvoiceItemList2Adpter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final InvoiceItemList2Adpter.MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int position) {
        /* ***This for FORM - 1 Default **/

        if (!App_preference.getSharedprefInstance().getLoginRes().getIsJobItemQuantityFormEnable().equals("1")) {

            if (!invoiceItemList.get(position).getInm().equals(""))
                myViewHolder.item_nm_invoice.setText(invoiceItemList.get(position).getInm());
            else myViewHolder.item_nm_invoice.setText(invoiceItemList.get(position).getTempNm());

            /* **ShoW unit when Unit permission allow by Administrator ***/
            if (!App_preference.getSharedprefInstance().getLoginRes().getCompPermission().get(0).getUnit().equals("1") && invoiceItemList.get(position).getUnit() != null && !invoiceItemList.get(position).getUnit().equals("")) {
                myViewHolder.qty_invoice.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.unit) + ": " + invoiceItemList.get(position).getUnit());
            } else {
                myViewHolder.qty_invoice.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.qty) + ": " + invoiceItemList.get(position).getQty());
            }

            try {
                if (invoiceItemList.get(position).getIsBillable().equals("0")) {
                    myViewHolder.non_billable.setVisibility(View.VISIBLE);
                    myViewHolder.non_billable.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.non_billable));
                } else {
                    myViewHolder.non_billable.setVisibility(View.GONE);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            myViewHolder.item_price_invoice.setText(
                    AppUtility.getRoundoff_amount
                            (AppUtility.getCalculatedAmountfordiscount(invoiceItemList.get(position).getQty(),
                                    invoiceItemList.get(position).getRate(),
                                    invoiceItemList.get(position).getDiscount(),
                                    invoiceItemList.get(position).getTax(),
                                    taxCalculationType,getDisCalculationType)));

            /* **update Item's base on Permissiom**/
            myViewHolder.item_layout.setOnClickListener(view -> {
                if (is_hide_checkBox&&is_click_disable) {
                   //Do nothing
                }
                else if (is_hide_checkBox&&!is_click_disable) {
                    Intent intent = new Intent(context, JoBInvoiceItemList2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("JobId", jobId);
                    context.startActivity(intent);
                } else
                    myListItemSelected.onMyListitemSeleted(invoiceItemList.get(position));
            });


            /* **remove this Jit Sir on 23 oct 2020**/
            if (App_preference.getSharedprefInstance().getLoginRes().getIsItemDeleteEnable().equals("0") || is_hide_checkBox) {
                myViewHolder.checkbox_invoice.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(-10, 0, 0, 0);
                myViewHolder.relativeLayout.setLayoutParams(params);
            } else {
                myViewHolder.checkbox_invoice.setVisibility(View.VISIBLE);
            }


            /* *Remove selected Item's****/
            myViewHolder.checkbox_invoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    is_pos_checked[position] = b;
                    if (b) {
                        ids_list.add(invoiceItemList.get(myViewHolder.getBindingAdapterPosition()).getItemId());
                        removeInvoiceItemArrayList.add(invoiceItemList.get(myViewHolder.getBindingAdapterPosition()));
                    } else {
                        ids_list.remove(invoiceItemList.get(myViewHolder.getBindingAdapterPosition()).getItemId());
                        removeInvoiceItemArrayList.remove(invoiceItemList.get(myViewHolder.getBindingAdapterPosition()));
                    }
                    invoce_rm_item.onMyListitem_Item_Seleted(removeInvoiceItemArrayList);
                }
            });

            myViewHolder.checkbox_invoice.setChecked(is_pos_checked[position]);

            if (invoiceItemList.get(position).getIjmmId().equals("")) {
                myViewHolder.des.setVisibility(View.VISIBLE);
                myViewHolder.des.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.item_not_sync));
                myViewHolder.des.setTextColor(EotApp.getAppinstance().getResources().getColor(android.R.color.holo_red_light));
            } else {
                myViewHolder.des.setVisibility(View.GONE);
            }

              /*Jit, 17:43 19-10-21
        Hi @all,
        items me  desc show karwana hai*/
            try {
                if (invoiceItemList.get(position).getDes() != null && !invoiceItemList.get(position).getDes().equals("")) {
                    myViewHolder.description.setVisibility(View.VISIBLE);
                    myViewHolder.description.setText(invoiceItemList.get(position).getDes());
                } else {
                    myViewHolder.description.setVisibility(View.GONE);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }


        } else {
            /****This for FORM - 2 Update QTY Form **/

            if (!invoiceItemList.get(position).getInm().equals(""))
                myViewHolder.item_nm.setText(invoiceItemList.get(position).getInm());
            else myViewHolder.item_nm.setText(invoiceItemList.get(position).getTempNm());


            try {
                if (invoiceItemList.get(position).getIsBillable().equals("0")) {
                    myViewHolder.non_billable.setVisibility(View.VISIBLE);
                    myViewHolder.non_billable.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.non_billable));
                } else {
                    myViewHolder.non_billable.setVisibility(View.GONE);

                }
            } catch (Exception exception) {
                exception.getMessage();
            }


            myViewHolder.price.setText(
                    AppUtility.getRoundoff_amount
                            (AppUtility.getCalculatedAmountfordiscount(invoiceItemList.get(position).getQty(),
                                    invoiceItemList.get(position).getRate(),
                                    invoiceItemList.get(position).getDiscount(),
                                    invoiceItemList.get(position).getTax(),
                                    App_preference.getSharedprefInstance().getLoginRes().getTaxCalculationType(),getDisCalculationType)));


            if (myViewHolder.des != null) {
                if (invoiceItemList.get(position).getIjmmId().equals("")) {
                    myViewHolder.des.setVisibility(View.VISIBLE);
                    myViewHolder.des.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.item_not_sync));
                    myViewHolder.des.setTextColor(EotApp.getAppinstance().getResources().getColor(android.R.color.holo_red_light));
                } else {
                    myViewHolder.des.setVisibility(View.GONE);
                }
            }

// for showing in the job detail screen

            if(is_hide_checkBox)
            {
                myViewHolder.item_layout2.setBackgroundColor(ContextCompat.getColor(context,R.color.bg_job));
                myViewHolder.qty_edit.setEnabled(false);
            }
            else {
                myViewHolder.item_layout2.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
                myViewHolder.qty_edit.setEnabled(true);
            }


            myViewHolder.qty_edit.setText(invoiceItemList.get(position).getQty());

                   /*Jit, 17:43 19-10-21
        Hi @all,
        items me  desc show karwana hai*/
            try {
                myViewHolder.description.setVisibility(View.GONE);
            } catch (Exception exception) {
                exception.printStackTrace();
            }


            myViewHolder.qty_edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    invoiceItemList.get(position).setQty(myViewHolder.qty_edit.getText().toString());

                    myViewHolder.price.setText(
                            AppUtility.getRoundoff_amount
                                    (AppUtility.getCalculatedAmountfordiscount(invoiceItemList.get(position).getQty(),
                                            invoiceItemList.get(position).getRate(),
                                            invoiceItemList.get(position).getDiscount(),
                                            invoiceItemList.get(position).getTax(),
                                            App_preference.getSharedprefInstance().getLoginRes().getTaxCalculationType(),getDisCalculationType)));
                }


                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return invoiceItemList.size();
    }

    /*****/

    public List<InvoiceItemDataModel> getItemList() {
        return invoiceItemList;
    }

    /****This for FORM - 1 **/
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView non_billable;
        private final TextView des;
        private final TextView description;
        /****This for FORM - 1 **/
        private TextView item_nm_invoice;
        private TextView qty_invoice;
        private TextView item_price_invoice;
        private CheckBox checkbox_invoice;
        private RelativeLayout item_layout, relativeLayout;
       LinearLayout item_layout2;
        /****This for FORM - 2 Update QTY Form **/
        private TextView item_nm, price;
        private EditText qty_edit;
        LinearLayout linear_lay_billable;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            /****This for FORM - 1 **/
            //TODO
            CompPermission compPermission = App_preference.getSharedprefInstance().getLoginRes().getCompPermission().get(0);

            if (!App_preference.getSharedprefInstance().getLoginRes().getIsJobItemQuantityFormEnable().equals("1")) {
                item_nm_invoice = itemView.findViewById(R.id.item_nm_invoice);
                qty_invoice = itemView.findViewById(R.id.qty_invoice);
                item_price_invoice = itemView.findViewById(R.id.item_price_invoice);

                if (compPermission.getAmount().equals("1")) {
                    item_price_invoice.setVisibility(View.GONE);
                }
                else {
                    item_price_invoice.setVisibility(View.VISIBLE);
                }
                checkbox_invoice = itemView.findViewById(R.id.checkbox_invoice);
                des = itemView.findViewById(R.id.des);
                description = itemView.findViewById(R.id.description);
                item_layout = itemView.findViewById(R.id.item_layout);
                relativeLayout = itemView.findViewById(R.id.relativeLayout);
                non_billable = itemView.findViewById(R.id.non_billable);
                linear_lay_billable = itemView.findViewById(R.id.linear_lay_billable);
            } else {
                /****This for FORM - 2 Update QTY Form **/
                item_layout2 = itemView.findViewById(R.id.item_layout);
                item_nm = itemView.findViewById(R.id.item_nm);
                price = itemView.findViewById(R.id.price);

                if (compPermission.getAmount().equals("1")) {
                    price.setVisibility(View.GONE);
                }
                else {
                    price.setVisibility(View.VISIBLE);
                }
                qty_edit = itemView.findViewById(R.id.qty_edit);
                des = itemView.findViewById(R.id.des);
                description = itemView.findViewById(R.id.description);

                qtyedit = qty_edit;
                non_billable = itemView.findViewById(R.id.non_billable);

            }

        }

    }
}
