package com.example.frequencydetectorlive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_PERMISSION_CODE = 1000;
    String[] freqText = {"192 KHz"};
    Integer[] freqSet = {192000};
    private ArrayAdapter adapter;
    Spinner spFrequency;
    Button startRec, stopRec, playBack;
    Boolean recording;  /** Called when the activity is first created. */
    TextView tv1,tv2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermissionFromDevice()){
            requestPermission();
        }


        startRec = (Button)findViewById(R.id.startbutton);
        stopRec = (Button)findViewById(R.id.stopbutton);

        tv1=(TextView) findViewById(R.id.textView);
        tv2=(TextView) findViewById(R.id.textView2);

        spFrequency = (Spinner)findViewById(R.id.frequency);

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, freqText);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spFrequency.setAdapter(adapter);

        startRec.setEnabled(true);
        stopRec.setEnabled(false);

        startRecButton();
        stopRecButton();

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

    private void startRecord(){
        File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");

        int selectedPos = spFrequency.getSelectedItemPosition();

        int sampleFreq = freqSet[selectedPos];

        try {
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize = AudioRecord.getMinBufferSize(sampleFreq,AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT);

            short[] audioData = new short[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleFreq,AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,minBufferSize);

            audioRecord.startRecording();

            String decodeResult="";



            while(recording){
                int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);

                Complex[] fftTempArray = new Complex[8192];
                for(int j = 0,k=1000; j<8192;j++,k++){
                    fftTempArray[j] = new Complex(audioData[k], 0);
                }
                Complex[] fftArray=FFT.fft(fftTempArray);

                int bufferSizeInBytes = 8192;

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


                tv1.setText(String.valueOf(freq));



                if(freq>800 && freq<1200){
                    decodeResult+=1;
                }
                else if(freq>1800 && freq<2200){
                    decodeResult+="2";
                }
                else if(freq>2800 && freq<3200){
                    decodeResult+="3";
                }
                else if(freq>3800 && freq<4200){
                    decodeResult+="4";
                }
                else if(freq>4800 && freq<5200){
                    decodeResult+="5";
                }
                else if(freq>5800 && freq<6200){
                    decodeResult+="6";
                }
                else if(freq>6800 && freq<7200){
                    decodeResult+="7";
                }
                else if(freq>7800 && freq<8200){
                    decodeResult+="8";
                }
                else if(freq>8800 && freq<9200){
                    decodeResult+="9";
                }
                else if(freq>9800 && freq<10200){
                    decodeResult+="0";
                }


                for(int i = 0; i < numberOfShort; i++){
                    dataOutputStream.writeShort(audioData[i]);
                }

            }

            if (decodeResult.length()!=0) {
                int a;
                int[] freqDecodeArray = new int[decodeResult.length()];
                String decode = "";

                for (int i = 0; i < decodeResult.length(); i++) {
                    freqDecodeArray[i] = Integer.parseInt(decodeResult.substring(i, i + 1));
                }

                a = freqDecodeArray[0];
                int count = 0;
                for (int i = 1; i < freqDecodeArray.length; i++) {

                    if (freqDecodeArray[i] == a) {
                        count += 1;
                        if (count == 3) {
                            decode += String.valueOf(a);
                        }
                    } else {
                        a = freqDecodeArray[i];
                        count = 1;
                    }
                }


                tv2.setText(decode);


            }
            else{
                tv2.setText("Code Not Detected");
            }

            audioRecord.stop();
            dataOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
}