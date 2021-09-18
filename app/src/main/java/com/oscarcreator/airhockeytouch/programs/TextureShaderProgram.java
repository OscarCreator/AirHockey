package com.oscarcreator.airhockeytouch.programs;

import android.content.Context;
import com.oscarcreator.airhockeytouch.R;
import static android.opengl.GLES20.*;

public class TextureShaderProgram extends ShaderProgram{

    //Uniform locations;
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    //Attribute locations
    private final int aPostionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context){
        super(context, R.raw.texture_vertex_shader,
                R.raw.texture_fragment_shader);

        //Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        //Retrieve attribute locations for the shader program.
        aPostionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation =
                glGetAttribLocation(program, A_TEXTURE_COORDINATES);

    }

    public void setUniforms(float[] matrix, int textureId){
        //Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        //Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        //bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);

        //Tell the texture uniform sampler to use this texture
        // in the shader by telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPostionAttributeLocation() {
        return aPostionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
