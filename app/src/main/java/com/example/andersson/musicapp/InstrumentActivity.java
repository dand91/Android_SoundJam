package com.example.andersson.musicapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public abstract class InstrumentActivity extends Activity {

    public ThreadHolder holder;
    public InstrumentThread instrument;
    public ArrayList<Integer> soundList;
    public String name;
    int index = 0;

    int bars;
    int loopTime;

    abstract void generateSoundInfo(int index);
    abstract String setName();
    abstract void initiate();
    abstract InstrumentThread getInstrumentClass();
    abstract int getActivity();
    abstract int getMenu();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivity());

        name = setName();

        Intent i = getIntent();

        holder = (ThreadHolder) i.getParcelableExtra("holder");

        Log.d("IA Example", "Holder status: " + holder.hasHolder());

        if(holder != null) {

            holder.transfer();

            if (holder.containsKey(name)) {

                instrument = (InstrumentThread) holder.get(name);
                Log.d("IA Intent","New instrument created");


            } else {

                instrument = getInstrumentClass();
                holder.addThread(name, instrument);
                instrument.start();
                Log.d("IA Intent", "New instrument fetched");

            }

        }else{

            instrument = getInstrumentClass();
            Log.d("IA Intent","holder is null");

        }

        initiate();

    }

    @Override
    public void onBackPressed() {

        Intent myIntent = new Intent();

        if(holder != null) {

            ThreadHolder tempHolder =  new ThreadHolder(holder);
            Log.d("IA Backpress", "Holder status: " + tempHolder.hasHolder() + " " + tempHolder.toString());
            myIntent.putExtra("holder", tempHolder);
            Log.d("IA Backpress", "Holder != null");
            setResult(RESULT_OK, myIntent);
            finish();

        }else{

            Log.d("IA Backpress","Holder == null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(getMenu(), menu);
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
