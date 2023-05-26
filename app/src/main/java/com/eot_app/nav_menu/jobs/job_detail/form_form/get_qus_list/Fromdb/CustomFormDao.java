package com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.Fromdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CustomFormDao {

    @Insert
    void insert(CustomForm form);

    @Query("select ans from CustomForm where frmid =:id and jobid=:jobid and opid=:opid")
    String getfrombyid(String id,String jobid,String opid);

    @Query("select ans from CustomForm where frmid =:id and jobid=:jobid")
    String getfrombyids(String id,String jobid);

    @Query("select ans from customform where frmid=:formid and jobid=:jobid and opid!='-1'")
    List<String> getfinalarray(String jobid, String formid);

    @Query("delete from CustomForm")
    void deleteallrecord();

    @Query("delete from CustomForm where frmid =:id and jobid=:jobid " )
    void deleterecode(String id,String jobid);

    @Query("delete from CustomForm where frmid =:id and jobid=:jobid and opid=:opid")
    void deletesinglerecord(String id,String jobid,String opid);
}
