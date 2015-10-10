package com.captor.points.gtnaozuka.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.captor.points.gtnaozuka.entity.Point;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.Constants;
import com.captor.points.gtnaozuka.util.DisplayToast;
import com.captor.points.gtnaozuka.util.OpenGLSurfaceView;
import com.captor.points.gtnaozuka.util.Versions;
import com.captor.points.gtnaozuka.util.operations.DataOperations;

import java.util.ArrayList;
import java.util.Collections;

public class BoundaryDefinitionFragment extends Fragment {

    private AppCompatActivity context;

    private ArrayList<Point> dataPoint;
    private ImageButton btnFit;
    private ImageButton btnInterpolate;
    private OpenGLSurfaceView glSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*context.requestWindowFeature(Window.FEATURE_NO_TITLE);
        context.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);

        if (!Versions.hasGLES20(context)) {
            new Handler().post(new DisplayToast(context, getResources().getString(R.string.opengl_not_supported)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_boundary_definition, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            dataPoint = bundle.getParcelableArrayList(Constants.DATA_POINT_MSG);
        }

        setBtnFit((ImageButton) rootView.findViewById(R.id.fitButton));
        setBtnInterpolate((ImageButton) rootView.findViewById(R.id.interpolateButton));
        glSurfaceView = (OpenGLSurfaceView) rootView.findViewById(R.id.view);
        glSurfaceView.init(this, DataOperations.centralize(DataOperations.convertPointsToFloatArray(dataPoint)));

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

    @Override
    public void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    public void fitGeometry() {
        glSurfaceView.fitGeometry();
        btnFit.setVisibility(View.INVISIBLE);
    }

    public void interpolate() {
        ArrayList<Integer> selectedIndices = glSurfaceView.getSelectedIndices();
        Collections.sort(selectedIndices);

        /*int[] numPoints = new int[4];
        for (int i = 0; i < numPoints.length - 1; i++) {
            numPoints[i] = selectedIndices.get(i + 1) - selectedIndices.get(i) + 1;
        }
        numPoints[numPoints.length - 1] = dataPoint.size() - selectedIndices.get(numPoints.length - 1)
                + selectedIndices.get(0) + 1;

        DialogFragment dialog = BoundaryDialog.newInstance(Math.max(numPoints[0], numPoints[2]),
                Math.max(numPoints[1], numPoints[3]));
        dialog.show(context.getFragmentManager(), "BoundaryDialog");*/
    }

    public ImageButton getBtnFit() {
        return btnFit;
    }

    public void setBtnFit(ImageButton btnFit) {
        this.btnFit = btnFit;
    }

    public ImageButton getBtnInterpolate() {
        return btnInterpolate;
    }

    public void setBtnInterpolate(ImageButton btnInterpolate) {
        this.btnInterpolate = btnInterpolate;
    }
}
