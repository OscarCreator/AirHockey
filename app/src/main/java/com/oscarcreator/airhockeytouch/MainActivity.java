package com.oscarcreator.airhockeytouch;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean renderSet = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        //This is used to check if the device supports opengl version 2.0
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();

        //This makes sure both emulators and device that run this supports OpenGl ES 2.0 or later
        //This also assumes that the emulator supports OpenGL ES 2.0
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));
        final AirHockeyRenderer airHockeyRenderer = new AirHockeyRenderer(this);

        if (supportsEs2) {
            //Request and OpenGL ES 2.0 compatible context.
            //This only works if the device supports the version
            glSurfaceView.setEGLContextClientVersion(2);

            //##### If emulator not working glSurfaceView.setEGLConfigChoose(8,8,8,8,16,0)

            //Assign our renderer.
            glSurfaceView.setRenderer(airHockeyRenderer);
            renderSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0",
                    Toast.LENGTH_LONG).show();
            return;
        }
        glSurfaceView.setOnTouchListener((view, event) -> {
            if (event != null) {
                final float normalizedX =
                        (event.getX() / (float) view.getWidth()) * 2 - 1;
                final float normalizedY =
                        -((event.getY() / (float) view.getHeight()) * 2 - 1);

                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    //New runnable. which is because the main activity
                    // runs separate from the OpenGL renderer.
                    glSurfaceView.queueEvent(() ->
                            airHockeyRenderer.handleTouchPress(
                                normalizedX, normalizedY));
                }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    glSurfaceView.queueEvent(() ->
                            airHockeyRenderer.handleTouchDrag(
                                normalizedX, normalizedY));
                }
                return true;
            }else {
                return false;
            }
        });

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //!!Makes sure to pause the glsurfaceview when another activity is
        // started up front
        if (renderSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //!!Makes sure to resume the glsurfaceview when another activity is
        // closed and this is the main activity.
        if (renderSet) {
            glSurfaceView.onResume();
        }
    }
}
