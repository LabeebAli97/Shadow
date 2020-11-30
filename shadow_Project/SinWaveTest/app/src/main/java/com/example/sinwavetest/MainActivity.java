package com.example.sinwavetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MyChart d = new MyChart(MainActivity.this);
        setContentView(d);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }
}
