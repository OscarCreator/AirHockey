package com.oscarcreator.airhockeytouch.util;

import android.util.Log;

import static android.opengl.GLES20.*;

public class ShaderHelper {


    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * Returns the shaderId which we can reference to later to
     * be able to run the code
     */
    private static int compileShader(int type, String shaderCode) {

        final int shaderObjectId = glCreateShader(type);

        //When this is true the shader creation failed
        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader.");
            }
        }
        //Uploads the shader code to the shaderObject
        glShaderSource(shaderObjectId, shaderCode);

        //Compiles the shader code which is in the shaderObject
        glCompileShader(shaderObjectId);

        //Retrieves information if the shader compilation succeeded.
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        //Log the shader result
        if (LoggerConfig.ON) {
            Log.v(TAG, "Result of compiling source:\n" + shaderCode + "\n:"
                    + glGetShaderInfoLog(shaderObjectId));
        }

        //Verifying if the shader compilation was successful.
        if (compileStatus[0] == 0) {
            //Deletes the shader object if compilation failed.
            glDeleteShader(shaderObjectId);

            if (LoggerConfig.ON) {
                Log.w(TAG, "compilation of shader failed.");
            }
            return 0;
        }

        return shaderObjectId;
    }

    /**
     * Links a vertex and fragment-shader to a shader program.
     *
     * @return returns 0 if it failed else the shader program id.
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = glCreateProgram();

        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new program.");
            }
            return 0;
        }
        //Attach the fragment and vertex-shader to the shader program object
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);

        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        //Prints out the result of link fragment and vertex-shader
        if (LoggerConfig.ON) {
            Log.v(TAG, "Results of linking program:\n"
                    + glGetProgramInfoLog(programObjectId));
        }

        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed.");
            }
            return 0;
        }

        return programObjectId;

    }

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        //Validates the program
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);


        Log.v(TAG, "Results of validating program: " + validateStatus[0]
                + "\nLog: " + glGetProgramInfoLog(programObjectId));


        return validateStatus[0] != 0;
    }

    /** will compile the shaders defined and link them together into a program
     * if logging is on program will be validated.*/
    public static int buildProgram(String vertexShaderSource,
                                   String fragmentShaderSource){
        int program;

        //Compile the shaders
        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        //Link them into a shader program.
        program = linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON){
            validateProgram(program);
        }

        return program;
    }
}
