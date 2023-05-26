package com.eot_app.nav_menu.jobs.job_detail.generate_invoice.invoice_adpter_pkg;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eot_app.R;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_detail_pkg.inv_detail_model.ItemData;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.App_preference;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.util_interfaces.MyListItemSelected;
import com.eot_app.utility.util_interfaces.MyListItemSelectedLisT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Generate_Invoice_list_Adpter extends RecyclerView.Adapter<Generate_Invoice_list_Adpter.MyViewHolder> {

    //private static final int NUMBER_OF_BLANK_CARDS = 2;
    Set<Boolean> chk_pos;
    MyListItemSelected<ItemData> myListItemSelected;
    private List<ItemData> invoiceItemList;
    Context context;
    private boolean[] is_pos_checked;
    Set<String> nm_list;
    Set<String> ids_list;
    MyListItemSelectedLisT<ItemData> invoce_rm_item;
    ArrayList<ItemData> itemDataArrayList = new ArrayList<>();
    private String taxCalculationType;


    public Generate_Invoice_list_Adpter(Context context, List<ItemData> invoiceItemList) {//, MyListItemSelected<ItemData> myListItemSelected, MyListItemSelectedLisT<ItemData> invoce_rm_item
        this.invoiceItemList = invoiceItemList;
        this.context = context;
//        this.myListItemSelected = myListItemSelected;
//        this.invoce_rm_item = invoce_rm_item;
        this.myListItemSelected=((MyListItemSelected<ItemData>)context);
        this.invoce_rm_item=((MyListItemSelectedLisT<ItemData>)context);
        this.is_pos_checked = new boolean[invoiceItemList.size()];
        nm_list = new HashSet<>();
        ids_list = new HashSet<>();
        chk_pos = new HashSet<>();

    }

    public void updateitemlist(List<ItemData> invoiceItemList,String taxCalculationType) {
        this.is_pos_checked = new boolean[invoiceItemList.size()];
        this.invoiceItemList = invoiceItemList;
        this.taxCalculationType=taxCalculationType;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.generate_invoice_list_adapter, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        if (position >= invoiceItemList.size()) {
            myViewHolder.card_invoice_item.setVisibility(View.INVISIBLE);
        } else {
            myViewHolder.card_invoice_item.setVisibility(View.VISIBLE);
            myViewHolder.item_nm_invoice.setText(invoiceItemList.get(position).getInm());
            String qty = AppUtility.getRoundoff_amount(invoiceItemList.get(position).getQty());
            myViewHolder.qty_invoice.setText(LanguageController.getInstance().getMobileMsgByKey(AppConstant.qty)+": " + qty);
            myViewHolder.item_price_invoice.setText(AppUtility.getRoundoff_amount(AppUtility.getCalculatedAmount(invoiceItemList.get(position).getQty(), invoiceItemList.get(position).getRate(), invoiceItemList.get(position).getDiscount(), invoiceItemList.get(position).getTax(),taxCalculationType)));


            myViewHolder.item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myListItemSelected.onMyListitemSeleted(invoiceItemList.get(position));
                }
            });

            if (App_preference.getSharedprefInstance().getLoginRes().getCompPermission().get(0).getIsItemEnable().equals("1")) {
                myViewHolder.checkbox_invoice.setVisibility(View.GONE);
            }

            myViewHolder.checkbox_invoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    is_pos_checked[position] = b;
                    if (b) {
                        ids_list.add(invoiceItemList.get(myViewHolder.getBindingAdapterPosition()).getItemId());
                        itemDataArrayList.add(invoiceItemList.get(myViewHolder.getBindingAdapterPosition()));
                    } else {
                        ids_list.remove(invoiceItemList.get(myViewHolder.getBindingAdapterPosition()).getItemId());
                        itemDataArrayList.remove(invoiceItemList.get(myViewHolder.getBindingAdapterPosition()));
                    }


                    invoce_rm_item.onMyListitem_Item_Seleted(itemDataArrayList);
                }
            });

            myViewHolder.checkbox_invoice.setChecked(is_pos_checked[position]);
        }
    }


    @Override
    public int getItemCount() {
        return invoiceItemList.size() ;//+ NUMBER_OF_BLANK_CARDS;
    }

    public int getListItemCount() {
        return invoiceItemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item_nm_invoice, qty_invoice, item_price_invoice;
        CardView card_invoice_item;
        CheckBox checkbox_invoice;
        RelativeLayout item_layout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_nm_invoice = itemView.findViewById(R.id.item_nm_invoice);
            qty_invoice = itemView.findViewById(R.id.qty_invoice);
            item_price_invoice = itemView.findViewById(R.id.item_price_invoice);
            checkbox_invoice = itemView.findViewById(R.id.checkbox_invoice);

            card_invoice_item = itemView.findViewById(R.id.card_invoice_item);
            item_layout = itemView.findViewById(R.id.item_layout);

        }
    }
}