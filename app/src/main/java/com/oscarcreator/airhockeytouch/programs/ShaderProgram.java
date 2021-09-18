package com.oscarcreator.airhockeytouch.programs;

import android.content.Context;

import com.oscarcreator.airhockeytouch.util.ShaderHelper;
import com.oscarcreator.airhockeytouch.util.TextResourceReader;

import static android.opengl.GLES20.*;

public class ShaderProgram {

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_COLOR = "u_Color";

    //Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    //Shader program
    protected final int program;
    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId){
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(
                        context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(
                        context, fragmentShaderResourceId));

    }

    //Tells OpenGL to use program for subsequent rendering
    public void useProgram(){
        //Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }

}
