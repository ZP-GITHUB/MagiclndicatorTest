package com.zpguet.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.zpguet.database.entry.WordRecordEntry;

import java.util.List;

@Dao
public interface WordRecordDao {
    @Query("SELECT * FROM WordRecordEntry ORDER BY id DESC")
    LiveData<List<WordRecordEntry>> queryAll();

    @Query("SELECT * FROM WordRecordEntry WHERE id = :id limit 1")
    LiveData<WordRecordEntry> queryById(int id);

    @Query("SELECT * FROM WordRecordEntry WHERE content like '%' || :content || '%' ORDER BY id DESC")
    LiveData<List<WordRecordEntry>> queryByContent(String content);
    @Insert
    void insertAll(WordRecordEntry... wordRecordEntries);

    @Delete
    void delete(WordRecordEntry... wordRecordEntry);
}
