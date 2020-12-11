package com.example.maxfrequencyfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_PERMISSION_CODE = 1000;
    String[] freqText = {"192 KHz"};
    Integer[] freqSet = {192000};
    private ArrayAdapter adapter;
    Spinner spFrequency;
    Button startRec, stopRec, playBack;
    Boolean recording;  /** Called when the activity is first created. */
    TextView tv1;
    Complex[] complexData;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermissionFromDevice()){
            requestPermission();
        }
        
        
        startRec = (Button)findViewById(R.id.startrec);
        stopRec = (Button)findViewById(R.id.stoprec);
        playBack = (Button)findViewById(R.id.playback);
        tv1=(TextView) findViewById(R.id.textView4);

        startRec.setOnClickListener(startRecOnClickListener);
        stopRec.setOnClickListener(stopRecOnClickListener);
        playBack.setOnClickListener(playBackOnClickListener);

        spFrequency = (Spinner)findViewById(R.id.frequency);

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, freqText);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spFrequency.setAdapter(adapter);

        stopRec.setEnabled(false);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    OnClickListener startRecOnClickListener= new OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Thread recordThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    recording = true;
                    startRecord();
                }
            });
            recordThread.start();
            startRec.setEnabled(false);
            stopRec.setEnabled(true);
        }
    };

    OnClickListener stopRecOnClickListener= new OnClickListener(){
        @Override
        public void onClick(View arg0)
        {recording = false;
            startRec.setEnabled(true);
            stopRec.setEnabled(false);
        }
    };

    OnClickListener playBackOnClickListener= new OnClickListener(){
        @Override
        public void onClick(View v) {
            playRecord();
        }
    };

    private void startRecord(){
        File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");
        int selectedPos = spFrequency.getSelectedItemPosition();
        int sampleFreq = freqSet[selectedPos];
        final String promptStartRecord ="startRecord()\n"+ file.getAbsolutePath() + "\n"+ (String)spFrequency.getSelectedItem();

        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,promptStartRecord,Toast.LENGTH_LONG).show();
            }
        });

        try {
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize = AudioRecord.getMinBufferSize(sampleFreq,AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT);

            short[] audioData = new short[minBufferSize];

            short[] audioDataNew = new short[2048];

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleFreq,AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,minBufferSize);

            audioRecord.startRecording();


            while(recording){
                int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);

//                if (numberOfShort > 0) {
//                    calculate(minBufferSize,audioData,sampleFreq);
//                }

                System.out.println("-----------------------------------");
                System.out.println(audioData.length);
                System.out.println("-----------------------------------");

                Complex[] fftTempArray = new Complex[8192];
                for(int j = 0,k=1000; j<8192;j++,k++){
                    fftTempArray[j] = new Complex(audioData[k], 0);
                }

                Complex[] fftArray=FFT.fft(fftTempArray);

                int bufferSizeInBytes = 8192;

                System.out.println("-///////////////////////");
                System.out.println(bufferSizeInBytes);
                System.out.println("-///////////////////////////");

                double[] magnitude = new double[bufferSizeInBytes/ 2];

//                calculate power spectrum (magnitude) values from fft[]
                for (int k = 0; k < (8192 / 2) - 1; ++k) {
                    double real = fftArray[k].re();
                    double imaginary = fftArray[k].im();
                    magnitude[k] = Math.sqrt(real * real + imaginary * imaginary);
                }

                // find largest peak in power spectrum
                double max_magnitude = magnitude[0];
                int max_index = 0;
                for (int k = 0; k < magnitude.length; ++k) {
                    if (magnitude[k] > max_magnitude) {
                        max_magnitude = (int) magnitude[k];
                        max_index = k;
                    }
                }
                double freq = (float)sampleFreq * (float)max_index /(float) 8192;//here will get frequency in hz like(17000,18000..etc)

                System.out.println("**************************");
                System.out.println(freq);
                System.out.println("***************************");

                tv1.setText(String.valueOf(freq));


//                double[] magnitude = new double[bufferSizeInBytes/ 2];
//
//                //Create Complex array for use in FFT
//                Complex[] fftTempArray = new Complex[bufferSizeInBytes+1];
//                for (int k = 0; k < bufferSizeInBytes; k++) {
//                    fftTempArray[k] = new Complex(audioData[k], 0);
//                }
//
//                System.out.println("***************************");
//                System.out.println(fftTempArray.length);
//                System.out.println("***************************");
//                System.out.println(fftTempArray[bufferSizeInBytes]);
//                System.out.println("***************************");
//
//                fftTempArray[bufferSizeInBytes]=new Complex(audioData[bufferSizeInBytes-1], 0);
//                System.out.println(fftTempArray[bufferSizeInBytes]);
//                System.out.println("***************************");
//
////            fftTempArray[bufferSizeInBytes+1]=new Complex(audioData[bufferSizeInBytes], 0);
//
//                //Obtain array of FFT data
//                Complex[] fftTempArrayNew = new Complex[8192];
//                if(sampleFreq==192000) {
//                    for (int j = 0, l = 200000; j < 8192; j++, l++) {
//                        fftTempArrayNew[j] = fftTempArray[l];
//                    }
//                }
//                else{
//                    for (int j = 0, l =40000; j < 8192; j++, l++) {
//                        fftTempArrayNew[j] = fftTempArray[l];
//                    }
//                }
//
//                System.out.println(fftTempArrayNew.length);
//                Complex[] fftArray=FFT.fft(fftTempArrayNew);
//
//
//
////             calculate power spectrum (magnitude) values from fft[]
//                for (int k = 0; k < (8192 / 2) - 1; ++k) {
//
//                    double real = fftArray[k].re();
//                    double imaginary = fftArray[k].im();
//                    magnitude[k] = Math.sqrt(real * real + imaginary * imaginary);
//                }
//
////            // find largest peak in power spectrum
//                double max_magnitude = magnitude[0];
//                int max_index = 0;
//                for (int k = 0; k < magnitude.length; ++k) {
//                    if (magnitude[k] > max_magnitude) {
//                        max_magnitude = (int) magnitude[k];
//                        max_index = k;
//                    }
//                }
//                double freq = (float)sampleFreq * (float)max_index /(float) 8192;//here will get frequency in hz like(17000,18000..etc)
//
//
//























                for(int i = 0; i < numberOfShort; i++){
                    dataOutputStream.writeShort(audioData[i]);
                }




            }
            audioRecord.stop();
            dataOutputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void calculate(int minBuffer,short[] buffer,int sampleFreq) {

        double[] magnitude = new double[minBuffer/ 2];

        //Create Complex array for use in FFT
        Complex[] fftTempArray = new Complex[minBuffer];
        for (int i = 0; i < minBuffer; i++) {
            fftTempArray[i] = new Complex(buffer[i], 0);
        }

        //Obtain array of FFT data
        final Complex[] fftArray = FFT.fft(fftTempArray);
        // calculate power spectrum (magnitude) values from fft[]
        for (int i = 0; i < (minBuffer / 2) - 1; ++i) {

            double real = fftArray[i].re();
            double imaginary = fftArray[i].im();
            magnitude[i] = Math.sqrt(real * real + imaginary * imaginary);

        }

        // find largest peak in power spectrum
        double max_magnitude = magnitude[0];
        int max_index = 0;
        for (int i = 0; i < magnitude.length; ++i) {
            if (magnitude[i] > max_magnitude) {
                max_magnitude = (int) magnitude[i];
                max_index = i;
            }
        }
        double freq = (float)sampleFreq * (float)max_index /(float) minBuffer;//here will get frequency in hz like(17000,18000..etc)

        System.out.println("--------------------------");
        System.out.println(freq);
        System.out.println("--------------------------");


    }

    void playRecord(){

        File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");
        int shortSizeInBytes = Short.SIZE/Byte.SIZE;
        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes)+1;
        short[] audioData = new short[bufferSizeInBytes];




        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            int i = 0;
            while(dataInputStream.available() > 0){
                audioData[i] = dataInputStream.readShort();
                i++;
            }
            dataInputStream.close();

            int selectedPos = spFrequency.getSelectedItemPosition();
            int sampleFreq = freqSet[selectedPos];

//            final String promptPlayRecord ="PlayRecord()\n"+ file.getAbsolutePath() + "\n"+ (String)spFrequency.getSelectedItem();
//            Toast.makeText(MainActivity.this,promptPlayRecord,Toast.LENGTH_LONG).show();
//            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,sampleFreq,AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSizeInBytes,AudioTrack.MODE_STREAM);
//            audioTrack.play();
//            audioTrack.write(audioData, 0, bufferSizeInBytes);



//            System.out.println("***************************");
//            System.out.println(audioData.length);
//            System.out.println("***************************");
//            System.out.println(bufferSizeInBytes);
//            System.out.println("***************************");
            System.out.println("***************************");
            System.out.println(bufferSizeInBytes);
            System.out.println("***************************");
            double[] magnitude = new double[bufferSizeInBytes/ 2];

            //Create Complex array for use in FFT
            Complex[] fftTempArray = new Complex[bufferSizeInBytes+1];
            for (int k = 0; k < bufferSizeInBytes; k++) {
                fftTempArray[k] = new Complex(audioData[k], 0);
            }

            System.out.println("***************************");
            System.out.println(fftTempArray.length);
            System.out.println("***************************");
            System.out.println(fftTempArray[bufferSizeInBytes]);
            System.out.println("***************************");

            fftTempArray[bufferSizeInBytes]=new Complex(audioData[bufferSizeInBytes-1], 0);
            System.out.println(fftTempArray[bufferSizeInBytes]);
            System.out.println("***************************");

//            fftTempArray[bufferSizeInBytes+1]=new Complex(audioData[bufferSizeInBytes], 0);

            //Obtain array of FFT data
            Complex[] fftTempArrayNew = new Complex[8192];
            if(sampleFreq==192000) {
                for (int j = 0, l = 200000; j < 8192; j++, l++) {
                    fftTempArrayNew[j] = fftTempArray[l];
                }
            }
            else{
                for (int j = 0, l =40000; j < 8192; j++, l++) {
                    fftTempArrayNew[j] = fftTempArray[l];
                }
            }

            System.out.println(fftTempArrayNew.length);
            Complex[] fftArray=FFT.fft(fftTempArrayNew);



//             calculate power spectrum (magnitude) values from fft[]
            for (int k = 0; k < (8192 / 2) - 1; ++k) {

                double real = fftArray[k].re();
                double imaginary = fftArray[k].im();
                magnitude[k] = Math.sqrt(real * real + imaginary * imaginary);
            }

//            // find largest peak in power spectrum
            double max_magnitude = magnitude[0];
            int max_index = 0;
            for (int k = 0; k < magnitude.length; ++k) {
                if (magnitude[k] > max_magnitude) {
                    max_magnitude = (int) magnitude[k];
                    max_index = k;
                }
            }
            double freq = (float)sampleFreq * (float)max_index /(float) 8192;//here will get frequency in hz like(17000,18000..etc)

           tv1.setText(String.valueOf(freq));

//            complexData = new Complex[audioData.length];
//            for (int j = 0; j < complexData.length; j++) {
//                complexData[j] = new Complex(audioData[j], 0);
//            }
//
//            Complex[] fftResult = FFT.fft(complexData);
//
//            System.out.println("****************************");
//            for(int k=0;k<fftResult.length;k++){
//                System.out.println(fftResult[k]);
//            }
//            System.out.println("****************************");

            //DO FFT TO FIND FREQUENCY

//            int frequency=findFrequency(sampleFreq,audioData);
//
//            System.out.println("*/*/*/*/*/*/*/*/*/*/*/*/");
//            System.out.println(frequency);
//            System.out.println("*/*/*/*/*/*/*/*/*/*/*/*/");




        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int findFrequency(int sampleFreq, short[] audioData) {
        int numSamples = audioData.length;
        int numCrossing = 0;
        for (int p = 0; p < numSamples-1; p++){
            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
                    (audioData[p] < 0 && audioData[p + 1] >= 0)){
                numCrossing++;}
        }
        float numSecondsRecorded = (float)numSamples/(float)sampleFreq;
        float numCycles = numCrossing/2;
        float frequency = numCycles/numSecondsRecorded;
        return (int)frequency;
    }
}
