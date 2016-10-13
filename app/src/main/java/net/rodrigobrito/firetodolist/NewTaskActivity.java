package net.rodrigobrito.firetodolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.ContextMenu;
import android.view.ContextMenu.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import net.rodrigobrito.firetodolist.data.TaskContract;
import net.rodrigobrito.firetodolist.data.TaskDBHelper;
import net.rodrigobrito.firetodolist.model.Task;
import net.rodrigobrito.firetodolist.data.TaskContract.TaskEntry;
import net.rodrigobrito.firetodolist.util.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NewTaskActivity extends AppCompatActivity implements Validator.ValidationListener {

    private Calendar calendar;
    private Validator validator;
    private FrameLayout mAdsFrame;
    @NotEmpty(messageResId = R.string.field_empty)
    private EditText titleTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleTask = (EditText) findViewById(R.id.title);
        validator = new Validator(this);
        validator.setValidationListener(this);

        //Adsense
        mAdsFrame = (FrameLayout) findViewById(R.id.ads_frame);
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        mAdsFrame.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.phone_test_unique_id))
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cancel) {
            this.finish();
            return true;
        }else if(id == R.id.action_save){
            validator.validate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        Calendar calendarTemp;
        if(calendar == null) {
            calendarTemp = Calendar.getInstance();
        }else{
            calendarTemp = this.calendar;
        }
        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                if(calendar == null){
                    calendar = Calendar.getInstance();
                }
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateTime();
                ImageButton button = (ImageButton) findViewById(R.id.button_remove_date);
                button.setVisibility(View.VISIBLE);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, calendarTemp.get(Calendar.YEAR), calendarTemp.get(Calendar.MONTH), calendarTemp.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void removeDate(View v) {
        ImageButton button = (ImageButton) findViewById(R.id.button_remove_date);
        button.setVisibility(View.GONE);
        calendar = null;
        updateTime();
    }

    public void showTimePickerDialog(View v) {
        Calendar calendarTemp;
        if(calendar == null) {
            calendarTemp = Calendar.getInstance();
        }else{
            calendarTemp = this.calendar;
        }
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                if(calendar == null){
                    calendar = Calendar.getInstance();
                }
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);
                updateTime();
                ImageButton button = (ImageButton) findViewById(R.id.button_remove_date);
                button.setVisibility(View.VISIBLE);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, myTimeListener, calendarTemp.get(Calendar.HOUR_OF_DAY), calendarTemp.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public void updateTime(){
        TextView textView = (TextView) findViewById(R.id.date);
        if(calendar != null){
            textView.setText(new DateUtil(this).parse(calendar.getTime()));
        }else{
            textView.setText("");
        }
    }

    @Override
    public void onValidationSucceeded() {
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        Task task = new Task(title.getText().toString(),description.getText().toString(),null,false);
        if( calendar != null ){
            task.setDate(calendar.getTime());
        }
        SaveTask saveTask = new SaveTask(this);
        saveTask.execute(task);
        this.finish();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SaveTask extends AsyncTask<Task, Void, Boolean> {

        private Context context;

        public SaveTask (Context context){
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(final Task... tasks) {
            int rowInserted = 0;
            for(Task task: tasks) {
                TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
                Long newRowId = taskDBHelper.insert(task);
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
