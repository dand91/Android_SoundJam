package com.example.andersson.musicapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.Vibrator;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public abstract class InstrumentActivity extends ActionBarActivity {

    private Button recordButton;
    private Button loopTimeButton;
    private EditText loopTimeText;
    private Button barButton;
    private EditText barText;
    private ThreadHolder holder;
    public InstrumentThread instrument;
    public ArrayList<Integer> soundList;
    public String name;

    abstract void generateSoundInfo();
    abstract String setName();
    abstract void initiate();
    abstract InstrumentThread getInstrumentClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        name = setName();

        Intent i = getIntent();

        holder = (ThreadHolder) i.getParcelableExtra("holder");

        Log.d("Example", "Holder status: " + holder.hasHolder());

        if(holder != null) {

            holder.transfer();

            if (holder.containsKey(name)) {

                instrument = (InstrumentThread) holder.get(name);
                Log.d("Intent","New instrument created");


            } else {

                instrument = getInstrumentClass();
                holder.addThread(name, instrument);
                instrument.start();
                Log.d("Intent", "New instrument fetched");

            }

        }else{

            instrument = new ExampleInstrument();
            Log.d("Intent","holder is null");

        }

        initiate();

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

        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                int i = 0;
                int bars = instrument.getBars();
                int loopTime = instrument.getLoopTime();
                barButton.setBackgroundColor(Color.RED);
                loopTimeButton.setBackgroundColor(Color.RED);

                barButton.setEnabled(false);
                loopTimeButton.setEnabled(false);


                soundList = new ArrayList<Integer>();

                while (true) {

                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(50);

                    i++;

                    generateSoundInfo();

                    if (i == bars) {

                        break;
                    }

                    try {

                        Thread.sleep((loopTime / bars) * 1000);

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }

                String s = "";
                for(int in : soundList){
                    s = s + in + " ";
                }
                Log.d("Record",s);
                instrument.setSoundList(soundList);
                barButton.setEnabled(true);
                loopTimeButton.setEnabled(true);

                barButton.setBackgroundColor(Color.GREEN);
                loopTimeButton.setBackgroundColor(Color.GREEN);

            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent myIntent = new Intent();

        if(holder != null) {

            ThreadHolder tempHolder =  new ThreadHolder(holder);
            Log.d("Backpress", "Holder status: " + tempHolder.hasHolder() + " " + tempHolder.toString());
            myIntent.putExtra("holder", tempHolder);
            Log.d("Backpress", "Holder != null");
            setResult(RESULT_OK, myIntent);
            finish();

        }else{

            Log.d("Backpress","Holder == null");
        }
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
