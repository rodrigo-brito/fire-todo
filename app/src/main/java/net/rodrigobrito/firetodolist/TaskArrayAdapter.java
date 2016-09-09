package net.rodrigobrito.firetodolist;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import net.rodrigobrito.firetodolist.data.TaskContract;
import net.rodrigobrito.firetodolist.data.TaskDBHelper;
import net.rodrigobrito.firetodolist.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rodrigo on 06/09/16.
 */
public class TaskArrayAdapter extends ArrayAdapter<Task> {
    private List<Task> tasks;

    public TaskArrayAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
        tasks = new ArrayList<Task>();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Task task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_row_item, parent, false);
        }
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.done_item);
        checkBox.setChecked(task.isDone());
        TextView title = (TextView) convertView.findViewById(R.id.title_item);
        TextView data = (TextView) convertView.findViewById(R.id.date_item);

        title.setText(task.getTitle());
        if( task.getDate() != null){
            data.setText(task.getDate().toString());
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                task.setDone(!task.isDone());
                UpdateTask updateTask = new UpdateTask(getContext());
                updateTask.execute(task);
                Log.i("LOG", "Cliclou POS="+position+" ID="+task.get_id());
            }
        });

        return convertView;
    }
    private class UpdateTask extends AsyncTask<Task, Void, Void>{
        private Context context;
        public UpdateTask(Context context){
            this.context = context;
        }
        @Override
        protected Void doInBackground(Task... tasks) {
            for(Task task : tasks){
                TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
                taskDBHelper.update(task);
            }
            return null;
        }

//        @Override
//        protected void onPostExecute(Void aVoid) {
//            TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
//            Cursor cursor = taskDBHelper.getAll();
//            this.taskArrayAdapter.clear();
//            cursor.moveToFirst();
//            while (cursor.isAfterLast() == false) {
//                Task task = new Task();
//                task.set_id( cursor.getInt( cursor.getColumnIndex( TaskContract.TaskEntry._ID )));
//                task.setTitle( cursor.getString(cursor.getColumnIndex( TaskContract.TaskEntry.COLUMN_NAME_TITLE )) );
//                task.setDescription( cursor.getString(cursor.getColumnIndex( TaskContract.TaskEntry.COLUMN_NAME_DECRTIPTION )) );
//                task.setDate( new Date( cursor.getLong(cursor.getColumnIndex( TaskContract.TaskEntry.COLUMN_NAME_DATE ))));
//                task.setDone( cursor.getInt(cursor.getColumnIndex( TaskContract.TaskEntry.COLUMN_NAME_DONE )) != 0 );
//                this.taskArrayAdapter.add(task);
//                cursor.moveToNext();
//            }
//            cursor.close();
//        }
    }
}
