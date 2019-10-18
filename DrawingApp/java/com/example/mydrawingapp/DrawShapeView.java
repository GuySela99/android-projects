package com.example.mydrawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Stack;

public class DrawShapeView extends View {

   Stack<Shape> shapes = new Stack<>();
   public Paint paint;
   public static int DRAW_CIRCLE =0;
   public static int DRAW_LINE = 1;
   public static int DRAW_POLYLINE=2;
   int SHAPE = DRAW_CIRCLE;
   int color = Color.MAGENTA;
    public DrawShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // defining the paint object.
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(10);
    }
    // draw a circle and line.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Shape shape:shapes){
            if(shape instanceof Line){
                Line line = (Line)shape;
                paint.setColor(line.colorShape);//before she draw the shape she set color do the instance that save there.
                canvas.drawLine(line.startLineX,line.startLineY,line.endLineX,line.endLineY,paint);
            }
            if(shape instanceof Circle){
                Circle circle = (Circle)shape;
                paint.setColor(circle.colorShape);//before she draw the shape she set color do the instance that save there.
                canvas.drawCircle(circle.startX,circle.startY,circle.radius,paint);
            }
            if(shape instanceof PolyLine){
                PolyLine polyLine = (PolyLine) shape;
                paint.setColor(polyLine.colorShape);//before she draw the shape she set color do the instance that save there.
                //paint.setStrokeWidth(20);
                for(int i =0; i< polyLine.polyLineInPoints.size(); i++){
                    Point point1 = polyLine.polyLineInPoints.get(i);
                    if(i+1<polyLine.polyLineInPoints.size()) {
                        Point point2 = polyLine.polyLineInPoints.get(i + 1);
                        canvas.drawLine(point1.x, point1.y,point2.x, point2.y,paint);
                    }
                    else
                        canvas.drawPoint(point1.x, point1.y,paint);
                }
                //paint.setStrokeWidth(0);
                
            }
        }
        paint.setColor(color);
    }
    // this method override and define the onTouchEvent.
    //basically she crate a new shape(could be circle or line) and save him in the database
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                if (SHAPE == DRAW_POLYLINE){
                    //shapes.add(polyLine);
                }
                if (SHAPE == DRAW_LINE) {
                    Line lastLine = (Line) shapes.peek();
                    lastLine.endLineX = event.getX();
                    lastLine.endLineY = event.getY();
                }
                invalidate();
                return false;
            case MotionEvent.ACTION_DOWN:
                if (SHAPE == DRAW_LINE) {
                    Line line = new Line(event.getX(), event.getY(),color);
                    shapes.push(line);
                }
                if (SHAPE == DRAW_CIRCLE) {
                    Circle circle = new Circle(event.getX(), event.getY(),color);
                    shapes.push(circle);
                }
                if (SHAPE == DRAW_POLYLINE){
                    PolyLine polyLine = new PolyLine(color);
                    shapes.push(polyLine);
                }
                return true;
            case(MotionEvent.ACTION_MOVE):
                if (SHAPE == DRAW_LINE) {
                    Line lastLine = (Line) shapes.peek();
                    lastLine.endLineX = event.getX();
                    lastLine.endLineY = event.getY();
                }
                if (SHAPE == DRAW_CIRCLE) {
                    Circle circle = (Circle) shapes.peek();
                    circle.radius = (float)Math.hypot(circle.startX-event.getX(),circle.startY-event.getY());
                }
                if(SHAPE == DRAW_POLYLINE){
                    PolyLine polyLine = (PolyLine) shapes.peek();
                    Point p = new Point(event.getX(),event.getY());
                    polyLine.addPoint(p);
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
    // change the shape- we have only two- line and circle.
    public void changeShape(int shape){
        SHAPE = shape;
    }
    // change the color of the next shape that will draw.
    public void setColor(int newColor){
        color = newColor;
        paint.setColor(newColor);
    }
    // delete the last shape that created.
    public void deleteLastShape(){
        if(shapes.empty())
            return;
        shapes.pop();
        invalidate();
    }
}
