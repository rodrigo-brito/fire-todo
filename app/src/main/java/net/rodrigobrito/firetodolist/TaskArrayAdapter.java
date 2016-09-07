package net.rodrigobrito.firetodolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import net.rodrigobrito.firetodolist.model.Task;

import java.util.ArrayList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_row_item, parent, false);
        }

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.done_item);
        TextView title = (TextView) convertView.findViewById(R.id.title_item);
        TextView data = (TextView) convertView.findViewById(R.id.date_item);

        title.setText(task.getTitle());
        if( task.getDate() != null){
            data.setText(task.getDate().toString());
        }
        return convertView;
    }
}
