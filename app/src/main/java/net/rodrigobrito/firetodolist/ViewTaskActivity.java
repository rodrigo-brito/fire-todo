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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.rodrigobrito.firetodolist.data.TaskDBHelper;
import net.rodrigobrito.firetodolist.model.Task;
import net.rodrigobrito.firetodolist.util.DateUtil;

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
    protected void onResume() {
        super.onResume();
        updateFields();
    }

    public void updateFields(){
        Task task = TaskDBHelper.getInstance(this).getTask(String.valueOf(this.id));

        TextView titulo = (TextView) findViewById(R.id.title_view);
        TextView data = (TextView) findViewById(R.id.date_view);
        TextView descricao = (TextView) findViewById(R.id.decription_view);

        titulo.setText(task.getTitle());
        descricao.setText(task.getDescription());
        if(task.getDate() == null){
            data.setVisibility(View.GONE);
        }else {
            data.setVisibility(View.VISIBLE);
            data.setText(new DateUtil(this).parse(task.getDate()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id_menu = item.getItemId();
        if (id_menu == R.id.action_delete) {
            if(this.id == 0) {
                Toast.makeText(this, "Task identifier unavaliable", Toast.LENGTH_SHORT);
            }else{
                DeleteTask deleteTask = new DeleteTask(this);
                deleteTask.execute(this.id);
            }
            return true;
        }else if(id_menu == R.id.action_edit){
            if(this.id == 0) {
                Toast.makeText(this, "Task identifier unavaliable", Toast.LENGTH_SHORT);
            }else{
                Intent intent = new Intent(this, EditViewActivity.class);
                intent.putExtra("id", String.valueOf(id));
                startActivity(intent);
            }
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
