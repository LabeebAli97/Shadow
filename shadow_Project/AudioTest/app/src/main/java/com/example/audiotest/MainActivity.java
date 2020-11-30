package com.example.audiotest;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    final int duration = 10; // duration of sound
    final int sampleRate = 22050; // Hz (maximum frequency is 7902.13Hz (B8))
    final int numSamples = duration * sampleRate;
    final double samples[] = new double[numSamples];
    final short buffer[] = new short[numSamples];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i = 0; i < numSamples; ++i)
        {
            int note[] = new int[100];
            samples[i] = Math.sin(2 * Math.PI * i / (sampleRate / note[0])); // Sine wave
            buffer[i] = (short) (samples[i] * Short.MAX_VALUE);  // Higher amplitude increases volume
        }

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buffer.length,
                AudioTrack.MODE_STATIC);

        audioTrack.write(buffer, 0, buffer.length);
        audioTrack.play();

    }


}