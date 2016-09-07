package net.rodrigobrito.firetodolist;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.rodrigobrito.firetodolist.model.Task;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewTaskActivityFragment extends Fragment {

    public NewTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_task, container, false);

    }
}
