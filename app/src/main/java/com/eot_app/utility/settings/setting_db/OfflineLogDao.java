package com.eot_app.utility.settings.setting_db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by aplite_pc302 on 6/25/18.
 */
@Dao
public interface OfflineLogDao {
    @Insert(onConflict = REPLACE)
    void insertLogOffline(OfflineLogTable offlinetable);

    @Query("select * from OfflineLogTable limit 1")
    OfflineLogTable getSingleRecordLog();

    @Query("SELECT COUNT(*) from OfflineLogTable")
    int getCountOfRowLog();

    @Query("delete from OfflineLogTable")
    void deleteLog();

    @Query("DELETE FROM OfflineLogTable WHERE id = :id")
    int deleteFromIdLog(int id);

    @Query("UPDATE OfflineLogTable SET count = :update_count WHERE id = :id")
    void updateCountByIdLog(int id, int update_count);

    @Query("DELETE FROM OfflineLogTable WHERE params like :jobId")
    int deleteFromSearchJobIDLog(String jobId);

    @Query("select * from OfflineLogTable where service_name like :serviceName")
    List<OfflineLogTable> getOfflinetablesByIdLog(String serviceName);

    /***/
    @Query("select * from OfflineLogTable")
    List<OfflineLogTable> getListLog();

    @Update
    void update(OfflineLogTable... offlinetablesupdate);

}
