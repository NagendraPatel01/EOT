package com.eot_app.nav_menu.jobs.joboffline_db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by Sonam-11 on 17/7/20.
 */
@Dao
public interface JobOfflineDataDao {
    @Insert(onConflict = REPLACE)
    void insertJobOfflineData(JobOfflineDataModel jobOfflineDataModel);

    @Query("select * from JobOfflineDataModel")
    List<JobOfflineDataModel> getList();

    @Query("select * from JobOfflineDataModel  WHERE tempId = :tempId and  service_name = :service_name")
    List<JobOfflineDataModel> getJobofflineDataById(String tempId, String service_name);


    @Query("DELETE FROM JobOfflineDataModel WHERE id = :id")
    int deleteFromId(int id);

    @Query("select * from JobOfflineDataModel  WHERE service_name = :service_name and tempId=:tempId")
    JobOfflineDataModel getJobofflineDataForInvoice(String service_name, String tempId);

    @Update
    void update(JobOfflineDataModel... updatejobofflineData);

    @Query("select count(*) from offlinetable where service_name='addAnswerWithAttachment' and params like :formId and params like :jobId")
    int getofflinedata(String formId,String jobId);

    @Query("delete from JobOfflineDataModel")
    void delete();
}
