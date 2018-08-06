package com.example.theoking.cubey;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class MyGLRenderer implements GLSurfaceView.Renderer{

  private Cube mCube;

  private final float[] mMVPMatrix = new float[16];
  private final float[] mProjectionMatrix = new float[16];
  private final float[] mViewMatrix = new float[16];
  private final float[] mRotationMatrix = new float[16];

  @Override
  public void onSurfaceCreated(GL10 unused, EGLConfig config){
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);

    mCube = new Cube();
  }

  public void onDrawFrame(GL10 unused){
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    float[] finalTransform = new float[16];

    long time = SystemClock.uptimeMillis() % 4000L;
    float angle = 0.09f * ((int) time);
    Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 1.0f, -1.0f);
    Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -4, 0f, 0f, 0f, 0f, 1.0f, 0f);
    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    Matrix.multiplyMM(finalTransform, 0, mMVPMatrix, 0, mRotationMatrix, 0);

    //mTriangle.draw(mMVPMatrix);
    mCube.draw(finalTransform);
  }

  public void onSurfaceChanged(GL10 unused, int width, int height){
    GLES20.glViewport(0, 0, width, height);

    float ratio = (float) width/height;
    Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
  }

  public static int loadShader(int type, String code){
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, code);
    GLES20.glCompileShader(shader);

    return shader;
  }
}