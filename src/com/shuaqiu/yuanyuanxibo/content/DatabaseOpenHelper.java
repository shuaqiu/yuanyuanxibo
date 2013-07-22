/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author shuaqiu 2013-5-6
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "yyxibo.db";
    private static final int DB_VERSION = 1;

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StatusHelper.getDdl());
        db.execSQL(EmotionHelper.getDdl());
        db.execSQL(FriendshipHelper.getDdl());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
