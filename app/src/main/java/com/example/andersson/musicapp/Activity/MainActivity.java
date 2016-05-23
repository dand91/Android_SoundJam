package com.example.andersson.musicapp.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.andersson.musicapp.AsyncUpdate.UpdateThread;
import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.MainHolder;
import com.example.andersson.musicapp.SharedResources.SinusThreadHolder;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;
import com.example.andersson.musicapp.TimeTracking.TimeThread;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity {

    private Button BassButton;
    private Button SinusButton;
    private Button GroupNameButton;
    private Button DrumsButton;
    private Button RestartButton;
    private Button SyncButton;
    private ImageButton PauseButton;
    private SeekBar BPMBar;
    private TextView BPMText;
    private EditText groupNameText;
    private TextView InfoView;
    private TextView groupNameTextView;

    private ThreadHolder threadHolder;
    private MainHolder mainHolder;

    private UpdateThread updater;
    private TimeThread timer;

    private double loopTime = 4;
    private int BPM = 120;
    private static String groupName = "noName";

    private boolean sync = false;
    private boolean pause;
    private boolean settingBPM = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!haveNetworkConnection()) {

            CreateDialog("No internet connection.");
        }

        if (threadHolder == null) {

            threadHolder = ThreadHolder.getInstance();
            mainHolder = MainHolder.getInstance();
            mainHolder.setMainActivity(this);

            ThreadPool threadPool = ThreadPool.getInstance();

            timer = TimeThread.getInstance();

            if (!timer.isAlive() && threadPool.getThread("timer") == null) {

                threadPool.add(timer, "timer");

            }

            updater = UpdateThread.getInstance();

            if (!updater.isAlive() && threadPool.getThread("updater") == null) {

                threadPool.add(updater, "updater");

            }

            Intent myIntent = new Intent(MainActivity.this, BassdrumActivity.class);
            myIntent.putExtra("backInfo", "back");
            MainActivity.this.startActivityForResult(myIntent, 10);

            myIntent = new Intent(MainActivity.this, HighHatActivity.class);
            myIntent.putExtra("backInfo", "back");
            MainActivity.this.startActivityForResult(myIntent, 10);

            myIntent = new Intent(MainActivity.this, SnareActivity.class);
            myIntent.putExtra("backInfo", "back");
            MainActivity.this.startActivityForResult(myIntent, 10);

            myIntent = new Intent(MainActivity.this, BassActivity.class);
            myIntent.putExtra("backInfo", "back");
            MainActivity.this.startActivityForResult(myIntent, 10);

        }

        BassButton = (Button) findViewById(R.id.bassButton);
        BassButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, BassActivity.class);
                myIntent.putExtra("backInfo", "notback");
                MainActivity.this.startActivityForResult(myIntent, 10);

            }
        });

        DrumsButton = (Button) findViewById(R.id.drumsButton);
        DrumsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, BeatActivity.class);
                myIntent.putExtra("backInfo", "notback");
                MainActivity.this.startActivityForResult(myIntent, 10);

            }
        });

        SinusButton = (Button) findViewById(R.id.synthButton);
        SinusButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, SinusActivity.class);
                MainActivity.this.startActivity(myIntent);

            }
        });

        groupNameTextView = (TextView) findViewById(R.id.groupName);

        if(!groupName.equals("noName")) {
            groupNameTextView.setText(groupName);
        }
        groupNameText = (EditText) findViewById(R.id.groupNameText);
        GroupNameButton = (Button) findViewById(R.id.groupNameButton);
        GroupNameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                setGroupNameButton(false);
                groupName = groupNameText.getText().toString();

                runOnUiThread(() -> {

                            groupNameTextView.setText(groupName);
                            groupNameText.setText("");
                        });

                updater.wake();
                Log.d("Main", "New group name: " + groupName);
            }
        });

        BPMText = (TextView) findViewById(R.id.BPMText);

        BPMBar = (SeekBar) findViewById(R.id.BPMBar);
        BPMBar.setProgress(0);
        BPMBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                BPM = (120 + i);
                BPMText.setText("BPM: " + BPM);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                settingBPM = true;

                ThreadHolder holder = ThreadHolder.getInstance();
                HashMap<String, Thread> map = holder.getThreads();

                for (Map.Entry thread : map.entrySet()) {

                    try {

                        ((AbstractInstrumentThread) thread.getValue()).resetTimeDifference();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                loopTime = (double) (8 * 60) / BPM;

                for (int i = 0; i < 10; i++) {

                    threadHolder.setLoopTime(loopTime);

                    holder = ThreadHolder.getInstance();
                    map = holder.getThreads();

                    for (Map.Entry thread : map.entrySet()) {

                        AbstractInstrumentThread tempThread = ((AbstractInstrumentThread) thread.getValue());
                        tempThread.setLoopTime(loopTime);
                        tempThread.setChangedStatus(true);

                        if (i == 0) {

                            tempThread.setPause(true);

                        } else if (i == 10 - 1) {

                            tempThread.setPause(false);

                        }
                    }

                    try {

                        Thread.sleep(CLIENT_UPDATE_TIME / 10);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                settingBPM = false;
            }

        });

        RestartButton = (Button) findViewById(R.id.restartButton);
        RestartButton.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                System.exit(1);
            }
        });

        SyncButton = (Button) findViewById(R.id.syncButton);
        SyncButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                sync = !sync;
                final boolean syncTemp = sync;

                runOnUiThread(() -> {

                    InfoView.setText("Sync is: " + syncTemp);

                });

                Log.i("Main", "Sync: " + sync);
                timer.setSync(sync);

            }
        });

        PauseButton = (ImageButton) findViewById(R.id.muteButton);
        PauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                pause = !pause;
                Log.i("Main", "Pause: " + pause);

                if(SinusThreadHolder.getInstance().getSinusThread() != null) {
                    SinusThreadHolder.getInstance().getSinusThread().setMute(pause);
                }
                for(Map.Entry entry : threadHolder.getThreads().entrySet()){

                    ((AbstractInstrumentThread)entry.getValue()).setPause(pause);
                }
            }
        });


        InfoView = (TextView) findViewById(R.id.infoView);
    }

    public int getBPM() {
        return BPM;
    }

    public void setBPM(int newBPM) {

        if (!settingBPM){

            BPM = newBPM;
            loopTime = (double) (8 * 60) / BPM;
            threadHolder.setLoopTime(loopTime);

        runOnUiThread(() -> {

            BPMText.setText("BPM: " + BPM);
            BPMBar.setProgress(BPM - 120);

        });
    }
    }

    public String getGroupName() {

        return groupName;
    }

    public void setInfoText(String text) {

        runOnUiThread(() -> {

            InfoView.setText(text);
        });

    }

    public double getLoopTime() {

        return loopTime;
    }

    public void setGroupNameButton(Boolean onoff){

        runOnUiThread(() -> {

            GroupNameButton.setClickable(onoff);
            GroupNameButton.setEnabled(onoff);

        });
    }

    /**
     * Method for creating an Dialog box which prints out the message contained in parameter message.
     *
     * @param message
     */
    public void CreateDialog(String message) {

        runOnUiThread(() -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(message);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder1.create();
            alert.show();

        });
    }

    /**
     * Method for checking if WIFI or other network connection is established.
     *
     * @return true - if network connection exists, otherwise false.
     */
    private boolean haveNetworkConnection() {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
