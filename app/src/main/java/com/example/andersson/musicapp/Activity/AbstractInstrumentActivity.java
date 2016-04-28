package com.example.andersson.musicapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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
import com.example.andersson.musicapp.SharedResources.SoundPoolHolder;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class AbstractInstrumentActivity extends Activity {

    public SharedInfoHolder holder;
    public AbstractInstrumentThread instrument;
    public ArrayList<Integer> soundList;
    public EditText soundListText;
    public String name;
    public boolean playRealTime;
    public boolean record;
    public Button recordButton;
    public Button playButton;
    public Button stopButton;
    public Button removeButton;
    public Button barButton;
    public EditText barText;
    public SeekBar volumeSeekBar;
    public TextView progressText;
    int index = 0;
    protected SoundPoolHolder sph;
    double bars;
    double loopTime;
    private int countDown;
    private String info;

    abstract void generateSoundInfo(int index);

    public abstract String getName();

    protected abstract void initiate();

    protected abstract void initiateGUI();

    protected abstract AbstractInstrumentThread getInstrumentClass();

    protected abstract int getLayout();

    protected abstract int getMenu();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        info = null;
        holder = SharedInfoHolder.getInstance();
        sph = SoundPoolHolder.getInstance();

        try {

            Intent i = getIntent();

            info = i.getStringExtra("backInfo");

        } catch (NullPointerException e) {

            Log.d("AbstractInstrument", "Error loading intent");
            System.exit(0);
        }

        if (info != null) {

            if (info.equals("back")) {

                Intent myIntent = new Intent();
                setResult(RESULT_OK, myIntent);
                finish();

            }

        } else {

            Log.e("AbstractInstrument", "BackInfo is null");

        }

        name = getName();

        if (holder != null) {

            if (holder.containsKey(name)) {

                instrument = (AbstractInstrumentThread) holder.getThread(name);
                Log.d("AbstractInstrument", "New playLoop fetched");


            } else {

                instrument = getInstrumentClass();
                holder.addThread(name, instrument);
                Log.d("AbstractInstrument", "New playLoop created");

            }

        }else if (holder == null) {

                Log.d("AbstractInstrument", "Holder is null - " + name);
                System.exit(0);

            }


            initiate();
            initiateGUI();

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


    public void setLoopTime(int loopTime) {

        this.loopTime = loopTime;
    }

    public void setBars(int bars) {

        this.bars = bars;

    }

    public void setSoundList(ArrayList<Integer> soundList) {

        this.soundList = soundList;

    }

    protected void recordGUI() {

        recordButton = (Button) findViewById(R.id.recordButton);
        progressText = (TextView) findViewById(R.id.progressText);

        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                if (!record) {

                    soundListText.setText("");

                    while (true) {

                        Calendar calendar = Calendar.getInstance();
                        int seconds = calendar.get(Calendar.SECOND);

                        if (seconds % loopTime == 0) {

                            index = 0;
                           // instrument.setChangedStatus(true);
                            barButton.setBackgroundColor(Color.RED);
                            barButton.setEnabled(false);

                            soundList = new ArrayList<Integer>();


                            new Thread() {

                                public void run() {

                                    record = true;
                                    instrument.setRecord(true);

                                    try {

                                        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                        v.vibrate(3 * 1000);

                                        for (int i = 4; i >= 0; i--) {

                                            countDown = i;

                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    progressText.setText("Record start in: " + countDown);

                                                }
                                            });

                                            sleep(1000);

                                        }


                                        while (true) {

                                            index++;

                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    progressText.setText("Beat " + index + " of " + bars);
                                                    generateSoundInfo(index);

                                                }
                                            });

                                            sleep(200);
                                            v.vibrate(50);

                                            if (index == bars) {

                                                break;
                                            }

                                            sleep((long) (((double) loopTime / (double) bars) * 1000) - 200);


                                        }

                                    } catch (InterruptedException e) {

                                        e.printStackTrace();
                                    }

                                    String s = "";
                                    for (int in : soundList) {
                                        s = s + in + " ";
                                    }
                                    Log.i("Recorded: ", s);

                                    instrument.setSoundList(soundList);
                                    record = false;
                                    instrument.setRecord(false);

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            barButton.setEnabled(true);
                                            barButton.setBackgroundColor(Color.GREEN);
                                        }
                                    });


                                }

                            }.start();

                            break;
                        }
                    }
                }
            }
        });
    }

    protected void volumeGUI() {

        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setProgress(instrument.getVolume());
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                instrument.setVolume(((float) i) / 100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

    }

    protected void stopPlayGUI() {

        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                playRealTime = true;
                instrument.setPlayRealTime(true);
                Log.d("EA1", "playRealTime");
            }
        });
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                playRealTime = false;
                instrument.setPlayRealTime(false);

                Log.d("EA1", "stopRealTime");
            }
        });
    }

    protected void removeGUI() {

        removeButton = (Button) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                ArrayList<Integer> tempList = new ArrayList<Integer>();
                tempList.add(-1);
                instrument.setSoundList(tempList);
                progressText.setText("Instrument removed.");
            }
        });
    }

    protected void barGUI() {

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
                        barText.setText((int) instrument.getBars());
                    } else {
                        barText.setText("");
                    }
                }
            }

        });
    }
    protected void soundListGUI(){

        soundListText = (EditText) findViewById(R.id.soundListText);

    }

}
