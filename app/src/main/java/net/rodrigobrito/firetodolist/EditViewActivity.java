package net.rodrigobrito.firetodolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import net.rodrigobrito.firetodolist.data.TaskDBHelper;
import net.rodrigobrito.firetodolist.model.Task;
import net.rodrigobrito.firetodolist.util.DateUtil;

import java.util.Calendar;
import java.util.List;

public class EditViewActivity extends AppCompatActivity implements  Validator.ValidationListener{

    private Calendar calendar;
    private Validator validator;
    @NotEmpty(messageResId = R.string.field_empty)
    private EditText titleTask;
    private EditText descriptionTask;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String tastId = intent.getStringExtra("id");
        task = TaskDBHelper.getInstance(this).getTask(tastId);

        titleTask = (EditText) findViewById(R.id.title);
        descriptionTask = (EditText) findViewById(R.id.description);

        titleTask.setText(task.getTitle());
        descriptionTask.setText(task.getDescription());
        if(task.getDate() == null){
            calendar = null;
        }else{
            calendar = Calendar.getInstance();
            calendar.setTime(task.getDate());
        }

        titleTask = (EditText) findViewById(R.id.title);
        validator = new Validator(this);
        validator.setValidationListener(this);
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
        task.setTitle(titleTask.getText().toString());
        task.setDescription(descriptionTask.getText().toString());
        if( calendar != null ){
            task.setDate(calendar.getTime());
        }
        UpdateTask updateTask = new UpdateTask(this);
        updateTask.execute(task);
        this.finish();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class UpdateTask extends AsyncTask<Task, Void, Void> {

        private Context context;

        public UpdateTask (Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(final Task... tasks) {
            for(Task task: tasks) {
                TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
                taskDBHelper.update(task);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
        }
    }
}
