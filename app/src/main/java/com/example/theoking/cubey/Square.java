package com.example.theoking.cubey;


import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

class Square{

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

  private final int mProgram;
  private int mPositionHandle;
  private int mColorHandle;
  private int mMVPMatrixHandle;

  private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
  private final int vertexStride = COORDS_PER_VERTEX * 4;

  private FloatBuffer vertexBuffer;
  private ShortBuffer drawListBuffer;

  static final int COORDS_PER_VERTEX = 3;
  static float squareCoords[] = {
      -0.5f,  0.5f, 0.0f,
      -0.5f, -0.5f, 0.0f,
      0.5f, -0.5f, 0.0f,
      0.5f,  0.5f, 0.0f
  };

  private short drawOrder[] = { 0, 1, 2, 0, 2, 3 };
  float color[] = {.5f, 1.0f, .2f, 1.0f};

  public Square(){
    ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
    bb.order(ByteOrder.nativeOrder());
    vertexBuffer = bb.asFloatBuffer();
    vertexBuffer.put(squareCoords);
    vertexBuffer.position(0);

    ByteBuffer sb = ByteBuffer.allocateDirect(drawOrder.length * 2);
    sb.order(ByteOrder.nativeOrder());
    drawListBuffer = sb.asShortBuffer();
    drawListBuffer.put(drawOrder);
    drawListBuffer.position(0);

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


    GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

    GLES20.glDisableVertexAttribArray(mPositionHandle);
  }
}

