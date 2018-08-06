package com.example.theoking.cubey;


import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class Triangle {

  private final String vertexShaderCode =
      "uniform mat4 uMVPMatrix;" +
          "attribute vec4 vPosition;" +
          "void main() {" +
          "  gl_Position = uMVPMatrix * vPosition;" +
          "}";

  private final String fragmentShaderCode =
      "precision mediump float;" +
          "uniform vec4 vColor;" +
          "void main() {" +
          "  gl_FragColor = vColor;" +
          "}";

  private FloatBuffer vertexBuffer;

  private final int mProgram;
  private int mPositionHandle;
  private int mColorHandle;
  private int mMVPMatrixHandle;

  private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
  private final int vertexStride = COORDS_PER_VERTEX * 4;

  static final int COORDS_PER_VERTEX = 3;
  // Counterclockwise winding order
  static float triangleCoords[] = {
      0.0f,  0.622008459f, 0.0f,
      -0.5f, -0.311004243f, 0.0f,
      0.5f, -0.311004243f, 0.0f
  };

  float color[] = {.5f, 1.0f, .2f, 1.0f};

  public Triangle() {

    ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
    bb.order(ByteOrder.nativeOrder());
    vertexBuffer = bb.asFloatBuffer();
    vertexBuffer.put(triangleCoords);
    vertexBuffer.position(0);

    int vertShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    mProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(mProgram, vertShader);
    GLES20.glAttachShader(mProgram, fragShader);

    GLES20.glLinkProgram(mProgram);
  }

  public void draw(float[] mvpMatrix){
    GLES20.glUseProgram(mProgram);

    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
    GLES20.glEnableVertexAttribArray(mPositionHandle);
    GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

    mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    GLES20.glUniform4fv(mColorHandle, 1, color, 0);

    mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

    GLES20.glDisableVertexAttribArray(mPositionHandle);
  }
}