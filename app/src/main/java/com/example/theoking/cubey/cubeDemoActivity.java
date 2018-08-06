package com.example.theoking.cubey;

import android.app.Activity;
import android.os.Bundle;


public class cubeDemoActivity extends Activity {

    private SingleThreadedGLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mGLView = new SingleThreadedGLSurfaceView(this, new MyGLRenderer());
        setContentView(mGLView);

        /*
        mGLView = new GLSurfaceView(this);
        mGLView.setEGLContextClientVersion(2);
        mGLView.setRenderer(new MyGLRenderer());
        // Only draw the view when there is a change to the drawing data
        //mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGLView);
        */
    }
}



