package com.zpguet.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.zpguet.database.dao.WordRecordDao;
import com.zpguet.database.entry.WordRecordEntry;

@Database(entities = {WordRecordEntry.class}, version = 3)
public abstract class AppRoomDataBase extends RoomDatabase {
    public abstract WordRecordDao wordRecordDao();
}
