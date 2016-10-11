package net.rodrigobrito.firetodolist.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import net.rodrigobrito.firetodolist.R;
import net.rodrigobrito.firetodolist.adapters.UpdateAdapter;
import net.rodrigobrito.firetodolist.data.TaskDBHelper;
import net.rodrigobrito.firetodolist.model.Task;
import net.rodrigobrito.firetodolist.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodrigo on 06/09/16.
 */
public class TaskArrayAdapter extends ArrayAdapter<Task>{
    private List<Task> tasks;
    private UpdateAdapter updateAdapter;

    public TaskArrayAdapter(Context context, ArrayList<Task> tasks, UpdateAdapter updateAdapter) {
        super(context, 0, tasks);
        tasks = new ArrayList<Task>();
        this.updateAdapter = updateAdapter;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Task task = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_row_item, parent, false);
        }
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.done_item);
        checkBox.setChecked(task.isDone());
        TextView title = (TextView) convertView.findViewById(R.id.title_item);
        TextView data = (TextView) convertView.findViewById(R.id.date_item);

        title.setText(task.getTitle());
        if( task.getDate() != null){
            DateUtil dateUtil = new DateUtil(getContext());
            data.setText(dateUtil.parse(task.getDate()));
            data.setVisibility(View.VISIBLE);
        }else{
            data.setText("");
            data.setVisibility(View.GONE);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                task.setDone(!task.isDone());
                UpdateTask updateTask = new UpdateTask(getContext(), updateAdapter);
                updateTask.execute(task);
            }
        });

        return convertView;
    }

    private class UpdateTask extends AsyncTask<Task, Void, Void>{
        private Context context;
        private UpdateAdapter updateAdapter;

        public UpdateTask(Context context, UpdateAdapter updateAdapter){
            this.context = context;
            this.updateAdapter = updateAdapter;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            for(Task task : tasks) {
                TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
                taskDBHelper.update(task);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            updateAdapter.update();
        }
    }
}
