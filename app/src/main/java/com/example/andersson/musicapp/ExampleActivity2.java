package com.example.andersson.musicapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

public class ExampleActivity2 extends InstrumentActivity {

    // GUI code

    private Button recordButton;
    private Button loopTimeButton;
    private EditText loopTimeText;
    private Button barButton;
    private EditText barText;

    // end GUI

    // Instrument code
    public ExampleActivity2(){
        super();
    }

    @Override
    void generateSoundInfo(int index) { // should be connected to a sensor, it's called at new beatTime

    }

    @Override
    String setName() { // Set the name, mostly for thread separation

        return "ExampleActivity2";
    }

    @Override
    int getActivity(){

        return R.layout.activity_example2;
    }

    @Override
    int getMenu(){

        return R.menu.menu_example2;
    }

    @Override
    InstrumentThread getInstrumentClass() {// Return corresponding instrument that the activity should use
        return new ExampleInstrumentThread2(this,holder.getTimer());
    }

    @Override
    void initiate() { // Sets basic information regarding bars, looptime and possibly initial sound.

        int bar = 8;
        int loop = 16;

        instrument.setSoundList(new ArrayList<Integer>(Arrays.asList(0,1,0,1,0,1,0,1)));
        instrument.setBars(bar);
        instrument.setLoopTime(loop);

        // GUI/Initiate initiate
        loopGUI();
        barGUI();
        recordGUI();
        // end GUI/Instrumet initiate

    }

    private void loopGUI(){

        loopTimeText = (EditText) findViewById(R.id.LoopTimeView);
        loopTimeButton = (Button) findViewById(R.id.loopTimeButton);
        loopTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                try {

                    instrument.setLoopTime(Integer.valueOf(loopTimeText.getText().toString()));
                    loopTimeText.setText("");

                } catch (Exception e) {

                    if (instrument != null) {
                        loopTimeText.setText(instrument.getLoopTime());
                    } else {
                        loopTimeText.setText("");
                    }
                }
            }
        });
    }
    private void barGUI(){

        barText = (EditText) findViewById(R.id.BarView);
        barButton = (Button) findViewById(R.id.BarButton);
        barButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                try {
                    instrument.setBars(Integer.valueOf(barText.getText().toString()));
                    barText.setText("");
                } catch (Exception e) {

                    if (instrument != null) {
                        barText.setText(instrument.getBars());
                    } else {
                        barText.setText("");
                    }
                }
            }

        });
    }
    private void recordGUI(){

        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                index = 0;
                bars = instrument.getBars();
                loopTime = instrument.getLoopTime();
                barButton.setBackgroundColor(Color.RED);
                loopTimeButton.setBackgroundColor(Color.RED);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        barButton.setEnabled(true);
                        loopTimeButton.setEnabled(true);

                        barButton.setBackgroundColor(Color.GREEN);
                        loopTimeButton.setBackgroundColor(Color.GREEN);
                    }
                }, loopTime*1000);

                barButton.setEnabled(false);
                loopTimeButton.setEnabled(false);

                soundList = new ArrayList<Integer>();


                new Thread() {

                    public synchronized void run() {

                        while (true) {

                            try {

                                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(50);

                                index++;

                                generateSoundInfo(index);

                                if (index == bars) {

                                    break;
                                }

                                Thread.sleep(((loopTime / bars) * 1000) - 50);

                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }

                        String s = "";
                        for(int in : soundList){
                            s = s + in + " ";
                        }
                        Log.d("EA - Record", s);

                        instrument.setSoundList(soundList);

                    }

                }.start();
            }
        });
    }

}