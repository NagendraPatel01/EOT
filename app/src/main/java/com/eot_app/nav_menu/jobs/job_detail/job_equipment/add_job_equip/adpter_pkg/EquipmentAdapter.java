package com.eot_app.nav_menu.jobs.job_detail.job_equipment.add_job_equip.adpter_pkg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.eot_app.R;
import com.eot_app.nav_menu.jobs.job_db.EquArrayModel;

import java.util.ArrayList;

/**
 * Created by Sonam-11 on 30/9/20.
 */
public class EquipmentAdapter extends ArrayAdapter<EquArrayModel> {

    int layoutResourceId;
    Context contextMylocationAdapter;
    ArrayList<EquArrayModel> arrayOfLocationPickUpDelivery;
    ArrayList<EquArrayModel> itemsAll;
    ArrayList<EquArrayModel> suggestions;
    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((EquArrayModel) (resultValue)).getEqunm();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (EquArrayModel customer : itemsAll) {
                    if (customer.getEqunm().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
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
            ArrayList<EquArrayModel> filteredList = (ArrayList<EquArrayModel>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (EquArrayModel c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

    @SuppressWarnings("unchecked")
    public EquipmentAdapter(Context contextMylocationAdapter, int layoutResourceId, ArrayList<EquArrayModel> arrayOfLocationPickUpDelivery) {
        super(contextMylocationAdapter, layoutResourceId, arrayOfLocationPickUpDelivery);
        this.layoutResourceId = layoutResourceId;
        this.contextMylocationAdapter = contextMylocationAdapter;
        this.arrayOfLocationPickUpDelivery = arrayOfLocationPickUpDelivery;
        this.itemsAll = (ArrayList<EquArrayModel>) arrayOfLocationPickUpDelivery.clone();
        this.suggestions = new ArrayList<EquArrayModel>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(layoutResourceId, null);
        }

        EquArrayModel pickOrDropModel = arrayOfLocationPickUpDelivery.get(position);
        if (pickOrDropModel != null) {
            TextView customerNameLabel = v.findViewById(R.id.item_title_name);
            if (customerNameLabel != null) {
                Log.i("", "getView Customer Name:" + pickOrDropModel.getEqunm());
                customerNameLabel.setText(pickOrDropModel.getEqunm());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

}

