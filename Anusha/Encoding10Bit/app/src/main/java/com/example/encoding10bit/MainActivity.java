package com.example.encoding10bit;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    EditText et1;
    ToggleButton tb1;
    int[] freqArray = new int[15];
    String s;
    PlaySine wave = new PlaySine();
    TextView tv1,tv2;
    SeekBar volumeSeekBar;
    AudioManager audioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = (EditText) findViewById(R.id.editTextNumber);
        tv1 = (TextView) findViewById(R.id.textView);
        tb1 = (ToggleButton) findViewById(R.id.toggleButton);
        tv2 = (TextView) findViewById(R.id.volume);
        volumeSeekBar = (SeekBar) findViewById(R.id.seekBar2);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        tv2.setText(String.valueOf(volumeSeekBar.getProgress()));

        encodeInit();
        volumeSeekBarInit();

    }

    private void volumeSeekBarInit() {
        try {
            volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
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
                tv2.setText(String.valueOf((int)(progress*6.67)));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void encodeInit() {

            tb1.setOnClickListener(v -> {

                s = et1.getText().toString();

                if(s.length()>10 || s.length()==0){
                    et1.setText("Enter a valid Code");
                }

                Thread encodeThread = new Thread(new Runnable(){
                    @Override
                    public void run() {

                        if(s.length()>0 && s.length()<=10) {
                            boolean press = tb1.isChecked();
                            if (press) {

                                for (int i = 0, j = 1; i < s.length(); i++, j++) {
                                    freqArray[i] = Integer.parseInt(s.substring(i, j));
                                    if (freqArray[i] != 0) {
                                        wave.setWave(1000 * freqArray[i]);
                                        wave.start();

                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        wave.setWave(10000);
                                        wave.start();
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    wave.stop();
                                }
                            } else {
                                wave.stop();
                            }
                        }

                    }
                });

                encodeThread.start();

            });
    }
}
