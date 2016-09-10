package net.rodrigobrito.firetodolist;

import android.database.Cursor;

import net.rodrigobrito.firetodolist.model.Task;

import java.util.ArrayList;

/**
 * Created by rodrigo on 09/09/16.
 */
public interface UpdateAdapter {
    public void updateTaskArrayAdapter(ArrayList<Task> tasks);
    public void updateTaskArrayAdapter(Cursor cursor);
    public void update();
}
