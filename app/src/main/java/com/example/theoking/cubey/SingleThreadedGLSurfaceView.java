package com.example.theoking.cubey;

import static javax.microedition.khronos.egl.EGL10.EGL_STENCIL_SIZE;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/** Class that emulates GLSurfaceView without creating a separate GLThread */
public class SingleThreadedGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {

  private static final EGL10 EGL = (EGL10) EGLContext.getEGL();
  private EGLConfig eglConfig;
  private EGLDisplay display;
  private EGLContext context;
  private EGLSurface surface;
  private final GLSurfaceView.Renderer render;

  public SingleThreadedGLSurfaceView(Context context, GLSurfaceView.Renderer myRenderer) {
    super(context);
    this.setWillNotDraw(false);
    getHolder().addCallback(this);
    createEGL();
    render = myRenderer;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    invalidate();
    render.onDrawFrame(null);
    EGL.eglSwapBuffers(display, surface);
  }

  private void createEGL() {
    int[] version = new int[2];
    display = EGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
    EGL.eglInitialize(display, version);

    int[] numConfigs = new int[1];
    EGL.eglGetConfigs(display, null, 0, numConfigs);
    EGLConfig[] allConfigs = new EGLConfig[numConfigs[0]];
    EGL.eglGetConfigs(display, allConfigs, numConfigs[0], numConfigs);

    int[] configAttrib =
        new int[] {
          EGL10.EGL_RENDERABLE_TYPE,
          EGL14.EGL_OPENGL_ES2_BIT,
          EGL10.EGL_SURFACE_TYPE,
          EGL10.EGL_WINDOW_BIT,
          EGL10.EGL_RED_SIZE,
          8,
          EGL10.EGL_GREEN_SIZE,
          8,
          EGL10.EGL_BLUE_SIZE,
          8,
          EGL10.EGL_ALPHA_SIZE,
          8,
          EGL10.EGL_DEPTH_SIZE,
          16,
          EGL_STENCIL_SIZE,
          0,
          EGL10.EGL_NONE
        };

    EGLConfig[] selectedConfig = new EGLConfig[1];
    EGL.eglChooseConfig(display, configAttrib, selectedConfig, 1, numConfigs);
    if (selectedConfig[0] != null) {
      eglConfig = selectedConfig[0];
      Log.i("SingleThreadSurfaceView", "Found matching EGL config");
    } else {
      Log.e("SingleThreadSurfaceView", "Could not find matching EGL config");
      throw new RuntimeException("No Matching EGL Config Found");
    }
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    surface = EGL.eglCreateWindowSurface(display, eglConfig, this, null);
    int[] contextAttribs = new int[] {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
    context = EGL.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, contextAttribs);
    EGL.eglMakeCurrent(display, surface, surface, context);
    render.onSurfaceCreated(null, eglConfig);
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    // Need to implement changes to EGL as well? Create new Window Surface, Context, etc?
    render.onSurfaceChanged(null, width, height);
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {}

  @Override
  public void surfaceRedrawNeeded(SurfaceHolder holder) {}
}
