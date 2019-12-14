package com.zpguet.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zpguet.database.AppRoomDataBase;
import com.zpguet.database.entry.WordRecordEntry;

import java.util.List;

public class WordRecordViewModel extends AndroidViewModel {
    private AppRoomDataBase dataBase;
//    private LiveData liveData = Transformations.switchMap()
    public WordRecordViewModel(Application application) {
        super(application);
        dataBase = Room.databaseBuilder(application.getApplicationContext(),
                AppRoomDataBase.class,"user1.db")
                .addMigrations(new Migration(1, 2) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("CREATE TABLE WordRecordEntry ( id integer primary key autoincrement not null, content text, contenttime text)");
                        database.execSQL("INSERT INTO WordRecordEntry SELECT * FROM tb_content ");
                    }
                }, new Migration(2,3) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {

                    }
                })
                .build();
    }

    public LiveData<List<WordRecordEntry>> queryAll() {
        return dataBase.wordRecordDao().queryAll();
    }

    public LiveData<List<WordRecordEntry>> queryByContent(String content) {
        return dataBase.wordRecordDao().queryByContent(content);
    }

    public void insertItems(WordRecordEntry... wordRecordEntries) {
        dataBase.wordRecordDao().insertAll(wordRecordEntries);
    }

    public void deleteItem(WordRecordEntry wordRecordEntry) {
        dataBase.wordRecordDao().delete(wordRecordEntry);
    }
}
