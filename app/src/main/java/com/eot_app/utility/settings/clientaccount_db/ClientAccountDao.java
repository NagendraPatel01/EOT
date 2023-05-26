package com.eot_app.utility.settings.clientaccount_db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by ubuntu on 23/6/18.
 */
@Dao
public interface ClientAccountDao {
    @Insert(onConflict = REPLACE)
    void insertClientAccount(List<ClientAccountType> account);

    @Query("SELECT COUNT(*) from ClientAccountType")
    int getCountOfRow();

    @Query("select * from ClientAccountType")
    List<ClientAccountType> getAccountList();

    @Query("delete from ClientAccountType")
    void delete();

    @Query("select type from ClientAccountType where accId=:pymtType")
    String getNameByID(String pymtType);
}
