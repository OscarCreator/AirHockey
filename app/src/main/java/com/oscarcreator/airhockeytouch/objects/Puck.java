package com.oscarcreator.airhockeytouch.objects;

import com.oscarcreator.airhockeytouch.data.VertexArray;
import com.oscarcreator.airhockeytouch.programs.ColorShaderProgram;

import static com.oscarcreator.airhockeytouch.util.Geometry.*;

import static com.oscarcreator.airhockeytouch.objects.ObjectBuilder.*;

import java.util.List;

public class Puck {

    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius, height;

    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck){
        GeneratedData generatedData = ObjectBuilder.createPuck(
                new Cylinder(new Point(0,0,0), radius, height), numPointsAroundPuck);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram){
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);

    }

    public void draw(){
        for (DrawCommand drawCommand : drawList){
            drawCommand.draw();
        }
    }
}
