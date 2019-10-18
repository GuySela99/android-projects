package com.example.mydrawingapp;

public class Line implements Shape{
    protected float startLineX;
    protected float endLineX;
    protected float startLineY;
    protected float endLineY;
    protected int colorShape;

    public Line(float startLineX, float endLineX, float startLineY, float endLineY) {
        this.startLineX = startLineX;
        this.endLineX = endLineX;
        this.startLineY = startLineY;
        this.endLineY = endLineY;
    }

    public Line(float startLineX, float startLineY, int color) {
        this.startLineX = startLineX;
        this.startLineY = startLineY;
        this.colorShape = color;
    }
}
