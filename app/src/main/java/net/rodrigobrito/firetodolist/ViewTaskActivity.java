package net.rodrigobrito.firetodolist;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.rodrigobrito.firetodolist.data.TaskDBHelper;

public class ViewTaskActivity extends AppCompatActivity {

    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        this.id = intent.getIntExtra("id", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id_menu = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id_menu == R.id.action_delete) {
            if(this.id == 0) {
                Toast.makeText(this, "Task identifier unavaliable", Toast.LENGTH_SHORT);
            }else{
                DeleteTask deleteTask = new DeleteTask(this);
                deleteTask.execute(this.id);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DeleteTask extends AsyncTask<Integer, Void, Void>{

        private Activity activity;

        public DeleteTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            for(Integer id: integers){
                TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.activity);
                taskDBHelper.delete(id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            activity.finish();
        }
    }
}
