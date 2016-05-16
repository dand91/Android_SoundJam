package com.example.andersson.musicapp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.andersson.musicapp.AsyncUpdate.UpdateThread;
import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.MainHolder;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;
import com.example.andersson.musicapp.TimeTracking.TimeThread;


public class MainActivity extends BaseActivity {

    private Button BassButton;
    private Button groupNameButton;
    private Button DrumsButton;
    private SeekBar BPMBar;
    private EditText BPMText;
    private EditText groupNameText;
    private TextView InfoView;
    private ThreadHolder threadHolder;
    private MainHolder mainHolder;
    private String groupName = "noName";

    private double loopTime = 4;
    private int BPM = 120;

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

            TimeThread timer = TimeThread.getInstance();

            if (!timer.isAlive()) {


                threadPool.add(timer, "timer");

            }

            UpdateThread updater = UpdateThread.getInstance();

            if (!updater.isAlive()) {

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

        BassButton = (Button) findViewById(R.id.BassButton);
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

        groupNameText = (EditText) findViewById(R.id.groupNameText);
        groupNameButton = (Button) findViewById(R.id.groupNameButton);
        groupNameButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                groupName = groupNameText.getText().toString();
                groupNameText.setText("");
                Log.d("Main", "New group name: " + groupNameText.getText().toString());
            }
        });

        BPMText = (EditText) findViewById(R.id.BPMText);

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

                loopTime = (double) (8 * 60) / BPM;
                threadHolder.setLoopTime(loopTime);

                try {
                    Thread.sleep(CLIENT_UPDATE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                threadHolder.setLoopTime(loopTime);
            }

        });

        InfoView = (TextView) findViewById(R.id.InfoView);
    }

    public int getBPM() {
        return BPM;
    }

    public void setBPM(int newBPM) {

        this.BPM = newBPM;
        loopTime = (double) (8 * 60) / BPM;
        threadHolder.setLoopTime(loopTime);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                BPMText.setText("BPM: " + BPM);
                BPMBar.setProgress(BPM - 120);

            }
        });

    }

    public String getGroupName() {

        return groupName;
    }

    public void setInfoText(String text) {

        final String tempText = text;

        runOnUiThread(new Runnable() {

            public void run() {

                InfoView.setText(tempText);
            }
        });

    }

    public double getLoopTime() {
        return loopTime;
    }


    /**
     * Method for creating an Dialog box which prints out the message contained in parameter message.
     * @param message
     */
    public void CreateDialog(String message) {

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
