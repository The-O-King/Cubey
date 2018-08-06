package com.example.theoking.cubey;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

class Cube {

  private final int mProgram;
  private int mPositionHandle;
  private int mColorHandle;
  private int mMVPMatrixHandle;
  private FloatBuffer mVertexBuffer;
  private FloatBuffer mColorBuffer;
  private ShortBuffer mIndexBuffer;
  static final int COORDS_PER_VERTEX = 3;
  private final int vertexStride = COORDS_PER_VERTEX * 4;

  private float vertices[] = {
      -0.5f, -0.5f, -0.5f,
      0.5f, -0.5f, -0.5f,
      0.5f, 0.5f, -0.5f,
      -0.5f, 0.5f, -0.5f,
      -0.5f, -0.5f, 0.5f,
      0.5f, -0.5f, 0.5f,
      0.5f, 0.5f, 0.5f,
      -0.5f, 0.5f, 0.5f
  };

  private float colors[] = {
      0.0f, 1.0f, 1.0f, 1.0f,
      1.0f, 0.0f, 0.0f, 1.0f,
      1.0f, 1.0f, 0.0f, 1.0f,
      0.0f, 1.0f, 0.0f, 1.0f,
      0.0f, 0.0f, 1.0f, 1.0f,
      1.0f, 0.0f, 1.0f, 1.0f,
      1.0f, 1.0f, 1.0f, 1.0f,
      0.0f, 1.0f, 1.0f, 1.0f,
  };

  private short indices[] = {
      0, 1, 3, 3, 1, 2, // Front face.
      0, 1, 4, 4, 5, 1, // Bottom face.
      1, 2, 5, 5, 6, 2, // Right face.
      2, 3, 6, 6, 7, 3, // Top face.
      3, 7, 4, 4, 3, 0, // Left face.
      4, 5, 7, 7, 6, 5, // Rear face.
  };

  private final String vertexShaderCode =
      "uniform mat4 uMVPMatrix;" +
          "attribute vec4 vPosition;" +
          "attribute vec4 vColor;" +
          "varying vec4 out_color;" +
          "void main() {" +
          "  out_color = vColor;" +
          "  gl_Position = uMVPMatrix * vPosition;" +
          "}";

  private final String fragmentShaderCode =
      "precision mediump float;" +
          "varying vec4 out_color;" +
          "void main() {" +
          "  gl_FragColor = out_color;" +
          "}";


  public Cube() {

    ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
    bb.order(ByteOrder.nativeOrder());
    mVertexBuffer = bb.asFloatBuffer();
    mVertexBuffer.put(vertices);
    mVertexBuffer.position(0);

    ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
    cb.order(ByteOrder.nativeOrder());
    mColorBuffer = cb.asFloatBuffer();
    mColorBuffer.put(colors);
    mColorBuffer.position(0);

    ByteBuffer ib = ByteBuffer.allocateDirect(indices.length * 2);
    ib.order(ByteOrder.nativeOrder());
    mIndexBuffer = ib.asShortBuffer();
    mIndexBuffer.put(indices);
    mIndexBuffer.position(0);

    int vertShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    mProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(mProgram, vertShader);
    GLES20.glAttachShader(mProgram, fragShader);
    GLES20.glLinkProgram(mProgram);
  }

  public void draw(float[] mvpMatrix) {
    GLES20.glUseProgram(mProgram);

    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
    GLES20.glEnableVertexAttribArray(mPositionHandle);
    GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, mVertexBuffer);

    mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
    GLES20.glEnableVertexAttribArray(mColorHandle);
    GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 16, mColorBuffer);

    mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

    GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

    GLES20.glDisableVertexAttribArray(mPositionHandle);
    GLES20.glDisableVertexAttribArray(mColorHandle);
  }

}
