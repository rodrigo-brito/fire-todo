package net.rodrigobrito.firetodolist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.rodrigobrito.firetodolist.data.TaskDBHelper;
import net.rodrigobrito.firetodolist.model.Task;
import net.rodrigobrito.firetodolist.data.TaskContract.TaskEntry;

import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private TaskArrayAdapter taskArrayAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        final ArrayList<Task> tasks = new ArrayList<Task>();
        taskArrayAdapter = new TaskArrayAdapter(getActivity(), tasks);
        GetTasks getTasks = new GetTasks(taskArrayAdapter, getContext());
        getTasks.execute();
        ListView listView = (ListView) rootView.findViewById(R.id.list_tasks);
        listView.setAdapter(taskArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Task task = taskArrayAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ViewTaskActivity.class);
                intent.putExtra("id", task.get_id());
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
                getActivity().openContextMenu(getActivity().findViewById(R.id.list_tasks));
                return true;
            }
        });
        registerForContextMenu(listView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        GetTasks getTasks = new GetTasks(taskArrayAdapter, getContext());
        getTasks.execute();
    }

    private class GetTasks extends AsyncTask<Void, Void, Cursor>{

        private TaskArrayAdapter taskArrayAdapter;
        private Context context;

        public GetTasks(final TaskArrayAdapter taskArrayAdapter, Context context) {
            this.taskArrayAdapter = taskArrayAdapter;
            this.context = context;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
            return taskDBHelper.getAll();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            this.taskArrayAdapter.clear();
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                Task task = new Task();
                task.set_id( cursor.getInt( cursor.getColumnIndex( TaskEntry._ID )));
                task.setTitle( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_TITLE )) );
                task.setDescription( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DECRTIPTION )) );
                task.setDate( new Date( cursor.getLong(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DATE ))));
                task.setDone( cursor.getInt(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DONE )) > 0);
                this.taskArrayAdapter.add(task);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}
