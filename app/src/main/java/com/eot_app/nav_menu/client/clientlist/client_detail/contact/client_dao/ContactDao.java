package com.eot_app.nav_menu.client.clientlist.client_detail.contact.client_dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by ubuntu on 7/6/18.
 */
@Dao
public interface ContactDao {
    @Insert(onConflict = REPLACE)
    void insertContactList(List<ContactData> contactData);

    @Insert(onConflict = REPLACE)
    void insertSingleContactList(ContactData contactData);

    @Query("select * from ContactData where cltId = :cltId  ORDER BY LOWER(cnm) ASC")
    List<ContactData> getContactFromcltId(String cltId);

    // to get the enabled or active contacts only
    @Query("select * from ContactData where cltId = :cltId and isactive=1 ORDER BY LOWER(cnm) ASC")
    List<ContactData> getActiveContactFromcltId(String cltId);


    @Insert(onConflict = REPLACE)
    void insertNewContact(ContactData contactData);

    @Update
    void update(ContactData... contactData);

    @Query("select * from ContactData where conId =:conId")
    ContactData getContactById(String conId);

    @Query("update ContactData set def = 0 where conId != :conId and cltId = :cltId")
    void updateDefaultContact(String conId, String cltId);

    @Query("delete from ContactData")
    void delete();

    @Query("SELECT * FROM ContactData WHERE cnm LIKE :nmText and cltId=:cltId")
    List<ContactData> getContactlist(String nmText, String cltId);

    @Query("select * from ContactData where def=1 and cltId = :clientId")
    ContactData getDefaultConFromCltId(String clientId);

    @Query("delete from ContactData where isdelete = 0")
    void deleteContactByIsDelete();

//    @Query("SELECT * FROM ContactData WHERE def=1 and cltId=:cltId order by conId DESC limit 1")
//    ContactData getLastUpdateDefault(String cltId);

    @Query("SELECT COUNT(*) from ContactData")
    int getTotalCount();

    @Query("UPDATE ContactData SET conId = :conId WHERE conId = :tempId and tempId = :tempId")
    void udpateContactByTempIdtoOriganalId(String conId, String tempId);

    //    delete Contact which is unused.
    @Query("delete from ContactData where conId=tempId")
    void deleteUnusedTempContact();

    @Query("select * from ContactData where cltId =:cltId")
    List<ContactData> getContactByClId(String cltId);

    // to get only enabled or active contact list
    @Query("select * from ContactData where cltId =:cltId  and isactive=1")
    List<ContactData> getActiveContactByClId(String cltId);

    // to set the is active param as 1 for all the previously stored contacts and make all as enabled by default
    @Query("update ContactData set isactive = 1")
    void updateContactsAsEnabled();

}
