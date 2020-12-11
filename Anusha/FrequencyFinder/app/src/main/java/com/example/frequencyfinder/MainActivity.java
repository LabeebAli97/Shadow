package com.example.frequencyfinder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    EditText et1;
    ToggleButton tb1;
    int[] freqArray = new int[15];
    String s;
    PlaySine wave = new PlaySine();
    TextView tv1;
    double frequency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = (EditText) findViewById(R.id.editTextNumber);
        tv1 = (TextView) findViewById(R.id.textView);
        tb1 = (ToggleButton) findViewById(R.id.toggleButton);

        encodeInit();


    }

    private void encodeInit() {

        tb1.setOnClickListener(v -> {

            try {
                frequency = Double.parseDouble(et1.getText().toString());
                if(frequency>=20 && frequency<=20000) {
                    tv1.setText(String.valueOf(frequency));

                }
                else{
                    tv1.setText("Enter a frequency in range of 20Hz-20Khz");
                }
            }
            catch (Exception e){

                e.printStackTrace();
            }

            Thread waveGeneratorThread = new Thread(new Runnable(){
                @Override
                public void run() {

                    if(frequency>=20 && frequency<=20000) {

                        wave.setWave((int)frequency);
                        boolean on = tb1.isChecked();
                        if(on){
                            wave.start();
                        }
                        else{
                            wave.stop();
                        }
                    }
                }
            });

            waveGeneratorThread.start();


        });
    }





}