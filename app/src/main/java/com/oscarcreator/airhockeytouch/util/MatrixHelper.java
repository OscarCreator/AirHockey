package com.oscarcreator.airhockeytouch.util;

import android.util.Log;

public class MatrixHelper {

    private static final String TAG = "MatrixHelper";

    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f){

        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180);
        final float a = (float) (1f / Math.tan(angleInRadians / 2f));

        if (m.length > 15) {
            m[0] = a / aspect;
            m[1] = 0f;
            m[2] = 0f;
            m[3] = 0f;
            m[4] = 0f;
            m[5] = a;
            m[6] = 0f;
            m[7] = 0f;
            m[8] = 0f;
            m[9] = 0f;
            m[10] = -((f + n) / (f - n));
            m[11] = -1f;
            m[12] = 0f;
            m[13] = 0f;
            m[14] = -((2f * f * n) / (f - n));
            m[15] = 0f;
        }else {
            if (LoggerConfig.ON){
                Log.w(TAG, "Array length was to short. " +
                        "\nNeeds to be atleast 16 current is: " + m.length);
            }
        }
    }
}
