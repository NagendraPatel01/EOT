package com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.Fromdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CustomFormSubmitedDao {

    @Insert
    void insert(CustomFormSubmited customFormSubmited);

    @Query("select isSubmited from CustomFormSubmited where formId=:formId and jobId=:jobId and UserId=:Userid")
    String getFormSubmitedorNot(String formId,String jobId,String Userid);

    @Query("delete from CustomFormSubmited")
    void delete();
}
