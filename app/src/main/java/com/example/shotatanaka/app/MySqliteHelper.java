package com.example.shotatanaka.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqliteHelper extends SQLiteOpenHelper {

    public MySqliteHelper(Context context) {
        super(context, "localDB", null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users(name text not null,fcmTokenId text,isOwner integer not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
