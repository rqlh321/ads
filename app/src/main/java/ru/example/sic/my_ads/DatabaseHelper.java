package ru.example.sic.my_ads;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_TABLE = "history";
    public static final String AD_ID = "adId";
    public static final String USER_ID = "userId";
    private static final String DATABASE_NAME = "localAds.db";
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + "(" + BaseColumns._ID + " integer primary key autoincrement, "
            + USER_ID + " text not null, "
            + AD_ID + " text not null);";
    private static final int DATABASE_VERSION = 1;
    SQLiteDatabase sdb = getReadableDatabase();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<String> getHistoryList(String userId) {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = sdb.query(DatabaseHelper.DATABASE_TABLE,
                new String[]{DatabaseHelper.AD_ID}, DatabaseHelper.USER_ID + "=?", new String[]{userId}, null, null, null);
        cursor.getCount();
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AD_ID));
            list.add(content);
        }
        cursor.close();
        return list;
    }

    public void addToDataBase(String userId, String adId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.AD_ID, adId);
        values.put(DatabaseHelper.USER_ID, userId);
        sdb.insert(DatabaseHelper.DATABASE_TABLE, null, values);
    }

}
