package com.eot_app.utility.settings.clientindustry_db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by Sonam-11 on 10/7/20.
 */
@Dao
public interface ClientIndustryDao {
    @Insert(onConflict = REPLACE)
    void insertClientIndustry(List<ClientIndustryModel> industryList);

    @Query("select * from ClientIndustryModel")
    List<ClientIndustryModel> getIndustryList();

}
