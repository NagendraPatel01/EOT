package com.eot_app.nav_menu.jobs.job_detail.invoice.inventry_pkg;

import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_detail_pkg.inv_detail_model.Tax;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class PartConverter {
    @TypeConverter
    public static List<ItemParts> toPartData(String strdata) {
        Type listType = new TypeToken<List<ItemParts>>() {
        }.getType();
        List<ItemParts> data = new Gson().fromJson(strdata, listType);
        return strdata == null ? null : data;
    }

    @TypeConverter
    public static String toStringData(List<ItemParts> data) {
        return data == null ? null : new Gson().toJson(data);
    }
}
