package com.eot_app.nav_menu.jobs.job_detail.detail.jobdetial_model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface JobStatusDao {

    @Insert(onConflict = REPLACE)
    void insertJobstatusList(List<JobStatusModelNew> jobStatusList);

    @Insert(onConflict = REPLACE)
    void insertJobstatus(JobStatusModelNew jobStatus);

    @Query("select * from JobStatusModelNew")
    List<JobStatusModelNew> getAllStatusList();

    // get getting list of status which are editable by fw i.e isFwSelect=1
    @Query("select * from JobStatusModelNew where isFwSelect=1")
    List<JobStatusModelNew> getFwStatusList();

    @Query("delete from JobStatusModelNew")
    void delete();

    @Query("select * from JobStatusModelNew where isDefault=1")
    List<JobStatusModelNew> getAllDefaultStatusList();

    @Query("select * from JobStatusModelNew where id like :statusId")
    JobStatusModelNew getSingleStatusObjectById(String statusId);

    @Query("update JobStatusModelNew set urlBitmap= :urlBitmap where id like :statusId")
    void upadteBitmapUrl(String statusId,String urlBitmap);
}
