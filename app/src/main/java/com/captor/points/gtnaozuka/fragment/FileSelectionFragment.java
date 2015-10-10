package com.captor.points.gtnaozuka.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.captor.points.gtnaozuka.adapter.DataAdapter;
import com.captor.points.gtnaozuka.entity.DividerItemDecoration;
import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.Constants;
import com.captor.points.gtnaozuka.util.operations.DataOperations;
import com.captor.points.gtnaozuka.util.operations.FileOperations;
import com.captor.points.gtnaozuka.util.operations.FragmentOperations;

import java.util.ArrayList;

public class FileSelectionFragment extends Fragment {

    private AppCompatActivity context;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private ArrayList<Point> dataPoint;
    private String currentFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_selection, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        updateFileList();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (AppCompatActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateFileList() {
        final String[] filenames = FileOperations.listAllFiles(context, FileOperations.DATA_PATH);
        if (filenames == null || filenames.length == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            RecyclerView.Adapter adapter = new DataAdapter(context, FileOperations.splitAttrs(filenames));
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(itemDecoration);

            ((DataAdapter) adapter).setOnItemClickListener(new DataAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    currentFile = filenames[position];
                    ArrayList<String> content = FileOperations.read(FileOperations.DATA_PATH, currentFile);

                    int middle = content.indexOf("----------");
                    ArrayList<String> strPoint = new ArrayList<>(content.subList(middle + 1, content.size()));
                    dataPoint = DataOperations.convertStringToPoints(strPoint);

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Constants.DATA_POINT_MSG, dataPoint);

                    FragmentOperations.newFragment(context, new BoundaryDefinitionFragment(), bundle, getResources().getString(R.string.fragment_boundary_definition));
                }
            });
        }
    }
}
