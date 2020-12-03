package com.example.encoding10bit;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    EditText et1;
    ToggleButton tb1;
    int[] freqArray = new int[15];
    String s;
    PlaySine wave = new PlaySine();
    TextView tv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = (EditText) findViewById(R.id.editTextNumber);
        tv1 = (TextView) findViewById(R.id.textView);
        tb1 = (ToggleButton) findViewById(R.id.toggleButton);

        encodeInit();

    }

    private void encodeInit() {

            tb1.setOnClickListener(v -> {
                s = et1.getText().toString();

                if(s.length()>10 || s.length()==0){
                    et1.setText("Enter a valid Code");
                }
                else {
                    boolean press = tb1.isChecked();
                    if (press) {

                        for (int i = 0, j = 1; i < s.length(); i++, j++) {
                            freqArray[i] = Integer.parseInt(s.substring(i, j));
                            if (freqArray[i] != 0) {
                                wave.setWave(1000 * freqArray[i]);
                                wave.start();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                wave.setWave(500);
                                wave.start();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            wave.stop();
                        }
                    } else {
                        wave.stop();
                    }
                }
            });
    }
}
