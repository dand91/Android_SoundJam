package com.example.andersson.musicapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class ExampleActivity extends ActionBarActivity {


    private Button recordButton;
    private InstrumentThread instrument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        instrument = new ExampleInstrument();
        instrument.setSoundList(new ArrayList<Integer>(Arrays.asList(200,210,220,230,240,250,260,270)));
        instrument.setBars(8);
        instrument.setLoopTime(16);
        instrument.start();


        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                int i = 0;
                int bars = instrument.getBars();
                int loopTime = instrument.getLoopTime();

                ArrayList<Integer> soundList = new ArrayList<Integer>();

                while (true) {

                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

                    // Vibrate for 500 milliseconds
                    v.vibrate(50);

                    i++;

                    int min = 200;
                    int max = 500;

                    Random rand = new Random();
                    int randomNum = rand.nextInt((max - min) + 1) + min;
                    soundList.add(randomNum);

                    if (i == bars) {
                        break;
                    }

                    try {

                        Thread.sleep((loopTime / bars) * 1000);

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }

                instrument.setSoundList(soundList);

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_example, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
