package net.rodrigobrito.firetodolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import net.rodrigobrito.firetodolist.data.TaskContract;
import net.rodrigobrito.firetodolist.data.TaskDBHelper;
import net.rodrigobrito.firetodolist.model.Task;
import net.rodrigobrito.firetodolist.data.TaskContract.TaskEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewTaskActivity extends AppCompatActivity {

    private Calendar calendar;
    private TaskDBHelper taskDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        taskDBHelper = new TaskDBHelper(this);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_save){
            TextView title = (TextView) findViewById(R.id.title);
            TextView description = (TextView) findViewById(R.id.description);
            Task task = new Task(title.getText().toString(),description.getText().toString(),calendar.getTime(),false);
            SaveTask saveTask = new SaveTask();
            saveTask.execute(task);
            Toast.makeText(this, "Taks inserted", Toast.LENGTH_SHORT);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateTime();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void showTimePickerDialog(View v) {
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);
                updateTime();
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, myTimeListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public void updateTime(){
        TextView textView = (TextView) findViewById(R.id.date);
        final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        textView.setText(df.format(calendar.getTime()));
    }

    private class SaveTask extends AsyncTask<Task, Void, Boolean> {
        SQLiteDatabase db;

        public SaveTask (){
            db = taskDBHelper.getWritableDatabase();
        }

        @Override
        protected Boolean doInBackground(final Task... tasks) {
            int rowInserted = 0;
            for(Task task: tasks) {
                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
                values.put(TaskEntry.COLUMN_NAME_DECRTIPTION, task.getDescription());
                values.put(TaskEntry.COLUMN_NAME_DATE, task.getDate().toString());
                values.put(TaskEntry.COLUMN_NAME_DONE, task.isDone());

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert( TaskEntry.TABLE_NAME, null, values);
                Log.e("DB", "Row inserted = "+newRowId);
                rowInserted += newRowId;
            }
            return rowInserted > 0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
