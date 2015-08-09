package com.captor.points.gtnaozuka.pointscaptor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.util.Util;

import java.util.ArrayList;

public class CapturedPointsActivity extends MenuActivity {

    private ArrayList<Point> dataPoint;
    private ArrayList<com.captor.points.gtnaozuka.entity.Location> dataLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_points);

        Intent intent = getIntent();
        dataPoint = intent.getParcelableArrayListExtra(Util.DATA_POINT_MSG);
        dataLocation = intent.getParcelableArrayListExtra(Util.DATA_LOCATION_MSG);

        createTable();
    }

    private void createTable() {
        TableLayout tl = (TableLayout) findViewById(R.id.table);
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
        TableRow tr = new TableRow(CapturedPointsActivity.this);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        return tr;
    }

    private TextView createTextView(String text, boolean padding, boolean bold) {
        TextView txtView = new TextView(CapturedPointsActivity.this);
        txtView.setTextAppearance(this, R.style.AppTheme);
        txtView.setText(text);
        txtView.setTextSize(15);
        if (padding)
            txtView.setPadding(25, 0, 0, 0);
        if (bold) {
            txtView.setTypeface(null, Typeface.BOLD);
            txtView.setGravity(Gravity.CENTER);
        }
        return txtView;
    }

    private View createHorizontalLine(int size) {
        View v = new View(CapturedPointsActivity.this);
        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, size));
        v.setBackgroundColor(Color.BLACK);
        return v;
    }
}

