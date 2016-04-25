package com.example.andersson.musicapp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

import java.io.Serializable;


public class MainActivity extends ActionBarActivity implements Serializable {

    private Button BassDrumButton;
    private Button snareButton;
    private Button BassButton;
    private Button HighHatButton;
    private Button groupNameButton;
    private SeekBar BPMBar;
    private EditText groupNameText;
    private SharedInfoHolder holder;
    private String groupName = "noName";
    private int loopTime = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!haveNetworkConnection()) {

            AlertNoInternet();
        }


        if (holder == null) {

            holder = new SharedInfoHolder(this);

            Intent myIntent = new Intent(MainActivity.this, BassdrumActivity.class);
            SharedInfoHolder tempHolder = new SharedInfoHolder(holder);
            myIntent.putExtra("holder", tempHolder);
            myIntent.putExtra("backInfo", "back");
            MainActivity.this.startActivityForResult(myIntent, 10);

            myIntent = new Intent(MainActivity.this, HighHatActivity.class);
            myIntent.putExtra("holder", tempHolder);
            myIntent.putExtra("backInfo", "back");
            MainActivity.this.startActivityForResult(myIntent, 10);

            myIntent = new Intent(MainActivity.this, SnareActivity.class);
            myIntent.putExtra("holder", tempHolder);
            myIntent.putExtra("backInfo", "back");
            MainActivity.this.startActivityForResult(myIntent, 10);

            myIntent = new Intent(MainActivity.this, BassActivity.class);
            myIntent.putExtra("holder", tempHolder);
            myIntent.putExtra("backInfo", "back");
            MainActivity.this.startActivityForResult(myIntent, 10);


        }

        InitiateButtons();

        groupNameText = (EditText) findViewById(R.id.groupNameText);
        groupNameButton = (Button) findViewById(R.id.groupNameButton);
        groupNameButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                groupName = groupNameText.getText().toString();
                Log.d("Main", "New group name: " + groupNameText.getText().toString());
            }
        });

        BPMBar = (SeekBar) findViewById(R.id.BPMBar);
        BPMBar.setProgress(0);
        BPMBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                EditText text = (EditText) findViewById(R.id.BPMText);
                text.setText("BPM: " + (120 + i));

                loopTime = (8 * 60) / (120 + i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                holder.setLoopTime(loopTime);

            }

        });

    }


    private void InitiateButtons(){

        BassDrumButton = (Button) findViewById(R.id.BassDrumButton);
        BassDrumButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, BassdrumActivity.class);

                SharedInfoHolder tempHolder = new SharedInfoHolder(holder);
                Log.d("Main", "Holder status: " + tempHolder.hasHolder() + " " + tempHolder.toString());
                myIntent.putExtra("holder", tempHolder);
                myIntent.putExtra("backInfo", "notback");
                MainActivity.this.startActivityForResult(myIntent, 10);


            }
        });

        snareButton = (Button) findViewById(R.id.snareButton);
        snareButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, SnareActivity.class);
                SharedInfoHolder tempHolder = new SharedInfoHolder(holder);
                Log.d("Main", "Holder status: " + tempHolder.hasHolder() + " " + tempHolder.toString());
                myIntent.putExtra("holder", tempHolder);
                myIntent.putExtra("backInfo", "notback");
                MainActivity.this.startActivityForResult(myIntent, 10);

            }
        });

        BassButton = (Button) findViewById(R.id.BassButton);
        BassButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, BassActivity.class);
                SharedInfoHolder tempHolder = new SharedInfoHolder(holder);
                Log.d("Main", "Holder status: " + tempHolder.hasHolder() + " " + tempHolder.toString());
                myIntent.putExtra("holder", tempHolder);
                myIntent.putExtra("backInfo", "notback");
                MainActivity.this.startActivityForResult(myIntent, 10);

            }
        });


        HighHatButton = (Button) findViewById(R.id.HighHatButton);
        HighHatButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, HighHatActivity.class);
                SharedInfoHolder tempHolder = new SharedInfoHolder(holder);
                Log.d("Main", "Holder status: " + tempHolder.hasHolder() + " " + tempHolder.toString());
                myIntent.putExtra("holder", tempHolder);
                myIntent.putExtra("backInfo", "notback");
                MainActivity.this.startActivityForResult(myIntent, 10);

            }
        });



    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {

            if (resultCode == RESULT_OK) {

                SharedInfoHolder tempHolder = data.getParcelableExtra("holder");
                Log.d("Main", "Holder status: " + tempHolder.hasHolder() + " " + tempHolder.toString());

                this.holder = tempHolder;
                this.holder.transfer();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public String getGroupName() {

        return groupName;
    }

    public int getLoopTime() {
        return loopTime;
    }

    public void AlertNoInternet() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("No internet connection.");
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
