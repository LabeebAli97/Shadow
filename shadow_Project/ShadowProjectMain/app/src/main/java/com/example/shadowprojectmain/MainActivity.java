package com.example.shadowprojectmain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    ToggleButton onOffSin,onOffSquare,onOffTriangle,onOffSawTooth;
    TextView tv1,tv2,tv3,tv4,tv5,tv6;
    EditText et1;
    double frequency;
    PlayWave wave = new PlayWave();
    SeekBar freqSeekBar,volumeSeekBar;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onOffSin =(ToggleButton) findViewById(R.id.toggleButton);
        onOffSquare =(ToggleButton) findViewById(R.id.toggleButton2);
        onOffTriangle =(ToggleButton) findViewById(R.id.toggleButton3);
        onOffSawTooth =(ToggleButton) findViewById(R.id.toggleButton4);
        tv1=(TextView) findViewById(R.id.disFreq);
        tv2=(TextView) findViewById(R.id.textView);
        tv3=(TextView) findViewById(R.id.sine);
        tv4=(TextView) findViewById(R.id.square);
        tv5=(TextView) findViewById(R.id.triangle);
        tv6=(TextView) findViewById(R.id.sawtooth);
        et1=(EditText) findViewById(R.id.enterFreq);
        freqSeekBar = (SeekBar) findViewById(R.id.seekBar);
        volumeSeekBar = (SeekBar) findViewById(R.id.seekBar2);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        onOffSin();
        onOffSquare();
        onOffTriangle();
        onOffSawTooth();
        freqSeekBarInit();
        volumeSeekBarInit();

    }




    private void volumeSeekBarInit() {
        try {
            volumeSeekBar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void freqSeekBarInit() {
        // When seekbar is changed

        freqSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

//                wave.stop();
                tv2.setText(String.valueOf(progress));
                frequency = Double.parseDouble(tv2.getText().toString());
                wave.setWave((int) frequency);
                wave.start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int freq = freqSeekBar.getProgress();
                wave.setWave((int) freq);
                wave.start();
                // app crashing but freqseekbar working
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.stop();
            }
        });
    }

    private void onOffSin() {
        //    When The ToggleSwitch is pressed
        onOffSin.setOnClickListener(v -> {
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

            if(frequency>=20 && frequency<=20000) {
                wave.setWave((int)frequency);

                boolean on = onOffSin.isChecked();
                if(on){
                    wave.start();
                }
                else{
                    wave.stop();
                }
            }
        });
    }

    private void onOffSquare() {
        onOffSquare.setOnClickListener(v -> {
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

            if(frequency>=20 && frequency<=20000) {
                wave.setWaveSquare((int)frequency);

                boolean on = onOffSquare.isChecked();
                if(on){
                    wave.start();

                }
                else{
                    wave.stop();
                }
            }
        });
    }

    private void onOffTriangle() {
        onOffTriangle.setOnClickListener(v -> {
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

            if(frequency>=20 && frequency<=20000) {
                wave.setWaveTriangle((int)frequency);

                boolean on = onOffTriangle.isChecked();
                if(on){
                    wave.start();

                }
                else{
                    wave.stop();
                }
            }

        });
    }

    private void onOffSawTooth() {
        onOffSawTooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                if(frequency>=20 && frequency<=20000) {
                    wave.setWaveSawTooth((int)frequency);

                    boolean on = onOffSawTooth.isChecked();
                    if(on){
                        wave.start();
                    }
                    else{
                        wave.stop();
                    }
                }
            }
        });
    }
}


