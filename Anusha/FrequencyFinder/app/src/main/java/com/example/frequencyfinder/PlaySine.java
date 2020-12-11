package com.example.frequencyfinder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlaySine {
    private final int sampleRate = 192000;
    private AudioTrack audioTrack;
    int bufferSize=AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
    private int sampleCount;


    public PlaySine(){

        audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, sampleRate , AudioFormat.CHANNEL_OUT_MONO , AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);
    }

    public void setWave(int frequency) {
        sampleCount = (int) (((float) sampleRate / frequency)*12);
        short samples[] = new short[sampleCount];
        int amplitude = 32767;
        double twoPi = Math.PI * 2;
        double phase = 0;


            for (int i = 0; i < sampleCount; i++) {
                samples[i] = (short) (amplitude * Math.sin(phase));
                phase += twoPi * frequency / sampleRate;
            }

//            samples[i] = (short) (amplitude * Math.sin((twopi * frequency / sampleRate) + phase));
//            phase+=(float)(360/sampleCount);

            audioTrack.write(samples, 0, sampleCount);
    }


//    AudioTrack audioTrack;
//    float samples[];
//    int minBufferSize=AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
//
//    public PlaySine(){
//        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STATIC);
//
//    }
//
//        public void setWave(int frequency) {
//
//
//            int sampleRate = 44100;
//            int bitsPerChannel = 16;
//            int bytesPerChannel = bitsPerChannel / 8;
//            int channelCount = 1;
//            int bytesPerSample = channelCount * bytesPerChannel;
//            int bytesPerRotation = (int)(sampleRate * bytesPerSample * (1d / (double) frequency));
//
//
//
//            short[] buffer = new short[minBufferSize*bytesPerRotation];
//            float increment = (float)(2*Math.PI) * frequency / 44100; // angular increment for each sample
//            float angle = 0;
//            samples = new float[minBufferSize*bytesPerRotation];
//
//            for (int i = 0; i < samples.length; i++) {
//                samples[i] = (float) Math.sin(angle);   //the part that makes this a sine wave....
//                buffer[i] = (short) (samples[i] * Short.MAX_VALUE);
//                angle += increment;
//            }
//
//            audioTrack.write(buffer, 0, samples.length );
//        }


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
