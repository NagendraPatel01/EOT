package com.eot_app.utility.settings.setting_db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by aplite_pc302 on 7/3/18.
 */
@Dao
public interface FieldWorkerDao {
    @Insert(onConflict = REPLACE)
    void inserFieldWorker(List<FieldWorker> workers);

    @Query("select * from FieldWorker")
    List<FieldWorker> getFieldWorkerlist();

    @Query("SELECT COUNT(*) from FieldWorker")
    int getCount();

    @Query("delete from FieldWorker")
    void delete();

    @Query("SELECT * from FieldWorker where usrId = :usrId")
    FieldWorker getFieldWorkerByID(String usrId);

}
