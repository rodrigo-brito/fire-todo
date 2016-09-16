package net.rodrigobrito.firetodolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import net.rodrigobrito.firetodolist.data.TaskDBHelper;
import net.rodrigobrito.firetodolist.model.Task;
import net.rodrigobrito.firetodolist.data.TaskContract.TaskEntry;
import net.rodrigobrito.firetodolist.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements UpdateAdapter {

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
        taskArrayAdapter = new TaskArrayAdapter(getActivity(), tasks, this);
        ListView listView = (ListView) rootView.findViewById(R.id.list_tasks);
        listView.setAdapter(taskArrayAdapter);

        //Open Task
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Task task = taskArrayAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ViewTaskActivity.class);
                intent.putExtra("id", task.get_id());
                startActivity(intent);
            }
        });

        Spinner spinner = (Spinner) rootView.findViewById(R.id.list_type);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0:
                        GetPendingTasks getPendingTasks = new GetPendingTasks(getContext(), MainActivityFragment.this);
                        getPendingTasks.execute();
                        break;
                    case 1:
                        GetDoneTasks getDoneTasks = new GetDoneTasks(getContext(), MainActivityFragment.this);
                        getDoneTasks.execute();
                        break;
                    case 2:
                        GetAllTasks getAllTasks = new GetAllTasks(getContext(), MainActivityFragment.this);
                        getAllTasks.execute();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Context Menu
        registerForContextMenu(listView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.update();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Task task = taskArrayAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.action_delete:
                DeleteTask deleteTask = new DeleteTask(getContext(), this);
                deleteTask.execute(task.get_id());
                return true;
            case R.id.action_edit:
                Intent intent2 = new Intent(getContext(), EditViewActivity.class);
                intent2.putExtra("id", String.valueOf(task.get_id()));
                startActivity(intent2);
                return true;
            case R.id.action_show:
                Intent intent3 = new Intent(getContext(), ViewTaskActivity.class);
                intent3.putExtra("id", task.get_id());
                startActivity(intent3);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void updateTaskArrayAdapter(ArrayList<Task> tasks) {
        this.taskArrayAdapter.clear();
        taskArrayAdapter.addAll(tasks);
    }

    @Override
    public void updateTaskArrayAdapter(Cursor cursor) {
        this.taskArrayAdapter.clear();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Task task = new Task();
            task.set_id( cursor.getInt( cursor.getColumnIndex( TaskEntry._ID )));
            task.setTitle( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_TITLE )) );
            task.setDescription( cursor.getString(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DECRTIPTION )) );
            Long valueDate = cursor.getLong(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DATE ));
            if(valueDate != 0){
                task.setDate( new Date( valueDate ));
            }
            task.setDone( cursor.getInt(cursor.getColumnIndex( TaskEntry.COLUMN_NAME_DONE )) > 0);
            this.taskArrayAdapter.add(task);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void update() {
        Spinner spinner = (Spinner) getActivity().findViewById(R.id.list_type);
        int position =  spinner.getSelectedItemPosition();
        switch (position){
            case 0:
                GetPendingTasks getPendingTasks = new GetPendingTasks(getContext(), MainActivityFragment.this);
                getPendingTasks.execute();
                break;
            case 1:
                GetDoneTasks getDoneTasks = new GetDoneTasks(getContext(), MainActivityFragment.this);
                getDoneTasks.execute();
                break;
            case 2:
                GetAllTasks getAllTasks = new GetAllTasks(getContext(), MainActivityFragment.this);
                getAllTasks.execute();
                break;
        }
    }

    private class GetAllTasks extends AsyncTask<Void, Void, Cursor>{

        private UpdateAdapter updateAdapter;
        private Context context;

        public GetAllTasks(Context context, UpdateAdapter updateAdapter) {
            this.updateAdapter = updateAdapter;
            this.context = context;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
            return taskDBHelper.getAll();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            updateAdapter.updateTaskArrayAdapter(cursor);
        }
    }

    private class GetDoneTasks extends AsyncTask<Void, Void, Cursor>{

        private UpdateAdapter updateAdapter;
        private Context context;

        public GetDoneTasks(Context context, UpdateAdapter updateAdapter) {
            this.updateAdapter = updateAdapter;
            this.context = context;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
            return taskDBHelper.getDone();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            updateAdapter.updateTaskArrayAdapter(cursor);
        }
    }

    private class GetPendingTasks extends AsyncTask<Void, Void, Cursor>{

        private UpdateAdapter updateAdapter;
        private Context context;

        public GetPendingTasks(Context context, UpdateAdapter updateAdapter) {
            this.updateAdapter = updateAdapter;
            this.context = context;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
            return taskDBHelper.getPending();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            updateAdapter.updateTaskArrayAdapter(cursor);
        }
    }

    private class DeleteTask extends AsyncTask<Integer, Void, Void>{

        private UpdateAdapter updateAdapter;
        private Context context;

        public DeleteTask(Context context, UpdateAdapter updateAdapter) {
            this.updateAdapter = updateAdapter;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            for(Integer id: integers){
                TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
                taskDBHelper.delete(id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateAdapter.update();
        }
    }
}
