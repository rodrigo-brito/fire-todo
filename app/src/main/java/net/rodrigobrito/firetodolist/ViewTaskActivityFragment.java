package net.rodrigobrito.firetodolist;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewTaskActivityFragment extends Fragment {

    private int id;

    public ViewTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_view_task, container, false);
        Intent intent = getActivity().getIntent();

        TextView titulo = (TextView) rootView.findViewById(R.id.title_view);
        TextView data = (TextView) rootView.findViewById(R.id.date_view);
        TextView descricao = (TextView) rootView.findViewById(R.id.decription_view);

        this.id = intent.getIntExtra("id", 0);
        titulo.setText(intent.getStringExtra("title"));
        data.setText(intent.getStringExtra("date"));
        descricao.setText(intent.getStringExtra("description"));

        return rootView;
    }
}
