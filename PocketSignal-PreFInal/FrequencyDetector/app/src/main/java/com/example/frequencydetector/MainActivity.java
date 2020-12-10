package com.example.frequencydetector;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_PERMISSION_CODE = 2000;

    String[] freqText = {"44.1 KHz","192 KHz"};
    Integer[] freqSet = {44100,192000};
    private ArrayAdapter adapter;
    Spinner spFrequency;
    Button startRec, stopRec, findFreq;
    Boolean recording;  /** Called when the activity is first created. */
    TextView tv1;
    private static DecimalFormat df = new DecimalFormat("0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(!checkPermissionFromDevice()){
            requestPermission();
        }

        startRec = (Button)findViewById(R.id.startrec);
        stopRec = (Button)findViewById(R.id.stoprec);
        findFreq = (Button)findViewById(R.id.findfreq);
        tv1=(TextView) findViewById(R.id.textView);
        spFrequency = (Spinner)findViewById(R.id.frequency);

        tv1.setText("Frequency = ");

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, freqText);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spFrequency.setAdapter(adapter);

        startRec.setEnabled(true);
        stopRec.setEnabled(false);

        startRecButton();
        stopRecButton();
        findFreqButton();

    }



    private void startRecButton() {

        startRec.setOnClickListener(v -> {

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
        });
    }

    private void stopRecButton() {
        stopRec.setOnClickListener(v -> {

            recording = false;
            startRec.setEnabled(true);
            stopRec.setEnabled(false);

        });
    }

    private void findFreqButton() {
        findFreq.setOnClickListener(v -> {
            findFreq();
        });

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,


        },REQUEST_PERMISSION_CODE);
    }


    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED ;
    }


    private void startRecord(){
        File file = new File(Environment.getExternalStorageDirectory(), "test1.pcm");
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

            int minBufferSize = AudioRecord.getMinBufferSize(sampleFreq, AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT);

            short[] audioData = new short[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleFreq,AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,minBufferSize);

            audioRecord.startRecording();

            while(recording){
                int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);

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

    private void findFreq() {

        File file = new File(Environment.getExternalStorageDirectory(), "test1.pcm");
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

            double[] magnitude = new double[bufferSizeInBytes/ 2];

            //Create Complex array for use in FFT
            Complex[] fftTempArray = new Complex[bufferSizeInBytes+1];
            for (int k = 0; k < bufferSizeInBytes; k++) {
                fftTempArray[k] = new Complex(audioData[k], 0);
            }

            fftTempArray[bufferSizeInBytes]=new Complex(audioData[bufferSizeInBytes-1], 0);

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

            //here will get frequency in hz like(17000,18000..etc)
            double freq = (float)sampleFreq * (float)max_index /(float) 8192;

            tv1.setText("Frequency = "+String.valueOf(df.format(freq))+" Hz");

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


}