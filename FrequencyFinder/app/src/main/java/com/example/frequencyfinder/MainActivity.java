package com.example.frequencyfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private String TAG = "AUDIO_RECORD_PLAYBACK";
    private boolean isRunning = true;
    private Thread m_thread;               /* Thread for running the Loop */

    private AudioRecord recorder = null;
    private AudioTrack track = null;

    int bufferSize = 320;                  /* Buffer for recording data */
    byte buffer[] = new byte[bufferSize];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableButton(R.id.startButton, true);
        enableButton(R.id.stopButton, false);

        /* Assign Button Click Handlers */
        ((Button) findViewById(R.id.startButton)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.stopButton)).setOnClickListener(btnClick);


    }

    /* Method to Enable/Disable Buttons */
    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startButton: {
                    Log.d(TAG, "======== Start Button Pressed ==========");
                    isRunning = true;
                    do_loopback(isRunning);
                    enableButton(R.id.startButton, false);
                    enableButton(R.id.stopButton, true);
                    break;
                }
                case R.id.stopButton: {
                    Log.d(TAG, "======== Stop Button Pressed ==========");
                    isRunning = false;
                    do_loopback(isRunning);
                    enableButton(R.id.stopButton, false);
                    enableButton(R.id.startButton, true);
                    break;
                }
            }
        }

    };

    private void do_loopback(final boolean flag)
    {
        m_thread = new Thread(new Runnable() {
            public void run() {
                run_loop(flag);
            }
        });
        m_thread.start();
    }

    public AudioTrack findAudioTrack (AudioTrack track)
    {
        Log.d(TAG, "===== Initializing AudioTrack API ====");
        int m_bufferSize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (m_bufferSize != AudioTrack.ERROR_BAD_VALUE)
        {
            track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, m_bufferSize,
                    AudioTrack.MODE_STREAM);

            if (track.getState() == AudioTrack.STATE_UNINITIALIZED) {
                Log.e(TAG, "===== AudioTrack Uninitialized =====");
                return null;
            }
        }
        return track;
    }

    public AudioRecord findAudioRecord (AudioRecord recorder)
    {
        Log.d(TAG, "===== Initializing AudioRecord API =====");
        int m_bufferSize = AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (m_bufferSize != AudioRecord.ERROR_BAD_VALUE)
        {
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, m_bufferSize);

            if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                Log.e(TAG, "====== AudioRecord UnInitilaised ====== ");
                return null;
            }
        }
        return recorder;
    }

    public void run_loop (boolean isRunning)
    {

        /** == If Stop Button is pressed == **/
        if (isRunning == false) {
            Log.d(TAG, "=====  Stop Button is pressed ===== ");

            if (AudioRecord.STATE_INITIALIZED == recorder.getState()){
                recorder.stop();
                recorder.release();
            }
            if (AudioTrack.STATE_INITIALIZED == track.getState()){
                track.stop();
                track.release();
            }
            return;
        }


        /** ======= Initialize AudioRecord and AudioTrack ======== **/
        recorder = findAudioRecord(recorder);
        if (recorder == null) {
            Log.e(TAG, "======== findAudioRecord : Returned Error! =========== ");
            return;
        }

        track = findAudioTrack(track);
        if (track == null) {
            Log.e(TAG, "======== findAudioTrack : Returned Error! ========== ");
            return;
        }

        if ((AudioRecord.STATE_INITIALIZED == recorder.getState()) &&
                (AudioTrack.STATE_INITIALIZED == track.getState()))
        {
            recorder.startRecording();
            Log.d(TAG, "========= Recorder Started... =========");
            track.play();
            Log.d(TAG, "========= Track Started... =========");
        }
        else
        {
            Log.d(TAG, "==== Initilazation failed for AudioRecord or AudioTrack =====");
            return;
        }

        /** ------------------------------------------------------ **/

        /* Recording and Playing in chunks of 320 bytes */
        bufferSize = 320;

        while (isRunning == true)
        {
            /* Read & Write to the Device */
            recorder.read(buffer, 0, bufferSize);
            track.write(buffer, 0, bufferSize);

        }
        Log.i(TAG, "Loopback exit");
        return;
    }

}