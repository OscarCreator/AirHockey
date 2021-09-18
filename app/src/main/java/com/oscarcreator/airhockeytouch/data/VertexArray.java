package com.oscarcreator.airhockeytouch.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.oscarcreator.airhockeytouch.Constants.*;

public class VertexArray {


    private final FloatBuffer floatBuffer;

    //takes in vertexdata and writes it to the buffer
    public VertexArray(float[] vertexData){
        floatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride){

        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount,
                GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);

        floatBuffer.position(0);
    }

}
