package com.example.shadowproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Random;

public class MyChart extends View

{
    Paint paint;
    Random g;
    float X, Y;

    public MyChart (Context context)
    {
        super(context);
        g = new Random ();
        paint = new Paint();
        paint.setColor (Color.WHITE);
    }

    @Override
    protected void onDraw (Canvas c)
    {
        float i;
        super.onDraw (c);
        c.drawPaint (paint);
        paint.setAntiAlias (true);
        paint.setStyle (Paint.Style.STROKE);
        paint.setColor (Color.BLACK);

        for (i = 150; i < 1300; i = i + 1)
        {
            c.drawLine (i, 200 + 100*(float)Math.sin(i*2*Math.PI * 3000 / 192000), i + 1, 200 + 100*(float)Math.sin((i+10)*2*Math.PI * 3000 / 192000), paint);
        }
    }
}