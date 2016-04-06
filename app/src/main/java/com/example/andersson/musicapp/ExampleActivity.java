package com.example.andersson.musicapp;

import android.content.Context;
import android.content.Intent;
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

public class ExampleActivity extends ActionBarActivity {

    private Button recordButton;
    private Button loopTimeButton;
    private EditText loopTimeText;
    private Button barButton;
    private EditText barText;
    private ThreadHolder holder;
    private InstrumentThread instrument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        Intent i = getIntent();

        holder = (ThreadHolder) i.getParcelableExtra("holder");

        Log.d("Example", "Holder status: " + holder.hasHolder());

        if(holder != null) {

            holder.transfer();

            if (holder.containsKey("instrument")) {

                instrument = (InstrumentThread) holder.get("instrument");
                Log.d("Intent","New instrument created");


            } else {

                instrument = new ExampleInstrument();
                holder.addThread("instrument", instrument);
                instrument.setSoundList(new ArrayList<Integer>(Arrays.asList(200, 210, 220, 230, 240, 250, 260, 270)));
                instrument.setBars(8);
                instrument.setLoopTime(16);
                instrument.start();
                Log.d("Intent", "New instrument fetched");

            }

        }else{

            instrument = new ExampleInstrument();
            Log.d("Intent","holder is null");

        }



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


        //ExampleActivity.this.startActivity(myIntent);

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
