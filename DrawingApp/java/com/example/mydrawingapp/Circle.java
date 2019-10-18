package com.example.mydrawingapp;

public class Circle implements Shape {
    protected float startX;
    protected float startY;
    protected float radius;
    protected int colorShape;

    public Circle(float startX, float startY,int color) {
        this.startX = startX;
        this.startY = startY;
        this.colorShape = color;
    }
}
