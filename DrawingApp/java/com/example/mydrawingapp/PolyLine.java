package com.example.mydrawingapp;

import java.util.ArrayList;

public class PolyLine implements Shape {
    ArrayList<Point> polyLineInPoints;
    protected int colorShape;

    public PolyLine(int color) {
        polyLineInPoints = new ArrayList<>();
        colorShape = color;
    }

    public void addPoint(Point point){
        polyLineInPoints.add(point);
    }
}
