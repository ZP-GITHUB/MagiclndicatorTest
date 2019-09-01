package com.zpguet.magiclndicatortest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.zpguet.model.Content;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DBAdapter {

    private final String DB_NAME = "user1.db";
    private final String CONTENT_DB_TABLE = "tb_content";
    private final int DB_VERSION = 1;

    public final String KEY_ID = "id";
    public final String CONTENT = "content";
    public final String CONTENTTIME = "contenttime";


    private SQLiteDatabase db;
    private  Context context;
    private DBOpenHelper dbOpenHelper;

    public DBAdapter(Context _context) {
        context = _context;
    }

    public DBAdapter() {
    }

    /** Close the database */
    public void close() {
        if (db != null){
            db.close();
            db = null;
        }
    }

    /** Open the database */
    public void open() throws SQLiteException {
        dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        }
        catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }


    public long insert(String content) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = df.format(new Date());

        ContentValues newValues = new ContentValues();
        newValues.put(CONTENT, content);
        newValues.put(CONTENTTIME, dateStr);

        return db.insert(CONTENT_DB_TABLE, null, newValues);
    }


    public Content[] queryAllData() {
        Cursor results =  db.query(CONTENT_DB_TABLE, new String[]{KEY_ID, CONTENT, CONTENTTIME}, null, null, null, null,
                null, null);

        return ConvertToContent(results);
    }

    private Content[] ConvertToContent(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        Content[] contents = new Content[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            contents[i] = new Content();
            contents[i].ID = cursor.getInt(0);
            contents[i]. setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
            contents[i].setContenttime(cursor.getString(cursor.getColumnIndex(CONTENTTIME)));
            cursor.moveToNext();
        }
        return contents;
    }



    public long deleteAllData() {
        return db.delete(CONTENT_DB_TABLE, null, null);
    }

    public long deleteOneData(long id) {
        return db.delete(CONTENT_DB_TABLE,  KEY_ID + "=" + id, null);
    }


    /** 静态Helper类，用于建立、更新和打开数据库*/
    public static class DBOpenHelper extends SQLiteOpenHelper {

        DBAdapter adapter = new DBAdapter();
        public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private  final String DB_CREATE = "create table " +
                adapter.CONTENT_DB_TABLE + " (" + adapter.KEY_ID + " integer primary key autoincrement, "
                +adapter.CONTENT + " text," + adapter.CONTENTTIME  + " text " + ") ";

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + adapter.CONTENT_DB_TABLE);
            onCreate(_db);
        }
    }
}