package com.example.shadowproject;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayWave {

    private final int sampleRate = 44100;
    private AudioTrack audioTrack;
    int buffersize=AudioTrack.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
    private int sampleCount;

    public PlayWave(){

        audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, sampleRate , AudioFormat.CHANNEL_OUT_MONO , AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STATIC);

    }

    public void setWave(int frequency) {
        sampleCount = (int) ((float) sampleRate / frequency);
        short samples[] = new short[sampleCount];
        int amplitude = 32767;
        double twopi = Math.PI * 2;
        double phase = 0;

        for (int i = 0; i < sampleCount; i++) {
            samples[i] = (short) (amplitude * Math.sin(phase));
            phase += twopi * frequency / sampleRate;
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
