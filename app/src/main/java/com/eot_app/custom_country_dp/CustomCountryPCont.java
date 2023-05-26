package com.eot_app.custom_country_dp;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eot_app.R;
import com.eot_app.utility.Country;
import com.eot_app.utility.EotApp;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Sonam-11 on 17/8/20.
 */
public class CustomCountryPCont {
    private PopupWindow countryPopupWindow;
    private List<Country> countryArrayList;
    private CustomCountryAdpter countryAdpter;
    private CountrySelctListn itemListener;


    public void showCountries(final Context context, View view, List<Country> countryArrayList) {
        this.countryArrayList = countryArrayList;
        itemListener = (CountrySelctListn) context;
        if (countryPopupWindow == null) {

            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.catrgory_list, null);

            countryPopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            countryPopupWindow.setHeight(getintToPX());
            countryPopupWindow.setWidth(width - 60);

            countryPopupWindow.setAnimationStyle(R.anim.bounce_down);

            countryPopupWindow.setOutsideTouchable(true);
            countryPopupWindow.setFocusable(true);

            if (Build.VERSION.SDK_INT >= 21) {
                countryPopupWindow.setElevation(5.0f);
            }


            final EditText edtSearch = customView.findViewById(R.id.edtSearch);
            RecyclerView recyclerView = customView.findViewById(R.id.recyclepop);
            final ImageView imvCross = customView.findViewById(R.id.imvCross);

            imvCross.setOnClickListener(v -> edtSearch.getText().clear());

            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.e("", "");
                    if (charSequence.length() > 0) {
                        imvCross.setVisibility(View.VISIBLE);
                    } else imvCross.setVisibility(View.GONE);
                }
                @Override
                public void afterTextChanged(Editable editable) {
                    filter(editable.toString());
                }
            });


            countryAdpter = new CustomCountryAdpter(countryArrayList, country -> {
                Log.e("", "");
                itemListener.selectedCountry(country);
                countryPopupWindow.dismiss();
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EotApp.getAppinstance().getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(countryAdpter);

        }
        countryPopupWindow.showAsDropDown(view, 0, 0, Gravity.CENTER);

    }


    private void filter(String text) {
        List<Country> filterdNames = new ArrayList<>();

        for (Country s : countryArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        countryAdpter.filterList(filterdNames);

    }

    private int getintToPX() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, EotApp.getAppinstance().getResources().getDisplayMetrics());
    }
}
