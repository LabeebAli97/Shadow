package com.example.testsine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    ToggleButton t1;
    double frequency;
    EditText e1;
    PlayWaves wave = new PlayWaves();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1=(ToggleButton) findViewById(R.id.toggleButton);
        e1=(EditText) findViewById(R.id.editTextNumber);

        onOffSin();
    }

    private void onOffSin() {
        //    When The ToggleSwitch is pressed
        t1.setOnClickListener(v -> {

            frequency = Double.parseDouble(e1.getText().toString());

            if(frequency>=20 && frequency<=20000) {
                wave.setWave((int)frequency);

                boolean on = t1.isChecked();
                if(on){
                    wave.start();
                }
                else{
                    wave.stop();
                }
            }
        });
    }

}