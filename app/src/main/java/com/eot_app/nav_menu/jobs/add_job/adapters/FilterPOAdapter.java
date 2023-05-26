package com.eot_app.nav_menu.jobs.add_job.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.eot_app.R;
import com.eot_app.nav_menu.jobs.add_job.GetPoListResponse;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class FilterPOAdapter extends ArrayAdapter<GetPoListResponse> {

    int layoutResourceId;
    Context context;
    ArrayList<GetPoListResponse> array;
    ArrayList<GetPoListResponse> itemsAll;
    ArrayList<GetPoListResponse> suggestions;
    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((GetPoListResponse) (resultValue)).getPoNum();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (GetPoListResponse customer : itemsAll) {
                    if (customer.getPoNum().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            try {
                if (results != null && results.count > 0) {
                    List<GetPoListResponse> filteredList = (ArrayList<GetPoListResponse>) ((ArrayList<GetPoListResponse>) results.values).clone();
                    clear();
                    for (GetPoListResponse c : filteredList) {
                        add(c);
                    }
                    notifyDataSetChanged();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    };

    @SuppressWarnings("unchecked")
    public FilterPOAdapter(Context contextMylocationAdapter, int layoutResourceId, ArrayList<GetPoListResponse> arrayOfLocationPickUpDelivery) {
        super(contextMylocationAdapter, layoutResourceId, arrayOfLocationPickUpDelivery);
        this.layoutResourceId = layoutResourceId;
        this.context = contextMylocationAdapter;
        this.array = arrayOfLocationPickUpDelivery;
        this.itemsAll = (ArrayList<GetPoListResponse>) arrayOfLocationPickUpDelivery.clone();
        this.suggestions = new ArrayList<GetPoListResponse>();
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (vi != null)
                v = vi.inflate(layoutResourceId, null);
        }
        GetPoListResponse pickOrDropModel = array.get(position);
        if (pickOrDropModel != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.item_title_name);
            if (customerNameLabel != null) {
                Log.i("", "getView Customer Name:" + pickOrDropModel.getPoNum());
                customerNameLabel.setText(pickOrDropModel.getPoNum());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }


    public GetPoListResponse getSelectedItem(String itemName) {
        GetPoListResponse model = null;
        try {
            for (GetPoListResponse model1 : itemsAll) {
                if (model1.getPoNum().equals(itemName)) {
                    model = model1;
                    break;
                }
            }
            // model = itemsAll.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }
}
