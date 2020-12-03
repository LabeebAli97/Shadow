package com.example.shadowprojectmain;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayWave {

    private final int sampleRate = 192000;
    private AudioTrack audioTrack;
    int buffersize=AudioTrack.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
    private int sampleCount;
    short s;

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
            phase += twopi * frequency / sampleRate ;


//            samples[i] = (short) (amplitude * Math.sin((twopi * frequency / sampleRate) + phase));
//            phase+=(float)(360/sampleCount);

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

    public void setWaveSquare(int frequency) {

        sampleCount = (int) ((float) sampleRate / frequency);
        short samples[] = new short[sampleCount];
        int amplitude = 32767;
        double twopi = Math.PI * 2;
        double phase = 0;

        for (int i = 0; i < sampleCount; i++) {
            samples[i] = (short) (amplitude * Math.sin(phase));
            s=samples[i];
            if (s > 0.0) {
                samples[i] = 32767;
            }
            if (s < 0.0) {
                samples[i] = -32767;
            }
            phase += twopi * frequency / sampleRate;
        }

        audioTrack.write(samples, 0, sampleCount);
    }

    public void setWaveTriangle(int frequency) {
        sampleCount = (int) ((float)sampleRate/frequency);
        int amplitude = 32767;
        short samples[] = new short[sampleCount];
        for(int i=0; i<sampleCount; i++){

            if (i < (sampleCount / 4)) {
                samples[i] = (short) ((((float) i * (4.0f * (float) amplitude)) / (float) sampleCount));
            }
            else if (i > (3 * (sampleCount / 4))) {
                samples[i] = (short) (((((float) i * (4.0f * (float) amplitude)) / (float)sampleCount) - 2.0f * amplitude));
            }
            else {

                samples[i] = (short) (((float) 2.0f * amplitude - (((float) i * (4.0f * (float) amplitude)) / (float) sampleCount)));
            }
        }
        audioTrack.write(samples, 0, sampleCount);
    }

    public void setWaveSawTooth(int frequency) {
        int amplitude = 32767;
        sampleCount = (int) ((float)sampleRate/frequency);
        short samples[] = new short[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
//            samples[i] = (short) (((float) Short.MIN_VALUE + (((float) i * (2.0f * (float) Short.MAX_VALUE)) / (float) sampleCount)));
            samples[i] = (short) (((float) (-amplitude) + (((float) i * (2.0f * (float) amplitude)) / (float) sampleCount)));
        }
        audioTrack.write(samples, 0, sampleCount);
    }
}
