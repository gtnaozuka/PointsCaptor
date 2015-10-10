package com.captor.points.gtnaozuka.util;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.captor.points.gtnaozuka.entity.Boundary;
import com.captor.points.gtnaozuka.util.operations.DataOperations;

import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRenderer implements Renderer {

    private float[] vertices, geometrySize;
    private ArrayList<Boundary> boundaries;

    private float[] mvpMatrix, projectionMatrix, viewMatrix, translationMatrix, scaleMatrix;

    private volatile float xOffset;
    private volatile float yOffset;
    private volatile float scaleFactor;

    private static final float[] COLOR_PRIMARY = {0.506f, 0.78f, 0.518f, 1.0f};
    private static final float[] COLOR_ACCENT = {1.0f, 0.596f, 0.0f, 1.0f};

    public OpenGLRenderer(float[] vertices) {
        this.vertices = vertices;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.961f, 0.961f, 0.961f, 1.0f);

        geometrySize = DataOperations.calculateGeometrySize(vertices);
        boundaries = new ArrayList<>();
        for (int i = 0; i < vertices.length; i += 3) {
            float[] vertex = new float[3];

            vertex[0] = vertices[i];
            vertex[1] = vertices[i + 1];
            vertex[2] = vertices[i + 2];

            Boundary b = new Boundary(vertex, COLOR_PRIMARY);
            boundaries.add(b);
        }

        mvpMatrix = new float[16];
        projectionMatrix = new float[16];
        viewMatrix = new float[16];
        translationMatrix = new float[16];
        scaleMatrix = new float[16];

        fitGeometry();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        mvpMatrix = applyTransformations(mvpMatrix);

        for (Boundary b : boundaries) {
            b.draw(mvpMatrix);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public float[] calculateMatrix() {
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        return applyTransformations(matrix);
    }

    private float[] applyTransformations(float[] matrix) {
        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0, xOffset, yOffset, 0);
        Matrix.multiplyMM(matrix, 0, matrix, 0, translationMatrix, 0);

        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scaleFactor, scaleFactor, 1.0f);
        Matrix.multiplyMM(matrix, 0, matrix, 0, scaleMatrix, 0);

        return matrix;
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public void changeColor(int i) {
        if (Arrays.equals(boundaries.get(i).getColors(), COLOR_PRIMARY)) {
            boundaries.get(i).setColors(COLOR_ACCENT);
        } else {
            boundaries.get(i).setColors(COLOR_PRIMARY);
        }
    }

    public void fitGeometry() {
        xOffset = 0.0f;
        yOffset = 0.0f;

        float xScaleFactor = 1.0f / geometrySize[0];
        float yScaleFactor = 1.0f / geometrySize[1];
        if (xScaleFactor < yScaleFactor)
            scaleFactor = xScaleFactor;
        else
            scaleFactor = yScaleFactor;
    }

    public float getxOffset() {
        return xOffset;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
}