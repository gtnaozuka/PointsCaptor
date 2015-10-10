package com.captor.points.gtnaozuka.util;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.captor.points.gtnaozuka.fragment.BoundaryDefinitionFragment;
import com.captor.points.gtnaozuka.util.operations.DataOperations;

import java.util.ArrayList;

public class OpenGLSurfaceView extends GLSurfaceView implements View.OnLayoutChangeListener {

    private Context context;
    private BoundaryDefinitionFragment bdf;
    private float[] vertices;

    private OpenGLRenderer renderer;

    private ScaleGestureDetector scaleDetector;
    private GestureDetectorCompat gestureDetector;

    private ArrayList<Integer> selectedIndices;

    private static float X_TOUCH_SCALE_FACTOR, Y_TOUCH_SCALE_FACTOR, RATIO;
    private float previousX, previousY;
    private static final float ERROR_RADIUS = 0.1f;

    public OpenGLSurfaceView(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.context = context;
    }

    public void init(BoundaryDefinitionFragment bdf, float[] vertices) {
        this.bdf = bdf;
        this.vertices = vertices;

        renderer = new OpenGLRenderer(this.vertices);
        this.setEGLContextClientVersion(2);
        this.setPreserveEGLContextOnPause(true);
        this.setRenderer(renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetectorCompat(context, new GestureListener());
        this.addOnLayoutChangeListener(this);

        selectedIndices = new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        scaleDetector.onTouchEvent(e);
        gestureDetector.onTouchEvent(e);

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                bdf.getBtnFit().setVisibility(VISIBLE);
                float dx = x - previousX;
                float dy = y - previousY;

                renderer.setxOffset(renderer.getxOffset() - dx * X_TOUCH_SCALE_FACTOR);
                renderer.setyOffset(renderer.getyOffset() - dy * Y_TOUCH_SCALE_FACTOR);
                requestRender();
        }

        previousX = x;
        previousY = y;
        return true;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (left == 0 && top == 0 && right == 0 && bottom == 0) {
            return;
        }
        int width = right - left;
        int height = bottom - top;

        X_TOUCH_SCALE_FACTOR = 1.0f / width;
        Y_TOUCH_SCALE_FACTOR = 1.0f / height;
        RATIO = (float) width / height;

        this.removeOnLayoutChangeListener(this);
    }

    public ArrayList<Integer> getSelectedIndices() {
        return selectedIndices;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            bdf.getBtnFit().setVisibility(VISIBLE);
            float scaleFactor = detector.getScaleFactor();

            renderer.setScaleFactor(renderer.getScaleFactor() * scaleFactor);
            requestRender();

            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = (-2.0f * e.getX() * X_TOUCH_SCALE_FACTOR + 1.0f) * RATIO;
            float y = -2.0f * e.getY() * Y_TOUCH_SCALE_FACTOR + 1.0f;
            float[] transformedVertices = DataOperations.calculateTransformedVertices(renderer.calculateMatrix(), vertices);

            int selectedIndex = 0;
            float smallerDistance = DataOperations.calculateDistance(x, y, transformedVertices[0], transformedVertices[1]);
            for (int i = 3; i < transformedVertices.length; i += 3) {
                float currentDistance = DataOperations.calculateDistance(x, y, transformedVertices[i], transformedVertices[i + 1]);
                if (currentDistance < smallerDistance) {
                    smallerDistance = currentDistance;
                    selectedIndex = i / 3;
                }
            }

            if (smallerDistance <= ERROR_RADIUS) {
                if (selectedIndices.contains(Integer.valueOf(selectedIndex))) {
                    bdf.getBtnInterpolate().setVisibility(INVISIBLE);
                    selectedIndices.remove(Integer.valueOf(selectedIndex));
                } else if (selectedIndices.size() == 4) {
                    return false;
                } else {
                    selectedIndices.add(selectedIndex);
                    if (selectedIndices.size() == 4) {
                        bdf.getBtnInterpolate().setVisibility(VISIBLE);
                    }
                }

                renderer.changeColor(selectedIndex);
                requestRender();
            }

            return true;
        }
    }

    public void fitGeometry() {
        renderer.fitGeometry();
        requestRender();
    }
}
