package com.oscarcreator.airhockeytouch;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.oscarcreator.airhockeytouch.objects.*;
import com.oscarcreator.airhockeytouch.programs.*;
import com.oscarcreator.airhockeytouch.util.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;


public class AirHockeyRenderer implements Renderer {

    private final String TAG = "AirHockeyRenderer";

    private final Context context;

    //Viewmatrix representation
    private final float[] viewMatrix = new float[16];
    //Placeholders for multiplications
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private final float[] modelMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];

    private final float[] invertedViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;
    private Geometry.Point previouseBlueMalletPosition;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;


    public AirHockeyRenderer(Context context) {
        this.context = context;

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        //Clear color to black
        glClearColor(0,0,0,0);

        //Creating our table and mallet objects
        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);
        puck = new Puck(0.06f, 0.02f, 32);

        //Creating our shader programs
        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        //Loading table texture
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

        puckPosition = new Geometry.Point(0f, puck.height / 2, 0f);
        puckVector = new Geometry.Vector(0,0,0);

    }



    @Override
    public void onDrawFrame(GL10 gl) {
        //Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        //This will cache the results of multiplying the projection and view matrices
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix,0,
                viewMatrix, 0);
        //converts a 2d point to a 3d line(ray)(undoing perspective projection which is
        // used to convert 3d to 2d)
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

        //Draw the table
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        //Draw the mallets.
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1, 0, 0);
        mallet.bindData(colorProgram);
        mallet.draw();
        //Drawing the same mallet at a different position and color.
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();


        puckPosition = puckPosition.translate(puckVector);

        if (puckPosition.x < leftBound + puck.radius
            || puckPosition.x > rightBound - puck.radius){
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
        }
        if (puckPosition.z < farBound + puck.radius
            || puckPosition.z > nearBound - puck.radius){
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
        }

        //Clamp the puck position
        puckPosition = new Geometry.Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius));

        puckVector = puckVector.scale(0.99f);
        if (puckVector.length() < 0.005){
            puckVector = new Geometry.Vector(0,0,0);
        }

        //Draw the puck.
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f,0.8f,1);
        puck.bindData(colorProgram);
        puck.draw();



    }

    //This is called when the size on the screen is changed.
    //For example from portrait to landscape mode.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        //Set up a projection matrix
        MatrixHelper.perspectiveM(projectionMatrix, 50,
                (float) width / (float) height, 1f, 10f);
        //Setting up a special type of projection matrix
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    public void handleTouchPress(float normalizedX, float normalizedY){
        if (LoggerConfig.ON){
            Log.d(TAG, String.format("Press: x:%f y:%f", normalizedX, normalizedY));
        }
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        //BoundingSphere of the mallet.
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(
                blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z), mallet.height / 2f);
        //If the ray intersects the mallets boundingsphere then
        // malletPressed = true
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY){
        if (LoggerConfig.ON){
            Log.d(TAG, String.format("Drag: x:%f y:%f", normalizedX, normalizedY));
        }

        if (malletPressed){
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            //Define a plane representing our air hockey table
            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0,0,0), new Geometry.Vector(0,1,0));

            //Find out where the touched point intersects the plane
            // representing our table. We'll move the mallet along this plane.
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            previouseBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = new Geometry.Point(clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z, mallet.radius, nearBound - mallet.radius));

            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();

            if (distance < (puck.radius + mallet.radius)){

                puckVector = Geometry.vectorBetween(
                        previouseBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    private void positionTableInScene(){
        //The table is defined in xy-plane so we rotate it
        // 90degrees to lie flat on the xz-plane.
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z){
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY){

        //Convert normalized device coordinates to world-space coordinates.
        //We'll pick a point on the near and far planes and create a line
        // between them. This is done with multiplying by inverse matrix
        // and then undo perspective divide.

        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};


        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix,
                0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix,
                0, farPointNdc, 0);
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay = new Geometry.Point(
                nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(
                farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay,
                Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector){
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    private float clamp(float value, float min, float max){
        return Math.min(max, Math.max(value, min));
    }



}
