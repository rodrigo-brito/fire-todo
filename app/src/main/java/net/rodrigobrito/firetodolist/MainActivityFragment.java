package net.rodrigobrito.firetodolist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.rodrigobrito.firetodolist.data.TaskContract;
import net.rodrigobrito.firetodolist.data.TaskDBHelper;
import net.rodrigobrito.firetodolist.model.Task;
import net.rodrigobrito.firetodolist.data.TaskContract.TaskEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private TaskArrayAdapter taskArrayAdapter;
    TaskDBHelper taskDBHelper;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        taskDBHelper = new TaskDBHelper(getContext());
        final ArrayList<Task> tasks = new ArrayList<Task>();
        taskArrayAdapter = new TaskArrayAdapter(getActivity(), tasks);
        GetTasks getTasks = new GetTasks(taskArrayAdapter);
        getTasks.execute();
        ListView listView = (ListView) rootView.findViewById(R.id.list_tasks);
        listView.setAdapter(taskArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Task task = taskArrayAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ViewTaskActivity.class);
                intent.putExtra("title", task.getTitle());
                intent.putExtra("date", task.getDate().toString());
                intent.putExtra("description", task.getDescription());
                intent.putExtra("done", task.isDone());
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), "LONGPRESS", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        registerForContextMenu(listView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        GetTasks getTasks = new GetTasks(taskArrayAdapter);
        getTasks.execute();
    }



    private class GetTasks extends AsyncTask<Void, Void, Cursor>{

        private SQLiteDatabase db = taskDBHelper.getReadableDatabase();
        private TaskArrayAdapter taskArrayAdapter;

        public GetTasks(final TaskArrayAdapter taskArrayAdapter) {
            this.taskArrayAdapter = taskArrayAdapter;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {

            String[] fields = {
                    TaskEntry._ID,
                    TaskEntry.COLUMN_NAME_TITLE,
                    TaskEntry.COLUMN_NAME_DECRTIPTION,
                    TaskEntry.COLUMN_NAME_DATE,
                    TaskEntry.COLUMN_NAME_DONE
            };

            Cursor cursor = this.db.query(
                    TaskContract.TaskEntry.TABLE_NAME,          // The table to query
                    fields,                                     // The columns to return
                    null,                                       // The columns for the WHERE clause
                    null,                                       // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    TaskEntry.COLUMN_NAME_DATE+" ASC"           // The sort order
            );
            return cursor;
        }

        public Date stringToDate(String string_date){
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            try {
                Date mDate = sdf.parse(string_date);
                return mDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            this.taskArrayAdapter.clear();
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                Task task = new Task();
                task.setTitle( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_TITLE )) );
                task.setDescription( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DECRTIPTION )) );
                task.setDate( new Date( cursor.getLong(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DATE ))));
                task.setDone( cursor.getInt(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DONE )) == 1);
                this.taskArrayAdapter.add(task);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}
