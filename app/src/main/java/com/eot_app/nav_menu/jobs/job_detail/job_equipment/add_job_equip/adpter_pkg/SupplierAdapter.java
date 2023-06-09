package com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.adpter_pkg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.eot_app.R;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.model_pkg.GetCatgData;
import com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.model_pkg.GetSupplierData;

import java.util.ArrayList;
import java.util.List;

public class SupplierAdapter extends ArrayAdapter<GetSupplierData> {


    int layoutResourceId;
    Context contextMylocationAdapter;
    ArrayList<GetSupplierData> arrayOfLocationPickUpDelivery;
    ArrayList<GetSupplierData> itemsAll;
    ArrayList<GetSupplierData> suggestions;
    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((GetSupplierData) (resultValue)).getSupName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (GetSupplierData customer : itemsAll) {
                    if (customer.getSupName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                FilterResults filterResults = new FilterResults();
                filterResults.values = itemsAll;
                filterResults.count =itemsAll.size();
                return filterResults;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            @SuppressWarnings("unchecked")
            ArrayList<GetSupplierData> filteredList = (ArrayList<GetSupplierData>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (GetSupplierData c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

    @SuppressWarnings("unchecked")
    public SupplierAdapter(Context contextMylocationAdapter, int layoutResourceId, ArrayList<GetSupplierData> arrayOfLocationPickUpDelivery) {
        super(contextMylocationAdapter, layoutResourceId, arrayOfLocationPickUpDelivery);
        this.layoutResourceId = layoutResourceId;
        this.contextMylocationAdapter = contextMylocationAdapter;
        this.arrayOfLocationPickUpDelivery = arrayOfLocationPickUpDelivery;
        this.itemsAll = (ArrayList<GetSupplierData>) arrayOfLocationPickUpDelivery.clone();
        this.suggestions = new ArrayList<GetSupplierData>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(layoutResourceId, null);
        }

        GetSupplierData pickOrDropModel = arrayOfLocationPickUpDelivery.get(position);
        if (pickOrDropModel != null) {
            TextView customerNameLabel = v.findViewById(R.id.item_title_name);
            if (customerNameLabel != null) {
                Log.i("", "getView Customer Name:" + pickOrDropModel.getSupName());
                customerNameLabel.setText(pickOrDropModel.getSupName());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    public void setnewlist(List<GetSupplierData> supplierDataList) {
        if (arrayOfLocationPickUpDelivery!=null)
        {
            arrayOfLocationPickUpDelivery.clear();
            arrayOfLocationPickUpDelivery.addAll(supplierDataList);
            notifyDataSetChanged();
        }
    }
}
