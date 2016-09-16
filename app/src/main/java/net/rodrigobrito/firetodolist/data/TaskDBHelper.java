package net.rodrigobrito.firetodolist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.rodrigobrito.firetodolist.data.TaskContract.TaskEntry;
import net.rodrigobrito.firetodolist.model.Task;

import java.util.Date;

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

    // Database Information
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Task.db";
    private static TaskDBHelper taskDBHelper;

    private TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static TaskDBHelper getInstance(Context context){
        if(taskDBHelper == null){
            taskDBHelper =  new TaskDBHelper(context);
        }
        return taskDBHelper;
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

    public long insert(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DECRTIPTION, task.getDescription());
        if(task.getDate() != null){
            values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE, task.getDate().getTime());
        }
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DONE, task.isDone());

        // Insert the new row, returning the primary key value of the new row
        long id = db.insert( TaskContract.TaskEntry.TABLE_NAME, null, values);
        return id;
    }

    public Cursor getAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] fields = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_DECRTIPTION,
                TaskContract.TaskEntry.COLUMN_NAME_DATE,
                TaskContract.TaskEntry.COLUMN_NAME_DONE
        };

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,          // The table to query
                fields,                                     // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                TaskContract.TaskEntry.COLUMN_NAME_DATE+" ASC"           // The sort order
        );
        return cursor;
    }

    public Cursor getDone(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] fields = {
            TaskContract.TaskEntry._ID,
            TaskContract.TaskEntry.COLUMN_NAME_TITLE,
            TaskContract.TaskEntry.COLUMN_NAME_DECRTIPTION,
            TaskContract.TaskEntry.COLUMN_NAME_DATE,
            TaskContract.TaskEntry.COLUMN_NAME_DONE
        };

        Cursor cursor = db.query(
            TaskContract.TaskEntry.TABLE_NAME,                      // The table to query
            fields,                                                 // The columns to return
            TaskEntry.COLUMN_NAME_DONE+" = '1'",                      // The columns for the WHERE clause
            null,                                                   // The values for the WHERE clause
            null,                                                   // don't group the rows
            null,                                                   // don't filter by row groups
            TaskContract.TaskEntry.COLUMN_NAME_DATE+" ASC"          // The sort order
        );
        return cursor;
    }

    public Task getTask(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] fields = {
            TaskContract.TaskEntry._ID,
            TaskContract.TaskEntry.COLUMN_NAME_TITLE,
            TaskContract.TaskEntry.COLUMN_NAME_DECRTIPTION,
            TaskContract.TaskEntry.COLUMN_NAME_DATE,
            TaskContract.TaskEntry.COLUMN_NAME_DONE
        };

        Cursor cursor = db.query(
            TaskContract.TaskEntry.TABLE_NAME,                      // The table to query
            fields,                                                 // The columns to return
            TaskEntry._ID+" = "+id,                                   // The columns for the WHERE clause
            null,                                     // The values for the WHERE clause
            null,                                                   // don't group the rows
            null,                                                   // don't filter by row groups
            null                                                    // The sort order
        );

        Task task = new Task();
        if(cursor != null && cursor.moveToFirst()){
            task.set_id( cursor.getInt( cursor.getColumnIndex( TaskEntry._ID )));
            task.setTitle( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_TITLE )) );
            task.setDescription( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DECRTIPTION )) );
            Long valueDate = cursor.getLong(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DATE ));
            if(valueDate != 0){
                task.setDate( new Date( valueDate ));
            }
            task.setDone( cursor.getInt(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DONE )) > 0);
        }
        return task;
    }

    public Cursor getPending(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] fields = {
            TaskContract.TaskEntry._ID,
            TaskContract.TaskEntry.COLUMN_NAME_TITLE,
            TaskContract.TaskEntry.COLUMN_NAME_DECRTIPTION,
            TaskContract.TaskEntry.COLUMN_NAME_DATE,
            TaskContract.TaskEntry.COLUMN_NAME_DONE
        };

        Cursor cursor = db.query(
            TaskContract.TaskEntry.TABLE_NAME,                      // The table to query
            fields,                                                 // The columns to return
            TaskEntry.COLUMN_NAME_DONE+" = '0'",                      // The columns for the WHERE clause
            null,                                                   // The values for the WHERE clause
            null,                                                   // don't group the rows
            null,                                                   // don't filter by row groups
            TaskContract.TaskEntry.COLUMN_NAME_DATE+" ASC"          // The sort order
        );
        return cursor;
    }

    public void delete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        // Define 'where' part of query.
        String selection = TaskContract.TaskEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };
        // Issue SQL statement.
        db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void update(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DECRTIPTION, task.getDescription());
        if(task.getDate() != null){
            values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE, task.getDate().getTime());
        }
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DONE, task.isDone());

        // Which row to update, based on the ID
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(task.get_id()) };

        db.update(TaskContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
