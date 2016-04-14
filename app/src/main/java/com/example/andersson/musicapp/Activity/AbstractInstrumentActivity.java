package com.example.andersson.musicapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

import java.util.ArrayList;

public abstract class AbstractInstrumentActivity extends Activity {


    public SeekBar volumeSeekBar;

    public SharedInfoHolder holder;
    public AbstractInstrumentThread instrument;
    public ArrayList<Integer> soundList;
    public String name;
    int index = 0;

    double bars;
    double loopTime;

    abstract void generateSoundInfo(int index);
    abstract String setName();
    abstract void initiate();
    abstract AbstractInstrumentThread getInstrumentClass();
    abstract int getActivity();
    abstract int getMenu();

    public Context getContext(){

        return this.getBaseContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivity());

        name = setName();

        Intent i = getIntent();

        holder = (SharedInfoHolder) i.getParcelableExtra("holder");

        Log.d("IA Example", "Holder status: " + holder.hasHolder());

        if(holder != null) {

            holder.transfer();

            if (holder.containsKey(name)) {

                instrument = (AbstractInstrumentThread) holder.getThread(name);
                Log.d("IA Intent", "New playLoop fetched");


            } else {

                instrument = getInstrumentClass();
                holder.addThread(name, instrument);
                instrument.start();
                Log.d("IA Intent", "New playLoop created");

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

            SharedInfoHolder tempHolder =  new SharedInfoHolder(holder);
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
