package com.example.andersson.musicapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

import java.util.ArrayList;

public abstract class AbstractInstrumentActivity extends Activity {

    public SharedInfoHolder holder;
    public AbstractInstrumentThread instrument;
    public ArrayList<Integer> soundList;
    public EditText soundListText;
    public String name;
    int index = 0;

    public boolean playRealTime;
    public boolean record;

    double bars;
    double loopTime;

    abstract void generateSoundInfo(int index);

    public abstract String getName();

    abstract void initiate();

    abstract void initiateGUI();

    abstract AbstractInstrumentThread getInstrumentClass();

    abstract int getActivity();

    abstract int getMenu();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivity());

        soundListText = (EditText) findViewById(R.id.soundListText);

        name = getName();

        Intent i = getIntent();

        holder = i.getParcelableExtra("holder");

        Log.d("IA Example", "Holder status: " + holder.hasHolder());

        if (holder != null) {

            holder.transfer();

            if (holder.containsKey(name)) {

                instrument = (AbstractInstrumentThread) holder.getThread(name);
                Log.d("IA Intent", "New playLoop fetched");


            } else {

                instrument = getInstrumentClass();
                holder.addThread(name, instrument);
                Log.d("IA Intent", "New playLoop created");

            }

        } else {

            instrument = getInstrumentClass();
            Log.e("IA Intent", "holder is null");

        }

        initiate();
        initiateGUI();


        if(i.getStringExtra("backInfo") != null) {

            if (i.getStringExtra("backInfo").equals("back")) {

                Intent myIntent = new Intent();
                SharedInfoHolder tempHolder = new SharedInfoHolder(holder);
                myIntent.putExtra("holder", tempHolder);
                setResult(RESULT_OK, myIntent);
                finish();

            }

        }else{

            Log.e("AIA BackInfo", "BackInfo is null");

        }

    }

    @Override
    public void onBackPressed() {

        Intent myIntent = new Intent();

        if (holder != null) {

            SharedInfoHolder tempHolder = new SharedInfoHolder(holder);
            Log.d("IA Backpress", "Holder status:§ " + tempHolder.hasHolder());
            myIntent.putExtra("holder", tempHolder);
            setResult(RESULT_OK, myIntent);
            finish();

        } else {

            Log.e("IA Backpress", "Holder is null");
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

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Context getContext() {

        return this.getBaseContext();
    }

    public void setLoopTime(int loopTime){

        this.loopTime = loopTime;
    }
    public void setBars(int bars){

        this.bars = bars;

    }

    public void setSoundList(ArrayList<Integer> soundList) {

        this.soundList = soundList;

    }
}
