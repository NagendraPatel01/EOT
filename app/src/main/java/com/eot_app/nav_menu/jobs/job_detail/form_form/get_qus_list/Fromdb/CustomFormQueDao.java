package com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.Fromdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CustomFormQueDao {

    @Insert
    void insert(CustomFormQue customFormQue);

    @Query("update CustomFormQue set quelist= :quelist where Formid=:id and opid=:opid")
    void updatefrom(String quelist,String id,String opid);

    @Query("select quelist from CustomFormQue where Formid =:id and opid=:opid")
    String getfrombyids(String id,String opid);

    @Query("delete from CustomFormQue")
    void delete();
}
