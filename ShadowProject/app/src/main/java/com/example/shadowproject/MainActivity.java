package com.example.shadowproject;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    ToggleButton onOff;
    TextView tv1;
    EditText et1;
    double frequency;
    PlayWave wave = new PlayWave();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onOff=(ToggleButton) findViewById(R.id.toggleButton);
        tv1=(TextView) findViewById(R.id.disFreq);
        et1=(EditText) findViewById(R.id.enterFreq);

        // When The ToggleSwitch is pressed
        onOff.setOnClickListener(v -> {
            try {
                frequency = Double.parseDouble(et1.getText().toString());
                if(frequency>=20 && frequency<=20000) {
                    tv1.setText(String.valueOf(frequency));
                }
                else{
                    tv1.setText("Enter a frequency in range of 20Hz-20Khz");
                }
            }
            catch (Exception e){

                e.printStackTrace();
            }

            if(frequency>=20 && frequency<=20000) {
                wave.setWave((int)frequency);

                boolean on = onOff.isChecked();
                if(on){
                    wave.start();
                }
                else{
                    wave.stop();
                }
            }


        });



    }

}


