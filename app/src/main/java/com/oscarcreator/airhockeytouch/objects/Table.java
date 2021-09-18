package com.oscarcreator.airhockeytouch.objects;

import com.oscarcreator.airhockeytouch.data.VertexArray;
import com.oscarcreator.airhockeytouch.programs.TextureShaderProgram;

import static com.oscarcreator.airhockeytouch.Constants.BYTES_PER_FLOAT;
import static android.opengl.GLES20.*;
public class Table {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            //Order of coordinates: X, Y, S, T

            //Triangle Fan
            0f,    0f, 0.5f, 0.5f,
            -0.5f, -0.8f,   0f, 0.9f,
            0.5f, -0.8f,   1f, 0.9f,
            0.5f,  0.8f,   1f, 0.1f,
            -0.5f,  0.8f,   0f, 0.1f,
            -0.5f, -0.8f,   0f, 0.9f
    };

    private final VertexArray vertexArray;

    public Table(){
        //Copy over vertexdata to a floatbuffer in native memory
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureProgram){
        //bind the position data to the shader attribute
        // referenced by getPositi...()
        vertexArray.setVertexAttribPointer(0,
                textureProgram.getPostionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }


}
