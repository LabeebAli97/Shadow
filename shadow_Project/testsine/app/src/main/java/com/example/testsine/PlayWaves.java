package com.example.testsine;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayWaves {

    private final int sampleRate = 44100;
    private AudioTrack audioTrack;
    int bufferSize=AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
    private int sampleCount;
    short s;

    short[] buffer = new short[bufferSize];
    short[] sample = new short[sampleRate];

    public PlayWaves(){

        audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, sampleRate , AudioFormat.CHANNEL_OUT_MONO , AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);
    }

    public void setWave(int frequency) {

        short[] returnArray = new short[sampleRate];

        for (int i = 0; i < sampleRate; i++) {
            if (i < (sampleRate / 2)) {
                returnArray[i] = Short.MAX_VALUE;
            }
            else {
                returnArray[i] = Short.MIN_VALUE;
            }
        }

        }
        audioTrack.write(samples, 0, sampleCount);
    }

    public void start(){
        audioTrack.reloadStaticData();
        audioTrack.setLoopPoints(0, sampleCount, -1);
        audioTrack.play();
    }

    public void stop(){

        audioTrack.stop();
    }
}
