package com.captor.points.gtnaozuka.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.Constants;

import java.util.ArrayList;

public class CapturedPointsFragment extends Fragment {

    private ArrayList<Point> dataPoint;
    private ArrayList<com.captor.points.gtnaozuka.entity.Location> dataLocation;

    private View rootView;
    private AppCompatActivity context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_captured_points, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            dataPoint = bundle.getParcelableArrayList(Constants.DATA_POINT_MSG);
            dataLocation = bundle.getParcelableArrayList(Constants.DATA_LOCATION_MSG);
            createTable();
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

    private void createTable() {
        TableLayout tl = (TableLayout) rootView.findViewById(R.id.table);
        tl.removeAllViewsInLayout();

        TableRow tr = createTableRow();
        tr.addView(createTextView(getResources().getString(R.string.n), false, true));
        tr.addView(createTextView(getResources().getString(R.string.latitude), false, true));
        tr.addView(createTextView(getResources().getString(R.string.longitude), false, true));
        tr.addView(createTextView(getResources().getString(R.string.x), false, true));
        tr.addView(createTextView(getResources().getString(R.string.y), false, true));

        tl.addView(tr);
        tl.addView(createHorizontalLine(2));

        Point p;
        com.captor.points.gtnaozuka.entity.Location l;
        for (int i = 0; i < dataPoint.size(); i++) {
            p = dataPoint.get(i);
            l = dataLocation.get(i);

            tr = createTableRow();
            tr.addView(createTextView(String.valueOf(i + 1), false, false));
            tr.addView(createTextView(String.valueOf(l.getLatitude()), true, false));
            tr.addView(createTextView(String.valueOf(l.getLongitude()), true, false));
            tr.addView(createTextView(String.valueOf(p.getX()), true, false));
            tr.addView(createTextView(String.valueOf(p.getY()), true, false));

            tl.addView(tr);
            tl.addView(createHorizontalLine(1));
        }
    }

    private TableRow createTableRow() {
        TableRow tr = new TableRow(context);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        return tr;
    }

    private TextView createTextView(String text, boolean padding, boolean bold) {
        TextView txtView = new TextView(context);
        txtView.setTextAppearance(context, R.style.AppTheme);
        txtView.setText(text);
        txtView.setTextSize(15);
        txtView.setTextColor(getResources().getColor(R.color.text_color));
        if (padding)
            txtView.setPadding(25, 0, 0, 0);
        if (bold) {
            txtView.setTypeface(null, Typeface.BOLD);
            txtView.setGravity(Gravity.CENTER);
        }
        return txtView;
    }

    private View createHorizontalLine(int size) {
        View v = new View(context);
        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, size));
        v.setBackgroundColor(Color.BLACK);
        return v;
    }

}
