package com.oscarcreator.airhockeytouch.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static android.opengl.GLUtils.*;
import android.util.Log;

import static android.opengl.GLES20.*;

public class TextureHelper {

    private static final String TAG = "TextureHelper";


    //Takes a context and resourceid and returns the id of the loaded texture.
    public static int loadTexture(Context context, int resourceId){
        final int[] textureObjectIds = new int[1];
        //Generate a new texture object
        glGenTextures(1, textureObjectIds, 0);

        //Checks generation succeeded
        if (textureObjectIds[0] == 0){
            if (LoggerConfig.ON){
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }

        //Create a bitmapfactory
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //Tell's android that original image data instead of a scaled version.
        options.inScaled = false;
        //Get the data by decoding the image to a bitmap
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null){
            if (LoggerConfig.ON){
                Log.w(TAG, "Reource ID " + resourceId + " could not be decoded.");
            }
            glDeleteTextures(1,textureObjectIds,0);
            return 0;
        }
        //future calls should be applied to this texture object.
        //1:treated as a two-dim texture, 2:which texture object to bind.
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        //For minification use trilinear filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        //For magnification use bilinear filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //tells OpenGL to read in the bitmap data defined by bitmap and copy it
        // over into the texture object that is currently bound
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        //speed up garbage collection by releasing data immediately
        bitmap.recycle();
        //Generate all different levels of mipmaps for the texture
        glGenerateMipmap(GL_TEXTURE_2D);
        //unbinds the last binded texture
        glBindTexture(GL_TEXTURE_2D,0);

        return textureObjectIds[0];

    }
}
