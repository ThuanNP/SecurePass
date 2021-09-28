package com.thuannp.securepass.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.thuannp.securepass.models.CredModel;

import java.util.List;

@Dao
public interface CredDao {

    @Insert
    void insert(CredModel credModel);

    @Update
    void update(CredModel credModel);

    @Delete
    void delete(CredModel credModel);

    @Query("DELETE FROM cred_table")
    void deleteAllNotes();

    @Query("SELECT * FROM cred_table")
    LiveData<List<CredModel>> getAllCred();

    @Query("SELECT * FROM cred_table WHERE provider = 'mail'")
    LiveData<List<CredModel>> getAllMail();

    @Query("SELECT * FROM cred_table WHERE provider = 'wifi'")
    LiveData<List<CredModel>> getAllWifi();

    @Query("SELECT * FROM cred_table WHERE provider = 'social'")
    LiveData<List<CredModel>> getAllSocial();
}
