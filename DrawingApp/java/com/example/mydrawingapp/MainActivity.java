package com.example.mydrawingapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    DrawShapeView drawShapeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_Layout);
        navigationView = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawShapeView = findViewById(R.id.drawShapeView);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //set what happen when item pressed.
        //defining what shape or color the brush will paint.
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.circle_id)
                   drawShapeView.changeShape(DrawShapeView.DRAW_CIRCLE);
                if(menuItem.getItemId() == R.id.line_id)
                    drawShapeView.changeShape(DrawShapeView.DRAW_LINE);
                if(menuItem.getItemId() == R.id.Polyline_id)
                    drawShapeView.changeShape(DrawShapeView.DRAW_POLYLINE);
                if ((menuItem.getItemId() == R.id.color_red))
                    drawShapeView.setColor(Color.RED);
                if ((menuItem.getItemId() == R.id.color_blue))
                    drawShapeView.setColor(Color.BLUE);
                if ((menuItem.getItemId() == R.id.color_green))
                    drawShapeView.setColor(Color.GREEN);
                drawerLayout.closeDrawers();
                return false;
            }
        });

        FloatingActionButton fab = findViewById(R.id.pab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawShapeView.deleteLastShape();
                drawShapeView.deleteLastShape();//bag- didn't found the problem here;
            }
        });


    }
    // make the hamburger button active.
    // when he will pressed the drawer will pop.
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            drawerLayout.openDrawer(Gravity.START);
            //Toast.makeText(this, "menu selected", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
