package com.example.andersson.musicapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {

    public final static int CLIENT_UPDATE_TIME = 3000;

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ThreadHolder holder = ThreadHolder.getInstance();
        HashMap<String, Thread> map = holder.getThreads();

        for (Map.Entry thread : map.entrySet()) {

            try {

                ((AbstractInstrumentThread) thread.getValue()).setPause(true);
                ((AbstractInstrumentThread) thread.getValue()).resetTimeDifference();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();


                ThreadHolder holder = ThreadHolder.getInstance();
                HashMap<String, Thread> map = holder.getThreads();

            for(Map.Entry thread:map.entrySet()){

                try {

                    ((AbstractInstrumentThread) thread.getValue()).setPause(false);
                    ((AbstractInstrumentThread) thread.getValue()).resetTimeDifference();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

}
