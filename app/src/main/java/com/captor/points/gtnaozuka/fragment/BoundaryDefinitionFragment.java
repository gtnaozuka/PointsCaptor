package com.captor.points.gtnaozuka.fragment;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.Constants;
import com.captor.points.gtnaozuka.util.DisplayToast;
import com.captor.points.gtnaozuka.util.OpenGLSurfaceView;
import com.captor.points.gtnaozuka.util.Versions;

public class BoundaryDefinitionFragment extends Fragment {

    private AppCompatActivity context;

    private float[] dataPoint;
    private GLSurfaceView glSurfaceView;

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
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            dataPoint = bundle.getFloatArray(Constants.DATA_POINT_MSG);
        }

        glSurfaceView = new OpenGLSurfaceView(context, dataPoint);
        return glSurfaceView;
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
}
