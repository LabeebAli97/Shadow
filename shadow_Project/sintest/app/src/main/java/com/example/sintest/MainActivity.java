package com.example.sintest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Canvas canvas = new Canvas();
        Paint p = new Paint();
        p.setColor(Color.BLUE);

        for(int i=10;i<100;i++) {
            canvas.drawPoint(100+i, 200+i, p);
        }


    }
}