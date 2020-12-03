package com.example.encoding10bit;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlaySine {
    private final int sampleRate = 192000;
    private AudioTrack audioTrack;
    int bufferSize=AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
    private int sampleCount;
    short s;

    public PlaySine(){

        audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, sampleRate , AudioFormat.CHANNEL_OUT_MONO , AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);
    }

    public void setWave(int frequency) {
        sampleCount = (int) ((float) sampleRate / frequency);
        short samples[] = new short[sampleCount];
        int amplitude = 32767;
        double twoPi = Math.PI * 2;
        double phase = 0;

        for (int i = 0; i < sampleCount; i++) {
            samples[i] = (short) (amplitude * Math.sin(phase));
            phase += twoPi * frequency / sampleRate ;


//            samples[i] = (short) (amplitude * Math.sin((twopi * frequency / sampleRate) + phase));
//            phase+=(float)(360/sampleCount);

        }
        audioTrack.write(samples, 0, sampleCount);
    }

    public void setWaveTwo(int frequency) {
        sampleCount = (int) ((float) sampleRate / frequency);
        short sample[] = new short[sampleCount];
        int amplitude = 32767;
        double twoPi = Math.PI * 2;
        double phase = 0;

        for (int i = 0; i < sampleCount; i++) {
            sample[i] = (short) (amplitude * Math.sin(phase));
            phase += twoPi * frequency / sampleRate ;


//            samples[i] = (short) (amplitude * Math.sin((twoPi * frequency / sampleRate) + phase));
//            phase+=(float)(360/sampleCount);

        }
        audioTrack.write(sample, 0, sampleCount);
    }

    public void start(){
        audioTrack.reloadStaticData();
        audioTrack.setLoopPoints(0, sampleCount, -1);
        audioTrack.play();
    }

    public void stop(){
        audioTrack.flush();
        audioTrack.stop();
    }
}
