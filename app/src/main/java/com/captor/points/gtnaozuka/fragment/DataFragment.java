package com.captor.points.gtnaozuka.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.captor.points.gtnaozuka.adapter.DataAdapter;
import com.captor.points.gtnaozuka.dialog.DataDialog;
import com.captor.points.gtnaozuka.entity.DividerItemDecoration;
import com.captor.points.gtnaozuka.entity.Location;
import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.pointscaptor.MapsActivity;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.operations.DataOperations;
import com.captor.points.gtnaozuka.util.DisplayToast;
import com.captor.points.gtnaozuka.util.operations.FileOperations;
import com.captor.points.gtnaozuka.util.operations.FragmentOperations;
import com.captor.points.gtnaozuka.util.Constants;

import java.io.File;
import java.util.ArrayList;

public class DataFragment extends Fragment {

    private AppCompatActivity context;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private ArrayList<Location> dataLocation;
    private ArrayList<Point> dataPoint;
    private String currentFile, currentFilePath;
    private boolean needsToShowDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_data, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        updateFileList();

        if (needsToShowDialog) {
            showDialog();
            needsToShowDialog = false;
        }

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
        final String[] filenames = FileOperations.listAllFiles(context, FileOperations.FILES_PATH, "dat");
        if (filenames == null || filenames.length == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            RecyclerView.Adapter adapter = new DataAdapter(context, filenames);
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(itemDecoration);

            ((DataAdapter) adapter).setOnItemClickListener(new DataAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    currentFile = filenames[position];
                    currentFilePath = FileOperations.FILES_PATH + File.separator + currentFile;

                    openDatFile();
                }
            });
        }
    }

    private void openDatFile() {
        ArrayList<String> content = FileOperations.read(FileOperations.FILES_PATH, currentFile);

        int middle = content.indexOf("----------");
        ArrayList<String> strLocation = new ArrayList<>(content.subList(0, middle));
        ArrayList<String> strPoint = new ArrayList<>(content.subList(middle + 1, content.size()));

        dataLocation = DataOperations.convertStringToLocations(strLocation);
        dataPoint = DataOperations.convertStringToPoints(strPoint);

        showDialog();
    }

    private void showDialog() {
        DialogFragment dialog = new DataDialog();
        dialog.show(context.getFragmentManager(), "FileManagerDialog");
    }

    public void viewCapturedPoints(DialogFragment dialog) {
        dialog.dismiss();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.DATA_POINT_MSG, dataPoint);
        bundle.putParcelableArrayList(Constants.DATA_LOCATION_MSG, dataLocation);

        FragmentOperations.newFragment(context, new CapturedPointsFragment(), bundle, getResources().getString(R.string.fragment_captured_points));
        needsToShowDialog = true;
    }

    public void viewInGoogleMaps() {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(Constants.STATUS_MSG, Constants.FINISHED);
        intent.putParcelableArrayListExtra(Constants.DATA_LOCATION_MSG, dataLocation);
        startActivity(intent);
    }

    public void removeRepeatedData() {
        //Criar um dialog de confirmacao
        dataLocation = DataOperations.removeRepeatedLocations(dataLocation);
        dataPoint = DataOperations.removeRepeatedPoints(dataPoint);

        new Handler().post(new DisplayToast(context, getResources().getString(R.string.removed_successfully)));
    }

    public void shareWithSomeone() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body));
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(currentFilePath)));

        try {
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_option)));
        } catch (ActivityNotFoundException ex) {
            new Handler().post(new DisplayToast(context, getResources().getString(R.string.no_email_client)));
        }
    }

    public void deleteFile(DialogFragment dialog) {
        dialog.dismiss();

        FileOperations.delete(new File(currentFilePath));

        new Handler().post(new DisplayToast(context, getResources().getString(R.string.deleted_successfully)));
        updateFileList();
    }
}
