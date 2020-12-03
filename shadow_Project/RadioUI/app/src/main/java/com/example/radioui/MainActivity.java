package com.example.radioui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    Button button;
    RadioButton WaveButton;
    RadioGroup radioGroup;
    ToggleButton onOff;
    double frequency;
    TextView tv1;
    EditText et1;
    PlayWave wave = new PlayWave();
    AudioManager audioManager;
    int id;
    SeekBar volumeSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        onOff = (ToggleButton) findViewById(R.id.toggleButton);
        tv1 = (TextView) findViewById(R.id.tv1);
        et1 = (EditText) findViewById(R.id.ed);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeSeekBar = (SeekBar) findViewById(R.id.seekBar2);

        volumeSeekBarInit();

        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch(id){
                        case 2131231028: onOffSquare();
                            break;
                        case 2131230734:  onOffTriangle();
                            break;
                        case 2131230989: onOffSawTooth();
                            break;
                        default: onOffSin();
                    }
                }
                else{
                    wave.stop();
                }
            }
        });
    }

    public void onclickbuttonMethod(View v) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        id = selectedId;
//        tv1.setText(id+"");
        WaveButton = (RadioButton) findViewById(selectedId);

        if (selectedId == -1) {
            Toast.makeText(MainActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show();
        }
        else {
            if (WaveButton.getText() == "Sine") {
                Toast.makeText(MainActivity.this, WaveButton.getText(), Toast.LENGTH_SHORT).show();

            } else if (WaveButton.getText() == "Square") {
                Toast.makeText(MainActivity.this, WaveButton.getText(), Toast.LENGTH_SHORT).show();

            } else if (WaveButton.getText() == "Triangular") {
                Toast.makeText(MainActivity.this, WaveButton.getText(), Toast.LENGTH_SHORT).show();


            } else if (WaveButton.getText() == "SawTooth") {
                Toast.makeText(MainActivity.this, WaveButton.getText(), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, WaveButton.getText(), Toast.LENGTH_SHORT).show();

            }
        }
    }


    private void onOffSin() {
        //    When The ToggleSwitch is pressed
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
            boolean on = onOff.isChecked();
            if(on){
                wave.start();
            }
            else{
                wave.stop();
            }
        }
    }
    private void onOffSquare() {
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
            boolean on = onOff.isChecked();
            if(on){
                wave.start();
            }
            else{
                wave.stop();
            }
        }
    }
    private void onOffTriangle() {
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
            boolean on = onOff.isChecked();
            if(on){
                wave.start();
            }
            else{
                wave.stop();
            }
        }
    }
    private void onOffSawTooth() {
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
            boolean on = onOff.isChecked();
            if(on){
                wave.start();
            }
            else{
                wave.stop();
            }
        }
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
}

