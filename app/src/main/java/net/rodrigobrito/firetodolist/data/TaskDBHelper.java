package net.rodrigobrito.firetodolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import net.rodrigobrito.firetodolist.data.TaskContract.TaskEntry;

/**
 * Created by rodrigo on 07/09/16.
 */
public class TaskDBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_NAME_TITLE + " VARCHAR(128),"+
                    TaskEntry.COLUMN_NAME_DECRTIPTION + " TEXT,"+
                    TaskEntry.COLUMN_NAME_DATE + " DATETIME,"+
                    TaskEntry.COLUMN_NAME_DONE + " BOOLEAN" +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    // Database Informationc
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Task.db";

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
